package dev.hugeblank.allium.util.fabricapi;

import dev.hugeblank.allium.loader.Script;

public interface ScriptEvent<T> {

    void allium$register(Script script, T listener);

}
