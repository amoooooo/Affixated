package coffee.amo.affixated.affix;

import coffee.amo.affixated.platform.Services;
import coffee.amo.affixated.slots.IAffixatedSlot;
import coffee.amo.affixated.util.ItemHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
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

import java.util.*;

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

    public static CompoundTag affixesToList(){
        CompoundTag tag = new CompoundTag();
        for(Wrapper<Affix> affix : affixes.unwrap()){
            tag.put(affix.getData().getId(), affix.getData().toNbt());
        }
        return tag;
    }

    public static Affix fromNbt(CompoundTag tag){
        Affix affix = new Affix();
        affix.attribute = Registry.ATTRIBUTE.get(new ResourceLocation(tag.getString("attribute")));
        affix.operation = AttributeModifier.Operation.values()[tag.getInt("operation")];
        affix.prefix = new ResourceLocation(tag.getString("prefix"));
        affix.suffix = new ResourceLocation(tag.getString("suffix"));
        affix.desc = new ResourceLocation(tag.getString("desc"));
        affix.id = tag.getString("id");
        affix.weight = tag.getInt("weight");
        ListTag allowedSlots = tag.getList("allowedSlots", 8);
        for(int i = 0; i < allowedSlots.size(); i++){
            affix.allowedSlots.add(allowedSlots.getString(i));
        }
        ListTag rarityMap = tag.getList("rarityMap", 10);
        for(int i = 0; i < rarityMap.size(); i++){
            CompoundTag rarityTag = rarityMap.getCompound(i);
            Rarity rarity = Rarity.raritiesMap.get(rarityTag.getString("rarity"));
            RarityRange range = new RarityRange(rarityTag.getDouble("min"), rarityTag.getDouble("max"), rarityTag.getDouble("steps"), rarityTag.getDouble("stepValue"));
            affix.rarityMap.put(rarity, range);
        }
        return affix;
    }

    public static void listToAffixes(CompoundTag tag){
        SimpleWeightedRandomList.Builder<Affix> builder = new SimpleWeightedRandomList.Builder<>();
        for(String key : tag.getAllKeys()){
            Affix affix = fromNbt(tag.getCompound(key));
            builder.add(affix, affix.getWeight().asInt());
        }
        setAffixesList(builder.build());
    }

    public CompoundTag toNbt(){
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id);
        tag.putString("attribute", AffixHelper.getAttributeKey(attribute).toString());
        tag.putString("operation", operation.toString());
        tag.putString("prefix", prefix.toString());
        tag.putString("suffix", suffix.toString());
        tag.putString("desc", desc.toString());
        tag.putInt("weight", weight);
        ListTag allowedSlots = new ListTag();
        for(String slot : this.allowedSlots){
            allowedSlots.add(StringTag.valueOf(slot));
        }
        tag.put("allowedSlots", allowedSlots);
        CompoundTag rarityMap = new CompoundTag();
        for(Map.Entry<Rarity, RarityRange> entry : this.rarityMap.entrySet()){
            rarityMap.put(entry.getKey().toString(), entry.getValue().toNbt());
        }
        tag.put("rarityMap", rarityMap);
        return tag;
    }

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
    public Affix(){

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

    public static Affix getValidAffix(ItemStack stack, RandomSource source){
        SimpleWeightedRandomList.Builder<Affix> builder = new SimpleWeightedRandomList.Builder<>();
        for(Wrapper<Affix> affix : affixes.unwrap()){
            if(ItemHelper.containsAllIn(affix.getData().getAllowedSlots(), Services.PLATFORM.getDefaultSlots(stack))){
                builder.add(affix.getData(), affix.getData().getWeight().asInt());
            }
        }
        return builder.build().getRandomValue(source).orElse(null);
    }


    public static class RarityRange {
        public double min, max, steps, stepValue;
        public RarityRange(double min, double max, double steps, double stepValue) {
            this.min = min;
            this.max = max;
            this.steps = steps;
            this.stepValue = stepValue;
        }

        public CompoundTag toNbt(){
            CompoundTag tag = new CompoundTag();
            tag.putDouble("min", min);
            tag.putDouble("max", max);
            tag.putDouble("steps", steps);
            tag.putDouble("stepValue", stepValue);
            return tag;
        }

        public static RarityRange fromNbt(CompoundTag tag){
            return new RarityRange(tag.getDouble("min"), tag.getDouble("max"), tag.getDouble("steps"), tag.getDouble("stepValue"));
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
