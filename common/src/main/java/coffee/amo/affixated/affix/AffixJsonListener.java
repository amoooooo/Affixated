package coffee.amo.affixated.affix;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.slots.IAffixatedSlot;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AffixJsonListener extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new Gson();
    public static final AffixJsonListener INSTANCE = new AffixJsonListener();
    public AffixJsonListener() {
        super(GSON, "affixes");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> folder, ResourceManager rm, ProfilerFiller profiler) {
        SimpleWeightedRandomList.Builder<Affix> affixesBuilder = new SimpleWeightedRandomList.Builder<>();
        folder.forEach((resourceLocation, jsonElement) -> {
            Attribute attribute = AffixHelper.getAttribute(new ResourceLocation(jsonElement.getAsJsonObject().get("attribute").getAsString())).get();
            String id = resourceLocation.getPath();
            ResourceLocation prefix = ResourceLocation.tryParse("affix.affixated." + id + ".prefix");
            ResourceLocation suffix = ResourceLocation.tryParse("affix.affixated." + id + ".suffix");
            ResourceLocation desc = ResourceLocation.tryParse("affix.affixated." + id + ".desc");
            AttributeModifier.Operation operation = AttributeModifier.Operation.valueOf(jsonElement.getAsJsonObject().get("operation").getAsString());
            JsonObject rarities = jsonElement.getAsJsonObject().get("rarities").getAsJsonObject();
            Map<Rarity, Affix.RarityRange> raritiesMap = new HashMap<>();
            rarities.entrySet().forEach(rarity -> {
                JsonObject rarityObject = rarity.getValue().getAsJsonObject();
                int steps = rarityObject.get("steps").getAsInt();
                double min = rarityObject.get("min").getAsDouble();
                double max = rarityObject.get("max").getAsDouble();
                double stepValue = rarityObject.get("stepValue").getAsDouble();
                raritiesMap.put(Rarity.raritiesMap.get(rarity.getKey()), new Affix.RarityRange(min, max, steps, stepValue));
            });
            JsonArray slots = jsonElement.getAsJsonObject().get("slots").getAsJsonArray();
            List<String> equipmentSlots = new ArrayList<>();
            slots.forEach(slot -> {
                equipmentSlots.add(slot.getAsString());
            });
            int weight = jsonElement.getAsJsonObject().get("weight").getAsInt();
            Affix affix = new Affix(id, attribute, prefix, suffix, desc, raritiesMap, equipmentSlots, operation, weight);
            affixesBuilder.add(affix, weight);
        });
        Affix.setAffixesList(affixesBuilder.build());
        Affixated.LOGGER.info("Loaded " + Affix.affixes.unwrap().size() + " affixes");
    }
}
