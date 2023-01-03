package coffee.amo.affixated.slots;

import net.minecraft.world.entity.EquipmentSlot;

public class AffixatedVanillaSlot implements IAffixatedSlot{
    EquipmentSlot slot;

    public AffixatedVanillaSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    @Override
    public String getSlotType() {
        return slot.getName();
    }
}
