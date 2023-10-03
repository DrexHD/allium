package dev.hugeblank.allium.loader.resources;

import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourcePackProfile;

public class AlliumPack implements ResourcePackProfile.PackFactory {

    private final ResourcePack pack;

    public AlliumPack(ResourcePack pack) {
        this.pack = pack;
    }

    @Override
    public ResourcePack open(String name) {
        return pack;
    }

    @Override
    public ResourcePack openWithOverlays(String name, ResourcePackProfile.Metadata metadata) {
        return pack;
    }
}
