package com.winkoko.tunnel

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.Window
import android.widget.Button
import android.widget.TextView
import com.google.android.material.dialog.MaterialAlertDialogBuilder

/**
 * WinKoKo Tunnel - About Dialog
 * Developed by WinKoKoOo
 */
object AboutDialog {

    fun show(context: Context) {
        val dialog = Dialog(context)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        
        // Inflate custom view
        val view = LayoutInflater.from(context).inflate(R.layout.dialog_about, null)
        dialog.setContentView(view)

        // Make background transparent for rounded corners
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        val btnTelegram = view.findViewById<Button>(R.id.btnTelegram)
        val btnClose = view.findViewById<Button>(R.id.btnCloseDialog)

        btnTelegram?.setOnClickListener {
            try {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/winkoko_tunnel"))
                context.startActivity(intent)
            } catch (e: Exception) {
                // Fallback
            }
        }

        btnClose?.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setCancelable(true)
        dialog.show()
    }

    /**
     * Standard Material Alert Dialog Alternative
     */
    fun showMaterialAbout(context: Context) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.about_title))
            .setMessage(
                "WinKoKo Tunnel\n\n" +
                "★ Developed by WinKoKoOo\n" +
                "★ Engine: WireGuard / Cloudflare WARP\n" +
                "★ Package: com.winkoko.tunnel\n" +
                "★ Version: 1.0.0\n\n" +
                "Created with dedication for high-speed, secure and uncensored internet connection in Myanmar."
            )
            .setPositiveButton("OK") { d, _ -> d.dismiss() }
            .setNeutralButton("Telegram") { _, _ ->
                try {
                    val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/winkoko_tunnel"))
                    context.startActivity(browserIntent)
                } catch (_: Exception) {}
            }
            .show()
    }
}
