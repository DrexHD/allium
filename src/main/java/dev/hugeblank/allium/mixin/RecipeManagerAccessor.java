package dev.hugeblank.allium.mixin;

import com.google.gson.JsonObject;
import net.minecraft.recipe.RecipeEntry;
import net.minecraft.recipe.RecipeManager;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(RecipeManager.class)
public interface RecipeManagerAccessor {
    @Invoker
    static RecipeEntry<?> callDeserialize(Identifier id, JsonObject json) {
        throw new UnsupportedOperationException();
    }
}
