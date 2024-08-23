/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.entity.passive;

import drzhark.mocreatures.MoCTools;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.entity.ai.EntityAIFleeFromEntityMoC;
import drzhark.mocreatures.entity.ai.EntityAIFollowOwnerPlayer;
import drzhark.mocreatures.entity.ai.EntityAIWanderMoC2;
import drzhark.mocreatures.entity.tameable.MoCEntityTameableAnimal;
import drzhark.mocreatures.init.MoCLootTables;
import drzhark.mocreatures.init.MoCSoundEvents;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;

import javax.annotation.Nullable;
import java.util.List;

public class MoCEntityBird extends MoCEntityTameableAnimal {

    private static final DataParameter<Boolean> PRE_TAMED = EntityDataManager.createKey(MoCEntityBird.class, DataSerializers.BOOLEAN);
    private static final DataParameter<Boolean> IS_FLYING = EntityDataManager.createKey(MoCEntityBird.class, DataSerializers.BOOLEAN);
    public float wingb;
    public float wingc;
    public float wingd;
    public float winge;
    public float wingh;
    public boolean textureSet;
    protected EntityAIWanderMoC2 wander;
    private boolean fleeing;
    private int jumpTimer;

    public MoCEntityBird(World world) {
        super(world);
        setSize(0.5F, 0.9F);
        this.collidedVertically = true;
        this.wingb = 0.0F;
        this.wingc = 0.0F;
        this.wingh = 1.0F;
        this.fleeing = false;
        this.textureSet = false;
        setTamed(false);
        this.stepHeight = 1.0F;
    }

    @Override
    protected void initEntityAI() {
        this.tasks.addTask(1, new EntityAISwimming(this));
        this.tasks.addTask(2, new EntityAIFleeFromEntityMoC(this, entity -> !(entity instanceof MoCEntityBird) && (entity.height > 0.4F || entity.width > 0.4F), 6.0F, 1.D, 1.3D));
        this.tasks.addTask(3, new EntityAIFollowOwnerPlayer(this, 0.8D, 2F, 10F));
        this.tasks.addTask(4, this.wander = new EntityAIWanderMoC2(this, 1.0D, 80));
        this.tasks.addTask(7, new EntityAIWatchClosest(this, EntityPlayer.class, 8.0F));
    }

    @Override
    protected void applyEntityAttributes() {
        super.applyEntityAttributes();
        this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(6.0D);
        this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.3D);
    }

    @Override
    public void selectType() {
        checkSpawningBiome();

        if (getType() == 0) {
            setType(this.rand.nextInt(6) + 1);
        }
    }

    @Override
    public ResourceLocation getTexture() {

        switch (getType()) {
            case 1:
                return MoCreatures.proxy.getModelTexture("bird_white.png");
            case 2:
                return MoCreatures.proxy.getModelTexture("bird_black.png");
            case 3:
                return MoCreatures.proxy.getModelTexture("bird_green.png");
            case 5:
                return MoCreatures.proxy.getModelTexture("bird_yellow.png");
            case 6:
                return MoCreatures.proxy.getModelTexture("bird_red.png");
            default:
                return MoCreatures.proxy.getModelTexture("bird_blue.png");
        }
    }

    @Override
    public boolean checkSpawningBiome() {
        BlockPos pos = new BlockPos(MathHelper.floor(this.posX), MathHelper.floor(getEntityBoundingBox().minY), this.posZ);
        Biome currentbiome = MoCTools.biomeKind(this.world, pos);

        try {
            if (BiomeDictionary.hasType(currentbiome, BiomeDictionary.Type.MESA)) {
                setType(2); // only black birds
            }
        } catch (Exception ignored) {
        }
        return true;
    }

    @Override
    protected void entityInit() {
        super.entityInit();
        this.dataManager.register(PRE_TAMED, Boolean.FALSE);
        this.dataManager.register(IS_FLYING, Boolean.FALSE);
    }

    public boolean getPreTamed() {
        return this.dataManager.get(PRE_TAMED);
    }

    public void setPreTamed(boolean flag) {
        this.dataManager.set(PRE_TAMED, flag);
    }

    public boolean getIsFlying() {
        return this.dataManager.get(IS_FLYING);
    }

    public void setIsFlying(boolean flag) {
        this.dataManager.set(IS_FLYING, flag);
    }

    @Override
    public void fall(float f, float f1) {
    }

    private int[] FindTreeTop(int i, int j, int k) {
        int l = i - 5;
        int i1 = k - 5;
        int j1 = i + 5;
        int k1 = j + 7;
        int l1 = k + 5;
        for (int i2 = l; i2 < j1; i2++) {
            label0:
            for (int j2 = i1; j2 < l1; j2++) {
                BlockPos pos = new BlockPos(i2, j, j2);
                IBlockState blockstate = this.world.getBlockState(pos);
                if (blockstate.getBlock().isAir(blockstate, this.world, pos) || (blockstate.getMaterial() != Material.WOOD)) {
                    continue;
                }
                int l2 = j;
                do {
                    if (l2 >= k1) {
                        continue label0;
                    }
                    BlockPos pos1 = new BlockPos(i2, l2, j2);
                    IBlockState blockstate1 = this.world.getBlockState(pos1);
                    if (blockstate1.getBlock().isAir(blockstate1, this.world, pos1)) {
                        return (new int[]{i2, l2 + 2, j2});
                    }
                    l2++;
                } while (true);
            }

        }

        return (new int[]{0, 0, 0});
    }

    private void FlyToNextEntity(Entity entity) {
        if (entity != null) {
            int i = MathHelper.floor(entity.posX);
            int j = MathHelper.floor(entity.posY);
            int k = MathHelper.floor(entity.posZ);
            faceLocation(i, j, k, 30F);
            if (MathHelper.floor(this.posY) < j) {
                this.motionY += 0.14999999999999999D;
            }
            if (this.posX < entity.posX) {
                double d = entity.posX - this.posX;
                if (d > 0.5D) {
                    this.motionX += 0.050000000000000003D;
                }
            } else {
                double d1 = this.posX - entity.posX;
                if (d1 > 0.5D) {
                    this.motionX -= 0.050000000000000003D;
                }
            }
            if (this.posZ < entity.posZ) {
                double d2 = entity.posZ - this.posZ;
                if (d2 > 0.5D) {
                    this.motionZ += 0.050000000000000003D;
                }
            } else {
                double d3 = this.posZ - entity.posZ;
                if (d3 > 0.5D) {
                    this.motionZ -= 0.050000000000000003D;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    private boolean FlyToNextTree() {
        int[] ai = ReturnNearestMaterialCoord(this, Material.LEAVES, 20D);
        int[] ai1 = FindTreeTop(ai[0], ai[1], ai[2]);
        if (ai1[1] != 0) {
            int i = ai1[0];
            int j = ai1[1];
            int k = ai1[2];
            faceLocation(i, j, k, 30F);
            if ((j - MathHelper.floor(this.posY)) > 2) {
                this.motionY += 0.14999999999999999D;
            }
            int l;
            int i1;
            if (this.posX < i) {
                l = i - MathHelper.floor(this.posX);
                this.motionX += 0.050000000000000003D;
            } else {
                l = MathHelper.floor(this.posX) - i;
                this.motionX -= 0.050000000000000003D;
            }
            if (this.posZ < k) {
                i1 = k - MathHelper.floor(this.posZ);
                this.motionZ += 0.050000000000000003D;
            } else {
                i1 = MathHelper.floor(this.posX) - k;
                this.motionZ -= 0.050000000000000003D;
            }
            double d = l + i1;
            return d < 3D;
        }
        return false;
    }

    @Override
    protected SoundEvent getDeathSound() {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource source) {
        return SoundEvents.ENTITY_GENERIC_HURT;
    }

    @Override
    protected SoundEvent getAmbientSound() {
        if (getType() == 1) {
            return MoCSoundEvents.ENTITY_BIRD_AMBIENT_WHITE;
        }
        if (getType() == 2) {
            return MoCSoundEvents.ENTITY_BIRD_AMBIENT_BLACK;
        }
        if (getType() == 3) {
            return MoCSoundEvents.ENTITY_BIRD_AMBIENT_GREEN;
        }
        if (getType() == 4) {
            return MoCSoundEvents.ENTITY_BIRD_AMBIENT_BLUE;
        }
        if (getType() == 5) {
            return MoCSoundEvents.ENTITY_BIRD_AMBIENT_YELLOW;
        } else {
            return MoCSoundEvents.ENTITY_BIRD_AMBIENT_RED;
        }
    }
    
    // TODO: Add unique sound event
    @Override
    protected void playStepSound(BlockPos pos, Block blockIn) {
        this.playSound(SoundEvents.ENTITY_PARROT_STEP, 0.15F, 1.0F);
    }

    @Nullable
    protected ResourceLocation getLootTable() {
        return MoCLootTables.BIRD;
    }

    @Override
    public double getYOffset() {
        if (this.getRidingEntity() instanceof EntityPlayer) {
            return this.getRidingEntity().isSneaking() ? 0.2 : 0.45F;
        }

        return super.getYOffset();
    }

    @Override
    public boolean processInteract(EntityPlayer player, EnumHand hand) {
        final Boolean tameResult = this.processTameInteract(player, hand);
        if (tameResult != null) {
            return tameResult;
        }

        final ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty() && getPreTamed() && !getIsTamed() && stack.getItem() == Items.WHEAT_SEEDS) {
            if (!player.capabilities.isCreativeMode) stack.shrink(1);
            if (!this.world.isRemote) {
                MoCTools.tameWithName(player, this);
            }
            return true;
        }

        if (!getIsTamed()) {
            return false;
        }
        if (this.getRidingEntity() == null) {
            if (this.startRidingPlayer(player)) {
                this.rotationYaw = player.rotationYaw;
            }

            return true;
        }

        return super.processInteract(player, hand);
    }

    // TODO: Add updated flap ai based on vanilla's parrot
    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();

        this.winge = this.wingb;
        this.wingd = this.wingc;
        this.wingc = (float) (this.wingc + ((this.onGround ? -1 : 4) * 0.3D));
        if (this.wingc < 0.0F) {
            this.wingc = 0.0F;
        }
        if (this.wingc > 1.0F) {
            this.wingc = 1.0F;
        }
        if (!this.onGround && (this.wingh < 1.0F)) {
            this.wingh = 1.0F;
        }
        this.wingh = (float) (this.wingh * 0.9D);
        if (!this.onGround && (this.motionY < 0.0D)) {
            this.motionY *= 0.8D;
        }
        this.wingb += this.wingh * 2.0F;

        //check added to avoid duplicating behavior on client / server
        if (!this.world.isRemote) {

            if (isMovementCeased() && getIsFlying()) {
                setIsFlying(false);
            }

            if (getIsFlying() && this.getNavigator().noPath() && !isMovementCeased() && this.getAttackTarget() == null && rand.nextInt(30) == 0) {
                this.wander.makeUpdate();
            }

            if (!getIsFlying() && !getIsTamed() && this.rand.nextInt(10) == 0) {
                List<Entity> list = this.world.getEntitiesWithinAABBExcludingEntity(this, getEntityBoundingBox().grow(4D));
                for (Entity entity1 : list) {
                    if (!(entity1 instanceof EntityLivingBase) || entity1 instanceof MoCEntityBird) {
                        continue;
                    }
                    if (entity1.width >= 0.4F && entity1.height >= 0.4F && canEntityBeSeen(entity1)) {
                        setIsFlying(true);
                        this.fleeing = true;
                        this.wander.makeUpdate();
                    }
                }
            }

            if (!isMovementCeased() && !getIsFlying() && this.rand.nextInt(getIsTamed() ? 1000 : 400) == 0) {
                setIsFlying(true);
                this.wander.makeUpdate();
            }

            if (getIsFlying() && rand.nextInt(200) == 0) {
                setIsFlying(false);
            }

            if (this.fleeing && rand.nextInt(50) == 0) {
                this.fleeing = false;
            }

            //TODO move to new AI
            if (!this.fleeing) {
                EntityItem entityitem = getClosestItem(this, 12D, Items.WHEAT_SEEDS, Items.MELON_SEEDS);
                if (entityitem != null) {
                    FlyToNextEntity(entityitem);
                    EntityItem entityitem1 = getClosestItem(this, 1.0D, Items.WHEAT_SEEDS, Items.MELON_SEEDS);
                    if ((this.rand.nextInt(50) == 0) && (entityitem1 != null)) {
                        entityitem1.setDead();
                        setPreTamed(true);
                    }
                }
            }
            if (this.rand.nextInt(10) == 0 && isInsideOfMaterial(Material.WATER)) {
                WingFlap();
            }
        }
    }

    @Override
    public void onUpdate() {
        super.onUpdate();

        if (this.getRidingEntity() != null) {
            this.rotationYaw = this.getRidingEntity().rotationYaw;
        }

        if ((this.getRidingEntity() != null) && (this.getRidingEntity() instanceof EntityPlayer)) {
            EntityPlayer entityplayer = (EntityPlayer) this.getRidingEntity();
            this.rotationYaw = entityplayer.rotationYaw;
            entityplayer.fallDistance = 0.0F;
            if (entityplayer.motionY < -0.1D)
                entityplayer.motionY *= 0.60;
        }

        if (--this.jumpTimer <= 0 && this.onGround
                && ((this.motionX > 0.05D) || (this.motionZ > 0.05D) || (this.motionX < -0.05D) || (this.motionZ < -0.05D))) {
            this.motionY = 0.25D;
            float velX = MathHelper.sin(this.rotationYaw * (float) Math.PI / 180.0F);
            float velZ = MathHelper.cos(this.rotationYaw * (float) Math.PI / 180.0F);

            this.motionX += (-0.2F * velX);
            this.motionZ += (0.2F * velZ);
            this.jumpTimer = 15;
        }
    }

    public int[] ReturnNearestMaterialCoord(Entity entity, Material material, Double double1) {
        AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox().grow(double1);
        int i = MathHelper.floor(axisalignedbb.minX);
        int j = MathHelper.floor(axisalignedbb.maxX + 1.0D);
        int k = MathHelper.floor(axisalignedbb.minY);
        int l = MathHelper.floor(axisalignedbb.maxY + 1.0D);
        int i1 = MathHelper.floor(axisalignedbb.minZ);
        int j1 = MathHelper.floor(axisalignedbb.maxZ + 1.0D);
        for (int k1 = i; k1 < j; k1++) {
            for (int l1 = k; l1 < l; l1++) {
                for (int i2 = i1; i2 < j1; i2++) {
                    BlockPos pos = new BlockPos(k1, l1, i2);
                    IBlockState blockstate = this.world.getBlockState(pos);
                    blockstate.getBlock();
                    if (!blockstate.getBlock().isAir(blockstate, this.world, pos) && blockstate.getMaterial() == material) {
                        return (new int[]{k1, l1, i2});
                    }
                }

            }

        }

        return (new int[]{-1, 0, 0});
    }

    @Override
    public void setDead() {
        // Server check required to prevent tamed entities from being duplicated on client-side
        if (!this.world.isRemote && (getIsTamed()) && (getHealth() > 0)) {
            return;
        }
        super.setDead();
    }

    private void WingFlap() {
        this.motionY += 0.05D;
        if (this.rand.nextInt(30) == 0) {
            this.motionX += 0.2D;
        }
        if (this.rand.nextInt(30) == 0) {
            this.motionX -= 0.2D;
        }
        if (this.rand.nextInt(30) == 0) {
            this.motionZ += 0.2D;
        }
        if (this.rand.nextInt(30) == 0) {
            this.motionZ -= 0.2D;
        }
    }

    @Override
    public int nameYOffset() {
        return -40;
    }

    @Override
    public boolean isMyHealFood(ItemStack stack) {
        return !stack.isEmpty() && (stack.getItem() == Items.WHEAT_SEEDS || stack.getItem() == Items.MELON_SEEDS);
    }

    @Override
    public boolean isNotScared() {
        return getIsTamed();
    }

    @Override
    public boolean isFlyer() {
        return true;
    }

    @Override
    public int maxFlyingHeight() {
        if (getIsTamed())
            return 4;
        return 6;
    }

    @Override
    public int minFlyingHeight() {
        return 2;
    }

    @Override
    public boolean canRidePlayer() {
        return true;
    }

    public float getEyeHeight() {
        return this.height * 0.75F;
    }

    @Override
    public boolean isReadyToFollowOwnerPlayer() { return !this.isMovementCeased(); }
}
