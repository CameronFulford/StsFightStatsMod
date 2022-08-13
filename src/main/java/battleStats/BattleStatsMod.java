package battleStats;

import basemod.BaseMod;
import basemod.ModLabeledToggleButton;
import basemod.ModPanel;
import basemod.abstracts.CustomSavableRaw;
import basemod.eventUtil.AddEventParams;
import basemod.interfaces.*;
import basemod.patches.com.megacrit.cardcrawl.characters.AbstractPlayer.OnPlayerDamagedHook;
import battleStats.characters.TheDefault;
import battleStats.events.IdentityCrisisEvent;
import battleStats.model.CombatStats;
import battleStats.model.EnemyCombatStats;
import battleStats.model.FightTracker;
import battleStats.potions.PlaceholderPotion;
import battleStats.stats.StatsStore;
import battleStats.stats.StatsUtils;
import battleStats.util.IDCheckDontTouchPls;
import battleStats.util.TextureLoader;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.evacipated.cardcrawl.modthespire.lib.SpireConfig;
import com.evacipated.cardcrawl.modthespire.lib.SpireInitializer;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.megacrit.cardcrawl.actions.GameActionManager;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.dungeons.TheCity;
import com.megacrit.cardcrawl.helpers.CardHelper;
import com.megacrit.cardcrawl.helpers.FontHelper;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

//TODO: DON'T MASS RENAME/REFACTOR
//TODO: DON'T MASS RENAME/REFACTOR
//TODO: DON'T MASS RENAME/REFACTOR
//TODO: DON'T MASS RENAME/REFACTOR
// Please don't just mass replace "theDefault" with "yourMod" everywhere.
// It'll be a bigger pain for you. You only need to replace it in 4 places.
// I comment those places below, under the place where you set your ID.

//TODO: FIRST THINGS FIRST: RENAME YOUR PACKAGE AND ID NAMES FIRST-THING!!!
// Right click the package (Open the project pane on the left. Folder with black dot on it. The name's at the very top) -> Refactor -> Rename, and name it whatever you wanna call your mod.
// Scroll down in this file. Change the ID from "theDefault:" to "yourModName:" or whatever your heart desires (don't use spaces). Dw, you'll see it.
// In the JSON strings (resources>localization>eng>[all them files] make sure they all go "yourModName:" rather than "theDefault", and change to "yourmodname" rather than "thedefault".
// You can ctrl+R to replace in 1 file, or ctrl+shift+r to mass replace in specific files/directories, and press alt+c to make the replace case sensitive (Be careful.).
// Start with the DefaultCommon cards - they are the most commented cards since I don't feel it's necessary to put identical comments on every card.
// After you sorta get the hang of how to make cards, check out the card template which will make your life easier

/*
 * With that out of the way:
 * Welcome to this super over-commented Slay the Spire modding base.
 * Use it to make your own mod of any type. - If you want to add any standard in-game content (character,
 * cards, relics), this is a good starting point.
 * It features 1 character with a minimal set of things: 1 card of each type, 1 debuff, couple of relics, etc.
 * If you're new to modding, you basically *need* the BaseMod wiki for whatever you wish to add
 * https://github.com/daviscook477/BaseMod/wiki - work your way through with this base.
 * Feel free to use this in any way you like, of course. MIT licence applies. Happy modding!
 *
 * And pls. Read the comments.
 */

@SpireInitializer
public class BattleStatsMod extends OnPlayerDamagedHook implements
        EditCardsSubscriber,
        EditRelicsSubscriber,
        EditStringsSubscriber,
        EditKeywordsSubscriber,
        EditCharactersSubscriber,
        PostInitializeSubscriber,
        OnStartBattleSubscriber,
        OnPlayerTurnStartSubscriber,
        OnPlayerDamagedSubscriber,
        PostDeathSubscriber,
        PostUpdateSubscriber,
        PreUpdateSubscriber,
        PostBattleSubscriber,
        RenderSubscriber,
        CustomSavableRaw {
    // Make sure to implement the subscribers *you* are using (read basemod wiki). Editing cards? EditCardsSubscriber.
    // Making relics? EditRelicsSubscriber. etc., etc., for a full list and how to make your own, visit the basemod wiki.
    public static final Logger logger = LogManager.getLogger(BattleStatsMod.class.getName());
    private static String modID;

    // Mod-settings settings. This is if you want an on/off savable button
    public static Properties testModDefaultSettings = new Properties();
    public static final String ENABLE_PLACEHOLDER_SETTINGS = "enablePlaceholder";
    public static boolean enablePlaceholder = true; // The boolean we'll be setting on/off (true/false)

    //This is for the in-game mod settings panel.
    private static final String MODNAME = "Test Mod";
    private static final String AUTHOR = "Camputer"; // And pretty soon - You!
    private static final String DESCRIPTION = "A base for Slay the Spire to start your own mod from, feat. the Default.";
    
    // =============== INPUT TEXTURE LOCATION =================
    
    // Colors (RGB)
    // Character Color
    public static final Color DEFAULT_GRAY = CardHelper.getColor(64.0f, 70.0f, 70.0f);
    
    // Potion Colors in RGB
    public static final Color PLACEHOLDER_POTION_LIQUID = CardHelper.getColor(209.0f, 53.0f, 18.0f); // Orange-ish Red
    public static final Color PLACEHOLDER_POTION_HYBRID = CardHelper.getColor(255.0f, 230.0f, 230.0f); // Near White
    public static final Color PLACEHOLDER_POTION_SPOTS = CardHelper.getColor(100.0f, 25.0f, 10.0f); // Super Dark Red/Brown
    
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
    // ONCE YOU CHANGE YOUR MOD ID (BELOW, YOU CAN'T MISS IT) CHANGE THESE PATHS!!!!!!!!!!!
  
    // Card backgrounds - The actual rectangular card.
    private static final String ATTACK_DEFAULT_GRAY = "battleStatsResources/images/512/bg_attack_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY = "battleStatsResources/images/512/bg_skill_default_gray.png";
    private static final String POWER_DEFAULT_GRAY = "battleStatsResources/images/512/bg_power_default_gray.png";
    
    private static final String ENERGY_ORB_DEFAULT_GRAY = "battleStatsResources/images/512/card_default_gray_orb.png";
    private static final String CARD_ENERGY_ORB = "battleStatsResources/images/512/card_small_orb.png";
    
    private static final String ATTACK_DEFAULT_GRAY_PORTRAIT = "battleStatsResources/images/1024/bg_attack_default_gray.png";
    private static final String SKILL_DEFAULT_GRAY_PORTRAIT = "battleStatsResources/images/1024/bg_skill_default_gray.png";
    private static final String POWER_DEFAULT_GRAY_PORTRAIT = "battleStatsResources/images/1024/bg_power_default_gray.png";
    private static final String ENERGY_ORB_DEFAULT_GRAY_PORTRAIT = "battleStatsResources/images/1024/card_default_gray_orb.png";
    
    // Character assets
    private static final String THE_DEFAULT_BUTTON = "battleStatsResources/images/charSelect/DefaultCharacterButton.png";
    private static final String THE_DEFAULT_PORTRAIT = "battleStatsResources/images/charSelect/DefaultCharacterPortraitBG.png";
    public static final String THE_DEFAULT_SHOULDER_1 = "battleStatsResources/images/char/defaultCharacter/shoulder.png";
    public static final String THE_DEFAULT_SHOULDER_2 = "battleStatsResources/images/char/defaultCharacter/shoulder2.png";
    public static final String THE_DEFAULT_CORPSE = "battleStatsResources/images/char/defaultCharacter/corpse.png";
    
    //Mod Badge - A small icon that appears in the mod settings menu next to your mod.
    public static final String BADGE_IMAGE = "battleStatsResources/images/Badge.png";
    
    // Atlas and JSON files for the Animations
    public static final String THE_DEFAULT_SKELETON_ATLAS = "battleStatsResources/images/char/defaultCharacter/skeleton.atlas";
    public static final String THE_DEFAULT_SKELETON_JSON = "battleStatsResources/images/char/defaultCharacter/skeleton.json";

    public static final String MOD_ID = "battleStats";
    // Track the details of the current fight.
    public static FightTracker fightTracker = new FightTracker();
    public static StatsStore statsStore = new StatsStore();
    public static EnemyCombatStats battleStats;
    public static boolean inBattle = false;
    public static SpireConfig configData;
    public static final String CONFIG_FILE_NAME = "config_data";

    // =============== MAKE IMAGE PATHS =================
    
    public static String makeCardPath(String resourcePath) {
        return getModID() + "Resources/images/cards/" + resourcePath;
    }
    
    public static String makeRelicPath(String resourcePath) {
        return getModID() + "Resources/images/relics/" + resourcePath;
    }
    
    public static String makeRelicOutlinePath(String resourcePath) {
        return getModID() + "Resources/images/relics/outline/" + resourcePath;
    }
    
    public static String makeOrbPath(String resourcePath) {
        return getModID() + "Resources/images/orbs/" + resourcePath;
    }
    
    public static String makePowerPath(String resourcePath) {
        return getModID() + "Resources/images/powers/" + resourcePath;
    }
    
    public static String makeEventPath(String resourcePath) {
        return getModID() + "Resources/images/events/" + resourcePath;
    }
    
    // =============== /MAKE IMAGE PATHS/ =================
    
    // =============== /INPUT TEXTURE LOCATION/ =================
    
    
    // =============== SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE =================
    
    public BattleStatsMod() {
        logger.info("Subscribe to BaseMod hooks");
        
        BaseMod.subscribe(this);
        
      /*
           (   ( /(  (     ( /( (            (  `   ( /( )\ )    )\ ))\ )
           )\  )\()) )\    )\()))\ )   (     )\))(  )\()|()/(   (()/(()/(
         (((_)((_)((((_)( ((_)\(()/(   )\   ((_)()\((_)\ /(_))   /(_))(_))
         )\___ _((_)\ _ )\ _((_)/(_))_((_)  (_()((_) ((_|_))_  _(_))(_))_
        ((/ __| || (_)_\(_) \| |/ __| __| |  \/  |/ _ \|   \  |_ _||   (_)
         | (__| __ |/ _ \ | .` | (_ | _|  | |\/| | (_) | |) |  | | | |) |
          \___|_||_/_/ \_\|_|\_|\___|___| |_|  |_|\___/|___/  |___||___(_)
      */
      
        setModID(MOD_ID);
        // cool
        // TODO: NOW READ THIS!!!!!!!!!!!!!!!:
        
        // 1. Go to your resources folder in the project panel, and refactor> rename theDefaultResources to
        // yourModIDResources.
        
        // 2. Click on the localization > eng folder and press ctrl+shift+r, then select "Directory" (rather than in Project) and press alt+c (or mark the match case option)
        // replace all instances of theDefault with yourModID, and all instances of thedefault with yourmodid (the same but all lowercase).
        // Because your mod ID isn't the default. Your cards (and everything else) should have Your mod id. Not mine.
        // It's important that the mod ID prefix for keywords used in the cards descriptions is lowercase!

        // 3. Scroll down (or search for "ADD CARDS") till you reach the ADD CARDS section, and follow the TODO instructions

        // 4. FINALLY and most importantly: Scroll up a bit. You may have noticed the image locations above don't use getModID()
        // Change their locations to reflect your actual ID rather than theDefault. They get loaded before getID is a thing.
        
        logger.info("Done subscribing");
        
        logger.info("Creating the color " + TheDefault.Enums.COLOR_GRAY.toString());
        
        BaseMod.addColor(TheDefault.Enums.COLOR_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY,
                DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY, DEFAULT_GRAY,
                ATTACK_DEFAULT_GRAY, SKILL_DEFAULT_GRAY, POWER_DEFAULT_GRAY, ENERGY_ORB_DEFAULT_GRAY,
                ATTACK_DEFAULT_GRAY_PORTRAIT, SKILL_DEFAULT_GRAY_PORTRAIT, POWER_DEFAULT_GRAY_PORTRAIT,
                ENERGY_ORB_DEFAULT_GRAY_PORTRAIT, CARD_ENERGY_ORB);
        
        logger.info("Done creating the color");
        
        
        logger.info("Adding mod settings");
        // This loads the mod settings.
        // The actual mod Button is added below in receivePostInitialize()
        testModDefaultSettings.setProperty(ENABLE_PLACEHOLDER_SETTINGS, "FALSE"); // This is the default setting. It's actually set...
        try {
            SpireConfig config = new SpireConfig("defaultMod", "testModConfig", testModDefaultSettings); // ...right here
            // the "fileName" parameter is the name of the file MTS will create where it will save our setting.
            config.load(); // Load the setting and set the boolean to equal it
            enablePlaceholder = config.getBool(ENABLE_PLACEHOLDER_SETTINGS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Done adding mod settings");
        
    }
    
    // ====== NO EDIT AREA ======
    // DON'T TOUCH THIS STUFF. IT IS HERE FOR STANDARDIZATION BETWEEN MODS AND TO ENSURE GOOD CODE PRACTICES.
    // IF YOU MODIFY THIS I WILL HUNT YOU DOWN AND DOWNVOTE YOUR MOD ON WORKSHOP
    
    public static void setModID(String ID) { // DON'T EDIT
        Gson coolG = new Gson(); // EY DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i hate u Gdx.files
        InputStream in = BattleStatsMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THIS ETHER
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // OR THIS, DON'T EDIT IT
        logger.info("You are attempting to set your mod ID as: " + ID); // NO WHY
        if (ID.equals(EXCEPTION_STRINGS.DEFAULTID)) { // DO *NOT* CHANGE THIS ESPECIALLY, TO EDIT YOUR MOD ID, SCROLL UP JUST A LITTLE, IT'S JUST ABOVE
            throw new RuntimeException(EXCEPTION_STRINGS.EXCEPTION); // THIS ALSO DON'T EDIT
        } else if (ID.equals(EXCEPTION_STRINGS.DEVID)) { // NO
            modID = EXCEPTION_STRINGS.DEFAULTID; // DON'T
        } else { // NO EDIT AREA
            modID = ID; // DON'T WRITE OR CHANGE THINGS HERE NOT EVEN A LITTLE
        } // NO
        logger.info("Success! ID is " + modID); // WHY WOULD U WANT IT NOT TO LOG?? DON'T EDIT THIS.
    } // NO
    
    public static String getModID() { // NO
        return modID; // DOUBLE NO
    } // NU-UH
    
    private static void pathCheck() { // ALSO NO
        Gson coolG = new Gson(); // NOPE DON'T EDIT THIS
        //   String IDjson = Gdx.files.internal("IDCheckStringsDONT-EDIT-AT-ALL.json").readString(String.valueOf(StandardCharsets.UTF_8)); // i still hate u btw Gdx.files
        InputStream in = BattleStatsMod.class.getResourceAsStream("/IDCheckStringsDONT-EDIT-AT-ALL.json"); // DON'T EDIT THISSSSS
        IDCheckDontTouchPls EXCEPTION_STRINGS = coolG.fromJson(new InputStreamReader(in, StandardCharsets.UTF_8), IDCheckDontTouchPls.class); // NAH, NO EDIT
        String packageName = BattleStatsMod.class.getPackage().getName(); // STILL NO EDIT ZONE
        FileHandle resourcePathExists = Gdx.files.internal(getModID() + "Resources"); // PLEASE DON'T EDIT THINGS HERE, THANKS
        if (!modID.equals(EXCEPTION_STRINGS.DEVID)) { // LEAVE THIS EDIT-LESS
            if (!packageName.equals(getModID())) { // NOT HERE ETHER
                throw new RuntimeException(EXCEPTION_STRINGS.PACKAGE_EXCEPTION + getModID()); // THIS IS A NO-NO
            } // WHY WOULD U EDIT THIS
            if (!resourcePathExists.exists()) { // DON'T CHANGE THIS
                throw new RuntimeException(EXCEPTION_STRINGS.RESOURCE_FOLDER_EXCEPTION + getModID() + "Resources"); // NOT THIS
            }// NO
        }// NO
    }// NO
    
    // ====== YOU CAN EDIT AGAIN ======
    
    
    public static void initialize() {
        try {
            logger.info("========================= Initializing Default Mod. Hi. =========================");
            BattleStatsMod battleStatsMod = new BattleStatsMod();
            logger.info("========================= /Default Mod Initialized. Hello World./ =========================");

            logger.info("Loading config data.");
            configData = new SpireConfig(MOD_ID, CONFIG_FILE_NAME);
            String configDataJson = configData.getString(CombatStats.FIGHT_STATS_MOD_JSON_KEY);
            if (configDataJson == null) {
                logger.info("No saved data. Instantiating fresh CombatStats.");
                statsStore.stats = new CombatStats();
            } else {
                logger.info("CombatStats config data found. Attempting to deserialize.");
                long start = System.currentTimeMillis();
                statsStore.stats = new Gson().fromJson(configDataJson, CombatStats.class);
                long end = System.currentTimeMillis();
                logger.info("Time to deserialize CombatStats: " + (end - start));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // ============== /SUBSCRIBE, CREATE THE COLOR_GRAY, INITIALIZE/ =================
    
    
    // =============== LOAD THE CHARACTER =================
    
    @Override
    public void receiveEditCharacters() {
        logger.info("Beginning to edit characters. " + "Add " + TheDefault.Enums.THE_DEFAULT.toString());
        
        logger.info("Added " + TheDefault.Enums.THE_DEFAULT.toString());
    }
    
    // =============== /LOAD THE CHARACTER/ =================
    
    
    // =============== POST-INITIALIZE =================
    
    @Override
    public void receivePostInitialize() {
        logger.info("Loading badge image and mod options");
        
        // Load the Mod Badge
        Texture badgeTexture = TextureLoader.getTexture(BADGE_IMAGE);
        
        // Create the Mod Menu
        ModPanel settingsPanel = new ModPanel();
        
        // Create the on/off button:
        ModLabeledToggleButton enableNormalsButton = new ModLabeledToggleButton("This is the text which goes next to the checkbox.",
                350.0f, 700.0f, Settings.CREAM_COLOR, FontHelper.charDescFont, // Position (trial and error it), color, font
                enablePlaceholder, // Boolean it uses
                settingsPanel, // The mod panel in which this button will be in
                (label) -> {}, // thing??????? idk
                (button) -> { // The actual button:
            
            enablePlaceholder = button.enabled; // The boolean true/false will be whether the button is enabled or not
            try {
                // And based on that boolean, set the settings and save them
                SpireConfig config = new SpireConfig("defaultMod", "testModConfig", testModDefaultSettings);
                config.setBool(ENABLE_PLACEHOLDER_SETTINGS, enablePlaceholder);
                config.save();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        
        settingsPanel.addUIElement(enableNormalsButton); // Add the button to the settings panel. Button is a go.
        
        BaseMod.registerModBadge(badgeTexture, MODNAME, AUTHOR, DESCRIPTION, settingsPanel);

        
        // =============== EVENTS =================
        // https://github.com/daviscook477/BaseMod/wiki/Custom-Events

        // You can add the event like so:
        // BaseMod.addEvent(IdentityCrisisEvent.ID, IdentityCrisisEvent.class, TheCity.ID);
        // Then, this event will be exclusive to the City (act 2), and will show up for all characters.
        // If you want an event that's present at any part of the game, simply don't include the dungeon ID

        // If you want to have more specific event spawning (e.g. character-specific or so)
        // deffo take a look at that basemod wiki link as well, as it explains things very in-depth
        // btw if you don't provide event type, normal is assumed by default

        // Create a new event builder
        // Since this is a builder these method calls (outside of create()) can be skipped/added as necessary
        AddEventParams eventParams = new AddEventParams.Builder(IdentityCrisisEvent.ID, IdentityCrisisEvent.class) // for this specific event
            .dungeonID(TheCity.ID) // The dungeon (act) this event will appear in
            .playerClass(TheDefault.Enums.THE_DEFAULT) // Character specific event
            .create();

        // Add the event
        BaseMod.addEvent(eventParams);

        // =============== /EVENTS/ =================
        logger.info("Done loading badge Image and mod options");


        // TODO: figure out how to properly load stats from a file.
        try {
            BaseMod.addSaveField(CombatStats.FIGHT_STATS_MOD_JSON_KEY, this);
//            Gson gson = new Gson();
//            String sourceStats = "{\"enemyCombatStatsMap\":{\"Cultist\":{\"The Silent\":{\"combatEnemyKey\":\"Cultist\",\"numCombats\":4,\"wins\":0,\"loss\":4,\"averageDamageTaken\":39.8706,\"averageDamageDealt\":14.15554,\"averageTurnsToWin\":1.9362346},\"The Watcher\":{\"combatEnemyKey\":\"Cultist\",\"numCombats\":1,\"wins\":0,\"loss\":1,\"averageDamageTaken\":13.711613,\"averageDamageDealt\":13.286872,\"averageTurnsToWin\":10.84672},\"The Defect\":{\"combatEnemyKey\":\"Cultist\",\"numCombats\":9,\"wins\":5,\"loss\":4,\"averageDamageTaken\":28.311348,\"averageDamageDealt\":12.114415,\"averageTurnsToWin\":6.437801},\"The Ironclad\":{\"combatEnemyKey\":\"Cultist\",\"numCombats\":2,\"wins\":0,\"loss\":2,\"averageDamageTaken\":16.327467,\"averageDamageDealt\":33.701447,\"averageTurnsToWin\":3.3450832}},\"2 Louse\":{\"The Silent\":{\"combatEnemyKey\":\"2 Louse\",\"numCombats\":1,\"wins\":1,\"loss\":0,\"averageDamageTaken\":20.29732,\"averageDamageDealt\":36.324097,\"averageTurnsToWin\":4.158781},\"The Watcher\":{\"combatEnemyKey\":\"2 Louse\",\"numCombats\":10,\"wins\":9,\"loss\":1,\"averageDamageTaken\":38.494812,\"averageDamageDealt\":42.39269,\"averageTurnsToWin\":10.586774},\"The Defect\":{\"combatEnemyKey\":\"2 Louse\",\"numCombats\":6,\"wins\":5,\"loss\":1,\"averageDamageTaken\":38.53638,\"averageDamageDealt\":11.857495,\"averageTurnsToWin\":5.7880707},\"The Ironclad\":{\"combatEnemyKey\":\"2 Louse\",\"numCombats\":8,\"wins\":6,\"loss\":2,\"averageDamageTaken\":49.592567,\"averageDamageDealt\":30.074987,\"averageTurnsToWin\":8.314305}},\"Jaw Worm\":{\"The Silent\":{\"combatEnemyKey\":\"Jaw Worm\",\"numCombats\":8,\"wins\":5,\"loss\":3,\"averageDamageTaken\":17.060352,\"averageDamageDealt\":28.809082,\"averageTurnsToWin\":9.067962},\"The Watcher\":{\"combatEnemyKey\":\"Jaw Worm\",\"numCombats\":6,\"wins\":2,\"loss\":4,\"averageDamageTaken\":34.089172,\"averageDamageDealt\":46.68964,\"averageTurnsToWin\":8.926318},\"The Defect\":{\"combatEnemyKey\":\"Jaw Worm\",\"numCombats\":12,\"wins\":9,\"loss\":3,\"averageDamageTaken\":29.26016,\"averageDamageDealt\":20.47667,\"averageTurnsToWin\":7.1529613},\"The Ironclad\":{\"combatEnemyKey\":\"Jaw Worm\",\"numCombats\":2,\"wins\":1,\"loss\":1,\"averageDamageTaken\":40.808006,\"averageDamageDealt\":40.9384,\"averageTurnsToWin\":5.1079755}},\"Small Slimes\":{\"The Silent\":{\"combatEnemyKey\":\"Small Slimes\",\"numCombats\":1,\"wins\":0,\"loss\":1,\"averageDamageTaken\":20.245722,\"averageDamageDealt\":46.489532,\"averageTurnsToWin\":4.9554553},\"The Watcher\":{\"combatEnemyKey\":\"Small Slimes\",\"numCombats\":10,\"wins\":6,\"loss\":4,\"averageDamageTaken\":18.777172,\"averageDamageDealt\":44.07,\"averageTurnsToWin\":8.005833},\"The Defect\":{\"combatEnemyKey\":\"Small Slimes\",\"numCombats\":1,\"wins\":1,\"loss\":0,\"averageDamageTaken\":45.131794,\"averageDamageDealt\":48.61703,\"averageTurnsToWin\":8.6862335},\"The Ironclad\":{\"combatEnemyKey\":\"Small Slimes\",\"numCombats\":8,\"wins\":7,\"loss\":1,\"averageDamageTaken\":14.210865,\"averageDamageDealt\":42.4738,\"averageTurnsToWin\":5.367388}}}}";
//            CombatStats combatStats = gson.fromJson(sourceStats, CombatStats.class);
//            statsStore.stats = new CombatStats(); // StatsUtils.generateTestStats();
        } catch (Exception e) {
            logger.error("Exception trying to deserialize CombatStats", e);
        } finally {
            if (statsStore.stats == null) {
                logger.info("Unable to load CombatStats. Generating dummy stats.");
                statsStore.stats = StatsUtils.generateTestStats();
            }
        }
        logger.info("Loaded CombatStats: " + statsStore.stats);
    }
    
    // =============== / POST-INITIALIZE/ =================
    
    // ================ ADD POTIONS ===================
    
    public void receiveEditPotions() {
        logger.info("Beginning to edit potions");
        
        logger.info("Done editing potions");
    }
    
    // ================ /ADD POTIONS/ ===================
    
    
    // ================ ADD RELICS ===================
    
    @Override
    public void receiveEditRelics() {
        logger.info("Adding relics");

        // Take a look at https://github.com/daviscook477/BaseMod/wiki/AutoAdd
        // as well as
        // https://github.com/kiooeht/Bard/blob/e023c4089cc347c60331c78c6415f489d19b6eb9/src/main/java/com/evacipated/cardcrawl/mod/bard/BardMod.java#L319
        // for reference as to how to turn this into an "Auto-Add" rather than having to list every relic individually.
        // Of note is that the bard mod uses it's own custom relic class (not dissimilar to our AbstractDefaultCard class for cards) that adds the 'color' field,
        // in order to automatically differentiate which pool to add the relic too.

        logger.info("Done adding relics!");
    }
    
    // ================ /ADD RELICS/ ===================
    
    
    // ================ ADD CARDS ===================
    
    @Override
    public void receiveEditCards() {
        logger.info("Adding variables");
        //Ignore this
        pathCheck();
        // Add the Custom Dynamic Variables
        logger.info("Add variables");
        // Add the Custom Dynamic variables

        logger.info("Adding cards");
        // Add the cards
        // Don't delete these default cards yet. You need 1 of each type and rarity (technically) for your game not to crash
        // when generating card rewards/shop screen items.

        // This method automatically adds any cards so you don't have to manually load them 1 by 1
        // For more specific info, including how to exclude cards from being added:
        // https://github.com/daviscook477/BaseMod/wiki/AutoAdd

        // The ID for this function isn't actually your modid as used for prefixes/by the getModID() method.
        // It's the mod id you give MTS in ModTheSpire.json - by default your artifact ID in your pom.xml

        //TODO: Rename the "BattleStatsMod" with the modid in your ModTheSpire.json file
        //TODO: The artifact mentioned in ModTheSpire.json is the artifactId in pom.xml you should've edited earlier

        // .setDefaultSeen(true) unlocks the cards
        // This is so that they are all "seen" in the library,
        // for people who like to look at the card list before playing your mod

        logger.info("Done adding cards!");
    }
    
    // ================ /ADD CARDS/ ===================
    
    
    // ================ LOAD THE TEXT ===================
    
    @Override
    public void receiveEditStrings() {
    }
    
    // ================ /LOAD THE TEXT/ ===================
    
    // ================ LOAD THE KEYWORDS ===================

    @Override
    public void receiveEditKeywords() {
        // Keywords on cards are supposed to be Capitalized, while in Keyword-String.json they're lowercase
        //
        // Multiword keywords on cards are done With_Underscores
        //
        // If you're using multiword keywords, the first element in your NAMES array in your keywords-strings.json has to be the same as the PROPER_NAME.
        // That is, in Card-Strings.json you would have #yA_Long_Keyword (#y highlights the keyword in yellow).
        // In Keyword-Strings.json you would have PROPER_NAME as A Long Keyword and the first element in NAMES be a long keyword, and the second element be a_long_keyword
        
//        Gson gson = new Gson();
//        String json = Gdx.files.internal(getModID() + "Resources/localization/eng/BattleStatsMod-Keyword-Strings.json").readString(String.valueOf(StandardCharsets.UTF_8));
//        com.evacipated.cardcrawl.mod.stslib.Keyword[] keywords = gson.fromJson(json, com.evacipated.cardcrawl.mod.stslib.Keyword[].class);
//
//        if (keywords != null) {
//            for (Keyword keyword : keywords) {
//                BaseMod.addKeyword(getModID().toLowerCase(), keyword.PROPER_NAME, keyword.NAMES, keyword.DESCRIPTION);
//                //  getModID().toLowerCase() makes your keyword mod specific (it won't show up in other cards that use that word)
//            }
//        }
    }
    
    // ================ /LOAD THE KEYWORDS/ ===================    
    
    // this adds "ModName:" before the ID of any card/relic/power etc.
    // in order to avoid conflicts if any other mod uses the same ID.
    public static String makeID(String idText) {
        return getModID() + ":" + idText;
    }

    @Override
    public void receiveOnBattleStart(AbstractRoom abstractRoom) {
        inBattle = true;
        logger.info("lastCombatMetricKey: " + CardCrawlGame.dungeon.lastCombatMetricKey);
        logger.info("MonsterRoom monsters: " + abstractRoom.monsters.monsters);

        fightTracker = new FightTracker();
        fightTracker.combatKey = CardCrawlGame.dungeon.lastCombatMetricKey;

        // Load stats for this encounter
        refreshBattleStats(fightTracker.combatKey);
    }

    @Override
    public void receiveRender(SpriteBatch spriteBatch) {
//        FontHelper.renderFontCentered(spriteBatch, FontHelper.cardEnergyFont_L, "Test message\nDoes it wrap?", 300, 500);

        // TODO: this seems like an overly complicated way to tell if the player is currently in a combat. Is there a better way?
        if (shouldRenderStats()) {
            renderStats(spriteBatch);
        }
        // TODO: above seems to make text grow/shrink when hovering over cards. Look at TopPanel#renderDungeonInfo to see how the floor number
        //  and ascension number are rendered.
    }

    private boolean shouldRenderStats() {
        // TODO: this seems like an overly complicated way to tell if the player is currently in a combat. Is there a better way?
        return inBattle;
//        CardCrawlGame.isInARun() && AbstractDungeon.isPlayerInDungeon() && (AbstractDungeon.getCurrMapNode() != null)
//                && (AbstractDungeon.getCurrRoom() != null) && (AbstractDungeon.getCurrRoom() instanceof MonsterRoom);
                //&& ((AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) || (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMPLETE));
    }
    private static void renderStats(SpriteBatch spriteBatch) {
        final String aggregateStatsFormatString = "%s\n\nCombats (wins/losses/total): %d/%d/%d\nAvg damage taken: %.1f\n" +
                "Avg damage dealt: %.1f\nAverage # of turns to win: %.1f";
        String aggregateStatsDisplay;
        if (battleStats != null) {
            aggregateStatsDisplay = String.format(aggregateStatsFormatString, battleStats.combatEnemyKey,
                    battleStats.wins, battleStats.loss, battleStats.numCombats, battleStats.averageDamageTaken,
                    battleStats.averageDamageDealt, battleStats.averageTurnsToWin);
        } else {
            final int defaultIntValue = 0;
            final float defaultFloatValue = 0f;
            aggregateStatsDisplay = String.format(aggregateStatsFormatString, fightTracker.combatKey, defaultIntValue,
                    defaultIntValue, defaultIntValue, defaultFloatValue, defaultFloatValue, defaultFloatValue);
        }
        FontHelper.renderFont(spriteBatch, FontHelper.tipBodyFont, aggregateStatsDisplay, 10, 900, Color.WHITE);

        // TODO: can render multi-line text with different fonts by offsetting text height. See TipHelper#getPowerTipHeight
        String currentFightStats = String.format("%s\n\nTurns: %d\nDamage taken: %d\nDamage dealt: %d", fightTracker.combatKey,
                fightTracker.numTurns, fightTracker.damageTaken, fightTracker.damageDealt);
        FontHelper.renderFont(spriteBatch, FontHelper.tipBodyFont, currentFightStats, 10, 700, Color.WHITE);
    }

    @Override
    public void receivePostUpdate() {
//        if (CardCrawlGame.isInARun() && AbstractDungeon.player.relics != null && !AbstractDungeon.player.relics.isEmpty()) {
//            AbstractRelic relic = AbstractDungeon.player.relics.get(0);
//            PowerTip tip = new PowerTip("Test header", "Test body");
////            ArrayList<PowerTip> tips = (ArrayList<PowerTip>) relic.tips.clone();
////            tips.add(tip);
//
//            ArrayList tips = new ArrayList();
//            tips.add(tip);
//            TipHelper.renderGenericTip(500, 250, "Tip header", "Tip body.");
//        }
    }

    @Override
    public int receiveOnPlayerDamaged(int i, DamageInfo damageInfo) {
        logger.info("On player damaged: " + i + ", " + damageInfo);
        // Note: this triggers for all damage, in and out of fights.
//        fightTracker.damageTaken += i;
        return i;
    }

    @Override
    public void receiveOnPlayerTurnStart() {
        fightTracker.numTurns++;
        logger.info("Incrementing turns: " + fightTracker.numTurns);

        logger.info("Settings.scale: " + Settings.scale);
    }

    @Override
    public void receivePostBattle(AbstractRoom abstractRoom) {
        logger.info("isBattleOver: " + abstractRoom.isBattleOver);
        logger.info("damageReceivedThisCombat: " + GameActionManager.damageReceivedThisCombat);
        logger.info("damageDealtThisCombat: ?");

        if (abstractRoom.isBattleOver) {
            if (abstractRoom.smoked) {
                fightTracker.result = FightTracker.FightResult.SMOKED;
            } else if (AbstractDungeon.player.isDead) {
                // Not sure if this ever triggers.
                fightTracker.result = FightTracker.FightResult.LOSS;
            } else {
                fightTracker.result = FightTracker.FightResult.WIN;
            }
        }

        writeFightStatsToStore();
    }

    public static void writeFightStatsToStore() {
        // Add current combat stats to the stats store
        String character = AbstractDungeon.player.getClass().getSimpleName();
        EnemyCombatStats enemyCombatStats = statsStore.stats.getEnemyCombatStats(fightTracker.combatKey, character);
        logger.info("Storing fight stats to CombatStats for " + character + "/" + fightTracker.combatKey + ": " + fightTracker);
        if (enemyCombatStats == null) {
            logger.info("Did not find CombatStats for character.");
            statsStore.stats.addCombatStats(fightTracker.combatKey, character, EnemyCombatStats.fromFightTracker(fightTracker));
        } else {
            enemyCombatStats.addFightStats(fightTracker);
            logger.info("CombatStats updated.");
        }
        // Refresh the overall battle stats.
        refreshBattleStats(fightTracker.combatKey);
    }

    public static void refreshBattleStats(String enemy) {
        battleStats = statsStore.stats.getAggregateCombatStats(enemy);
        if (battleStats == null) {
            logger.info("No stats for current enemy.");
        } else {
            logger.info("Refreshed battleStats: " + battleStats);
        }
    }

    @Override
    public void receivePreUpdate() {
        if (CardCrawlGame.isInARun() && AbstractDungeon.player.relics != null && !AbstractDungeon.player.relics.isEmpty()) {
//            AbstractRelic relic = AbstractDungeon.player.relics.get(0);
//
//            TipHelper.renderGenericTip(500, 250, "Tip header", "Tip body.");
        }
    }

    @Override
    public JsonElement onSaveRaw() {
        try {
            logger.info("Saving CombatStats to json.");
            long start = System.currentTimeMillis();
            Gson gson = new Gson();
            JsonElement statsJson = gson.toJsonTree(statsStore.stats);
            long end = System.currentTimeMillis();
            saveConfig(gson.toJson(statsJson));
            logger.info("Time to serialize CombatStats: " + (end - start));
            return gson.toJsonTree(statsStore.stats);
        } catch (Exception e) {
            logger.error("Failed converting stats to Json for saving.", e);
            return null;
        }
    }

    @Override
    public void onLoadRaw(JsonElement jsonElement) {
        if (jsonElement != null) {
//            logger.info("Loading CombatStats from json.");
//            Gson gson = new Gson();
//            statsStore.stats = gson.fromJson(jsonElement, CombatStats.class);
//            logger.info("Loaded CombatStats: " + statsStore.stats);
        } else {
            logger.info("No CombatStats JsonElement to load.");
        }

    }

    public void saveConfig(String statsJson) {
        try {
            long start = System.currentTimeMillis();
            configData.setString(CombatStats.FIGHT_STATS_MOD_JSON_KEY, statsJson);
            configData.save();
            long end = System.currentTimeMillis();
            logger.info("Time to save config data: " + (end - start));
        } catch (Exception e) {
            logger.error("Failed to save stats to config.", e);
        }
    }

    @Override
    public void receivePostDeath() {
        logger.info("receivePostDeath. player last damage taken: " + AbstractDungeon.player.lastDamageTaken);
//        fightTracker.result = FightTracker.FightResult.LOSS;
//        writeFightStatsToStore();
    }
}
