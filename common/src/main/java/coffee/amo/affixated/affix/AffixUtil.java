package coffee.amo.affixated.affix;

public class AffixUtil {
    public static AffixInstance generateAffixInstance(Affix affix, Rarity rarity){
        return new AffixInstance(affix, rarity);
    }
}
