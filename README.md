# com.sailvisionpro.www #

This is the backend for www.sailvisionpro.com!

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
