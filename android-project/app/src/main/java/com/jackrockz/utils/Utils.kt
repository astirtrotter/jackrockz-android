package com.jackrockz.utils

import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.support.v4.content.ContextCompat.startActivity
import android.support.v7.app.AlertDialog
import android.text.format.DateUtils
import android.view.Window
import android.widget.Toast
import com.google.gson.Gson
import com.jackrockz.MyApplication
import com.jackrockz.R
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*


class Utils {
    companion object {
        var progressDialog: Dialog? = null
        val settings by lazy { MyApplication.instance.getSharedPreferences(GlobalConstants.PREFS_NAME, 0) }
        val editor by lazy { settings.edit() }
        val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")

        fun showLoading(activity: Activity) {
            progressDialog?.hide()
            progressDialog = Dialog(activity)
            progressDialog?.apply {
                requestWindowFeature(Window.FEATURE_NO_TITLE)
                setContentView(R.layout.loading_dialog)
                setCancelable(false)
                setCanceledOnTouchOutside(false)
                window.setBackgroundDrawable(ColorDrawable(android.graphics.Color.TRANSPARENT))

                show()
            }
        }

        fun hideLoading() {
            progressDialog?.hide()
            progressDialog = null
        }

        fun showAlertDialog(activity: Activity, title: String? = null, message: String) {
            AlertDialog.Builder(activity)
                    .setTitle(title)
                    .setMessage(message)
                    .setPositiveButton("OK", { dialog, _ -> dialog.dismiss() })
                    .show()
        }

        fun showToast(activity: Activity, message: String) {
            Toast.makeText(activity, message, Toast.LENGTH_LONG).show()
        }

        fun saveData(key: String, value: String) = editor.putString(key, value).commit()
        fun loadData(key: String) = settings.getString(key, "")

        fun saveObject(key: String, value: Any) = editor.putString(key, Gson().toJson(value)).commit()
        fun <T> loadObject(key: String, cls: Class<T>) = Gson().fromJson(loadData(key), cls)

        fun convertStringToDate(string: String, outFormat: String): String {
            inputFormat.timeZone = TimeZone.getTimeZone("GMT")
            val outputFormat = SimpleDateFormat(outFormat, Locale.getDefault())

            val parsed = inputFormat.parse(string)
            return outputFormat.format(parsed)
        }

        fun getStringFromTwoDates(strDate1: String, strDate2: String): String {
            inputFormat.timeZone = TimeZone.getTimeZone("GMT")
            val realDate1 = inputFormat.parse(strDate1)
            val realDate2 = inputFormat.parse(strDate2)

            val outputFormat = SimpleDateFormat("dd/MM/yyyy")
            val timeFormat= SimpleDateFormat("HH:mm")

            val localizedStartDay: String = if (DateUtils.isToday(realDate1.time)) {
                "Today"
            } else if (DateUtils.isToday(realDate1.time + DateUtils.DAY_IN_MILLIS)) {
                "Yesterday"
            } else if (DateUtils.isToday(realDate1.time - DateUtils.DAY_IN_MILLIS)) {
                "Tomorrow"
            } else {
                outputFormat.format(realDate1)
            }
            return String.format("%s, %s - %s", localizedStartDay, timeFormat.format(realDate1), timeFormat.format(realDate2))
        }

        fun UrlIntent(context: Context, url: String) {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            context.startActivity(intent)
        }

    }
}