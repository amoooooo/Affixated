package coffee.amo.affixated.fabric.slots;

import coffee.amo.affixated.slots.IAffixatedSlot;
import dev.emi.trinkets.TrinketSlot;
import dev.emi.trinkets.api.SlotGroup;
import dev.emi.trinkets.api.SlotType;

public class AffixatedTrinketSlot implements IAffixatedSlot {
    SlotType type;

    public AffixatedTrinketSlot(SlotType type) {
        this.type = type;
    }
    @Override
    public String getSlotType() {
        return type.getGroup() + "/" + type.getName();
    }
}
