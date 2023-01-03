package coffee.amo.affixated.forge.slots;

import coffee.amo.affixated.slots.IAffixatedSlot;
import top.theillusivec4.curios.api.type.ISlotType;

public class AffixatedCurioSlot implements IAffixatedSlot {
    ISlotType slot;

    public AffixatedCurioSlot(ISlotType slot) {
        this.slot = slot;
    }
    @Override
    public String getSlotType() {
        return slot.getIdentifier();
    }
}
