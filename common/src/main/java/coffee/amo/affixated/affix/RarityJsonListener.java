package coffee.amo.affixated.affix;

import coffee.amo.affixated.Affixated;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.WeightedRandomList;

import java.util.Map;

public class RarityJsonListener extends SimpleJsonResourceReloadListener {
    public static Gson GSON = new Gson();
    public static final RarityJsonListener INSTANCE = new RarityJsonListener();
    public RarityJsonListener() {
        super(GSON, "rarities");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> folder, ResourceManager rm, ProfilerFiller profiler) {
        Rarity.raritiesMap.clear();
        SimpleWeightedRandomList.Builder<Rarity> raritiesBuilder = new SimpleWeightedRandomList.Builder<>();
        folder.forEach((resourceLocation, jsonElement) -> {
            Rarity rarity = new Rarity(resourceLocation, jsonElement.getAsJsonObject().get("weight").getAsInt(), jsonElement.getAsJsonObject().get("affixes").getAsInt(), jsonElement.getAsJsonObject().get("minAffixes").getAsInt(), jsonElement.getAsJsonObject().get("color").getAsString());
            raritiesBuilder.add(rarity, rarity.getWeight().asInt());
            Rarity.raritiesMap.put(resourceLocation.getPath(), rarity);
        });
        Rarity.setRaritiesList(raritiesBuilder.build());
        Affixated.LOGGER.info("Loaded " + Rarity.raritiesMap.size() + " rarities");
    }
}
