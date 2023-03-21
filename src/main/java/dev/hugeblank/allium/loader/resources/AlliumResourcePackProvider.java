package dev.hugeblank.allium.loader.resources;

import net.minecraft.resource.ResourcePackProfile;
import net.minecraft.resource.ResourcePackProvider;
import net.minecraft.resource.ResourcePackSource;
import net.minecraft.resource.ResourceType;
import net.minecraft.text.Text;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class AlliumResourcePackProvider implements ResourcePackProvider {

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
                factory -> AlliumResourcePack.create("Allium Generated"),
                ResourceType.SERVER_DATA,
                ResourcePackProfile.InsertionPosition.TOP,
                ALLIUM_PACK_SOURCE
        ));
    }
}