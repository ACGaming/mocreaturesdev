/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.item;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.hostile.MoCEntityOgre;
import drzhark.mocreatures.init.MoCItems;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

import java.util.List;

// TODO: Fix hitbox
public class MoCEntityLitterBox extends EntityLiving {

    private static final DataParameter<Boolean> PICKED_UP = EntityDataManager.createKey(MoCEntityLitterBox.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> USED_LITTER = EntityDataManager.createKey(MoCEntityLitterBox.class, DataSerializers.BOOLEAN);
    public int litterTime;

    public MoCEntityLitterBox(World world) {
        super(world);
        setSize(1.0F, 0.15F);
        setNoAI(true);
    }

    public ResourceLocation getTexture() {
        return MoCreatures.proxy.getModelTexture("litter_box.png");
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(20.0D);
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(PICKED_UP, Boolean.FALSE);
        this.dataManager.register(USED_LITTER, Boolean.FALSE);
    }

    public boolean getPickedUp() {
        return this.dataManager.get(PICKED_UP);
    }

    public void setPickedUp(boolean flag) {
        this.dataManager.set(PICKED_UP, flag);
    }

    public boolean getUsedLitter() {
        return this.dataManager.get(USED_LITTER);
    }

    public void setUsedLitter(boolean flag) {
        this.dataManager.set(USED_LITTER, flag);
    }

    public boolean attackEntityFrom(Entity entity, int i) {
        return false;
    }

    @Override
    public boolean canBePushed() {
        return !this.isDead;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Override
    protected boolean canDespawn() {
        return false;
    }

    @Override
    public void fall(float f, float f1) {
    }

    @Override
    protected float getSoundVolume() {
        return 0.0F;
    }

    @Override
    public double getMountedYOffset() {
        return 0.0D;
    }

    @Override
    public void handleStatusUpdate(byte byte0) {
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        final ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && stack.getItem() == Item.getItemFromBlock(Blocks.SAND)) {
            MoCTools.playCustomSound(this, SoundEvents.BLOCK_SAND_PLACE);
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            setUsedLitter(false);
            this.litterTime = 0;
            return true;
        }
        if (this.getRidingEntity() == null) {
            if (player.isSneaking()) {
                player.inventory.addItemStackToInventory(new ItemStack(MoCItems.litterbox));
                MoCTools.playCustomSound(this, SoundEvents.ENTITY_ITEM_PICKUP, 0.2F);
                setDead();
            } else {
                setRotationYawHead((float) MoCTools.roundToNearest90Degrees(this.rotationYawHead) + 90.0F);
                MoCTools.playCustomSound(this, SoundEvents.ENTITY_ITEMFRAME_ROTATE_ITEM);
            }
            return true;
        }
        return true;
    }

    @Override
    public void move(MoverType type, double d, double d1, double d2) {
        if (!this.world.isRemote && (getRidingEntity() != null || !this.onGround || !MoCreatures.proxy.staticLitter)) {
            super.move(type, d, d1, d2);
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();
        if (this.onGround) {
            setPickedUp(false);
        }
        if (getUsedLitter()) {
            if (!this.world.isRemote) {
                this.litterTime++;
                List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(12D, 4D, 12D));
                for (Entity entity : list) {
                    if (!(entity instanceof EntityMob)) {
                        continue;
                    }
                    EntityMob entityMob = (EntityMob) entity;
                    entityMob.setAttackTarget(this);
                    if (entityMob instanceof EntityCreeper) {
                        ((EntityCreeper) entityMob).setCreeperState(-1);
                    }
                    if (entityMob instanceof MoCEntityOgre) {
                        ((MoCEntityOgre) entityMob).smashCounter = 0;
                    }
                }
            } else {
                this.world.spawnParticle(EnumParticleTypes.SMOKE_NORMAL, this.posX, this.posY, this.posZ, 0.0D, 0.0D, 0.0D);
            }
        }
        if (this.litterTime > 5000 && !this.world.isRemote) {
            setUsedLitter(false);
            this.litterTime = 0;
        }
        if (this.isRiding()) MoCTools.dismountPassengerFromEntity(this, this.getRidingEntity(), false);
    }

    @Override
    public void writeEntityToNBT(NBTTagCompound compound) {
        compound = MoCTools.getEntityData(this);
        compound.setBoolean("UsedLitter", getUsedLitter());
    }

    @Override
    public void readEntityFromNBT(NBTTagCompound compound) {
        compound = MoCTools.getEntityData(this);
        setUsedLitter(compound.getBoolean("UsedLitter"));
    }

    @Override
    public boolean attackEntityFrom(DamageSource damagesource, float i) {
        return false;
    }
}
