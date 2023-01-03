package coffee.amo.affixated.affix;

import coffee.amo.affixated.platform.Services;
import coffee.amo.affixated.util.ItemHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import java.util.UUID;

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

    public void apply(ItemStack stack, Player player){
        Services.PLATFORM.apply(this, stack, Services.PLATFORM.getDefaultSlots(stack, player).get(0), uuid);
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
        AffixInstance instance = new AffixInstance(Affix.getAffix(tag.getString("affix")), Rarity.getRarity(tag.getString("rarity")));
        instance.setValue(tag.getDouble("value"));
        instance.uuid = tag.getUUID("uuid");
        return instance;
    }
}
