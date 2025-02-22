/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.neutral;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.ai.EntityAIWanderMoC2;
import drzhark.mocreatures.entity.inventory.MoCAnimalChest;
import drzhark.mocreatures.entity.item.MoCEntityEgg;
import drzhark.mocreatures.entity.tameable.MoCEntityTameableAnimal;
import drzhark.mocreatures.entity.tameable.MoCPetData;
import drzhark.mocreatures.init.MoCItems;
import drzhark.mocreatures.init.MoCLootTables;
import drzhark.mocreatures.init.MoCSoundEvents;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSaddle;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;

public class MoCEntityWyvern extends MoCEntityTameableAnimal {

    private static final DataParameter<Boolean> RIDEABLE = EntityDataManager.createKey(MoCEntityWyvern.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CHESTED = EntityDataManager.createKey(MoCEntityWyvern.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(MoCEntityWyvern.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> GHOST = EntityDataManager.createKey(MoCEntityWyvern.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> FLYING = EntityDataManager.createKey(MoCEntityWyvern.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> ARMOR_TYPE = EntityDataManager.createKey(MoCEntityWyvern.class, DataSerializers.VARINT);
    public MoCAnimalChest localchest;
    public ItemStack localstack;
    public int mouthCounter;
    public int wingFlapCounter;
    public int diveCounter;
    protected EntityAIWanderMoC2 wander;
    private int transformType;
    private int transformCounter;
    private int tCounter;
    private float fTransparency;


    public MoCEntityWyvern(World world) {
        super(world);
        setSize(1.45F, 1.55F);
        setAdult(true);
        setTamed(false);
        this.stepHeight = 1.0F;

        // TODO: Make hitboxes adjust depending on size
        /*if (this.rand.nextInt(6) == 0) {
            setAge(50 + this.rand.nextInt(50));
        } else {
            setAge(80 + this.rand.nextInt(20));
        }*/

        setAge(80);
        experienceValue = 20;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(4, this.wander = new EntityAIWanderMoC2(this, 1.0D, 80));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
        //this.targetTasks.addTask(1, new EntityAIHunt<>(this, EntityAnimal.class, true));
        this.targetTasks.addTask(2, new EntityAIHurtByTarget(this, false));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(80.0D);
        this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(14.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(9.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(RIDEABLE, Boolean.FALSE); // rideable: 0 nothing, 1 saddle
        this.dataManager.register(SITTING, Boolean.FALSE); // rideable: 0 nothing, 1 saddle
        this.dataManager.register(CHESTED, Boolean.FALSE);
        this.dataManager.register(FLYING, Boolean.FALSE);
        this.dataManager.register(GHOST, Boolean.FALSE);
        this.dataManager.register(ARMOR_TYPE, 0);// armor 0 by default, 1 metal, 2 gold, 3 diamond, 4 crystaline
    }

    @Override
    protected int getExperiencePoints(EntityPlayer player) {
        return experienceValue;
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData par1EntityLivingData) {
        if (this.world.provider.getDimension() == MoCreatures.proxy.wyvernDimension) this.enablePersistence();
        return super.onInitialSpawn(difficulty, par1EntityLivingData);
    }

    @Override
    public boolean getCanSpawnHere() {
        IBlockState iblockstate = this.world.getBlockState((new BlockPos(this)).down());
        return iblockstate.canEntitySpawn(this);
    }

    @Override
    protected boolean canDespawn() {
        return this.world.provider.getDimension() != MoCreatures.proxy.wyvernDimension;
    }

    public boolean getIsFlying() {
        return this.dataManager.get(FLYING);
    }

    public void setIsFlying(boolean flag) {
        this.dataManager.set(FLYING, flag);
    }

    @Override
    public int getArmorType() {
        return this.dataManager.get(ARMOR_TYPE);
    }

    @Override
    public void setArmorType(int i) {
        this.dataManager.set(ARMOR_TYPE, i);
    }

    @Override
    public boolean getIsRideable() {
        return this.dataManager.get(RIDEABLE);
    }

    @Override
    public void setRideable(boolean flag) {
        this.dataManager.set(RIDEABLE, flag);
    }

    public boolean getIsChested() {
        return this.dataManager.get(CHESTED);
    }

    public void setIsChested(boolean flag) {
        this.dataManager.set(CHESTED, flag);
    }

    @Override
    public boolean getIsSitting() {
        return this.dataManager.get(SITTING);
    }

    public void setSitting(boolean flag) {
        this.dataManager.set(SITTING, flag);
    }

    public boolean getIsGhost() {
        return this.dataManager.get(GHOST);
    }

    public void setIsGhost(boolean flag) {
        this.dataManager.set(GHOST, flag);
    }

    @Override
    public void selectType() {
        if (getType() == 0) {
            if (rand.nextInt(5) == 0) {
                setType(5);
            } else {
                int i = this.rand.nextInt(100);
                if (i <= 12) {
                    setType(1);
                } else if (i <= 24) {
                    setType(2);
                } else if (i <= 36) {
                    setType(3);
                } else if (i <= 48) {
                    setType(4);
                } else if (i <= 60) {
                    setType(9);
                } else if (i <= 72) {
                    setType(10);
                } else if (i <= 84) {
                    setType(11);
                } else if (i <= 95) {
                    setType(12);
                } else {
                    setType(5);
                }
            }
        }
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(calculateMaxHealth());
        this.setHealth(getMaxHealth());
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(calculateAttackDmg());
    }

    @Override
    public boolean isNotScared() {
        return true;
    }

    public double calculateMaxHealth() {
        if (this.getType() == 13) {
            return 100.0D;
        }
        return 80.0D;
    }

    public double calculateAttackDmg() {
        if (this.getType() == 5) {
            return 12.0D;
        }
        return 10.0D;
    }

    /**
     * 1-4 regular wyverns
     * 5 mother wyvern
     * 6 undead
     * 7 light
     * 8 darkness
     * 9-12 extra wyverns
     */
    @Override
    public ResourceLocation getTexture() {
        if (this.transformCounter != 0 && this.transformType > 5) {
            String newText = "wyvern_mother_dark.png";
            if (this.transformType == 6) {
                newText = "wyvern_mother_undead.png";
            }
            if (this.transformType == 7) {
                newText = "wyvern_mother_light.png";
            }
            if (this.transformType == 8) {
                newText = "wyvern_mother_dark.png";
            }

            if ((this.transformCounter % 5) == 0) {
                return MoCreatures.proxy.getModelTexture(newText);
            }
            if (this.transformCounter > 50 && (this.transformCounter % 3) == 0) {
                return MoCreatures.proxy.getModelTexture(newText);
            }

            if (this.transformCounter > 75 && (this.transformCounter % 4) == 0) {
                return MoCreatures.proxy.getModelTexture(newText);
            }
        }

        switch (getType()) {
            case 1:
                return MoCreatures.proxy.getModelTexture("wyvern_jungle.png");
            case 2:
                return MoCreatures.proxy.getModelTexture("wyvern_swamp.png");
            case 3:
                return MoCreatures.proxy.getModelTexture("wyvern_sand.png");
            case 5:
                return MoCreatures.proxy.getModelTexture("wyvern_mother.png");
            case 6:
                return MoCreatures.proxy.getModelTexture("wyvern_mother_undead.png");
            case 7:
                return MoCreatures.proxy.getModelTexture("wyvern_mother_light.png");
            case 8:
                return MoCreatures.proxy.getModelTexture("wyvern_mother_dark.png");
            case 9:
                return MoCreatures.proxy.getModelTexture("wyvern_arctic.png");
            case 10:
                return MoCreatures.proxy.getModelTexture("wyvern_cave.png");
            case 11:
                return MoCreatures.proxy.getModelTexture("wyvern_mountain.png");
            case 12:
                return MoCreatures.proxy.getModelTexture("wyvern_sea.png");
            default:
                return MoCreatures.proxy.getModelTexture("wyvern_sun.png");
        }
    }

    public void transform(int tType) {
        if (!this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), tType), new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        this.transformType = tType;
        this.transformCounter = 1;
    }

    @Override
    public void onLivingUpdate() {

        if (this.wingFlapCounter > 0 && ++this.wingFlapCounter > 20) {
            this.wingFlapCounter = 0;
        }
        if (this.wingFlapCounter == 5 && !this.world.isRemote) {
            MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_WYVERN_FLAP);
        }

        if (this.transformCounter > 0) {
            if (this.transformCounter == 40) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_MAGIC_CONVERSION);
            }

            if (++this.transformCounter > 100) {
                this.transformCounter = 0;
                if (this.transformType != 0) {
                    setType(this.transformType);
                    selectType();
                }
            }
        }

        if (!this.world.isRemote) {
            if (!isMovementCeased() && !this.getIsTamed() && this.rand.nextInt(300) == 0) {
                setIsFlying(!getIsFlying());
            }

            if (isMovementCeased() && getIsFlying()) {
                setIsFlying(false);
            }

            if (getAttackTarget() != null && (!this.getIsTamed() || this.getRidingEntity() != null) && !isMovementCeased() && this.rand.nextInt(20) == 0) {
                setIsFlying(true);
            }

            if (getIsFlying() && this.getNavigator().noPath() && !isMovementCeased() && this.getAttackTarget() == null && rand.nextInt(30) == 0) {
                this.wander.makeUpdate();
            }

            if (this.motionY > 0.5) // prevent large boundingbox checks
            {
                this.motionY = 0.5;
            }

            if (isOnAir()) {
                float myFlyingSpeed = MoCTools.getMyMovementSpeed(this);
                int wingFlapFreq = (int) (25 - (myFlyingSpeed * 10));
                if (!this.isBeingRidden() || wingFlapFreq < 5) {
                    wingFlapFreq = 5;
                }
                if (this.rand.nextInt(wingFlapFreq) == 0) {
                    wingFlap();
                }
            }

            if (getIsGhost() && getAge() > 0 && getAge() < 10 && this.rand.nextInt(5) == 0) {
                setAge(getAge() + 1);
                if (getAge() == 9) {
                    setAge(140);
                    setAdult(true);
                }
            }

        } else {

            if (this.mouthCounter > 0 && ++this.mouthCounter > 30) {
                this.mouthCounter = 0;
            }

            if (this.diveCounter > 0 && ++this.diveCounter > 5) {
                this.diveCounter = 0;
            }
        }
        super.onLivingUpdate();
    }

    public void wingFlap() {
        if (this.wingFlapCounter == 0) {
            this.wingFlapCounter = 1;
            if (!this.world.isRemote) {
                MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 3), new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
            }
        }
    }

    @Override
    public float getSizeFactor() {
        return getAge() * 0.01F;
    }

    @Override
    public boolean isFlyingAlone() {
        return getIsFlying() && !this.isBeingRidden();
    }

    @Override
    public int maxFlyingHeight() {
        if (getIsTamed()) return 5;
        return 18;
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        final Boolean tameResult = this.processTameInteract(player, hand);
        if (tameResult != null) {
            return tameResult;
        }

        final ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && (stack.getItem() == MoCItems.whip) && getIsTamed() && (!this.isBeingRidden())) {
            setSitting(!getIsSitting());
            setIsJumping(false);
            getNavigator().clearPath();
            setAttackTarget(null);
            return true;
        }

        if (!stack.isEmpty() && !getIsRideable() && getAge() > 90 && this.getIsTamed() && (stack.getItem() instanceof ItemSaddle)) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            setRideable(true);
            return true;
        }

        if (!stack.isEmpty() && this.getIsTamed() && getAge() > 90 && stack.getItem() == Items.IRON_HORSE_ARMOR) {
            if (getArmorType() == 0) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_ON);
            }
            dropArmor();
            setArmorType((byte) 1);
            if (!player.capabilities.isCreativeMode) stack.shrink(1);

            return true;
        }

        if (!stack.isEmpty() && this.getIsTamed() && getAge() > 90 && stack.getItem() == Items.GOLDEN_HORSE_ARMOR) {
            if (getArmorType() == 0) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_ON);
            }
            dropArmor();
            setArmorType((byte) 2);
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            return true;
        }

        if (!stack.isEmpty() && this.getIsTamed() && getAge() > 90 && stack.getItem() == Items.DIAMOND_HORSE_ARMOR) {
            if (getArmorType() == 0) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_ON);
            }
            dropArmor();
            setArmorType((byte) 3);
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            return true;
        }

        if (!stack.isEmpty() && getIsTamed() && getAge() > 90 && !getIsChested() && (stack.getItem() == Item.getItemFromBlock(Blocks.CHEST))) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            setIsChested(true);
            MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
            return true;
        }

        if (getIsChested() && player.isSneaking()) {
            if (this.localchest == null) {
                this.localchest = new MoCAnimalChest("WyvernChest", 9);
            }
            if (!this.world.isRemote) {
                player.displayGUIChest(this.localchest);
            }
            return true;
        }

        if (!stack.isEmpty() && this.getIsGhost() && this.getIsTamed() && stack.getItem() == MoCItems.amuletghost) {

            player.setHeldItem(hand, ItemStack.EMPTY);
            if (!this.world.isRemote) {
                MoCPetData petData = MoCreatures.instance.mapData.getPetData(this.getOwnerId());
                if (petData != null) {
                    petData.setInAmulet(this.getOwnerPetId(), true);
                }
                this.dropMyStuff();
                MoCTools.dropAmulet(this, 3, player);
                this.isDead = true;
            }

            return true;

        }

        if (!stack.isEmpty() && !this.getIsGhost() && (stack.getItem() == MoCItems.essencelight) && getIsTamed() && getAge() > 90 && getType() < 5) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            if (stack.isEmpty()) {
                player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }

            if (!this.world.isRemote) {
                int i = getType() + 49;
                MoCEntityEgg entityegg = new MoCEntityEgg(this.world, i);
                entityegg.setPosition(player.posX, player.posY, player.posZ);
                player.world.spawnEntity(entityegg);
                entityegg.motionY += this.world.rand.nextFloat() * 0.05F;
                entityegg.motionX += (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
                entityegg.motionZ += (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
            }
            return true;
        }

        if (!stack.isEmpty() && this.transformCounter == 0 && !this.getIsGhost() && getType() == 5 && (stack.getItem() == MoCItems.essenceundead) && getIsTamed()) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            if (stack.isEmpty()) {
                player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }

            if (!this.world.isRemote) {
                transform(6);
            }
            return true;
        }

        if (!stack.isEmpty() && this.transformCounter == 0 && !this.getIsGhost() && getType() == 5 && (stack.getItem() == MoCItems.essencelight) && getIsTamed()) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            if (stack.isEmpty()) {
                player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }

            if (!this.world.isRemote) {
                transform(7);
            }
            return true;
        }

        if (!stack.isEmpty() && this.transformCounter == 0 && !this.getIsGhost() && getType() == 5 && (stack.getItem() == MoCItems.essencedarkness) && getIsTamed()) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            if (stack.isEmpty()) {
                player.setHeldItem(hand, new ItemStack(Items.GLASS_BOTTLE));
            } else {
                player.inventory.addItemStackToInventory(new ItemStack(Items.GLASS_BOTTLE));
            }

            if (!this.world.isRemote) {
                transform(8);
            }
            return true;
        }

        if (this.getIsRideable() && getAge() > 90 && (!this.getIsChested() || !player.isSneaking()) && !this.isBeingRidden()) {
            if (!this.world.isRemote && player.startRiding(this)) {
                player.rotationYaw = this.rotationYaw;
                player.rotationPitch = this.rotationPitch;
                setSitting(false);
            }

            return true;
        }

        return super.processInteract(player, hand);
    }

    /**
     * Drops the current armor
     */
    @Override
    public void dropArmor() {
        if (!this.world.isRemote) {
            int i = getArmorType();
            if (i != 0) {
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_ARMOR_OFF);
            }

            if (i == 1) {
                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.IRON_HORSE_ARMOR, 1));
                entityitem.setDefaultPickupDelay();
                this.world.spawnEntity(entityitem);
            }
            if (i == 2) {
                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.GOLDEN_HORSE_ARMOR, 1));
                entityitem.setDefaultPickupDelay();
                this.world.spawnEntity(entityitem);
            }
            if (i == 3) {
                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY, this.posZ, new ItemStack(Items.DIAMOND_HORSE_ARMOR, 1));
                entityitem.setDefaultPickupDelay();
                this.world.spawnEntity(entityitem);
            }
            setArmorType((byte) 0);
        }
    }

    @Override
    public boolean rideableEntity() {
        return true;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return MoCSoundEvents.ENTITY_WYVERN_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        openMouth();
        return MoCSoundEvents.ENTITY_WYVERN_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        openMouth();
        return MoCSoundEvents.ENTITY_WYVERN_AMBIENT;
    }
    
    @Override
    protected void playStepSound(BlockPos pos, Block block) {
        this.playSound(MoCSoundEvents.ENTITY_WYVERN_STEP, 0.4F, (this.rand.nextFloat() - this.rand.nextFloat()) * 0.4F + 1.0F);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        if (!getIsAdult()) {
            return null;
        }

        return MoCLootTables.WYVERN;
    }

    @Override
    public int getTalkInterval() {
        return 400;
    }

    @Override
    public boolean isMovementCeased() {
        return (this.isBeingRidden()) || getIsSitting();
    }

    @Override
    public boolean isFlyer() {
        return true;
    }

    @Override
    public void fall(float f, float f1) {
    }

    @Override
    public double getMountedYOffset() {
        return this.height * 0.85 * getSizeFactor();
    }

    @Override
    public void updatePassenger(Entity passenger) {
        double dist = getSizeFactor() * (0.3D);
        double newPosX = this.posX - (dist * Math.cos((MoCTools.realAngle(this.renderYawOffset - 90F)) / 57.29578F));
        double newPosZ = this.posZ - (dist * Math.sin((MoCTools.realAngle(this.renderYawOffset - 90F)) / 57.29578F));
        passenger.setPosition(newPosX, this.posY + getMountedYOffset() + passenger.getYOffset(), newPosZ);
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (entityIn instanceof EntityPlayer && !shouldAttackPlayers()) {
            return false;
        }
        openMouth();
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    protected void applyEnchantments(EntityLivingBase entityLivingBaseIn, Entity entityIn) {
        if (entityIn instanceof EntityPlayer && this.rand.nextInt(3) == 0) {
            ((EntityLivingBase) entityIn).addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 0));
        }

        super.applyEnchantments(entityLivingBaseIn, entityIn);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        Entity entity = damagesource.getTrueSource();
        if (entity != null && this.isRidingOrBeingRiddenBy(entity)) {
            return false;
        }
        if (super.attackEntityFrom(damagesource, i)) {
            if (entity != null && getIsTamed() && entity instanceof EntityPlayer) {
                return false;
            }

            if ((entity != this) && (super.shouldAttackPlayers())) {
                setAttackTarget((EntityLivingBase) entity);
            }
            return true;
        }
        return false;
    }

    /*@Override
    public boolean entitiesToIgnore(Entity entity) {
        return (super.entitiesToIgnore(entity) || (entity instanceof MoCEntityWyvern) || (entity instanceof EntityPlayer));
    }*/

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound.setBoolean("Saddle", getIsRideable());
        nbttagcompound.setBoolean("Chested", getIsChested());
        nbttagcompound.setInteger("ArmorType", getArmorType());
        nbttagcompound.setBoolean("isSitting", getIsSitting());
        nbttagcompound.setBoolean("isGhost", getIsGhost());
        if (getIsChested() && this.localchest != null) {
            NBTTagList nbttaglist = new NBTTagList();
            for (int i = 0; i < this.localchest.getSizeInventory(); i++) {
                this.localstack = this.localchest.getStackInSlot(i);
                if (!this.localstack.isEmpty()) {
                    NBTTagCompound nbttagcompound1 = new NBTTagCompound();
                    nbttagcompound1.setByte("Slot", (byte) i);
                    this.localstack.writeToNBT(nbttagcompound1);
                    nbttaglist.appendTag(nbttagcompound1);
                }
            }
            nbttagcompound.setTag("Items", nbttaglist);
        }
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        setRideable(nbttagcompound.getBoolean("Saddle"));
        setIsChested(nbttagcompound.getBoolean("Chested"));
        setArmorType(nbttagcompound.getInteger("ArmorType"));
        setSitting(nbttagcompound.getBoolean("isSitting"));
        setIsGhost(nbttagcompound.getBoolean("isGhost"));
        if (getIsChested()) {
            NBTTagList nbttaglist = nbttagcompound.getTagList("Items", 10);
            this.localchest = new MoCAnimalChest("WyvernChest", 14);
            for (int i = 0; i < nbttaglist.tagCount(); i++) {
                NBTTagCompound nbttagcompound1 = nbttaglist.getCompoundTagAt(i);
                int j = nbttagcompound1.getByte("Slot") & 0xff;
                if (j < this.localchest.getSizeInventory()) {
                    this.localchest.setInventorySlotContents(j, new ItemStack(nbttagcompound1));
                }
            }
        }
    }

    @Override
    public int nameYOffset() {
        int yOff = getAge() * -1;
        if (yOff < -120) {
            yOff = -120;
        }
        if (getIsSitting()) yOff += 25;
        return yOff;
    }

    @Override
    public boolean isMyHealFood(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == MoCItems.ratRaw || stack.getItem() == MoCItems.rawTurkey);
    }

    private void openMouth() {
        if (!this.world.isRemote) {
            this.mouthCounter = 1;
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 1), new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }

    }

    @Override
    public void performAnimation(int animationType) {
        if (animationType == 1) //opening mouth
        {
            this.mouthCounter = 1;
        }
        if (animationType == 2) //diving mount
        {
            this.diveCounter = 1;
        }
        if (animationType == 3) {
            this.wingFlapCounter = 1;
        }
        if (animationType > 5 && animationType < 9) //transform 6 - 8
        {
            this.transformType = animationType;
            this.transformCounter = 1;
        }
    }

    @Override
    public void makeEntityDive() {
        if (!this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 2), new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        super.makeEntityDive();
    }

    // TODO: Remove this once wyvern eggs are overhauled
    @Override
    public void dropLegacyEgg() {
        int chance = MoCreatures.proxy.wyvernEggDropChance;
        if (getType() == 5) { //mother wyverns drop eggs more frequently
            chance = MoCreatures.proxy.motherWyvernEggDropChance;
        }
        if (this.rand.nextInt(100) < chance) {
            entityDropItem(new ItemStack(MoCItems.mocegg, 1, getType() + 49), 0.0F);
        }
    }

    @Override
    public boolean canBeCollidedWith() {
        return !this.isBeingRidden();
    }

    @Override
    public void dropMyStuff() {
        if (!this.world.isRemote) {
            dropArmor();
            MoCTools.dropSaddle(this, this.world);

            if (getIsChested()) {
                MoCTools.dropInventory(this, this.localchest);
                MoCTools.dropCustomItem(this, this.world, new ItemStack(Blocks.CHEST, 1));
                setIsChested(false);
            }
        }
    }

    @Override
    public float getAdjustedYOffset() {
        if (getIsSitting()) {
            return 0.4F;
        }
        return 0F;
    }

    @Override
    public double getCustomSpeed() {
        if (this.isBeingRidden()) {
            return 1.0D;
        }
        return 0.8D;
    }

    @Override
    public int getMaxAge() {
        if (this.getType() == 5) {
            return 180;
        }
        if (this.getType() == 6 || this.getType() == 7 || this.getType() == 8) {
            return 160;
        }
        return 120;
    }

    @Override
    public EnumCreatureAttribute getCreatureAttribute() {
        if (getType() == 6 || getIsGhost()) {
            return EnumCreatureAttribute.UNDEAD;
        }
        return super.getCreatureAttribute();
    }

    @Override
    public boolean isReadyToHunt() {
        return !this.isMovementCeased() && !this.isBeingRidden();
    }

    @Override
    public boolean canAttackTarget(EntityLivingBase entity) {
        return !(entity instanceof MoCEntityWyvern) && entity.height <= 1D && entity.width <= 1D;
    }

    @Override
    protected double flyerThrust() {
        return 0.6D;
    }

    @Override
    public float getAIMoveSpeed() {
        if (getIsFlying()) {
            return 0.4F;
        }
        return super.getAIMoveSpeed();
    }

    @Override
    protected float flyerFriction() {
        if (this.getType() == 5) {
            return 0.96F;
        }
        if (this.getType() == 6 || this.getType() == 7 || this.getType() == 8 || this.getIsGhost()) {
            return 0.96F;
        }
        return 0.94F;
    }

    @Override
    public void makeEntityJump() {
        wingFlap();
        super.makeEntityJump();
    }

    @Override
    public boolean shouldAttackPlayers() {
        return !getIsTamed() && super.shouldAttackPlayers();
    }

    @Override
    public void onDeath(DamageSource damagesource) {
        if (!this.world.isRemote) {
            if (this.getType() == 6) {
                MoCTools.spawnMaggots(this.world, this);
            }

            if (!getIsGhost() && getIsTamed() && this.rand.nextInt(4) == 0) {
                MoCEntityWyvern entitywyvern = new MoCEntityWyvern(this.world);
                entitywyvern.setPosition(this.posX, this.posY, this.posZ);
                this.world.spawnEntity(entitywyvern);
                MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_GENERIC_MAGIC_ENCHANTED);

                entitywyvern.setOwnerId(this.getOwnerId());
                entitywyvern.setTamed(true);
                EntityPlayer entityplayer = this.world.getClosestPlayerToEntity(this, 24D);
                if (entityplayer != null) {
                    MoCTools.tameWithName(entityplayer, entitywyvern);
                }

                entitywyvern.setAdult(false);
                entitywyvern.setAge(1);
                entitywyvern.setType(this.getType());
                entitywyvern.selectType();
                entitywyvern.setIsGhost(true);
            }

        }
        super.onDeath(damagesource);

    }

    public float tFloat() {

        if (++this.tCounter > 30) {
            this.tCounter = 0;
            this.fTransparency = (this.rand.nextFloat() * (0.4F - 0.2F) + 0.15F);
        }

        if (this.getAge() < 10) {
            return 0F;
        }
        return fTransparency;
    }

    @Override
    protected boolean canBeTrappedInNet() {
        return this.getIsTamed() && !this.getIsGhost();
    }

    public float getEyeHeight() {
        return this.height * 0.925F;
    }
}
