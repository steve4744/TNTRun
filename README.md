# TNTRun_reloaded

TNTRun is a highly configurable, fully automated minigame for Minecraft servers. Traditionally players start on a layer of sand supported by TNT blocks, although any combination of blocks can be used, and every block that they step on disappears. If a player falls through a hole, he/she will continue to run on the layer below. Once a player falls through the final layer he/she loses the game and becomes a spectator for the remainder of the game. The last player remaining wins the game.

This fork was created in 2016 from the unsupported TNTRun by Shevchik for Minecraft v1.10, and has been improved and updated as new versions of Minecraft have been released. The latest version of TNTRun_reloaded supports all Minecraft versions from 1.13 to 1.16.1, while the legacy version is supported for servers running Minecraft versions from 1.8 through to 1.12.2. 

The following description and features apply to the latest release. Many of the features will not be present in the legacy version which is maintained and supported on an 'as is' basis, and will only be updated if a bug is reported or an existing feature breaks.

The plugin features a customisable shop where players can buy items such as double-jumps, splash potions and snowballs (with knockback), an option to enable PVP in an arena, and interfaces with HeadsPlus (by ThatsMusic99) allowing players to buy/wear custom heads during the game.

Optionally, a fee can be set to join each arena, which can be monetary or any minecraft item such gold_nuggets. Rewards for winning the game can be set to any combination of coins, materials, XP or a command based reward. Scoreboards, leaderboards, placeholders and holograms are fully supported (see the Dependencies section below).


## Download

If your server is running Minecraft 1.13 or later, then the latest version of TNTRun\_reloaded can be [downloaded from Spigot.](https://www.spigotmc.org/resources/tntrun_reloaded.53359/ "TNTRun_reloaded")

For Minecraft versions from 1.8 through to 1.12.2, the legacy version of TNTRun\_reloaded (version 6.8) can be downloaded from Spigot by visiting the "Version History" tab on the plugin pages.

Note that there is a legacy bug fix release (version 6.8.2) only available from the GitHub Releases tab above, which fixes a couple of bugs with Featherboard/scoreboards and with "stats". It can be [downloaded here.](https://github.com/steve4744/TNTRun/releases/download/v6.8.1/TNTRun_reloaded_6.8.2.jar "v6.8.2")


## Features

    Supports multiple arenas
    Automatic arena regeneration
    Custom Events
    Configurable block destroy delay
    Force-start voting system
    Permission controlled force-start command
    Join fee can be set per arena
    Arena currency (money or any Minecraft material)
    Arena selection GUI
    Configurable anti-camping system
    Custom messages
    Formatting codes support
    Full tab completion based on permissions
    Signs
    Configurable per-arena time limit
    Configurable per-arena countdown
    Configurable sounds
    In-game scoreboard
    Titles and bossbars
    Spectator system
    Player stats
    Leader board
    Auto updating leader board signs
    Arena leave checker
    Customizable shop
    Kits - can be enabled per arena
    Heads - interfaces with HeadsPlus plugin by Thatsmusic99
    PVP can be enabled/disabled per arena
    Player rewards
    Built-in placeholder support
    mcMMO support - allow players in same mcMMO party to PVP if enabled in arena
    MySQL support
    Bungeecord support
    Legacy placeholder support - https://www.spigotmc.org/resources/tntrun_reloaded-expansion.53945/

## Dependencies

The following plugin dependencies are needed to compile the source code. All are optional to run TNTRun_reloaded on a Spigot server.
Links to download each plugin are available on TNTRun_reloaded's Spigot page.

The latest version of TNTRun_reloaded has been tested with the following versions of these plugins:

    WorldEdit 7.1.0 (optional, internal commands setP1 and setP2 can be used to set arena bounds)
    Vault 1.7.2 (optional, required to use economy)
    HeadsPlus 6.10.9 (optional, allow players to buy and run around wearing different heads)
    mcMMO Classic 1.6.1 (optional, will allow players in same mcMMO party to PVP in arena)
    PlaceholderAPI 2.10.6 (optional, needed to use placeholders)
    
Although not required to compile the plugin, the following plugins (or similar) are required to create Holographic Leaderboards for TNTRun_reloaded.
    
    HolographicDisplays (optional, an example plugin needed to create holograms)
    HolographicExtension (optional, needed with HolographicDisplays to create holograms using placeholders. Also requires ProtolcolLib)

FAWE is also supported, and can be used in place of the WorldEdit on 1.13+ servers.

For legacy Minecraft 1.12.2 and below:

    TNTRun_reloaded 6.8.2
    WorldEdit 6
    Vault (optional)


<br />
<br />
<br />
Updated steve4744 - 17th July 2020