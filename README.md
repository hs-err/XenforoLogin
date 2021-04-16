# SodionAuth

[English](https://github.com/Mohist-Community/SodionAuth/edit/master/README.md)
|    [中文](https://github.com/Mohist-Community/SodionAuth/blob/master/README-zh.md)

## What is this?

A new generation of Minecraft authenticate plugin.

## What can this do?

- Offer extra protection for your online Minecraft server。
- Protect your offline server。
- Through the back-end system(Xenforo，Discuz, etc.) to realize the "invitation code" function。
- Prevent player to do anything before they login.
- Synchronize game accounts and forum accounts.

## Advantages

- Adaptive rate limiting(Intelligent leaky bucket algorithm).
- Country Limitation(Using GeoIP).
- Input password directly into your chat textbox without command.
- Completely asynchronous Network/File IO request after load complete.
- FastLogin support(Pull request opened but not merged yet).
- Automate login support(Using session).
- External login support(Experimental).
- Hide almost all player status before login。
    - Location.
    - Inventory.
    - Gamemode.
    - Health.
    - Food(Including foodLevel, exhaustion and saturation).
    - Oxygen left.
    - Potion effect.
- Resume almost all player status after Login。
    - Location.
    - Gamemode.
    - Health.
    - Falling distance.
    - Velocity.
    - Food(Including foodLevel, exhaustion and saturation).
    - Oxygen left.
    - Potion effect.
- Various back-end system support.
    - Xenforo.
    - UCenter(Discuz).
    - MySQL.
    - SQLite.
    - Customized RestAPI.
- Various password handle support.
- Various platform support.
    - Bukkit.
        - Paper(Competely).
        - Mohist(Partly).
        - Spigot(Partly)
        - CraftBukkit(Partly).
    - Sponge(Partly).
    - Fabric(Experimental).

## Disadventages

- Cant retrieve password by e-mail.
- External login doesnt support skins。
- Session is not cached, resulting in poor performance.
- Stored some player status, may resulting these action **not exist any longer**, make players uncomfortable:
    - Quit server before landing from high area，re-login with no damage.
    - Quit server while no oxygen left, recovering oxygen before login.
    - Quit server because of getting debuff(Such as hungry, poison, etc.), not login until the debuff was over, not
      affected by the debuff.
    - Quit server while gliding with elytra, re-login to get slow down.

## Why not fix the disadvantages

- Some of back-end systems dont have API to change password.
- The work is too difficult, but it will be solved later.
- For Crossing server!
- Server owners are recommended withstand the pressure of players to retain this feature

## How to use?

### Install

#### Bukkit

Download the .jar file and copy it to /plugins folder, then start the server. After init complete at first start,
shutdown server.

The configurations are at /plugins/sodionauth folder.

#### Sponge & Fabric

Download the .jar file and copy it to /mods folder, then start the server. After init complete at first start, shutdown
server.

The configurations are at /config/sodionauth folder.

### Configure back-end system

#### Xenforo

Open this url in browser (which http://example.com is xenforo's access directory: http://example.com/admin.php?api-keys

Create a super-admin key with these permissions: auth & user:read.

Copy your key.

Edit config.json

````json
{
  "api": {
    "system": "xenforo","_system": "Edit this to xenforo",
    "xenforo": {
      "url": "http://example.com/api","_url": "Edit this to your xenforo url",
      "key": "XXXX0000XXXX0000XXXX00_00XXXX000","_key": "Your access key here"
    }
}
````

#### Flarum

Install composer correctly.

Run at website's root directory.

````
composer require mohist/flarum-sodion-auth
````

Enable SodionAuth extension at control panel.

Edit config.json

````json
{
  "api": {
    "system": "web","_system": "Edit this to web",
    "web": {
      "url": "http://example.com/api/sodionAuth","_url": "Edit this to your website's url",
      "key": "XXXX0000XXXX0000XXXX00_00XXXX000","_key": "Try your luck here~ flarum officially does not set rate limits."
    }
}
````

Enjoy.

#### Discuz

Download and enable SodionAuthDiscuz

Edit config.json

````json
{
  "api": {
    "system": "web","_system": "Edit this to web",
    "web": {
      "url": "http://example.com/plugin.php?id=discuz_sodion_auth","_url": "Edit to your website.",
      "key": "XXXX0000XXXX0000XXXX00_00XXXX000","_key": "Try your luck here~"
    }
}
````

#### Mysql

Edit config.json

````json
{
  "api": {
    "system": "mysql","_system": "mysql here",
    "mysql": {
      "host": "localhost:3306","_host": "The host of Mysql",
      "username": "sodionauth","_username": "Mysql username",
      "password": "sodionauth","_password": "Mysql password",
      "database": "sodionauth","_database": "Database name (not create automated)",
      "tableName": "users","_tableName": "The table name that stores user data (not recommended to modify)",
      "emailField": "email","_emailField": "The field name of the mailbox (not recommended to modify)",
      "usernameField": "username","_usernameField": "The field name of the username (not recommended to modify)",
      "passwordField": "password","_passwordField": "The field name of the password (not recommended to modify)",
      "saltField": "salt","_saltField": "The field name of the salt (not recommended to modify)",
      "saltLength": 6,"_saltLength": "The length of the salt (not recommended to modify)",
      "passwordHash": "BCrypt","_passwordHash": "Irreversible encryption method of password, see: How to choose encryption method?"
    }
}
````

#### Sqlite

Edit config.json

````json
{
  "api": {
    "system": "sqlite","_system": "sqlite here",
    "sqlite": {
      "path": "Users.db","_path": "The path of the database",
      "absolute": false,"_absolute": "Whether path is an absolute path",
      "tableName": "users","_tableName": "The table name that stores user data (not recommended to modify)",
      "emailField": "email","_emailField": "The field name of the mailbox (not recommended to modify)",
      "usernameField": "username","_usernameField": "The field name of the username (not recommended to modify)",
      "passwordField": "password","_passwordField": "The field name of the password (not recommended to modify)",
      "saltField": "salt","_saltField": "The field name of the salt (not recommended to modify)",
      "saltLength": 6,"_saltLength": "The length of the salt (not recommended to modify)",
      "passwordHash": "BCrypt","_passwordHash": "Irreversible encryption method of password, see: How to choose encryption method?"
    }
}
````

### Start server and enjoy

## How to choose encryption method?

| Method    | Security |
| ---------- | ---------- |
| BCrypt     | Very High, Easy to migrate to modern applications like Xenforo |
| Plain      | None |
| MD5        | Very Low |
| MD5Salt    | Common, Easy to migrate to the applications which need to support older versions like Discuz |
| SHA1       | Common |
| SHA1Salt   | High |
| SHA224     | Common |
| SHA224Salt | High |
| SHA256     | Common |
| SHA256Salt | High |
| SHA384     | Common |
| SHA384Salt | High |
| SHA512     | Common |
| SHA512Salt | Quite High |

If need to migrate to modern applications like [Blessing Skin](https://github.com/bs-community/blessing-skin-server)
, [Xenforo](https://xenforo.com) or [flarum](https://flarum.org), use BCrypt.

If need to migrate to applications like [Discuz](https://www.discuz.net)[PHPWind](http://www.phpwind.net.cn), use
MD5Salt.

Others please choose according to your needs, if not necessary, do not use Plain.

## Download link

[Stable and Preview version](https://github.com/Mohist-Community/SodionAuth/releases)

[Develop Build](https://ci.ishland.com:43333/job/SodionAuth)

## Report problem? New feature request?

[GitHub issues](https://github.com/Mohist-Community/SodionAuth/issues)

English only.

If you have bad English, you can raise it here, but doesn’t read it very often, so it’s best to raise it in GitHub
issues.

## License

Copyright 2020 Mohist-Community

Licensed under the [Apache License, Version 2.0](licenses/apache-2.0.txt)
