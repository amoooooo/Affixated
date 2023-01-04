package coffee.amo.affixated.affix;

import coffee.amo.affixated.platform.Services;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

public class AffixInstance {
    private Affix affix;
    private double value;
    private Rarity rarity;

    private UUID uuid;

    public AffixInstance(Affix affix, Rarity rarity) {
        this.affix = affix;
        this.value = affix.getRarityRange(rarity.getName().toString()).min;
        this.rarity = rarity;
        this.uuid = UUID.randomUUID();
    }

    public Rarity getRarity() {
        return rarity;
    }

    public void setRarity(Rarity rarity) {
        this.rarity = rarity;
    }

    public Affix getAffix() {
        return affix;
    }

    public double getValue() {
        return value;
    }

    public void setAffix(Affix affix) {
        this.affix = affix;
    }

    public void setValue(double value) {
        this.value = value;
    }

    public UUID getUuid() {
        return uuid;
    }

    public void roll(Level level){
        int rolls = level.random.nextInt((int) (affix.getRarityRange(rarity.getName().toString()).steps+1));
        for(int i = 0; i < rolls; i++){
            value += affix.getRarityRange(rarity.getName().toString()).stepValue;
        }
    }

    public void apply(ItemStack stack){
        Services.PLATFORM.apply(this, stack, Services.PLATFORM.getDefaultSlots(stack).get(0), uuid);
    }

    public CompoundTag toNbt(){
        CompoundTag tag = new CompoundTag();
        tag.putString("affix", affix.getId());
        tag.putString("rarity", rarity.getName().toString());
        tag.putDouble("value", value);
        tag.putUUID("uuid", uuid);
        return tag;
    }

    public static AffixInstance fromNbt(CompoundTag tag){
        AtomicReference<AffixInstance> instance = new AtomicReference<>();
        tag.getAllKeys().forEach(key -> {
            instance.set(new AffixInstance(Affix.getAffix(tag.getCompound(key).getString("affix")), Rarity.getRarity(tag.getCompound(key).getString("rarity"))));
            instance.get().value = tag.getCompound(key).getDouble("value");
            instance.get().uuid = tag.getCompound(key).getUUID("uuid");
        });
        return instance.get();
    }
}
