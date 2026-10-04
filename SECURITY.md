# NORA TUNNEL — Security Policy

## Security Principles

NORA TUNNEL follows these principles:

1. Never fake a successful VPN connection.
2. Never expose credentials in logs.
3. Never hardcode private keys or passwords.
4. Validate imported configurations before saving them.
5. Use Android VPN APIs for the VPN interface.
6. Store sensitive credentials using encrypted storage.
7. Report unavailable functionality honestly.

## Secret Storage

Sensitive credentials must use encrypted Android storage.

Do not replace encrypted storage with ordinary SharedPreferences.

## Logging

Sensitive values must be redacted before logging.

Examples:

password=******
privateKey=******
token=******
psk=******

## Configuration Validation

Imported configuration data must be:

1. Parsed
2. Validated
3. Normalized only when safe
4. Previewed
5. Saved only after user confirmation

Invalid protocol/core/transport combinations must be rejected.

## VPN State

The application must never display `CONNECTED` unless the underlying VPN/tunnel is actually connected.

Unavailable tunnel cores must return an explicit unavailable/error state.

## Cryptography

NORA TUNNEL does not implement custom cryptographic algorithms.

Established platform and protocol implementations should be used instead.

## Diagnostics

Diagnostic exports must be privacy-safe and must never include:

- Passwords
- Private keys
- Tokens
- PSKs
- Authentication headers
- Raw secret configuration values

## Reporting

Security issues should be reported privately to the project maintainer before public disclosure.
