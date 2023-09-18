package joserodpt.realskywars.game.modes;

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

import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.cages.Cage;
import joserodpt.realskywars.chests.SWChest;
import joserodpt.realskywars.configuration.Config;
import joserodpt.realskywars.game.Countdown;
import joserodpt.realskywars.game.modes.teams.Team;
import joserodpt.realskywars.managers.LanguageManager;
import joserodpt.realskywars.player.PlayerManager;
import joserodpt.realskywars.player.RSWPlayer;
import joserodpt.realskywars.utils.FireworkUtils;
import joserodpt.realskywars.utils.Text;
import joserodpt.realskywars.world.SWWorld;
import org.apache.commons.lang.WordUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class Solo extends SWGame {

    private final List<Cage> cages;

    public Solo(String nome, World w, String schematicName, SWWorld.WorldType wt, SWGame.GameState estado, List<Cage> cages, int maxPlayers, Location spectatorLocation, Boolean specEnabled, Boolean instantEnding, Boolean border, Location pos1, Location pos2, List<SWChest> chests, Boolean rankd, RealSkywars rs) {
        super(nome, w, schematicName, wt, estado, maxPlayers, spectatorLocation, specEnabled, instantEnding, border,  pos1, pos2, chests, rankd, rs);
        this.cages = cages;
    }

    @Override
    public boolean isPlaceHolder() {
        return false;
    }

    @Override
    public void startGameFunction() {
        if (canStartGame()) {
            super.cancelGameStart();
        } else {
            this.setState(GameState.PLAYING);

            super.getStartRoomTimer().killTask();

            super.calculateVotes();

            for (RSWPlayer p : this.getPlayers()) {
                if (p.getPlayer() != null) {
                    p.setBarNumber(0);
                    p.getInventory().clear();

                    super.getBossBar().addPlayer(p.getPlayer());

                    //start msg
                    for (String s : Text.color(super.getRealSkywars().getLanguageManager().getList(p, LanguageManager.TL.ARENA_START))) {
                        p.sendCenterMessage(s.replace("%chests%", WordUtils.capitalizeFully(super.getChestTier().name())).replace("%kit%", p.getKit().getDisplayName()).replace("%project%", WordUtils.capitalizeFully(super.getProjectileTier().name().replace("_", " "))).replace("%time%", WordUtils.capitalizeFully(super.getTimeType().name())));
                    }

                    p.getKit().give(p);

                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.PLAYING);
                    p.getCage().open();
                }
            }

            super.startTimers();
        }
    }

    @Override
    public boolean canStartGame() {
        return super.getPlayerCount() < Config.file().getInt("Config.Min-Players-ToStart");
    }

    @Override
    public void removePlayer(RSWPlayer p) {
        if (p.hasCage()) {
            p.getCage().removePlayer(p);
        }

        super.commonRemovePlayer(p);
    }

    @Override
    public void addPlayer(RSWPlayer p) {
        if (super.getRealSkywars().getPartyManager().checkForParties(p, this)) {
            switch (this.getState()) {
                case RESETTING:
                    p.sendMessage(super.getRealSkywars().getLanguageManager().getString(p, LanguageManager.TS.CANT_JOIN, true));
                    break;
                case FINISHING:
                case PLAYING:
                    if (this.isSpectatorEnabled()) {
                        spectate(p, SpectateType.EXTERNAL, null);
                    } else {
                        p.sendMessage(super.getRealSkywars().getLanguageManager().getString(p, LanguageManager.TS.SPECTATING_DISABLED, true));
                    }
                    break;
                default:
                    if (this.getPlayerCount() == this.getMaxPlayers()) {
                        p.sendMessage(super.getRealSkywars().getLanguageManager().getString(p, LanguageManager.TS.ROOM_FULL, true));
                        return;
                    }

                    //cage

                    for (Cage c : this.cages) {
                        if (c.isEmpty() && p.getPlayer() != null) {
                            c.addPlayer(p);
                            break;
                        }
                    }

                    p.setRoom(this);
                    p.setProperty(RSWPlayer.PlayerProperties.STATE, RSWPlayer.PlayerState.CAGE);

                    super.getAllPlayers().add(p);

                    if (p.getPlayer() != null) {
                        super.getBossBar().addPlayer(p.getPlayer());
                        p.heal();
                        List<String> up = super.getRealSkywars().getLanguageManager().getList(p, LanguageManager.TL.TITLE_ROOMJOIN);
                        p.getPlayer().sendTitle(up.get(0), up.get(1), 10, 120, 10);
                    }

                    for (RSWPlayer ws : this.getAllPlayers()) {
                        ws.sendMessage(super.getRealSkywars().getLanguageManager().getString(ws, LanguageManager.TS.PLAYER_JOIN_ARENA, true).replace("%player%", p.getDisplayName()).replace("%players%", getPlayerCount() + "").replace("%maxplayers%", getMaxPlayers() + ""));
                    }

                    super.getRealSkywars().getPlayerManager().giveItems(p.getPlayer(), PlayerManager.Items.CAGE);

                    //update tab
                    if (!p.isBot()) {
                        for (RSWPlayer player : this.getPlayers()) {
                            if (!player.isBot()) {
                                RSWPlayer.RoomTAB rt = player.getTab();
                                List<Player> players = this.getPlayers().stream().map(RSWPlayer::getPlayer).collect(Collectors.toList());
                                rt.clear();
                                rt.add(players);
                                rt.updateRoomTAB();
                            }
                        }
                    }

                    if (getPlayerCount() == Config.file().getInt("Config.Min-Players-ToStart")) {
                        startRoom();
                    }
                    break;
            }

            //call api
            super.getRealSkywars().getEventsAPI().callRoomStateChange(this);

            //signal that is ranked
            if (super.isRanked()) p.sendActionbar("&b&lRANKED");
        }
    }

    @Override
    public void resetArena(OperationReason rr) {
        super.commonResetArena(rr);
    }

    @Override
    public void checkWin() {
        if (this.getPlayerCount() == 1 && super.getState() != SWGame.GameState.FINISHING) {
            this.setState(GameState.FINISHING);

            RSWPlayer p = getPlayers().get(0);
            p.setInvincible(true);

            super.getStartTimer().killTask();
            super.getTimeCounterTask().cancel();

            super.getBossBar().setTitle(super.getRealSkywars().getLanguageManager().getString(LanguageManager.TSsingle.BOSSBAR_ARENA_END));
            super.getBossBar().setProgress(0);
            super.getBossBar().setColor(BarColor.BLUE);

            super.getRealSkywars().getPlayerManager().getPlayers().forEach(gamePlayer -> gamePlayer.sendMessage(super.getRealSkywars().getLanguageManager().getString(gamePlayer, LanguageManager.TS.WINNER_BROADCAST, true).replace("%winner%", p.getDisplayName()).replace("%map%", super.getName())));

            if (this.isInstantEndEnabled()) {
                super.getBossBar().removeAll();
                this.sendLog(p, true);
                this.kickPlayers(null);
                this.resetArena(OperationReason.RESET);
            } else {
                super.setWinTimer(new Countdown(RealSkywars.getPlugin(RealSkywars.class), Config.file().getInt("Config.Time-EndGame"), () -> {
                    if (p.getPlayer() != null) {
                        p.setInvincible(true);
                        p.addStatistic(RSWPlayer.Statistic.SOLO_WIN, 1, this.isRanked());
                        p.executeWinBlock(Config.file().getInt("Config.Time-EndGame") - 2);
                    }

                    for (RSWPlayer g : super.getAllPlayers()) {
                        g.delCage();
                        g.sendMessage(super.getRealSkywars().getLanguageManager().getString(p, LanguageManager.TS.MATCH_END, true).replace("%time%", Text.formatSeconds(Config.file().getInt("Config.Time-EndGame"))));
                    }
                }, () -> {
                    super.getBossBar().removeAll();
                    this.sendLog(p, true);
                    this.kickPlayers(null);
                    this.resetArena(OperationReason.RESET);
                }, (t) -> {
                    double div = (double) t.getSecondsLeft() / (double) Config.file().getInt("Config.Time-EndGame");
                    if (div <= 1 && div >= 0) {
                        super.getBossBar().setProgress(div);
                    }

                    super.getAllPlayers().forEach(rswPlayer -> rswPlayer.setBarNumber(t.getSecondsLeft(), Config.file().getInt("Config.Time-EndGame")));

                    if (p.getPlayer() != null) {
                        FireworkUtils.spawnRandomFirework(p.getLocation());
                    }
                }));

                super.getWinTimer().scheduleTimer();
            }

            super.getChests().forEach(SWChest::cancelTasks);
            super.getChests().forEach(SWChest::clearHologram);
        }
    }

    @Override
    public Mode getGameMode() {
        return Mode.SOLO;
    }

    @Override
    public List<Cage> getCages() {
        return this.cages;
    }

    @Override
    public List<Team> getTeams() {
        return null;
    }

    @Override
    public int maxMembersTeam() {
        return 0;
    }

    @Override
    public int getMaxTime() {
        return Config.file().getInt("Config.Maximum-Game-Time.Solo");
    }

    @Override
    public int minimumPlayersToStartGame() {
        return Config.file().getInt("Config.Min-Players-ToStart");
    }
}