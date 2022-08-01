package menagerie.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;

public class BlueFastDarkSmokeEffect {
    private float x;
    private final float y;
    private float vX;
    private float rotation;
    private final float fadeInTime;
    private float fadeInTimer;
    private float scale = 0.01F;
    private final float targetScale;
    private boolean fadingIn = true;
    private final Color color;
    private final AtlasRegion img;
    private boolean killed = false;
    private final float killSpeed;

    public BlueFastDarkSmokeEffect(float x, float y) {
        this.targetScale = MathUtils.random(0.5F, 2.0F) * Settings.scale;
        this.fadeInTime = MathUtils.random(1.0F, 1.5F);
        this.fadeInTimer = this.fadeInTime;
        float darkness = MathUtils.random(0.0F, 0.1F);
        this.color = new Color(darkness + 0.05F, darkness + 0.1F, darkness + 0.1F + 0.05F, 1.0F);
        if (this.targetScale > 0.5F) {
            this.img = ImageMaster.EXHAUST_L;
        } else {
            this.img = ImageMaster.EXHAUST_S;
            this.vX /= 3.0F;
        }

        this.x = x + MathUtils.random(-100.0F, 100.0F) * Settings.scale - (float)this.img.packedWidth / 2.0F;
        this.y = y + MathUtils.random(-75.0F, 75.0F) * Settings.scale - (float)this.img.packedHeight / 2.0F;
        this.rotation = MathUtils.random(360.0F);
        this.killSpeed = MathUtils.random(1.0F, 4.0F);
    }

    public void update() {
        if (this.fadingIn) {
            this.fadeInTimer -= Gdx.graphics.getDeltaTime();
            if (this.fadeInTimer < 0.0F) {
                this.fadeInTimer = 0.0F;
                this.fadingIn = false;
            }

            this.scale = Interpolation.swingIn.apply(this.targetScale, 0.01F, this.fadeInTimer / this.fadeInTime);
        }

        this.x += this.vX * Gdx.graphics.getDeltaTime();
        this.rotation += this.vX * 2.0F * Gdx.graphics.getDeltaTime();
        if (this.killed) {
            this.color.a -= this.killSpeed * Gdx.graphics.getDeltaTime();
            if (this.color.a < 0.0F) {
                this.color.a = 0.0F;
            }

            this.scale += 5.0F * Gdx.graphics.getDeltaTime();
        }

    }

    public void kill() {
        this.killed = true;
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(this.img, this.x, this.y, (float)this.img.packedWidth / 2.0F, (float)this.img.packedHeight / 2.0F, (float)this.img.packedWidth, (float)this.img.packedHeight, this.scale, this.scale, this.rotation);
    }
}
