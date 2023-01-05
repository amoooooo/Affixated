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

    public Rarity(){

    }

    public static void setRaritiesList(SimpleWeightedRandomList<Rarity> rarities) {
        Rarity.rarities = rarities;
    }
    private ResourceLocation name;
    private int weight;

    private int maxAffixes;

    public int color;

    public static CompoundTag raritiesToList(){
        CompoundTag tag = new CompoundTag();
        raritiesMap.forEach((key, value) -> tag.put(key, value.toNbt()));
        return tag;
    }

    public static void listToRarities(CompoundTag tag){
        raritiesMap.clear();
        tag.getAllKeys().forEach(key -> raritiesMap.put(key, fromNbt(tag.getCompound(key))));
    }

    public CompoundTag toNbt(){
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name.toString());
        tag.putInt("weight", weight);
        tag.putInt("maxAffixes", maxAffixes);
        tag.putInt("color", color);
        return tag;
    }

    public static Rarity fromNbt(CompoundTag tag){
        Rarity rarity = new Rarity();
        rarity.name = new ResourceLocation(tag.getString("name"));
        rarity.weight = tag.getInt("weight");
        rarity.maxAffixes = tag.getInt("maxAffixes");
        rarity.color = tag.getInt("color");
        return rarity;
    }



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
