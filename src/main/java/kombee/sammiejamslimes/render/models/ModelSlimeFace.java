package kombee.sammiejamslimes.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSlimeFace extends ModelBase {
    private ModelRenderer faceLayer;

    public ModelSlimeFace() {
        // Create inner layer cube element
        this.textureWidth = 64;
        this.textureHeight = 32;

        // Create the inner layer ModelRenderer
        this.faceLayer = new ModelRenderer(this, 0, 0);
        this.faceLayer.addBox(2.5F, 3F, 2.5F, 2, 2, 2); // Right eye
        this.faceLayer.addBox(-2.5F, 3F, 2.5F, 2, 2, 2); // Left eye
        this.faceLayer.addBox(1.0F, 1.0F, 3.5F, 1, 1, 1); // Mouth

        // Adjust the position to center the model within the slime's AABB
        this.faceLayer.setRotationPoint(-1F, 1F, -1F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        this.faceLayer.render(scale);
    }
}
