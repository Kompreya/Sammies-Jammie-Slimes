package kombee.sammiejamslimes.render;

import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import kombee.sammiejamslimes.render.models.SlimeModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelSlime;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class TextureRender extends RenderLiving<EntityJamSlimeBase> {
    private final SammieJamSlimeData slimeData;
    private final String appearanceType;
    private final Object appearanceSource;
    private final SlimeHelper slimeRender;
    private final SlimeModel slimeModel;

    public TextureRender(RenderManager renderManagerIn, SammieJamSlimeData slimeData, String appearanceType, Object appearanceSource) {
        super(renderManagerIn, new SlimeModel(0), 0.5F);
        this.slimeRender = new SlimeHelper(renderManagerIn);
        this.slimeModel = (SlimeModel) mainModel;
        this.slimeData = slimeData;
        this.appearanceType = appearanceType;
        this.appearanceSource = appearanceSource;
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityJamSlimeBase entity) {
        // Return the appropriate texture ResourceLocation here
        if ("texture".equals(appearanceType)) {
            try {
                // Load the texture from the external path
                BufferedImage textureImage = ImageIO.read(new File(String.valueOf(appearanceSource)));

                // Bind the custom texture to your custom model
                slimeModel.setTexture(textureImage);

                // Return a placeholder texture (it won't be used)
                return new ResourceLocation("modid:textures/entity/default.png");
            } catch (IOException e) {
                // Handle any potential exceptions here
                e.printStackTrace();
            }
        }

        // Return a default texture if needed
        return new ResourceLocation("modid:textures/entity/default.png");
    }

    @Override
    public void doRender(EntityJamSlimeBase entity, double x, double y, double z, float entityYaw, float partialTicks) {
        // Your custom rendering logic here

        // Delegate the rendering of the slime to the CustomSlimeRender helper class
        slimeRender.doRender(entity, x, y, z, entityYaw, partialTicks);

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }
}


