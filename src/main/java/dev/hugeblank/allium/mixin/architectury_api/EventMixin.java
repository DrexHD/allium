package dev.hugeblank.allium.mixin.architectury_api;

import dev.architectury.event.Event;
import dev.hugeblank.allium.util.architectury_api.ScriptEventImpl;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Event.class)
public interface EventMixin<T> extends ScriptEventImpl<T> {
}
