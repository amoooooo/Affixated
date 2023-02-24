package coffee.amo.affixated.fabric.components;

import net.minecraft.nbt.CompoundTag;

public class ManaComponentContainer implements ManaComponent {
    private float currentMana = 0;
    private float maxMana = 100;
    private float manaRegen = 0.1f;
    private float manaRegenDelay = 15;
    @Override
    public void readFromNbt(CompoundTag tag) {
        tag.getFloat("currentMana");
        tag.getFloat("maxMana");
        tag.getFloat("manaRegen");
        tag.getFloat("manaRegenDelay");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putFloat("currentMana", currentMana);
        tag.putFloat("maxMana", maxMana);
        tag.putFloat("manaRegen", manaRegen);
        tag.putFloat("manaRegenDelay", manaRegenDelay);
    }

    @Override
    public float getMana() {
        return currentMana;
    }

    @Override
    public void setMana(float mana) {
        currentMana = mana;
    }

    @Override
    public void addMana(float mana) {
        currentMana += mana;
    }

    @Override
    public void removeMana(float mana) {
        currentMana -= mana;
    }

    @Override
    public void setMaxMana(float maxManaV) {
        maxMana = maxManaV;
    }

    @Override
    public float getMaxMana() {
        return maxMana;
    }

    @Override
    public void setManaRegen(float manaRegenV) {
        manaRegen = manaRegenV;
    }

    @Override
    public float getManaRegen() {
        return manaRegen;
    }

    @Override
    public void setManaRegenDelay(float manaRegenDelayV) {
        manaRegenDelay = manaRegenDelayV;
    }

    @Override
    public float getManaRegenDelay() {
        return manaRegenDelay;
    }
}
