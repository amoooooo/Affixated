package coffee.amo.affixated.affix;

import coffee.amo.affixated.Affixated;
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

    public static Rarity getRar(String id){
        id = id.substring(id.indexOf(":")+1);
        for(String rarity : raritiesMap.keySet()){
            if(rarity.toLowerCase().equals(id)){
                return raritiesMap.get(rarity);
            }
        }
        return null;
    }

    public static void setRaritiesList(SimpleWeightedRandomList<Rarity> rarities) {
        Rarity.rarities = rarities;
    }
    private ResourceLocation name;
    private int weight;

    private int maxAffixes;
    private int minAffixes;

    public int color;

    public static CompoundTag raritiesToList(){
        CompoundTag tag = new CompoundTag();
        raritiesMap.forEach((key, value) -> tag.put(key, value.toNbt()));
        return tag;
    }

    public static void listToRarities(CompoundTag tag){
        raritiesMap.clear();
        tag.getAllKeys().forEach(key -> {
            raritiesMap.put(key, fromNbt(tag.getCompound(key)));
        });
        Affixated.LOGGER.info("Loaded " + raritiesMap.size() + " rarities");
    }

    public CompoundTag toNbt(){
        CompoundTag tag = new CompoundTag();
        tag.putString("name", name.toString());
        tag.putInt("weight", weight);
        tag.putInt("maxAffixes", maxAffixes);
        tag.putInt("minAffixes", minAffixes);
        tag.putInt("color", color);
        return tag;
    }

    public static Rarity fromNbt(CompoundTag tag){
        Rarity rarity = new Rarity();
        rarity.name = new ResourceLocation(tag.getString("name"));
        rarity.weight = tag.getInt("weight");
        rarity.maxAffixes = tag.getInt("maxAffixes");
        rarity.minAffixes = tag.getInt("minAffixes");
        rarity.color = tag.getInt("color");
        return rarity;
    }



    public Rarity(ResourceLocation name, int weight, int maxAffixes, int minAffixes, String hex) {
        this.name = name;
        this.weight = weight;
        this.maxAffixes = maxAffixes;
        this.minAffixes = minAffixes;
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

    public int getMinAffixes() {
        return minAffixes;
    }

    public void setMinAffixes(int minAffixes) {
        this.minAffixes = minAffixes;
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
