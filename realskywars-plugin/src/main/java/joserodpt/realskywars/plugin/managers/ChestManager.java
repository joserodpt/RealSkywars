package joserodpt.realskywars.plugin.managers;

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
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.chests.RSWChest;
import joserodpt.realskywars.api.chests.RSWChestItem;
import joserodpt.realskywars.api.config.chests.BasicChestConfig;
import joserodpt.realskywars.api.config.chests.EPICChestConfig;
import joserodpt.realskywars.api.config.chests.NormalChestConfig;
import joserodpt.realskywars.api.managers.ChestManagerAPI;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ChestManager extends ChestManagerAPI {

    private final RealSkywarsAPI rsa;

    public ChestManager(RealSkywarsAPI rsa) {
        this.rsa = rsa;
    }

    @Override
    public void set2ChestRaw(RSWChest.Tier t, RSWChest.Type type, List<RSWChestItem> itens) {
        List<Map<String, Object>> map = itens.stream().map(swChestItem -> ImmutableMap.of("Chance", swChestItem.getChance(), "Item", ItemStackSpringer.getItemSerialized(swChestItem.getItemStack()))).collect(Collectors.toList());

        switch (t) {
            case BASIC:
                BasicChestConfig.file().set((type == RSWChest.Type.MID) ? "Mid-Items" : "Items", map);
                BasicChestConfig.save();
                break;
            case NORMAL:
                NormalChestConfig.file().set((type == RSWChest.Type.MID) ? "Mid-Items" : "Items", map);
                NormalChestConfig.save();
                break;
            case EPIC:
                EPICChestConfig.file().set((type == RSWChest.Type.MID) ? "Mid-Items" : "Items", map);
                EPICChestConfig.save();
                break;
        }
    }

    @Override
    public void set2Chest(RSWChest.Tier t, RSWChest.Type type, List<ItemStack> itens) {
        List<Map<String, Object>> map = itens.stream().map(itemStack -> ImmutableMap.of("Chance", 50, "Item", ItemStackSpringer.getItemSerialized(itemStack))).collect(Collectors.toList());

        switch (t) {
            case BASIC:
                BasicChestConfig.file().set((type == RSWChest.Type.MID) ? "Mid-Items" : "Items", map);
                BasicChestConfig.save();
                break;
            case NORMAL:
                NormalChestConfig.file().set((type == RSWChest.Type.MID) ? "Mid-Items" : "Items", map);
                NormalChestConfig.save();
                break;
            case EPIC:
                EPICChestConfig.file().set((type == RSWChest.Type.MID) ? "Mid-Items" : "Items", map);
                EPICChestConfig.save();
                break;
        }
    }

    @Override
    public List<RSWChestItem> getChest(RSWChest.Tier t, RSWChest.Type type) {
        String header = (type == RSWChest.Type.MID) ? "Mid-Items" : "Items";

        switch (t) {
            case BASIC:
                return BasicChestConfig.file().getMapList(header).stream()
                        .map(this::createSWChestItemFromMap)
                        .collect(Collectors.toList());
            case NORMAL:
                return NormalChestConfig.file().getMapList(header).stream()
                        .map(this::createSWChestItemFromMap)
                        .collect(Collectors.toList());

            case EPIC:
                return EPICChestConfig.file().getMapList(header).stream()
                        .map(this::createSWChestItemFromMap)
                        .collect(Collectors.toList());

            default:
                rsa.getLogger().severe("There is no tier registered with the name: " + t.name());
                return Collections.emptyList();
        }
    }

    @Override
    protected RSWChestItem createSWChestItemFromMap(Map<?, ?> map) {
        ItemStack itemStack = ItemStackSpringer.getItemDeSerialized(ImmutableMap.copyOf((Map<String, Object>) map.get("Item")));
        Integer chance = (Integer) map.get("Chance");
        return new RSWChestItem(itemStack, chance);
    }

    @Override
    public int getMaxItems(RSWChest.Tier chestTier) {
        String itens = "Max-Itens-Per-Chest";
        switch (chestTier) {
            case BASIC:
                return BasicChestConfig.file().getInt(itens);
            case NORMAL:
                return NormalChestConfig.file().getInt(itens);
            case EPIC:
                return EPICChestConfig.file().getInt(itens);
        }
        return 0;
    }

}
