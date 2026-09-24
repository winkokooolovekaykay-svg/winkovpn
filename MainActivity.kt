package com.winkoko.tunnel

import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.VpnService
import android.os.Bundle
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.material.navigation.NavigationView
import com.winkoko.tunnel.databinding.ActivityMainBinding
import com.winkoko.tunnel.model.WarpConfig
import com.winkoko.tunnel.service.WinKoKoVpnService

/**
 * WinKoKo Tunnel - Main User Interface
 * Developed by WinKoKoOo
 */
class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: ActivityMainBinding
    private var isConnected = false

    // Register Activity Result Launcher for VPN Permission Dialog
    private val vpnPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // Permission Granted -> Start VPN Service
            startTunnelService()
        } else {
            Toast.makeText(this, "VPN permission is required to create secure tunnel", Toast.LENGTH_SHORT).show()
            updateUiState(WinKoKoVpnService.STATUS_DISCONNECTED)
        }
    }

    // BroadcastReceiver listening for real-time status from WinKoKoVpnService
    private val vpnStatusReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == WinKoKoVpnService.ACTION_VPN_STATUS_CHANGED) {
                val status = intent.getStringExtra(WinKoKoVpnService.EXTRA_STATUS) ?: return
                val message = intent.getStringExtra(WinKoKoVpnService.EXTRA_MESSAGE) ?: ""
                val bytesIn = intent.getLongExtra(WinKoKoVpnService.EXTRA_BYTES_IN, 0L)
                val bytesOut = intent.getLongExtra(WinKoKoVpnService.EXTRA_BYTES_OUT, 0L)

                updateUiState(status, message, bytesIn, bytesOut)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Setup Toolbar & Navigation Drawer
        setupNavigationDrawer()

        // Setup Big Connect / Disconnect Toggle Button
        binding.btnPowerConnect.setOnClickListener {
            if (isConnected) {
                stopTunnelService()
            } else {
                prepareAndConnectVpn()
            }
        }

        // Initialize UI with current state
        updateUiState(WinKoKoVpnService.currentStatus)
    }

    override fun onStart() {
        super.onStart()
        // Register Local Broadcast Receiver for instant status updates
        val filter = IntentFilter(WinKoKoVpnService.ACTION_VPN_STATUS_CHANGED)
        LocalBroadcastManager.getInstance(this).registerReceiver(vpnStatusReceiver, filter)
    }

    override fun onStop() {
        super.onStop()
        // Unregister to prevent memory leaks
        LocalBroadcastManager.getInstance(this).unregisterReceiver(vpnStatusReceiver)
    }

    /**
     * Step (2) Requirement: Setup Navigation Drawer & About Dialog Trigger
     */
    private fun setupNavigationDrawer() {
        setSupportActionBar(binding.toolbar)
        val toggle = ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.nav_home,
            R.string.nav_home
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        binding.navigationView.setNavigationItemSelectedListener(this)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_about -> {
                // Show About Dialog highlighting "Developed by WinKoKoOo"
                AboutDialog.show(this)
            }
            R.id.nav_servers -> {
                Toast.makeText(this, "Clean Cloudflare IPs selected", Toast.LENGTH_SHORT).show()
            }
            R.id.nav_apps -> {
                Toast.makeText(this, "Split tunneling settings", Toast.LENGTH_SHORT).show()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    /**
     * Step (3) Requirement: Prepare Android VpnService and Connect
     */
    private fun prepareAndConnectVpn() {
        val vpnIntent = VpnService.prepare(this)
        if (vpnIntent != null) {
            // Android OS requires user prompt to authorize VPN configuration
            vpnPermissionLauncher.launch(vpnIntent)
        } else {
            // Already authorized -> Direct start
            startTunnelService()
        }
    }

    private fun startTunnelService() {
        val intent = Intent(this, WinKoKoVpnService::class.java).apply {
            action = WinKoKoVpnService.ACTION_CONNECT
            putExtra(WinKoKoVpnService.EXTRA_CONFIG, WarpConfig.defaultWarp())
        }
        startService(intent)
        updateUiState(WinKoKoVpnService.STATUS_CONNECTING)
    }

    private fun stopTunnelService() {
        val intent = Intent(this, WinKoKoVpnService::class.java).apply {
            action = WinKoKoVpnService.ACTION_DISCONNECT
        }
        startService(intent)
        updateUiState(WinKoKoVpnService.STATUS_DISCONNECTING)
    }

    /**
     * Update UI based on VpnService Broadcast status
     */
    private fun updateUiState(
        status: String,
        message: String = "",
        bytesIn: Long = 0L,
        bytesOut: Long = 0L
    ) {
        when (status) {
            WinKoKoVpnService.STATUS_CONNECTED -> {
                isConnected = true
                binding.tvConnectionStatus.text = getString(R.string.status_connected)
                binding.tvConnectionStatus.setTextColor(getColor(R.color.status_connected_green))
                binding.btnPowerConnect.setImageResource(R.drawable.ic_lock_power_snapshot)
                binding.btnPowerConnect.setBackgroundResource(R.drawable.bg_power_button_connected)
                binding.tvActionPrompt.text = getString(R.string.btn_disconnect)
                binding.pulseView.startPulseAnimation()
            }
            WinKoKoVpnService.STATUS_CONNECTING -> {
                isConnected = false
                binding.tvConnectionStatus.text = getString(R.string.status_connecting)
                binding.tvConnectionStatus.setTextColor(getColor(R.color.status_connecting_amber))
                binding.tvActionPrompt.text = getString(R.string.btn_connecting)
            }
            WinKoKoVpnService.STATUS_DISCONNECTING -> {
                isConnected = false
                binding.tvConnectionStatus.text = getString(R.string.status_disconnecting)
                binding.tvConnectionStatus.setTextColor(getColor(R.color.status_connecting_amber))
            }
            WinKoKoVpnService.STATUS_ERROR -> {
                isConnected = false
                binding.tvConnectionStatus.text = "Connection Error: $message"
                binding.tvConnectionStatus.setTextColor(getColor(R.color.status_disconnected_red))
                binding.tvActionPrompt.text = getString(R.string.btn_reconnect)
                binding.pulseView.stopPulseAnimation()
            }
            else -> { // DISCONNECTED
                isConnected = false
                binding.tvConnectionStatus.text = getString(R.string.status_disconnected)
                binding.tvConnectionStatus.setTextColor(getColor(R.color.status_disconnected_red))
                binding.btnPowerConnect.setBackgroundResource(R.drawable.bg_power_button_disconnected)
                binding.tvActionPrompt.text = getString(R.string.btn_connect)
                binding.pulseView.stopPulseAnimation()
            }
        }

        // Update real-time stats if available
        if (bytesIn > 0 || bytesOut > 0) {
            binding.tvDownloadStat.text = formatBytes(bytesIn)
            binding.tvUploadStat.text = formatBytes(bytesOut)
        }
    }

    private fun formatBytes(bytes: Long): String {
        return when {
            bytes >= 1024 * 1024 * 1024 -> String.format("%.2f GB", bytes / (1024.0 * 1024.0 * 1024.0))
            bytes >= 1024 * 1024 -> String.format("%.2f MB", bytes / (1024.0 * 1024.0))
            bytes >= 1024 -> String.format("%.2f KB", bytes / 1024.0)
            else -> "$bytes B"
        }
    }
}
