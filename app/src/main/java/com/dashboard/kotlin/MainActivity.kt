package com.dashboard.kotlin

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.service.quicksettings.TileService
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.dashboard.kotlin.MApplication.Companion.KV

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        KV.putBoolean("TailLongClick", false)
        if (intent.action == TileService.ACTION_QS_TILE_PREFERENCES) {
            val componentName =intent.extras?.get(Intent.EXTRA_COMPONENT_NAME) as ComponentName?
            componentName?:return
            Class.forName(componentName.className).newInstance().apply {
                if (this is TileButtonService) {
                    KV.putBoolean("TailLongClick", true)
                }
            }
        }
    }
}