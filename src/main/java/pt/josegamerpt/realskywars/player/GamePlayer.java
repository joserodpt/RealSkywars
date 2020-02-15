package pt.josegamerpt.realskywars.player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.Player;

import pt.josegamerpt.realskywars.Debugger;
import pt.josegamerpt.realskywars.cages.Cage;
import pt.josegamerpt.realskywars.classes.*;
import pt.josegamerpt.realskywars.classes.Enum;
import pt.josegamerpt.realskywars.classes.Enum.InteractionState;
import pt.josegamerpt.realskywars.classes.Enum.PlayerState;
import pt.josegamerpt.realskywars.classes.Enum.Selection;
import pt.josegamerpt.realskywars.classes.Enum.Selections;
import pt.josegamerpt.realskywars.configuration.Config;
import pt.josegamerpt.realskywars.configuration.Players;
import pt.josegamerpt.realskywars.effects.BlockWinTrail;
import pt.josegamerpt.realskywars.managers.LanguageManager;
import pt.josegamerpt.realskywars.managers.PlayerManager;
import pt.josegamerpt.realskywars.utils.Entry;
import pt.josegamerpt.realskywars.utils.EntryBuilder;
import pt.josegamerpt.realskywars.utils.Text;

public class GamePlayer {

    public Player p;
    public PlayerState state = PlayerState.LOBBY_OR_NOGAME;
    public InteractionState istate = InteractionState.NONE;

    public String language = LanguageManager.getDefaultLanguage();

    public GameRoom room;
    public SetupRoom setup;
    public Team team;
    public Cage cage;

    public int GameDeaths = 0;
    public int gamekills = 0;
    public int totalkills;
    public int deaths;
    public int soloWins;
    public int teamWins;
    public int loses;
    public int gamesPlayed;
    public Double coins = 0D;
    public Double balanceGame = 0D;
    public PlayerScoreboard ps;

    public Material cageBlock = Material.GLASS;

    public List<String> bought = new ArrayList<String>();
    public HashMap<Selection, Selections> selections = new HashMap<Selection, Selections>();
    public Boolean bot = false;

    public Kit kit;
    public Particle bowParticle;
    public boolean winblockRandom = false;
    public Material winblockMaterial;
    public List<Trail> trails = new ArrayList<Trail>();

    public GamePlayer(Player jog, PlayerState estado, GameRoom rom, int tk, int d, int solowin, int teamwin, Double coi, String lang,
                      List<String> bgh, int l, int gp) {
        this.p = jog;
        this.state = estado;
        this.room = rom;
        this.totalkills = tk;
        this.soloWins = solowin;
        this.teamWins = teamwin;
        this.deaths = d;
        this.coins = coi;
        this.language = lang;
        this.bought = bgh;
        this.loses = l;
        this.gamesPlayed = gp;
        ps = new PlayerScoreboard(this);
    }

    public GamePlayer(String lang) {
        this.language = lang;
    }

    public GamePlayer() {
        bot = true;
    }

    public List<Entry> getInfoList() {
        EntryBuilder b = new EntryBuilder().blank().blank().next("Player: " + this.p.getName())
                .next("Player State: " + this.state)
                .next("Interaction State: " + this.istate)
                .next("Language: " + this.language);

        if (this.room != null) {
            b.next("Room: " + this.room.getName());
        } else {
            b.next("Room: None");
        }
        if (this.team != null) {
            b.next("Team: " + this.team.getName() + " | Members: " + this.team.members.size() + " | Full: " + this.team.isTeamFull());
        } else {
            b.next("Team: None");
        }

        b.next("Cage Block: " + this.cageBlock)
                .next("Solo Wins: " + this.soloWins)
                .next("Team Wins: " + this.teamWins)
                .next("Game Kills: " + this.gamekills)
                .next("Kills: " + this.totalkills)
                .next("Deaths: " + this.deaths)
                .next("Coins: " + this.coins)
                .next("Game Balance: " + this.balanceGame);

        if (this.kit == null) {
            b.next("Kit: None");
        } else {
            b.next("Kit: " + this.kit.name);
        }

        if (this.bowParticle == null) {
            b.next("Bow Particle: None");
        } else {
            b.next("Bow Particle: " + this.bowParticle.name());
        }

        if (this.winblockMaterial == null) {
            b.next("Win Block: None");
        } else {
            b.next("Win Block: " + this.winblockMaterial.name());
        }
        b.next("Win Block Random: " + this.winblockRandom);
        b.next("Trails:");

        trails.forEach(trail -> b.next("> Trail Type: " + trail.getType().name()));

        b.blank().blank();
        return b.build();
    }

    public void save() {
        for (GamePlayer gp : PlayerManager.players) {
            if (p != null) {
                if (gp.p.getUniqueId().equals(p.getUniqueId())) {
                    return;
                }
            }
        }
        if (bot == false) {
            PlayerManager.players.add(this);
        }
    }

    public void addStatistic(Enum.Statistic t, int i) {
        switch (t) {
            case SOLO_WIN:
                this.soloWins += i;
                this.balanceGame = (this.balanceGame + Config.file().getDouble("Config.Coins.Per-Win"));
                this.addStatistic(Enum.Statistic.GAMES_PLAYED, 1);
                break;
            case TEAM_WIN:
                this.teamWins += i;
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

    public void setStatistic(Enum.Statistic t, int i) {
        switch (t) {
            case SOLO_WIN:
                this.soloWins = i;
                break;
            case TEAM_WIN:
                this.teamWins = i;
                break;
            case KILL:
                this.gamekills = i;
                break;
            case LOSE:
                this.loses = i;
                break;
            case DEATH:
                this.deaths = i;
                break;
            case GAMES_PLAYED:
                this.gamesPlayed = i;
                break;
        }
    }

    public void saveData() {
        totalkills += gamekills;
        coins = (coins + balanceGame);
        balanceGame = 0D;
        gamekills = 0;
        PlayerManager.savePlayer(this);
    }

    public double getSumBalTotal() {
        return (coins + balanceGame);
    }

    public void sendMessage(String string) {
        if (p != null) {
            p.sendMessage(Text.addColor(string));
        }
    }

    public void resetData() {
        Players.file().set(p.getUniqueId().toString(), null);
        Players.save();
        PlayerManager.players.remove(this);
        p.kickPlayer(LanguageManager.getPrefix() + "§4Your data was resetted with success. \n §cPlease join the server again to complete the reset.");
    }

    public String getName() {
        if (p != null) {
            return p.getName();
        } else {
            return "Null Name";
        }
    }

    public void teleport(Location l) {
        if (p != null) {
            p.teleport(l);
        }
    }

    public void stopTrails() {
        for (Trail t : trails) {
            t.cancelTask();
        }
    }

    public boolean isInGame() {
        return this.room != null;
    }

    public void addTrail(Trail t) {
        this.trails.add(t);
        Debugger.print(trails + "");
    }

    public void removeTrail(Trail t) {
        this.trails.remove(t);
        Debugger.print(trails + "");
    }

    public Location getLocation() {
        return p.getLocation();
    }

    public World getWorld() {
        return p.getWorld();
    }

    public void setWinBlock(String s) {
        if (s.equals("RandomBlock")) {
            this.winblockRandom = true;
        } else {
            this.winblockRandom = false;
            this.winblockMaterial = Material.valueOf(s);
        }
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

    public void leaveCage() {
        if (this.cage != null) {
            this.cage.removePlayer(this);
        }
    }

    public void setFlying(boolean b) {
        if (b) {
            p.setAllowFlight(true);
            p.setFlying(true);
        } else {
            p.setAllowFlight(false);
            p.setFlying(false);
        }
    }
}
