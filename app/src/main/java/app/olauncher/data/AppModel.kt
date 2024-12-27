package app.olauncher.data

import android.graphics.drawable.Drawable
import android.os.UserHandle
import java.text.CollationKey

data class AppModel(
    val appLabel: String,
    val key: CollationKey?,
    val appPackage: String,
    val activityClassName: String?,
    val isNew: Boolean? = false,
    val user: UserHandle,
    val appIcon: Drawable,
) : Comparable<AppModel> {
    override fun compareTo(other: AppModel): Int = when {
        key != null && other.key != null -> key.compareTo(other.key)
        else -> appLabel.compareTo(other.appLabel, true)
    }
}

data class HomeAppModel(
    val appLabel: String,
    val appPackage: String,
    val activityClassName: String,
    val user: String,
)