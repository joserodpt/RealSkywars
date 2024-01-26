package joserodpt.realskywars.chests;

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

import com.google.common.collect.ImmutableMap;
import joserodpt.realskywars.RealSkywars;
import joserodpt.realskywars.config.chests.BasicChest;
import joserodpt.realskywars.config.chests.EPICChest;
import joserodpt.realskywars.config.chests.NormalChest;
import joserodpt.realskywars.utils.ItemStackSpringer;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChestManager {

    public void set2ChestRaw(SWChest.Tier t, SWChest.Type type, List<SWChestItem> itens) {
        List<Map<String, Object>> map = itens.stream().map(swChestItem -> ImmutableMap.of("Chance", swChestItem.getChance(), "Item", ItemStackSpringer.getItemSerialized(swChestItem.getItemStack()))).collect(Collectors.toList());

        switch (t) {
            case BASIC:
                BasicChest.file().set((type == SWChest.Type.MID) ? "Mid-Items" : "Items", map);
                BasicChest.save();
                break;
            case NORMAL:
                NormalChest.file().set((type == SWChest.Type.MID) ? "Mid-Items" : "Items", map);
                NormalChest.save();
                break;
            case EPIC:
                EPICChest.file().set((type == SWChest.Type.MID) ? "Mid-Items" : "Items", map);
                EPICChest.save();
                break;
        }
    }

    public void set2Chest(SWChest.Tier t, SWChest.Type type, List<ItemStack> itens) {
        List<Map<String, Object>> map = itens.stream().map(itemStack -> ImmutableMap.of("Chance", 50, "Item", ItemStackSpringer.getItemSerialized(itemStack))).collect(Collectors.toList());

        switch (t) {
            case BASIC:
                BasicChest.file().set((type == SWChest.Type.MID) ? "Mid-Items" : "Items", map);
                BasicChest.save();
                break;
            case NORMAL:
                NormalChest.file().set((type == SWChest.Type.MID) ? "Mid-Items" : "Items", map);
                NormalChest.save();
                break;
            case EPIC:
                EPICChest.file().set((type == SWChest.Type.MID) ? "Mid-Items" : "Items", map);
                EPICChest.save();
                break;
        }
    }

    public List<SWChestItem> getChest(SWChest.Tier t, SWChest.Type type) {
        String header = (type == SWChest.Type.MID) ? "Mid-Items" : "Items";

        switch (t) {
            case BASIC:
                return BasicChest.file().getMapList(header).stream()
                        .map(this::createSWChestItemFromMap)
                        .collect(Collectors.toList());
            case NORMAL:
                return NormalChest.file().getMapList(header).stream()
                        .map(this::createSWChestItemFromMap)
                        .collect(Collectors.toList());

            case EPIC:
                return EPICChest.file().getMapList(header).stream()
                        .map(this::createSWChestItemFromMap)
                        .collect(Collectors.toList());

            default:
                RealSkywars.getPlugin().getLogger().severe("There is no tier Registered like: " + t.name());
                return Collections.emptyList();
        }
    }

    private SWChestItem createSWChestItemFromMap(Map<?, ?> map) {
        ItemStack itemStack = ItemStackSpringer.getItemDeSerialized(ImmutableMap.copyOf((Map<String, Object>) map.get("Item")));
        Integer chance = (Integer) map.get("Chance");
        return new SWChestItem(itemStack, chance);
    }

    public int getMaxItems(SWChest.Tier chestTier) {
        String itens = "Max-Itens-Per-Chest";
        switch (chestTier) {
            case BASIC:
                return BasicChest.file().getInt(itens);
            case NORMAL:
                return NormalChest.file().getInt(itens);
            case EPIC:
                return EPICChest.file().getInt(itens);
        }
        return 0;
    }

}
