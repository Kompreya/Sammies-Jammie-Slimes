package kombee.sammiejamslimes.render.models;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.entity.Entity;

public class ModelSlimeInner extends ModelBase {
    private ModelRenderer innerLayer;

    public ModelSlimeInner() {
        // Create inner layer cube element
        this.textureWidth = 64;
        this.textureHeight = 32;

        // Create the inner layer ModelRenderer
        this.innerLayer = new ModelRenderer(this, 0, 0);
        this.innerLayer.addBox(-2.0F, 0.0F, -2.0F, 6, 6, 6); // Adjust size and position

        // Adjust the position to center the model within the slime's AABB
        this.innerLayer.setRotationPoint(-1F, 1F, -1F);
    }

    public void render(Entity entity, float f, float f1, float f2, float f3, float f4, float scale) {
        this.innerLayer.render(scale);
    }
}

