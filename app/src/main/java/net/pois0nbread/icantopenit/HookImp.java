package net.pois0nbread.icantopenit;

import android.app.Activity;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * <pre>
 *     author : Pois0nBread
 *     e-mail : pois0nbreads@gmail.com
 *     time   : 2019/12/02
 *     desc   : Hook
 *     version: 1.0
 * </pre>
 */

public class HookImp implements IXposedHookLoadPackage, IXposedHookZygoteInit {
    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        if (!Settings.isEnable()) return;

        try {
            if (Settings.isCantOpen(lpparam.packageName)){
                XposedHelpers.findAndHookMethod(XposedHelpers.findClass(Activity.class.getName(), lpparam.classLoader),
                        "onStart", XC_MethodReplacement.returnConstant("Error"));
            }
        } catch (Exception e) {}
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {}
}
