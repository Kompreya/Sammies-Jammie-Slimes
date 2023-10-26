package kombee.sammiejamslimes.render;

import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;


public class JamSlimeLayerTexture implements LayerRenderer<EntityJamSlimeBase> {
    private final RenderLiving<EntityJamSlimeBase> slimeRenderer;
    private final ModelBase slimeModel = new ModelSlime(0);

    public JamSlimeLayerTexture(RenderLiving<EntityJamSlimeBase> slimeRendererIn) {
        this.slimeRenderer = slimeRendererIn;
    }

    @Override
    public void doRenderLayer(EntityJamSlimeBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entitylivingbaseIn.isInvisible()) {
            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }
}









