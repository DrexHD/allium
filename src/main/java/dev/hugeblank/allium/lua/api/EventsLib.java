package dev.hugeblank.allium.lua.api;

import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.loader.ScriptResource;
import dev.hugeblank.allium.util.fabric_api.ArrayBackedScriptEvent;

import java.util.HashSet;
import java.util.Set;

public class EventsLib {

    public static final Set<ScriptResource> handlers = new HashSet<>();

    private EventsLib() {
    }

    public static class FabricAPIEventHandler<T> implements ScriptResource {

        private final net.fabricmc.fabric.api.event.Event<T> event;
        private final T listener;
        private final Script.ResourceRegistration registration;

        public FabricAPIEventHandler(Script script, net.fabricmc.fabric.api.event.Event<T> event, T listener) {
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

    public static class ArchitecturyAPIEventHandler<T> implements ScriptResource {

        private final dev.architectury.event.Event<T> event;
        private final T listener;
        private final Script.ResourceRegistration registration;

        public ArchitecturyAPIEventHandler(Script script, dev.architectury.event.Event<T> event, T listener) {
            this.event = event;
            this.listener = listener;
            registration = script.registerResource(this);
        }

        @Override
        public void close() throws Exception {
            handlers.remove(this);
            event.unregister(listener);
            registration.close();
        }
    }

}
