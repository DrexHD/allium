package me.hugeblank.allium.lua.type;

import org.squiddev.cobalt.LuaState;
import org.squiddev.cobalt.LuaTable;
import org.squiddev.cobalt.LuaValue;
import org.squiddev.cobalt.lib.LuaLibrary;

public class ItemType implements LuaLibrary {


    public ItemType() {

    }

    @Override
    public LuaValue add(LuaState state, LuaTable environment) {
        LuaTable tbl = new LuaTable();

        return tbl;
    }
}
