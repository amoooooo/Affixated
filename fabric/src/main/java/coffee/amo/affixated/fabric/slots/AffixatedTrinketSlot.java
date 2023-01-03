package coffee.amo.affixated.fabric.slots;

import coffee.amo.affixated.slots.IAffixatedSlot;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotGroup;

public class AffixatedTrinketSlot implements IAffixatedSlot {
    SlotGroup slot;

    public AffixatedTrinketSlot(SlotGroup slot) {
        this.slot = slot;
    }
    @Override
    public String getSlotType() {
        return slot.getName();
    }

    public SlotGroup getSlot(){
        return slot;
    }
}
