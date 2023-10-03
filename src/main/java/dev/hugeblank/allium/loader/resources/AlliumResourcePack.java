package dev.hugeblank.allium.loader.resources;

import com.google.common.collect.Sets;
import dev.hugeblank.allium.loader.Script;
import net.minecraft.resource.*;
import net.minecraft.resource.metadata.ResourceMetadataReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.PathUtil;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

public class AlliumResourcePack implements ResourcePack {

    private static final Logger LOGGER = LoggerFactory.getLogger(AlliumResourcePack.class);
    private static final List<Script> BASES = new ArrayList<>();

    private final Map<Script, Path> basePaths;

    public static void register(Script script) {
        BASES.add(script);
    }

    public static void drop(Script script) {
        BASES.remove(script);
    }

    public AlliumResourcePack() {
        Map<Script, Path> paths = new HashMap<>();
        BASES.forEach((script) -> paths.put(script, script.getPath()));

        this.basePaths = paths;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> openRoot(String... segments) {
        PathUtil.validatePath(segments);
        String fileName = String.join("/", segments);
        if (fileName.equals(PACK_METADATA_NAME)) {
            return () -> AlliumResourcePack.class.getResourceAsStream("/assets/pack.mcmeta");
        } else if (fileName.equals("pack.png")) {
            return () -> AlliumResourcePack.class.getResourceAsStream("/assets/allium/icon.png");
        }
        return null;
    }

    @Nullable
    @Override
    public InputSupplier<InputStream> open(ResourceType type, Identifier id) {
        for (Map.Entry<Script, Path> basePath : basePaths.entrySet()) {
            Path nsPath = basePath.getValue().resolve(type.getDirectory()).resolve(id.getNamespace());
            InputSupplier<InputStream> inputSupplier = DirectoryResourcePack.open(id, nsPath);
            if (inputSupplier != null) return inputSupplier;
        }
        return null;
    }

    @Override
    public void findResources(ResourceType type, String namespace, String prefix, ResultConsumer consumer) {
        for (Map.Entry<Script, Path> basePath : basePaths.entrySet()) {
            PathUtil.split(prefix).get().ifLeft(prefixSegments -> {
                Path nsPath = basePath.getValue().resolve(type.getDirectory()).resolve(namespace);
                DirectoryResourcePack.findResources(namespace, nsPath, prefixSegments, consumer);
            }).ifRight(result -> LOGGER.error("Invalid path {}: {}", prefix, result.message()));
        }
    }

    @Override
    public Set<String> getNamespaces(ResourceType type) {
        HashSet<String> set = Sets.newHashSet();
        for (Map.Entry<Script, Path> basePath : basePaths.entrySet()) {
            Path path = basePath.getValue().resolve(type.getDirectory());
            try (DirectoryStream<Path> directoryStream2 = Files.newDirectoryStream(path);) {
                for (Path path2 : directoryStream2) {
                    String string = path2.getFileName().toString();
                    if (Identifier.isNamespaceValid(string)) {
                        set.add(string);
                        continue;
                    }
                    LOGGER.warn("Non [a-z0-9_.-] character in namespace {} in pack {}, ignoring", string, basePath.getValue());
                }
            } catch (NoSuchFileException | NotDirectoryException ignored) {
            } catch (IOException iOException) {
                LOGGER.error("Failed to list path {}", path, iOException);
            }
        }
        return set;
    }

    @Nullable
    @Override
    public <T> T parseMetadata(ResourceMetadataReader<T> metaReader) {
        InputSupplier<InputStream> inputSupplier = this.openRoot(PACK_METADATA_NAME);
        if (inputSupplier == null) return null;
        try (InputStream inputStream = inputSupplier.get();) {
            return AbstractFileResourcePack.parseMetadata(metaReader, inputStream);
        } catch (IOException ignored) {
        }
        return null;
    }

    @Override
    public String getName() {
        return "Allium";
    }

    @Override
    public void close() {

    }
}
