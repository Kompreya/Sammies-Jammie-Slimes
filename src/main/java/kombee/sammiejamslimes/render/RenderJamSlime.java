package kombee.sammiejamslimes.render;

import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import com.google.gson.JsonArray;

public class RenderJamSlime extends RenderLiving<EntityJamSlimeBase> {
    private final SammieJamSlimeData slimeData;
    private final String appearanceType;
    private final Object appearanceSource;

    public RenderJamSlime(RenderManager renderManagerIn, SammieJamSlimeData slimeData, String appearanceType, Object appearanceSource) {
        super(renderManagerIn, new ModelSlime(0), 0.5F);
        this.slimeData = slimeData;
        this.appearanceType = appearanceType;
        this.appearanceSource = appearanceSource;
    }

    @Override
    protected void preRenderCallback(EntityJamSlimeBase entity, float partialTickTime) {
        // Calculate the scale based on the slime's size
        float sizeScale = 1F * entity.getSlimeSize(); // Adjust the multiplier as needed

        GlStateManager.scale(sizeScale, sizeScale, sizeScale);

        super.preRenderCallback(entity, partialTickTime);
    }



    @Override
    protected ResourceLocation getEntityTexture(EntityJamSlimeBase entity) {
        if ("texture".equals(appearanceType)) {
            if (appearanceSource instanceof String) {
                String texturePath = (String) appearanceSource;
                return new ResourceLocation(texturePath);
            }
        }
        return new ResourceLocation("minecraft:textures/entity/slime/slime.png");
    }

    @Override
    public void doRender(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if ("color".equals(appearanceType)) {
            applyColorRenderer(appearanceSource);
        } else if ("texture".equals(appearanceType)) {
            applyTextureRenderer(appearanceSource);
        }
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    private void applyColorRenderer(Object source) {
        if (source instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) source;

            if (jsonArray.size() == 4) {
                try {
                    float red = jsonArray.get(0).getAsFloat();
                    float green = jsonArray.get(1).getAsFloat();
                    float blue = jsonArray.get(2).getAsFloat();
                    float alpha = jsonArray.get(3).getAsFloat();

                    GlStateManager.color(red, green, blue, alpha);
                } catch (Exception e) {
                    // Handle the exception
                }
            }
        }
    }

    private void applyTextureRenderer(Object source) {
        if (source instanceof String) {
            String texturePath = (String) source;
            bindTexture(new ResourceLocation(texturePath));
        }
    }
}