package kombee.sammiejamslimes.render;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class JamSlimeLayerColor implements LayerRenderer<EntityJamSlimeBase> {
    private final RenderLiving<EntityJamSlimeBase> slimeRenderer;
    private final ModelBase slimeModel = new ModelSlime(0);
    private final Color color;

    public JamSlimeLayerColor(RenderLiving<EntityJamSlimeBase> slimeRendererIn, Color color) {
        this.slimeRenderer = slimeRendererIn;
        this.color = color;
    }

    @Override
    public void doRenderLayer(EntityJamSlimeBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (!entitylivingbaseIn.isInvisible()) {
            applyColorBlendingToTexture();

            GlStateManager.enableNormalize();
            GlStateManager.enableBlend();
            GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            Minecraft.getMinecraft().getTextureManager().bindTexture(getColoredSlimeTexture());

            this.slimeModel.setModelAttributes(this.slimeRenderer.getMainModel());
            this.slimeModel.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);

            restoreOriginalTexture();

            GlStateManager.disableBlend();
            GlStateManager.disableNormalize();
        }
    }

    @Override
    public boolean shouldCombineTextures() {
        return true;
    }

    private ResourceLocation getColoredSlimeTexture() {
        return new ResourceLocation(SammieJamSlimes.MODID, "textures/entity/slime_color.png");
    }

    private void applyColorBlendingToTexture() {
        int red = color.getRed();
        int green = color.getGreen();
        int blue = color.getBlue();
        int alpha = color.getAlpha();
    }

    private void restoreOriginalTexture() {
    }
}










