package com.appndigital.amusepush.helper

import android.content.Context
import android.content.pm.PackageManager
import com.appndigital.amusepush.Constants
import java.text.SimpleDateFormat
import java.util.*


class Utils {

    companion object {
        const val stringFormat = "yyyy-MM-dd HH:mm"

        @JvmStatic
        fun getInstallDate(context: Context): String {
            val packageManager = context.packageManager
            val installTimeInMilliseconds: Long // install time is conveniently provided in milliseconds
            var installDateString: String?

            try {
                val packageInfo = packageManager.getPackageInfo(context.packageName, 0)
                installTimeInMilliseconds = packageInfo.firstInstallTime
                installDateString = getDate(installTimeInMilliseconds, stringFormat)
            } catch (e: PackageManager.NameNotFoundException) {
                // an error occurred, so display the Unix epoch
                val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
                var date = prefs.getString(Constants.DATE_INSTALL_PREFERENCES_KEY, "")

                if (date == "") {
                    date = SimpleDateFormat(stringFormat, Locale.getDefault()).format(Date())
                    prefs.edit().putString(Constants.DATE_INSTALL_PREFERENCES_KEY, date).apply()
                }

                installDateString = date
            }

            return installDateString!!
        }

        fun saveDateLastOpening(context: Context) {
            val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
            val date = SimpleDateFormat(stringFormat, Locale.getDefault()).format(Date())
            prefs.edit().putString(Constants.LAST_DATE_OPENED_PREF_KEY, date).apply()
        }

        fun getDateLastOpening(context: Context): String {
            val prefs = context.getSharedPreferences(Constants.USER_PREFERENCES_KEY, Context.MODE_PRIVATE)
            val date = SimpleDateFormat(stringFormat, Locale.getDefault()).format(Date())
            return prefs.getString(Constants.LAST_DATE_OPENED_PREF_KEY, date)
        }

        private fun getDate(milliSeconds: Long, dateFormat: String): String {
            // Create a DateFormatter object for displaying date in specified format.
            val formatter = SimpleDateFormat(dateFormat)

            // Create a calendar object that will convert the date and time value in milliseconds to date.
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = milliSeconds
            return formatter.format(calendar.time)
        }

    }
}