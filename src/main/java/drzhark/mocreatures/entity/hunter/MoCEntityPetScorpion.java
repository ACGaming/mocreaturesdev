/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.hunter;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.ai.EntityAIFleeFromPlayer;
import drzhark.mocreatures.entity.ai.EntityAIFollowOwnerPlayer;
import drzhark.mocreatures.entity.ai.EntityAIWanderMoC2;
import drzhark.mocreatures.entity.tameable.MoCEntityTameableAnimal;
import drzhark.mocreatures.init.MoCItems;
import drzhark.mocreatures.init.MoCLootTables;
import drzhark.mocreatures.init.MoCSoundEvents;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class MoCEntityPetScorpion extends MoCEntityTameableAnimal {

    private static final DataParameter<Boolean> CLIMBING = EntityDataManager.createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> RIDEABLE = EntityDataManager.createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HAS_BABIES = EntityDataManager.createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_SITTING = EntityDataManager.createKey(MoCEntityPetScorpion.class, DataSerializers.BOOLEAN);
    public int mouthCounter;
    public int armCounter;
    public int transformType;
    private boolean isPoisoning;
    private int poisontimer;
    private int transformCounter;

    public MoCEntityPetScorpion(World world) {
        super(world);
        setSize(1.4F, 0.9F);
        this.poisontimer = 0;
        setAdult(false);
        setAge(20);
        setHasBabies(false);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(0, new EntityAISwimming(this));
        this.tasks.addTask(3, new EntityAILeapAtTarget(this, 0.4F));
        this.tasks.addTask(4, new MoCEntityPetScorpion.AIPetScorpionAttack(this));
        this.tasks.addTask(4, new EntityAIWanderMoC2(this, 1.0D));
        this.tasks.addTask(5, new EntityAIWanderAvoidWater(this, 0.8D));
        this.tasks.addTask(5, new EntityAIFleeFromPlayer(this, 1.2D, 4D));
        this.tasks.addTask(6, new EntityAIFollowOwnerPlayer(this, 1.0D, 2F, 10F));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        this.tasks.addTask(7, new EntityAILookIdle(this));
        //this.targetTasks.addTask(1, new EntityAIHunt<>(this, EntityAnimal.class, true));
    }

    // TODO: Varied stats depending on type
    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(3.0D);
    }

    @Override
    public void selectType() {
        if (getType() == 0) {
            setType(1);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        boolean saddle = getIsRideable();

        // Textures based on type for transforming
        if (this.transformCounter != 0 && this.transformType != 0) {
            String newText;
            switch (this.transformType) {
                case 2: // Cave Scorpion
                    newText = saddle ? "scorpion_cave_saddled.png" : "scorpion_cave.png";
                    break;
                case 3: // Fire Scorpion
                    newText = saddle ? "scorpion_fire_saddled.png" : "scorpion_fire.png";
                    break;
                case 4: // Frost Scorpion
                    newText = saddle ? "scorpion_frost_saddled.png" : "scorpion_frost.png";
                    break;
                case 5: // Undead Scorpion
                    newText = saddle ? "scorpion_undead_saddled.png" : "scorpion_undead.png";
                    break;
                default:
                    newText = saddle ? "scorpion_undead_saddled.png" : "scorpion_undead.png";
                    break;
            }

            // Textures flashing during transformation
            if (this.transformCounter > 60 && (this.transformCounter % 3) == 0) {
                return MoCreatures.proxy.getModelTexture(newText);
            }
        }

        switch (getType()) {
            case 1:
                if (!saddle) {
                    return MoCreatures.proxy.getModelTexture("scorpion_dirt.png");
                }
                return MoCreatures.proxy.getModelTexture("scorpion_dirt_saddled.png");
            case 2:
                if (!saddle) {
                    return MoCreatures.proxy.getModelTexture("scorpion_cave.png");
                }
                return MoCreatures.proxy.getModelTexture("scorpion_cave_saddled.png");
            case 3:
                if (!saddle) {
                    return MoCreatures.proxy.getModelTexture("scorpion_fire.png");
                }
                return MoCreatures.proxy.getModelTexture("scorpion_fire_saddled.png");
            case 4:
                if (!saddle) {
                    return MoCreatures.proxy.getModelTexture("scorpion_frost.png");
                }
                return MoCreatures.proxy.getModelTexture("scorpion_frost_saddled.png");
            case 5:
                if (!saddle) {
                    return MoCreatures.proxy.getModelTexture("scorpion_undead.png");
                }
                return MoCreatures.proxy.getModelTexture("scorpion_undead_saddled.png");
            default:
                return MoCreatures.proxy.getModelTexture("scorpion_dirt.png");
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(CLIMBING, Boolean.FALSE);
        this.dataManager.register(HAS_BABIES, Boolean.FALSE);
        this.dataManager.register(IS_SITTING, Boolean.FALSE);
        this.dataManager.register(RIDEABLE, Boolean.FALSE);
    }

    @Override
    public boolean isReadyToFollowOwnerPlayer() {
        return !this.isMovementCeased();
    }

    @Override
    public void setRideable(boolean flag) {
        this.dataManager.set(RIDEABLE, flag);
    }

    @Override
    public boolean getIsRideable() {
        return this.dataManager.get(RIDEABLE);
    }

    public boolean getHasBabies() {
        return getIsAdult() && this.dataManager.get(HAS_BABIES);
    }

    public void setHasBabies(boolean flag) {
        this.dataManager.set(HAS_BABIES, flag);
    }

    public boolean getIsPoisoning() {
        return this.isPoisoning;
    }

    @Override
    public boolean getIsSitting() {
        return this.dataManager.get(IS_SITTING);
    }

    public void setSitting(boolean flag) {
        this.dataManager.set(IS_SITTING, flag);
    }

    public void setPoisoning(boolean flag) {
        if (flag && !this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0),
                    new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        this.isPoisoning = flag;
    }

    @Override
    public void performAnimation(int animationType) {
        if (animationType == 0) // Tail Animation
        {
            setPoisoning(true);
        } else if (animationType == 1) // Attack Animation (Claws)
        {
            this.armCounter = 1;
            swingArm();
        } else if (animationType == 3) // Mouth Movement Animation
        {
            this.mouthCounter = 1;
        } else if (animationType == 5) // Type Transformation (e.g. Undead)
        {
            this.transformCounter = 1;
        }
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
    public boolean attackEntityAsMob(Entity entity) {
        // Claw Attack Sound
        if (this.poisontimer != 1) {
            MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SCORPION_ATTACK);
        }
        return super.attackEntityAsMob(entity);
    }

    @Override
    public void onLivingUpdate() {

        if (!this.onGround && (this.getRidingEntity() != null)) {
            this.rotationYaw = this.getRidingEntity().rotationYaw;
        }

        if (this.mouthCounter != 0 && this.mouthCounter++ > 50) {
            this.mouthCounter = 0;
        }

        if (this.armCounter != 0 && this.armCounter++ > 24) {
            this.armCounter = 0;
        }

        if (getIsPoisoning()) {
            this.poisontimer++;
            if (this.poisontimer == 1) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_SCORPION_STING);
            }

            if (this.poisontimer > 50) {
                this.poisontimer = 0;
                setPoisoning(false);
            }
        }

        if (this.transformCounter > 0) {
            // Sound plays after this amount of time has passed during transformation
            if (this.transformCounter == 60) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_MAGIC_CONVERSION);
            }

            // Transformation completed
            if (++this.transformCounter > 100) {
                this.transformCounter = 0;

                if (this.transformType != 0) {
                    setType(this.transformType);
                }
            }
        }

        super.onLivingUpdate();
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (super.attackEntityFrom(damagesource, i)) {
            Entity entity = damagesource.getTrueSource();
            if (!(entity instanceof EntityLivingBase) || entity instanceof EntityPlayer && getIsTamed()) {
                return false;
            }
            if (entity != this && super.shouldAttackPlayers() && getIsAdult()) {
                setAttackTarget((EntityLivingBase) entity);
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
        if (!getIsPoisoning() && this.rand.nextInt(5) == 0 && entityIn instanceof EntityLivingBase) {
            setPoisoning(true);
            if (getType() <= 1) // Dirt Scorpion
            {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 15 * 20, 1)); // 15 seconds
            } else if (getType() == 2) // Cave Scorpion
            {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 15 * 20, 0)); // 15 seconds
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WEAKNESS, 15 * 20, 0));
            } else if (getType() == 3) // Fire Scorpion
            {
                entityIn.setFire(15);
            } else if (getType() == 4) // Frost Scorpion
            {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 25 * 20, 0)); // 25 seconds
            } else if (getType() == 5) // Undead Scorpion
            {
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.BLINDNESS, 15 * 20, 0)); // 15 seconds
                ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.WITHER, 15 * 20, 0));
            }
        } else {
            swingArm();
        }
        super.applyEnchantments(entityLivingBaseIn, entityIn);
    }

    public void swingArm() {
        if (!this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1),
                    new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (!this.world.isRemote) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
        }
    }

    public boolean swingingTail() {
        return getIsPoisoning() && this.poisontimer < 15;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MoCSoundEvents.ENTITY_SCORPION_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return MoCSoundEvents.ENTITY_SCORPION_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        // Mouth Movement Animation
        if (!this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3),
                    new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        return MoCSoundEvents.ENTITY_SCORPION_AMBIENT;
    }

    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.5F);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        if (!getIsAdult()) {
            return null;
        }

        switch (getType()) {
            case 1:
                return MoCLootTables.DIRT_SCORPION;
            case 2:
                return MoCLootTables.CAVE_SCORPION;
            case 3:
                return MoCLootTables.FIRE_SCORPION;
            case 4:
                return MoCLootTables.FROST_SCORPION;
            case 5:
                return MoCLootTables.UNDEAD_SCORPION;
            default:
                return MoCLootTables.DIRT_SCORPION;
        }
    }

    // TODO: Make it not give items in creative
    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        final Boolean tameResult = this.processTameInteract(player, hand);
        if (tameResult != null) {
            return tameResult;
        }

        final ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && getIsAdult() && !getIsRideable()
                && (stack.getItem() instanceof ItemSaddle)) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            setRideable(true);
            return true;
        }

        if (!stack.isEmpty() && (stack.getItem() == MoCItems.whip) && getIsTamed() && (!this.isBeingRidden())) {
            setSitting(!getIsSitting());
            setIsJumping(false);
            getNavigator().clearPath();
            setAttackTarget(null);
            return true;
        }

        // Transformations
        if (!stack.isEmpty() && this.getIsTamed() && !this.isBeingRidden() && !this.isRiding() && this.transformCounter < 1) {
            // Cave Scorpion (Essence of Darkness)
            if (stack.getItem() == MoCItems.essencedarkness && this.getType() != 2) {
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                }

                transform(2);
                return true;
            }

            // Fire Scorpion (Essence of Fire)
            if (stack.getItem() == MoCItems.essencefire && this.getType() != 3) {
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                }

                transform(3);
                return true;
            }

            // Frost Scorpion (Essence of Ice)
            if (stack.getItem() == MoCItems.essenceIce && this.getType() != 4) {
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                }

                transform(4);
                return true;
            }

            // Undead Scorpion (Essence of Undead)
            if (!stack.isEmpty() && this.getIsTamed() && !this.isBeingRidden() && !this.isRiding() && this.transformCounter < 1 && stack.getItem() == MoCItems.essenceundead && this.getType() != 5) {
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                if (stack.isEmpty()) {
                    player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
                } else {
                    player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
                }

                transform(5);
                return true;
            }
        }

        if (this.getRidingEntity() == null && this.getAge() < 60 && !getIsAdult()) {
            if (this.startRidingPlayer(player)) {
                this.rotationYaw = player.rotationYaw;
                if (!this.world.isRemote && !getIsTamed()) {
                    MoCTools.tameWithName(player, this);
                }
            }

            return true;
        } else if (this.getRidingEntity() != null) {
            MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
            this.dismountRidingEntity();
            this.motionX = player.motionX * 5D;
            this.motionY = (player.motionY / 2D) + 0.5D;
            this.motionZ = player.motionZ * 5D;
            return true;
        }

        if (getIsRideable() && getIsTamed() && getIsAdult() && (!this.isBeingRidden())) {
            player.rotationYaw = this.rotationYaw;
            player.rotationPitch = this.rotationPitch;
            if (!this.world.isRemote) {
                player.startRiding(this);
            }

            return true;
        }

        return super.processInteract(player, hand);
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        setHasBabies(nbttagcompound.getBoolean("Babies"));
        setRideable(nbttagcompound.getBoolean("Saddled"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Babies", getHasBabies());
        nbttagcompound.setBoolean("Saddled", getIsRideable());
    }

    @Override
    public int nameYOffset() {
        int n = (int) (1 - (getAge() * 0.8));
        if (n < -60) {
            n = -60;
        }
        if (getIsAdult()) {
            n = -60;
        }
        if (getIsSitting()) {
            n = (int) (n * 0.8);
        }
        return n;
    }

    // TODO: Overhaul acceptable food
    @Override
    protected boolean isMyHealFood(ItemStack itemstack) {
        return (itemstack.getItem() == MoCItems.ratRaw || itemstack.getItem() == MoCItems.ratCooked);
    }

    @Override
    public int getTalkInterval() {
        return 300;
    }

    @Override
    public void fall(float f, float f1) {
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isBeingRidden();
    }

    @Override
    public boolean rideableEntity() {
        return true;
    }

    @Override
    public boolean isMovementCeased() {
        return (this.isBeingRidden()) || getIsSitting();
    }

    @Override
    public void dropMyStuff() {
        MoCTools.dropSaddle(this, this.world);
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        return EnumCreatureAttribute.ARTHROPOD;
    }

    @Override
    public float getAdjustedYOffset() {
        return 0.2F;
    }

    @Override
    public int getMaxAge() {
        return 120;
    }

    @Override
    public double getMountedYOffset() {
        return (this.height * 0.75D) - 0.15D;
    }

    @Override
    public double getYOffset() {
        if (this.getRidingEntity() instanceof EntityPlayer && this.getRidingEntity() == MoCreatures.proxy.getPlayer() && this.world.isRemote) {
            return 0.1F;
        }

        if ((this.getRidingEntity() instanceof EntityPlayer) && this.world.isRemote) {
            return (super.getYOffset() + 0.1F);
        } else {
            return super.getYOffset();
        }
    }

    @Override
    public void updatePassenger(Entity passenger) {
        double dist = (0.2D);
        double newPosX = this.posX + (dist * Math.sin(this.renderYawOffset / 57.29578F));
        double newPosZ = this.posZ - (dist * Math.cos(this.renderYawOffset / 57.29578F));
        passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
    }

    @Override
    public boolean isNotScared() {
        return getIsTamed();
    }

    public void transform(int tType) {
        if (!this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), tType),
                    new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }

        // Set what type to transform to based on type integer
        this.transformType = tType;
        if (this.transformType != 0) {
            this.transformCounter = 1;
        }
    }

    @Override
    public boolean isReadyToHunt() {
        return this.getIsAdult() && !this.isMovementCeased();
    }

    @Override
    public boolean canAttackTarget(EntityLivingBase entity) {
        return !(entity instanceof MoCEntityFox) && entity.height <= 1D && entity.width <= 1D;
    }

    static class AIPetScorpionAttack extends EntityAIAttackMelee {
        public AIPetScorpionAttack(MoCEntityPetScorpion scorpion) {
            super(scorpion, 1.0D, true);
        }

        @Override
        public boolean shouldContinueExecuting() {
            float f = this.attacker.getBrightness();

            if (f >= 0.5F && this.attacker.getRNG().nextInt(100) == 0) {
                this.attacker.setAttackTarget(null);
                return false;
            } else {
                return super.shouldContinueExecuting();
            }
        }

        @Override
        protected double getAttackReachSqr(EntityLivingBase attackTarget) {
            return 4.0F + attackTarget.width;
        }
    }
}
