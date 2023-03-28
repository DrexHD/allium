package dev.hugeblank.allium.mixin;

import dev.hugeblank.allium.util.fapi.ArrayBackedScriptEvent;
import dev.hugeblank.allium.util.fapi.EventPhaseDataRemovable;
import net.fabricmc.fabric.api.event.Event;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;
import java.util.Objects;

@Mixin(targets = "net.fabricmc.fabric.impl.base.event.ArrayBackedEvent")
public abstract class ArrayBackedScriptEventMixin<T> extends Event<T> implements ArrayBackedScriptEvent<T> {

    @Shadow
    @Final
    private Object lock;

    @Shadow
    @Final
    private Map<Identifier, Object> phases;

    @Shadow
    protected abstract void rebuildInvoker(int newLength);

    @Shadow
    private T[] handlers;

    @Shadow
    public abstract void register(T listener);

    @Override
    public void allium$remove(T listener) {
        Objects.requireNonNull(listener, "Tried to remove a null listener!");
        synchronized (lock) {
            int removed = 0;
            for (Map.Entry<Identifier, Object> phaseDataEntry : phases.entrySet()) {
                if (((EventPhaseDataRemovable<T>) phaseDataEntry.getValue()).allium$removeListener(listener)) {
                    removed++;
                }
            }
            if (removed == 0) throw new IllegalArgumentException("Tried to remove unregistered listener");
            rebuildInvoker(handlers.length - removed);
        }
    }


}
