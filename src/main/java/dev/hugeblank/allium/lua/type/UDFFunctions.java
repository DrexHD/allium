package dev.hugeblank.allium.lua.type;

import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.mixin.AlliumMixinPlugin;
import dev.hugeblank.allium.util.architectury_api.ScriptEventImpl;
import dev.hugeblank.allium.util.fabric_api.ArrayBackedScriptEvent;
import me.basiqueevangelist.enhancedreflection.api.EClass;
import me.basiqueevangelist.enhancedreflection.api.EMethod;
import me.basiqueevangelist.enhancedreflection.api.EType;
import me.basiqueevangelist.enhancedreflection.api.typeuse.EClassUse;
import org.squiddev.cobalt.LuaError;
import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.Varargs;
import org.squiddev.cobalt.function.VarArgFunction;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public final class UDFFunctions<T> extends VarArgFunction {
    private final EClass<T> clazz;
    private final List<EMethod> matches;
    private final String name;
    private final T boundReceiver;
    private final boolean isStatic;

    public UDFFunctions(EClass<T> clazz, List<EMethod> matches, String name, T boundReceiver, boolean isStatic) {
        this.clazz = clazz;
        this.matches = matches;
        this.name = name;
        this.boundReceiver = boundReceiver;
        this.isStatic = isStatic;
    }

    @Override
    public Varargs invoke(LuaState state, Varargs args) throws LuaError {
        List<String> paramList = new ArrayList<>(); // String for displaying errors more smartly
        StringBuilder error = new StringBuilder("Could not find parameter match for called function \"" +
                name + "\" for \"" + clazz.name() + "\"" +
                "\nThe following are correct argument types:\n"
        );

        try {
            T instance = boundReceiver != null || isStatic ? boundReceiver : args.arg(1).checkUserdata(clazz.raw());
            for (EMethod method : matches) { // For each matched method from the index call
                var parameters = method.parameters();
                try {
                    var jargs = toJavaArguments(instance, state, args, method);

                    if (jargs.length == parameters.size()) { // Found a match!
                        try { // Get the return type, invoke method, cast returned value, cry.
                            EClassUse<?> ret = method.returnTypeUse().upperBound();
                            Object out = method.invoke(instance, jargs);
                            if (ret.type().raw() == Varargs.class)
                                return (Varargs) out;
                            else
                                return TypeCoercions.toLuaValue(out, ret);
                        } catch (IllegalAccessException e) {
                            throw new LuaError(e);
                        } catch (InvocationTargetException e) {
                            if (e.getTargetException() instanceof LuaError err)
                                throw err;

                            throw new LuaError(e.getTargetException());
                        }
                    }
                } catch (InvalidArgumentException e) {
                    paramList.add(ArgumentUtils.paramsToPrettyString(parameters));
                }
            }
        } catch (Exception e) {
            if (e instanceof LuaError) {
                throw e;
            } else {
                e.printStackTrace();
            }
        }

        for (String headers : paramList) {
            error.append(headers).append("\n");
        }

        throw new LuaError(error.toString());
    }

    private Object[] toJavaArguments(T instance, LuaState state, Varargs args, EMethod method) throws InvalidArgumentException, LuaError {
        if (method.name().equals("allium$register")) {
            if (
                    AlliumMixinPlugin.FABRIC_API_LOADED && instance instanceof ArrayBackedScriptEvent<?> ||
                            AlliumMixinPlugin.ARCHITECTURY_API_LOADED && instance instanceof ScriptEventImpl<?>) {
                return handleEventRegisterArguments(state, args);

            }
        }
        return ArgumentUtils.toJavaArguments(state, args, boundReceiver == null && !isStatic ? 2 : 1, method.parameters());
    }

    private Object[] handleEventRegisterArguments(LuaState state, Varargs args) throws InvalidArgumentException, LuaError {
        if (args.count() == 3) {
            EType eType = clazz.typeVariableValues().get(0);
            return new Object[]{
                    TypeCoercions.toJava(state, args.arg(2), Script.class),
                    TypeCoercions.toJava(state, args.arg(3), eType.upperBound())
            };
        }
        throw new InvalidArgumentException();
    }

}