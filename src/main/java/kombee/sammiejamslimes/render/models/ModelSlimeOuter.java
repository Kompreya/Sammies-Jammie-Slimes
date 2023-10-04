package kombee.sammiejamslimes.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSlimeOuter extends ModelBase {
    private ModelRenderer outerLayer;

    public ModelSlimeOuter() {
        // Create outer layer cube element
        this.textureWidth = 64;
        this.textureHeight = 32;

        // Create the outer layer ModelRenderer
        this.outerLayer = new ModelRenderer(this, 0, 0);
        this.outerLayer.addBox(-4.0F, 0.0F, -4.0F, 8, 8, 8); // Adjust position to center vertically

        // Adjust the position to center the model within the slime's AABB
        this.outerLayer.setRotationPoint(0.0F, 0.0F, 0.0F); // Set to (0.0F, 6.0F, 0.0F)
    }
    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        this.outerLayer.render(scale);
    }
}
