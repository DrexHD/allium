package dev.hugeblank.allium.mixin.architectury_api;

import dev.architectury.event.Event;
import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.lua.api.EventsLib;
import dev.hugeblank.allium.lua.type.annotation.LuaWrapped;
import dev.hugeblank.allium.util.architectury_api.ScriptEventImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "dev.architectury.event.EventFactory$EventImpl")
public abstract class EventImplMixin<T> implements ScriptEventImpl<T> {

    @Shadow
    public abstract void register(T listener);

    @Override
    @LuaWrapped(name = "register")
    public void allium$register(Script script, T listener) {
        register(listener);
        var handler = new EventsLib.ArchitecturyAPIEventHandler<>(script, (Event<T>) this, listener);
        EventsLib.handlers.add(handler);
    }

}
