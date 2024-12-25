package app.olauncher.data

import android.content.Context
import android.content.SharedPreferences
import android.view.Gravity
import androidx.appcompat.app.AppCompatDelegate

class Prefs(context: Context) {
    private val PREFS_FILENAME = "app.olauncher"

    private val FIRST_OPEN = "FIRST_OPEN"
    private val FIRST_OPEN_TIME = "FIRST_OPEN_TIME"
    private val FIRST_SETTINGS_OPEN = "FIRST_SETTINGS_OPEN"
    private val FIRST_HIDE = "FIRST_HIDE"
    private val USER_STATE = "USER_STATE"
    private val LOCK_MODE = "LOCK_MODE"
    private val HOME_APPS_NUM = "HOME_APPS_NUM"
    private val AUTO_SHOW_KEYBOARD = "AUTO_SHOW_KEYBOARD"
    private val KEYBOARD_MESSAGE = "KEYBOARD_MESSAGE"
    private val DAILY_WALLPAPER = "DAILY_WALLPAPER"
    private val DAILY_WALLPAPER_URL = "DAILY_WALLPAPER_URL"
    private val WALLPAPER_UPDATED_DAY = "WALLPAPER_UPDATED_DAY"
    private val HOME_ALIGNMENT = "HOME_ALIGNMENT"
    private val HOME_BOTTOM_ALIGNMENT = "HOME_BOTTOM_ALIGNMENT"
    private val APP_LABEL_ALIGNMENT = "APP_LABEL_ALIGNMENT"
    private val STATUS_BAR = "STATUS_BAR"
    private val DATE_TIME_VISIBILITY = "DATE_TIME_VISIBILITY"
    private val SWIPE_LEFT_ENABLED = "SWIPE_LEFT_ENABLED"
    private val SWIPE_RIGHT_ENABLED = "SWIPE_RIGHT_ENABLED"
    private val HIDDEN_APPS = "HIDDEN_APPS"
    private val HIDDEN_APPS_UPDATED = "HIDDEN_APPS_UPDATED"
    private val SHOW_HINT_COUNTER = "SHOW_HINT_COUNTER"
    private val APP_THEME = "APP_THEME"
    private val ABOUT_CLICKED = "ABOUT_CLICKED"
    private val RATE_CLICKED = "RATE_CLICKED"
    private val SHARE_SHOWN_TIME = "SHARE_SHOWN_TIME"
    private val SWIPE_DOWN_ACTION = "SWIPE_DOWN_ACTION"
    private val TEXT_SIZE_SCALE = "TEXT_SIZE_SCALE"
    private val PRO_MESSAGE_SHOWN = "PRO_MESSAGE_SHOWN"

    private val APP_NAME = "APP_NAME_1"
    private val APP_PACKAGE = "APP_PACKAGE_1"
    private val APP_ACTIVITY_CLASS_NAME = "APP_ACTIVITY_CLASS_NAME_1"
    private val APP_USER = "APP_USER_1"

    private val APP_NAME_SWIPE_LEFT = "APP_NAME_SWIPE_LEFT"
    private val APP_NAME_SWIPE_RIGHT = "APP_NAME_SWIPE_RIGHT"
    private val APP_PACKAGE_SWIPE_LEFT = "APP_PACKAGE_SWIPE_LEFT"
    private val APP_PACKAGE_SWIPE_RIGHT = "APP_PACKAGE_SWIPE_RIGHT"
    private val APP_ACTIVITY_CLASS_NAME_SWIPE_LEFT = "APP_ACTIVITY_CLASS_NAME_SWIPE_LEFT"
    private val APP_ACTIVITY_CLASS_NAME_SWIPE_RIGHT = "APP_ACTIVITY_CLASS_NAME_SWIPE_RIGHT"
    private val APP_USER_SWIPE_LEFT = "APP_USER_SWIPE_LEFT"
    private val APP_USER_SWIPE_RIGHT = "APP_USER_SWIPE_RIGHT"
    private val CLOCK_APP_PACKAGE = "CLOCK_APP_PACKAGE"
    private val CLOCK_APP_USER = "CLOCK_APP_USER"
    private val CLOCK_APP_CLASS_NAME = "CLOCK_APP_CLASS_NAME"
    private val CALENDAR_APP_PACKAGE = "CALENDAR_APP_PACKAGE"
    private val CALENDAR_APP_USER = "CALENDAR_APP_USER"
    private val CALENDAR_APP_CLASS_NAME = "CALENDAR_APP_CLASS_NAME"

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_FILENAME, 0);

    var firstOpen: Boolean
        get() = prefs.getBoolean(FIRST_OPEN, true)
        set(value) = prefs.edit().putBoolean(FIRST_OPEN, value).apply()

    var firstOpenTime: Long
        get() = prefs.getLong(FIRST_OPEN_TIME, 0L)
        set(value) = prefs.edit().putLong(FIRST_OPEN_TIME, value).apply()

    var firstSettingsOpen: Boolean
        get() = prefs.getBoolean(FIRST_SETTINGS_OPEN, true)
        set(value) = prefs.edit().putBoolean(FIRST_SETTINGS_OPEN, value).apply()

    var firstHide: Boolean
        get() = prefs.getBoolean(FIRST_HIDE, true)
        set(value) = prefs.edit().putBoolean(FIRST_HIDE, value).apply()

    var userState: String
        get() = prefs.getString(USER_STATE, Constants.UserState.START).toString()
        set(value) = prefs.edit().putString(USER_STATE, value).apply()

    var lockModeOn: Boolean
        get() = prefs.getBoolean(LOCK_MODE, false)
        set(value) = prefs.edit().putBoolean(LOCK_MODE, value).apply()

    var autoShowKeyboard: Boolean
        get() = prefs.getBoolean(AUTO_SHOW_KEYBOARD, true)
        set(value) = prefs.edit().putBoolean(AUTO_SHOW_KEYBOARD, value).apply()

    var keyboardMessageShown: Boolean
        get() = prefs.getBoolean(KEYBOARD_MESSAGE, false)
        set(value) = prefs.edit().putBoolean(KEYBOARD_MESSAGE, value).apply()

    var dailyWallpaper: Boolean
        get() = prefs.getBoolean(DAILY_WALLPAPER, false)
        set(value) = prefs.edit().putBoolean(DAILY_WALLPAPER, value).apply()

    var dailyWallpaperUrl: String
        get() = prefs.getString(DAILY_WALLPAPER_URL, "").toString()
        set(value) = prefs.edit().putString(DAILY_WALLPAPER_URL, value).apply()

    var homeAppsNum: Int
        get() = prefs.getInt(HOME_APPS_NUM, 4)
        set(value) = prefs.edit().putInt(HOME_APPS_NUM, value).apply()

    var homeAlignment: Int
        get() = prefs.getInt(HOME_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(HOME_ALIGNMENT, value).apply()

    var homeBottomAlignment: Boolean
        get() = prefs.getBoolean(HOME_BOTTOM_ALIGNMENT, false)
        set(value) = prefs.edit().putBoolean(HOME_BOTTOM_ALIGNMENT, value).apply()

    var appLabelAlignment: Int
        get() = prefs.getInt(APP_LABEL_ALIGNMENT, Gravity.START)
        set(value) = prefs.edit().putInt(APP_LABEL_ALIGNMENT, value).apply()

    var showStatusBar: Boolean
        get() = prefs.getBoolean(STATUS_BAR, false)
        set(value) = prefs.edit().putBoolean(STATUS_BAR, value).apply()

    var dateTimeVisibility: Int
        get() = prefs.getInt(DATE_TIME_VISIBILITY, Constants.DateTime.ON)
        set(value) = prefs.edit().putInt(DATE_TIME_VISIBILITY, value).apply()

    var swipeLeftEnabled: Boolean
        get() = prefs.getBoolean(SWIPE_LEFT_ENABLED, true)
        set(value) = prefs.edit().putBoolean(SWIPE_LEFT_ENABLED, value).apply()

    var swipeRightEnabled: Boolean
        get() = prefs.getBoolean(SWIPE_RIGHT_ENABLED, true)
        set(value) = prefs.edit().putBoolean(SWIPE_RIGHT_ENABLED, value).apply()

    var appTheme: Int
        get() = prefs.getInt(APP_THEME, AppCompatDelegate.MODE_NIGHT_YES)
        set(value) = prefs.edit().putInt(APP_THEME, value).apply()

    var textSizeScale: Float
        get() = prefs.getFloat(TEXT_SIZE_SCALE, 1.0f)
        set(value) = prefs.edit().putFloat(TEXT_SIZE_SCALE, value).apply()

    var proMessageShown: Boolean
        get() = prefs.getBoolean(PRO_MESSAGE_SHOWN, false)
        set(value) = prefs.edit().putBoolean(PRO_MESSAGE_SHOWN, value).apply()

    var hiddenApps: MutableSet<String>
        get() = prefs.getStringSet(HIDDEN_APPS, mutableSetOf()) as MutableSet<String>
        set(value) = prefs.edit().putStringSet(HIDDEN_APPS, value).apply()

    var hiddenAppsUpdated: Boolean
        get() = prefs.getBoolean(HIDDEN_APPS_UPDATED, false)
        set(value) = prefs.edit().putBoolean(HIDDEN_APPS_UPDATED, value).apply()

    var toShowHintCounter: Int
        get() = prefs.getInt(SHOW_HINT_COUNTER, 1)
        set(value) = prefs.edit().putInt(SHOW_HINT_COUNTER, value).apply()

    var aboutClicked: Boolean
        get() = prefs.getBoolean(ABOUT_CLICKED, false)
        set(value) = prefs.edit().putBoolean(ABOUT_CLICKED, value).apply()

    var rateClicked: Boolean
        get() = prefs.getBoolean(RATE_CLICKED, false)
        set(value) = prefs.edit().putBoolean(RATE_CLICKED, value).apply()

    var shareShownTime: Long
        get() = prefs.getLong(SHARE_SHOWN_TIME, 0L)
        set(value) = prefs.edit().putLong(SHARE_SHOWN_TIME, value).apply()

    var swipeDownAction: Int
        get() = prefs.getInt(SWIPE_DOWN_ACTION, Constants.SwipeDownAction.NOTIFICATIONS)
        set(value) = prefs.edit().putInt(SWIPE_DOWN_ACTION, value).apply()

    var appNameSwipeLeft: String
        get() = prefs.getString(APP_NAME_SWIPE_LEFT, "Camera").toString()
        set(value) = prefs.edit().putString(APP_NAME_SWIPE_LEFT, value).apply()

    var appNameSwipeRight: String
        get() = prefs.getString(APP_NAME_SWIPE_RIGHT, "Phone").toString()
        set(value) = prefs.edit().putString(APP_NAME_SWIPE_RIGHT, value).apply()

    var appPackageSwipeLeft: String
        get() = prefs.getString(APP_PACKAGE_SWIPE_LEFT, "").toString()
        set(value) = prefs.edit().putString(APP_PACKAGE_SWIPE_LEFT, value).apply()

    var appActivityClassNameSwipeLeft: String?
        get() = prefs.getString(APP_ACTIVITY_CLASS_NAME_SWIPE_LEFT, "").toString()
        set(value) = prefs.edit().putString(APP_ACTIVITY_CLASS_NAME_SWIPE_LEFT, value).apply()

    var appPackageSwipeRight: String
        get() = prefs.getString(APP_PACKAGE_SWIPE_RIGHT, "").toString()
        set(value) = prefs.edit().putString(APP_PACKAGE_SWIPE_RIGHT, value).apply()

    var appActivityClassNameRight: String?
        get() = prefs.getString(APP_ACTIVITY_CLASS_NAME_SWIPE_RIGHT, "").toString()
        set(value) = prefs.edit().putString(APP_ACTIVITY_CLASS_NAME_SWIPE_RIGHT, value).apply()

    var appUserSwipeLeft: String
        get() = prefs.getString(APP_USER_SWIPE_LEFT, "").toString()
        set(value) = prefs.edit().putString(APP_USER_SWIPE_LEFT, value).apply()

    var appUserSwipeRight: String
        get() = prefs.getString(APP_USER_SWIPE_RIGHT, "").toString()
        set(value) = prefs.edit().putString(APP_USER_SWIPE_RIGHT, value).apply()

    var clockAppPackage: String
        get() = prefs.getString(CLOCK_APP_PACKAGE, "").toString()
        set(value) = prefs.edit().putString(CLOCK_APP_PACKAGE, value).apply()

    var clockAppUser: String
        get() = prefs.getString(CLOCK_APP_USER, "").toString()
        set(value) = prefs.edit().putString(CLOCK_APP_USER, value).apply()

    var clockAppClassName: String?
        get() = prefs.getString(CLOCK_APP_CLASS_NAME, "").toString()
        set(value) = prefs.edit().putString(CLOCK_APP_CLASS_NAME, value).apply()

    var calendarAppPackage: String
        get() = prefs.getString(CALENDAR_APP_PACKAGE, "").toString()
        set(value) = prefs.edit().putString(CALENDAR_APP_PACKAGE, value).apply()

    var calendarAppUser: String
        get() = prefs.getString(CALENDAR_APP_USER, "").toString()
        set(value) = prefs.edit().putString(CALENDAR_APP_USER, value).apply()

    var calendarAppClassName: String?
        get() = prefs.getString(CALENDAR_APP_CLASS_NAME, "").toString()
        set(value) = prefs.edit().putString(CALENDAR_APP_CLASS_NAME, value).apply()

    fun getAppName(location: Int): String = prefs.getString("{$APP_NAME}_$location", "").toString()

    fun getAppPackage(location: Int): String = prefs.getString("${APP_PACKAGE}_$location", "").toString()

    fun getAppActivityClassName(location: Int): String = prefs.getString("${APP_ACTIVITY_CLASS_NAME}_$location", "").toString()

    fun getAppUser(location: Int): String = prefs.getString("${APP_USER}_$location", "").toString()

    fun getAppRenameLabel(appPackage: String): String = prefs.getString(appPackage, "").toString()

    fun setAppRenameLabel(appPackage: String, renameLabel: String) = prefs.edit().putString(appPackage, renameLabel).apply()

    fun setAppName(location: Int, name: String) {
        prefs.edit().putString("{$APP_NAME}_$location", name).apply()
    }

    fun setAppPackage(location: Int, packageName: String) {
        prefs.edit().putString("${APP_PACKAGE}_$location", packageName).apply()
    }

    fun setAppUser(location: Int, user: String) {
        prefs.edit().putString("${APP_USER}_$location", user).apply()
    }

    fun setAppActivityClassName(location: Int, activityClassName: String?) {
        prefs.edit().putString("${APP_ACTIVITY_CLASS_NAME}_$location", activityClassName).apply()
    }
}