# StsFightStatsMod

## TODO
- Enable stats display if restarting the game from a battle completed screen
- Stop displaying stats when exiting to the main menu
- count loss when run is abandoned
- save stats to file after loss
- cosmetic enhancements to stats display. Formatting, color, text effects.
- add character killed most by enemy
- add an envelope to saved stats data with a version number to allow for future versioning of the stats model

### Notes
Saving:
- SaveAndContinue#save builds a HashMap and serializes it, then calls AsyncSaver.save

```aidl
APPDATA = System.getenv("LOCALAPPDATA");
        if (APPDATA == null || APPDATA.isEmpty()) {
            APPDATA = System.getenv("APPDATA");
        }
        APPDATA += "/ModTheSpire/MyModDir/";
```