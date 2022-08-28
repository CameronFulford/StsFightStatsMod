# StsFightStatsMod

## Features
1. Saves stats for each combat across runs. Displays the current combat's stats and the aggregates stats from all
encounters with the enemy.
2. Draggable stats makes repositioning on the screen simple. Left-click and drag to reposition the stats display.

## TODO
- count loss when run is abandoned
- save stats to file after loss
- cosmetic enhancements to stats display. Formatting, color, text effects.
- add character killed most by enemy
- add an envelope to saved stats data with a version number to allow for future versioning of the stats model
- show stats on death screen
- when continuing game at end of combat, fight stats are empty. Could store this in run data and reload.
- fix render stats in combat events. Masked Bandits, are there others?
- when loading into post-combat for an event combat (e.g., Masked Bandits) the AbstractDungeon.lastCombatMetricKey is not set and will be null.
how do we figure out the combat key when loading into an event post combat? Possibly use onSaveRaw to save FightTracker during the
game. That way, we can load the FightTracker to display as well as extract the last combat key.
- Mind Bloom combat loads the lastCombatMetricKey as "Mind Bloom Boss Battle". Should I just keep it like that even though
the bosses might be different? I could also get the name of the monster (boss) from the room.

## BUGS
1. Not sure if this is my bug, but after fighting and winning against the double orb walker event, I quit the game before
moving to the next room to verify it reloaded the stats correctly. But when I reloaded it loaded a shop room instead and
there were no rewards. Though my stats did load correctly. Not sure if this is expected or an existing bug.
2. After Mind Bloom combat before advancing to the next room, I save and quit. Then reloaded the game and it reloaded my
saved stats correctly, but instead of being post-combat the event started from the beginning so I had to fight the same
combat a second time. This effectively meant I saved 2 copies of the combat stats since I had fought it twice. This might
be a game bug.


### Notes

- sword image to possibly use? `TextureAtlas.AtlasRegion img = ImageMaster.vfxAtlas.findRegion("combat/battleStartSword");
- popin animation, see AbstractCreature updateHealthBar() and updateHbPopInAnimation(), called from AbstractPlayer#update

- AbstractEvent#enterCombat() called from several events that may have combats and need to be handled specially since they are not MonsterRooms
-   AbstractDungeon.isLoadingPostCombatSave
  - Mind Bloom, Mysterious Sphere, Colosseum, Masked Bandits, Dead Adventurer, Mushrooms
- Check out FontHelper.getSmartHeight to get height of text lines. See TipHelper#render, also FontHelper.renderSmartText
- To render stats in a PowerTip style box, see TipHelper#renderPowerTips and TipHelper#renderTipBox
- to render text at scale, try FontHelper#renderSmartText with scale