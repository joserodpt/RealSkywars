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
import joserodpt.realskywars.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.block.data.type.WallSign;

public class SWSign {

    private final SWGame game;
    private final Block b;

    public SWSign(SWGame gm, Block b) {
        this.game = gm;
        this.b = b;

        this.update();
    }

    public void update() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
            if (b.getType().name().contains("SIGN")) {
                Sign s = (Sign) b.getState();

                s.setLine(0, RealSkywars.getPlugin().getLanguageManager().getPrefix());
                s.setLine(1, Text.color("&b" + game.getName()));
                s.setLine(2, Text.color("&f" + game.getGameMode().name() + " | &f" + game.getPlayerCount() + "&7/&f" + game.getMaxPlayers()));
                s.setLine(3, Text.color("&b&l" + game.getState().name()));
                s.update();

                this.b.getWorld().getBlockAt(this.getBehindBlock().getLocation()).setType(selectGlass());
            }
        });
    }

    private Material selectGlass() {
        switch (this.game.getState()) {
            case WAITING:
                return Material.LIGHT_BLUE_CONCRETE;
            case AVAILABLE:
                return Material.GREEN_CONCRETE;
            case STARTING:
                return Material.YELLOW_CONCRETE;
            case PLAYING:
                return Material.RED_CONCRETE;
            case FINISHING:
                return Material.PURPLE_CONCRETE;
            case RESETTING:
                return Material.BLACK_CONCRETE;
            default:
                return Material.DIRT;
        }
    }

    private Block getGlass(Block b) {
        WallSign signData = (WallSign) b.getState().getBlockData();
        BlockFace attached = signData.getFacing().getOppositeFace();

        return b.getRelative(attached);
    }

    public Block getBlock() {
        return this.b;
    }

    public Block getBehindBlock() {
        return this.getGlass(this.b);
    }

    public Location getLocation() {
            return this.b.getLocation();
        }

    public String getLocationSerialized() {
        return this.getLocation().getWorld().getName() + "<" +
                this.getLocation().getBlockX() + "<" +
                this.getLocation().getBlockY() + "<" +
                this.getLocation().getBlockZ();
    }

    public void delete() {
        Bukkit.getScheduler().scheduleSyncDelayedTask(RealSkywars.getPlugin(), () -> {
            if (this.b.getType().name().contains("SIGN")) {
                this.b.setType(Material.AIR);
            }
        });
    }
}