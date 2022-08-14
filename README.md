# StsFightStatsMod

## TODO
- count loss when run is abandoned
- save stats to file after loss
- cosmetic enhancements to stats display. Formatting, color, text effects.
- add character killed most by enemy
- add an envelope to saved stats data with a version number to allow for future versioning of the stats model
- show stats on death screen
- when continuing game at end of combat, fight stats are empty. Could store this in run data and reload.

### Notes

- sword image to possibly use? `TextureAtlas.AtlasRegion img = ImageMaster.vfxAtlas.findRegion("combat/battleStartSword");
- popin animation, see AbstractCreature updateHealthBar() and updateHbPopInAnimation(), called from AbstractPlayer#update