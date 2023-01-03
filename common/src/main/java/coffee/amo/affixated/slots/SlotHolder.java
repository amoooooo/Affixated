package coffee.amo.affixated.slots;

import java.util.ArrayList;
import java.util.List;

public class SlotHolder {
    public static List<IAffixatedSlot> slots = new ArrayList<>();

    public static void registerSlot(IAffixatedSlot slot) {
        slots.add(slot);
    }

    public static IAffixatedSlot getSlot(String slotType) {
        for (IAffixatedSlot slot : slots) {
            if (slot.getSlotType().equals(slotType)) {
                return slot;
            }
        }
        return null;
    }
}
