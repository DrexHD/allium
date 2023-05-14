package dev.hugeblank.allium.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import dev.hugeblank.allium.Allium;
import dev.hugeblank.allium.loader.Entrypoint;
import dev.hugeblank.allium.loader.Manifest;
import dev.hugeblank.allium.loader.Script;
import net.fabricmc.loader.api.FabricLoader;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class FileHelper {
    /* Allium Script directory spec
      /allium
        /<unique dir name> | unique file name, bonus point if using the namespace ID
          /<libs and stuff>
          manifest.json |  File containing key information about the script. ID, Name, Version, Entrypoint files
    */

    public static final Path SCRIPT_DIR = FabricLoader.getInstance().getGameDir().resolve(Allium.ID);
    public static final Path CONFIG_DIR = FabricLoader.getInstance().getConfigDir().resolve(Allium.ID);
    public static final Path DOCS_BASE_DIR = FabricLoader.getInstance().getGameDir().resolve(Allium.ID + "-docs");
    public static final Path HTML_DOCS_DIR = DOCS_BASE_DIR.resolve("html");
    public static final Path LUA_DOCS_DIR = DOCS_BASE_DIR.resolve("lua");
    public static final Path PERSISTENCE_DIR = FabricLoader.getInstance().getConfigDir().resolve(Allium.ID + "_persistence");
    public static final Path MAPPINGS_CFG_DIR = FabricLoader.getInstance().getConfigDir().resolve(Allium.ID + "_mappings");
    public static final String MANIFEST_FILE_NAME = "manifest.json";
    public static final Gson MANIFEST_GSON = new GsonBuilder()
            .registerTypeHierarchyAdapter(Manifest.class, new Manifest.ManifestAdapter())
            .registerTypeHierarchyAdapter(Entrypoint.class, new Entrypoint.EntrypointAdapter())
            .create();

    public static Path getScriptsDirectory() {
        if (!Files.exists(SCRIPT_DIR)) {
            Allium.LOGGER.warn("Missing allium directory, creating one for you");
            try {
                Files.createDirectory(SCRIPT_DIR);
            } catch (IOException e) {
                throw new RuntimeException("Failed to create allium directory", new FileSystemException(SCRIPT_DIR.toAbsolutePath().toString()));
            }
        }
        return SCRIPT_DIR;
    }

    public static Set<Script> getValidDirScripts(Path p) {
        Set<Script> out = new HashSet<>();
        try {
            Stream<Path> files = Files.list(p);
            files.forEach((scriptDir) -> {
                Path path;
                if (Files.isDirectory(scriptDir)) {
                    path = scriptDir;
                } else {
                    try {
                        FileSystem fs = FileSystems.newFileSystem(scriptDir); // zip, tarball, whatever has a provider.
                        path = fs.getPath("/");
                    } catch (IOException | ProviderNotFoundException e) {
                        return; // Just a file we can't read, ignore it and move on.
                    }
                }
                try {
                    BufferedReader reader = Files.newBufferedReader(path.resolve(MANIFEST_FILE_NAME));
                    Manifest manifest = MANIFEST_GSON.fromJson(reader, Manifest.class);
                    out.add(new Script(manifest, path));
                } catch (IOException e) {
                    Allium.LOGGER.error("Could not find " + MANIFEST_FILE_NAME  + " file on path " + scriptDir, e);
                }
            });
        } catch (IOException e) {
            Allium.LOGGER.error("Could not read from scripts directory", e);
        }
        return out;
    }

    public static JsonElement getConfig(Script script) throws IOException {
        Path path = FileHelper.CONFIG_DIR.resolve(script.getId() + ".json");
        if (Files.exists(path)) {
            return JsonParser.parseReader(Files.newBufferedReader(path));
        }
        return null;
    }

    public static void saveConfig(Script script, JsonElement element) throws IOException {
        Path path = FileHelper.CONFIG_DIR.resolve(script.getId() + ".json");
        Files.deleteIfExists(path);
        OutputStream outputStream = Files.newOutputStream(path);
        String jstr = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create().toJson(element);
        Allium.LOGGER.info(jstr);
        outputStream.write(jstr.getBytes(StandardCharsets.UTF_8));
        outputStream.close();
    }

}
