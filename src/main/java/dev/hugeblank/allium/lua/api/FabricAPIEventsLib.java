package dev.hugeblank.allium.lua.api;

import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.loader.ScriptResource;
import dev.hugeblank.allium.util.fabricapi.ArrayBackedScriptEvent;
import net.fabricmc.fabric.api.event.Event;

import java.util.HashSet;
import java.util.Set;

public class FabricAPIEventsLib {

    public static final Set<FabricAPIEventHandler<?>> handlers = new HashSet<>();

    private FabricAPIEventsLib() {
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
