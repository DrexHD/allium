package dev.hugeblank.allium.mixin.recipe;

import dev.hugeblank.allium.lua.api.recipe.RecipeLib;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.CuttingRecipe;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.squiddev.cobalt.LuaError;

@Mixin(CuttingRecipe.class)
public class CuttingRecipeMixin {
    @Shadow @Mutable @Final protected Ingredient ingredient;

    @Shadow @Mutable @Final protected ItemStack result;

    @Shadow @Mutable @Final protected String group;

    public Ingredient allium$getInput() {
        return ingredient;
    }

    public void allium$setInput(Ingredient input) throws LuaError {
        RecipeLib.assertInModifyPhase();

        this.ingredient = input;
    }

    public void allium$setOutput(ItemStack output) throws LuaError {
        RecipeLib.assertInModifyPhase();

        this.result = output;
    }

    public void allium$setGroup(String group) throws LuaError {
        RecipeLib.assertInModifyPhase();

        this.group = group;
    }
}
