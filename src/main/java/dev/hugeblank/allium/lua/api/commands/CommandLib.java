package dev.hugeblank.allium.lua.api.commands;

import dev.hugeblank.allium.lua.api.WrappedLuaLibrary;
import dev.hugeblank.allium.lua.type.annotation.CoerceToBound;
import dev.hugeblank.allium.lua.type.annotation.LuaWrapped;

@LuaWrapped(name = "command")
public class CommandLib implements WrappedLuaLibrary {

    public CommandLib() {
    }

    @LuaWrapped(name = "arguments")
    public static final @CoerceToBound ArgumentTypeLib ARGUMENTS = new ArgumentTypeLib();
}
