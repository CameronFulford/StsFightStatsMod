# StsFightStatsMod

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

### Notes

- sword image to possibly use? `TextureAtlas.AtlasRegion img = ImageMaster.vfxAtlas.findRegion("combat/battleStartSword");
- popin animation, see AbstractCreature updateHealthBar() and updateHbPopInAnimation(), called from AbstractPlayer#update

- AbstractEvent#enterCombat() called from several events that may have combats and need to be handled specially since they are not MonsterRooms
-   AbstractDungeon.isLoadingPostCombatSave
  - Mind Bloom, Mysterious Sphere, Colosseum, Masked Bandits, Dead Adventurer, Mushrooms