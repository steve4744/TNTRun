# TNTRun_reloaded

TNTRun is a highly configurable, fully automated minigame for Minecraft servers. Traditionally players start on a layer of sand supported by TNT blocks, although any combination of blocks can be used, and every block that they step on disappears. If a player falls through a hole, he/she will continue to run on the layer below. Once a player falls through the final layer he/she loses the game and becomes a spectator for the remainder of the game. The last player remaining wins the game.

This fork was created in 2016 from the unsupported TNTRun by Shevchik for Minecraft v1.10, and has been improved and updated as new versions of Minecraft have been released. The latest version of TNTRun_reloaded supports all Minecraft versions from 1.13 to 1.16.1, while the legacy version is supported for servers running Minecraft versions from 1.8 through to 1.12.2. 

The following description and features apply to the latest release. Many of the features will not be present in the legacy version which is maintained and supported on an 'as is' basis, and will only be updated if a bug is reported or an existing feature breaks.

The plugin features a customisable shop where players can buy items such as double-jumps, splash potions and snowballs (with knockback), an option to enable PVP in an arena, and interfaces with HeadsPlus (by ThatsMusic99) allowing players to buy/wear custom heads during the game.

Optionally, a fee can be set to join each arena, which can be monetary or any minecraft item such gold_nuggets. Rewards for winning the game can be set to any combination of coins, materials, XP or a command based reward. Scoreboards, leaderboards, placeholders and holograms are fully supported (see the Dependencies section below).


## Download

If your server is running Minecraft 1.13 or later, then the latest version of TNTRun\_reloaded can be [downloaded from Spigot.](https://www.spigotmc.org/resources/tntrun_reloaded.53359/ "TNTRun_reloaded")

For Minecraft versions from 1.8 through to 1.12.2, the legacy version of TNTRun\_reloaded (version 6.8.x) is available from the GitHub Releases tab above, or can be [downloaded here.](https://github.com/steve4744/TNTRun/releases/download/v6.8.3/TNTRun_reloaded-6.8.3.jar "v6.8.3")


## Features

    Supports multiple arenas
    Automatic arena regeneration
    Force-start voting system
    Anti-camping system
    Custom messages
    Formatting codes support
    Signs
    Configurable per-arena time limit
    Configurable per-arena countdown
    In-game scoreboard
    Titles and bossbars
    Spectator system
    Player stats
    Leader board
    Arena leave checker
    Customizable shop
    PVP can be enabled/disabled per arena
    Player rewards
    MySQL support
    Built-in placeholder support


## Dependencies

The following plugin dependencies are needed to compile the source code. All are optional to run TNTRun_reloaded on a Spigot server.
Links to download each plugin are available on TNTRun_reloaded's Spigot page.

    
For legacy Minecraft 1.12.2 and below:

    TNTRun_reloaded 6.8.x
    WorldEdit 6
    Vault (optional)
    PlaceholderAPI 2.10.9 (optional, needed to use placeholders)
    

Although not required to compile the plugin, the following plugins (or similar) are required to create Holographic Leaderboards for TNTRun_reloaded.
    
    HolographicDisplays (optional, an example plugin needed to create holograms)
    HolographicExtension (optional, needed with HolographicDisplays to create holograms using placeholders. Also requires ProtolcolLib)


<br />
<br />
<br />
Updated steve4744 - 14th August 2020