/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.neutral;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.ai.EntityAIFollowAdult;
import drzhark.mocreatures.entity.ai.EntityAIPanicMoC;
import drzhark.mocreatures.entity.item.MoCEntityKittyBed;
import drzhark.mocreatures.entity.item.MoCEntityLitterBox;
import drzhark.mocreatures.entity.tameable.MoCEntityTameableAnimal;
import drzhark.mocreatures.init.MoCItems;
import drzhark.mocreatures.init.MoCLootTables;
import drzhark.mocreatures.init.MoCSoundEvents;
import drzhark.mocreatures.network.MoCMessageHandler;
import drzhark.mocreatures.network.message.MoCMessageAnimation;
import net.minecraft.block.material.Material;
import net.minecraft.entity.*;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWanderAvoidWater;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;

import javax.annotation.Nullable;
import java.util.List;

public class MoCEntityKitty extends MoCEntityTameableAnimal {

    private static final DataParameter<Boolean> SITTING = EntityDataManager.createKey(MoCEntityKitty.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> HUNGRY = EntityDataManager.createKey(MoCEntityKitty.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> EMO = EntityDataManager.createKey(MoCEntityKitty.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> CLIMBING = EntityDataManager.createKey(MoCEntityKitty.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Integer> KITTY_STATE = EntityDataManager.createKey(MoCEntityKitty.class, DataSerializers.VARINT);
    private static final DataParameter<Integer> TEMPER = EntityDataManager.createKey(MoCEntityKitty.class, DataSerializers.VARINT);
    private final int[] treeCoord = {-1, -1, -1};
    private int kittyTimer;
    private int madTimer;
    private boolean foundTree;
    private boolean isSwinging;
    private boolean onTree;
    private EntityItem itemAttackTarget;

    public MoCEntityKitty(World world) {
        super(world);
        setSize(0.8F, 0.8F);
        setAdult(true);
        setAge(40);
        setKittyState(1);
        this.kittyTimer = 0;
        this.madTimer = this.rand.nextInt(5);
        this.foundTree = false;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIPanicMoC(this, 1.0D));
        this.tasks.addTask(4, new EntityAIFollowAdult(this, 1.0D));
        this.tasks.addTask(5, new EntityAIAttackMelee(this, 1.0D, false));
        this.tasks.addTask(6, new EntityAIWanderAvoidWater(this, 1.0D));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(10.0D);
        this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
        this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(2.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.25D);
    }

    @Override
    public void selectType() {
        if (getType() == 0) {
            setType(this.rand.nextInt(11) + 1);
        }
    }

    @Override
    public ResourceLocation getTexture() {
        switch (getType()) {
            case 2:
                return MoCreatures.proxy.getModelTexture("kitty_gray.png");
            case 3:
                return MoCreatures.proxy.getModelTexture("kitty_black.png");
            case 4:
                return MoCreatures.proxy.getModelTexture("kitty_calico.png");
            case 5:
                return MoCreatures.proxy.getModelTexture("kitty_tuxedo.png");
            case 6:
                return MoCreatures.proxy.getModelTexture("kitty_white_black.png");
            case 7:
                return MoCreatures.proxy.getModelTexture("kitty_white.png");
            case 8:
                return MoCreatures.proxy.getModelTexture("kitty_orange_tabby.png");
            case 9:
                return MoCreatures.proxy.getModelTexture("kitty_cream_dark.png");
            case 10:
                return MoCreatures.proxy.getModelTexture("kitty_gray_tabby.png");
            case 11:
                return MoCreatures.proxy.getModelTexture("kitty_yellow_tabby.png");
            default:
                return MoCreatures.proxy.getModelTexture("kitty_cream.png");
        }
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(SITTING, Boolean.FALSE);
        this.dataManager.register(HUNGRY, Boolean.FALSE);
        this.dataManager.register(EMO, Boolean.FALSE);
        this.dataManager.register(CLIMBING, Boolean.FALSE);
        this.dataManager.register(KITTY_STATE, 0);
        this.dataManager.register(TEMPER, 0);
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
        switch (this.world.rand.nextInt(3)) {
            case 0: // NEUTRAL
                setTemper(0);
                break;
            case 1: // DOCILE
                setTemper(1);
                break;
            case 2: // DEFIANT
                setTemper(2);
                break;
        }
        return super.onInitialSpawn(difficulty, livingdata);
    }

    public int getKittyState() {
        return this.dataManager.get(KITTY_STATE);
    }

    public void setKittyState(int i) {
        this.dataManager.set(KITTY_STATE, i);
    }

    public int getTemper() {
        return this.dataManager.get(TEMPER);
    }

    public void setTemper(int i) {
        this.dataManager.set(TEMPER, i);
    }

    public boolean getIsSitting() {
        return this.dataManager.get(SITTING);
    }

    public boolean getIsHungry() {
        return this.dataManager.get(HUNGRY);
    }

    public boolean getShowEmoteIcon() {
        return this.dataManager.get(EMO);
    }

    public void setShowEmoteIcon(boolean flag) {
        this.dataManager.set(EMO, flag);
    }

    public boolean getIsSwinging() {
        return this.isSwinging;
    }

    public boolean getOnTree() {
        return this.onTree;
    }

    public void setOnTree(boolean var1) {
        this.onTree = var1;
    }

    public void setSitting(boolean flag) {
        this.dataManager.set(SITTING, flag);
    }

    public void setHungry(boolean flag) {
        this.dataManager.set(HUNGRY, flag);
    }

    public void setSwinging(boolean var1) {
        this.isSwinging = var1;
    }

    @Override
    public float getEyeHeight() {
        return this.height * 0.8F;
    }

    @Override
    public boolean attackEntityAsMob(Entity entityIn) {
        if (getKittyState() != 18 && getKittyState() != 10) {
            swingArm();
        }
        if ((getKittyState() == 13 && entityIn instanceof EntityPlayer) || (getKittyState() == 8 && entityIn instanceof EntityItem) || (getKittyState() == 18 && entityIn instanceof MoCEntityKitty) || getKittyState() == 10) {
            return false;
        }
        return super.attackEntityAsMob(entityIn);
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        if (super.attackEntityFrom(damagesource, i)) {
            Entity entity = damagesource.getTrueSource();
            if (entity != this && entity instanceof EntityLivingBase) {
                EntityLivingBase entity1 = (EntityLivingBase) entity;
                if (getKittyState() == 10) {
                    List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(16D, 6D, 16D));
                    for (Entity entity2 : list) {
                        if (entity2 instanceof MoCEntityKitty && ((MoCEntityKitty) entity2).getKittyState() == 21) {
                            ((MoCEntityKitty) entity2).setAttackTarget(entity1);
                            return true;
                        }
                    }
                    return true;
                }
                if (entity1 instanceof EntityPlayer && super.shouldAttackPlayers() && (getTemper() == 2 || (getTemper() == 0 && this.rand.nextInt(2) < 1))) {
                    if (getKittyState() < 2) {
                        setAttackTarget(entity1);
                    } else if (getKittyState() == 19 || getKittyState() == 20 || getKittyState() == 21) {
                        setAttackTarget(entity1);
                        setSitting(false);
                    } else if (getKittyState() > 1 && getKittyState() != 10 && getKittyState() != 19 && getKittyState() != 20 && getKittyState() != 21) {
                        setKittyState(13);
                        setSitting(false);
                    }
                    return true;
                }
                setAttackTarget(entity1);
            }
            return true;
        } else {
            return false;
        }
    }

    public void changeKittyState(int i) {
        setKittyState(i);
        setSitting(false);
        this.kittyTimer = 0;
        setOnTree(false);
        this.foundTree = false;
        setAttackTarget(null);
        this.itemAttackTarget = null;
    }

    public boolean climbingTree() {
        return getKittyState() == 16 && isOnLadder();
    }

    @Override
    public void fall(float f, float f1) {
        // No fall damage
    }

    @Override
    protected Entity findPlayerToAttack() {
        if (this.world.getDifficulty().getId() > 0 && getKittyState() != 8 && getKittyState() != 10 && getKittyState() != 15 && getKittyState() != 18 && getKittyState() != 19 && !isMovementCeased() && getIsHungry()) {
            return getClosestTarget(this, 10D);
        } else {
            return null;
        }
    }

    public ResourceLocation getEmoteIcon() {
        switch (getKittyState()) {
            case -1:
                return MoCreatures.proxy.getMiscTexture("emoticon_blank.png"); // Blank
            case 3:
                return MoCreatures.proxy.getMiscTexture("emoticon_3.png"); // Food
            case 4:
                return MoCreatures.proxy.getMiscTexture("emoticon_4.png"); // Happy
            case 5:
                return MoCreatures.proxy.getMiscTexture("emoticon_5.png"); // Litter Box
            case 7:
                return MoCreatures.proxy.getMiscTexture("emoticon_7.png"); // Very Happy
            case 8:
                return MoCreatures.proxy.getMiscTexture("emoticon_8.png"); // Very, Very Happy
            case 9:
            case 18:
                return MoCreatures.proxy.getMiscTexture("emoticon_9.png"); // In Love
            case 10:
            case 21:
                return MoCreatures.proxy.getMiscTexture("emoticon_10.png"); // Pleased
            case 11:
                return MoCreatures.proxy.getMiscTexture("emoticon_11.png"); // Wondering
            case 12:
                return MoCreatures.proxy.getMiscTexture("emoticon_12.png"); // Sleeping
            case 13:
                return MoCreatures.proxy.getMiscTexture("emoticon_13.png"); // Angry
            case 16:
                return MoCreatures.proxy.getMiscTexture("emoticon_16.png"); // Tree
            case 17:
                return MoCreatures.proxy.getMiscTexture("emoticon_17.png"); // Scared
            case 19:
            case 20:
                return MoCreatures.proxy.getMiscTexture("emoticon_19.png"); // In Labor
            default:
                return MoCreatures.proxy.getMiscTexture("emoticon_1.png"); // Neutral
        }
    }

    @Override
    protected SoundEvent getDeathSound() {
        return getKittyState() == 10 ? MoCSoundEvents.ENTITY_KITTY_DEATH_BABY : MoCSoundEvents.ENTITY_KITTY_DEATH;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return getKittyState() == 10 ? MoCSoundEvents.ENTITY_KITTY_HURT_BABY : MoCSoundEvents.ENTITY_KITTY_HURT;
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return getIsAdult() ? MoCLootTables.KITTY : null;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        switch (getKittyState()) {
            case 3:
                return MoCSoundEvents.ENTITY_KITTY_HUNGRY;
            case 4:
                if (this.getRidingEntity() != null) {
                    MoCEntityKittyBed kittyBed = (MoCEntityKittyBed) this.getRidingEntity();
                    if (kittyBed != null && !kittyBed.getHasMilk()) {
                        return MoCSoundEvents.ENTITY_KITTY_DRINK;
                    }
                    if (kittyBed != null && !kittyBed.getHasFood()) {
                        return MoCSoundEvents.ENTITY_KITTY_EAT;
                    }
                }
                return null;
            case 10:
                return MoCSoundEvents.ENTITY_KITTY_AMBIENT_BABY;
            case 12:
            case 18:
                return MoCSoundEvents.ENTITY_KITTY_PURR;
            case 13:
                return MoCSoundEvents.ENTITY_KITTY_ANGRY;
            case 17:
                return MoCSoundEvents.ENTITY_KITTY_TRAPPED;
            default:
                return MoCSoundEvents.ENTITY_KITTY_AMBIENT;
        }
    }

    public EntityLiving getKittyStuff(Entity entity, double d, boolean flag) {
        double d1 = -1D;
        EntityLiving obj = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(entity, getEntityBoundingBox().grow(d));
        for (Entity entity1 : list) {
            if (flag) {
                if (!(entity1 instanceof MoCEntityLitterBox)) {
                    continue;
                }
                MoCEntityLitterBox entitylitterbox = (MoCEntityLitterBox) entity1;
                if (entitylitterbox.getUsedLitter()) {
                    continue;
                }
                double d2 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
                if (((d < 0.0D) || (d2 < (d * d))) && ((d1 == -1.0D) || (d2 < d1)) && entitylitterbox.canEntityBeSeen(entity)) {
                    d1 = d2;
                    obj = entitylitterbox;
                }
                continue;
            }
            if (!(entity1 instanceof MoCEntityKittyBed)) {
                continue;
            }
            MoCEntityKittyBed kittyBed = (MoCEntityKittyBed) entity1;
            double d3 = entity1.getDistanceSq(entity.posX, entity.posY, entity.posZ);
            if (((d < 0.0D) || (d3 < (d * d))) && ((d1 == -1.0D) || (d3 < d1)) && kittyBed.canEntityBeSeen(entity)) {
                d1 = d3;
                obj = kittyBed;
            }
        }
        return obj;
    }

    @Override
    public double getYOffset() {
        if (this.getRidingEntity() instanceof EntityPlayer && this.world.isRemote) {
            if (getKittyState() == 10) {
                return (super.getYOffset() + 0.4F);
            }
            if (upsideDown()) {
                return (super.getYOffset() - 0.1F);
            }
            if (onMaBack()) {
                return (super.getYOffset() + 0.1F);
            }
        }
        return super.getYOffset();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        // Only process one hand to prevent double interactions
        if (hand != EnumHand.MAIN_HAND) {
            return false;
        }
        final Boolean tameResult = this.processTameInteract(player, hand);
        if (tameResult != null) {
            return tameResult;
        }
        final ItemStack stack = player.getHeldItem(hand);
        if (getKittyState() == 2 && !stack.isEmpty() && stack.getItem() == MoCItems.medallion) {
            if (!this.world.isRemote) {
                MoCTools.tameWithName(player, this);
            }
            if (getIsTamed()) {
                if (!player.capabilities.isCreativeMode) stack.shrink(1);
                changeKittyState(3);
                this.setHealth(getMaxHealth());
                return true;
            }
            return false;
        }
        if (getKittyState() == 7 && !stack.isEmpty() && (stack.getItem() == Items.FISH || stack.getItem() == Items.COOKED_FISH)) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_KITTY_EAT);
            this.setHealth(getMaxHealth());
            changeKittyState(9);
            return true;
        }
        if (getKittyState() == 11 && !stack.isEmpty() && stack.getItem() == MoCItems.woolball) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            setKittyState(8);
            if (!this.world.isRemote) {
                EntityItem entityitem = new EntityItem(this.world, this.posX, this.posY + 1.0D, this.posZ, new ItemStack(MoCItems.woolball, 1));
                entityitem.setPickupDelay(30);
                entityitem.setNoDespawn();
                this.world.spawnEntity(entityitem);
                entityitem.motionY += this.world.rand.nextFloat() * 0.05F;
                entityitem.motionX += (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
                entityitem.motionZ += (this.world.rand.nextFloat() - this.world.rand.nextFloat()) * 0.3F;
                this.itemAttackTarget = entityitem;
            }
            return true;
        }
        if (getKittyState() == 13 && !stack.isEmpty() && (stack.getItem() == Items.FISH || stack.getItem() == Items.COOKED_FISH)) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_KITTY_EAT);
            this.setHealth(getMaxHealth());
            changeKittyState(7);
            return true;
        }
        if (!stack.isEmpty() && getKittyState() > 2 && stack.getItem() == MoCItems.scrollOfRenaming) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            return MoCTools.tameWithName(player, this);
        }
        if (!stack.isEmpty() && getKittyState() > 2 && pickable() && stack.getItem() == Items.LEAD) {
            if (this.startRidingPlayer(player)) {
                changeKittyState(14);
            }
            return true;
        }
        if (!stack.isEmpty() && getKittyState() > 2 && whipable() && stack.getItem() == MoCItems.whip) {
            setSitting(!getIsSitting());
            setIsJumping(false);
            getNavigator().clearPath();
            setAttackTarget(null);
            return true;
        }
        // Can be picked up, then pick it up
        if (getKittyState() > 2 && pickable()) {
            if (this.startRidingPlayer(player)) {
                changeKittyState(15);
            }
            return true;
        }
        // Roped and not on player
        if (getKittyState() == 14 && this.getRidingEntity() != null) {
            changeKittyState(7);
            return true;
        }
        return super.processInteract(player, hand);
    }

    @Override
    public boolean isMovementCeased() {
        return getIsSitting() || getKittyState() == 6 || (getKittyState() == 16 && getOnTree()) || getKittyState() == 12 || getKittyState() == 17 || getKittyState() == 14 || getKittyState() == 20 || getKittyState() == 23;
    }


    // Called when an Entity is dismounted from riding on the Player's head.
    @Override
    public void onStopRidingPlayer() {
        // Stopped riding player, reset state to Idle.
        changeKittyState(7);
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
    public void onLivingUpdate() {
        if (!this.world.isRemote) {
            if (!getIsAdult() && getKittyState() != 10) {
                setKittyState(10);
            }
            if (getKittyState() != 12) {
                super.onLivingUpdate();
            }
            if (this.rand.nextInt(200) < 1) {
                setShowEmoteIcon(!getShowEmoteIcon());
            }
            if (!getIsAdult() && this.rand.nextInt(200) < 1) {
                setAge(getAge() + 1);
                if (getAge() >= 100) {
                    setAdult(true);
                }
            }
            if (!getIsHungry() && !getIsSitting() && this.rand.nextInt(100) < 1) {
                setHungry(true);
            }
            switch (getKittyState()) {
                case -1:
                case 23:
                    break;
                case 0:
                    changeKittyState(1);
                    break;
                case 1: // Untamed
                    if (this.rand.nextInt(20) < 1) {
                        changeKittyState(2);
                        break;
                    }
                    if (!getIsHungry() || this.rand.nextInt(10) != 0) {
                        break;
                    }
                    EntityItem entityItem = getClosestItem(this, 10D, Items.COOKED_FISH, Items.COOKED_FISH);
                    if (entityItem != null) {
                        float f = entityItem.getDistance(this);
                        if (f > 2.0F) {
                            setPathToEntity(entityItem, f);
                        }
                        if (f < 2.0F && this.deathTime < 1) {
                            entityItem.setDead();
                            MoCTools.playCustomSound(this, MoCSoundEvents.ENTITY_KITTY_EAT);
                            setHungry(false);
                        }
                    }
                    break;
                case 2: // Scared
                    EntityLivingBase living1 = getBoogey(6D);
                    if (living1 != null) {
                        MoCTools.runLikeHell(this, living1);
                    }
                    if (this.rand.nextInt(200) < 1) {
                        changeKittyState(1);
                        break;
                    }
                    break;
                case 3: // Looking for kitty bed to rest
                    this.kittyTimer++;
                    if (this.kittyTimer > 500) {
                        if (this.rand.nextInt(200) < 1) {
                            changeKittyState(13);
                            break;
                        }
                        if (this.rand.nextInt(500) < 1) {
                            changeKittyState(7);
                            break;
                        }
                    }
                    if (this.rand.nextInt(20) != 0) {
                        break;
                    }
                    MoCEntityKittyBed kittyBed = (MoCEntityKittyBed) getKittyStuff(this, 18D, false);
                    if (kittyBed == null || kittyBed.isBeingRidden() || (!kittyBed.getHasMilk() && !kittyBed.getHasFood())) {
                        break;
                    }
                    float f5 = kittyBed.getDistance(this);
                    if (f5 > 2.0F) {
                        setPathToEntity(kittyBed, f5);
                    }
                    if (f5 < 2.0F && this.startRiding(kittyBed)) {
                        changeKittyState(4);
                        setSitting(true);
                    }
                    break;
                case 4: // Sitting in kitty bed
                    if (this.getRidingEntity() != null) {
                        MoCEntityKittyBed kittyBed1 = (MoCEntityKittyBed) this.getRidingEntity();
                        if (kittyBed1 != null && !kittyBed1.getHasMilk() && !kittyBed1.getHasFood()) {
                            this.setHealth(getMaxHealth());
                            this.dismountRidingEntity();
                            changeKittyState(5);
                        }
                    } else {
                        this.setHealth(getMaxHealth());
                        this.dismountRidingEntity();
                        changeKittyState(5);
                    }
                    if (this.rand.nextInt(2500) < 1) {
                        this.setHealth(getMaxHealth());
                        this.dismountRidingEntity();
                        changeKittyState(7);
                    }
                    break;
                case 5: // Looking for litter box
                    this.kittyTimer++;
                    if ((this.kittyTimer > 2000) && (this.rand.nextInt(1000) < 1)) {
                        changeKittyState(13);
                        break;
                    }
                    if (this.rand.nextInt(20) != 0) {
                        break;
                    }
                    MoCEntityLitterBox litterBox = (MoCEntityLitterBox) getKittyStuff(this, 18D, true);
                    if (litterBox == null || litterBox.isBeingRidden() || litterBox.getUsedLitter()) {
                        break;
                    }
                    float f6 = litterBox.getDistance(this);
                    if (f6 > 2.0F) {
                        setPathToEntity(litterBox, f6);
                    }
                    if (f6 < 2.0F && this.startRiding(litterBox)) {
                        changeKittyState(6);
                    }
                    break;
                case 6: // Doing business in litter box
                    this.kittyTimer++;
                    if (this.kittyTimer <= 300) {
                        if (this.rand.nextInt(40) < 1) {
                            MoCTools.playCustomSound(this, SoundEvents.BLOCK_SAND_BREAK);
                        }
                        break;
                    }
                    // TODO: Custom sound
                    MoCTools.playCustomSound(this, SoundEvents.BLOCK_SLIME_PLACE);
                    MoCEntityLitterBox litterBox1 = (MoCEntityLitterBox) this.getRidingEntity();
                    if (litterBox1 != null) {
                        litterBox1.setUsedLitter(true);
                        litterBox1.litterTime = 0;
                    }
                    this.dismountRidingEntity();
                    changeKittyState(7);
                    break;
                case 7: // Idling
                    if (getIsSitting()) {
                        break;
                    }
                    if (this.rand.nextInt(20) < 1) {
                        EntityPlayer player = this.world.getClosestPlayerToEntity(this, 12D);
                        if (player != null) {
                            ItemStack stack = player.inventory.getCurrentItem();
                            if (!stack.isEmpty() && stack.getItem() == MoCItems.woolball) {
                                changeKittyState(11);
                                break;
                            }
                        }
                    }
                    // When wet
                    if (this.isWet() && this.rand.nextInt(500) < 1) {
                        changeKittyState(13);
                        break;
                    }
                    // When nighttime
                    if (!this.world.isDaytime() && this.rand.nextInt(500) < 1) {
                        MoCEntityKittyBed kittyBed1 = (MoCEntityKittyBed) getKittyStuff(this, 18D, false);
                        if (kittyBed1 == null || kittyBed1.isBeingRidden()) {
                            changeKittyState(12);
                        } else {
                            float f9 = kittyBed1.getDistance(this);
                            if (f9 > 2.0F) {
                                setPathToEntity(kittyBed1, f9);
                            } else if (this.startRiding(kittyBed1)) {
                                changeKittyState(12);
                            }
                        }
                        break;
                    }
                    // When injured or random
                    if (getHealth() < getMaxHealth() || this.rand.nextInt(3000) < 1) {
                        changeKittyState(3);
                        break;
                    }
                    // When outside
                    if (this.world.canSeeSky(getPosition()) && this.rand.nextInt(4000) < 1) {
                        changeKittyState(16);
                    }
                    break;
                case 8: // Playing with wool ball
                    if (this.rand.nextInt(getTemper() == 2 ? 300 : 200) < 1) {
                        if (this.isWet()) {
                            changeKittyState(13);
                        } else {
                            changeKittyState(7);
                        }
                        break;
                    }
                    if (this.itemAttackTarget != null) {
                        float f1 = getDistance(itemAttackTarget);
                        if (f1 < 1.5F) {
                            swingArm();
                            if (this.rand.nextInt(10) < 1) {
                                float force = 0.2F;
                                if (getTemper() == 2) force = 0.3F;
                                if (getType() == 10) force = 0.1F;
                                MoCTools.bigSmack(this, itemAttackTarget, force);
                            }
                        } else {
                            setPathToEntity(itemAttackTarget, f1);
                        }
                    }
                    break;
                case 9: // Looking for mate
                    this.kittyTimer++;
                    if (this.rand.nextInt(50) < 1) {
                        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(16D, 6D, 16D));
                        int j = 0;
                        do {
                            if (j >= list.size()) {
                                break;
                            }
                            Entity entity = list.get(j);
                            if (entity instanceof MoCEntityKitty && ((MoCEntityKitty) entity).getKittyState() == 9) {
                                changeKittyState(18);
                                setAttackTarget((EntityLivingBase) entity);
                                ((MoCEntityKitty) entity).changeKittyState(18);
                                ((MoCEntityKitty) entity).setAttackTarget(this);
                                break;
                            }
                            j++;
                        } while (true);
                    }
                    if (this.kittyTimer > 2000) {
                        changeKittyState(7);
                    }
                    break;
                case 10: // Baby state
                    if (getIsAdult()) {
                        changeKittyState(7);
                        break;
                    }
                    if (this.rand.nextInt(50) < 1) {
                        List<Entity> list1 = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(16D, 6D, 16D));
                        for (Entity entity1 : list1) {
                            if (!(entity1 instanceof MoCEntityKitty) || ((MoCEntityKitty) entity1).getKittyState() != 21) {
                                continue;
                            }
                            float f9 = getDistance(entity1);
                            if (f9 > 12F) {
                                setAttackTarget((EntityLivingBase) entity1);
                            }
                        }
                    }
                    if ((this.itemAttackTarget == null || getAttackTarget() == null) && this.rand.nextInt(100) < 1) {
                        int i = this.rand.nextInt(10);
                        if (i < 7) {
                            this.itemAttackTarget = getClosestItem(this, 10D, null, null);
                        } else {
                            this.setAttackTarget(this.world.getClosestPlayerToEntity(this, 18D));
                        }
                    }
                    if ((this.getAttackTarget() != null || this.itemAttackTarget != null) && this.rand.nextInt(400) < 1) {
                        setAttackTarget(null);
                        this.itemAttackTarget = null;
                    }
                    if (this.itemAttackTarget != null) {
                        float f2 = getDistance(this.itemAttackTarget);
                        if (f2 < 1.5F) {
                            swingArm();
                            if (this.rand.nextInt(10) < 1) {
                                MoCTools.bigSmack(this, this.itemAttackTarget, 0.2F);
                            }
                        }
                    }
                    if (getAttackTarget() instanceof MoCEntityKitty && this.rand.nextInt(20) < 1) {
                        float f3 = getDistance(getAttackTarget());
                        if (f3 < 2.0F) {
                            swingArm();
                            this.getNavigator().clearPath();
                        }
                    }
                    if (!(getAttackTarget() instanceof EntityPlayer)) {
                        break;
                    }
                    float f4 = getDistance(getAttackTarget());
                    if ((f4 < 2.0F) && (this.rand.nextInt(20) < 1)) {
                        swingArm();
                    }
                    break;
                case 11: // Looking for player holding wool ball
                    EntityPlayer player1 = this.world.getClosestPlayerToEntity(this, 18D);
                    if (player1 == null || this.rand.nextInt(10) != 0) {
                        break;
                    }
                    ItemStack stack1 = player1.inventory.getCurrentItem();
                    if (stack1.getItem() != MoCItems.woolball) {
                        changeKittyState(7);
                        break;
                    }
                    float f8 = player1.getDistance(this);
                    if (f8 > 5F) {
                        getPathOrWalkableBlock(player1, f8);
                    }
                    break;
                case 12: // Lying down to sleep at night
                    this.kittyTimer++;
                    if (this.world.isDaytime() || (this.kittyTimer > 500 && this.rand.nextInt(500) < 1)) {
                        this.dismountRidingEntity();
                        changeKittyState(7);
                        break;
                    }
                    setSitting(true);
                    if (this.rand.nextInt(100) < 1 || !this.onGround) {
                        super.onLivingUpdate();
                    }
                    break;
                case 13: // Aggressive behavior
                    if (getTemper() == 1 || (getTemper() == 0 && this.rand.nextInt(2) < 1)) {
                        changeKittyState(7);
                    }
                    setHungry(false);
                    setAttackTarget(this.world.getClosestPlayerToEntity(this, 18D));
                    if (getAttackTarget() != null) {
                        float f7 = getDistance(getAttackTarget());
                        if (f7 < 1.5F) {
                            swingArm();
                            if (this.rand.nextInt(20) < 1) {
                                this.madTimer--;
                                getAttackTarget().attackEntityFrom(DamageSource.causeMobDamage(this), 1);
                                if (this.madTimer < 1) {
                                    changeKittyState(7);
                                    this.madTimer = this.rand.nextInt(5);
                                }
                            }
                        }
                        if (this.rand.nextInt(500) < 1) {
                            changeKittyState(7);
                        }
                    } else {
                        changeKittyState(7);
                    }
                    break;
                case 14: // Held by rope
                    if (this.onGround) {
                        changeKittyState(13);
                        break;
                    }
                    if (this.rand.nextInt(50) < 1) {
                        swingArm();
                    }
                    if (this.getRidingEntity() == null) {
                        break;
                    }
                    this.rotationYaw = this.getRidingEntity().rotationYaw + 90F;
                    EntityPlayer player2 = (EntityPlayer) this.getRidingEntity();
                    if (player2 == null) {
                        changeKittyState(13);
                        break;
                    }
                    ItemStack stack2 = player2.inventory.getCurrentItem();
                    if (stack2.getItem() != Items.LEAD) {
                        changeKittyState(13);
                    }
                    break;
                case 15: // Picked up by player
                    if (this.onGround) {
                        changeKittyState(7);
                    }
                    if (this.getRidingEntity() != null) {
                        this.rotationYaw = this.getRidingEntity().rotationYaw + 90F;
                    }
                    break;
                case 16: // Looking for nearby tree
                    kittyTimer++;
                    if (kittyTimer > 500) {
                        if (!getOnTree()) {
                            changeKittyState(7);
                        } else {
                            setKittyState(17);
                        }
                    }
                    if (!getOnTree()) {
                        if (!foundTree && rand.nextInt(50) < 1) {
                            BlockPos treeTop = MoCTools.getTreeTop(world, this, 18);
                            if (treeTop != null) {
                                treeCoord[0] = treeTop.getX();
                                treeCoord[1] = treeTop.getY();
                                treeCoord[2] = treeTop.getZ();
                                foundTree = true;
                            }
                        }
                        Path pathEntity = navigator.getPathToXYZ(treeCoord[0], treeCoord[1], treeCoord[2]);
                        if (pathEntity != null) {
                            navigator.setPath(pathEntity, 1.5D);
                        }
                        double dX = treeCoord[0] + 0.5D - posX;
                        double dY = treeCoord[1] + 0.5D - (posY + getEyeHeight());
                        double dZ = treeCoord[2] + 0.5D - posZ;
                        double distance = Math.sqrt(dX * dX + dY * dY + dZ * dZ);
                        // Climbing the tree
                        motionX = (dX / distance) * 0.05D;
                        motionZ = (dZ / distance) * 0.05D;
                        // Climbing through leaves
                        if (!onGround && collidedHorizontally) {
                            // Disable collision checks while climbing through leaves
                            noClip = world.getBlockState(getPosition()).getMaterial() == Material.LEAVES || world.getBlockState(getPosition().up()).getMaterial() == Material.LEAVES;
                        }
                        // Reached the top of the tree
                        else if (!world.getBlockState(getPosition()).causesSuffocation()) {
                            // Re-enable collision checks after climbing through leaves
                            noClip = false;
                            pushOutOfBlocks(this.posX, (getEntityBoundingBox().minY + getEntityBoundingBox().maxY) / 2.0D, this.posZ);
                            // Check if the block below is leaves
                            BlockPos posBelow = getPosition().down();
                            if (world.getBlockState(posBelow).getMaterial() == Material.LEAVES) {
                                setOnTree(true);
                            }
                        }
                    }
                    break;
                case 17: // Stuck on tree, looking for player nearby
                    if (getTemper() == 2 || (getTemper() == 0 && this.rand.nextInt(2) < 1)) {
                        changeKittyState(7);
                    }
                    EntityPlayer player3 = this.world.getClosestPlayerToEntity(this, 2D);
                    if (player3 != null) {
                        changeKittyState(7);
                    }
                    break;
                case 18: // Mating with another kitty
                    if (!(getAttackTarget() instanceof MoCEntityKitty)) {
                        changeKittyState(9);
                        break;
                    }
                    MoCEntityKitty kitty = (MoCEntityKitty) getAttackTarget();
                    if (kitty != null && kitty.getKittyState() == 18) {
                        if (this.rand.nextInt(50) < 1) {
                            swingArm();
                        }
                        float f10 = getDistance(kitty);
                        if (f10 < 5F) {
                            this.kittyTimer++;
                        }
                        if (this.kittyTimer > 500 && this.rand.nextInt(50) < 1) {
                            ((MoCEntityKitty) getAttackTarget()).changeKittyState(7);
                            changeKittyState(19);
                        }
                    } else {
                        changeKittyState(9);
                    }
                    break;
                case 19: // Looking for kitty bed to give birth
                    if (this.rand.nextInt(20) != 0) {
                        break;
                    }
                    MoCEntityKittyBed kittyBed2 = (MoCEntityKittyBed) getKittyStuff(this, 18D, false);
                    if (kittyBed2 == null || kittyBed2.isBeingRidden()) {
                        break;
                    }
                    float f11 = kittyBed2.getDistance(this);
                    if (f11 > 2.0F) {
                        setPathToEntity(kittyBed2, f11);
                    }
                    if (f11 < 2.0F && this.startRiding(kittyBed2)) {
                        changeKittyState(20);
                    }
                    break;
                case 20: // Giving birth in kitty bed
                    if (this.getRidingEntity() == null) {
                        changeKittyState(19);
                        break;
                    }
                    this.rotationYaw = 180F;
                    this.kittyTimer++;
                    if (this.kittyTimer <= 1000) {
                        break;
                    }
                    int i2 = this.rand.nextInt(3) + 1;
                    for (int l2 = 0; l2 < i2; l2++) {
                        MoCEntityKitty kitty1 = new MoCEntityKitty(this.world);
                        int babyType = this.getType();
                        if (this.rand.nextInt(2) < 1) {
                            babyType = (this.rand.nextInt(8) + 1);
                        }
                        kitty1.setType(babyType);
                        kitty1.setPosition(this.posX, this.posY, this.posZ);
                        this.world.spawnEntity(kitty1);
                        MoCTools.playCustomSound(this, SoundEvents.ENTITY_CHICKEN_EGG);
                        kitty1.setAdult(false);
                        kitty1.changeKittyState(10);
                    }
                    this.dismountRidingEntity();
                    changeKittyState(21);
                    break;
                case 21: // Defending kittens
                    this.kittyTimer++;
                    if (this.kittyTimer > 2000) {
                        List<Entity> list2 = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(24D, 8D, 24D));
                        int i3 = 0;
                        for (Entity entity2 : list2) {
                            if (entity2 instanceof MoCEntityKitty && ((MoCEntityKitty) entity2).getKittyState() == 10) {
                                i3++;
                            }
                        }
                        if (i3 < 1) {
                            changeKittyState(7);
                            break;
                        }
                        this.kittyTimer = 1000;
                    }
                    if (getAttackTarget() instanceof EntityPlayer && this.rand.nextInt(300) < 1) {
                        setAttackTarget(null);
                    }
                    break;
                default:
                    changeKittyState(7);
                    break;
            }
        } else {
            super.onLivingUpdate();
        }
        // Dismount player on both sides to prevent desyncs
        if (this.isRiding()) MoCTools.dismountPassengerFromEntity(this, this.getRidingEntity(), false);
    }

    public boolean onMaBack() {
        return getKittyState() == 15;
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (getIsSwinging()) {
            this.swingProgress += 0.2F;
            if (this.swingProgress > 2.0F) {
                setSwinging(false);
                this.swingProgress = 0.0F;
            }
        }
        if (!this.world.isRemote && getKittyState() == 16) {
            this.setBesideClimbableBlock(this.collidedHorizontally);
        }
    }

    private boolean pickable() {
        return getKittyState() != 13 && getKittyState() != 14 && getKittyState() != 15 && getKittyState() != 19 && getKittyState() != 20 && getKittyState() != 21;
    }

    @Override
    public boolean renderName() {
        return getKittyState() != 14 && getKittyState() != 15 && getKittyState() > 1 && super.renderName();
    }

    @Override
    public void setDead() {
        // Server check required to prevent tamed entities from being duplicated on client-side
        if (!this.world.isRemote && (getKittyState() > 2) && (getHealth() > 0)) {
            return;
        }
        super.setDead();
    }

    public void swingArm() {
        // To synchronize, uses the packet handler to invoke the same method in the clients
        if (!this.world.isRemote) {
            MoCMessageHandler.INSTANCE.sendToAllAround(new MoCMessageAnimation(this.getEntityId(), 0), new TargetPoint(this.world.provider.getDimensionType().getId(), this.posX, this.posY, this.posZ, 64));
        }
        if (!getIsSwinging()) {
            setSwinging(true);
            this.swingProgress = 0.0F;
        }
    }

    @Override
    public void performAnimation(int i) {
        swingArm();
    }

    public boolean upsideDown() {
        return getKittyState() == 14;
    }

    public boolean whipable() {
        return getKittyState() != 13;
    }

    @Override
    public boolean getCanSpawnHere() {
        if (MoCreatures.proxy.kittyVillageChance <= 0) return super.getCanSpawnHere();
        BlockPos villagePos = world.findNearestStructure("Village", getPosition(), true);
        if (villagePos != null) {
            double villageDist = villagePos.getDistance(getPosition().getX(), getPosition().getY(), getPosition().getZ());
            if (villageDist < 128) return super.getCanSpawnHere();
        }
        return false;
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        super.readEntityFromNBT(compound);
        setSitting(compound.getBoolean("Sitting"));
        setKittyState(compound.getInteger("KittyState"));
        setTemper(compound.getInteger("Temper"));
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        super.writeEntityToNBT(compound);
        compound.setBoolean("Sitting", getIsSitting());
        compound.setInteger("KittyState", getKittyState());
        compound.setInteger("Temper", getTemper());
    }

    @Override
    public void dropMyStuff() {
        if (!this.world.isRemote && getIsTamed()) {
            MoCTools.dropCustomItem(this, this.world, new ItemStack(MoCItems.medallion, 1));
        }
    }

    @Override
    public void onDeath(DamageSource damagesource) {
        dropMyStuff();
        super.onDeath(damagesource);
    }

    @Override
    public boolean swimmerEntity() {
        return true;
    }

    @Override
    public int nameYOffset() {
        if (this.getIsSitting()) return -30;
        return -40;
    }
}
