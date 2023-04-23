-- Wetworks
-- By hugeblank - March 22, 2022
-- By drex - Apr 24, 2023 (use fapi)
-- Applies the 1.19 mud water bottle mechanic to concrete powder blocks
-- This file is marked as a dynamic entrypoint in the manifest.json.
-- Try modifying, and running /reload!

-- Import java classes
local Items = java.import("Items")
local Registries = java.import("Registries")
local Identifier = java.import("Identifier")
local SoundEvents = java.import("SoundEvents")
local SoundCategory = java.import("SoundCategory")
local EquipmentSlot = java.import("EquipmentSlot")
local ActionResult = java.import("ActionResult")
local UseBlockCallback = java.import("net.fabricmc.fabric.api.event.player.UseBlockCallback")

-- Return a function that we can modify while the game is playing

UseBlockCallback.EVENT:register(script, function(player, world, hand, hitResult)
    local pos = hitResult:getBlockPos() -- Get the position of the block interacted with
    local state = world:getBlockState(pos)
    local concrete = Registries.BLOCK:getId(state:getBlock()):getPath() -- Get the name of the block interacted with
    local mainHand = player:getEquippedStack(EquipmentSlot.MAINHAND) -- Get the main hand itemstack of the player
    -- Check if the block name has 'concrete_powder' in it, then check if the main hand is holding a water bottle
    if concrete:find("concrete_powder") and mainHand:isItemEqual(Items.POTION:getDefaultStack()) then
        -- Replace the powder block with the concrete variant
        world:setBlockState(pos, Registries.BLOCK:get(Identifier("minecraft:"..concrete:gsub("_powder", ""))):getDefaultState())
        -- Play the water bottle emptying sound effect
        world:playSound(nil, pos:getX(), pos:getY(), pos:getZ(), SoundEvents.ITEM_BOTTLE_FILL, SoundCategory.BLOCKS, 1, 1)
        if (not player:isCreative()) then -- If the player isn't in creative
            mainHand:setCount(0) -- Remove the water bottle
            player:equipStack(EquipmentSlot.MAINHAND, Items.GLASS_BOTTLE:getDefaultStack()) -- Replace it with an empty glass bottle
            return ActionResult.SUCCESS
        end
    end
    return ActionResult.PASS
end)