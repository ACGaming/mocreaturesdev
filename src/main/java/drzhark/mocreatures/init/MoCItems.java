/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.init;

import drzhark.mocreatures.MoCConstants;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.item.*;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraft.item.ItemArmor.ArmorMaterial;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.*;

// TODO: Rework item registration to be like block registration
public class MoCItems {
    public static final Set<Item> ITEMS = new HashSet<>();
    // Misc
    public static final MoCItemRecord recordshuffle = new MoCItemRecord("recordshuffle", MoCSoundEvents.MUSIC_DISC_SHUFFLING);
    public static final MoCItem sharkteeth = new MoCItem("sharkteeth");
    public static final MoCItemFood sugarlump = (MoCItemFood) new MoCItemFood("sugarlump", 1, 0.1F, false, 12).setPotionEffect(new PotionEffect(MobEffects.NAUSEA, 4 * 20, 0), 0.15F);
    public static final MoCItem mocegg = new MoCItemEgg("mocegg");
    public static final MoCItem bigcatclaw = new MoCItem("bigcatclaw");
    public static final MoCItem staffPortal = new ItemStaffPortal("staffportal");
    public static final MoCItem medallion = new MoCItem("medallion");
    public static final MoCItemKittyBed[] kittybed = new MoCItemKittyBed[16];
    public static final MoCItem litterbox = new MoCItemLitterBox("kittylitter");
    public static final MoCItem woolball = new MoCItem("woolball");
    public static final MoCItem petfood = new MoCItem("petfood");
    public static final MoCItem hideCroc = new MoCItem("reptilehide");
    public static final MoCItem fur = new MoCItem("fur");
    public static final MoCItem essencedarkness = new MoCItem("essencedarkness");
    public static final MoCItem essenceEternal = new MoCItem("essence_eternal");
    public static final MoCItem essencefire = new MoCItem("essencefire");
    public static final MoCItem essenceIce = new MoCItem("essence_ice");
    public static final MoCItem essenceundead = new MoCItem("essenceundead");
    public static final MoCItem essencelight = new MoCItem("essencelight");
    public static final MoCItem amuletbone = new MoCItemHorseAmulet("amuletbone");
    public static final MoCItem amuletbonefull = new MoCItemHorseAmulet("amuletbonefull");
    public static final MoCItem amuletghost = new MoCItemHorseAmulet("amuletghost");
    public static final MoCItem amuletghostfull = new MoCItemHorseAmulet("amuletghostfull");
    public static final MoCItem amuletfairy = new MoCItemHorseAmulet("amuletfairy");
    public static final MoCItem amuletfairyfull = new MoCItemHorseAmulet("amuletfairyfull");
    public static final MoCItem amuletpegasus = new MoCItemHorseAmulet("amuletpegasus");
    public static final MoCItem amuletpegasusfull = new MoCItemHorseAmulet("amuletpegasusfull");
    public static final MoCItem fishnet = new MoCItemPetAmulet("fishnet");
    public static final MoCItem fishnetfull = new MoCItemPetAmulet("fishnetfull");
    public static final MoCItem petamulet = new MoCItemPetAmulet("petamulet", 1);
    public static final MoCItem petamuletfull = new MoCItemPetAmulet("petamuletfull", 1);
    public static final MoCItem petamuletuniversal = new MoCItemPetAmuletNew("petamuletuniversal");
    public static final MoCItem heartdarkness = new MoCItem("heartdarkness");
    public static final MoCItem heartEternal = new MoCItem("heart_eternal");
    public static final MoCItem heartfire = new MoCItem("heartfire");
    public static final MoCItem heartIce = new MoCItem("heart_ice");
    public static final MoCItem heartundead = new MoCItem("heartundead");
    public static final MoCItem unicornhorn = new MoCItem("unicornhorn");
    public static final MoCItem horsearmorcrystal = (MoCItem) new MoCItem("horsearmorcrystal", 0, true).setMaxStackSize(1);
    public static final MoCItem animalHide = new MoCItem("hide");
    public static final MoCItem chitinCave = new MoCItem("chitinblack");
    public static final MoCItem chitinFrost = new MoCItem("chitinfrost");
    public static final MoCItem chitinNether = new MoCItem("chitinnether");
    public static final MoCItem chitinUndead = new MoCItem("chitinundead");
    public static final MoCItem chitin = new MoCItem("chitin");
    public static final MoCItem tusksWood = new MoCItem("tuskswood");
    public static final MoCItem tusksIron = new MoCItem("tusksiron");
    public static final MoCItem tusksDiamond = new MoCItem("tusksdiamond");
    public static final MoCItem elephantHarness = new MoCItem("elephantharness");
    public static final MoCItem elephantChest = new MoCItem("elephantchest");
    public static final MoCItem elephantGarment = new MoCItem("elephantgarment");
    public static final MoCItem elephantHowdah = new MoCItem("elephanthowdah");
    public static final MoCItem mammothPlatform = new MoCItem("mammothplatform");
    public static final MoCItem scrollOfFreedom = new MoCItem("scrolloffreedom", 0, true);
    public static final MoCItem scrollOfSale = new MoCItem("scrollofsale", 0, true);
    public static final MoCItem scrollOfRenaming = new MoCItem("scrollofrenaming", 0, true);
    public static final MoCItem ancientSilverScrap = new MoCItem("ancientsilverscrap");
    public static final MoCItem ancientSilverIngot = new MoCItem("ancientsilveringot");
    public static final MoCItem ancientSilverNugget = new MoCItem("ancientsilvernugget");
    public static final MoCItem fineSilverIngot = new MoCItem("fine_silver_ingot");
    public static final MoCItem fineSilverNugget = new MoCItem("fine_silver_nugget");
    public static final MoCItem firestoneChunk = new MoCItem("firestonechunk");
    //public static final MoCItemCrabClaw brackishClaw = new MoCItemCrabClaw("brackish_claw", 768, 15, 0.0F, 1, 2.0F);
    // Doors - These need to be registered alongside the blocks
    public static final MoCItemDoor wyvwoodDoor = new MoCItemDoor(MoCBlocks.wyvwoodDoor, "wyvwood_door");
    // Food
    public static final MoCItemFood cookedTurkey = new MoCItemFood("turkeycooked", 7, 0.8F, true);
    public static final MoCItemFood crabraw = (MoCItemFood) new MoCItemFood("crabraw", 2, 0.1F, true).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 30 * 20, 0), 0.8F);
    public static final MoCItemFood crabcooked = new MoCItemFood("crabcooked", 4, 0.6F, true);
    public static final MoCItemFood duckCooked = new MoCItemFood("duckcooked", 6, 0.7F, true);
    public static final MoCItemFood duckRaw = new MoCItemFood("duckraw", 2, 0.4F, true);
    public static final MoCItemFood mysticPear = new MoCItemFood("mysticpear", 4, 0.8F, false, 16).setAlwaysEdible();
    public static final MoCItemFood omelet = new MoCItemFood("omelet", 3, 0.5F, false);
    public static final MoCItemFood ostrichraw = (MoCItemFood) new MoCItemFood("ostrichraw", 3, 0.4F, true).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 30 * 20, 0), 0.8F);
    public static final MoCItemFood ostrichcooked = new MoCItemFood("ostrichcooked", 7, 0.8F, true);
    public static final MoCItemFood ratBurger = new MoCItemFood("ratburger", 9, 0.5F, false);
    public static final MoCItemFood ratCooked = new MoCItemFood("ratcooked", 4, 0.5F, true);
    public static final MoCItemFood ratRaw = (MoCItemFood) new MoCItemFood("ratraw", 2, 0.1F, true).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 30 * 20, 0), 0.8F);
    public static final MoCItemFood rawTurkey = (MoCItemFood) new MoCItemFood("turkeyraw", 3, 0.4F, true).setPotionEffect(new PotionEffect(MobEffects.HUNGER, 30 * 20, 0), 0.8F);
    public static final MoCItemFood turtlecooked = new MoCItemFood("turtlecooked", 6, 0.7F, true);
    public static final MoCItemFood turtleraw = new MoCItemFood("turtleraw", 2, 0.2F, true);
    public static final MoCItemFood turtlesoup = new MoCItemTurtleSoup("turtlesoup", 8, 0.8F, false);
    public static final MoCItemFood venisonCooked = new MoCItemFood("venisoncooked", 8, 0.9F, true);
    public static final MoCItemFood venisonRaw = new MoCItemFood("venisonraw", 3, 0.4F, true);
    // Weapons
    static ToolMaterial WHIP = EnumHelper.addToolMaterial("WHIP", 0, 184, 2.0F, 3.0F, 15).setRepairItem(new ItemStack(Items.LEATHER));
    public static final MoCItemWhip whip = new MoCItemWhip("whip", WHIP, 1.9F);
    static ToolMaterial REPTILEWHIP = EnumHelper.addToolMaterial("REPTILEWHIP", 0, 224, 3.0F, 4.0F, 17).setRepairItem(new ItemStack(hideCroc));
    public static final MoCItemWhip reptileWhip = new MoCItemWhip("reptile_whip", REPTILEWHIP, 1.9F);
    static ToolMaterial NINJA = EnumHelper.addToolMaterial("NINJA", 3, 501, 10.0F, 3.5F, 20).setRepairItem(new ItemStack(ancientSilverIngot));
    public static final MoCItemSword nunchaku = new MoCItemSword("nunchaku", NINJA);
    public static final MoCItemSword sai = new MoCItemSword("sai", NINJA);
    public static final MoCItemSword bo = new MoCItemSword("bo", NINJA);
    public static final MoCItemSword katana = new MoCItemSword("katana", NINJA);
    static ToolMaterial SHARK = EnumHelper.addToolMaterial("SHARK", 1, 161, 7.0F, 2.5F, 15).setRepairItem(new ItemStack(sharkteeth));
    public static final MoCItemSword sharksword = new MoCItemSword("sharksword", SHARK);
    public static final MoCItemMattock sharkMattock = new MoCItemMattock("shark_mattock", SHARK, 4.5F, 1.2F);
    public static final MoCItemAxe sharkaxe = new MoCItemAxe("sharkaxe", SHARK, 9.5F, 1.0F);
    static ToolMaterial SILVER = EnumHelper.addToolMaterial("SILVER", 3, 404, 9.5F, 3.0F, 19).setRepairItem(new ItemStack(ancientSilverIngot));
    public static final MoCItemSword silversword = new MoCItemSword("silversword", SILVER);
    public static final MoCItemMattock silverMattock = new MoCItemMattock("ancient_silver_mattock", SILVER, 6.0F, 1.3F);
    public static final MoCItemAxe silveraxe = new MoCItemAxe("silveraxe", SILVER, 10.0F, 1.1F);
    public static final MoCItemBow silverBow = new MoCItemBow("ancient_silver_bow", 720, 1.3F, 1.2F, 0.8F, 0.8F, Ingredient.fromStacks(new ItemStack(ancientSilverIngot)));
    static ToolMaterial SCORPC = EnumHelper.addToolMaterial("SCORPC", 3, 371, 7.5F, 2.5F, 16).setRepairItem(new ItemStack(chitinCave));
    public static final MoCItemSword scorpSwordCave = new MoCItemSword("scorpswordcave", SCORPC, 4);
    public static final MoCItemMattock scorpMattockCave = new MoCItemMattock("dark_scorpion_mattock", SCORPC, 4.5F, 1.2F, 4);
    public static final MoCItemAxe scorpAxeCave = new MoCItemAxe("scorpaxecave", SCORPC, 9.5F, 1.0F, 4);
    static ToolMaterial SCORPF = EnumHelper.addToolMaterial("SCORPF", 3, 371, 7.5F, 2.5F, 16).setRepairItem(new ItemStack(chitinFrost));
    public static final MoCItemSword scorpSwordFrost = new MoCItemSword("scorpswordfrost", SCORPF, 2);
    public static final MoCItemMattock scorpMattockFrost = new MoCItemMattock("frost_scorpion_mattock", SCORPF, 4.5F, 1.2F, 2);
    public static final MoCItemAxe scorpAxeFrost = new MoCItemAxe("scorpaxefrost", SCORPF, 9.5F, 1.0F, 2);
    static ToolMaterial SCORPN = EnumHelper.addToolMaterial("SCORPN", 3, 371, 7.5F, 2.5F, 16).setRepairItem(new ItemStack(chitinNether));
    public static final MoCItemSword scorpSwordNether = new MoCItemSword("scorpswordnether", SCORPN, 3);
    public static final MoCItemMattock scorpMattockNether = new MoCItemMattock("fire_scorpion_mattock", SCORPN, 4.5F, 1.2F, 3);
    public static final MoCItemAxe scorpAxeNether = new MoCItemAxe("scorpaxenether", SCORPN, 9.5F, 1.0F, 3);
    static ToolMaterial SCORPD = EnumHelper.addToolMaterial("SCORPD", 3, 371, 7.5F, 2.5F, 16).setRepairItem(new ItemStack(chitin));
    public static final MoCItemSword scorpSwordDirt = new MoCItemSword("scorpsworddirt", SCORPD, 1);
    public static final MoCItemMattock scorpMattockDirt = new MoCItemMattock("earth_scorpion_mattock", SCORPD, 4.5F, 1.2F, 1);
    public static final MoCItemAxe scorpAxeDirt = new MoCItemAxe("scorpaxedirt", SCORPD, 9.5F, 1.0F, 1);
    static ToolMaterial SCORPU = EnumHelper.addToolMaterial("SCORPU", 3, 371, 7.5F, 2.5F, 16).setRepairItem(new ItemStack(chitinUndead));
    public static final MoCItemSword scorpSwordUndead = new MoCItemSword("scorpswordundead", SCORPU, 5);
    public static final MoCItemMattock scorpMattockUndead = new MoCItemMattock("undead_scorpion_mattock", SCORPU, 4.5F, 1.2F, 5);
    public static final MoCItemAxe scorpAxeUndead = new MoCItemAxe("scorpaxeundead", SCORPU, 9.5F, 1.0F, 5);
    static ToolMaterial STING = EnumHelper.addToolMaterial("STING", 0, 8, 6.0F, 0.0F, 5);
    public static final MoCItemWeapon scorpStingCave = new MoCItemWeapon("scorpstingcave", STING, 4);
    public static final MoCItemWeapon scorpStingFrost = new MoCItemWeapon("scorpstingfrost", STING, 2);
    public static final MoCItemWeapon scorpStingNether = new MoCItemWeapon("scorpstingnether", STING, 3);
    public static final MoCItemWeapon scorpStingDirt = new MoCItemWeapon("scorpstingdirt", STING, 1);
    public static final MoCItemWeapon scorpStingUndead = new MoCItemWeapon("scorpstingundead", STING, 5);
    // Armor
    static ArmorMaterial crocARMOR = EnumHelper.addArmorMaterial("crocARMOR", "crocARMOR", 10, new int[]{1, 3, 4, 1}, 17, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 1.0F).setRepairItem(new ItemStack(hideCroc));
    public static final MoCItemArmor plateCroc = new MoCItemArmor("reptileplate", crocARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor helmetCroc = new MoCItemArmor("reptilehelmet", crocARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor legsCroc = new MoCItemArmor("reptilelegs", crocARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor bootsCroc = new MoCItemArmor("reptileboots", crocARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial scorpdARMOR = EnumHelper.addArmorMaterial("scorpdARMOR", "scorpdARMOR", 18, new int[]{2, 6, 7, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.0F).setRepairItem(new ItemStack(chitin));
    public static final MoCItemArmor scorpPlateDirt = new MoCItemArmor("scorpplatedirt", scorpdARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor scorpHelmetDirt = new MoCItemArmor("scorphelmetdirt", scorpdARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor scorpLegsDirt = new MoCItemArmor("scorplegsdirt", scorpdARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor scorpBootsDirt = new MoCItemArmor("scorpbootsdirt", scorpdARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial scorpfARMOR = EnumHelper.addArmorMaterial("scorpfARMOR", "scorpfARMOR", 18, new int[]{2, 6, 7, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.0F).setRepairItem(new ItemStack(chitinFrost));
    public static final MoCItemArmor scorpPlateFrost = new MoCItemArmor("scorpplatefrost", scorpfARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor scorpHelmetFrost = new MoCItemArmor("scorphelmetfrost", scorpfARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor scorpLegsFrost = new MoCItemArmor("scorplegsfrost", scorpfARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor scorpBootsFrost = new MoCItemArmor("scorpbootsfrost", scorpfARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial scorpnARMOR = EnumHelper.addArmorMaterial("scorpnARMOR", "scorpnARMOR", 18, new int[]{2, 6, 7, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.0F).setRepairItem(new ItemStack(chitinNether));
    public static final MoCItemArmor scorpPlateNether = new MoCItemArmor("scorpplatenether", scorpnARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor scorpHelmetNether = new MoCItemArmor("scorphelmetnether", scorpnARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor scorpLegsNether = new MoCItemArmor("scorplegsnether", scorpnARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor scorpBootsNether = new MoCItemArmor("scorpbootsnether", scorpnARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial scorpcARMOR = EnumHelper.addArmorMaterial("scorpcARMOR", "scorpcARMOR", 18, new int[]{2, 6, 7, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.0F).setRepairItem(new ItemStack(chitinCave));
    public static final MoCItemArmor scorpPlateCave = new MoCItemArmor("scorpplatecave", scorpcARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor scorpHelmetCave = new MoCItemArmor("scorphelmetcave", scorpcARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor scorpLegsCave = new MoCItemArmor("scorplegscave", scorpcARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor scorpBootsCave = new MoCItemArmor("scorpbootscave", scorpcARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial scorpuARMOR = EnumHelper.addArmorMaterial("scorpuARMOR", "scorpuARMOR", 18, new int[]{2, 6, 7, 2}, 16, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 2.0F).setRepairItem(new ItemStack(chitinUndead));
    public static final MoCItemArmor scorpPlateUndead = new MoCItemArmor("scorpplateundead", scorpuARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor scorpHelmetUndead = new MoCItemArmor("scorphelmetundead", scorpuARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor scorpLegsUndead = new MoCItemArmor("scorplegsundead", scorpuARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor scorpBootsUndead = new MoCItemArmor("scorpbootsundead", scorpuARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial furARMOR = EnumHelper.addArmorMaterial("furARMOR", "furARMOR", 4, new int[]{1, 2, 2, 1}, 15, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F).setRepairItem(new ItemStack(fur));
    public static final MoCItemLuckyArmor chestFur = new MoCItemLuckyArmor("furchest", 0.5F, furARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemLuckyArmor helmetFur = new MoCItemLuckyArmor("furhelmet", 0.5F, furARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemLuckyArmor legsFur = new MoCItemLuckyArmor("furlegs", 0.5F, furARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemLuckyArmor bootsFur = new MoCItemLuckyArmor("furboots", 0.5F, furARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial hideARMOR = EnumHelper.addArmorMaterial("hideARMOR", "hideARMOR", 8, new int[]{1, 3, 3, 1}, 18, SoundEvents.ITEM_ARMOR_EQUIP_LEATHER, 0.0F).setRepairItem(new ItemStack(animalHide));
    public static final MoCItemArmor chestHide = new MoCItemArmor("hidechest", hideARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor helmetHide = new MoCItemArmor("hidehelmet", hideARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor legsHide = new MoCItemArmor("hidelegs", hideARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor bootsHide = new MoCItemArmor("hideboots", hideARMOR, 4, EntityEquipmentSlot.FEET);
    static ArmorMaterial silverARMOR = EnumHelper.addArmorMaterial("silverARMOR", "silverARMOR", 15, new int[]{2, 6, 5, 2}, 22, SoundEvents.ITEM_ARMOR_EQUIP_GOLD, 1.5F);
    public static final MoCItemArmor chestSilver = new MoCItemArmor("ancient_silver_chestplate", silverARMOR, 4, EntityEquipmentSlot.CHEST);
    public static final MoCItemArmor helmetSilver = new MoCItemArmor("ancient_silver_helmet", silverARMOR, 4, EntityEquipmentSlot.HEAD);
    public static final MoCItemArmor legsSilver = new MoCItemArmor("ancient_silver_leggings", silverARMOR, 4, EntityEquipmentSlot.LEGS);
    public static final MoCItemArmor bootsSilver = new MoCItemArmor("ancient_silver_boots", silverARMOR, 4, EntityEquipmentSlot.FEET);

    @SubscribeEvent
    @SideOnly(Side.CLIENT)
    public static void registerRenders(final ModelRegistryEvent modelRegistryEvent) {
        // All bow items go here
        ModelLoader.setCustomModelResourceLocation(silverBow, 0, new ModelResourceLocation(silverBow.delegate.name(), "inventory"));
    }

    @Mod.EventBusSubscriber(modid = MoCConstants.MOD_ID)
    public static class RegistrationHandler {
        /**
         * Register this mod's {@link Item}s.
         *
         * @param event The event
         */
        @SubscribeEvent
        public static void registerItems(final RegistryEvent.Register<Item> event) {
            List<Item> items = new ArrayList<>(Arrays.asList(
                    sharkteeth,
                    sugarlump,
                    mocegg,
                    bigcatclaw,
                    medallion,
                    litterbox,
                    woolball,
                    petfood,
                    hideCroc,
                    helmetCroc,
                    plateCroc,
                    legsCroc,
                    bootsCroc,
                    fur,
                    omelet,
                    turtleraw,
                    turtlecooked,
                    turtlesoup,
                    staffPortal,

                    ancientSilverScrap,
                    ancientSilverIngot,
                    ancientSilverNugget,
                    fineSilverIngot,
                    fineSilverNugget,
                    helmetSilver,
                    chestSilver,
                    legsSilver,
                    bootsSilver,
                    firestoneChunk,

                    whip,
                    reptileWhip,
                    nunchaku,
                    sai,
                    bo,
                    katana,
                    sharksword,
                    sharkMattock,
                    sharkaxe,
                    silversword,
                    silverMattock,
                    silveraxe,
                    silverBow,

                    essencedarkness,
                    essenceEternal,
                    essencefire,
                    essenceIce,
                    essenceundead,
                    essencelight,

                    amuletbone,
                    amuletbonefull,
                    amuletghost,
                    amuletghostfull,
                    amuletfairy,
                    amuletfairyfull,
                    amuletpegasus,
                    amuletpegasusfull,
                    fishnet,
                    fishnetfull,
                    petamulet,
                    petamuletfull,
                    petamuletuniversal,

                    helmetFur,
                    chestFur,
                    legsFur,
                    bootsFur,

                    heartdarkness,
                    heartEternal,
                    heartfire,
                    heartIce,
                    heartundead,
                    ostrichraw,
                    ostrichcooked,
                    unicornhorn,
                    horsearmorcrystal,
                    mysticPear,
                    recordshuffle,
                    wyvwoodDoor,

                    animalHide,
                    rawTurkey,
                    cookedTurkey,
                    duckRaw,
                    duckCooked,
                    helmetHide,
                    chestHide,
                    legsHide,
                    bootsHide,
                    ratRaw,
                    ratCooked,
                    ratBurger,
                    venisonRaw,
                    venisonCooked,

                    chitinCave,
                    chitin,
                    chitinNether,
                    chitinFrost,
                    chitinUndead,

                    scorpSwordCave,
                    scorpMattockCave,
                    scorpAxeCave,
                    scorpSwordDirt,
                    scorpMattockDirt,
                    scorpAxeDirt,
                    scorpSwordNether,
                    scorpMattockNether,
                    scorpAxeNether,
                    scorpSwordFrost,
                    scorpMattockFrost,
                    scorpAxeFrost,
                    scorpSwordUndead,
                    scorpMattockUndead,
                    scorpAxeUndead,

                    scorpHelmetCave,
                    scorpPlateCave,
                    scorpLegsCave,
                    scorpBootsCave,
                    scorpHelmetDirt,
                    scorpPlateDirt,
                    scorpLegsDirt,
                    scorpBootsDirt,
                    scorpHelmetNether,
                    scorpPlateNether,
                    scorpLegsNether,
                    scorpBootsNether,
                    scorpHelmetFrost,
                    scorpPlateFrost,
                    scorpLegsFrost,
                    scorpBootsFrost,
                    scorpHelmetUndead,
                    scorpPlateUndead,
                    scorpLegsUndead,
                    scorpBootsUndead,

                    scorpStingCave,
                    scorpStingDirt,
                    scorpStingNether,
                    scorpStingFrost,
                    scorpStingUndead,

                    tusksWood,
                    tusksIron,
                    tusksDiamond,
                    elephantChest,
                    elephantGarment,
                    elephantHarness,
                    elephantHowdah,
                    mammothPlatform,

                    scrollOfFreedom,
                    scrollOfSale,
                    scrollOfRenaming,
                    crabraw,
                    crabcooked
                    //brackishClaw
            ));

            final IForgeRegistry<Item> registry = event.getRegistry();

            for (int i = 0; i < 16; i++) {
                String s = EnumDyeColor.byMetadata(i).getTranslationKey().toLowerCase();
                if (s.equalsIgnoreCase("lightBlue")) s = "light_blue";
                kittybed[i] = new MoCItemKittyBed("kittybed_" + s, i);
                registry.register(kittybed[i]);
                if (!MoCreatures.isServer()) {
                    ModelLoader.setCustomModelResourceLocation(kittybed[i], 0,
                            new ModelResourceLocation(MoCConstants.MOD_PREFIX + kittybed[i].getTranslationKey().replace("item.", ""), "inventory"));
                }
            }

            for (final Item item : items) {
                registry.register(item);
                ITEMS.add(item);
                if (!MoCreatures.isServer()) {
                    ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(MoCConstants.MOD_PREFIX + item.getTranslationKey().replace("item.",
                            ""), "inventory"));
                }
                if (item instanceof MoCItemEgg) {
                    for (int i = 0; i < 91; i++) {
                        if (!MoCreatures.isServer()) {
                            ModelLoader.setCustomModelResourceLocation(item, i, new ModelResourceLocation(MoCConstants.MOD_PREFIX + "mocegg", "inventory"));
                        }
                    }
                }
            }
        }
    }
}
