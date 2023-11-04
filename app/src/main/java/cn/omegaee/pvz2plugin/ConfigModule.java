package cn.omegaee.pvz2plugin;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.PreferenceFragmentCompat;

import java.io.File;

public class ConfigModule extends PreferenceFragmentCompat {

    public static String UNLOCK_FRAME = "unlock_frame";
    public static String CLOSE_LOG = "close_log";
//    public static String WIDE_SCREEN = "wide_screen";
    public static String FAST_AD = "fast_ad";
    public static String GET_DEVICE_ID = "get_device_id";
    public static String SET_DEVICE_ID = "set_device_id";
    public static String DEVICE_CONFIG = "device_config";

//    private SeekBarPreference unlock_frame;
//    private SwitchPreferenceCompat close_log;
//    private SwitchPreferenceCompat wide_screen;
//    private SwitchPreferenceCompat fast_ad;
//    private Preference get_device_id;
//    private EditTextPreference set_device_id;

    private void setWorldReadable() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            File dataDir = new File(getContext().getApplicationInfo().dataDir);
            File prefsDir = new File(dataDir, "shared_prefs");
            File prefsFile = new File(prefsDir, getPreferenceManager().getSharedPreferencesName() + ".xml");
            System.out.println("--> "+prefsFile.exists());
            if (prefsFile.exists()) {
                for (File file : new File[]{dataDir, prefsDir, prefsFile}) {
                    file.setReadable(true, false);
                    file.setExecutable(true, false);
                }
            }
        } else {
            getPreferenceManager().setSharedPreferencesMode(Context.MODE_WORLD_READABLE);
        }
    }

    @Override
    public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
        setWorldReadable();
        addPreferencesFromResource(R.xml.plugin_config);
        findPres();
        initPres();
    }

    @Override
    public void onPause() {
        super.onPause();
        setWorldReadable();
    }

//    public void addClipText(String label, String text){
//        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
//        clipboard.setPrimaryClip(ClipData.newPlainText(label, text));
//    }

    private void findPres(){
//        unlock_frame = findPreference(UNLOCK_FRAME);
//        close_log = findPreference(CLOSE_LOG);
//        wide_screen = findPreference(WIDE_SCREEN);
//        fast_ad = findPreference(FAST_AD);
//        get_device_id = findPreference(GET_DEVICE_ID);
//        set_device_id = findPreference(SET_DEVICE_ID);
    }

//    private String getAndroidId(){
//        return Settings.System.getString(getContext().getContentResolver(), "android_id");
//    }

    private void initPres(){
//        get_device_id.setOnPreferenceClickListener(preference -> {
//            addClipText("device_id", getAndroidId());
//            Toast.makeText(getContext(), "已复制到粘贴板", Toast.LENGTH_SHORT).show();
//            return true;
//        });
    }

}
