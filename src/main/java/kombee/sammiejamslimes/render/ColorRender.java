package kombee.sammiejamslimes.render;

import com.google.gson.JsonArray;
import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

public class ColorRender extends RenderLiving<EntityJamSlimeBase> {
    private final SammieJamSlimeData slimeData;
    private final String appearanceType;
    private final Object appearanceSource;
    private final JamSlimeLayerColor slimeLayer;

    public ColorRender(RenderManager renderManagerIn, SammieJamSlimeData slimeData, String appearanceType, Object appearanceSource) {
        super(renderManagerIn, new ModelSlime(16), 0.25F);
        this.slimeData = slimeData;
        this.appearanceType = appearanceType;
        this.appearanceSource = appearanceSource;
        this.slimeLayer = new JamSlimeLayerColor(this, getColorFromAppearanceSource(appearanceSource));
        this.addLayer(slimeLayer);
    }


    @Override
    protected void preRenderCallback(EntityJamSlimeBase entitylivingbaseIn, float partialTickTime) {
        float f = 0.999F;
        GlStateManager.scale(0.999F, 0.999F, 0.999F);
        float f1 = (float) entitylivingbaseIn.getSlimeSize();
        float f2 = (entitylivingbaseIn.prevSquishFactor + (entitylivingbaseIn.squishFactor - entitylivingbaseIn.prevSquishFactor) * partialTickTime) / (f1 * 0.5F + 1.0F);
        float f3 = 1.0F / (f2 + 1.0F);
        GlStateManager.scale(f3 * f1, 1.0F / f3 * f1, f3 * f1);
    }

    @Override
    public void doRender(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        if (appearanceSource instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) appearanceSource;

            if (jsonArray.size() == 4) {
                try {
                    float red = jsonArray.get(0).getAsFloat();
                    float green = jsonArray.get(1).getAsFloat();
                    float blue = jsonArray.get(2).getAsFloat();
                    float alpha = jsonArray.get(3).getAsFloat();

                    GlStateManager.color(red, green, blue, alpha);

                    super.doRender(entity, x, y, z, entityYaw, partialTicks);

                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                } catch (Exception e) {
                }
            }
        }
    }


    private Color getColorFromAppearanceSource(Object appearanceSource) {
        if (appearanceSource instanceof JsonArray) {
            JsonArray jsonArray = (JsonArray) appearanceSource;

            if (jsonArray.size() == 4) {
                try {
                    float red = jsonArray.get(0).getAsFloat();
                    float green = jsonArray.get(1).getAsFloat();
                    float blue = jsonArray.get(2).getAsFloat();
                    float alpha = jsonArray.get(3).getAsFloat();

                    return new Color(red, green, blue, alpha);
                } catch (Exception e) {
                }
            }
        }
        return new Color(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityJamSlimeBase entity) {
        return new ResourceLocation(SammieJamSlimes.MODID, "textures/entity/slime_color.png");
    }
}
