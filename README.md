SessionSkip
-----------
### Warnings
* SessionSkip can allow players to log in as your staff if you do not configure it properly.  Do not use this plugin if you aren't 100% certain of what you're doing.
* If you use SessionSkip with a Minecraft version that supports UUIDs, it will cause an offline mode UUID to be used for any players who skip session server authentication.  If you change your name, your offline mode UUID will also change.

### Description
SessionSkip is a simple BungeeCord plugin to skip authentication with the Minecraft session servers under certain conditions.

Please note: The plugin will not auto-generate the config file.  Create a plugins/SessionSkip folder and manually copy it before use.

### Configuration
SessionSkip allows players to skip authentication with the Mojang session servers when they meet one of the following configurable criterion:
* They connect to a specific listener/hostname combination.
* They connect using a specific hostname.
* They connect from a specific IP address.
* They have a specific login/remote IP/hostname combination.

While hostnames and IP addresses are fairly self explanatory, a listener takes two forms: A connection made directly to an IP address and port, or a connection made to an IP address and port with a specified hostname.  With that in mind, a listener could look like either of the following, depending on what the player types in when connecting:
* If the player types "172.16.0.1"
  * /172.16.0.1:25565
* If the player types "play.example.com"
  * play.example.com/172.16.0.1:25565

The playername combination let you to allow specific players in your network coming from specific IP and / or to specific hostname, it is formed like: `PLAYER_LOGIN@PLAYER_IP/HOSTNAME_TYPED`

`PLAYER_IP` and `HOSTNAME_TYPED` are player's remote IP Address and server address typed as seen as above, they are optional and can be replaced by a `*` wildcard.

For example if you want to allow cracked player `Foobar` to connect from any IP / hostname typed, you'll add the following line in the config.yml: `Foobar@*/*`

If you want to allow a player only if his IP address is `192.168.1.7`, here is the example config line: `Foobar@192.168.1.7/*`

If you want to allow a player only if the hostname typed is `gabuzomeu`, you have to add: `Foobar@*/gabuzomeu`

If you want to restrict both player remote IP and typed hostname, use this line: `Foobar@192.168.1.7/gabuzomeu`


### Commands
* /sessionskip reload - Reloads the plugin and config file.
* /sessionskip enable - Enables SessionSkip until BungeeCord is restarted.
* /sessionskip disable - Disables SessionSkip until BungeeCord is restarted.

SessionSkip has a "enabled" setting in the config file, which will determine whether it is enabled (true) or disabled (false) by default when BungeeCord starts up.

### Requirements
* BungeeCord build 691+

