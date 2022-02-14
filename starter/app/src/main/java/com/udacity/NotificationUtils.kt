package com.udacity

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat

private const val NOTIFICATION_ID = 0
private const val REQUEST_CODE = 0
private const val FLAGS = 0


fun NotificationManager.sendNotification(
    messageBody: String,
    mContext: Context,
    status: String
) {
    val contentIntent = Intent(mContext, DetailActivity::class.java)
    contentIntent.apply {
        putExtra("fileName", messageBody)
        putExtra("status", status)
    }

    val contentPendingIntent = PendingIntent.getActivity(
        mContext,
        NOTIFICATION_ID,
        contentIntent,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val action = NotificationCompat.Action.Builder(
        0,
        mContext.getString(R.string.show_details),
        contentPendingIntent
    ).build()
    val notificationBuilder = NotificationCompat.Builder(
        mContext,
        mContext.getString(R.string.load_app_download_channel_id)
    )
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(mContext.getString(R.string.download_complete))
        .setContentText(messageBody)
        .setContentIntent(contentPendingIntent)
        .setAutoCancel(true)
        .setPriority(NotificationCompat.PRIORITY_HIGH)
        .addAction(action)

    notify(NOTIFICATION_ID, notificationBuilder.build())
}