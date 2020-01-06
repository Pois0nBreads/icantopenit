package net.pois0nbread.icantopenit;

import de.robv.android.xposed.XSharedPreferences;

/**
 * <pre>
 *     author : Pois0nBread
 *     e-mail : pois0nbreads@gmail.com
 *     time   : 2019/12/02
 *     desc   : Settings
 *     version: 1.0
 * </pre>
 */

public class Settings {

    private static XSharedPreferences preferences;

    private static XSharedPreferences getModuleSharedPreferences() {
        if (preferences == null) {
            preferences = new XSharedPreferences(BuildConfig.APPLICATION_ID, "settings");
            preferences.makeWorldReadable();
        } else
            preferences.reload();
        return preferences;
    }

    public static boolean isCantOpen(String package_name) {
        return getModuleSharedPreferences().getBoolean(package_name, false);
    }

    public static boolean isEnable() {
        return getModuleSharedPreferences().getBoolean("Enable", false);
    }
}
