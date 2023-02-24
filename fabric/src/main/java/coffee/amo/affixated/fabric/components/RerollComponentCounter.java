package coffee.amo.affixated.fabric.components;

import net.minecraft.nbt.CompoundTag;

public class RerollComponentCounter implements RerollComponent{
    private int rerolls = 0;
    @Override
    public void readFromNbt(CompoundTag tag) {
        rerolls = tag.getInt("rerolls");
    }

    @Override
    public void writeToNbt(CompoundTag tag) {
        tag.putInt("rerolls", rerolls);
    }

    public int getRerolls() {
        return rerolls;
    }

    public void setRerolls(int rerolls) {
        this.rerolls = rerolls;
    }

    public void addReroll() {
        this.rerolls++;
    }

    public void removeReroll() {
        this.rerolls--;
    }
}
