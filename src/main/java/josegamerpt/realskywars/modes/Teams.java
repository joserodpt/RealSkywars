package josegamerpt.realskywars.modes;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.cages.Cage;
import josegamerpt.realskywars.classes.Enum;
import josegamerpt.realskywars.classes.SWWorld;
import josegamerpt.realskywars.classes.Team;
import josegamerpt.realskywars.configuration.Config;
import josegamerpt.realskywars.game.Countdown;
import josegamerpt.realskywars.managers.GameManager;
import josegamerpt.realskywars.managers.LanguageManager;
import josegamerpt.realskywars.managers.PlayerManager;
import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ArenaCuboid;
import josegamerpt.realskywars.utils.Demolition;
import josegamerpt.realskywars.utils.MathUtils;
import josegamerpt.realskywars.utils.Text;
import josegamerpt.realskywars.worlds.WorldManager;
import org.bukkit.*;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class Teams implements SWGameMode {

    private final WorldManager wm = RealSkywars.getWorldManager();
    private final HashMap<String, Integer> tasks = new HashMap<>();
    public int id;
    public String name;
    public int maxPlayers;
    public int maxMembersTeam;
    public Enum.GameState state;
    public SWWorld world;
    public WorldBorder border;
    public BossBar bossBar;
    public ArenaCuboid arenaCuboid;
    public int borderSize;
    public ArrayList<Team> teams;
    public ArrayList<RSWPlayer> inRoom = new ArrayList<>();
    public ArrayList<Integer> votes = new ArrayList<>();
    public ArrayList<UUID> voters = new ArrayList<>();
    public Enum.TierType tierType = Enum.TierType.NORMAL;
    public Countdown timer;
    public int timePassed = 0;
    public Location spectatorLocation;
    public Boolean specEnabled;
    public Boolean instantEnding;
    // b-1, n-2, o-3, c-4

    public Teams(int i, String nome, World w, Enum.GameState estado, ArrayList<Team> teams, int maxPlayers,
                 Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Location pos1, Location pos2) {
        this.id = i;
        this.name = nome;
        this.world = new SWWorld(this, w);
        this.state = estado;
        this.teams = teams;
        this.maxPlayers = maxPlayers;
        this.maxMembersTeam = teams.get(0).getMaxMembers();
        this.spectatorLocation = spectatorLocation;
        this.specEnabled = specEnabled;
        this.instantEnding = instantEnding;
        this.arenaCuboid = new ArenaCuboid(pos1, pos2);
        this.border = w.getWorldBorder();
        this.border.setCenter(this.arenaCuboid.getCenter());
        this.borderSize = this.arenaCuboid.getSizeX();
        this.border.setSize(this.borderSize);

        votes.add(2);

        bossBar = Bukkit.createBossBar(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)),
                BarColor.WHITE, BarStyle.SOLID);
    }

    public void saveRoom() {
        GameManager.addRoom(this);
    }

    public String getName() {
        return this.name;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    public int getPlayersCount() {
        return this.teams.stream().mapToInt(team -> team.getMembers().size()).sum();
    }

    public ArrayList<RSWPlayer> getPlayers() {
        ArrayList<RSWPlayer> players = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.PLAYING || rswPlayer.getState() == RSWPlayer.PlayerState.CAGE)
                players.add(rswPlayer);
        }

        return players;
    }

    public ArrayList<RSWPlayer> getSpectators() {
        ArrayList<RSWPlayer> spec = new ArrayList<>();
        for (RSWPlayer rswPlayer : this.inRoom) {
            if (rswPlayer.getState() == RSWPlayer.PlayerState.SPECTATOR || rswPlayer.getState() == RSWPlayer.PlayerState.EXTERNAL_SPECTATOR)
                spec.add(rswPlayer);
        }

        return spec;
    }

    public int getSpectatorsCount() {
        return this.getSpectators().size();
    }

    public World getWorld() {
        return this.world.getWorld();
    }

    public void kickPlayers(String msg) {
        ArrayList<RSWPlayer> tmp = new ArrayList<>(this.inRoom);

        for (RSWPlayer p : tmp) {
            if (msg != null) {
                p.sendMessage(Text.color(msg));
            }
            removePlayer(p);
        }
    }

    public Enum.GameState getState() {
        return this.state;
    }

    public void setState(Enum.GameState w) {
        this.state = w;
    }

    public boolean isPlaceHolder() {
        return false;
    }

    public String forceStart(RSWPlayer p) {
        if (this.getPlayersCount() < (this.maxMembersTeam() + 1)) {
            return LanguageManager.getString(p, Enum.TS.CMD_CANT_FORCESTART, true);
        } else {
            switch (this.state) {
                case PLAYING:
                case FINISHING:
                    return LanguageManager.getString(p, Enum.TS.ALREADY_STARTED, true);
                default:
                    startGameFunction();
                    return LanguageManager.getString(p, Enum.TS.CMD_MATCH_FORCESTART, true);
            }
        }
    }

    public void cancelTask(String task) {
        if (this.tasks.containsKey(task)) {
            Bukkit.getScheduler().cancelTask(this.tasks.get(task));
        }
    }

    public ArrayList<Cage> getCages() {
        ArrayList<Cage> c = new ArrayList<>();
        this.teams.forEach(team -> c.add(team.getTeamCage()));
        return c;
    }

    public ArrayList<Team> getTeams() {
        return this.teams;
    }

    @Override
    public int maxMembersTeam() {
        return this.maxMembersTeam;
    }

    @Override
    public void clear() {
        this.wm.deleteWorld(this.getWorld().getName(), true);
    }

    @Override
    public void reset() {
        this.state = Enum.GameState.RESETTING;

        this.kickPlayers(LanguageManager.getString(new RSWPlayer(false), Enum.TS.ARENA_RESET, true));
        this.resetArena();
    }

    @Override
    public ArenaCuboid getArena() {
        return this.arenaCuboid;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getBorderSize() {
        return this.borderSize;
    }

    @Override
    public void addVote(UUID u, int i) {
        this.votes.add(i);
        this.voters.add(u);
    }

    private void startGameFunction() {
        this.state = Enum.GameState.PLAYING;

        cancelTask("startingcount");

        int timeleft = Config.file().getInt("Config.Maximum-Game-Time");

        int bigger = MathUtils.bigger(votes.stream().mapToInt(i -> i).toArray());
        switch (bigger) {
            case 1:
                this.tierType = Enum.TierType.BASIC;
                break;
            case 3:
                this.tierType = Enum.TierType.OP;
                break;
            case 4:
                this.tierType = Enum.TierType.CAOS;
                break;
            default:
                this.tierType = Enum.TierType.NORMAL;
                break;
        }

        for (Team t : this.teams) {
            for (RSWPlayer p : t.getMembers()) {
                if (p.getPlayer() != null) {
                    p.getInventory().clear();

                    bossBar.addPlayer(p.getPlayer());

                    //start msg
                    for (String s : Text.color(LanguageManager.getList(p, Enum.TL.ARENA_START))) {
                        p.sendCenterMessage(s.replace("%chests%", this.tierType.name() + "")
                                .replace("%kit%", p.getKit().getName() + ""));
                    }

                    if (p.hasKit()) {
                        p.getPlayer().getInventory().setContents(p.getKit().getContents());
                    }

                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.PLAYING);
                }
            }
            t.openCage();
        }

        this.timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), timeleft, () -> {
            //
        }, () -> {
            bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_DEATHMATCH)));
            bossBar.setProgress(0);
            bossBar.setColor(BarColor.RED);

            this.getPlayers().forEach(rswPlayer -> rswPlayer.sendTitle("", Text.color(LanguageManager.getString(rswPlayer, Enum.TS.TITLE_DEATHMATCH, false)), 10, 20,
                    5));

            border.setSize(this.borderSize / 2, 30L);
            border.setCenter(this.arenaCuboid.getCenter());
        }, (t) -> {
            bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_RUNTIME).replace("%time%",
                    t.getSecondsLeft() + "")));
            double div = (double) t.getSecondsLeft() / (double) timeleft;
            bossBar.setProgress(div);

            //future events
        });
        timer.scheduleTimer();

        startCountingTime();
    }

    private void startCountingTime() {
        this.tasks.put("timeCounter", Bukkit.getScheduler().scheduleSyncRepeatingTask(RealSkywars.getPlugin(), () -> timePassed += 1, 0L, 20L));
    }

    public void removePlayer(RSWPlayer p) {
        if (bossBar != null && !p.isBot()) {
            bossBar.removePlayer(p.getPlayer());
        }

        p.setInvincible(false);
        p.sendMessage(Text.color(LanguageManager.getString(p, Enum.TS.MATCH_LEAVE, true)));

        switch (p.getState()) {
            case PLAYING:
            case CAGE:
                GameManager.tpToLobby(p);
                break;
        }

        PlayerManager.giveItems(p.getPlayer(), PlayerManager.PlayerItems.LOBBY);

        p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.LOBBY_OR_NOGAME);
        p.setFlying(false);
        p.heal();

        this.inRoom.remove(p);
        if (p.hasTeam()) {
            p.getTeam().removePlayer(p);
        }
        p.setRoom(null);

        if (this.state == Enum.GameState.PLAYING || this.state == Enum.GameState.FINISHING) {
                checkWin();

        }

    }

    @Override
    public Location getSpectatorLocation() {
        return new Location(getWorld(), this.spectatorLocation.getBlockX(), this.spectatorLocation.getBlockY(), this.spectatorLocation.getBlockZ());
    }

    @Override
    public Location getPOS1() {
        return this.arenaCuboid.getPOS1();
    }

    @Override
    public Location getPOS2() {
        return this.arenaCuboid.getPOS2();
    }

    @Override
    public ArrayList<UUID> getVoters() {
        return this.voters;
    }

    public ArrayList<Integer> getVoteList() {
        return this.votes;
    }

    public void addPlayer(RSWPlayer gp) {
        if (getPlayersCount() == maxPlayers) {
            gp.sendMessage(LanguageManager.getPrefix() + "&cThis room is full");
            return;
        }

        if (state == Enum.GameState.RESETTING) {
            gp.sendMessage(LanguageManager.getPrefix() + "&cYou cant join this room.");
            return;
        }
        if (state == Enum.GameState.FINISHING || state == Enum.GameState.PLAYING
                || state == Enum.GameState.STARTING) {
            if (this.specEnabled) {
                spectate(gp, SpectateType.EXTERNAL, null);
            } else {
                gp.sendMessage(LanguageManager.getPrefix() + "&cSpectating is not enabled in this room.");
            }
            return;
        }

        gp.setRoom(this);
        gp.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

        for (RSWPlayer ws : this.inRoom) {
            if (gp.getPlayer() != null) {
                ws.sendMessage(LanguageManager.getString(ws, Enum.TS.PLAYER_JOIN_ARENA, true).replace("%player%",
                        gp.getDisplayName()).replace("%players%", getPlayersCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
            }
        }

        this.inRoom.add(gp);
        gp.heal();

        if (gp.getPlayer() != null) {
            bossBar.addPlayer(gp.getPlayer());
            ArrayList<String> up = LanguageManager.getList(gp, Enum.TL.TITLE_ROOMJOIN);
            gp.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
        }

        //cage

        for (Team c : this.teams) {
            if (!c.isTeamFull()) {
                c.addPlayer(gp);
                break;
            }
        }

        PlayerManager.giveItems(gp.getPlayer(), PlayerManager.PlayerItems.CAGE);

        if (getPlayersCount() == this.maxMembersTeam + 1) {
            startRoom();
        }
    }

    private void startRoom() {
        Countdown timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-To-Start"),
                () -> {
                    //
                }, () -> {
            startGameFunction();
            this.tasks.remove("startingcount");
        }, (t) -> {
            if (getPlayersCount() < this.maxMembersTeam + 1) {
                Bukkit.getScheduler().cancelTask(t.getTaskId());
                for (RSWPlayer p : this.inRoom) {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.ARENA_CANCEL, true));
                }
                bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)));
                bossBar.setProgress(0D);
                this.state = Enum.GameState.WAITING;
            } else {
                for (RSWPlayer p : this.getPlayers()) {
                    p.sendMessage(LanguageManager.getString(p, Enum.TS.ARENA_START_COUNTDOWN, true)
                            .replace("%time%", t.getSecondsLeft() + ""));
                }
                bossBar.setTitle(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_STARTING)
                        .replace("%time%", t.getSecondsLeft() + "")));
                double div = (double) t.getSecondsLeft()
                        / (double) Config.file().getInt("Config.Time-To-Start");
                bossBar.setProgress(div);
            }
        });

        timer.scheduleTimer();
        this.tasks.put("startingcount", timer.getTaskId());
    }

    public boolean isSpectatorEnabled() {
        return this.specEnabled;
    }

    public boolean isInstantEndEnabled() {
        return this.instantEnding;
    }

    public Enum.TierType getTierType() {
        return this.tierType;
    }

    public void setTierType(Enum.TierType b) {
        this.tierType = b;
    }

    public int getTimePassed() {
        return this.timePassed;
    }

    public void resetArena() {
        this.state = Enum.GameState.RESETTING;

        this.teams.forEach(Team::reset);
        this.voters.clear();
        this.votes.clear();
        votes.add(2);

        bossBar = Bukkit.createBossBar(Text.color(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_WAIT)),
                BarColor.WHITE, BarStyle.SOLID);

        cancelTask("timeCounter");
        this.tasks.clear();
        timePassed = 0;

        this.world.resetWorld();
    }

    public void setSpectator(boolean b) {
        this.specEnabled = b;
    }

    public void setInstantEnd(boolean b) {
        this.instantEnding = b;
    }

    public void spectate(RSWPlayer p, SpectateType st, Location killLoc) {
        p.setInvincible(true);
        p.setFlying(true);
        PlayerManager.giveItems(p.getPlayer(), PlayerManager.PlayerItems.SPECTATOR);
        switch (st) {
            case GAME:
                if (p.hasTeam()) {
                    Team t = p.getTeam();
                    t.removePlayer(p);
                    if (t.isEliminated()) {
                        new Demolition(this.getSpectatorLocation(), p.getCage(), 5, 3).start(RealSkywars.getPlugin());
                    }
                }
                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.SPECTATOR);
                p.heal();
                p.getPlayer().teleport(killLoc.add(0, 1, 0));

                sendLog(p);
                checkWin();
                break;
            case EXTERNAL:
                this.inRoom.add(p);
                p.setRoom(this);

                p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.EXTERNAL_SPECTATOR);
                p.teleport(this.getSpectatorLocation());
                p.getPlayer().setGameMode(GameMode.SURVIVAL);
                p.heal();
                p.sendMessage(LanguageManager.getString(p, Enum.TS.MATCH_SPECTATE, true));
                break;
        }
    }

    private void sendLog(RSWPlayer p) {
        for (String s : Text.color(LanguageManager.getList(p, Enum.TL.END_LOG))) {
            p.sendCenterMessage(s.replace("%recvcoins%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_BALANCE) + "")
                    .replace("%totalcoins%", p.getGameBalance() + "")
                    .replace("%kills%", p.getStatistics(RSWPlayer.PlayerStatistics.GAME_KILLS) + ""));
        }
        p.saveData();
    }

    public void checkWin() {
        if (getAliveTeams() == 1 && this.state != Enum.GameState.FINISHING) {
            this.state = Enum.GameState.FINISHING;
            Team winTeam = getPlayers().get(0).getTeam();

            bossBar.setTitle(LanguageManager.getString(Enum.TSsingle.BOSSBAR_ARENA_END));
            bossBar.setProgress(0);
            bossBar.setColor(BarColor.BLUE);

            PlayerManager.getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(LanguageManager.getString(gamePlayer, Enum.TS.WINNER_BROADCAST, true).replace("%winner%", winTeam.getNames()).replace("%map%", this.name)));

            Countdown timer = new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"),
                    () -> {
                        this.timer.killTask();
                        this.cancelTask("countTime");

                        for (RSWPlayer p : winTeam.getMembers()) {
                            if (p.getPlayer() != null) {
                                p.addStatistic(Enum.Statistic.TEAM_WIN, 1);
                                p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                            }
                            sendLog(p);
                        }

                        for (RSWPlayer g : this.inRoom) {
                            if (g.getPlayer() != null) {
                                g.sendMessage(LanguageManager.getString(g, Enum.TS.MATCH_END, true).replace("%time%", "" + Config.file().getInt("Config.Time-EndGame")));
                                g.getPlayer().sendTitle("",
                                        Text.color(LanguageManager.getString(g, Enum.TS.TITLE_WIN, true)
                                                .replace("%player%", winTeam.getNames())),
                                        10, 40, 10);
                            }
                        }
                    }, () -> {
                this.bossBar.removeAll();
                winTeam.getMembers().forEach(this::sendLog);
                kickPlayers(null);
                resetArena();
            }, (t) -> {
                // if (Players.get(0).p != null) {
                //     firework(Players.get(0));
                // }
                double div = (double) t.getSecondsLeft()
                        / (double) Config.file().getInt("Config.Time-EndGame");
                bossBar.setProgress(div);
            });

            timer.scheduleTimer();
        }
    }

    public int getAliveTeams() {
        int al = 0;
        for (Team t : this.teams) {
            if (!t.isEliminated() && t.getMemberCount() > 0) {
                al++;
            }
        }
        return al;
    }

    public Enum.GameType getMode() {
        return Enum.GameType.TEAMS;
    }
}
