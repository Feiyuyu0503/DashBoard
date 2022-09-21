package com.dashboard.kotlin

import android.graphics.drawable.Icon
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
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

    lateinit var statusJob: Job
    override fun onStartListening() {
        statusJob = GlobalScope.launch {
            while (true) {
                ClashStatus.getRunStatus {
                     when(it) {
                        ClashStatus.Status.CmdRunning -> {
                            qsTile.state =Tile.STATE_UNAVAILABLE
                            qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_web_dashboard)
                        }
                        ClashStatus.Status.Running -> {
                            qsTile.state =Tile.STATE_ACTIVE
                            qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_activited)
                        }
                        ClashStatus.Status.Stop -> {
                            qsTile.state =Tile.STATE_INACTIVE
                            qsTile.icon = Icon.createWithResource(applicationContext, R.drawable.ic_service_not_running)
                        }
                    }
                    qsTile.updateTile()
                }
                delay(500)
            }
        }
    }

    override fun onStopListening() {
        runCatching { statusJob.cancel() }
    }

    override fun onClick() {
        ClashStatus.switch()
    }
}