package josegamerpt.realskywars.kits;

import josegamerpt.realskywars.player.RSWPlayer;
import josegamerpt.realskywars.utils.ItemStackSpringer;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
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
        p.getInventory().clear();
        p.getInventory().setContents(this.getInventory());
    }

    public ItemStack[] getInventory() {
        return this.inventory;
    }

    public List<ItemStack> getListInventory() {
        return Arrays.stream(this.getInventory().clone())
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
