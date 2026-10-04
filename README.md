# NORA TUNNEL

Secure. Private. Connected.

NORA TUNNEL is a local-first Android VPN/tunnel management application.

## Platform

- Android 10+
- Kotlin
- Jetpack Compose
- Material 3
- Android VpnService
- Room
- DataStore
- Encrypted Android storage

## Core Features

- VPN profile management
- Configuration import and validation
- Protocol/core capability validation
- Routing rules
- DNS configuration
- Per-app VPN routing
- Network diagnostics
- Connection history
- Privacy-safe logs
- Diagnostic export
- Security status
- Live traffic statistics

## Supported Architecture

The application separates:

UI
→ ViewModel
→ Repository
→ Tunnel Manager
→ Tunnel Adapter
→ Android VpnService

## Security

NORA TUNNEL does not intentionally use fake connection states or fake network statistics.

Sensitive information is redacted from logs and diagnostic exports.

See:

- `PRIVACY.md`
- `SECURITY.md`

## Build

From the project root:

```bash
./gradlew assembleDebug
