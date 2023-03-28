package dev.hugeblank.allium.mixin;

import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.lua.api.FabricAPIEventsLib;
import dev.hugeblank.allium.lua.type.annotation.LuaWrapped;
import dev.hugeblank.allium.util.fapi.ScriptEvent;
import net.fabricmc.fabric.api.event.Event;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(Event.class)
public abstract class EventMixin<T> implements ScriptEvent<T> {

    @Shadow public abstract void register(T listener);

    // TODO: Add phase support
    @Override
    @LuaWrapped(name = "register")
    public void allium$register(Script script, T listener) {
        register(listener);
        var handler = new FabricAPIEventsLib.FabricAPIEventHandler<>(script, (Event<T>) (Object)this, listener);
        FabricAPIEventsLib.handlers.add(handler);
    }
}
