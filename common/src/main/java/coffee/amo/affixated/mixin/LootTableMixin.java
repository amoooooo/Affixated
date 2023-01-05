package coffee.amo.affixated.mixin;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.affix.Affix;
import coffee.amo.affixated.affix.AffixHelper;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.List;
import java.util.function.Consumer;

@Mixin(LootTable.class)
public class LootTableMixin {
    @Inject(method = "fill", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V", ordinal = 1), locals = LocalCapture.CAPTURE_FAILSOFT)
    private void fillMixin(Container container, LootContext lootContext, CallbackInfo ci, ObjectArrayList objectArrayList, RandomSource randomSource, List list, ObjectListIterator var6, ItemStack itemStack){
        Affix aff = Affix.getValidAffix(itemStack, lootContext.getLevel().random);
        if (aff != null) {
            AffixHelper.createLootItem(itemStack, lootContext.getLevel());
        }
    }

    @Inject(method = "getRandomItems(Lnet/minecraft/world/level/storage/loot/LootContext;)Lit/unimi/dsi/fastutil/objects/ObjectArrayList;", at = @At("TAIL"), locals = LocalCapture.CAPTURE_FAILSOFT, cancellable = true)
    private void getRandomItemsMixin(LootContext lootContext, CallbackInfoReturnable<ObjectArrayList<ItemStack>> cir, ObjectArrayList<ItemStack> objectArrayList){
        objectArrayList.forEach(stack -> {
            AffixHelper.createLootItem(stack, lootContext.getLevel());
        });
        cir.setReturnValue(objectArrayList);
    }
}
