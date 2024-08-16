/*
 * GNU GENERAL PUBLIC LICENSE Version 3
 */
package drzhark.mocreatures.init;

import drzhark.mocreatures.MoCConstants;
import drzhark.mocreatures.MoCreatures;
import drzhark.mocreatures.config.RareItemLootTableRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetCount;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

// TODO: Ents, Elephants, Horses, Horse Mobs, Snakes
@Mod.EventBusSubscriber(modid = MoCConstants.MOD_ID)
public class MoCLootTables {
    // Ambient - All of these are empty, they're mainly for modpack developers
    public static final ResourceLocation ANT = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/ant");
    public static final ResourceLocation BEE = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/bee");
    public static final ResourceLocation BUTTERFLY = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/butterfly");
    public static final ResourceLocation CRICKET = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/cricket");
    public static final ResourceLocation DRAGONFLY = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/dragonfly");
    public static final ResourceLocation FIREFLY = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/firefly");
    public static final ResourceLocation FLY = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/fly");
    public static final ResourceLocation GRASSHOPPER = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/grasshopper");
    public static final ResourceLocation MAGGOT = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/maggot");
    public static final ResourceLocation ROACH = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/roach");
    public static final ResourceLocation SNAIL = new ResourceLocation(MoCConstants.MOD_ID, "entities/ambient/snail");
    // Animals
    public static final ResourceLocation BIRD = new ResourceLocation(MoCConstants.MOD_ID, "entities/bird");
    public static final ResourceLocation BOAR = new ResourceLocation(MoCConstants.MOD_ID, "entities/boar");
    public static final ResourceLocation BUNNY = new ResourceLocation(MoCConstants.MOD_ID, "entities/bunny");
    public static final ResourceLocation CROCODILE = new ResourceLocation(MoCConstants.MOD_ID, "entities/crocodile");
    public static final ResourceLocation DEER = new ResourceLocation(MoCConstants.MOD_ID, "entities/deer");
    public static final ResourceLocation DUCK = new ResourceLocation(MoCConstants.MOD_ID, "entities/duck");
    public static final ResourceLocation FOX = new ResourceLocation(MoCConstants.MOD_ID, "entities/fox");
    public static final ResourceLocation GOAT = new ResourceLocation(MoCConstants.MOD_ID, "entities/goat");
    public static final ResourceLocation KITTY = new ResourceLocation(MoCConstants.MOD_ID, "entities/kitty");
    public static final ResourceLocation KOMODO_DRAGON = new ResourceLocation(MoCConstants.MOD_ID, "entities/komodo_dragon");
    public static final ResourceLocation MOLE = new ResourceLocation(MoCConstants.MOD_ID, "entities/mole");
    public static final ResourceLocation OSTRICH = new ResourceLocation(MoCConstants.MOD_ID, "entities/ostrich");
    public static final ResourceLocation RACCOON = new ResourceLocation(MoCConstants.MOD_ID, "entities/raccoon");
    public static final ResourceLocation TURKEY = new ResourceLocation(MoCConstants.MOD_ID, "entities/turkey");
    public static final ResourceLocation TURTLE = new ResourceLocation(MoCConstants.MOD_ID, "entities/turtle");
    public static final ResourceLocation WILD_WOLF = new ResourceLocation(MoCConstants.MOD_ID, "entities/wild_wolf");
    // Bears
    public static final ResourceLocation BLACK_BEAR = new ResourceLocation(MoCConstants.MOD_ID, "entities/black_bear");
    public static final ResourceLocation GRIZZLY_BEAR = new ResourceLocation(MoCConstants.MOD_ID, "entities/grizzly_bear");
    public static final ResourceLocation PANDA_BEAR = new ResourceLocation(MoCConstants.MOD_ID, "entities/panda_bear");
    public static final ResourceLocation POLAR_BEAR = new ResourceLocation(MoCConstants.MOD_ID, "entities/polar_bear");
    // Big Cats
    public static final ResourceLocation LEOGER = new ResourceLocation(MoCConstants.MOD_ID, "entities/leoger");
    public static final ResourceLocation LEOPARD = new ResourceLocation(MoCConstants.MOD_ID, "entities/leopard");
    public static final ResourceLocation LIARD = new ResourceLocation(MoCConstants.MOD_ID, "entities/liard");
    public static final ResourceLocation LIGER = new ResourceLocation(MoCConstants.MOD_ID, "entities/liger");
    public static final ResourceLocation LION = new ResourceLocation(MoCConstants.MOD_ID, "entities/lion");
    public static final ResourceLocation LITHER = new ResourceLocation(MoCConstants.MOD_ID, "entities/lither");
    public static final ResourceLocation PANTHARD = new ResourceLocation(MoCConstants.MOD_ID, "entities/panthard");
    public static final ResourceLocation PANTHER = new ResourceLocation(MoCConstants.MOD_ID, "entities/panther");
    public static final ResourceLocation PANTHGER = new ResourceLocation(MoCConstants.MOD_ID, "entities/panthger");
    public static final ResourceLocation TIGER = new ResourceLocation(MoCConstants.MOD_ID, "entities/tiger");
    // Dragons
    public static final ResourceLocation WYVERN = new ResourceLocation(MoCConstants.MOD_ID, "entities/wyvern"); // Currently empty as a placeholder
    // Filch Lizard
    public static final ResourceLocation FILCH_LIZARD = new ResourceLocation(MoCConstants.MOD_ID, "entities/filch_lizard/filch_lizard"); // Currently empty as a placeholder
    public static final ResourceLocation FILCH_LIZARD_SPAWN = new ResourceLocation(MoCConstants.MOD_ID, "entities/filch_lizard/filch_lizard_spawn");
    public static final ResourceLocation FILCH_LIZARD_STEAL = new ResourceLocation(MoCConstants.MOD_ID, "entities/filch_lizard/filch_lizard_steal");
    // Golems
    public static final ResourceLocation BIG_GOLEM = new ResourceLocation(MoCConstants.MOD_ID, "entities/big_golem");
    public static final ResourceLocation MINI_GOLEM = new ResourceLocation(MoCConstants.MOD_ID, "entities/mini_golem");
    // Manticores
    public static final ResourceLocation DARK_MANTICORE = new ResourceLocation(MoCConstants.MOD_ID, "entities/dark_manticore");
    public static final ResourceLocation FIRE_MANTICORE = new ResourceLocation(MoCConstants.MOD_ID, "entities/fire_manticore");
    public static final ResourceLocation FROST_MANTICORE = new ResourceLocation(MoCConstants.MOD_ID, "entities/frost_manticore");
    public static final ResourceLocation PLAIN_MANTICORE = new ResourceLocation(MoCConstants.MOD_ID, "entities/plain_manticore");
    public static final ResourceLocation TOXIC_MANTICORE = new ResourceLocation(MoCConstants.MOD_ID, "entities/toxic_manticore");
    // Ogres
    public static final ResourceLocation CAVE_OGRE = new ResourceLocation(MoCConstants.MOD_ID, "entities/cave_ogre");
    public static final ResourceLocation FIRE_OGRE = new ResourceLocation(MoCConstants.MOD_ID, "entities/fire_ogre");
    public static final ResourceLocation GREEN_OGRE = new ResourceLocation(MoCConstants.MOD_ID, "entities/green_ogre");
    // Rodents
    public static final ResourceLocation HELL_RAT = new ResourceLocation(MoCConstants.MOD_ID, "entities/hell_rat");
    public static final ResourceLocation MOUSE = new ResourceLocation(MoCConstants.MOD_ID, "entities/mouse");
    public static final ResourceLocation RAT = new ResourceLocation(MoCConstants.MOD_ID, "entities/rat");
    // Sea Creatures
    public static final ResourceLocation ANCHOVY = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/anchovy");
    public static final ResourceLocation ANGELFISH = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/angelfish");
    public static final ResourceLocation ANGLERFISH = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/anglerfish");
    public static final ResourceLocation BASS = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/bass");
    public static final ResourceLocation CLOWNFISH = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/clownfish");
    public static final ResourceLocation COD = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/cod");
    public static final ResourceLocation CRAB = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/crab");
    public static final ResourceLocation DOLPHIN = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/dolphin");
    public static final ResourceLocation FISHY = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/fishy");
    public static final ResourceLocation GOLDFISH = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/goldfish");
    public static final ResourceLocation HIPPO_TANG = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/hippo_tang");
    public static final ResourceLocation JELLYFISH = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/jellyfish");
    public static final ResourceLocation MANDARINFISH = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/mandarinfish");
    public static final ResourceLocation MANTA_RAY = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/manta_ray"); // Currently empty as a placeholder
    public static final ResourceLocation PIRANHA = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/piranha");
    public static final ResourceLocation SALMON = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/salmon");
    public static final ResourceLocation SHARK = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/shark");
    public static final ResourceLocation STINGRAY = new ResourceLocation(MoCConstants.MOD_ID, "entities/aquatic/stingray"); // Currently empty as a placeholder
    // Scorpions
    public static final ResourceLocation CAVE_SCORPION = new ResourceLocation(MoCConstants.MOD_ID, "entities/cave_scorpion");
    public static final ResourceLocation DIRT_SCORPION = new ResourceLocation(MoCConstants.MOD_ID, "entities/dirt_scorpion");
    public static final ResourceLocation FIRE_SCORPION = new ResourceLocation(MoCConstants.MOD_ID, "entities/fire_scorpion");
    public static final ResourceLocation FROST_SCORPION = new ResourceLocation(MoCConstants.MOD_ID, "entities/frost_scorpion");
    public static final ResourceLocation UNDEAD_SCORPION = new ResourceLocation(MoCConstants.MOD_ID, "entities/undead_scorpion");
    // Undead
    public static final ResourceLocation FLAME_WRAITH = new ResourceLocation(MoCConstants.MOD_ID, "entities/flame_wraith");
    public static final ResourceLocation SILVER_SKELETON = new ResourceLocation(MoCConstants.MOD_ID, "entities/silver_skeleton");
    public static final ResourceLocation WRAITH = new ResourceLocation(MoCConstants.MOD_ID, "entities/wraith");
    // Werewolves
    public static final ResourceLocation WEREHUMAN = new ResourceLocation(MoCConstants.MOD_ID, "entities/werehuman");
    public static final ResourceLocation WEREWOLF = new ResourceLocation(MoCConstants.MOD_ID, "entities/werewolf");

    // Rare Drops Overrides
    public static final ResourceLocation COMMON_RARE_DROPS = new ResourceLocation(MoCConstants.MOD_ID, "entities/rare_drops/common");
    public static final ResourceLocation UNCOMMON_RARE_DROPS = new ResourceLocation(MoCConstants.MOD_ID, "entities/rare_drops/uncommon");
    public static final ResourceLocation RARE_RARE_DROPS = new ResourceLocation(MoCConstants.MOD_ID, "entities/rare_drops/rare");

    @SubscribeEvent
    public static void onLootTableLoad(LootTableLoadEvent event) {
        LootPool main = event.getTable().getPool("main");

        RareItemLootTableRarity raritySetting = MoCreatures.proxy.rareItemLootTableRarity;
        ResourceLocation baseTable = event.getName();
        ResourceLocation rareItemsTable = null;
        if (raritySetting == RareItemLootTableRarity.COMMON) {
            rareItemsTable = COMMON_RARE_DROPS;
        } else if (raritySetting == RareItemLootTableRarity.UNCOMMON) {
            rareItemsTable = UNCOMMON_RARE_DROPS;
        } else if (raritySetting == RareItemLootTableRarity.RARE) {
            rareItemsTable = RARE_RARE_DROPS;
        }
        if (main == null || rareItemsTable == null) {
            return;
        }
        LootPool rareItemsPool = event.getLootTableManager().getLootTableFromLocation(rareItemsTable).getPool("main");



        if (baseTable.equals(LootTableList.CHESTS_ABANDONED_MINESHAFT) || baseTable.equals(LootTableList.CHESTS_SIMPLE_DUNGEON) || baseTable.equals(LootTableList.CHESTS_DESERT_PYRAMID) || baseTable.equals(LootTableList.CHESTS_JUNGLE_TEMPLE) || baseTable.equals(LootTableList.CHESTS_IGLOO_CHEST) || baseTable.equals(LootTableList.CHESTS_END_CITY_TREASURE) || baseTable.equals(LootTableList.CHESTS_NETHER_BRIDGE) || baseTable.equals(LootTableList.CHESTS_STRONGHOLD_LIBRARY) || baseTable.equals(LootTableList.CHESTS_STRONGHOLD_CROSSING) || baseTable.equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR) || baseTable.equals(LootTableList.CHESTS_VILLAGE_BLACKSMITH) || baseTable.equals(LootTableList.CHESTS_WOODLAND_MANSION)) {
            main.addEntry(new LootEntryItem(new ItemStack(MoCItems.ancientSilverScrap).getItem(), 5, 0, new LootFunction[]{new SetCount(new LootCondition[0], new RandomValueRange(1, 2))}, new LootCondition[0], "loottable:ancient_silver_scrap"));
        }

        // add some rare items dynamically to the loot table, based on the mob
        if (event.getName().equals(DARK_MANTICORE) || event.getName().equals(WEREWOLF)) {
            // get entry from drops based on raritySetting
            main.addEntry(rareItemsPool.getEntry("heartdarkness"));
        }
    }
}
