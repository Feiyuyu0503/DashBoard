package com.dashboard.kotlin

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import android.util.Log
import com.dashboard.kotlin.clashhelper.ClashStatus
import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.*

@Suppress("OPT_IN_IS_NOT_ENABLED")
@OptIn(DelicateCoroutinesApi::class)
class TileButtonService : TileService() {
    companion object {
        const val TAG = "TileButtonService"
    }

    override fun onTileAdded() {
        if (!Shell.cmd("su -c 'exit'").exec().isSuccess) {
            qsTile.state = Tile.STATE_UNAVAILABLE
            qsTile.updateTile()
        }
    }

    private lateinit var statusJob: Job
    override fun onStartListening() {
        Log.d(TAG, "onStartListening: ")
        qsTile.label = getString(R.string.app_name)
        qsTile.updateTile()
        statusJob = GlobalScope.launch {
            while (true) {
                when(ClashStatus.getRunStatus()) {
                    ClashStatus.Status.CmdRunning -> {
                        qsTile.state = Tile.STATE_UNAVAILABLE
                        qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_web_dashboard)
                    }
                    ClashStatus.Status.Running -> {
                        qsTile.state = Tile.STATE_ACTIVE
                        qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_activited)
                    }
                    ClashStatus.Status.Stop -> {
                        qsTile.state = Tile.STATE_INACTIVE
                        qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_service_not_running)
                    }
                }
                qsTile.updateTile()
                delay(500)
            }
        }
    }

    override fun onStopListening() {
        Log.d(TAG, "onStopListening: ")
        runCatching { statusJob.cancel() }
        qsTile.label = getString(R.string.app_name)+'X'
        qsTile.updateTile()
    }

    override fun onClick() {
        Log.d(TAG, "onClick: ")
        runBlocking {
            ClashStatus.switch()
        }
    }
}