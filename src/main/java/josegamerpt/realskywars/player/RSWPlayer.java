package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.achievements.Achievement;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.effects.BlockWinTrail;
import josegamerpt.realskywars.effects.Trail;
import josegamerpt.realskywars.game.SetupRoom;
import josegamerpt.realskywars.game.modes.SWGameMode;
import josegamerpt.realskywars.game.modes.teams.Team;
import josegamerpt.realskywars.kits.Kit;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.misc.Selections;
import josegamerpt.realskywars.party.Party;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.utils.fastboard.FastBoard;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RSWPlayer {
    private final List<Trail> trails = new ArrayList<>();
    private ArrayList<RSWGameLog> gamesList = new ArrayList<>();
    private RoomTAB rt;
    private String anonName = "?";
    private Player p;
    private PlayerState state = PlayerState.LOBBY_OR_NOGAME;
    private String language = RealSkywars.getPlugin().getLanguageManager().getDefaultLanguage();
    private SWGameMode room;
    private SetupRoom setup;
    private Team team;
    private Cage cage;
    private Party party;
    //statistics

    private int gamekills;

    private int kills;
    private int deaths;
    private int winsSolo;
    private int winsTEAMS;
    private int loses;
    private int gamesPlayed;

    private int rankedTotalkills;
    private int rankedDeaths;
    private int rankedWinsSolo;
    private int rankedWinsTEAMS;
    private int rankedLoses;
    private int rankedGamesPlayed;
    private Double coins = 0D;
    private Double balanceGame = 0D;
    private PlayerScoreboard playerscoreboard;
    private Material cageBlock = Material.GLASS;
    private ArrayList<String> bought = new ArrayList<>();
    private Selections.MapViewerPref mapViewerPref;
    private Boolean bot = false;
    private Kit kit;
    private Particle bowParticle;
    private boolean winblockRandom = false;
    private Material winblockMaterial;
    private Boolean invincible = false;

    public RSWPlayer(Player jog, RSWPlayer.PlayerState estado, int kills, int d, int solowin, int teamwin, Double coi, String lang, ArrayList<String> bgh, int l, int gp, int rankedTotalkills, int rankedDeaths, int rankedWinsSolo, int rankedWinsTEAMS, int rankedLoses, int rankedGamesPlayed, ArrayList<RSWGameLog> gamesList) {
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
        this.playerscoreboard = new PlayerScoreboard(this);

        this.gamesList = gamesList;

        this.rankedTotalkills = rankedTotalkills;
        this.rankedDeaths = rankedDeaths;
        this.rankedWinsSolo = rankedWinsSolo;
        this.rankedWinsTEAMS = rankedWinsTEAMS;
        this.rankedLoses = rankedLoses;
        this.rankedGamesPlayed = rankedGamesPlayed;

        this.rt = new RoomTAB(this);
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
                    Achievement a = RealSkywars.getPlugin().getAchievementsManager().getAchievement(PlayerStatistics.WINS_SOLO, this.winsSolo);
                    if (a != null) {
                        a.giveAchievement(this);
                    }
                }

                this.addStatistic(RSWPlayer.Statistic.GAMES_PLAYED, 1, ranked);
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Win"));
                this.sendMessage("&e+ &6" + Config.file().getDouble("Config.Coins.Per-Win") + "&e coins");
                break;
            case TEAM_WIN:
                if (ranked) {
                    this.rankedWinsTEAMS += i;
                } else {
                    this.winsTEAMS += i;

                    //achievement
                    Achievement a = RealSkywars.getPlugin().getAchievementsManager().getAchievement(PlayerStatistics.WINS_TEAMS, this.winsTEAMS);
                    if (a != null) {
                        a.giveAchievement(this);
                    }
                }
                this.addStatistic(RSWPlayer.Statistic.GAMES_PLAYED, 1, ranked);
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Win"));
                this.sendMessage("&e+ &6" + Config.file().getDouble("Config.Coins.Per-Win") + "&e coins");
                break;
            case KILL:
                this.gamekills += i;
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Kill"));
                this.sendMessage("&e+ &6" + Config.file().getDouble("Config.Coins.Per-Kill") + "&e coins");

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
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Death"));
                this.sendMessage("&e- &6" + Config.file().getDouble("Config.Coins.Per-Death") + "&e coins");
                break;
            case GAMES_PLAYED:
                if (ranked) {
                    this.rankedGamesPlayed += i;
                } else {
                    this.gamesPlayed += i;
                    //achievement
                    Achievement a = RealSkywars.getPlugin().getAchievementsManager().getAchievement(PlayerStatistics.GAMES_PLAYED, this.gamesPlayed);
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

            Achievement a = RealSkywars.getPlugin().getAchievementsManager().getAchievement(PlayerStatistics.KILLS, this.kills);
            if (a != null) {
                a.giveAchievement(this);
            }

            this.coins += this.balanceGame;
            this.balanceGame = 0D;
            this.gamekills = 0;
        }

        RealSkywars.getPlugin().getPlayerManager().savePlayer(this, pd);
    }

    public double getGameBalance() {
        return (this.coins + this.balanceGame);
    }

    public void sendMessage(String string) {
        if (!this.bot) {
            this.p.sendMessage(Text.color(string));
        }
    }

    public void resetData() {
        RealSkywars.getPlugin().getDatabaseManager().deletePlayerData(RealSkywars.getPlugin().getDatabaseManager().getPlayerData(this.getPlayer()), true);
        RealSkywars.getPlugin().getPlayerManager().removePlayer(this);
        this.p.kickPlayer(RealSkywars.getPlugin().getLanguageManager().getPrefix() + "§4Your data was cleared with success. \n §cPlease join the server again to complete the reset.");
    }

    public String getName() {
        if (this.p != null) {
            return this.p.getName();
        } else {
            return this.anonName;
        }
    }

    public void teleport(Location l) {
        if (this.p != null) {
            this.p.teleport(l);
        }
    }

    public void stopTrails() {
        this.trails.forEach(Trail::cancelTask);
    }

    public void addTrail(Trail t) {
        this.trails.add(t);
    }

    public void removeTrail(Trail t) {
        this.trails.remove(t);
    }

    public Location getLocation() {
        if (this.p != null) {
            return this.p.getLocation();
        }
        return null;
    }

    public void playSound(Sound s, int i, int i1) {
        if (this.p != null) {
            this.p.playSound(this.p.getLocation(), s, i, i1);
        }
    }

    public World getWorld() {
        if (this.p != null) {
            return this.p.getWorld();
        }
        return null;
    }

    public void executeWinBlock(int t) {
        if (t < 0) {
            return;
        }
        if (this.winblockRandom) {
            addTrail(new BlockWinTrail(this, t));
        } else {
            if (this.winblockMaterial != null) {
                addTrail(new BlockWinTrail(this, t, winblockMaterial));
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
                this.mapViewerPref = Selections.MapViewerPref.valueOf((String) o);
                this.saveData(PlayerData.MAPVIEWER_PREF);
                break;
            case KIT:
                this.kit = (Kit) o;
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
                RealSkywars.getPlugin().getPlayerManager().savePlayer(this, PlayerData.CAGE_BLOCK);
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

    public SWGameMode getMatch() {
        return this.room;
    }

    public void setRoom(SWGameMode o) {
        this.room = o;
    }

    public SetupRoom getSetup() {
        return this.setup;
    }

    public void setSetup(SetupRoom o) {
        this.setup = o;
    }

    public ArrayList<String> getBoughtItems() {
        return this.bought != null ? this.bought : new ArrayList<>();
    }

    public PlayerScoreboard getScoreboard() {
        return this.playerscoreboard;
    }

    public Kit getKit() {
        return this.kit == null ? new Kit() : this.kit;
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

    public double getCoins() {
        return this.coins;
    }

    public void setCoins(double v) {
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

    public Cage getCage() {
        return this.cage;
    }

    public void setCage(Cage c) {
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
        RealSkywars.getPlugin().getPlayerManager().removePlayer(this);
    }

    public void sendTitle(String s, String s1, int i, int i1, int i2) {
        if (this.p != null) this.p.sendTitle(s, s1, i, i1, i2);
    }

    public boolean hasKit() {
        return this.kit != null;
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
        if (!this.bot && pl != null && !RealSkywars.getPlugin().getGameManager().endingGames)
            this.getPlayer().hidePlayer(plugin, pl);
    }

    public void showPlayer(Plugin plugin, Player pl) {
        if (!this.bot && pl != null && !RealSkywars.getPlugin().getGameManager().endingGames)
            this.getPlayer().showPlayer(plugin, pl);
    }

    public RoomTAB getTab() {
        return this.rt;
    }

    public enum PlayerData { CAGE_BLOCK, GAME, COINS, LANG, MAPVIEWER_PREF, BOW_PARTICLES, BOUGHT_ITEMS }


    public void buyItem(String s) {
        this.bought.add(Text.strip(s));
        RealSkywars.getPlugin().getPlayerManager().savePlayer(this, PlayerData.BOUGHT_ITEMS);
    }

    public void sendActionbar(String s) {
        if (this.p != null) this.p.spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Text.color(s)));
    }

    public boolean hasParty() {
        return this.party != null;
    }

    public void createParty() {
        this.party = new Party(this);
    }

    public void disbandParty() {
        this.party.disband();
    }

    public Party getParty() {
        return this.party;
    }

    public void joinParty(RSWPlayer player) {
        this.party = player.getParty();
    }

    public void leaveParty() {
        this.party.playerLeave(this);
        this.party = null;
        this.sendMessage(RealSkywars.getPlugin().getLanguageManager().getString(this, LanguageManager.TS.PARTY_LEAVE, true).replace("%player%", this.getDisplayName()));
    }

    public Selections.MapViewerPref getMapViewerPref() {
        return this.mapViewerPref;
    }

    public void setMapViewerPref(Selections.MapViewerPref a) {
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

    public ArrayList<RSWGameLog> getGamesList() {
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

    //TAB per player

    public class RoomTAB {

        private final RSWPlayer player;
        private ArrayList<Player> show = new ArrayList<>();

        public RoomTAB(RSWPlayer player) {
            this.player = player;
            clear();
            updateRoomTAB();
        }

        public void add(Player p) {
            if (p.getUniqueId() != this.player.getUUID() && !this.show.contains(p)) {
                this.show.add(p);
            }
        }

        public void add(List<Player> p) {
            this.show.addAll(p);
        }

        public void remove(Player p) {
            this.show.remove(p);
        }

        public void reset() {
            this.show.addAll(Bukkit.getOnlinePlayers());
        }

        public void clear() {
            this.show.clear();
        }

        public void setHeaderFooter(String h, String f) {
            if (!this.player.isBot()) {
                this.player.getPlayer().setPlayerListHeaderFooter(Text.color(h), Text.color(f));
            }
        }


        public void updateRoomTAB() {
            if (!this.player.isBot()) {
                Bukkit.getOnlinePlayers().forEach(pl -> this.player.hidePlayer(RealSkywars.getPlugin(), pl));
                this.show.forEach(rswPlayer -> this.player.showPlayer(RealSkywars.getPlugin(), rswPlayer));


                String header, footer;

                if (this.player.isInMatch()) {
                    header = String.join("\n", RealSkywars.getPlugin().getLanguageManager().getList(this.player, LanguageManager.TL.TAB_HEADER_MATCH)).replace("%map%", this.player.getMatch().getName()).replace("%players%", this.player.getMatch().getPlayers().size() + "").replace("%space%", Text.makeSpace());
                    footer = String.join("\n", RealSkywars.getPlugin().getLanguageManager().getList(this.player, LanguageManager.TL.TAB_FOOTER_MATCH)).replace("%map%", this.player.getMatch().getName()).replace("%players%", this.player.getMatch().getPlayers().size() + "").replace("%space%", Text.makeSpace());
                } else {
                    header = String.join("\n", RealSkywars.getPlugin().getLanguageManager().getList(this.player, LanguageManager.TL.TAB_HEADER_OTHER)).replace("%players%", RealSkywars.getPlugin().getPlayerManager().getPlayingPlayers(GameManager.GameModes.ALL) + "").replace("%space%", Text.makeSpace());
                    footer = String.join("\n", RealSkywars.getPlugin().getLanguageManager().getList(this.player, LanguageManager.TL.TAB_FOOTER_OTHER)).replace("%players%", RealSkywars.getPlugin().getPlayerManager().getPlayingPlayers(GameManager.GameModes.ALL) + "").replace("%space%", Text.makeSpace());
                }

                this.setHeaderFooter(header, footer);
            }
        }
    }


    //Player Scoreboard

    public class PlayerScoreboard {

        private final FastBoard fb;
        private final RSWPlayer p;
        private BukkitTask task;

        public PlayerScoreboard(RSWPlayer r) {
            this.p = r;
            this.fb = new FastBoard(r.getPlayer());
            if (RealSkywars.getPlugin().getGameManager().getLobbyLocation() != null) {
                this.run();
            }

        }

        protected String variables(String s, RSWPlayer gp) {
            if (gp.isInMatch()) {
                return s.replace("%space%", Text.makeSpace()).replace("%players%", gp.getMatch().getPlayerCount() + "").replace("%nextevent%", nextEvent(gp.getMatch())).replace("%spectators%", gp.getMatch().getSpectatorsCount() + "").replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS, gp.getMatch().isRanked()) + "").replace("%map%", gp.getMatch().getName()).replace("%runtime%", Text.formatSeconds(gp.getMatch().getTimePassed())).replace("%state%", RealSkywars.getPlugin().getGameManager().getStateString(gp, gp.getMatch().getState())).replace("%mode%", gp.getMatch().getGameMode().name()).replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, gp.getMatch().isRanked()) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, gp.getMatch().isRanked()) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, gp.getMatch().isRanked()) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, gp.getMatch().isRanked()) + "");
            } else {
                return s.replace("%space%", Text.makeSpace()).replace("%coins%", gp.getCoins() + "").replace("%playing%", "" + RealSkywars.getPlugin().getPlayerManager().getPlayingPlayers(GameManager.GameModes.ALL)).replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.KILLS, false) + "").replace("%deaths%", gp.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, false) + "").replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, false) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, false) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, false) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, false) + "").replace("%playing%", "" + RealSkywars.getPlugin().getPlayerManager().getPlayingPlayers(GameManager.GameModes.ALL)).replace("%rankedkills%", gp.getStatistics(RSWPlayer.PlayerStatistics.KILLS, true) + "").replace("%rankeddeaths%", gp.getStatistics(RSWPlayer.PlayerStatistics.DEATHS, true) + "").replace("%rankedsolowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO, true) + "").replace("%rankedteamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS, true) + "").replace("%rankedloses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES, true) + "").replace("%rankedgamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED, true) + "");
            }
        }

        private String nextEvent(SWGameMode match) {
            return match.getEvents().isEmpty() ? "-" : match.getEvents().get(0).getName();
        }

        public void stop() {
            if (this.task != null) {
                this.task.cancel();
            }
            this.fb.delete();
        }

        public void run() {
            this.task = new BukkitRunnable() {
                public void run() {
                    ArrayList<String> lista;
                    String tit;
                    if (p.getState() != null) {
                        switch (p.getState()) {
                            case LOBBY_OR_NOGAME:
                                if (!RealSkywars.getPlugin().getGameManager().scoreboardInLobby() || !RealSkywars.getPlugin().getGameManager().isInLobby(p.getWorld())) {
                                    return;
                                }
                                lista = RealSkywars.getPlugin().getLanguageManager().getList(p, LanguageManager.TL.SCOREBOARD_LOBBY_LINES);
                                tit = RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.SCOREBOARD_LOBBY_TITLE, false);
                                break;
                            case CAGE:
                                lista = RealSkywars.getPlugin().getLanguageManager().getList(p, LanguageManager.TL.SCOREBOARD_CAGE_LINES);
                                tit = RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.SCOREBOARD_CAGE_TITLE, false).replace("%map%", p.getMatch().getName()).replace("%mode%", p.getMatch().getGameMode().name());
                                break;
                            case SPECTATOR:
                            case EXTERNAL_SPECTATOR:
                                lista = RealSkywars.getPlugin().getLanguageManager().getList(p, LanguageManager.TL.SCOREBOARD_SPECTATOR_LINES);
                                tit = RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.SCOREBOARD_SPECTATOR_TITLE, false).replace("%map%", p.getMatch().getName()).replace("%mode%", p.getMatch().getGameMode().name());
                                break;
                            case PLAYING:
                                lista = RealSkywars.getPlugin().getLanguageManager().getList(p, LanguageManager.TL.SCOREBOARD_PLAYING_LINES);
                                tit = RealSkywars.getPlugin().getLanguageManager().getString(p, LanguageManager.TS.SCOREBOARD_PLAYING_TITLE, false).replace("%map%", p.getMatch().getName()).replace("%mode%", p.getMatch().getGameMode().name());
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value SCOREBOARD!!! : " + p.getState());
                        }

                        List<String> send = lista.stream()
                                .map(s -> variables(s, p))
                                .collect(Collectors.toList());
                        displayScoreboard(tit, send);
                    }
                }
            }.runTaskTimer(RealSkywars.getPlugin(), 0L, 20);
        }

        private void displayScoreboard(String title, List<String> elements) {
            this.fb.updateTitle(title);
            this.fb.updateLines(elements);
        }
    }
}
