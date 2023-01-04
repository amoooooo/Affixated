package coffee.amo.affixated.affix;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Rarity implements WeightedEntry {
    public static SimpleWeightedRandomList<Rarity> rarities;
    public static Map<String, Rarity> raritiesMap = new HashMap<>();

    public static void setRaritiesList(SimpleWeightedRandomList<Rarity> rarities) {
        Rarity.rarities = rarities;
    }
    private ResourceLocation name;
    private int weight;

    private final int maxAffixes;

    public int color;

    public Rarity(ResourceLocation name, int weight, int maxAffixes, String hex) {
        this.name = name;
        this.weight = weight;
        this.maxAffixes = maxAffixes;
        this.color = Integer.parseInt(hex.replace("#", ""), 16);
    }

    public static Rarity getRarity(String rarity) {
        rarity = rarity.substring(rarity.indexOf(":") + 1);
        return raritiesMap.get(rarity);
    }

    public ResourceLocation getName() {
        return name;
    }

    public Weight getWeight() {
        return Weight.of(weight);
    }

    public int getMaxAffixes() {
        return maxAffixes;
    }

    public void setName(ResourceLocation name) {
        this.name = name;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public String toString() {
        return capitalize(name.getPath().substring(name.getPath().indexOf(":") + 1));
    }

    public static String capitalize(String str) {
        if (str == null || str.isEmpty()) {
            return str;
        }
        return str.substring(0, 1).toUpperCase() + str.substring(1);
    }

}
