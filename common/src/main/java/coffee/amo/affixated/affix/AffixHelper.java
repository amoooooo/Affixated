package coffee.amo.affixated.affix;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.util.ItemHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;


public class AffixHelper {
    public static void createLootItem(ItemStack stack, Level level, Player player){
        Rarity rarity = Rarity.rarities.getRandomValue(level.random).get();
        int affixes = level.random.nextInt(rarity.getMaxAffixes())+1;
        List<Affix> appliedAffixes = new ArrayList<>();
        Affixated.LOGGER.info("Creating loot item with " + affixes + " affixes");
        stack.getAttributeModifiers(ItemHelper.getDefaultSlot(stack)).forEach((attribute, attributeModifier) -> {
            Affixated.LOGGER.info("Removing affix " + attributeModifier.getName());
            stack.addAttributeModifier(attribute, attributeModifier, ItemHelper.getDefaultSlot(stack));
        });
        for(int i = 0; i < affixes; i++){
            Affix affix = Affix.getValidAffix(stack, level.random, player);
            if(appliedAffixes.contains(affix)){
                continue;
            }
            AffixInstance instance = new AffixInstance(affix, rarity);
            instance.roll(level);
            instance.apply(stack, player);
            appliedAffixes.add(affix);
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
}
