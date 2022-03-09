# Allium
Lua Plugin loader for Java Minecraft.

Currently, only functioning and in development in fabric, but should (in theory) be very easily portable to forge.

## Demo
This is the test script I'm using, if you'd like to play/develop with Allium for yourself. 

This should be pasted in your game directory, as plugins/test/main.lua

```lua

allium.onEvent("chat_message", function(e, name, uuid, message)
    print(name.." said "..message)
end)

local i = 0
allium.onEvent("player_tick", function(e, name, uuid)
    local player = players.getPlayer(name)
    player.setExperienceLevel(i)
    i=(i+1)%20
end)

print("Loading test plugin!\n", "Test", 1, 2, 3)

return {
    id = "test",
    version = "0.1.0",
    name = "Allium Test Plugin"
}
```

## But Allium is for ComputerCraft?
You're right! That's moved to (allium-cc)[https://github.com/hugeblank/allium-cc]. I think this project is going to be
more important going forward, as it has serious utility, moreso than the dinky CC counterpart. Plus this is in active
development, so stick around!