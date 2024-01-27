package joserodpt.realskywars.api.managers;

import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.chests.RSWChestItem;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;

public abstract class ChestManagerAPI {
    public abstract void set2ChestRaw(RSWChest.Tier t, RSWChest.Type type, List<RSWChestItem> itens);

    public abstract void set2Chest(RSWChest.Tier t, RSWChest.Type type, List<ItemStack> itens);

    public abstract List<RSWChestItem> getChest(RSWChest.Tier t, RSWChest.Type type);

    protected abstract RSWChestItem createSWChestItemFromMap(Map<?, ?> map);

    public abstract int getMaxItems(RSWChest.Tier chestTier);
}
