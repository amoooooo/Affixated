package coffee.amo.affixated.fabric.components;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public interface ManaComponent extends Component {
    public void readFromNbt(CompoundTag tag);

    public void writeToNbt(CompoundTag tag);
    
    public float getMana();
    
    public void setMana(float mana);
    
    public void addMana(float mana);
    
    public void removeMana(float mana);
    
    public void setMaxMana(float maxMana);
    
    public float getMaxMana();
    
    public void setManaRegen(float manaRegen);
    
    public float getManaRegen();
    
    public void setManaRegenDelay(float manaRegenDelay);
    
    public float getManaRegenDelay();
}
