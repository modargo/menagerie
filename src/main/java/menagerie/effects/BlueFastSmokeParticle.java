package menagerie.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.MathUtils;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.helpers.ImageMaster;
import com.megacrit.cardcrawl.vfx.AbstractGameEffect;

public class BlueFastSmokeParticle extends AbstractGameEffect {
    private final float x;
    private final float y;
    private float scale = 0.01F;
    private final float targetScale;
    private static AtlasRegion img;

    public BlueFastSmokeParticle(float x, float y) {
        if (img == null) {
            img = ImageMaster.EXHAUST_L;
        }

        this.targetScale = MathUtils.random(0.3F, 0.6F) * Settings.scale;
        this.color = new Color(MathUtils.random(0.5F, 0.8F), MathUtils.random(0.8F, 1.0F), 1.0F, 1.0F);
        this.x = x - (float)img.packedWidth / 2.0F;
        this.y = y - (float)img.packedHeight / 2.0F;
        this.rotation = MathUtils.random(360.0F);
        this.duration = 0.6F;
    }

    public void update() {
        Color c;
        if (this.color.r > 0.1F) {
            c = this.color;
            c.r -= Gdx.graphics.getDeltaTime() * 4.0F;
            c = this.color;
            c.g -= Gdx.graphics.getDeltaTime() * 3.0F;
            c = this.color;
            c.b -= Gdx.graphics.getDeltaTime() * 0.5F;
        } else if (this.color.g > 0.1F) {
            c = this.color;
            c.g -= Gdx.graphics.getDeltaTime() * 4.0F;
        } else if (this.color.b > 0.1F) {
            c = this.color;
            c.b -= Gdx.graphics.getDeltaTime() * 4.0F;
        }

        if (this.color.b < 0.0F) {
            this.color.b = 0.0F;
        }

        if (this.color.g < 0.0F) {
            this.color.g = 0.0F;
        }

        if (this.color.r < 0.0F) {
            this.color.r = 0.0F;
        }

        this.scale = Interpolation.swingIn.apply(this.targetScale, 0.1F, this.duration / 0.6F);
        this.rotation += Gdx.graphics.getDeltaTime();
        this.color.a = this.duration;
        this.duration -= Gdx.graphics.getDeltaTime();
        if (this.duration < 0.0F) {
            this.isDone = true;
        }
    }

    public void render(SpriteBatch sb) {
        sb.setColor(this.color);
        sb.draw(img, this.x, this.y, (float)img.packedWidth / 2.0F, (float)img.packedHeight / 2.0F, (float)img.packedWidth, (float)img.packedHeight, this.scale, this.scale, this.rotation);
    }

    public void dispose() {
    }
}
