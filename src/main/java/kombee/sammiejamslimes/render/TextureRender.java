package kombee.sammiejamslimes.render;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

public class TextureRender extends RenderLiving<EntityJamSlimeBase> {
    private final String textureFilename;

    public TextureRender(RenderManager renderManagerIn, String textureFilename) {
        super(renderManagerIn, new ModelSlime(16), 0.25F);
        this.textureFilename = textureFilename;
        this.addLayer(new JamSlimeLayerTexture(this));
    }

    @Override
    protected void preRenderCallback(EntityJamSlimeBase entitylivingbaseIn, float partialTickTime) {
        float f = 0.999F;
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        float f1 = (float)entitylivingbaseIn.getSlimeSize();
        float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GlStateManager.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityJamSlimeBase entity) {
        return new ResourceLocation(SammieJamSlimes.MODID, "textures/entity/" + textureFilename);
    }
}







