<div align="center">

![Logo](https://i.imgur.com/MsyVYtt.png)
## RealSkywars
### A New SkyWars plugin that's been coded on the 1.14 API

[![Build](https://img.shields.io/github/actions/workflow/status/joserodpt/RealSkywars/maven.yml?branch=master)](https://github.com/joserodpt/RealSkywars/actions)
![Issues](https://img.shields.io/github/issues-raw/joserodpt/RealSkywars)
[![Stars](https://img.shields.io/github/stars/joserodpt/RealSkywars)](https://github.com/joserodpt/RealSkywars/stargazers)
[![Chat)](https://img.shields.io/discord/817810368649887744?logo=discord&logoColor=white)](https://discord.gg/t7gfnYZKy8)

<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/supported/spigot_46h.png" height="35"></a>
<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/supported/paper_46h.png" height="35"></a>
<a href="/#"><img src="https://raw.githubusercontent.com/intergrav/devins-badges/v2/assets/compact/supported/purpur_46h.png" height="35"></a>

</div>

----

A New SkyWars plugin that's been coded on the **1.14 API**.
**Version support**: 
 - 1.14.4 [v1_14_R1]
 - 1.15.2 [v1_15_R1]
 - 1.16.5 [v1_16_R3]
 - 1.17.1 [v1_17_R1]
 - 1.18.2 [v1_18_R2]
 - 1.19.4 [v1_19_R3]
 - 1.20.1 [v1_20_R1]

---

# Features

 - Multi-language and Multi-arena support.
 - Default and Schematic arenas support.
 - SOLO and TEAMS arenas.
 - Cage Blocks, Bow Trails and Win Blocks.
 - Custom animations and game events.
 - Party system built in.
 - GUI based management and interaction system.
 - Sign maps supported.
 - Configurable messages.
 - 3 configurable tiersw.
 - Game history logging.
 - Chest, Time and Projectile voting per arena.
 - SQLLite, MySQL, MariaDB and other database solutions supported.
 - Tab costumizing option.
 - Achievements and Leaderboards.
 - Supports PlaceholderAPI for custom placeholder injection.
 - Hooks onto DecentHolograms and HolographicDisplays
 - API System.

# Dependencies

RealSkywars depends on **FastAsyncWorldEdit or WorldEdit**.
RealSkywars softdepends on: **Multiverse-Core, My_Worlds, HolographicDisplays, DecentHolograms, PlaceholderAPI**

## Permissions

All major and admin related permissions are assigned to: **rsw.admin**
Default Kits have the permission: **rsw.SWKit**
Permissions to vote:
|What is the player voting for?|Permission|
|--|--|
| Basic Chest Tier |rsw.basic|
| Normal Chest Tier |rsw.normal|
| Epic Chest Tier |rsw.epic|
| Game Time Day |rsw.day|
| Game Time Sunset|rsw.sunset|
| Game Time Night|rsw.night|
| Game Normal Projectiles |rsw.normal-projectile|
| Game Break Projectiles |rsw.break-projectile|
## Commands

**/rsw list** - Opens the maps menu
 Permission: rsw.join

**/rsw kits** - Opens the kits menu
 Permission: rsw.kits

**/rsw shop** - Opens the shop menu
 Permission: rsw.shop

**/rsw coins** - Sends the players current balance.
 Permission: rsw.coins

**/rsw lobby** - Teleport to the lobby.
 Permission: rsw.lobby

**/rsw forcestart** - Force starts the current match.
 Permission: rsw.forcestart

**/rsw leave** - Leaves the current match.
 Permission: rsw.leave

**/party create** - Creates a party.
 Permission: rsw.party.owner

**/party disband** - Disbands a party.
 Permission: rsw.party.owner

**/party kick** - Disbands a party.
 Permission: rsw.party.owner

**/party invite** - Disbands a party.
 Permission: rsw.party.invite

**/party accept** - Accepts a party invite.
 Permission: rsw.party.accept

**/party leave** - Accepts a party invite.
 Permission: rsw.party.leave
 
## Creating an Arena

 1. Use **/rsw create name type players** or /**rsw create name type number of teams players-per-team**
 2. A GUI will appear. Click on the settings to change then, and then save the changes.
 3. RealSkywars will attempt to generate a world and, if sucessfull, it will teleport the player to the world.
 4. If the game is SHEMATIC based, RealSkywars will try to paste the shematic provided in the arena name.
 5. Set normal, mid chests and cages with the items on the hotbar.
 6. Set the spectator location with /rsw setspectator.
 7. Set the arena boundaries with //pos1 or //pos2 or with the worldedit axe.
	 > NOTE: Don't forget to do //expand vert to cover the entire arena height.
8.  Save the arena with /rsw finishsetup

## Creating a game sign

Once the arena is fully registered, you can place a sign with the following content to link it to the arena.

Line 1: [rsw]

Line 2: -map name-

## Creating a schematic from a world with WorldEdit

 1. Select the area that's going to be saved as a shematic.
 2. //copy
 3. //schem save name
 4. It will be saved to the shematics directory of WorldEdit.
5. If you want to use this schematic, you have to copy the .schem file to the RealSkywars/maps folder.

----

## API
You can access the RealSkywars API via the [RealSkywarsAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2FRealSkywarsAPI.java) API class:

```java
var rswAPI = RealSkywarsAPI.getInstance();
```

You can access the plugin managers that are responsible for varios tasks in the plugin like this:

```java
var rswAPI = RealSkywarsAPI.getInstance();
rswAPI.getPlayerManagerAPI() and others.
```

The managers on the API are:
[AchievementsManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FAchievementsManagerAPI.java)
[ChestManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FChestManagerAPI.java)
[CurrencyManager.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FCurrencyManager.java)
[DatabaseManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FDatabaseManagerAPI.java)
[GameManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FGameManagerAPI.java)
[HologramManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FHologramManagerAPI.java)
[KitManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FKitManagerAPI.java)
[LanguageManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FLanguageManagerAPI.java)
[LeaderboardManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FLeaderboardManagerAPI.java)
[MapManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FMapManagerAPI.java)
[PartiesManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FPartiesManagerAPI.java)
[PlayerManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FPlayerManagerAPI.java)
[ShopManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FShopManagerAPI.java)
[WorldManagerAPI.java](realskywars-api%2Fsrc%2Fmain%2Fjava%2Fjoserodpt%2Frealskywars%2Fapi%2Fmanagers%2FWorldManagerAPI.java)

----