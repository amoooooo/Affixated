package coffee.amo.affixated.affix;

import coffee.amo.affixated.platform.Services;
import coffee.amo.affixated.slots.IAffixatedSlot;
import coffee.amo.affixated.util.ItemHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.SimpleWeightedRandomList;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Affix implements WeightedEntry {
    public static SimpleWeightedRandomList<Affix> affixes;
    public static void setAffixesList(SimpleWeightedRandomList<Affix> affixes) {
        Affix.affixes = affixes;
    }
    private Attribute attribute;

    private Map<Rarity, RarityRange> rarityMap = new HashMap<>();
    private List<String> allowedSlots = new ArrayList<>();

    private AttributeModifier.Operation operation;
    private ResourceLocation prefix, suffix, desc;
    private String id;
    private int weight;

    public Affix(String id, Attribute attribute, ResourceLocation prefix, ResourceLocation suffix, ResourceLocation desc, Map<Rarity, RarityRange> rarityMap, List<String> allowedSlots, AttributeModifier.Operation operation, int weight) {
        this.attribute = attribute;
        this.prefix = prefix;
        this.suffix = suffix;
        this.id = id;
        this.rarityMap = rarityMap;
        this.allowedSlots = allowedSlots;
        this.operation = operation;
        this.weight = weight;
        this.desc = desc;
    }

    public static Affix getAffix(String affix) {
        return affixes.unwrap().stream().filter(a -> a.getData().id.equals(affix)).findFirst().orElse(null).getData();
    }

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public void addAllowedSlot(String slot){
        allowedSlots.add(slot);
    }

    public void addAllowedSlots(List<String> slots){
        allowedSlots.addAll(slots);
    }

    public List<String> getAllowedSlots(){
        return allowedSlots;
    }

    public Map<Rarity, RarityRange> getRarityMap() {
        return rarityMap;
    }

    public void addRarity(Rarity rarity, RarityRange range){
        rarityMap.put(rarity, range);
    }

    public void setRarityMap(Map<Rarity, RarityRange> rarityMap) {
        this.rarityMap = rarityMap;
    }

    public RarityRange getRarityRange(Rarity rarity){
        return rarityMap.get(rarity);
    }

    public RarityRange getRarityRange(String rarity){
        for(Rarity r : rarityMap.keySet()){
            if(r.getName().toString().equals(rarity)){
                return rarityMap.get(r);
            }
        }
        return null;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public ResourceLocation getPrefix() {
        return prefix;
    }

    public ResourceLocation getSuffix() {
        return suffix;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }
    public void setPrefix(ResourceLocation prefix) {
        this.prefix = prefix;
    }

    public void setSuffix(ResourceLocation suffix) {
        this.suffix = suffix;
    }

    @Override
    public Weight getWeight() {
        return Weight.of(weight);
    }

    public static Affix getValidAffix(ItemStack stack, RandomSource source, Player player){
        SimpleWeightedRandomList.Builder<Affix> builder = new SimpleWeightedRandomList.Builder<>();
        for(Wrapper<Affix> affix : affixes.unwrap()){
            if(ItemHelper.containsAllIn(affix.getData().getAllowedSlots(), Services.PLATFORM.getDefaultSlots(stack, player))){
                builder.add(affix.getData(), affix.getData().getWeight().asInt());
            }
        }
        return builder.build().getRandomValue(source).get();
    }


    public static class RarityRange {
        public double min, max, steps, stepValue;
        public RarityRange(double min, double max, double steps, double stepValue) {
            this.min = min;
            this.max = max;
            this.steps = steps;
            this.stepValue = stepValue;
        }

        public double getRandomInRange(){
            return Math.random() * (max - min) + min;
        }

        public boolean isInRange(double value){
            return value >= min && value <= max;
        }

        public double getRange(){
            return max - min;
        }

        public double getPercentage(double value){
            return (value - min) / getRange();
        }

        public double getValue(double percentage){
            return min + (getRange() * percentage);
        }

        public double getMidpoint(){
            return min + (getRange() / 2);
        }

        public double getMidpointPercentage(){
            return getPercentage(getMidpoint());
        }

    }

}
