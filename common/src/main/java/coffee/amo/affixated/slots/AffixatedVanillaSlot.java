package coffee.amo.affixated.slots;

import net.minecraft.world.entity.EquipmentSlot;

public class AffixatedVanillaSlot implements IAffixatedSlot{
    EquipmentSlot slot;
    String type;

    public AffixatedVanillaSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    @Override
    public String getSlotType() {
        return slot.getName();
    }
}
