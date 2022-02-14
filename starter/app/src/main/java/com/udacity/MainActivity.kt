package com.udacity

import android.annotation.SuppressLint
import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityMainBinding
import com.udacity.databinding.ContentMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action
    private lateinit var binding: ActivityMainBinding
    private lateinit var contentMainBinding: ContentMainBinding
    private var source: String? = null
    private var fileName: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setSupportActionBar(toolbar)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setContentView(binding.root)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        binding.contentMain.loadingButton.setLoadingButtonState(ButtonState.Completed)
        binding.contentMain.loadingButton.setOnClickListener {
            download()
        }
    }

    private val receiver = object : BroadcastReceiver() {
        @SuppressLint("Range")
        override fun onReceive(context: Context?, intent: Intent) {
            val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            val action = intent.action

            if (downloadID == id) {
                if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
                    val query = DownloadManager.Query()
                    query.setFilterById(intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0))
                    val manager =
                        context!!.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
                    val cursor: Cursor = manager.query(query)
                    if (cursor.moveToFirst()) {
                        if (cursor.count > 0) {
                            val status =
                                cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                            if (status == DownloadManager.STATUS_SUCCESSFUL) {
                                binding.contentMain.loadingButton.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    fileName.toString(),
                                    applicationContext,
                                    getString(R.string.success)
                                )
                            } else {
                                binding.contentMain.loadingButton.setLoadingButtonState(ButtonState.Completed)
                                notificationManager.sendNotification(
                                    fileName.toString(),
                                    applicationContext,
                                    getString(R.string.failed)
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    private fun download() {
        binding.contentMain.loadingButton.setLoadingButtonState(ButtonState.Clicked)
        if (source != null) {
            binding.contentMain.loadingButton.setLoadingButtonState(ButtonState.Loading)
            initNotificationManager()

            createChannel(
                getString(R.string.load_app_download_channel_id),
                getString(R.string.load_app_download_channel_name)
            )

            // chk if cache folder exists to download the files
            createDirectoryForDownload()

            val request =
                DownloadManager.Request(Uri.parse(URL))
                    .setTitle(getString(R.string.app_name))
                    .setDescription(getString(R.string.app_description))
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setDestinationInExternalPublicDir(
                        Environment.DIRECTORY_DOWNLOADS,
                        "$DOWNLOAD_FOLDER/$fileName"
                    )

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
        } else {
            binding.contentMain.loadingButton.setLoadingButtonState(ButtonState.Completed)
            Toast.makeText(this, R.string.no_repo_selected, Toast.LENGTH_SHORT).show()
        }
    }

    private fun initNotificationManager() {
        notificationManager = ContextCompat.getSystemService(
            applicationContext,
            NotificationManager::class.java
        ) as NotificationManager
    }

    private fun createDirectoryForDownload() {
        val file = File(getExternalFilesDir(null), "/$DOWNLOAD_FOLDER")
        if (!file.exists()) {
            file.mkdirs()
        }
    }

    fun onRadioButtonClicked(view: View) {
        if (view is RadioButton) {
            if (view.isChecked) {
                when (view.getId()) {
                    R.id.rb_glide -> {
                        source = getString(R.string.git_glide_repo_url)
                        fileName = getString(R.string.glideFileName)
                    }
                    R.id.rb_git_repo -> {
                        source = getString(R.string.git_udacity_repo_url)
                        fileName = getString(R.string.loadAppFileName)
                    }
                    R.id.rb_retrofit -> {
                        source = getString(R.string.git_retrofit_repo_url)
                        fileName = getString(R.string.retrofitFileName)
                    }
                }
            }
        }
    }

    private fun createChannel(channelId: String, channelName: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel =
                NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.enableLights(true)
            notificationChannel.lightColor = Color.RED
            notificationChannel.enableVibration(true)
            notificationChannel.description = "Download Completed!"

            notificationManager.createNotificationChannel(notificationChannel)
        }
    }

    companion object {
        private const val DOWNLOAD_FOLDER = "/cache"
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
