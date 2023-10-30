package kombee.sammiejamslimes.entities.jamslimes;

import kombee.sammiejamslimes.data.SammieJamSlimeData;
import kombee.sammiejamslimes.entities.EntityJamSlimeBase;
import net.minecraft.world.World;

public class EntityJamSlime2 extends EntityJamSlimeBase {
    public EntityJamSlime2(World worldIn) {
        super(worldIn);
    }

    public EntityJamSlime2(World worldIn, int slimeSize) {
        super(worldIn, slimeSize);
    }



    @Override
    public boolean getCanSpawnHere() {
        SammieJamSlimeData slimeData = getSammieJamSlimeData();
        return slimeData != null && slimeData.isSpawningEnable() && super.getCanSpawnHere();
    }

}