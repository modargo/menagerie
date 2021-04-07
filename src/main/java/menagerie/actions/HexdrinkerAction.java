package menagerie.actions;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.actions.common.GainBlockAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.cards.DamageInfo.DamageType;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.ArtifactPower;
import com.megacrit.cardcrawl.powers.DexterityPower;
import com.megacrit.cardcrawl.powers.PoisonPower;
import com.megacrit.cardcrawl.powers.StrengthPower;
import com.megacrit.cardcrawl.relics.ChemicalX;
import com.megacrit.cardcrawl.ui.panels.EnergyPanel;
import menagerie.cards.spells.Hexdrinker;

public class HexdrinkerAction extends AbstractGameAction {
    private boolean freeToPlayOnce;
    private int damage;
    private int block;
    private boolean upgraded;
    private AbstractPlayer p;
    private AbstractMonster m;
    private DamageType damageTypeForTurn;
    private int energyOnUse;

    public HexdrinkerAction(AbstractPlayer p, AbstractMonster m, int damage, int block, boolean upgraded, DamageType damageTypeForTurn, boolean freeToPlayOnce, int energyOnUse) {
        this.p = p;
        this.m = m;
        this.damage = damage;
        this.block = block;
        this.upgraded = upgraded;
        this.freeToPlayOnce = freeToPlayOnce;
        this.duration = Settings.ACTION_DUR_XFAST;
        this.actionType = ActionType.SPECIAL;
        this.damageTypeForTurn = damageTypeForTurn;
        this.energyOnUse = energyOnUse;
    }

    public void update() {
        int effect = EnergyPanel.totalCount;
        if (this.energyOnUse != -1) {
            effect = this.energyOnUse;
        }

        if (this.p.hasRelic(ChemicalX.ID)) {
            effect += 2;
            this.p.getRelic(ChemicalX.ID).flash();
        }

        if (effect > 0) {
            for(int i = 0; i < effect; ++i) {
                this.addToBot(new GainBlockAction(this.p, this.p, this.block));
                this.addToBot(new DamageAction(this.m, new DamageInfo(this.p, this.damage, this.damageTypeForTurn), AttackEffect.BLUNT_LIGHT));
            }

            int multiplier = effect >= 4 ? (upgraded ? 3 : 2) : 1;
            int poison = Hexdrinker.POISON * multiplier;
            this.addToBot(new ApplyPowerAction(this.m, this.p, new PoisonPower(this.m, this.p, poison), poison));
            if (effect >= 2) {
                int artifact = Hexdrinker.ARTIFACT * multiplier;
                this.addToBot(new ApplyPowerAction(this.p, this.p, new ArtifactPower(this.p, artifact), artifact));
            }
            if (effect >= 3) {
                int stats = Hexdrinker.STATS * multiplier;
                this.addToBot(new ApplyPowerAction(this.p, this.p, new StrengthPower(this.p, stats), stats));
                this.addToBot(new ApplyPowerAction(this.p, this.p, new DexterityPower(this.p, stats), stats));
            }

            if (!this.freeToPlayOnce) {
                this.p.energy.use(EnergyPanel.totalCount);
            }
        }

        this.isDone = true;
    }
}
