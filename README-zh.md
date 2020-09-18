# SodionAuth

## 是什么？

新生的MC登录插件。

## 能做什么？

- 为你的正版服务器提供额外的保护。
- 为离线服务器提供安全。
- 通过后端系统（如Xenforo，Discuz）实现例如“邀请码”的功能。
- 在登录前阻止玩家做几乎任何事。
- 将游戏与您的论坛同步

## 那...优势有哪些？

- 自适应速率限制（智能漏桶算法）。
- 国家限制（GeoIP）。
- 无需指令直接在聊天栏输入密码。
- 加载完成后完全异步的 网络+文件 IO请求。
- 提供FastLogin支持（已pr尚未合并）。
- 提供自动登录（session）。
- 提供外置登录（实验）。
- 在登录前隐藏几乎全部的状态。
  - 位置。
  - 背包。
  - 游戏模式。
  - 生命值。
  - 食物（包含foodLevel exhaustion saturation）。
  - 剩余氧气。
  - 药水效果。
- 在登录后恢复几乎全部的状态。
  - 位置。
  - 游戏模式。
  - 生命值。
  - 跌落距离。
  - 速度矢量。
  - 食物（包含foodLevel exhaustion saturation）。
  - 剩余氧气。
  - 药水效果。
- 支持较多的后端。
  - Xenforo。
  - UCenter (Discuz) 。
  - MySQL。
  - SQLite。
  - Customized RestAPI。
- 支持较多的密码处理方式。
- 支持较多的平台。
  - Bukkit。
    - Paper（完全）。
    - Mohist（部分）。
    - Spigot（部分）。
    - CraftBukkit（(部分）。
  - Sponge（部分）。
  - Fabric（实验）。

## 我就不信你这没有劣势！

- 无法提供通过邮箱找回密码。
- 外置登录无法使用皮肤。
- Session未缓存性能较差。
- 由于保存了较多状态，可能会使以下操作**失效**，进而引起玩家不适
  - 在落地前一秒退出服务器，在登录后可无伤害落地
  - 在水下无氧气时退出服务器，在未登录状态恢复氧气
  - 在获得debuff（如中毒，凋零，饥饿）之后退出服务器，在未登录状态等待buff结束后登录，可避免伤害
  - 在鞘翅飞行时退出服务器，再次登录获得减速

## 喂喂，那你这些劣势为什么不改啊

- 部分后端未提供修改密码的API。
- 太肝了诶，不过会做的。
- 为了跨服！
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