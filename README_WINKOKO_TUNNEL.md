# WinKoKo Tunnel - Android Project
Developed by WinKoKoOo
Package Name: com.winkoko.tunnel

## Structure:
- app/build.gradle.kts (Package name com.winkoko.tunnel, WireGuard dependencies)
- app/src/main/res/values/strings.xml (Branding strings, WinKoKo Tunnel)
- app/src/main/res/values/colors.xml (Cyan/Emerald VPN dark theme)
- app/src/main/AndroidManifest.xml (VpnService permission, Android 14/15 foreground service)
- app/src/main/res/layout/nav_header.xml ("WinKoKo Tunnel - Developed by WinKoKoOo")
- app/src/main/res/layout/dialog_about.xml (About Dialog layout)
- app/src/main/java/com/winkoko/tunnel/AboutDialog.kt (Kotlin code showing WinKoKoOo credit)
- app/src/main/java/com/winkoko/tunnel/service/WinKoKoVpnService.kt (Android VpnService implementation)
- app/src/main/java/com/winkoko/tunnel/model/WarpConfig.kt (WireGuard / WARP data structure)
- app/src/main/java/com/winkoko/tunnel/MainActivity.kt (UI connection state & drawer logic)

## Developed by WinKoKoOo
For help or Telegram updates: https://t.me/winkoko_tunnel
