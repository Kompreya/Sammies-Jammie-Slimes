package kombee.sammiejamslimes.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.awt.image.BufferedImage;

public class SlimeModel extends ModelBase {
    ModelRenderer slimeBodies;
    ModelRenderer slimeRightEye;
    ModelRenderer slimeLeftEye;
    ModelRenderer slimeMouth;
    private ResourceLocation textureLocation;

    public SlimeModel(int slimeBodyTexOffY) {
        if (slimeBodyTexOffY > 0) {
            this.slimeBodies = new ModelRenderer(this, 0, 0); // Default UV coordinates
            this.slimeBodies.addBox(-3.0F, 1.0F, -3.0F, 6, 6, 6);

            this.slimeRightEye = new ModelRenderer(this, 8, 0); // Default UV coordinates
            this.slimeRightEye.addBox(-3.25F, 2.0F, -3.5F, 2, 2, 2);

            this.slimeLeftEye = new ModelRenderer(this, 8, 4); // Default UV coordinates
            this.slimeLeftEye.addBox(1.25F, 2.0F, -3.5F, 2, 2, 2);

            this.slimeMouth = new ModelRenderer(this, 0, 12); // Default UV coordinates
            this.slimeMouth.addBox(0.0F, 5.0F, -3.5F, 1, 1, 1);
        } else {
            this.slimeBodies = new ModelRenderer(this, 0, slimeBodyTexOffY);
            this.slimeBodies.addBox(-4.0F, 0.0F, -4.0F, 8, 8, 8);
        }
    }

    public void render(Entity entityIn, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        this.setRotationAngles(limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale, entityIn);
        GlStateManager.translate(0.0F, 0.001F, 0.0F);
        this.slimeBodies.render(scale);

        if (this.slimeRightEye != null) {
            this.slimeRightEye.render(scale);
            this.slimeLeftEye.render(scale);
            this.slimeMouth.render(scale);
        }
    }

    public void setTexture(ResourceLocation textureLocation) {
        this.textureLocation = textureLocation;
        // Set the texture for your model's ModelRenderer objects
        // The UV coordinates are based on your JSON file
        // The following values are just placeholders; you need to replace them

        // UV coordinates for slimeBodies
        slimeBodies.setTextureOffset(0, 0); // North face
        slimeBodies.setTextureOffset(0, 0); // East face
        slimeBodies.setTextureOffset(0, 0); // South face
        slimeBodies.setTextureOffset(0, 0); // West face
        slimeBodies.setTextureOffset(0, 0); // Up face
        slimeBodies.setTextureOffset(0, 0); // Down face

        // UV coordinates for slimeRightEye
        slimeRightEye.setTextureOffset(0, 0); // North face
        slimeRightEye.setTextureOffset(0, 0); // East face
        slimeRightEye.setTextureOffset(0, 0); // South face
        slimeRightEye.setTextureOffset(0, 0); // West face
        slimeRightEye.setTextureOffset(0, 0); // Up face
        slimeRightEye.setTextureOffset(0, 0); // Down face

        // UV coordinates for slimeLeftEye
        slimeLeftEye.setTextureOffset(0, 0); // North face
        slimeLeftEye.setTextureOffset(0, 0); // East face
        slimeLeftEye.setTextureOffset(0, 0); // South face
        slimeLeftEye.setTextureOffset(0, 0); // West face
        slimeLeftEye.setTextureOffset(0, 0); // Up face
        slimeLeftEye.setTextureOffset(0, 0); // Down face

        // UV coordinates for slimeMouth
        slimeMouth.setTextureOffset(0, 0); // North face
        slimeMouth.setTextureOffset(0, 0); // East face
        slimeMouth.setTextureOffset(0, 0); // South face
        slimeMouth.setTextureOffset(0, 0); // West face
        slimeMouth.setTextureOffset(0, 0); // Up face
        slimeMouth.setTextureOffset(0, 0); // Down face
    }
    public ResourceLocation getTextureLocation() {
        return textureLocation;
    }
}

