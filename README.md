# www #

This is the backend for www.sailvision.net!

## Development ##

Run the web server with the following, which will automatically reload the page
when changes are saved:

    lein ring server-headless 8080

The server can also be run as it will be in production with the following:

    lein run

## Deployment ##

Whenever the main branch is updated, GitHub is configured to automatically build
and deploy a standalone uberjar. Deployment itself is done by POSTing the new
uberjar to `/i/deploy`, using the worker's OIDC token for authentication. The
server writes the new uberjar to a temporary location on disk and shuts itself
down. systemd is configured to always restart the service and, before execution,
move the new uberjar to its permanent location on disk.

This is a fragile setup, since a working server is required to deploy new code.

### Configuration ###

| Environment Variable | Description                                           |
|:---------------------|:------------------------------------------------------|
| `NEXT_PATH`          | Location to write the uploaded uberjar for deployment |
| `DB_STOARGE`         | Location of the database, or `:mem`                   |
| `PRETTY_PRINT`       | Include formatting in generated CSS                   |
| `POSTHOG`            | Include PostHog tracker on store page                 |

## Admin Access ##

The server has two endpoints on which it listens: a publicly available one and a
private one. This private one is hidden and only accessible via a WireGuard
tunnel at <https://www.i.sailvision.net/admin>. In order to get access, install
the WireGuard app on your device(s) and configure the tunnel as follows:

```ini
[Interface]
PrivateKey = ******
Address = <next available IP>/32

[Peer]
PublicKey = 4e0bwu5q6fInesM6T3BCgxJ4exrzhG4wbL5vDqJ3gAM=
AllowedIPs = 172.62.4.1/32
Endpoint = www.sailvision.net:63532
PersistentKeepalive = 25
```

The next available IP can be determined by looking at the list of admins in the
[machine configuration](./nixops.nix). Once the public key and IP are added, the
machine can be redeployed and access will be granted.
