package coffee.amo.affixated.affix;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.platform.Services;
import coffee.amo.affixated.util.ItemHelper;
import com.mojang.datafixers.util.Pair;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;


public class AffixHelper {
    public static Map<ResourceLocation, Attribute> ATTRIBUTE_HOLDER = new HashMap<>();

    public static void reroll(ItemStack stack, Level level, Player player) {
        if (stack.hasTag()) {
            if (stack.getTag().contains("affixes")) {
                ItemStack finalStack = stack;
                AtomicReference<Rarity> rar = new AtomicReference<>();
                stack.getTag().getList("affixes", 10).forEach(tag -> {
                    AffixInstance inst = AffixInstance.fromNbt((CompoundTag) tag);
                    if(rar.get() == null){
                        rar.set(inst.getRarity());
                    }
                    List<Tag> toRemove = new ArrayList<>();
                    finalStack.getTag().getList("AttributeModifiers", 10).forEach(tag1 -> {
                        if (tag1 instanceof CompoundTag tag2) {
                            if (tag2.getString("AttributeName").equals(AffixHelper.getAttributeKey(inst.getAffix().getAttribute()).get().toString())) {
                                Affixated.LOGGER.info("Removing affix: " + inst.getAffix().getId());
                                toRemove.add(tag1);
                            }
                        }
                    });
                    toRemove.forEach(tag1 -> {
                        finalStack.getTag().getList("AttributeModifiers", 10).remove(tag1);
                    });
                });
                stack.getTag().remove("affixes");
                createLootItem(stack, level);
            } else if (stack.getItem().getDescriptionId().contains("relicex")){
                int slot = player.getInventory().findSlotMatchingItem(stack);
                stack = Services.PLATFORM.rerollRelic(stack);
                createLootItem(stack, level);
                ItemEntity itemEntity = new ItemEntity(level, player.getX(), player.getY(), player.getZ(), stack);
                level.addFreshEntity(itemEntity);

            }
        }
        if(stack.hasTag()){
            stack.getOrCreateTag().putInt("AdminRerolls", 1);
        }
    }

    public static void createLootItem(ItemStack stack, Level level) {
        Affix testerAff = Affix.getValidAffix(stack, level.random);
        if (testerAff == null) {
            return;
        }
        Affixated.LOGGER.info("Attempting to roll loot affixes for item: " + stack.getDisplayName().getString());
        if (stack.getOrCreateTag().contains("affixes")) {
            return;
        }
        List<Component> tooltip = new ArrayList<>();
        try {
            if(Services.PLATFORM.testBadlyCodedMod(stack)) throw new Exception("Badly coded mod detected");
            tooltip = stack.getTooltipLines(null, TooltipFlag.Default.NORMAL);
        } catch (Exception e) {
            Affixated.LOGGER.error("Error while attempting to roll loot affixes for item: " + stack.getDisplayName().getString());
            e.printStackTrace();
        }
//        String line2 = "";
//        if(tooltip.size() > 0){
//            line2 = tooltip.size() > 2 ? tooltip.get(2).getString() : tooltip.get(tooltip.size() - 1).getString();
//        }
        Rarity rarity;
        AtomicReference<String> rarstring = new AtomicReference<>();
        tooltip.forEach(line -> {
            if (Rarity.raritiesMap.containsKey(line.getString().toLowerCase(Locale.ROOT))) {
                rarstring.set(line.getString());
                line.getStyle().withColor(Rarity.raritiesMap.get(line.getString().toLowerCase(Locale.ROOT)).color);
            }
        });
        if (rarstring.get() != null) {
            rarity = Rarity.raritiesMap.get(rarstring.get().toLowerCase(Locale.ROOT));
        } else {
            rarity = Rarity.rarities.getRandomValue(level.random).get();
        }
        int affixes = level.random.nextIntBetweenInclusive(rarity.getMinAffixes(), rarity.getMaxAffixes());
        List<Affix> appliedAffixes = new ArrayList<>();
        stack.getAttributeModifiers(ItemHelper.getDefaultSlot(stack)).forEach((attribute, attributeModifier) -> {
            stack.addAttributeModifier(attribute, attributeModifier, ItemHelper.getDefaultSlot(stack));
        });
        if(rarstring.get() !=null){
            affixes = 0;
        }
        for (int i = 0; i < affixes; i++) {
            Affix affix = Affix.getValidAffix(stack, level.random);
            if (affix == null) {
                Affixated.LOGGER.info("No affix registered for item " + stack.getItem().getDescriptionId());
                continue;
            }
            if (appliedAffixes.contains(affix)) {
                continue;
            }
            Affixated.LOGGER.info("Attempting to roll affix: " + affix.getId() + " for item: " + stack.getDisplayName().getString() + " with rarity: " + rarity.getName());
            AffixInstance instance = new AffixInstance(affix, rarity);
            if (instance.getRarity() == null) {
                Affixated.LOGGER.info("No affix registered for item " + stack.getItem().getDescriptionId());
                continue;
            }
            instance.roll(level);
            instance.apply(stack);
            appliedAffixes.add(affix);
        }
        if (appliedAffixes.size() == 0 && rarstring.get()==null) {
            Affixated.LOGGER.info("No affixes applied to item " + stack.getItem().getDescriptionId());
            return;
        }
        String prefix = "";
        String suffix = "";
        if(!appliedAffixes.isEmpty()){
            int pre = level.random.nextInt(appliedAffixes.size());
            if(!(pre > appliedAffixes.size())){
                if(pre == 0) pre++;
                prefix = appliedAffixes.get(level.random.nextInt(pre)).getPrefix().getPath();
                suffix = level.random.nextInt(appliedAffixes.size()) == pre ? "" : appliedAffixes.get(level.random.nextInt(appliedAffixes.size())).getSuffix().getPath();
            }
        }
        if(rarstring.get() != null){
            Pair<String, String> tip = getPrefixSuffixFromTooltip(stack);
            prefix = tip.getFirst();
            suffix = tip.getSecond();
        }
        Component name = Component.translatable(prefix)
                .withStyle(s -> s.withColor(rarity.color).withItalic(false))
                .append(Component.literal(" "))
                .append(Component.translatable(stack.getDescriptionId()))
                .withStyle(s -> s.withColor(rarity.color).withItalic(false)).append(Component.literal(" "))
                .append(Component.translatable(suffix))
                .withStyle(s -> s.withColor(rarity.color).withItalic(false));
        stack.setHoverName(name);
        stack.getTag().putInt("AdminRerolls", 1);
    }

    public static void createLootItem(ItemStack stack, Level level, Rarity rarity) {
        Affix testerAff = Affix.getValidAffix(stack, level.random);
        if (testerAff == null) {
            return;
        }
        Affixated.LOGGER.info("Attempting to roll loot affixes for item: " + stack.getDisplayName().getString());
        if (stack.getOrCreateTag().contains("affixes")) {
            return;
        }
        List<Component> tooltip = new ArrayList<>();
        try {
            if(Services.PLATFORM.testBadlyCodedMod(stack)) throw new Exception("Badly coded mod detected");
            tooltip = stack.getTooltipLines(null, TooltipFlag.Default.NORMAL);
        } catch (Exception e) {
            Affixated.LOGGER.error("Error while attempting to roll loot affixes for item: " + stack.getDisplayName().getString());
            e.printStackTrace();
        }
//        String line2 = "";
//        if(tooltip.size() > 0){
//            line2 = tooltip.size() > 2 ? tooltip.get(2).getString() : tooltip.get(tooltip.size() - 1).getString();
//        }
        AtomicReference<String> rarstring = new AtomicReference<>();
        tooltip.forEach(line -> {
            if (Rarity.raritiesMap.containsKey(line.getString().toLowerCase(Locale.ROOT))) {
                rarstring.set(line.getString());
                line.getStyle().withColor(Rarity.raritiesMap.get(line.getString().toLowerCase(Locale.ROOT)).color);
            }
        });
        if (rarstring.get() != null) {
            rarity = Rarity.raritiesMap.get(rarstring.get().toLowerCase(Locale.ROOT));
        }
        int affixes = level.random.nextIntBetweenInclusive(rarity.getMinAffixes(), rarity.getMaxAffixes());
        List<Affix> appliedAffixes = new ArrayList<>();
        stack.getAttributeModifiers(ItemHelper.getDefaultSlot(stack)).forEach((attribute, attributeModifier) -> {
            stack.addAttributeModifier(attribute, attributeModifier, ItemHelper.getDefaultSlot(stack));
        });
        if(rarstring.get() !=null){
            affixes = 0;
        }
        for (int i = 0; i < affixes; i++) {
            Affix affix = Affix.getValidAffix(stack, level.random);
            if (affix == null) {
                Affixated.LOGGER.info("No affix registered for item " + stack.getItem().getDescriptionId());
                continue;
            }
            if (appliedAffixes.contains(affix)) {
                continue;
            }
            Affixated.LOGGER.info("Attempting to roll affix: " + affix.getId() + " for item: " + stack.getDisplayName().getString() + " with rarity: " + rarity.getName());
            AffixInstance instance = new AffixInstance(affix, rarity);
            if (instance.getRarity() == null) {
                Affixated.LOGGER.info("No affix registered for item " + stack.getItem().getDescriptionId());
                continue;
            }
            instance.roll(level);
            instance.apply(stack);
            appliedAffixes.add(affix);
        }
        if (appliedAffixes.size() == 0 && rarstring.get()==null) {
            Affixated.LOGGER.info("No affixes applied to item " + stack.getItem().getDescriptionId());
            return;
        }
        String prefix = "";
        String suffix = "";
        if(!appliedAffixes.isEmpty()){
            int pre = level.random.nextInt(appliedAffixes.size());
            if(!(pre > appliedAffixes.size())){
                if(pre == 0) pre++;
                prefix = appliedAffixes.get(level.random.nextInt(pre)).getPrefix().getPath();
                suffix = level.random.nextInt(appliedAffixes.size()) == pre ? "" : appliedAffixes.get(level.random.nextInt(appliedAffixes.size())).getSuffix().getPath();
            }
        }
        if(rarstring.get() != null){
            Pair<String, String> tip = getPrefixSuffixFromTooltip(stack);
            prefix = tip.getFirst();
            suffix = tip.getSecond();
        }
        Rarity finalRarity = rarity;
        Component name = Component.translatable(prefix)
                .withStyle(s -> s.withColor(finalRarity.color).withItalic(false))
                .append(Component.literal(" "))
                .append(Component.translatable(stack.getDescriptionId()))
                .withStyle(s -> s.withColor(finalRarity.color).withItalic(false)).append(Component.literal(" "))
                .append(Component.translatable(suffix))
                .withStyle(s -> s.withColor(finalRarity.color).withItalic(false));
        stack.setHoverName(name);
    }

    public static Pair<String, String> getPrefixSuffixFromTooltip(ItemStack stack){
        String prefix = "";
        String suffix = "";
        if(stack.getTag().contains("Attributes")){
            for(int i = 0; i < stack.getTag().getList("Attributes", 10).size(); i++){
                CompoundTag tag = stack.getTag().getList("Attributes", 10).getCompound(i);
                ResourceLocation name = ResourceLocation.tryParse(tag.getString("Name"));
                if(prefix == ""){
                    prefix = "affix.affixated."+name.getPath()+".prefix";
                } else if (suffix == ""){
                    suffix = "affix.affixated."+name.getPath()+".suffix";
                }
            }
        }
        return new Pair<>(prefix, suffix);
    }

    public static Tag getOrCreateTagElement(ItemStack stack, String string) {
        if (stack.getTag() != null && stack.getTag().contains(string, 10)) {
            Affixated.LOGGER.info("Found tag element: " + string);
            return stack.getTag().get(string);
        } else {
            ListTag compoundTag = new ListTag();
            stack.addTagElement(string, compoundTag);
            return compoundTag;
        }
    }

    public static Supplier<ResourceLocation> getAttributeKey(Attribute att) {
        return () -> getKeyByValue(ATTRIBUTE_HOLDER, att);
    }

    public static Supplier<Attribute> getAttribute(ResourceLocation key) {
        return () -> ATTRIBUTE_HOLDER.get(key);
    }

    public static <T, E> Set<T> getKeysByValue(Map<T, E> map, E value) {
        Set<T> keys = new HashSet<T>();
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                keys.add(entry.getKey());
            }
        }
        return keys;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
