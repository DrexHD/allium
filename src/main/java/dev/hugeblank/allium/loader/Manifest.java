package dev.hugeblank.allium.loader;

import com.google.gson.*;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.VersionParsingException;
import net.fabricmc.loader.api.metadata.ModDependency;
import net.fabricmc.loader.api.metadata.ModEnvironment;
import net.fabricmc.loader.impl.lib.gson.JsonReader;
import net.fabricmc.loader.impl.metadata.ModDependencyImpl;
import net.minecraft.util.JsonHelper;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class Manifest {
    private final String id;
    private final String version;
    private final String name;
    private final ModEnvironment environment;
    private final Collection<ModDependency> dependencies;
    private final Entrypoint entrypoints;

    public Manifest(String id, String version, String name, ModEnvironment environment, Collection<ModDependency> dependencies, Entrypoint entrypoint) {
        this.id = id;
        this.version = version;
        this.name = name;
        this.environment = environment;
        this.dependencies = dependencies;
        this.entrypoints = entrypoint;
    }

    public String id() {
        return id;
    }

    public String version() {
        return version;
    }

    public String name() {
        return name;
    }

    public ModEnvironment environment() {
        return environment;
    }

    public Collection<ModDependency> dependencies() {
        return dependencies;
    }

    public Entrypoint entrypoints() {
        return entrypoints;
    }

    public boolean matchesRequirements() {
        FabricLoader loader = FabricLoader.getInstance();
        if (!environment.matches(loader.getEnvironmentType())) return false;
        // Check dependencies requirements
        for (ModDependency dependency : dependencies) {
            Optional<Version> optionalVersion = loader.getModContainer(dependency.getModId())
                    .map(modContainer -> modContainer.getMetadata().getVersion());
            if (dependency.getKind() == ModDependency.Kind.DEPENDS) {
                if (optionalVersion.isEmpty() || !dependency.matches(optionalVersion.get())) return false;
            } else if (dependency.getKind() == ModDependency.Kind.BREAKS) {
                if (optionalVersion.isPresent() && dependency.matches(optionalVersion.get())) return false;
            }
        }
        return true;
    }

    public static class ManifestAdapter implements JsonDeserializer<Manifest> {

        @Override
        public Manifest deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonObject()) {
                JsonObject jsonObject = json.getAsJsonObject();

                String id = JsonHelper.getString(jsonObject, "id");
                String version = JsonHelper.getString(jsonObject, "version");
                String name = JsonHelper.getString(jsonObject, "name");
                ModEnvironment environment = ModEnvironment.UNIVERSAL;
                if (jsonObject.has("environment")) {
                    environment = readEnvironment(JsonHelper.getString(jsonObject, "environment"));
                }
                List<ModDependency> dependencies = new ArrayList<>();
                parseDependencies(jsonObject, ModDependency.Kind.DEPENDS, dependencies);
                parseDependencies(jsonObject, ModDependency.Kind.BREAKS, dependencies);
                Entrypoint entrypoints = context.deserialize(jsonObject.get("entrypoints"), Entrypoint.class);
                return new Manifest(id, version, name, environment, dependencies, entrypoints);
            }
            throw new JsonParseException("Don't know how to turn " + json + " into a Script Manifest");
        }
    }

    // Implements a parser for the schema version 1 of fabric.mod.json dependency entries
    private static void parseDependencies(JsonObject jsonObject, ModDependency.Kind kind, List<ModDependency> dependencies) {
        if (jsonObject.has(kind.getKey())) {
            JsonElement dependencyContainer = jsonObject.get(kind.getKey());
            if (dependencyContainer.isJsonObject()) {
                JsonObject container = dependencyContainer.getAsJsonObject();
                for (String modId : container.keySet()) {
                    final List<String> matcherStringList = new ArrayList<>();
                    JsonElement element = container.get(modId);
                    if (element.isJsonPrimitive()) {
                        JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
                        if (jsonPrimitive.isString()) {
                            matcherStringList.add(jsonPrimitive.getAsString());
                        } else {
                            throw new JsonParseException("Dependency version range must be a string or string array!");
                        }
                    } else if (element.isJsonArray()) {
                        JsonArray jsonArray = element.getAsJsonArray();
                        for (JsonElement jsonElement : jsonArray) {
                            if (jsonElement.isJsonPrimitive()) {
                                JsonPrimitive jsonPrimitive = element.getAsJsonPrimitive();
                                if (jsonPrimitive.isString()) {
                                    matcherStringList.add(jsonPrimitive.getAsString());
                                    continue;
                                }
                            }
                            throw new JsonParseException("Dependency version range array must only contain string values!");
                        }
                    } else {
                        throw new JsonParseException("Dependency version range must be a string or string array!");
                    }
                    try {
                        dependencies.add(new ModDependencyImpl(kind, modId, matcherStringList));
                    } catch (VersionParsingException e) {
                        throw new JsonParseException(e);
                    }
                }
            } else {
                throw new JsonParseException("Dependency container must be an object!");
            }
        }
    }

    /**
     * Adapted from {@link net.fabricmc.loader.impl.metadata.V1ModMetadataParser#readEnvironment(JsonReader)}
     * */
    public static ModEnvironment readEnvironment(String environment) {
        if (environment.isEmpty() || environment.equals("*")) {
            return ModEnvironment.UNIVERSAL;
        } else if (environment.equals("client")) {
            return ModEnvironment.CLIENT;
        } else if (environment.equals("server")) {
            return ModEnvironment.SERVER;
        } else {
            throw new JsonParseException("Invalid environment type: " + environment + "!");
        }
    }

}
