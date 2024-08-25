/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.passive;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.MoCEntityAnimal;
import drzhark.mocreatures.entity.ai.EntityAIFleeFromPlayer;
import drzhark.mocreatures.entity.ai.EntityAIWanderMoC2;
import drzhark.mocreatures.init.MoCLootTables;
import drzhark.mocreatures.init.MoCSoundEvents;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIPanic;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNavigateClimber;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;

import javax.annotation.Nullable;

public class MoCEntityMouse extends MoCEntityAnimal {
    private static final DataParameter<Boolean> CLIMBING = EntityDataManager.createKey(MoCEntityMouse.class, DataSerializers.BOOLEAN);

    public MoCEntityMouse(World world) {
        super(world);
        setSize(0.45F, 0.3F);
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(1, new EntityAIFleeFromPlayer(this, 1.2D, 4D));
        this.tasks.addTask(2, new EntityAIPanic(this, 1.4D));
        this.tasks.addTask(5, new EntityAIWanderMoC2(this, 1.0D));
        this.tasks.addTask(6, new EntityAIWatchClosest(this, EntityPlayer.class, 6.0F));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(8.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.35D);
    }

    @Override
    protected PathNavigate createNavigator(World worldIn) {
        return new PathNavigateClimber(this, worldIn);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CLIMBING, Boolean.FALSE);
    }

    @Override
    public void selectType() {
        checkSpawningBiome();

        if (getType() == 0) {
            setType(this.rand.nextInt(3) + 1);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        switch (getType()) {
            case 2:
                return MoCreatures.proxy.getModelTexture("mouse_brown.png");
            case 3:
                return MoCreatures.proxy.getModelTexture("mouse_white.png");
            default:
                return MoCreatures.proxy.getModelTexture("mouse_beige.png");
        }
    }

    @Override
    public boolean checkSpawningBiome() {
        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(getEntityBoundingBox().minY), this.posZ);
        Biome currentbiome = MoCTools.biomeKind(this.world, pos);

        try {
            if (BiomeDictionary.hasType(currentbiome, Type.MESA)) {
                setType(2); // only brown mice
            }

            if (BiomeDictionary.hasType(currentbiome, Type.SNOWY)) {
                setType(3); // only white mice
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    public boolean getIsPicked() {
        return this.getRidingEntity() != null;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MoCSoundEvents.ENTITY_MOUSE_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MoCSoundEvents.ENTITY_MOUSE_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        return MoCSoundEvents.ENTITY_MOUSE_AMBIENT;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return MoCLootTables.MOUSE;
    }

    @Override
    protected SoundEvent getFallSound(int heightIn) {
        return null;
    }

    @Override
    public void fall(float f, float f1) {
    }

    @Override
    public double getYOffset() {
        if (this.getRidingEntity() instanceof EntityPlayer && this.getRidingEntity() == MoCreatures.proxy.getPlayer() && this.world.isRemote) {
            return (super.getYOffset() - 0.7F);
        }

        if ((this.getRidingEntity() instanceof EntityPlayer) && this.world.isRemote) {
            return (super.getYOffset() - 0.1F);
        } else {
            return super.getYOffset();
        }
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        if (this.getRidingEntity() == null) {
            if (this.startRidingPlayer(player)) {
                this.rotationYaw = player.rotationYaw;
            }

            return true;
        }

        return super.processInteract(player, hand);
    }

    @Override
    public boolean isOnLadder() {
        return this.isBesideClimbableBlock();
    }

    public boolean isBesideClimbableBlock() {
        return this.dataManager.get(CLIMBING);
    }

    public void setBesideClimbableBlock(boolean climbing) {
        this.dataManager.set(CLIMBING, climbing);
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
        }
    }


    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (!this.onGround && (this.getRidingEntity() != null)) {
            this.rotationYaw = this.getRidingEntity().rotationYaw;
        }

    }

    public boolean upsideDown() {
        return getIsPicked();
    }

    @Override
    public boolean canRidePlayer() {
        return true;
    }

    public float getEyeHeight() {
        return this.height * 0.575F;
    }
}
