# Server and client time

The main rule is that server side is time zone `UTC` and client side is `Europe/Stockholm`.

## Time zones

Area|Time zone|Description
---|---|---
Database|UTC|"Date with time" is stored as `DATETIME` with format `YYYY-MM-DD HH:mm:ss`.
Server|UTC|`RosetteApplication` is calling `TimeZone.setDefault(TimeZone.getTimeZone("UTC"))` at startup.
Client|Europe/Stockholm|All "date with time" from/to client have format `YYYY-MM-DDTHH:mm:ss` and is in time zone `Europe/Stockholm`.
Test server|UTC|Tests are running at server instance and therefore `UTC` is the time zone.
