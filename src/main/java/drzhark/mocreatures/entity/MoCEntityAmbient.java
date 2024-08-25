/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.tameable.IMoCTameable;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.*;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.pathfinding.Path;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;
import java.util.UUID;

public abstract class MoCEntityAmbient extends EntityCreature implements IMoCEntity {

    protected static final DataParameter<Boolean> ADULT = EntityDataManager.createKey(MoCEntityAmbient.class, DataSerializers.BOOLEAN);
    protected static final DataParameter<Integer> TYPE = EntityDataManager.createKey(MoCEntityAmbient.class, DataSerializers.VARINT);
    protected static final DataParameter<Integer> AGE = EntityDataManager.createKey(MoCEntityAmbient.class, DataSerializers.VARINT);
    protected static final DataParameter<String> NAME_STR = EntityDataManager.createKey(MoCEntityAmbient.class, DataSerializers.STRING);

    protected String texture;
    protected boolean riderIsDisconnecting;

    protected MoCEntityAmbient(World world) {
        super(world);
    }

    @SideOnly(Side.CLIENT)
    @Override
    public String getName() {
        String entityString = EntityList.getEntityString(this);
        if (!MoCreatures.proxy.verboseEntityNames || entityString == null) return super.getName();
        String translationKey = "entity." + entityString + ".verbose.name";
        String translatedString = I18n.format(translationKey);
        return !translatedString.equals(translationKey) ? translatedString : super.getName();
    }

    @Override
    public ResourceLocation getTexture() {
        return MoCreatures.proxy.getModelTexture(this.texture);
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
    }

    @Override
    public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData par1EntityLivingData) {
        selectType();
        return super.onInitialSpawn(difficulty, par1EntityLivingData);
    }

    /**
     * Put your code to choose a texture / the mob type in here. Will be called
     * by default MocEntity constructors.
     */
    @Override
    public void selectType() {
        setType(1);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(ADULT, false);
        this.dataManager.register(TYPE, 0);
        this.dataManager.register(AGE, 45);
        this.dataManager.register(NAME_STR, "");
    }

    @Override
    public int getType() {
        return this.dataManager.get(TYPE);
    }

    @Override
    public void setType(int i) {
        this.dataManager.set(TYPE, i);
    }

    @Override
    public boolean renderName() {
        return MoCreatures.proxy.getDisplayPetName() && (getPetName() != null && !getPetName().isEmpty() && (!this.isBeingRidden()) && (this.getRidingEntity() == null));
    }

    @Override
    public boolean shouldRenderNameAndHealth() {
        return getIsTamed() && (!this.isBeingRidden()) && (this.getRidingEntity() == null);
    }

    @Override
    public boolean getIsAdult() {
        return this.dataManager.get(ADULT);
    }

    @Override
    public void setAdult(boolean flag) {
        this.dataManager.set(ADULT, flag);
    }

    @Override
    public String getPetName() {
        return this.dataManager.get(NAME_STR);
    }

    @Override
    public void setPetName(String name) {
        this.dataManager.set(NAME_STR, name);
    }

    @Override
    public int getAge() {
        return this.dataManager.get(AGE);
    }

    @Override
    public void setAge(int i) {
        this.dataManager.set(AGE, i);
    }

    @Override
    public boolean getIsTamed() {
        return false;
    }

    @Override
    public int getOwnerPetId() {
        return 0;
    }

    @Override
    public void setOwnerPetId(int petId) {
    }

    @Override
    public UUID getOwnerId() {
        return null;
    }

    /**
     * called in getCanSpawnHere to make sure the right type of creature spawns
     * in the right biome i.e. snakes, rays, bears, BigCats and later wolves,
     * etc.
     */
    @Override
    public boolean checkSpawningBiome() {
        return true;
    }

    @Override
    public void onLivingUpdate() {
        if (!this.world.isRemote) {
            if (isMovementCeased()) {
                this.getNavigator().clearPath();
            }
            this.getNavigator().onUpdateNavigation();
        }
        super.onLivingUpdate();
    }

    public boolean swimmerEntity() {
        return false;
    }

    //used to drop armor, inventory, saddles, etc.
    public void dropMyStuff() {
    }

    /**
     * Used to heal the animal
     */
    protected boolean isMyHealFood(ItemStack itemstack) {
        return false;
    }

    @Override
    public boolean isInWater() {
        if (swimmerEntity()) {
            return false;
        }
        return super.isInWater();
    }

    @Override
    public boolean canBreatheUnderwater() {
        return swimmerEntity();
    }

    public void faceLocation(int i, int j, int k, float f) {
        double var4 = i + 0.5D - this.posX;
        double var8 = k + 0.5D - this.posZ;
        double var6 = j + 0.5D - this.posY;
        double var14 = MathHelper.sqrt(var4 * var4 + var8 * var8);
        float var12 = (float) (Math.atan2(var8, var4) * 180.0D / Math.PI) - 90.0F;
        float var13 = (float) (-(Math.atan2(var6, var14) * 180.0D / Math.PI));
        this.rotationPitch = -this.updateRotation(this.rotationPitch, var13, f);
        this.rotationYaw = this.updateRotation(this.rotationYaw, var12, f);
    }

    /**
     * Arguments: current rotation, intended rotation, max increment.
     */
    private float updateRotation(float par1, float par2, float par3) {
        float var4;

        for (var4 = par2 - par1; var4 < -180.0F; var4 += 360.0F) {
        }

        while (var4 >= 180.0F) {
            var4 -= 360.0F;
        }

        if (var4 > par3) {
            var4 = par3;
        }

        if (var4 < -par3) {
            var4 = -par3;
        }

        return par1 + var4;
    }

    public void getMyOwnPath(Entity entity, float f) {
        Path pathentity = this.getNavigator().getPathToEntityLiving(entity);
        if (pathentity != null) {
            this.getNavigator().setPath(pathentity, 1D);
        }
    }

    @Override
    public boolean getCanSpawnHere() {
        boolean willSpawn;
        boolean debug = MoCreatures.proxy.debug;
        willSpawn = this.world.canSeeSky(new BlockPos(this)) && this.world.checkNoEntityCollision(this.getEntityBoundingBox()) && this.world.getCollisionBoxes(this, this.getEntityBoundingBox()).isEmpty() && !this.world.containsAnyLiquid(this.getEntityBoundingBox());
        if (willSpawn && debug)
            MoCreatures.LOGGER.info("Ambient: " + this.getName() + " at: " + this.getPosition() + " State: " + this.world.getBlockState(this.getPosition()) + " biome: " + MoCTools.biomeName(world, getPosition()));
        return willSpawn;
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
        super.writeEntityToNBT(nbttagcompound);
        nbttagcompound = MoCTools.getEntityData(this);
        nbttagcompound.setBoolean("Adult", getIsAdult());
        nbttagcompound.setInteger("Edad", getAge());
        nbttagcompound.setString("Name", getPetName());
        nbttagcompound.setInteger("TypeInt", getType());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
        super.readEntityFromNBT(nbttagcompound);
        nbttagcompound = MoCTools.getEntityData(this);
        setAdult(nbttagcompound.getBoolean("Adult"));
        setAge(nbttagcompound.getInteger("Edad"));
        setPetName(nbttagcompound.getString("Name"));
        setType(nbttagcompound.getInteger("TypeInt"));
    }

    /**
     * Sets a flag that will make the Entity "jump" in the next onGround
     * moveEntity update
     */
    @Override
    public void makeEntityJump() {
    }

    /**
     * Boolean used for flying mounts
     */
    public boolean isFlyer() {
        return false;
    }

    @Override
    public void makeEntityDive() {
    }

    @Override
    public int nameYOffset() {
        return -80;
    }

    /**
     * Used to synchronize animations between server and client
     */
    @Override
    public void performAnimation(int attackType) {
    }

    /**
     * Used to follow the player carrying the item
     */
    @SuppressWarnings("unused")
    public boolean isMyFavoriteFood(ItemStack par1ItemStack) {
        return false;
    }

    public boolean isOnAir() {
        return (this.world.isAirBlock(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 0.2D), MathHelper.floor(this.posZ))) && this.world.isAirBlock(new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(this.posY - 1.2D), MathHelper.floor(this.posZ))));
    }

    @Override
    public float getSizeFactor() {
        return 1.0F;
    }

    @Override
    public float getAdjustedYOffset() {
        return 0F;
    }

    public boolean getIsRideable() {
        return false;
    }

    public void setRideable(boolean b) {
    }

    @Override
    public void setArmorType(int i) {
    }

    /**
     * Finds and entity described in entitiesToInclude at d distance
     */
    protected EntityLivingBase getBoogey(double d) {
        EntityLivingBase entityliving = null;
        List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(d, 4D, d));
        for (Entity entity : list) {
            if (entitiesToInclude(entity)) {
                entityliving = (EntityLivingBase) entity;
            }
        }
        return entityliving;
    }

    /**
     * Used in getBoogey to specify what kind of entity to look for
     */
    public boolean entitiesToInclude(Entity entity) {
        return ((entity instanceof EntityLivingBase) && ((entity.width >= 0.5D) || (entity.height >= 0.5D)));
    }

    @Override
    public float pitchRotationOffset() {
        return 0F;
    }

    @Override
    public float rollRotationOffset() {
        return 0F;
    }

    @Override
    public float yawRotationOffset() {
        return 0F;
    }

    @Override
    public float getAdjustedZOffset() {
        return 0F;
    }

    @Override
    public float getAdjustedXOffset() {
        return 0F;
    }

    protected boolean canBeTrappedInNet() {
        return (this instanceof IMoCTameable) && getIsTamed();
    }

    /**
     * Returns true if the entity is of the @link{EnumCreatureType} provided
     *
     * @param type          The EnumCreatureType type this entity is evaluating
     * @param forSpawnCount If this is being invoked to check spawn count caps.
     * @return If the creature is of the type provided
     */
    @Override
    public boolean isCreatureType(EnumCreatureType type, boolean forSpawnCount) {
        return type == EnumCreatureType.AMBIENT;
    }

    @Override
    public boolean canAttackTarget(EntityLivingBase entity) {
        return false;
    }

    @Override
    public boolean canBeLeashedTo(EntityPlayer player) {
        if (!this.world.isRemote && !MoCTools.isThisPlayerAnOP(player) && this.getIsTamed() && !player.getUniqueID().equals(this.getOwnerId())) {
            return false;
        }
        return super.canBeLeashedTo(player);
    }

    @Override
    public boolean getIsSitting() {
        return false;
    }

    @Override
    public boolean isNotScared() {
        return false;
    }

    @Override
    public boolean isMovementCeased() {
        return getIsSitting();
    }

    @Override
    public boolean shouldAttackPlayers() {
        return this.world.getDifficulty() != EnumDifficulty.PEACEFUL;
    }

    @Override
    protected boolean canTriggerWalking() {
        return false;
    }

    /**
     * The distance the entity will float under the surface. 0F = surface 1.0F = 1 block under
     */
    @Override
    public double getDivingDepth() {
        return 0.5D;
    }

    @Override
    public boolean isDiving() {
        return false;
    }

    @Override
    public void forceEntityJump() {
        this.jump();
    }

    @Override
    public void fall(float f, float f1) {
    }

    @Override
    public int minFlyingHeight() {
        return 2;
    }

    /**
     * Maximum flyer height when moving autonomously
     */
    public int maxFlyingHeight() {
        return 4;
    }

    @Override
    public void travel(float strafe, float vertical, float forward) {
        if (!getIsFlying()) {
            super.travel(strafe, vertical, forward);
            return;
        }
        this.moveEntityWithHeadingFlying(strafe, vertical, forward);
    }

    public void moveEntityWithHeadingFlying(float strafe, float vertical, float forward) {
        if (this.isServerWorld()) {

            this.moveRelative(strafe, vertical, forward, 0.1F);
            this.move(MoverType.SELF, this.motionX, this.motionY, this.motionZ);
            this.motionX *= 0.8999999761581421D;
            this.motionY *= 0.8999999761581421D;
            this.motionZ *= 0.8999999761581421D;
        } else {
            super.travel(strafe, vertical, forward);
        }
    }

    @Override
    public boolean getIsFlying() {
        return false;
    }

    @Override
    public String getClazzString() {
        return EntityList.getEntityString(this);
    }

    @Override
    public boolean getIsGhost() {
        return false;
    }

    @Override
    public void setLeashHolder(Entity entityIn, boolean sendAttachNotification) {
        if (this.getIsTamed() && entityIn instanceof EntityPlayer) {
            EntityPlayer entityplayer = (EntityPlayer) entityIn;
            if (MoCreatures.proxy.enableOwnership && this.getOwnerId() != null && !entityplayer.getUniqueID().equals(this.getOwnerId()) && !MoCTools.isThisPlayerAnOP((entityplayer))) {
                return;
            }
        }
        super.setLeashHolder(entityIn, sendAttachNotification);
    }

    /***
     * Used to select Animals that can 'ride' the player. Like mice, snakes, turtles, birds
     */
    @Override
    public boolean canRidePlayer() {
        return false;
    }

    @Override
    public boolean startRidingPlayer(EntityPlayer player) {
        if (MoCTools.getEntityRidingPlayer(player) != null) {
            return false; // Something is already riding this player.
        }
        return super.startRiding(player, true);
    }

    @Override
    public void onStopRidingPlayer() {
        // Called when an Entity is dismounted from riding on the Player's head.
    }
}
