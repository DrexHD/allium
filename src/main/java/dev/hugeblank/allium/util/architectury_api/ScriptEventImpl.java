package dev.hugeblank.allium.util.architectury_api;

import dev.hugeblank.allium.loader.Script;
import dev.hugeblank.allium.lua.type.annotation.LuaWrapped;

public interface ScriptEventImpl<T> {

    @LuaWrapped(name = "register")
    void allium$register(Script script, T listener);

}
