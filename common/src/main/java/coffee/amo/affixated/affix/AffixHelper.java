package coffee.amo.affixated.affix;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.util.ItemHelper;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.*;
import java.util.function.Supplier;


public class AffixHelper {
    public static Map<ResourceLocation, Attribute> ATTRIBUTE_HOLDER = new HashMap<>();
    public static void createLootItem(ItemStack stack, Level level){
        if(stack.getOrCreateTag().contains("affixes")) return;
        Affix testerAff = Affix.getValidAffix(stack, level.random);
        if(testerAff == null) return;
        Rarity rarity = Rarity.rarities.getRandomValue(level.random).get();
        int affixes = level.random.nextInt(rarity.getMaxAffixes())+1;
        List<Affix> appliedAffixes = new ArrayList<>();
        Affixated.LOGGER.info("Creating loot item with " + affixes + " affixes");
        stack.getAttributeModifiers(ItemHelper.getDefaultSlot(stack)).forEach((attribute, attributeModifier) -> {
            Affixated.LOGGER.info("Removing affix " + attributeModifier.getName());
            stack.addAttributeModifier(attribute, attributeModifier, ItemHelper.getDefaultSlot(stack));
        });
        for(int i = 0; i < affixes; i++){
            Affix affix = Affix.getValidAffix(stack, level.random);
            if(affix == null){
                Affixated.LOGGER.info("No affix registered for item " + stack.getItem().getDescriptionId());
                continue;
            }
            if(appliedAffixes.contains(affix)){
                continue;
            }
            AffixInstance instance = new AffixInstance(affix, rarity);
            instance.roll(level);
            instance.apply(stack);
            appliedAffixes.add(affix);
        }
        if(appliedAffixes.size() == 0) {
            Affixated.LOGGER.info("No affixes applied to item " + stack.getItem().getDescriptionId());
            return;
        }
        Component name = Component.translatable(appliedAffixes.get(0).getPrefix().getPath())
                        .withStyle(s->s.withColor(rarity.color).withItalic(false))
                                .append(Component.literal(" "))
                                        .append(Component.translatable(stack.getDescriptionId()))
                                                .withStyle(s->s.withColor(rarity.color).withItalic(false)).append(Component.literal(" "))
                                                        .append(Component.translatable(appliedAffixes.get(appliedAffixes.size()-1).getSuffix().getPath()))
                                                                .withStyle(s->s.withColor(rarity.color).withItalic(false));
        stack.setHoverName(name);;
    }

    public static Tag getOrCreateTagElement(ItemStack stack, String string) {
        if (stack.getTag() != null && stack.getTag().contains(string, 10)) {
            return stack.getTag().get(string);
        } else {
            ListTag compoundTag = new ListTag();
            stack.addTagElement(string, compoundTag);
            return compoundTag;
        }
    }

    public static Supplier<ResourceLocation> getAttributeKey(Attribute att){
        return () -> getKeyByValue(ATTRIBUTE_HOLDER, att);
    }

    public static Supplier<Attribute> getAttribute(ResourceLocation key){
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
