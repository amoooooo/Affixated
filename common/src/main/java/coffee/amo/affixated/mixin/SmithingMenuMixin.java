package coffee.amo.affixated.mixin;

import coffee.amo.affixated.Affixated;
import coffee.amo.affixated.affix.AffixHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(SmithingMenu.class)
abstract class SmithingMenuMixin extends ItemCombinerMenu {
    public SmithingMenuMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }
    @Shadow @Final private Level level;


    @Inject(method = "mayPickup", at = @At("HEAD"), cancellable = true)
    private void mayPickup(Player player, boolean bl, CallbackInfoReturnable<Boolean> cir) {
        rerollItem(cir);
    }

    public void rerollItem(CallbackInfoReturnable<Boolean> cir) {
        Affixated.LOGGER.info("rerollItem");
        ItemStack item = this.inputSlots.getItem(0);
        ItemStack item2 = this.inputSlots.getItem(1);
        if(item.hasTag()){
            if(item.getTag().contains("affixes")){
                if(item2.getItem().equals(Items.EXPERIENCE_BOTTLE)){
                    cir.setReturnValue(true);
                }
            }
        }
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void createResult(CallbackInfo ci) {
        doRerollItem(ci);
    }

    public void doRerollItem(CallbackInfo ci) {
        Affixated.LOGGER.info("doRerollItem");
        ItemStack item = this.inputSlots.getItem(0);
        ItemStack item2 = this.inputSlots.getItem(1);
        if(item.hasTag()){
            if(item.getTag().contains("affixes")){
                if(item2.getItem().equals(Items.EXPERIENCE_BOTTLE) && item2.getCount() == 4){
                    ItemStack item3 = item.copy();
                    item3.getTag().remove("affixes");
                    AffixHelper.createLootItem(item3, this.level);
                    this.resultSlots.setItem(0, item3);
                    ci.cancel();
                }
            }
        }
    }
}
