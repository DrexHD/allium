package dev.hugeblank.allium.mixin;

import net.fabricmc.loader.api.FabricLoader;
import org.objectweb.asm.tree.ClassNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.List;
import java.util.Set;

public class AlliumMixinPlugin implements IMixinConfigPlugin {
    public static final boolean QUILT_LOADED = FabricLoader.getInstance().isModLoaded("quilt_base");
    public static final boolean FABRIC_API_LOADED = FabricLoader.getInstance().isModLoaded("fabric-api");
    public static final boolean ARCHITECTURY_API_LOADED = FabricLoader.getInstance().isModLoaded("architectury");
    public static final Logger LOGGER = LoggerFactory.getLogger("AlliumMixinPlugin");

    @Override
    public void onLoad(String mixinPackage) {
        LOGGER.info("Loading Allium on " + (QUILT_LOADED ? "Quilt" : "Fabric"));
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        if (mixinClassName.contains(".fabric.")) {
            return !QUILT_LOADED;
        } else if (mixinClassName.contains(".fabric_api.")){
            return FABRIC_API_LOADED;
        } else if (mixinClassName.contains(".architectury_api.")){
            return ARCHITECTURY_API_LOADED;
        } else {
            return true;
        }
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
