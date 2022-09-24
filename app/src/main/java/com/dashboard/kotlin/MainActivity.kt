package com.dashboard.kotlin

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.service.quicksettings.TileService
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.BusyBoxInstaller
import com.topjohnwu.superuser.Shell

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
        this.window.statusBarColor = ResourcesCompat.getColor(
            resources,
            android.R.color.transparent,
            applicationContext?.theme
        )
        this.window.navigationBarColor = ResourcesCompat.getColor(
            resources,
            android.R.color.transparent,
            applicationContext?.theme
        )

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