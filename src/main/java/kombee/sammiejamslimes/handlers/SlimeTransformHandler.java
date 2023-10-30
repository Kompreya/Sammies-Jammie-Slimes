package kombee.sammiejamslimes.handlers;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import kombee.sammiejamslimes.entities.SlimeEntityRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Constructor;

public class SlimeTransformHandler {

    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);

    public static void transformSlime(EntityJamSlimeBase originalSlime, ItemStack item, int slimeSize, float currentHealth) {
        if (originalSlime != null && !item.isEmpty()) {
            ResourceLocation heldItemRegistryName = item.getItem().getRegistryName();
            if (heldItemRegistryName != null) {
                String heldItemID = heldItemRegistryName.toString();

                Class<? extends EntityJamSlimeBase> targetEntityClass = SlimeEntityRegistry.getTargetEntityClassForItem(heldItemID);

                if (targetEntityClass != null && !targetEntityClass.equals(originalSlime.getClass())) {
                    transformSlimeTo(originalSlime.world, targetEntityClass, slimeSize, originalSlime, currentHealth);
                    // Consume the item if needed
                    // if (consumeItem) {
                    //     item.shrink(1);
                    // }
                }
            }
        }
    }




    private static void transformSlimeTo(World world, Class<? extends EntityJamSlimeBase> targetEntityClass, int originalSize, EntityJamSlimeBase originalSlime, float currentHealth) {

        if (!world.isRemote) {
            if (targetEntityClass != null) {
                try {
                    originalSlime.setDead();
                    Constructor<? extends EntityJamSlimeBase> constructor = targetEntityClass.getDeclaredConstructor(World.class, int.class);
                    EntityJamSlimeBase targetEntity = constructor.newInstance(world, originalSize);

                    targetEntity.setLocationAndAngles(originalSlime.posX, originalSlime.posY, originalSlime.posZ, originalSlime.rotationYaw, originalSlime.rotationPitch);

                    targetEntity.setHealth(currentHealth);

                    world.spawnEntity(targetEntity);

                } catch (Exception e) {
                    LOGGER.error("Error transforming slime.", e);
                }
            }
        }
    }









}

