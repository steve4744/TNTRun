# TNTRun_reloaded

[![Resource](https://img.shields.io/badge/SpigotMC-Resource-orange.svg)](https://www.spigotmc.org/resources/tntrun_reloaded-tntrun-for-1-13-1-16.53359/)
[![Java CI with Maven](https://github.com/steve4744/TNTRun/workflows/Java%20CI%20with%20Maven/badge.svg)](https://github.com/steve4744/TNTRun/actions?query=workflow%3A%22Java+CI+with+Maven%22)
[![Dev Build](https://img.shields.io/badge/Dev%20Build-Latest-orange?logo=github-actions)](https://github.com/steve4744/TNTRun/releases)
[![GPLv3 License](https://img.shields.io/badge/License-GPL%20v3-yellow.svg)](https://opensource.org/licenses/)
[![Legacy Release](https://img.shields.io/badge/Legacy%20Release-v6.8.7-blue.svg)](https://github.com/steve4744/TNTRun/releases/tag/v6.8.7)


[![Discord Chat](https://img.shields.io/discord/308323056592486420?logo=discord)](https://discord.gg/wFYSAS4)
[![bStats](https://img.shields.io/badge/statistics-bstats-brightgreen.svg)](https://bstats.org/plugin/bukkit/TNTRun_reloaded)
[![gitlocalized ](https://gitlocalize.com/repo/5420/whole_project/badge.svg)](https://gitlocalize.com/repo/5420/whole_project?utm_source=badge)
[![PayPal](https://img.shields.io/badge/paypal-donate-yellow?logo=paypal)](https://www.paypal.com/paypalme/steve4744)


## Description

TNTRun is a highly configurable, fully automated minigame for Minecraft servers. Traditionally players start on a layer of sand supported by TNT blocks, although any combination of blocks can be used. Once the game starts, every block that a player steps on will disappear. If a player falls through a hole, he/she will continue to run on the layer below. Once a player falls through the final layer he/she loses the game and becomes a spectator for the remainder of the game. The last player remaining wins the game.

This fork was created in 2016 from the unsupported TNTRun by Shevchik for Minecraft v1.10, and has been improved and updated as new versions of Minecraft have been released. The latest version of TNTRun_reloaded, like Minecraft itself, requires a minimum Java version of 16, so is supported on servers running Minecraft versions 1.16.5 to 1.18.1. Servers running a Java version less than 16 and Minecraft versions 1.13.2 to 1.6.4 should download version 9.11 of the plugin, while the legacy version (v6.8.7) is available for servers running Minecraft versions 1.8 through to 1.12.2.

The following description and features apply to the latest release. Some of the features will not be present in the older versions which are maintained and supported on an 'as is' basis, and will only be updated if a bug is reported or an existing feature breaks.

The plugin features a customisable shop where players can buy items such as weapons, armour, double-jumps, splash potions, snowballs (with knockback) and commands which run when the game starts. There is an option to enable PVP in an arena, assign kits, and the plugin also interfaces with HeadsPlus (by ThatsMusic99) allowing players to buy/wear custom heads during the game.

Optionally, a fee can be set to join each arena, which can be monetary or any Minecraft item such gold_nuggets. Rewards for winning the game can be set to any combination of coins, materials, XP or a command based reward. Scoreboards, leaderboards, placeholders and holograms are fully supported (see the Dependencies section below).


## Download

If your server is running with a minimum of Java 16 with Minecraft 1.16.5 or later, then the latest version of TNTRun\_reloaded can be [downloaded from Spigot.](https://www.spigotmc.org/resources/tntrun_reloaded.53359/ "TNTRun_reloaded")

For Minecraft versions from 1.13.2 to 1.16.5, on servers running less than Java 16, you should download version 9.11 from Spigot.

For Minecraft versions from 1.8 through to 1.12.2, the legacy version of TNTRun\_reloaded (version 6.8.7) can be downloaded from the GitHub Releases tab above. It can be [downloaded here.](https://github.com/steve4744/TNTRun/releases/download/v6.8.6/TNTRun_reloaded-6.8.7.jar "v6.8.7")


## Development Builds

Development snapshots are created by GitHub Actions every time a commit is pushed to the most recent snapshot branch. The latest snapshot build can be downloaded from [GitHub Releases.](https://github.com/steve4744/TNTRun/releases "Releases")


## Features

    Supports multiple arenas
    Automatic arena regeneration
    Custom Events
    Native Party system
    Support for AlessioDP Parties
    Configurable block destroy delay
    Force-start voting system
    Permission controlled force-start command
    Join fee can be set per arena
    Arena currency (money or any Minecraft material)
    Arena selection GUI
    Configurable anti-camping system
    Translatable messages
    Command whitelist
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
    Shop can be enabled/disabled per arena
    Kits - can be enabled per arena
    Heads - interfaces with HeadsPlus plugin by Thatsmusic99
    PVP can be enabled/disabled per arena
    Configurable player rewards for 1st, 2nd and 3rd places
    Built-in placeholder support
    mcMMO support - allow players in same mcMMO party to PVP if enabled in arena
    MySQL support
    Bungeecord support


## Dependencies

The following plugin dependencies are needed to compile the source code. All are optional to run TNTRun_reloaded on a Spigot server.
Links to download each plugin are available on TNTRun_reloaded's Spigot page.

The latest version of TNTRun_reloaded has been tested with the following versions of these plugins:

    WorldEdit 7.2.8 (optional, internal commands setP1 and setP2 can be used to set arena bounds)
    Vault 1.7.1 (optional, required to use economy)
    HeadsPlus 7.0.2 (optional, allow players to buy and run around wearing different heads)
    mcMMO 2.1.200 (optional, will allow players in same mcMMO party to PVP in arena)
    PlaceholderAPI 2.10.11 (optional, needed to use placeholders)
    AlessioDP Parties 3.1.14 (optional, can be used in place of native tntrun parties)
    
Although not required to compile the plugin, the following plugins (or similar) are required to create Holographic Leaderboards for TNTRun_reloaded.
    
    HolographicDisplays (optional, an example plugin needed to create holograms)
    HolographicExtension (optional, needed with HolographicDisplays to create holograms using placeholders. Also requires ProtolcolLib)

If you are interested in recording the amount of time players spend playing TNTRun_reloaded, with the option to reward players in-game for surviving a set amount of time (includes its own leaderboards and placeholders):

    TNTRunTimedRewards (optional add-on for recording time played in arena, and rewarding survival time)

FAWE is also supported, and can be used in place of the WorldEdit on 1.13+ servers.

For legacy Minecraft 1.12.2 and below:

    TNTRun_reloaded 6.8.7
    WorldEdit 6
    Vault (optional)


<br />
<br />
<br />
Updated steve4744 - 1st January 2022
