<div align="center">

## Happwner · derskii edition

**Your subscriptions. Your choice.**

[![Android](https://github.com/derskii/Happwner/actions/workflows/android.yml/badge.svg)](https://github.com/derskii/Happwner/actions/workflows/android.yml)
[![Release](https://img.shields.io/github/v/release/derskii/Happwner?color=b89af3)](https://github.com/derskii/Happwner/releases/latest)
![Android 5.0+](https://img.shields.io/badge/Android-5.0%2B-a9efcd)

[**Download APK**](https://github.com/derskii/Happwner/releases/latest) · [**Project website**](https://derskii.github.io/Happwner/) · [**Русский**](README_RU.md) · [Release notes](RELEASE_NOTES.md)

</div>

An independent, **non-commercial** fork of [Omegaplexx/Happwner](https://github.com/Omegaplexx/Happwner), originally developed by **Omegaplex and slavrom21**.

### What's different?

| Feature | This fork |
| --- | --- |
| Request device | Android, Android TV, iOS, Windows, macOS, Linux; saved and included in Bridge URLs |
| User-Agent | Updated Happ/INCY; added v2rayNG, Hiddify, sing-box and mihomo; editable custom values |
| File export | Full processed result as UTF-8 `.json` / `.txt` through Android's document picker |
| Large subscriptions | Background conversion, bounded 15,000-character pages, full export preserved |
| Error handling | HTTP 500 JSON is an error, not a successful subscription; Bridge preserves failure status |
| Regression tests | 500/1000 JSON configs, malformed/deep JSON, HTTP errors, request metadata and preview bounds |

> **Installation:** this fork uses its own signing key. It cannot update the original APK in place. Save your data and uninstall the original first. Package ID is retained for Xposed compatibility. Later fork releases use the same persistent key.

Device profiles set request metadata (`x-device-os`, `x-device-model`, Android's `x-ver-os`) and the platform suffix of plain Happ presets. They do not change hardware identity or guarantee provider compatibility. Custom User-Agents and the HWID field remain under your control. To export original JSON rather than converted links, disable the relevant conversion settings before fetching.

### Build & verification

JDK 17 and Android SDK 36:

```sh
bash gradlew testDebugUnitTest lintDebug assembleDebug
```

Tagged releases additionally build an R8-optimized APK, sign it using repository secrets, verify the APK signature and publish checksums. Secrets and private keys are never committed. Automated tests do not imply testing on a physical phone.

### User-Agent snapshot

Updated 2026-11-05 from upstream releases: [Happ 4.10.1](https://github.com/Happ-proxy/happ-android/releases/tag/4.10.1), [INCY Android 3.9.9](https://github.com/INCY-DEV/incy-platforms/releases/tag/latest), [v2rayNG 2.3.15](https://github.com/2dust/v2rayNG/releases/tag/2.3.15), [Hiddify 5.5.5](https://github.com/hiddify/hiddify-app/releases/tag/v5.5.5), [sing-box 1.14.4](https://github.com/SagerNet/sing-box/releases/tag/v1.14.4), [mihomo 1.19.33](https://github.com/MetaCubeX/mihomo/releases/tag/v1.19.33). v2RayTun retains its platform-style `v2raytun/android` identifier. Supported installed Android app versions take precedence.

---

## Original project documentation

<p align="center">
  <b>English</b> |
  <a href="README_RU.md">Русский</a>
</p>

# Happwner

<div align="center">
<img width="100" height="100" alt="Happwner icon" src="https://github.com/user-attachments/assets/93bc69a7-82b3-44b3-a577-52d6b56edc51" />

An Android app and Xposed module for exporting subscriptions from the proxy utility [Happ](https://play.google.com/store/apps/details?id=com.happproxy) into any VPN apps (NekoBox, v2rayNG, Hiddify, and similar clients).

**Android 5.0+** is supported.
</div>

## Screenshots

<div>
<img src="https://github.com/user-attachments/assets/11e8ce9d-02d6-4c9f-943d-d94277290ab8" width="30%" />
<img src="https://github.com/user-attachments/assets/b91e0269-4175-4111-b439-9972d13a0529" width="30%" />
<img src="https://github.com/user-attachments/assets/8c71d7e2-112b-445f-b1e5-53c30eaa7a74" width="30%" />
<img src="https://github.com/user-attachments/assets/f339409c-5505-49a3-9a2c-b7aa8a2837fe" width="30%" />
<img src="https://github.com/user-attachments/assets/c37acc91-86d3-401b-abd4-eba543a424a3" width="30%" />
<img src="https://github.com/user-attachments/assets/4d943fcf-a480-4396-9eb6-81b1235ae779" width="30%" />
<img src="https://github.com/user-attachments/assets/3293a5a3-aa3e-4d57-a719-ce6cdb1ce4f8" width="30%" />
</div>

## Features

**Happwner** extracts clean VPN configurations and transfers them into any application. You do <u>not</u> need to install Happ to use all of its subscriptions.

Features:

* **Link decryption**. Supported formats: `happ://crypt`, `crypt2`, `crypt3`, `crypt4`, `crypt5`. Decryption does <u>not require</u> Happ, Xposed, LSPatch/NPatch, or an internet connection.
* **Subscription profile decryption**. If a provider encrypted their servers using one of the ten keys built into Happ, the app will automatically decrypt them and display the corresponding message.
* **HWID spoofing** when requesting subscriptions <u>through the Happwner interface</u>. Allows bypassing device limits and restoring access to subscriptions after changing devices;
* **"Bridge"** — a built-in service for updating Happ subscriptions inside any apps (NekoBox, Hiddify, v2rayNG, husi, Exclave, Karing, and others);
* **Intent link handling** for `happ://add` and `happ://crypt`, instead of Happ itself.

### Xposed Features

* **Link interception**. An alternative method for decrypting `crypt` links. Works in all applications and may be useful if decryption keys are unavailable (for example, when `crypt6` appears);
* **HWID spoofing <u>inside Happ</u>** and other apps marked as targets in the Xposed/LSPosed manager;
* **Unlocking encrypted subscription profiles inside Happ**. Forces profile arrows to appear and allows exporting configurations even when the subscription is encrypted;
* **Quick access to the Happwner interface** via a three-finger tap gesture while Happ or another target app is open.

**Xposed functionality requires Xposed / EdXposed / LSPosed / Vector, OR a patched version of Happ created using Xpatch / LSPatch / NPatch / FPA.**

## Installation

1. Install the Happwner APK from the [Releases](https://github.com/Omegaplexx/Happwner/releases) page.

The app supports **Android 5.0 (Lollipop)** and above.

### Xposed (Optional)

#### Root

1. Install **Happwner** and **Happ**;
2. Enable **Happwner** in your **Xposed** manager and select the recommended apps;
3. Restart **Happ**.

#### Without Root

1. Install **Happwner**, **Happ**, and [NPatch](https://github.com/7723mod/NPatch/releases/tag/v1.0.2) (version **1.0.2** is recommended);
2. In **NPatch**: *Manage tab* → *"+" button* → *Select an installed app* → ***Happ*** → *Integrated mode* → *Embed Modules* → ***Happwner*** → *Start Patch*.

**NPatch does not notify the user when the patched app has been installed successfully. Tap "Install", wait a few seconds, then check whether Happ appears in your application list.**

## Why Happwner?

To give users control over their subscriptions again, improve service transparency, and protect themselves from dishonest providers.

**Happ** is not just a VPN client, but part of a commercial ecosystem aimed at VPN providers. It gives them extended capabilities by introducing restrictions for users.

* Encrypted links (`happ://crypt5`) hide the real URL and prevent viewing server configurations;
* The hardware identifier (**HWID**) tightly binds a purchased subscription to a specific device;
* The lack of advanced settings in Happ makes it impossible to hide traffic from questionable providers using proxy chains;
* If a [ProviderID](https://www.happ.su/main/ru/dev-docs/provider-id) is embedded into a `crypt5` link, the app will once a day compare the subscription domain hash, app version, and OS version against the data specified in the seller's dashboard on [happ-proxy.com](https://happ-proxy.com);
* A seller who added a **ProviderID** to an encrypted link gains the ability to [remotely manage the application](https://www.happ.su/main/ru/dev-docs/app-management) without user interaction. Happ allows them to:
  * Force-enable HWID transmission even if it is disabled in settings;
  * Block manual User-Agent modification while still allowing remote changes;
  * Manage local SOCKS and HTTP proxies by reconfiguring or disabling authentication;
  * Force users to connect to a specific server when launching Happ;
  * Disable global routing in Happ;
  * Configure application proxying by adding or removing exclusions;
  * Hide VPN servers depending on the connection type (Wi-Fi / mobile network);
  * Configure automatic server testing (auto-ping) when opening the app;
  * Control the `ping` type (`via Proxy - GET/HEAD`, `TCP`, `ICMP`) and specify a custom URL for server availability checks;
  * Change the subscription URL;
  * Set the subscription auto-update interval;
  * Enable Happ auto-start on device boot;
  * Force-update all subscriptions every time the app starts;
  * Expand the server list after subscription updates or completely disable collapsing;
  * Pin or unpin subscriptions in the main list;
  * Change server sorting order alphabetically or by ping;
  * Control traffic multiplexing.

**The list is incomplete.** Management parameters are sent through HTTP headers and the response body with each subscription update.

## Acknowledgements

* **[slavrom21](https://github.com/21slavrom)** for his invaluable contribution to the project (and most of the hard work): reverse-engineering Happ to decrypt profiles, the Xray-to-sing-box converter, profile unlocking via Xposed, new animations, Monet support, and many other improvements.

## Terms of Use

You may use, copy, distribute, modify, and build this software for non-commercial purposes, provided that proper attribution is given to the authors (**Omegaplex**, **slavrom21**) and a link to the source repository is included (https://github.com/Omegaplexx/Happwner).

Commercial use, sale, monetization, inclusion in commercial products, or generating profit from this software without the author's permission is prohibited.

## Disclaimer

I assume no obligations toward you as a user, do not guarantee the software will function correctly, and am not responsible for any actions you take.

**Happ** attempts to prevent VPN server reselling by restricting user actions. I remove those restrictions and, in doing so, restore the ability to use servers for purposes beyond the provider's intended limitations. **I condemn any activity that harms a VPN provider's infrastructure and encourage using Happwner responsibly**: the program is intended for personal convenience and for sharing configurations with friends and family. Sharing Internet access is not wrong, as long as you do not create problems for the people providing it.
