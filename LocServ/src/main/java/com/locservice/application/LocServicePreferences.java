package com.locservice.application;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.locservice.CMApplication;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * Created by Vahagn Gevorgyan
 * 29 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LocServicePreferences implements SharedPreferences {
    private final static String TAG = LocServicePreferences.class
            .getSimpleName();

    /**
     * Settings keys.
     */
    public static enum Settings {

        IS_FIRST_LAUNCH("IS_FIRST_LAUNCH", Boolean.class),
        CITIES_DATE("CITIES_DATE", String.class),
        LANDMARKS_DATE("LANDMARKS_DATE", String.class),
        TARIFFS_DATE("TARIFFS_DATE", String.class),
        CITIES_LINK("CITIES_LINK", String.class),
        LANDMARKS_LINK("LANDMARKS_LINK", String.class),
        TARIFFS_LINK("TARIFFS_LINK", String.class),
        CURRENT_ORDER_ID("CURRENT_ORDER_ID", String.class),
        CURRENT_ORDER_STATUS("CURRENT_ORDER_STATUS", Integer.class),
        CURRENT_ORDER_PRICE("CURRENT_ORDER_PRICE", String.class),
        CURRENT_ORDER_BONUS("CURRENT_ORDER_BONUS", String.class),
        CURRENT_ORDER_TRIP_TIME("CURRENT_ORDER_TRIP_TIME", String.class),

        LAST_TR_ID("LAST_TR_ID", Integer.class),

        SENT_TOKEN_TO_SERVER("SENT_TOKEN_TO_SERVER", Boolean.class),
        GCM_TOKEN("GCM_TOKEN", String.class),
        PROPERTY_APP_VERSION("PROPERTY_APP_VERSION", Integer.class),
        NEWS_WEBVIEW_MESSAGE("NEWS_WEBVIEW_MESSAGE", String.class),
        CHAT_SESSION_ACTIVE("CHAT_SESSION_ACTIVE", Boolean.class),

        AUTH_TOKEN("AUTH_TOKEN", String.class),

        PHONE_NUMBER("PHONE_NUMBER", String.class),
        PROFILE_FORMATTED_PHONE_NUMBER("PROFILE_FORMATTED_PHONE_NUMBER", String.class),
        PROFILE_NAME("PROFILE_NAME", String.class),
        PROFILE_EMAIL("PROFILE_EMAIL", String.class),
        PROFILE_SMS_NOTIFICATION("PROFILE_SMS_NOTIFICATION", Integer.class),
        PROFILE_BASE_64_IMAGE("PROFILE_BASE_64_IMAGE", String.class),
        PROFILE_FACEBOOK_ID("PROFILE_FACEBOOK_ID", String.class),
        PROFILE_VK_ID("PROFILE_VK_ID", String.class),
        PROFILE_BONUS("PROFILE_BONUS", String.class),
        PROFILE_HAS_CARDS("PROFILE_HAS_CARDS", Integer.class),
        PROFILE_REFERAL("PROFILE_REFERAL", String.class),
        PROFILE_SALE("PROFILE_SALE", String.class),
        PROFILE_CAN_USE_BONUS("PROFILE_CAN_USE_BONUS", Integer.class),
        PROFILE_MIN_BONUS_SUM("PROFILE_MIN_BONUS_SUM", Integer.class),
        PROFILE_PHOTO_LINK("PROFILE_PHOTO_LINK", String.class),
        PROFILE_CAN_USE_NO_CASH("PROFILE_CAN_USE_NO_CASH", Integer.class),

        CURRENT_CITY_ID("CURRENT_CITY_ID", String.class),
        BOUNDS_CITY_ID("BOUNDS_CITY_ID", String.class),

        IS_USER_LOG_IN("IS_USER_LOG_IN", Boolean.class),
        CURRENT_PHONE_NUMBER("CURRENT_PHONE_NUMBER", String.class),
        IS_SHOW_ENABLE_LOCATION("IS_SHOW_ENABLE_LOCATION", Boolean.class),
        IS_VIDEO_LOADED("IS_VIDEO_LOADED", Boolean.class),
        ACTIVE_ORDER_ID("ACTIVE_ORDER_ID", String.class),
        DB_VERSION("DB_VERSION", Integer.class),
        IS_LARGE_TEXT_DIALOG_SHOWN("IS_LARGE_TEXT_DIALOG_SHOWN", Boolean.class)
        ;



        private final String key;
        private final Class<?> type;

        private Settings(final String key, final Class<?> type) {
            this.key = key;
            this.type = type;
        }

        public String key() {
            return this.key;
        }

        Class<?> type() {
            return this.type;
        }
    }

    private static LocServicePreferences settingsObject = null;
    private static SharedPreferences sPreferences = null;
    private final static String PREF_FILENAME = "LocServicePreferences";

    /**
     * Constructor.
     *
     * @param context
     *            - Context
     */
    private LocServicePreferences(final Context context) {
        try {
            sPreferences = context.getSharedPreferences(PREF_FILENAME,
                    Context.MODE_PRIVATE);

            if (!contains(Settings.IS_FIRST_LAUNCH)) {
                setSetting(Settings.IS_FIRST_LAUNCH, true);
            }
        } catch (final Exception e) {
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Creator for AppSettings component.
     */
    public static synchronized LocServicePreferences getAppSettings() {
        if (null == settingsObject) {
            settingsObject = new LocServicePreferences(CMApplication.getAppContext());
        }

        return settingsObject;
    }

    public boolean contains(final Settings settingsKey) {
        return contains(settingsKey.key());
    }

    @Override
    public boolean contains(final String key) {
        boolean retval = false;

        if (null != sPreferences) {
            retval = sPreferences.contains(key);
        }

        return retval;
    }

    public synchronized void setSetting(final Settings settingsKey,
                                        final Object value) throws ClassCastException {

        final Editor settingsEditor = edit();
        boolean bChanged = false;
        final String key = settingsKey.key();
        final Class<?> type = settingsKey.type();

        if (null != settingsEditor) {
            if (String.class == type) {
                settingsEditor.putString(key, (String) value);
                bChanged = true;
            } else if (Boolean.class == type) {
                settingsEditor.putBoolean(key, (Boolean) value);
                bChanged = true;
            } else if (Integer.class == type) {
                settingsEditor.putInt(key, (Integer) value);
                bChanged = true;
            } else if (Long.class == type) {
                settingsEditor.putLong(key, (Long) value);
                bChanged = true;
            } else if (Float.class == type) {
                settingsEditor.putFloat(key, (Float) value);
                bChanged = true;
            }

            if (bChanged) {
                settingsEditor.commit();
            }
        }
    }

    public synchronized void setSetting(final Settings settingsKey,
                                        final String keyAdd,
                                        final Object value) throws ClassCastException {

        final Editor settingsEditor = edit();
        boolean bChanged = false;
        final String key = settingsKey.key();
        final Class<?> type = settingsKey.type();

        if (null != settingsEditor) {
            if (String.class == type) {
                settingsEditor.putString(key+keyAdd, (String) value);
                bChanged = true;
            } else if (Boolean.class == type) {
                settingsEditor.putBoolean(key+keyAdd, (Boolean) value);
                bChanged = true;
            } else if (Integer.class == type) {
                settingsEditor.putInt(key+keyAdd, (Integer) value);
                bChanged = true;
            } else if (Long.class == type) {
                settingsEditor.putLong(key+keyAdd, (Long) value);
                bChanged = true;
            } else if (Float.class == type) {
                settingsEditor.putFloat(key+keyAdd, (Float) value);
                bChanged = true;
            }

            if (bChanged) {
                settingsEditor.commit();
            }
        }
    }

    @Override
    public Editor edit() {
        Editor editor = null;

        if (null != sPreferences) {
            editor = sPreferences.edit();
        }

        return editor;
    }

    @Override
    public Map<String, ?> getAll() {
        Map<String, ?> map = Collections.emptyMap();

        if (null != sPreferences) {
            map = sPreferences.getAll();
        }

        return map;
    }

    public boolean getBoolean(final Settings settingsKey)
            throws ClassCastException {
        return getBoolean(settingsKey.key(), false);
    }

    @Override
    public boolean getBoolean(final String key, final boolean defValue) {
        boolean retval = defValue;

        if (null != sPreferences) {
            retval = sPreferences.getBoolean(key, defValue);
        }

        return retval;
    }

    public float getFloat(final Settings settingsKey) throws ClassCastException {
        return getFloat(settingsKey.key(), 0);
    }

    @Override
    public float getFloat(final String key, final float defValue) {
        float retval = defValue;

        if (null != sPreferences) {
            retval = sPreferences.getFloat(key, defValue);
        }

        return retval;
    }

    public int getInt(final Settings settingsKey) throws ClassCastException {
        return getInt(settingsKey.key(), -1);
    }

    @Override
    public int getInt(final String key, final int defValue) {
        int retval = defValue;

        if (null != sPreferences) {
            retval = sPreferences.getInt(key, defValue);
        }

        return retval;
    }

    public long getLong(final Settings settingsKey) throws ClassCastException {
        return getLong(settingsKey.key(), -1);
    }

    @Override
    public long getLong(final String key, final long defValue) {
        long retval = defValue;

        if (null != sPreferences) {
            retval = sPreferences.getLong(key, defValue);
        }

        return retval;
    }

    public String getString(final Settings settingsKey)
            throws ClassCastException {
        return getString(settingsKey.key(), null);
    }

    @Override
    public String getString(final String key, final String defValue) {
        String retval = defValue;

        if (null != sPreferences) {
            retval = sPreferences.getString(key, defValue);
        }

        return retval;
    }

    @Override
    public void registerOnSharedPreferenceChangeListener(
            final OnSharedPreferenceChangeListener listener) {

        if (null != sPreferences) {
            sPreferences.registerOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public void unregisterOnSharedPreferenceChangeListener(
            final OnSharedPreferenceChangeListener listener) {

        if (null != sPreferences) {
            sPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        }
    }

    @Override
    public Set<String> getStringSet(String key, Set<String> defValue) {
        Set<String> retval = defValue;

        if (null != sPreferences) {
            retval = sPreferences.getStringSet(key, defValue);
        }

        return retval;
    }

}
