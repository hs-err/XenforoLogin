# SodionAuth

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
  - Quit server because of getting debuff(Such as hungry, poison, etc.), not login until the debuff was over, not affected by the debuff.
  - Quit server while gliding with elytra, re-login to get slow down.

## Why not fix the disadvantages

- Some of back-end systems dont have API to change password.
- The work is too difficult, but it will be solved later.
- For Crossing server!
- 建议腐竹顶住玩家的压力保留此特性

## 怎么用？

### 安装插件

####  Bukkit

下载.jar文件并将其复制到 /plugins 文件夹，然后启动服务器。一切开始之后，关闭服务器。

配置目录位于 /plugins/sodionauth

####  Sponge，Fabric

下载.jar文件并将其复制到 /mods 文件夹，然后启动服务器。一切开始之后，关闭服务器。

配置目录位于 /config/sodionauth

### 配置后端

####  Xenforo

在浏览器中打开（其中 http://example.com 为xenforo访问目录）：http://example.com/admin.php?api-keys

创建一个具有 auth 和 user:read 权限的超级管理员密钥。

复制您的密钥。

编辑config.json
````json
{
  "api": {
    "system": "xenforo","_system": "这里改为xenforo。",
    "xenforo": {
      "url": "http://example.com/api","_url": "这里改为你网站的api地址。",
      "key": "XXXX0000XXXX0000XXXX00_00XXXX000","_key": "这里改为您刚刚复制的密钥。"
    }
}
````

#### Flarum

正确安装composer

在网站根目录运行

````
composer require mohist/flarum-sodion-auth
````

在控制面板启用SodionAuth拓展

编辑config.json。

````json
{
  "api": {
    "system": "web","_system": "这里改为web。",
    "web": {
      "url": "http://example.com/api/sodionAuth","_url": "这里改为你网站的地址。",
      "key": "XXXX0000XXXX0000XXXX00_00XXXX000","_key": "这里随缘吧～毕竟flarum官方也没作速率限制。"
    }
}
````

享受

####  Discuz

将支持文件下载到网站根目录。

建议将文件名更改为不易猜测的文件名，例如 sgdwdegj3hr8h3uf2hewh.php。

复制你的UCenter密钥（一般在 管理面板->Ucenter->应用管理->Discuz! Board后面的编辑->通讯密钥）。

编辑config.json。

````json
{
  "api": {
    "system": "web","_system": "这里改为web。",
    "web": {
      "url": "http://example.com/sgdwdegj3hr8h3uf2hewh.php","_url": "这里改为你上传的支持文件的地址。",
      "key": "XXXX0000XXXX0000XXXX00_00XXXX000","_key": "这里改为您刚刚复制的密钥。"
    }
}
````

####  Mysql

编辑config.json。

````json
{
  "api": {
    "system": "mysql","_system": "这里改为mysql。",
    "mysql": {
      "host": "localhost:3306","_host": "mysql的地址。",
      "username": "sodionauth","_username": "mysql的用户名。",
      "password": "sodionauth","_password": "mysql的密码。",
      "database": "sodionauth","_database": "数据库名（不会自动创建）。",
      "tableName": "users","_tableName": "存储用户的表名（不建议修改）。",
      "emailField": "email","_emailField": "邮箱的字段名（不建议修改）。",
      "usernameField": "username","_usernameField": "用户名的字段名（不建议修改）。",
      "passwordField": "password","_passwordField": "密码的字段名（不建议修改）。",
      "saltField": "salt","_saltField": "盐的字段名（不建议修改）。",
      "saltLength": 6,"_saltLength": "盐的长度（不建议修改）。",
      "passwordHash": "BCrypt","_passwordHash": "密码不可逆加密方式，详见：加密方式好多呀，我该怎么选？"
    }
}
````

####  Sqlite

编辑config.json。

````json
{
  "api": {
    "system": "sqlite","_system": "这里改为sqlite。",
    "sqlite": {
      "path": "Users.db","_path": "数据库路径。",
      "absolute": false,"_absolute": "path是否为绝对路径。",
      "tableName": "users","_tableName": "存储用户的表名（不建议修改）。",
      "emailField": "email","_emailField": "邮箱的字段名（不建议修改）。",
      "usernameField": "username","_usernameField": "用户名的字段名（不建议修改）。",
      "passwordField": "password","_passwordField": "密码的字段名（不建议修改）。",
      "saltField": "salt","_saltField": "盐的字段名（不建议修改）。",
      "saltLength": 6,"_saltLength": "盐的长度（不建议修改）。",
      "passwordHash": "BCrypt","_passwordHash": "密码不可逆加密方式，详见：加密方式好多呀，我该怎么选？"
    }
}
````

### 启动服务器并享受

## 加密方式好多呀，我该怎么选？

| 加密方式    | 建议 |
| ---------- | ---------- |
| BCrypt     | 安全性极高，便于迁移到Xenforo等现代化应用 |
| Plain      | 安全性无 |
| MD5        | 安全性极低 |
| MD5Salt    | 安全性中，便于迁移到Discuz等需要照顾旧版本的应用 |
| SHA1       | 安全性中 |
| SHA1Salt   | 安全性略高 |
| SHA224     | 安全性中 |
| SHA224Salt | 安全性略高 |
| SHA256     | 安全性中 |
| SHA256Salt | 安全性略高 |
| SHA384     | 安全性中 |
| SHA384Salt | 安全性略高 |
| SHA512     | 安全性中 |
| SHA512Salt | 安全性略高 |

如果有迁移到[Blessing Skin](https://github.com/bs-community/blessing-skin-server)[Xenforo](https://xenforo.com)[flarum](https://flarum.org)等现代化应用的需求，请使用BCrypt

如果有迁移到[Discuz](https://www.discuz.net)[PHPWind](http://www.phpwind.net.cn)等应用的需求，请使用MD5Salt

其他请根据需求自行选择，如非必要，请勿选择Plain。


## 你们要的下载地址

[https://github.com/Mohist-Community/SodionAuth/releases](稳定版和预发布版)

[https://ci.ishland.com:43333/job/SodionAuth](动态构建版)

## 报告问题？想要新特性？

[ttps://github.com/Mohist-Community/SodionAuth/issues](GitHub issues)

所有问题请使用英语提出。

英语不好的可以在mcbbs中提出，但mcbbs本人不会经常看，所以说尽可能在GitHub issues提出。

## 开源协议

Copyright 2020 Mohist-Community

版权所有者：Mohist-Community

Licensed under the [Apache License, Version 2.0](licenses/apache-2.0.txt)

我们根据[Apache License, Version 2.0](licenses/apache-2.0.txt)协议开源

SodionAuth-Core with extra license

其中SodionAuth-Core带有附加协议：

````
You are not permitted to interfere any protection that prevents loading in CatServer.
````

````
你不被授权影响本插件阻止被CatServer加载。
````

关于协议翻译：如有争议以英文版本为准。

关于商业使用：如果商业使用SodionAuth，实属我的荣幸。不过由于Apache2.0协议，你还是需要我们的授权。

关于授权方式：我们不会提供任何授权！但我们绝不影响您商业使用。

关于协议修改：已征得所有参与者授权。
