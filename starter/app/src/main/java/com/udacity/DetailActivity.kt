package com.udacity

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {
    private var fileName: String = ""
    private var status: String = ""
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)

        ok_button.setOnClickListener {
            finish()
        }

        fileName = intent.getStringExtra("fileName").toString()
        status = intent.getStringExtra("status").toString()
        tv_file_name.text = fileName
        tv_download_status.text = status
    }

}
