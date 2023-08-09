package josegamerpt.realskywars.kits;

/*
 *  _____            _  _____ _
 * |  __ \          | |/ ____| |
 * | |__) |___  __ _| | (___ | | ___   ___      ____ _ _ __ ___
 * |  _  // _ \/ _` | |\___ \| |/ / | | \ \ /\ / / _` | '__/ __|
 * | | \ \  __/ (_| | |____) |   <| |_| |\ V  V / (_| | |  \__ \
 * |_|  \_\___|\__,_|_|_____/|_|\_\\__, | \_/\_/ \__,_|_|  |___/
 *                                 __/ |
 *                                |___/
 *
 * Licensed under the MIT License
 * @author José Rodrigues
 * @link https://github.com/joserodpt/RealSkywars
 * Wiki Reference: https://www.spigotmc.org/wiki/itemstack-serialization/
 */

import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ItemStackSpringer;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class KitInventory {

    private ItemStack[] inventory;

    public KitInventory(ItemStack[] inventory) {
        this.inventory = inventory;
    }

    public boolean hasItems() {
        return !this.getListInventory().isEmpty();
    }

    public void giveToPlayer(RSWPlayer p) {
        if (this.getInventory() != null) {
            p.getInventory().clear();
            p.getInventory().setContents(this.getInventory());
        }
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public List<ItemStack> getListInventory() {
        return this.getInventory() == null ? Collections.emptyList() : Arrays.stream(this.getInventory().clone())
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getSerialized() {
        return ItemStackSpringer.getItemsSerialized(this.getInventory().clone());
    }

    @Override
    public String toString() {
        return "KitInventory{" +
                "inventory=" + Arrays.toString(inventory) +
                '}';
    }
}
