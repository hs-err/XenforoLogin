# SodionAuth

Prevent minecraft username stealing with Xenforo and more.

Development builds: https://ci.ishland.com:43333/job/XenforoLogin/

## Fabric builds
Fabric support is still WIP. Do NOT use them unless you would like to help us. 

## Description
- Provide an extra protection for your online server.
- Secure your offline server.
- Protect your server further with "invitation code" of forum systems.
- Prevent players doing almost everything unless they are authenticated.

## Features
- Hide inventory unless they are authenticated with ProtocolLib.
- Hide location unless they are authenticated.
- Type password directly in chat.
- Asynchronous networking
- Supported backends:
  - Xenforo via RestAPI
  - UCenter (Discuz) via php script
  - MySQL via socket
  - SQLite
  - Customized RestAPI
- Multi-platform support:
  - Paper (Full)
  - Mohist (Full)
  - Spigot (Partial)
  - CraftBukkit (Partial)
  - Sponge (Experimental)

## Planned features
- Support for fabric servers. (currently in development)
- Session login.

## Usage
Moved to wiki.

## License

[LICENSE.md](LICENSE.md)