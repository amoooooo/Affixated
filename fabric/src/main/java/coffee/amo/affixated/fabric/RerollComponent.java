package coffee.amo.affixated.fabric;

import dev.onyxstudios.cca.api.v3.component.Component;
import net.minecraft.nbt.CompoundTag;

public interface RerollComponent extends Component {
    public void readFromNbt(CompoundTag tag);

    public void writeToNbt(CompoundTag tag);

    public int getRerolls();

    public void setRerolls(int rerolls);

    public void addReroll();

    public void removeReroll();
}
