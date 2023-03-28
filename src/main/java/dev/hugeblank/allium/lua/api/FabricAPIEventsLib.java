package dev.hugeblank.allium.lua.api;

import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.loader.ScriptResource;
import dev.hugeblank.allium.lua.type.annotation.LuaWrapped;
import dev.hugeblank.allium.util.fapi.ArrayBackedScriptEvent;
import net.fabricmc.fabric.api.event.Event;

import java.util.HashSet;
import java.util.Set;

@LuaWrapped(name = "fabric_api")
public class FabricAPIEventsLib implements WrappedLuaLibrary {

    public static final Set<FabricAPIEventHandler<?>> handlers = new HashSet<>();

    public FabricAPIEventsLib() {
    }

    /**
     * This function cannot currently be called from scripts, due to generics issues.
     * {@link dev.hugeblank.allium.mixin.EventMixin#allium$register(Script, Object)} can be used instead.
     * <p>
     * Script example:
     * <pre>
     * ServerPlayConnectionEvents.JOIN:register(script, function(handler, sender, server)
     *     -- do something
     * end)
     * </pre>
     */
    @LuaWrapped
    public static <T> FabricAPIEventHandler<T> register(Script script, Event<T> event, T listener) {
        event.register(listener);
        var handler = new FabricAPIEventHandler<>(script, event, listener);
        handlers.add(handler);
        return handler;
    }

    public static class FabricAPIEventHandler<T> implements ScriptResource {

        private final Event<T> event;
        private final T listener;
        private final Script.ResourceRegistration registration;

        public FabricAPIEventHandler(Script script, Event<T> event, T listener) {
            this.event = event;
            this.listener = listener;
            registration = script.registerResource(this);
        }

        @Override
        public void close() throws Exception {
            handlers.remove(this);
            ((ArrayBackedScriptEvent<T>) event).allium$remove(listener);
            registration.close();
        }
    }

}
