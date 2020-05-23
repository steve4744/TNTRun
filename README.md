# TNTRun_reloaded

This was originally forked from the (now unsupported) TNTRun by Shevchik for Minecraft v1.9 and has been improved and updated as new versions of Minecraft have been released. The latest version runs on all Minecraft versions from 1.13 to 1.5.2, while the legacy version is for servers running Minecraft versions from 1.8 through to 1.12.2.


## Description

TNTRun is a fully automated minigame plugin that is based on the popular map TNTRUN. Players start on a layer of sand and every block that they step on disappears. If a player falls through a hole, he will continue to run on the layer below. When a player falls through the final layer he loses the game. The last player remaining wins the game.

## Download

If your server is running Minecraft 1.13 or later, then the latest version of TNTRun\_reloaded can be [downloaded from Spigot.](https://www.spigotmc.org/resources/tntrun_reloaded.53359/ "TNTRun_reloaded")

For Minecraft versions from 1.8 through to 1.12.2, the legacy version of TNTRun\_reloaded (version 6.8) can also be downloaded from Spigot by visiting the "Version History" tab on the plugin pages.

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

The following plugin dependencies are needed to compile the source code. With the exception of WorldEdit, all are optional to run TNTRun_reloaded on a Spigot server.
Links to download each plugin are available on TNTRun_reloaded's Spigot page.

The latest version of TNTRun_reloaded has been tested with the following versions of these plugins:

    WorldEdit 7.1.0 (required)
    Vault 1.7.2 (optional, required to use economy)
    HeadsPlus 6.10.2 (optional, allow players to buy and run around wearing different heads)
    mcMMO Classic 1.6.1 (optional, will allow players in same mcMMO party to PVP in arena)
    PlaceholderAPI 2.10.3 (optional, needed to use placeholders)
    
Although not required to compile the plugin, the following plugins (or similar) are required to create Holographic Leaderboards for TNTRun_reloaded.
    
    HolographicDisplays 2.4.1 (optional, an example plugin needed to create holograms)
    HolographicExtension (optional, needed with HolographicDisplays to create holograms using placeholders. Also requires ProtolcolLib)

FAWE is also supported, and can be used in place of the WorldEdit 7.1.0 dependency on 1.13+ servers.

For legacy Minecraft 1.12.2 and below:

    TNTRun_reloaded 6.8.2
    WorldEdit 6
    Vault (optional)


<br />
<br />
<br />
Updated steve4744 - 1st May 2020
