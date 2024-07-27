package joserodpt.realskywars.api.managers.holograms.support;

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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import com.gmail.filoghost.holographicdisplays.api.Hologram;
import com.gmail.filoghost.holographicdisplays.api.HologramsAPI;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.managers.holograms.RSWHologram;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class HDHologram implements RSWHologram {

    private Hologram holo;

    @Override
    public void spawnHologram(Location loc) {
        if (this.holo == null || this.holo.isDeleted()) {
            this.holo = HologramsAPI.createHologram(RealSkywarsAPI.getInstance().getPlugin(), loc.add(0.5, 2, 0.5));
            this.holo.clearLines();
            this.holo.appendItemLine(new ItemStack(Material.CLOCK));
        }
    }

    @Override
    public void setTime(int time) {
        if (this.holo == null || this.holo.isDeleted()) {
            this.holo.insertTextLine(1, Text.formatSeconds(time));
        }
    }

    @Override
    public void deleteHologram() {
        if (this.holo != null && !this.holo.isDeleted()) {
            this.holo.delete();
        }
        this.holo = null;
    }

    @Override
    public HType getType() {
        return HType.HOLOGRAPHIC_DISPLAYS;
    }
}
