package kombee.sammiejamslimes.render;

import com.google.gson.JsonArray;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import kombee.sammiejamslimes.render.models.ModelSlimeFace;
import kombee.sammiejamslimes.render.models.ModelSlimeInner;
import kombee.sammiejamslimes.render.models.ModelSlimeOuter;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class ColorRender extends RenderLiving<EntityJamSlimeBase> {
    private final SammieJamSlimeData slimeData;
    private final String appearanceType;
    private final Object appearanceSource;
    private final ModelSlimeOuter modelOuterLayer;
    private final ModelSlimeInner modelInnerLayer;
    private final ModelSlimeFace modelSlimeFace;
    private static final ResourceLocation OUTER_LAYER_TEXTURE = new ResourceLocation("sammiejamslimes:textures/entity/outer.png");
    private static final ResourceLocation INNER_LAYER_TEXTURE = new ResourceLocation("sammiejamslimes:textures/entity/inner.png");
    private static final ResourceLocation FACE_LAYER_TEXTURE = new ResourceLocation("sammiejamslimes:textures/entity/face.png");

    public ColorRender(RenderManager renderManagerIn, SammieJamSlimeData slimeData, String appearanceType, Object appearanceSource) {
        super(renderManagerIn, new ModelSlime(0), 0.5F);
        this.slimeData = slimeData;
        this.appearanceType = appearanceType;
        this.appearanceSource = appearanceSource;
        this.modelOuterLayer = new ModelSlimeOuter();
        this.modelInnerLayer = new ModelSlimeInner();
        this.modelSlimeFace = new ModelSlimeFace();
    }

    @Override
    protected void preRenderCallback(EntityJamSlimeBase entity, float partialTickTime) {
        // Calculate the scale based on the slime's size
        float sizeScale = 0.0625F * entity.getSlimeSize();

        GlStateManager.scale(sizeScale, sizeScale, sizeScale);

        super.preRenderCallback(entity, partialTickTime);
    }

    @Override
    public void doRender(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Rendering logic for entities with color appearance
        if (appearanceSource instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) appearanceSource;

            if (jsonArray.size() == 4) {
                try {
                    float red = jsonArray.get(0).getAsFloat();
                    float green = jsonArray.get(1).getAsFloat();
                    float blue = jsonArray.get(2).getAsFloat();
                    float alpha = jsonArray.get(3).getAsFloat();

                    GlStateManager.pushMatrix();
                    GlStateManager.pushAttrib();

                    GlStateManager.enableBlend();
                    GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

                    float entityPitch = entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks;

                    GlStateManager.enableDepth();
                    GL11.glDepthFunc(GL11.GL_LEQUAL);

                    // Enable alpha blending for all layers
                    GlStateManager.enableAlpha();

                    // Set the color here to apply to all models
                    GlStateManager.color(red, green, blue, alpha);

                    GL11.glDepthRange(0.01, 1.0);
                    // Render the inner layer (without color blending) FIRST
                    renderInnerLayer(entity, x, y, z, entityYaw, entityPitch, partialTicks);
                    renderFaceLayer(entity, x, y, z, entityYaw, entityPitch, partialTicks);

                    // Restore the previous depth function
                    GlStateManager.depthFunc(GL11.GL_LESS);

                    // Render the outer layer
                    renderOuterLayer(entity, x, y, z, entityYaw, entityPitch, partialTicks);
                    GL11.glDepthRange(0.0, 1.0);

                    // Disable alpha blending after rendering
                    GlStateManager.disableAlpha();

                    // Disable depth testing after rendering
                    GlStateManager.disableDepth();

                    // Restore the previous OpenGL states
                    GlStateManager.popAttrib();
                    GlStateManager.popMatrix();
                } catch (Exception e) {
                    // Handle the exception
                }
            }
        }
    }


    private void renderOuterLayer(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float entityPitch, float partialTicks) {
        // Bind the texture for the outer layer
        bindTexture(OUTER_LAYER_TEXTURE); // Replace with the appropriate texture path

        // Set up transformations and render the outer layer
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        float sizeScale = 0.0625F * entity.getSlimeSize();
        GlStateManager.scale(sizeScale, sizeScale, sizeScale); // Scale according to slime size

        GlStateManager.rotate(-entityYaw, 0.0F, 1.0F, 0.0F); // Rotate around the Y-axis
        GlStateManager.rotate(entityPitch, 1.0F, 0.0F, 0.0F); // Rotate around the X-axis

        // Enable alpha blending specifically for the outer layer
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        // Render the outer layer using the custom outer model
        modelOuterLayer.render(entity, 0, 0, 0, 0, 0, 1.0F); // Use a scale of 1.0F for the model

        // Disable blending after rendering the outer layer
        GlStateManager.disableBlend();

        GlStateManager.popMatrix();
    }


    private void renderInnerLayer(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float entityPitch, float partialTicks) {
        // Bind the texture for the inner layer
        bindTexture(INNER_LAYER_TEXTURE); // Replace with the appropriate texture path

        // Set up transformations and render the inner layer
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        float sizeScale = 0.0625F * entity.getSlimeSize();
        GlStateManager.scale(sizeScale, sizeScale, sizeScale); // Scale according to slime size

        GlStateManager.rotate(-entityYaw, 0.0F, 1.0F, 0.0F); // Rotate around the Y-axis
        GlStateManager.rotate(entityPitch, 1.0F, 0.0F, 0.0F); // Rotate around the X-axis

        // Disable writing to the alpha channel, but allow writing to RGB channels
        GlStateManager.colorMask(true, true, true, false);

        // Enable standard alpha blending
        GlStateManager.enableBlend();

        // Render the inner layer using the custom inner model
        modelInnerLayer.render(entity, 0, 0, 0, 0, 0, 1.0F); // Use a scale of 1.0F for the model

        GlStateManager.disableBlend();

        // Restore the color mask to allow writing to all color channels
        GlStateManager.colorMask(true, true, true, true);

        GlStateManager.popMatrix();
    }

    private void renderFaceLayer(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float entityPitch, float partialTicks) {
        bindTexture(FACE_LAYER_TEXTURE);

        // Set up transformations and render the face layer
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        float sizeScale = 0.0625F * entity.getSlimeSize();
        GlStateManager.scale(sizeScale, sizeScale, sizeScale);

        // Adjust rotations and translations as needed
        GlStateManager.rotate(-entityYaw, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entityPitch, 1.0F, 0.0F, 0.0F);

        // Render the face layer using the face model
        modelSlimeFace.render(entity, 0, 0, 0, 0, 0, 1.0F); // Use a scale of 1.0F for the model

        GlStateManager.popMatrix();
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityJamSlimeBase entity) {
        // Return the appropriate texture ResourceLocation here
        if ("texture".equals(appearanceType)) {
            if (appearanceSource instanceof String) {
                return new ResourceLocation((String) appearanceSource);
            }
        }
        // Return a default texture if needed
        return new ResourceLocation("modid:textures/entity/default.png");
    }

}
