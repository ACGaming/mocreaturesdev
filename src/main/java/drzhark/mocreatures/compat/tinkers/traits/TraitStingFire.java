package drzhark.mocreatures.compat.tinkers.traits;

import drzhark.mocreatures.MoCConstants;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import slimeknights.tconstruct.library.tools.ToolNBT;
import slimeknights.tconstruct.library.traits.AbstractTrait;
import slimeknights.tconstruct.library.utils.TagUtil;
import slimeknights.tconstruct.library.utils.TinkerUtil;

public class TraitStingFire extends AbstractTrait {
    protected final float damage;

    public TraitStingFire(float damage) {
        super(MoCConstants.MOD_ID + "." + "fire_sting", 0xC62B13);

        this.damage = damage;
    }

    @Override
    public void applyEffect(NBTTagCompound rootCompound, NBTTagCompound modifierTag) {
        // Apply bonus damage if it hasn't been applied yet
        if (!TinkerUtil.hasTrait(rootCompound, identifier)) {
            ToolNBT data = TagUtil.getToolStats(rootCompound);
            data.attack += damage;
            TagUtil.setToolTag(rootCompound, data.get());
        }
        super.applyEffect(rootCompound, modifierTag);
    }

    @Override
    public void afterHit(ItemStack tool, EntityLivingBase player, EntityLivingBase target, float damageDealt, boolean wasCritical, boolean wasHit) {
        if (wasHit && target.isEntityAlive()) {
            target.setFire(5);
        }
    }
}
