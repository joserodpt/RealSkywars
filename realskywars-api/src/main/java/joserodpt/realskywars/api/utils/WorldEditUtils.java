package joserodpt.realskywars.api.utils;

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

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldedit.world.block.BaseBlock;
import com.sk89q.worldedit.world.block.BlockState;
import com.sk89q.worldedit.world.block.BlockType;
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import org.bukkit.Location;

import java.io.File;
import java.nio.file.Files;

public class WorldEditUtils {

    public static boolean schemFileExists(String name) {
        File folder = new File(RealSkywarsAPI.getInstance().getPlugin().getDataFolder(), "maps");
        File file = new File(folder, name);
        return file.exists();
    }

    public static void setBlocks(Location loc1, Location loc2, BlockType type) {
        Debugger.print(WorldEditUtils.class, "Setting blocks!");

        World world = BukkitAdapter.adapt(loc1.getWorld());
        EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

        BlockVector3 bv1 = BlockVector3.at(loc1.getBlockX(), loc1.getBlockY(), loc1.getBlockZ());
        BlockVector3 bv2 = BlockVector3.at(loc2.getBlockX(), loc2.getBlockY(), loc2.getBlockZ());

        CuboidRegion rg = new CuboidRegion(world, bv1, bv2);

        BlockState b = type.getDefaultState();
        BaseBlock block = b.toBaseBlock();

        try {
            editSession.setBlocks(rg, block);
            editSession.close();

            Debugger.print(WorldEditUtils.class, "All done!");
        } catch (WorldEditException e) {
            RealSkywarsAPI.getInstance().getLogger().severe("Error while setting blocks!");
            RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
        }
    }

    public static void pasteSchematic(String name, Location location) {
        Debugger.print(WorldEditUtils.class, "Pasting schematic named " + name);
        File folder = new File(RealSkywarsAPI.getInstance().getPlugin().getDataFolder(), "maps");
        File file = new File(folder, name);

        ClipboardFormat clipboardFormat = ClipboardFormats.findByFile(file);
        Clipboard clipboard;

        BlockVector3 blockVector3 = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());

        if (clipboardFormat != null) {
            try (ClipboardReader clipboardReader = clipboardFormat.getReader(Files.newInputStream(file.toPath()))) {

                if (location.getWorld() == null)
                    throw new NullPointerException("Failed to paste schematic due to world being null");

                World world = BukkitAdapter.adapt(location.getWorld());

                EditSession editSession = WorldEdit.getInstance().newEditSessionBuilder().world(world).build();

                clipboard = clipboardReader.read();

                Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(blockVector3).ignoreAirBlocks(true).build();

                try {
                    Operations.complete(operation);
                    editSession.close();

                    Debugger.print(WorldEditUtils.class, "Pasted!");

                    location.getWorld().getPlayers().forEach(player -> player.sendMessage("[RealSkywars] Schematic pasted with success!"));
                } catch (Exception e) {
                    RealSkywarsAPI.getInstance().getLogger().severe("Error while pasting schematic!");
                    RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
                }
            } catch (Exception e) {
                RealSkywarsAPI.getInstance().getLogger().severe("Error while pasting schematic!");
                RealSkywarsAPI.getInstance().getLogger().severe(e.getMessage());
            }
        }
    }
}
