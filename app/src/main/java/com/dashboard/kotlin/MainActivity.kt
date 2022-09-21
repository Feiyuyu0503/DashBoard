package com.dashboard.kotlin

import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.service.quicksettings.TileService
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import com.tencent.mmkv.MMKV
import com.topjohnwu.superuser.BusyBoxInstaller
import com.topjohnwu.superuser.Shell


lateinit var GExternalCacheDir: String
lateinit var KV: MMKV

class MainActivity : AppCompatActivity() {
    init {
        Shell.setDefaultBuilder(
            Shell.Builder.create()
                .setInitializers(BusyBoxInstaller::class.java)
                .setFlags(Shell.FLAG_REDIRECT_STDERR)
        )
    }

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

        GExternalCacheDir = applicationContext.externalCacheDir.toString()
        MMKV.initialize(this)
        KV = MMKV.defaultMMKV(MMKV.MULTI_PROCESS_MODE, null)

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