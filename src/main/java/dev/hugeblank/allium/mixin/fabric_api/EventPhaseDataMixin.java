package dev.hugeblank.allium.mixin.fabric_api;

import dev.hugeblank.allium.util.fabric_api.EventPhaseDataRemovable;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.fabricmc.fabric.impl.base.event.EventPhaseData")
public class EventPhaseDataMixin<T> implements EventPhaseDataRemovable<T> {

    @Shadow(remap = false)
    T[] listeners;

    // returns true if it was removed
    @Override
    public boolean allium$removeListener(T listener) {
        int index = -1;
        for (int i = 0; i < listeners.length; i++) {
            if (listeners[i] == listener) {
                index = i;
                break;
            }
        }
        if (index == -1) {
            return false;
        }
        listeners = ArrayUtils.remove(listeners, index);
        return true;
    }

}
