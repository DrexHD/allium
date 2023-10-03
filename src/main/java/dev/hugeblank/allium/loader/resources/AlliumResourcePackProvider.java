package dev.hugeblank.allium.loader.resources;

import net.minecraft.resource.*;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;

import java.util.function.Consumer;

public class AlliumResourcePackProvider implements ResourcePackProvider {

    private static final ResourcePack RESOURCE_PACK = new AlliumResourcePack();
    public static final ResourcePackSource ALLIUM_PACK_SOURCE = new ResourcePackSource() {
        @Override
        public Text decorate(Text packName) {
            return Text.translatable("pack.nameAndSource", packName, Text.translatable("pack.source.builtin")).styled(style -> style.withColor(TextColor.parse("#B668F5")));
        }

        @Override
        public boolean canBeEnabledLater() {
            return true;
        }
    };


    @Override
    public void register(Consumer<ResourcePackProfile> profileAdder) {
        // TODO: ResourceType should probably not always be SERVER_DATA
        profileAdder.accept(ResourcePackProfile.create(
            "allium",
            Text.literal("Allium (Scripts)"),
            true,
            new AlliumPack(RESOURCE_PACK),
            ResourceType.SERVER_DATA,
            ResourcePackProfile.InsertionPosition.TOP,
            ALLIUM_PACK_SOURCE
        ));
    }
}