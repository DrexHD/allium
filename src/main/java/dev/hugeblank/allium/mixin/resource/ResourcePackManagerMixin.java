package dev.hugeblank.allium.mixin.resource;

import dev.hugeblank.allium.loader.resources.AlliumResourcePackProvider;
import net.minecraft.resource.ResourcePackManager;
import net.minecraft.resource.ResourcePackProvider;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

@Mixin(ResourcePackManager.class)
public class ResourcePackManagerMixin {
    @Shadow
    @Final
    @Mutable
    private Set<ResourcePackProvider> providers;

    @Inject(at = @At("RETURN"), method = "<init>")
    private void init(ResourcePackProvider[] Tproviders, CallbackInfo ci) {
        this.providers = new HashSet<>(this.providers);
        this.providers.add(new AlliumResourcePackProvider());
    }
}
