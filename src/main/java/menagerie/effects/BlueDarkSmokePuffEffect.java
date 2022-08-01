package menagerie.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

import java.util.ArrayList;

public class BlueDarkSmokePuffEffect extends AbstractGameEffect {
    private static final float DEFAULT_DURATION = 0.8F;
    private final ArrayList<BlueFastDarkSmokeEffect> smoke = new ArrayList<>();

    public BlueDarkSmokePuffEffect(float x, float y) {
        this.duration = DEFAULT_DURATION;

        for(int i = 0; i < 20; ++i) {
            this.smoke.add(new BlueFastDarkSmokeEffect(x, y));
        }

    }

    public void update() {
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        } else if (this.duration < 0.7F) {
            this.killSmoke();
        }

        for (BlueFastDarkSmokeEffect b : this.smoke) {
            b.update();
        }
    }

    private void killSmoke() {
        for (BlueFastDarkSmokeEffect b : this.smoke) {
            b.kill();
        }
    }

    public void render(SpriteBatch sb) {
        for (BlueFastDarkSmokeEffect b : this.smoke) {
            b.render(sb);
        }
    }

    public void dispose() {
    }
}
