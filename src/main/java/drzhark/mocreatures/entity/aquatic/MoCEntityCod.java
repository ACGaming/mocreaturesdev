/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.aquatic;

import drzhark.mocreatures.MoCreatures;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class MoCEntityCod extends MoCEntityMediumFish {

    public MoCEntityCod(World world) {
        super(world);
        this.setType(2);
    }

    @Override
    public ResourceLocation getTexture() {
        return MoCreatures.proxy.getModelTexture("mediumfish_cod.png");
    }

    @Override
    protected int getEggNumber() {
        return 71;
    }
}
