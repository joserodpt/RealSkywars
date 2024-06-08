package joserodpt.realskywars.api.player;

/*
 *   _____            _  _____ _
 *  |  __ \          | |/ ____| |
 *  | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 *  |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 *  | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 *  |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                   __/ |
 *                                  |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.achievements.RSWAchievement;
import joserodpt.realskywars.api.cages.RSWCage;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.effects.RSWBlockWinTrail;
import joserodpt.realskywars.api.effects.RSWTrail;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.managers.ShopManagerAPI;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.RSWSetupMap;
import joserodpt.realskywars.api.map.modes.teams.Team;
import joserodpt.realskywars.api.party.RSWParty;
import joserodpt.realskywars.api.utils.Text;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RSWPlayer {
    private final List<RSWTrail> RSWTrails = new ArrayList<>();
    private List<RSWGameLog> gamesList = new ArrayList<>();
    private RSWPlayerTab rt;
    private String anonName = "?";
    private Player p;
    private PlayerState state = PlayerState.LOBBY_OR_NOGAME;
    private String language = RealSkywarsAPI.getInstance().getLanguageManagerAPI().getDefaultLanguage();
    private RSWMap room;
    private RSWSetupMap setup;
    private Team team;
    private RSWCage cage;
    private RSWParty rswParty;

    //statistics
    private int gamekills, kills, deaths, winsSolo, winsTEAMS, loses, gamesPlayed;
    private int rankedTotalkills, rankedDeaths, rankedWinsSolo, rankedWinsTEAMS, rankedLoses, rankedGamesPlayed;

    private Double coins = 0D, balanceGame = 0D;
    private RSWPlayerSB playerscoreboard;
    private Material cageBlock = Material.GLASS;
    private List<String> bought = new ArrayList<>();
    private MapViewerPref mapViewerPref;
    private RSWKit swKit;
    private Particle bowParticle;
    private Material winblockMaterial;
    private Boolean invincible = false, bot = false, winblockRandom = false;

    public RSWPlayer(Player jog, RSWPlayer.PlayerState estado, int kills, int d, int solowin, int teamwin, Double coi, String lang, List<String> bgh, int l, int gp, int rankedTotalkills, int rankedDeaths, int rankedWinsSolo, int rankedWinsTEAMS, int rankedLoses, int rankedGamesPlayed, List<RSWGameLog> gamesList) {
        this.anonName = Text.anonName();

        this.p = jog;
        this.state = estado;
        this.kills = kills;
        this.winsSolo = solowin;
        this.winsTEAMS = teamwin;
        this.deaths = d;
        this.coins = coi;
        this.language = lang;
        this.bought = bgh;
        this.loses = l;
        this.gamesPlayed = gp;
        this.playerscoreboard = new RSWPlayerSB(this);

        this.gamesList = gamesList;

        this.rankedTotalkills = rankedTotalkills;
        this.rankedDeaths = rankedDeaths;
        this.rankedWinsSolo = rankedWinsSolo;
        this.rankedWinsTEAMS = rankedWinsTEAMS;
        this.rankedLoses = rankedLoses;
        this.rankedGamesPlayed = rankedGamesPlayed;

        this.rt = new RSWPlayerTab(this);
    }

    public RSWPlayer(boolean anonName) {
        if (anonName) {
            this.anonName = Text.anonName();
        }
        this.bot = true;
    }

    public <T extends Entity> void spawnAbovePlayer(Class<T> c) {
        if (this.p != null) {
            Entity ent = this.getWorld().spawn(this.getLocation().add(0, 3, 0), c);
            if (ent instanceof TNTPrimed) {
                ((TNTPrimed) ent).setFuseTicks(60);
            }
        }
    }

    public boolean isInMatch() {
        return this.room != null;
    }

    public void addStatistic(RSWPlayer.Statistic t, int i, Boolean ranked) {
        switch (t) {
            case SOLO_WIN:
                if (ranked) {
                    this.rankedWinsSolo += i;
                } else {
                    this.winsSolo += i;

                    //achievement
                    RSWAchievement a = RealSkywarsAPI.getInstance().getAchievementsManagerAPI().getAchievement(PlayerStatistics.WINS_SOLO, this.winsSolo);
                    if (a != null) {
                        a.giveAchievement(this);
                    }
                }

                this.addStatistic(RSWPlayer.Statistic.GAMES_PLAYED, 1, ranked);
                this.balanceGame = (this.balanceGame + RSWConfig.file().getDouble("Config.Coins.Per-Win"));
                this.sendMessage("&e+ &6" + RSWConfig.file().getDouble("Config.Coins.Per-Win") + "&e coins");
                break;
            case TEAM_WIN:
                if (ranked) {
                    this.rankedWinsTEAMS += i;
                } else {
                    this.winsTEAMS += i;

                    //achievement
                    RSWAchievement a = RealSkywarsAPI.getInstance().getAchievementsManagerAPI().getAchievement(PlayerStatistics.WINS_TEAMS, this.winsTEAMS);
                    if (a != null) {
                        a.giveAchievement(this);
                    }
                }
                this.addStatistic(RSWPlayer.Statistic.GAMES_PLAYED, 1, ranked);
                this.balanceGame = (this.balanceGame + RSWConfig.file().getDouble("Config.Coins.Per-Win"));
                this.sendMessage("&e+ &6" + RSWConfig.file().getDouble("Config.Coins.Per-Win") + "&e coins");
                break;
            case KILL:
                this.gamekills += i;
                this.balanceGame = (this.balanceGame + RSWConfig.file().getDouble("Config.Coins.Per-Kill"));
                this.sendMessage("&e+ &6" + RSWConfig.file().getDouble("Config.Coins.Per-Kill") + "&e coins");

                break;
            case LOSE:
                if (ranked) {
                    this.rankedLoses += i;
                } else {
                    this.loses += i;
                }
                break;
            case DEATH:
                if (ranked) {
                    this.rankedDeaths += i;
                } else {
                    this.deaths += i;
                }
                this.addStatistic(RSWPlayer.Statistic.LOSE, 1, ranked);
                this.addStatistic(RSWPlayer.Statistic.GAMES_PLAYED, 1, ranked);
                this.balanceGame = (this.balanceGame + RSWConfig.file().getDouble("Config.Coins.Per-Death"));
                this.sendMessage("&e- &6" + RSWConfig.file().getDouble("Config.Coins.Per-Death") + "&e coins");
                break;
            case GAMES_PLAYED:
                if (ranked) {
                    this.rankedGamesPlayed += i;
                } else {
                    this.gamesPlayed += i;
                    //achievement
                    RSWAchievement a = RealSkywarsAPI.getInstance().getAchievementsManagerAPI().getAchievement(PlayerStatistics.GAMES_PLAYED, this.gamesPlayed);
                    if (a != null) {
                        a.giveAchievement(this);
                    }
                }
                break;
        }
    }

    public void saveData(PlayerData pd) {
        if (pd == PlayerData.GAME) {
            this.kills += this.gamekills;

            RSWAchievement a = RealSkywarsAPI.getInstance().getAchievementsManagerAPI().getAchievement(PlayerStatistics.KILLS, this.kills);
            if (a != null) {
                a.giveAchievement(this);
            }

            RealSkywarsAPI.getInstance().getCurrencyAdapter().addCoins(this, this.balanceGame);
            this.balanceGame = 0D;
            this.gamekills = 0;
        }

        RealSkywarsAPI.getInstance().getPlayerManagerAPI().savePlayer(this, pd);
    }

    public double getGameBalance() {
        return RealSkywarsAPI.getInstance().getCurrencyAdapter().getCoins(this) + this.balanceGame;
    }

    public void sendMessage(String string) {
        if (!this.bot) {
            this.p.sendMessage(Text.color(string));
        }
    }

    public void resetData() {
        RealSkywarsAPI.getInstance().getDatabaseManagerAPI().deletePlayerData(RealSkywarsAPI.getInstance().getDatabaseManagerAPI().getPlayerData(this.getPlayer()), true);
        RealSkywarsAPI.getInstance().getPlayerManagerAPI().removePlayer(this);
        this.p.kickPlayer(RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + "§4Your data was cleared with success. \n §cPlease join the server again to complete the reset.");
    }

    public String getName() {
        return this.p == null ? this.anonName : this.p.getName();
    }

    public void teleport(Location l) {
        if (this.p != null) {
            this.p.teleport(l);
        }
    }

    public void stopTrails() {
        this.RSWTrails.forEach(RSWTrail::cancelTask);
    }

    public void addTrail(RSWTrail t) {
        this.RSWTrails.add(t);
    }

    public void removeTrail(RSWTrail t) {
        this.RSWTrails.remove(t);
    }

    public Location getLocation() {
        return this.p == null ? null : this.p.getLocation();
    }

    public void playSound(Sound s, int i, int i1) {
        if (this.p != null) {
            this.p.playSound(this.p.getLocation(), s, i, i1);
        }
    }

    public World getWorld() {
        return this.p == null ? null : this.p.getWorld();
    }

    public void executeWinBlock(int t) {
        if (t < 0) {
            return;
        }
        if (this.winblockRandom) {
            addTrail(new RSWBlockWinTrail(this, t));
        } else {
            if (this.winblockMaterial != null) {
                addTrail(new RSWBlockWinTrail(this, t, winblockMaterial));
            }
        }
    }

    public void delCage() {
        if (this.cage != null) this.cage.removePlayer(this);
    }

    public void setFlying(boolean b) {
        if (this.p != null) {
            this.p.setAllowFlight(b);
            this.p.setFlying(b);
        }
    }

    public UUID getUUID() {
        return p.getUniqueId();
    }

    public String getLanguage() {
        return this.language;
    }

    public void setLanguage(String lang) {
        this.language = lang;
    }

    public Player getPlayer() {
        return this.p;
    }

    public void setProperty(PlayerProperties pp, Object o) {
        switch (pp) {
            case MAPVIEWER_PREF:
                this.mapViewerPref = MapViewerPref.valueOf((String) o);
                this.saveData(PlayerData.MAPVIEWER_PREF);
                break;
            case KIT:
                this.swKit = (RSWKit) o;
                break;
            case BOW_PARTICLES:
                this.bowParticle = (Particle) o;
                this.saveData(PlayerData.BOW_PARTICLES);
                break;
            case CAGE_BLOCK:
                Material m = (Material) o;
                if (m != this.cageBlock) {
                    this.cageBlock = m;
                    if (isInMatch()) {
                        switch (this.getMatch().getGameMode()) {
                            case SOLO:
                                if (hasCage()) {
                                    this.cage.setCage();
                                }
                                break;
                            case TEAMS:
                                if (hasTeam()) {
                                    this.getTeam().getTeamCage().setCage();
                                }
                                break;
                        }
                    }
                }
                RealSkywarsAPI.getInstance().getPlayerManagerAPI().savePlayer(this, PlayerData.CAGE_BLOCK);
                break;
            case WIN_BLOCKS:
                if (o.equals("RandomBlock")) {
                    this.winblockRandom = true;
                } else {
                    this.winblockRandom = false;
                    this.winblockMaterial = Material.getMaterial((String) o);
                }
                break;
            case STATE:
                this.state = (PlayerState) o;
                break;
            case LANGUAGE:
                this.language = (String) o;
                this.saveData(PlayerData.LANG);
                break;
        }
    }

    public Object getStatistics(PlayerStatistics pp, Boolean ranked) {
        switch (pp) {
            case LOSES:
                return ranked ? this.rankedLoses : this.loses;
            case DEATHS:
                return ranked ? this.rankedDeaths : this.deaths;
            case WINS_SOLO:
                return ranked ? this.rankedWinsSolo : this.winsSolo;
            case WINS_TEAMS:
                return ranked ? this.rankedWinsTEAMS : this.winsTEAMS;
            case KILLS:
                return ranked ? this.rankedTotalkills : this.kills;
            case GAMES_PLAYED:
                return ranked ? this.rankedGamesPlayed : this.gamesPlayed;
            case GAME_BALANCE:
                return this.balanceGame;
            case GAME_KILLS:
                return this.gamekills;
        }
        return null;
    }

    public RSWPlayer.PlayerState getState() {
        return this.state;
    }

    public RSWMap getMatch() {
        return this.room;
    }

    public void setRoom(RSWMap o) {
        this.room = o;
    }

    public RSWSetupMap getSetupRoom() {
        return this.setup;
    }

    public void setSetup(RSWSetupMap o) {
        this.setup = o;
    }

    public List<String> getBoughtItems() {
        return this.bought != null ? this.bought : new ArrayList<>();
    }

    public Boolean boughtItem(String name, ShopManagerAPI.Categories c) {
        return this.getBoughtItems().contains(name + "|" + c.name());
    }

    public RSWPlayerSB getScoreboard() {
        return this.playerscoreboard;
    }

    public RSWKit getKit() {
        return this.hasKit() ? this.swKit : new RSWKit();
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team o) {
        this.team = o;
    }

    public Object getProperty(PlayerProperties pp) {
        switch (pp) {
            case CAGE_BLOCK:
                return this.cageBlock;
            case BOW_PARTICLES:
                return this.bowParticle;
            case MAPVIEWER_PREF:
                return this.mapViewerPref;
        }
        return null;
    }

    public double getLocalCoins() {
        return this.coins;
    }

    public void setLocalCoins(Double v) {
        this.coins = v;
    }

    public void heal() {
        if (this.p != null) {
            this.p.setFireTicks(0);
            this.p.setHealth(20);
            this.p.setFoodLevel(20);

            this.p.getActivePotionEffects().forEach(potionEffect -> this.p.removePotionEffect(potionEffect.getType()));
        }
    }

    public RSWCage getCage() {
        return this.cage;
    }

    public void setCage(RSWCage c) {
        this.cage = c;
    }

    public boolean isInvencible() {
        return this.invincible;
    }

    public void setInvincible(boolean b) {
        this.invincible = b;
    }

    public void leave() {
        if (this.room != null) {
            this.room.removePlayer(this);
        }

        if (this.hasParty()) {
            if (this.getParty().isOwner(this)) {
                this.disbandParty();
            } else {
                this.leaveParty();
            }
        }

        this.stopTrails();

        this.playerscoreboard.stop();
        this.saveData(PlayerData.GAME);
        RealSkywarsAPI.getInstance().getPlayerManagerAPI().removePlayer(this);
    }

    public void sendTitle(String s, String s1, int i, int i1, int i2) {
        if (this.p != null) this.p.sendTitle(s, s1, i, i1, i2);
    }

    public boolean hasKit() {
        return this.swKit != null;
    }

    public boolean hasCage() {
        return this.cage != null;
    }

    public PlayerInventory getInventory() {
        return this.p.getInventory();
    }

    public boolean hasTeam() {
        return this.team != null;
    }

    public String getDisplayName() {
        return this.bot ? this.anonName : this.p.getDisplayName();
    }

    public void sendCenterMessage(String r) {
        sendMessage(Text.centerMessage(r));
    }

    public boolean isBot() {
        return this.bot;
    }

    public void hidePlayer(Plugin plugin, Player pl) {
        if (!this.bot && pl != null && !RealSkywarsAPI.getInstance().getGameManagerAPI().endingGames)
            this.getPlayer().hidePlayer(plugin, pl);
    }

    public void showPlayer(Plugin plugin, Player pl) {
        if (!this.bot && pl != null && !RealSkywarsAPI.getInstance().getGameManagerAPI().endingGames)
            this.getPlayer().showPlayer(plugin, pl);
    }

    public RSWPlayerTab getTab() {
        return this.rt;
    }

    public enum PlayerData {CAGE_BLOCK, GAME, COINS, LANG, MAPVIEWER_PREF, BOW_PARTICLES, BOUGHT_ITEMS}


    public void buyItem(String s) {
        this.bought.add(Text.strip(s));
        RealSkywarsAPI.getInstance().getPlayerManagerAPI().savePlayer(this, PlayerData.BOUGHT_ITEMS);
    }

    public void sendActionbar(String s) {
        if (this.p != null) this.p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Text.color(s)));
    }

    public boolean hasParty() {
        return this.rswParty != null;
    }

    public void createParty() {
        this.rswParty = new RSWParty(this);
    }

    public void disbandParty() {
        this.rswParty.disband();
    }

    public RSWParty getParty() {
        return this.rswParty;
    }

    public void joinParty(RSWPlayer player) {
        this.rswParty = player.getParty();
    }

    public void leaveParty() {
        this.rswParty.playerLeave(this);
        this.rswParty = null;
        this.sendMessage(TranslatableLine.PARTY_LEAVE.get(this, true).replace("%player%", this.getDisplayName()));
    }

    public MapViewerPref getMapViewerPref() {
        return this.mapViewerPref;
    }

    public void setMapViewerPref(MapViewerPref a) {
        this.mapViewerPref = a;
    }

    public void closeInventory() {
        if (this.p != null) {
            this.p.closeInventory();
        }
    }

    public void setBarNumber(int xp) {
        if (this.p != null) {
            this.p.setLevel(xp);
            this.p.setExp(xp);
        }
    }

    public void setBarNumber(int xp, int max) {
        if (this.p != null && xp != 0 && max != 0) {
            this.p.setLevel(xp);
            float div = (float) xp / (float) max;
            this.p.setExp(div);
        }
    }

    public List<RSWGameLog> getGamesList() {
        return this.gamesList;
    }

    public void addGameLog(RSWGameLog rswGameLog) {
        this.gamesList.add(0, rswGameLog);
    }

    public void setGameMode(GameMode gameMode) {
        if (!this.isBot()) {
            this.getPlayer().setGameMode(gameMode);
        }
    }

    //ENUMs

    public enum PlayerState {
        LOBBY_OR_NOGAME, CAGE, PLAYING, SPECTATOR, EXTERNAL_SPECTATOR
    }

    public enum PlayerProperties {KIT, BOW_PARTICLES, CAGE_BLOCK, STATE, LANGUAGE, MAPVIEWER_PREF, WIN_BLOCKS}

    public enum Statistic {KILL, SOLO_WIN, TEAM_WIN, LOSE, DEATH, GAMES_PLAYED}

    public enum PlayerStatistics {WINS_SOLO, WINS_TEAMS, KILLS, DEATHS, LOSES, GAMES_PLAYED, GAME_BALANCE, GAME_KILLS}

    public enum MapViewerPref {
        MAPV_SPECTATE, MAPV_AVAILABLE, MAPV_STARTING, MAPV_WAITING, MAPV_ALL, SOLO, TEAMS, SOLO_RANKED, TEAMS_RANKED;

        public String getDisplayName(RSWPlayer p) {
            switch (this) {
                case MAPV_ALL:
                    return TranslatableLine.MAP_ALL.get(p);
                case MAPV_WAITING:
                    return TranslatableLine.MAP_WAITING.get(p);
                case MAPV_SPECTATE:
                    return TranslatableLine.MAP_SPECTATE.get(p);
                case MAPV_STARTING:
                    return TranslatableLine.MAP_STARTING.get(p);
                case MAPV_AVAILABLE:
                    return TranslatableLine.MAP_AVAILABLE.get(p);
                case SOLO:
                    return Text.color("&eSolo");
                case SOLO_RANKED:
                    return Text.color("&eSolo &b&LRANKED");
                case TEAMS_RANKED:
                    return Text.color("&9Teams &b&LRANKED");
                case TEAMS:
                    return Text.color("&9Teams");
                default:
                    return "?";
            }
        }
    }
}
