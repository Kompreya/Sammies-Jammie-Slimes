package kombee.sammiejamslimes.entities;

import kombee.sammiejamslimes.SammieJamSlimes;
import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.handlers.SlimeTransformHandler;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.minecraft.entity.monster.IMob;

import java.lang.reflect.Constructor;


public class EntityJamSlimeBase extends EntitySlime implements IMob {
    private static final Logger LOGGER = LogManager.getLogger(SammieJamSlimes.MODID);


    public EntityJamSlimeBase(World worldIn) {
        super(worldIn);
    }

    public EntityJamSlimeBase(World worldIn, int slimeSize) {
        super(worldIn);
        this.setSlimeSize(slimeSize, false);
    }

    public void setEntitySize(float width, float height) {
        this.setSize(width, height);
    }



    @Override
    public EntityJamSlimeBase createInstance() {
        try {
            Constructor<? extends EntityJamSlimeBase> constructor = this.getClass().getConstructor(World.class);
            EntityJamSlimeBase newSlime = constructor.newInstance(this.world);

            newSlime.setSlimeSize(this.getSlimeSize(), false);

            return newSlime;
        } catch (Exception e) {
            LOGGER.error("Error creating child slime.", e);
            return null;
        }
    }
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        ItemStack heldItem = player.getHeldItem(hand);

        if (!world.isRemote) {
            SlimeTransformHandler.transformSlime(this, heldItem, this.getSlimeSize(), this.getHealth());
        }

        return true;
    }


    public SammieJamSlimeData getSammieJamSlimeData() {
        Class<? extends EntityJamSlimeBase> entityClass = this.getClass();
        return SlimeEntityRegistry.getEntityDataForClass(entityClass);
    }
}






