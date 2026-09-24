package com.winkoko.tunnel.model

import java.io.Serializable

/**
 * WireGuard / Cloudflare WARP Configuration Model
 * WinKoKo Tunnel - Developed by WinKoKoOo
 */
data class WarpConfig(
    val privateKey: String,
    val publicKey: String,
    val interfaceAddress: String = "172.16.0.2",
    val ipv6Address: String? = "2606:4700:110:8735:674e:c060:3da8:4eb6",
    val dnsServer: String = "1.1.1.1",
    val endpoint: String = "162.159.192.1:2408", // Cloudflare WARP Endpoint
    val allowedIPs: String = "0.0.0.0/0, ::/0",
    val mtu: Int = 1280,
    val reserved: ByteArray = byteArrayOf(0, 0, 0)
) : Serializable {

    companion object {
        fun defaultWarp(): WarpConfig {
            return WarpConfig(
                privateKey = "aG9tZW1hZGUtc2VjcmV0LWtleS13aW5rb2tvLXR1bm5lbA==",
                publicKey = "bm9uc2Vuc2UtcHVibGljLWtleS1jbG91ZGZsYXJlLXdhcnA=",
                endpoint = "engage.cloudflareclient.com:2408"
            )
        }

        /**
         * Myanmar ISP Bypass Clean IPs for Cloudflare
         */
        val CLEAN_WARP_ENDPOINTS = listOf(
            "162.159.192.1:2408",
            "162.159.193.10:2408",
            "162.159.195.5:2408",
            "188.114.96.1:2408",
            "188.114.97.10:2408"
        )
    }
}
