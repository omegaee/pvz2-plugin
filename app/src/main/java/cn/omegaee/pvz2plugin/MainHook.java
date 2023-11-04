package cn.omegaee.pvz2plugin;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import javax.microedition.khronos.opengles.GL10;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class MainHook implements IXposedHookLoadPackage {

    private long timeOffset = 0L;
    private long adTime = 30000 - 10000;

    private void setFrame(long frame){
        timeOffset = (1000/30) - (1000/frame);
    }

    private String ssaid = null;

    private XSharedPreferences shared;
    private XSharedPreferences getShared(){
        System.out.println("--> into...");
        if (shared == null){
            shared = new XSharedPreferences(BuildConfig.APPLICATION_ID);
            shared.makeWorldReadable();
        }else {
            shared.reload();
        }
        return shared;
    }

    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {
        // 过滤不必要的应用
        if (!lpparam.packageName.startsWith("com.popcap.pvz2cthd")) return;

        XSharedPreferences shared = getShared();

//        System.out.println("--> "+shared.getAll());

        // fps
        int fps = shared.getInt(ConfigModule.UNLOCK_FRAME, 60);
        if (fps > 30  && fps <= 90){
            hookFPS(lpparam, fps);
        }

        // 关闭日志
        boolean isLog = shared.getBoolean(ConfigModule.CLOSE_LOG, true);
        if(isLog){
            hookLog(lpparam);
        }

        // 设备
        boolean openDevConf = shared.getBoolean(ConfigModule.DEVICE_CONFIG, false);
        boolean isCopy = shared.getBoolean(ConfigModule.GET_DEVICE_ID, true);
        String device_id = shared.getString(ConfigModule.SET_DEVICE_ID, "");
        if (openDevConf || isCopy){
            hookSSAID(lpparam, openDevConf ? device_id.trim() : null, isCopy);
        }

        // TapTap
        if (lpparam.packageName.endsWith("bk")){
            // 广告
            boolean fast_ad = shared.getBoolean(ConfigModule.FAST_AD, false);
            if (fast_ad){
                hookAd(lpparam);
            }
        }

    }

    private void hookSSAID(XC_LoadPackage.LoadPackageParam lpparam, String _ssaid, boolean isCopySSAID) throws Throwable {
        XposedHelpers.findAndHookMethod("com.talkweb.twOfflineSdk.tools.Tools", lpparam.classLoader, "getAndroidId", android.content.Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (isCopySSAID && ssaid == null){
                    Context context = (Context) param.args[0];
                    ssaid = (String) param.getResult();

                    ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setPrimaryClip(ClipData.newPlainText("pvz2_ssaid", ssaid));
                }

                if(_ssaid != null && !_ssaid.isEmpty()){
                    param.setResult(_ssaid);
                }
            }
        });
    }

    // Close Log
    private void hookLog(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable{
        XC_MethodReplacement repMethod = new XC_MethodReplacement() {
            @Override
            protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
                String tag = (String) param.args[0];
                String msg = (String) param.args[1];
                return tag.length() + msg.length();
            }
        };

        Class<?> cLog = XposedHelpers.findClass("android.util.Log", lpparam.classLoader);
        Method[] methods = cLog.getDeclaredMethods();
        for (Method method : methods){
            if(method.getName().matches("[ivd]")){
                XposedBridge.hookMethod(method, repMethod);
            }
        }
    }

    // Ad
    private void hookAd(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable{
        Class<?> cCountTimer = XposedHelpers.findClass("com.tapsdk.tapad.internal.RewardPresenter$b", lpparam.classLoader);
        Method mOnTick = cCountTimer.getDeclaredMethod("onTick", long.class);
        XposedBridge.hookMethod(mOnTick, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                long remainTime = (long) param.args[0];
                if (remainTime > adTime)remainTime -= adTime;
                else remainTime = 0;
                param.args[0] = remainTime;
            }
        });

    }

    // Set FPS
    private void hookFPS(XC_LoadPackage.LoadPackageParam lpparam, int fps) throws Throwable {
        setFrame(fps);

        Class<?> cAndroidRenderer = XposedHelpers.findClass("com.popcap.SexyAppFramework.AndroidSurfaceView$AndroidRenderer", lpparam.classLoader);
        Field fStartTime = cAndroidRenderer.getDeclaredField("mStartTime");
        fStartTime.setAccessible(true);

        Method mOnDrawFrame = cAndroidRenderer.getDeclaredMethod("onDrawFrame", GL10.class);
        XposedBridge.hookMethod(mOnDrawFrame, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                long startTime = fStartTime.getLong(param.thisObject);
                if (startTime > timeOffset)fStartTime.setLong(param.thisObject, startTime - timeOffset);
            }
        });

    }

}
