package dev.hugeblank.allium.mixin;

import dev.hugeblank.allium.util.fapi.EventPhaseDataRemovable;
import org.apache.commons.lang3.ArrayUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(targets = "net.fabricmc.fabric.impl.base.event.EventPhaseData")
public class EventPhaseDataMixin<T> implements EventPhaseDataRemovable<T> {

    @Shadow
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
//        System.arraycopy(listeners, index + 1, listeners, index, listeners.length - 1 - index);
        return true;
    }

}
