package josegamerpt.realskywars.player;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.*;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.configuration.Players;
import josegamerpt.realskywars.effects.BlockWinTrail;
import josegamerpt.realskywars.effects.Trail;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.PlayerManager;
import josegamerpt.realskywars.modes.SWGameMode;
import josegamerpt.realskywars.utils.Text;
import org.apache.http.util.TextUtils;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.scoreboard.DisplaySlot;

import java.util.*;

public class RSWPlayer {

    private RoomTAB rt;
    private String anonName = "?";
    private final List<Trail> trails = new ArrayList<>();
    private Player p;
    private PlayerState state = PlayerState.LOBBY_OR_NOGAME;
    private String language = LanguageManager.getDefaultLanguage();
    private SWGameMode room;
    private SetupRoom setup;
    private Team team;
    private Cage cage;
    //statistics
    private int gamekills = 0;
    private int totalkills;
    private int deaths;
    private int winsSOlO;
    private int winsTEAMS;
    private int loses;
    private int gamesPlayed;
    private Double coins = 0D;
    private Double balanceGame = 0D;
    private PlayerScoreboard playerscoreboard;
    private Material cageBlock = Material.GLASS;
    private List<String> bought = new ArrayList<>();
    private HashMap<Enum.Selection, Enum.Selections> selections = new HashMap<>();
    private Boolean bot = false;
    private Kit kit;
    private Particle bowParticle;
    private boolean winblockRandom = false;
    private Material winblockMaterial;
    private Boolean invincible = false;

    public RSWPlayer(Player jog, RSWPlayer.PlayerState estado, SWGameMode rom, int tk, int d, int solowin, int teamwin, Double coi, String lang,
                     List<String> bgh, int l, int gp) {
        anonName = Text.anonName();

        this.p = jog;
        this.state = estado;
        this.room = rom;
        this.totalkills = tk;
        this.winsSOlO = solowin;
        this.winsTEAMS = teamwin;
        this.deaths = d;
        this.coins = coi;
        this.language = lang;
        this.bought = bgh;
        this.loses = l;
        this.gamesPlayed = gp;
        this.playerscoreboard = new PlayerScoreboard(this);

        this.rt = new RoomTAB(this);
    }

    @Override
    public String toString() {
        return "RSWPlayer{" +
                "anonName='" + anonName + '\'' +
                ", trails=" + trails +
                ", p=" + p +
                ", state=" + state +
                ", language='" + language + '\'' +
                ", room=" + room +
                ", setup=" + setup +
                ", team=" + team +
                ", cage=" + cage +
                ", gamekills=" + gamekills +
                ", totalkills=" + totalkills +
                ", deaths=" + deaths +
                ", winsSOlO=" + winsSOlO +
                ", winsTEAMS=" + winsTEAMS +
                ", loses=" + loses +
                ", gamesPlayed=" + gamesPlayed +
                ", coins=" + coins +
                ", balanceGame=" + balanceGame +
                ", playerscoreboard=" + playerscoreboard +
                ", cageBlock=" + cageBlock +
                ", bought=" + bought +
                ", selections=" + selections +
                ", bot=" + bot +
                ", kit=" + kit +
                ", bowParticle=" + bowParticle +
                ", winblockRandom=" + winblockRandom +
                ", winblockMaterial=" + winblockMaterial +
                ", invincible=" + invincible +
                '}';
    }

    public RSWPlayer(boolean anonName) {
        if (anonName)
        {
            this.anonName = Text.anonName();
        }
        bot = true;
    }

    public void save() {
        for (RSWPlayer gp : PlayerManager.getPlayers()) {
            if (p != null && gp.getUniqueId().equals(p.getUniqueId())) {
                return;
            }
        }
        if (!this.bot) {
            PlayerManager.addPlayer(this);
        }
    }

    public boolean isInMatch() {
        return room != null;
    }

    public void addStatistic(Enum.Statistic t, int i) {
        switch (t) {
            case SOLO_WIN:
                this.winsSOlO += i;
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Win"));
                this.addStatistic(Enum.Statistic.GAMES_PLAYED, 1);
                break;
            case TEAM_WIN:
                this.winsTEAMS += i;
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Win"));
                this.addStatistic(Enum.Statistic.GAMES_PLAYED, 1);
                break;
            case KILL:
                this.gamekills += i;
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Kill"));
                break;
            case LOSE:
                this.loses += i;
                break;
            case DEATH:
                this.deaths += i;
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Death"));
                this.addStatistic(Enum.Statistic.LOSE, 1);
                this.addStatistic(Enum.Statistic.GAMES_PLAYED, 1);
                break;
            case GAMES_PLAYED:
                this.gamesPlayed += i;
                break;
        }
    }

    public void saveData() {
        this.totalkills += this.gamekills;
        this.coins += this.balanceGame;
        this.balanceGame = 0D;
        this.gamekills = 0;
        PlayerManager.savePlayer(this);
    }

    public double getGameBalance() {
        return (coins + balanceGame);
    }

    public void sendMessage(String string) {
        if (!this.bot && !TextUtils.isEmpty(string)) {
            p.sendMessage(Text.color(string));
        }
    }

    public void resetData() {
        Players.file().set(p.getUniqueId().toString(), null);
        Players.save();
        PlayerManager.removePlayer(this);
        p.kickPlayer(LanguageManager.getPrefix() + "§4Your data was resetted with success. \n §cPlease join the server again to complete the reset.");
    }

    public String getName() {
        if (p != null) {
            return p.getName();
        } else {
            return anonName;
        }
    }

    public void teleport(Location l) {
        if (p != null) {
            p.teleport(l);
        }
    }

    public void stopTrails() {
        trails.forEach(Trail::cancelTask);
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
        if (winblockRandom) {
            addTrail(new BlockWinTrail(this, t));
        } else {
            if (winblockMaterial != null) {
                addTrail(new BlockWinTrail(this, t, winblockMaterial));
            }
        }
    }

    public void delCage() {
        if (this.cage != null) {
            this.cage.removePlayer(this);
        }
    }

    public void setFlying(boolean b) {
        if (this.p != null) {
            if (b) {
                this.p.setAllowFlight(true);
                this.p.setFlying(true);
            } else {
                this.p.setAllowFlight(false);
                this.p.setFlying(false);
            }
        }
    }

    public UUID getUniqueId() {
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
            case KIT:
                this.kit = (Kit) o;
                break;
            case BOW_PARTICLES:
                this.bowParticle = (Particle) o;
                break;
            case CAGE_BLOCK:
                Material m = (Material) o;
                if (m != this.cageBlock) {
                    this.cageBlock = m;
                    if (isInMatch()) {
                        switch (this.getRoom().getMode()) {
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
                break;
            case WIN_BLOCKS:
                if (o.equals("RandomBlock")) {
                    this.winblockRandom = true;
                } else {
                    this.winblockRandom = false;
                    this.winblockMaterial = Material.valueOf((String) o);
                }
                break;
            case STATE:
                this.state = (PlayerState) o;
                break;
            case LANGUAGE:
                this.language = (String) o;
                break;
        }
    }

    public Object getStatistics(PlayerStatistics pp) {
        switch (pp) {
            case LOSES:
                return this.loses;
            case DEATHS:
                return this.deaths;
            case WINS_SOLO:
                return this.winsSOlO;
            case WINS_TEAMS:
                return this.winsTEAMS;
            case TOTAL_KILLS:
                return this.totalkills;
            case GAMES_PLAYED:
                return this.gamesPlayed;
            case GAME_BALANCE:
                return this.balanceGame;
            case GAME_KILLS:
                return this.gamekills;
        }
        return null;
    }

    public Enum.Selections getSelection(Enum.Selection m) {
        return this.selections.get(m);
    }

    public void setSelection(Enum.Selection s, Enum.Selections ss) {
        this.selections.remove(s);
        this.selections.put(s, ss);
    }

    public RSWPlayer.PlayerState getState() {
        return this.state;
    }

    public SWGameMode getRoom() {
        return this.room;
    }

    public void setRoom(Object o) {
        this.room = (SWGameMode) o;
    }

    public SetupRoom getSetup() {
        return this.setup;
    }

    public void setSetup(SetupRoom o) {
        this.setup = o;
    }

    public <V, K> HashMap<Enum.Selection, Enum.Selections> getSelections() {
        return this.selections;
    }

    public void setSelections(HashMap<Enum.Selection, Enum.Selections> ss) {
        this.selections = ss;
    }

    public List<String> getBoughtItems() {
        return this.bought;
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
        }
        return null;
    }

    public Material getCageBlock() {
        return this.cageBlock;
    }

    public double getCoins() {
        return this.coins;
    }

    public void setCoins(double v) {
        this.coins = v;
    }

    public void heal() {
        if (this.p != null) {
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

        stopTrails();

        this.playerscoreboard.stop();
        p.saveData();
        PlayerManager.removePlayer(this);
    }

    public void sendCenterMessageList(List<String> l) {
        l.forEach(s -> sendMessage(Text.centerMessage(s)));
    }

    public void sendTitle(String s, String s1, int i, int i1, int i2) {
        if (this.p != null) {
            this.p.sendTitle(s, s1, i, i1, i2);
        }
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

    public CharSequence getDisplayName() {
        if (!this.bot)
        {
            return this.p.getDisplayName();
        }
        return this.anonName;
    }

    public void sendCenterMessage(String r) {
        sendMessage(Text.centerMessage(r));
    }

    public boolean isBot() {
        return this.bot;
    }

    public void hidePlayer(Plugin plugin, Player pl) {
        if (!this.bot)
        {
            this.getPlayer().hidePlayer(plugin, pl);
        }
    }

    public void showPlayer(Plugin plugin, Player pl) {
        if (!this.bot && pl != null)
        {
            this.getPlayer().showPlayer(plugin, pl);
        }
    }

    public RoomTAB getTab() {
        return this.rt;
    }

    //ENUMs

    public enum PlayerState {
        LOBBY_OR_NOGAME, CAGE, PLAYING, SPECTATOR, EXTERNAL_SPECTATOR
    }

    public enum PlayerProperties {KIT, BOW_PARTICLES, CAGE_BLOCK, STATE, LANGUAGE, WIN_BLOCKS}

    public enum PlayerStatistics {WINS_SOLO, WINS_TEAMS, TOTAL_KILLS, DEATHS, LOSES, GAME_BALANCE, GAME_KILLS, GAMES_PLAYED}

    //Player Scoreboard

    public class PlayerScoreboard {

        public RSWPlayer linked;
        public BukkitTask task;

        public PlayerScoreboard(RSWPlayer r) {
            linked = r;
            if (GameManager.getLobbyLocation() != null)
                run();
        }

        protected String variables(String s, RSWPlayer gp) {
            if (gp.getRoom() != null) {
                return s.replace("%space%", Text.makeSpace()).replace("%players%", gp.getRoom().getPlayersCount() + "")
                        .replace("%spectators%", gp.getRoom().getSpectatorsCount() + "").replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS) + "")
                        .replace("%map%", gp.getRoom().getName()).replace("%runtime%", gp.getRoom().getTimePassed() + "").replace("%state%", GameManager.getStateString(gp, gp.getRoom().getState())).replace("%mode%", gp.getRoom().getMode().name()).replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED) + "");
            } else {
                return s.replace("%space%", Text.makeSpace()).replace("%coins%", gp.getCoins() + "")
                        .replace("%kills%", gp.getStatistics(RSWPlayer.PlayerStatistics.TOTAL_KILLS) + "").replace("%deaths%", gp.getStatistics(RSWPlayer.PlayerStatistics.DEATHS) + "").replace("%playing%", "" + PlayerManager.countPlayingPlayers()).replace("%solowins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_SOLO) + "").replace("%teamwins%", gp.getStatistics(RSWPlayer.PlayerStatistics.WINS_TEAMS) + "").replace("%loses%", gp.getStatistics(RSWPlayer.PlayerStatistics.LOSES) + "").replace("%gamesplayed%", gp.getStatistics(RSWPlayer.PlayerStatistics.GAMES_PLAYED) + "");
            }
        }

        public void stop() {
            if (this.task != null) {
                task.cancel();
            }
        }

        public void run() {
            task = new BukkitRunnable() {
                public void run() {
                    ArrayList<String> lista;
                    String tit;
                    if (linked.getState() != null) {
                        switch (linked.getState()) {
                            case LOBBY_OR_NOGAME:
                                if (!GameManager.scoreboardInLobby()) {
                                    return;
                                }
                                if (GameManager.getLobbyLocation().getWorld() != linked.getWorld()) {
                                    return;
                                }
                                lista = LanguageManager.getList(linked, Enum.TL.SCOREBOARD_LOBBY_LINES);
                                tit = LanguageManager.getString(linked, Enum.TS.SCOREBOARD_LOBBY_TITLE, false);
                                break;
                            case CAGE:
                                lista = LanguageManager.getList(linked, Enum.TL.SCOREBOARD_CAGE_LINES);
                                tit = LanguageManager.getString(linked, Enum.TS.SCOREBOARD_CAGE_TITLE, false).replace("%map%", linked.getRoom().getName());
                                break;
                            case SPECTATOR:
                            case EXTERNAL_SPECTATOR:
                                lista = LanguageManager.getList(linked, Enum.TL.SCOREBOARD_SPECTATOR_LINES);
                                tit = LanguageManager.getString(linked, Enum.TS.SCOREBOARD_SPECTATOR_TITLE, false).replace("%map%", linked.getRoom().getName());
                                break;
                            case PLAYING:
                                lista = LanguageManager.getList(linked, Enum.TL.SCOREBOARD_PLAYING_LINES);
                                tit = LanguageManager.getString(linked, Enum.TS.SCOREBOARD_PLAYING_TITLE, false).replace("%map%", linked.getRoom().getName());
                                break;
                            default:
                                throw new IllegalStateException("Unexpected value SCOREBOARD!!! : " + linked.getState());
                        }
                        Map<String, Integer> linhas = new HashMap<>();

                        int linha = lista.size();
                        for (String s : lista) {
                            linhas.put(variables(s, linked), linha--);
                        }
                        displayScoreboard(tit, linked, linhas);
                    }
                }
            }.runTaskTimer(RealSkywars.getPlugin(), 0L, 20);
        }

        private void displayScoreboard(String title, RSWPlayer p, Map<String, Integer> elements) {
            if (p.getPlayer() != null) {
                if (title.length() > 32) {
                    title = title.substring(0, 32);
                }
                while (elements.size() > 15) {
                    String minimumKey = (String) elements.keySet().toArray()[0];
                    int minimum = elements.get(minimumKey);
                    for (String string : elements.keySet()) {
                        if (elements.get(string) < minimum
                                || (elements.get(string) == minimum && string.compareTo(minimumKey) < 0)) {
                            minimumKey = string;
                            minimum = elements.get(string);
                        }
                    }
                    elements.remove(minimumKey);
                }
                for (String string2 : new ArrayList<>(elements.keySet())) {
                    if (string2 != null && string2.length() > 40) {
                        int value = elements.get(string2);
                        elements.remove(string2);
                        elements.put(string2.substring(0, 40), value);
                    }
                }
                if (p.getPlayer().getScoreboard() == null
                        || p.getPlayer().getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16)) == null) {
                    p.getPlayer().setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
                    p.getPlayer().getScoreboard().registerNewObjective(p.getUniqueId().toString().substring(0, 16), "dummy",
                            "dummy");
                    p.getPlayer().getScoreboard().getObjective(p.getUniqueId().toString().substring(0, 16))
                            .setDisplaySlot(DisplaySlot.SIDEBAR);
                }
                p.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).setDisplayName(title);
                for (String string2 : elements.keySet()) {
                    if (p.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string2).getScore() != elements
                            .get(string2)) {
                        p.getPlayer().getScoreboard().getObjective(DisplaySlot.SIDEBAR).getScore(string2)
                                .setScore(elements.get(string2));
                    }
                }
                for (String string2 : new ArrayList<>(p.getPlayer().getScoreboard().getEntries())) {
                    if (!elements.containsKey(string2)) {
                        p.getPlayer().getScoreboard().resetScores(string2);
                    }
                }
            }
        }
    }
}
