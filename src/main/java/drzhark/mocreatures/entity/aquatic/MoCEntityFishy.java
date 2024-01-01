/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.aquatic;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.ai.EntityAIFleeFromEntityMoC;
import drzhark.mocreatures.entity.ai.EntityAIPanicMoC;
import drzhark.mocreatures.entity.ai.EntityAIWanderMoC2;
import drzhark.mocreatures.entity.tameable.MoCEntityTameableAquatic;
import drzhark.mocreatures.init.MoCItems;
import drzhark.mocreatures.init.MoCLootTables;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageHeart;
import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.List;

public class MoCEntityFishy extends MoCEntityTameableAquatic {

    public static final String[] fishNames = {"Blue", "Orange", "Light Blue", "Lime", "Green", "Purple", "Yellow", "Cyan", "Striped", "Red"};
    private static final DataParameter<Boolean> HAS_EATEN = EntityDataManager.createKey(MoCEntityFishy.class, DataSerializers.BOOLEAN);
    public int gestationtime;

    public MoCEntityFishy(World world) {
        super(world);
        setSize(0.5f, 0.3f);
        setAdult(true);
        //setAge(50 + this.rand.nextInt(50));
        setAge(100);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(2, new EntityAIPanicMoC(this, 1.3D));
        this.tasks.addTask(3, new EntityAIFleeFromEntityMoC(this, entity -> (entity.height > 0.3F || entity.width > 0.3F), 2.0F, 0.6D, 1.5D));
        this.tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0D, 80));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(3.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5D);
    }

    @Override
    public void selectType() {
        if (getType() == 0) {
            setType(this.rand.nextInt(fishNames.length) + 1);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        switch (getType()) {
            case 2:
                return MoCreatures.proxy.getModelTexture("fishy_orange.png");
            case 3:
                return MoCreatures.proxy.getModelTexture("fishy_light_blue.png");
            case 4:
                return MoCreatures.proxy.getModelTexture("fishy_lime.png");
            case 5:
                return MoCreatures.proxy.getModelTexture("fishy_green.png");
            case 6:
                return MoCreatures.proxy.getModelTexture("fishy_purple.png");
            case 7:
                return MoCreatures.proxy.getModelTexture("fishy_yellow.png");
            case 8:
                return MoCreatures.proxy.getModelTexture("fishy_cyan.png");
            case 9:
                return MoCreatures.proxy.getModelTexture("fishy_striped.png");
            case 10:
                return MoCreatures.proxy.getModelTexture("fishy_red.png");
            default:
                return MoCreatures.proxy.getModelTexture("fishy_blue.png");
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(HAS_EATEN, Boolean.FALSE);
    }

    public boolean getHasEaten() {
        return this.dataManager.get(HAS_EATEN);
    }

    public void setHasEaten(boolean flag) {
        this.dataManager.set(HAS_EATEN, flag);
    }

    @Override
    public void dropLegacyEgg() {
        int i = this.rand.nextInt(100);
        if (i < 30) { // 30% of egg dropping
            int j = this.rand.nextInt(2);
            for (int k = 0; k < j; k++) {
                entityDropItem(new ItemStack(MoCItems.mocegg, 1, getType()), 0.0F);
            }

        }
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        if (!getIsAdult()) {
            return null;
        }

        return MoCLootTables.FISHY;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.isInsideOfMaterial(Material.WATER)) {
            this.prevRenderYawOffset = this.renderYawOffset = this.rotationYaw = this.prevRotationYaw;
            this.rotationPitch = this.prevRotationPitch;
        }

        if (!this.world.isRemote) {

            if (getIsTamed() && this.rand.nextInt(100) == 0 && getHealth() < getMaxHealth()) {
                this.setHealth(getMaxHealth());
            }

            if (!ReadyforParenting(this)) {
                return;
            }
            int i = 0;
            List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(4D, 3D, 4D));
            for (Entity entity : list) {
                if (entity instanceof MoCEntityFishy) {
                    i++;
                }
            }

            if (i > 1) {
                return;
            }
            List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(4D, 2D, 4D));
            for (int k = 0; k < list.size(); k++) {
                Entity entity1 = list1.get(k);
                if (!(entity1 instanceof MoCEntityFishy) || (entity1 == this)) {
                    continue;
                }
                MoCEntityFishy entityfishy = (MoCEntityFishy) entity1;
                if (!ReadyforParenting(this) || !ReadyforParenting(entityfishy) || (this.getType() != entityfishy.getType())) {
                    continue;
                }
                if (this.rand.nextInt(100) == 0) {
                    this.gestationtime++;
                }
                if (this.gestationtime % 3 == 0) {
                    MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageHeart(this.getEntityId()),
                            new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
                }
                if (this.gestationtime <= 50) {
                    continue;
                }
                int l = this.rand.nextInt(3) + 1;
                for (int i1 = 0; i1 < l; i1++) {
                    MoCEntityFishy entityfishy1 = new MoCEntityFishy(this.world);
                    entityfishy1.setPosition(this.posX, this.posY, this.posZ);
                    this.world.spawnEntity(entityfishy1);
                    MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
                    setHasEaten(false);
                    entityfishy.setHasEaten(false);
                    this.gestationtime = 0;
                    entityfishy.gestationtime = 0;

                    EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24D);
                    if (entityplayer != null) {
                        MoCTools.tameWithName(entityplayer, entityfishy1);
                    }

                    entityfishy1.setAge(20);
                    entityfishy1.setAdult(false);
                    entityfishy1.setTypeInt(getType());
                }

                break;
            }
        }

    }

    public boolean ReadyforParenting(MoCEntityFishy entityfishy) {
        return false; //TOOD pending overhaul of breeding
    }

    @Override
    protected boolean canBeTrappedInNet() {
        return true;
    }

    @Override
    public int nameYOffset() {
        return -25;
    }

    @Override
    public float rollRotationOffset() {
        if (!this.isInsideOfMaterial(Material.WATER)) {
            return -90F;
        }
        return 0F;
    }

    @Override
    protected boolean isFisheable() {
        return !getIsTamed();
    }

    @Override
    protected boolean usesNewAI() {
        return true;
    }

    @Override
    public float getAIMoveSpeed() {
        return 0.10F;
    }

    @Override
    public boolean isMovementCeased() {
        return !isInWater();
    }

    @Override
    protected double maxDivingDepth() {
        return 2.0D;
    }

    @Override
    public float getSizeFactor() {
        return getAge() * 0.006F;
    }

    @Override
    public float getAdjustedXOffset() {
        if (!isInWater()) {
            return -0.1F;
        }
        return 0F;
    }

    @Override
    public float getAdjustedYOffset() {
        if (!this.isInsideOfMaterial(Material.WATER)) {
            return 0.2F;
        }
        return -0.5F;
    }

    public float getEyeHeight() {
        return this.height * 0.65F;
    }
}
