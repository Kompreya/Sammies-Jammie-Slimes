package kombee.sammiejamslimes.entities;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.monster.IMob;

import java.lang.reflect.Constructor;


public class EntityJamSlimeBase extends EntitySlime implements IMob {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);
    private boolean hasSplit = false;

    public EntityJamSlimeBase(World worldIn) {
        super(worldIn);
    }

    @Override
    public EntityJamSlimeBase createInstance() {
        try {
            // Use reflection to create a new instance of the same class
            Constructor<? extends EntityJamSlimeBase> constructor = this.getClass().getConstructor(World.class);
            return constructor.newInstance(this.world);
        } catch (Exception e) {
            LOGGER.error("Error creating child slime.", e);
            return null;
        }
    }

    @Override
    public void setDead() {
        if (!this.world.isRemote && !hasSplit) {
            int size = this.getSlimeSize();
            if (size > 1) {
                // Calculate how many smaller slimes should be spawned
                int numSmallerSlimes = size / 2;
                if (size % 2 != 0 && this.rand.nextBoolean()) {
                    numSmallerSlimes++;
                }

                for (int i = 0; i < numSmallerSlimes; i++) {
                    EntityJamSlimeBase newSlime = createInstance();
                    if (this.hasCustomName()) {
                        newSlime.setCustomNameTag(this.getCustomNameTag());
                    }

                    newSlime.copyLocationAndAnglesFrom(this);
                    newSlime.setSlimeSize(size / 2, true);

                    // Ensure that only instances of the custom slime class are spawned
                    if (newSlime instanceof EntityJamSlimeBase) {
                        this.world.spawnEntity(newSlime);
                    }
                }

                // Set the flag to prevent multiple splits
                hasSplit = true;
            }
        }

        super.setDead();
    }

    public SammieJamSlimeData getSammieJamSlimeData() {
        Class<? extends EntityJamSlimeBase> entityClass = this.getClass();
        return SlimeEntityRegistry.getEntityDataForClass(entityClass);
    }
}






