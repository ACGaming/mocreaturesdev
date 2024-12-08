/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.item;

import drzhark.mocreatures.MoCTools;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.List;

public class MoCItemPetAmuletNew extends MoCItem {

    public MoCItemPetAmuletNew(String name) {
        super(name);
        this.maxStackSize = 1;
    }

    @Override
    // Release Entity
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        ItemStack stack = player.getHeldItem(hand);
        if (!world.isRemote && stack.hasTagCompound()) {
            Entity entity = EntityList.createEntityFromNBT(stack.getTagCompound(), world);
            if (entity != null) {
                double dist = 1D;
                double newPosX = player.posX - (dist * Math.cos((MoCTools.realAngle(player.rotationYaw - 90F)) / 57.29578F));
                double newPosY = player.posY;
                double newPosZ = player.posZ - (dist * Math.sin((MoCTools.realAngle(player.rotationYaw - 90F)) / 57.29578F));
                entity.setLocationAndAngles(newPosX, newPosY, newPosZ, player.rotationYaw, 0.0F);
                world.spawnEntity(entity);
                stack.setTagCompound(null);
                return new ActionResult<>(EnumActionResult.SUCCESS, stack);
            }
        }
        return new ActionResult<>(EnumActionResult.PASS, stack);
    }

    @Override
    // Capture Entity
    public boolean itemInteractionForEntity(ItemStack stack, EntityPlayer player, EntityLivingBase entity, EnumHand hand) {
        if (!entity.world.isRemote && !stack.hasTagCompound()/* && entity.hasCustomName()*/) {
            NBTTagCompound entityNBT = new NBTTagCompound();
            if (!entity.writeToNBTAtomically(entityNBT)) {
                return false;
            }
            entityNBT.setString("id", EntityList.getKey(entity.getClass()).toString());
            entityNBT.setString("name", entity.getName());
            stack.setTagCompound(entityNBT);
            entity.setDead();
            return true;
        }
        return false;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        if (stack.hasTagCompound() && stack.getTagCompound().hasKey("name")) {
            tooltip.add(TextFormatting.BLUE + stack.getTagCompound().getString("name"));
        }
    }
}
