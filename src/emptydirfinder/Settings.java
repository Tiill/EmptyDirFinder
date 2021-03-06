package emptydirfinder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;

public class Settings {

    List<String> IGNORE_FILES = new LinkedList<>();
    List<String> IGNORE_SYSTEM_DIRECTRIES = new LinkedList<>();
    boolean IGNORE_SYS = true;
    boolean IGNORE_OMB = true;
    Preferences localSettings = Preferences.userRoot();
    public List<Pattern> pat = new ArrayList<>();

    public Settings() {
        IGNORE_SYSTEM_DIRECTRIES.add(System.getenv("WINDIR"));
        IGNORE_SYSTEM_DIRECTRIES.add("/boot");
        IGNORE_SYSTEM_DIRECTRIES.add("/lib");
        IGNORE_SYSTEM_DIRECTRIES.add("/root");
        IGNORE_SYSTEM_DIRECTRIES.add("/sys");
        IGNORE_SYSTEM_DIRECTRIES.add("/usr");
        IGNORE_SYSTEM_DIRECTRIES.add("/System");
        try {
            load();
        } catch (BackingStoreException ex) {
            Logger.getLogger(Settings.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void load() throws BackingStoreException {
        if (localSettings.nodeExists("/EmptyDirFinder")) {
            localSettings = localSettings.node("/EmptyDirFinder");
            IGNORE_OMB = localSettings.getBoolean("IGNORE_OMB", false);
            IGNORE_SYS = localSettings.getBoolean("IGNORE_SYS", true);
            String[] parts = localSettings.get("IGNORE_FILES", "Thumbs.db;desktop.ini;.*\\\\.tmp;").split(";");
            IGNORE_FILES = new LinkedList<>();
            for (String part : parts) {
                IGNORE_FILES.add(part);
            }
        } else {
            IGNORE_FILES = new LinkedList<>();
            IGNORE_FILES.add("Thumbs.db");
            IGNORE_FILES.add("desktop.ini");
            IGNORE_FILES.add(".*\\.tmp");
            IGNORE_OMB = true;
            IGNORE_SYS = true;
        }
    }

    void save() throws BackingStoreException {
        localSettings = localSettings.node("/EmptyDirFinder");
        localSettings.putBoolean("IGNORE_OMB", IGNORE_OMB);
        localSettings.putBoolean("IGNORE_SYS", IGNORE_SYS);
        StringBuilder strOut = new StringBuilder();
        for (String z : IGNORE_FILES) {
            strOut.append(z).append(";");
        }
        localSettings.put("IGNORE_FILES", strOut.toString());
    }

    void setDefaultSettings() {
        IGNORE_FILES = new LinkedList<>();
        IGNORE_FILES.add("Thumbs.db");
        IGNORE_FILES.add("desktop.ini");
        IGNORE_FILES.add(".*\\.tmp");
        IGNORE_OMB = true;
    }
}
