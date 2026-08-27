package io.github.dogeiscut.simulated_items.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.github.dogeiscut.simulated_items.SSI;
import io.github.dogeiscut.simulated_items.content.subLevelItem.SubLevelItemShape;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.*;

public class PhysicsItemPropertiesLoader extends SimpleJsonResourceReloadListener {
    public static final String NAME = "physics_item_properties";

    private static List<Entry> activeEntries = List.of();

    public PhysicsItemPropertiesLoader() {
        super(new Gson(), NAME);
    }

    private record Entry(String selector, int priority, SubLevelItemShape shape) {}

    public static Optional<SubLevelItemShape> resolveShape(ItemStack stack) {
        if (stack.isEmpty()) return Optional.empty();

        for (Entry entry : activeEntries) {
            if (entry.selector().startsWith("#")) {
                ResourceLocation tagId = ResourceLocation.parse(entry.selector().substring(1));
                TagKey<Item> tagKey = TagKey.create(Registries.ITEM, tagId);
                if (stack.is(tagKey)) {
                    return Optional.of(entry.shape());
                }
            } else {
                ResourceLocation itemId = ResourceLocation.parse(entry.selector());
                if (BuiltInRegistries.ITEM.getKey(stack.getItem()).equals(itemId)) {
                    return Optional.of(entry.shape());
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> resourceList, ResourceManager resourceManager, ProfilerFiller profiler) {
        List<Entry> entries = new ArrayList<>();

        for (Map.Entry<ResourceLocation, JsonElement> file : resourceList.entrySet()) {
            try {
                JsonObject json = file.getValue().getAsJsonObject();
                String selector = GsonHelper.getAsString(json, "selector");
                int priority = GsonHelper.getAsInt(json, "priority", 0);
                JsonObject properties = GsonHelper.getAsJsonObject(json, "properties");

                if (!properties.has("simulated_items:shape")) continue;
                String shapeName = GsonHelper.getAsString(properties, "simulated_items:shape");

                SubLevelItemShape shape = Arrays.stream(SubLevelItemShape.values())
                        .filter(s -> s.getSerializedName().equals(shapeName))
                        .findFirst()
                        .orElse(null);

                if (shape == null) {
                    SSI.LOGGER.warn("Unknown simulated_items:shape '{}' in {}, skipping", shapeName, file.getKey());
                    continue;
                }

                entries.add(new Entry(selector, priority, shape));
            } catch (Exception e) {
                SSI.LOGGER.error("Failed to parse physics item properties file {}", file.getKey(), e);
            }
        }

        entries.sort(Comparator.comparingInt(Entry::priority).reversed());
        activeEntries = List.copyOf(entries);

        SSI.LOGGER.info("Loaded {} physics item property rules", activeEntries.size());
    }
}