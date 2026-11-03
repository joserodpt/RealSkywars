package joserodpt.realskywars.plugin.gui.bedrock;

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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.TranslatableLine;
import joserodpt.realskywars.api.kits.RSWKit;
import joserodpt.realskywars.api.map.RSWMap;
import joserodpt.realskywars.api.map.modes.PlaceholderMode;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.shop.RSWBuyableItem;
import joserodpt.realskywars.api.utils.bedrock.BedrockForms;
import joserodpt.realskywars.plugin.gui.guis.MapsListGUI;
import joserodpt.realskywars.plugin.gui.guis.ShopGUI;
import joserodpt.realskywars.plugin.gui.guis.VoteGUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.DISPLAYNAME;
import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.MAP;
import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.MODE;
import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.NAME;
import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.PRICE;
import static joserodpt.realskywars.api.config.TranslatableLine.TranslatableLinePlaceholder.TYPE;

/**
 * The Bedrock form versions of the cage menus and the maps list. Each one returns false if the form
 * could not be sent, so the caller opens the inventory menu instead. The rules they apply - votes,
 * kit purchases, map filters - live in the inventory menus and are shared, not copied.
 */
public final class BedrockMenus {

    private BedrockMenus() {
    }

    // ---------------------------------------------------------------- kits

    //the kits this player owns, as PlayerItemsGUI lists them
    public static boolean openKits(RSWPlayer p) {
        RSWBuyableItem.ItemCategory cat = RSWBuyableItem.ItemCategory.KIT;

        List<RSWBuyableItem> kits = new ArrayList<>(RealSkywarsAPI.getInstance().getShopManagerAPI().getBoughtItems(cat, p));
        kits.removeIf(RSWBuyableItem::isDummy);

        List<String> buttons = new ArrayList<>();
        for (RSWBuyableItem kit : kits) {
            buttons.add("&f" + kit.getDisplayName());
        }

        return BedrockForms.send(p, cat.getCategoryTitle(p), "", buttons, id -> {
            if (id < 0 || id >= kits.size() || p.getState() != RSWPlayer.PlayerState.CAGE) {
                return;
            }

            RSWBuyableItem clicked = kits.get(id);
            RSWKit kit = RealSkywarsAPI.getInstance().getKitManagerAPI().getKit(clicked.getName());
            if (kit == null) {
                return;
            }

            p.setKit(kit);
            p.sendMessage(TranslatableLine.PROFILE_SELECTED.with(NAME, clicked.getDisplayName()).with(TYPE, cat.getCategoryTitle(p)).get(p, true));
        });
    }

    //with Only-Buy-Kits-Per-Match, the kit shop the bow opens instead
    public static boolean openKitShop(RSWPlayer p) {
        RSWBuyableItem.ItemCategory cat = RSWBuyableItem.ItemCategory.KIT;

        List<RSWBuyableItem> kits = new ArrayList<>(RealSkywarsAPI.getInstance().getShopManagerAPI().getCategoryContents(cat));
        kits.removeIf(item -> item.isDummy() || !(item instanceof RSWKit));

        List<String> buttons = new ArrayList<>();
        for (RSWBuyableItem kit : kits) {
            String button = "&f" + kit.getDisplayName();
            if (!kit.isBought(p).getKey() && kit.getPrice() != 0) {
                button += "\n" + TranslatableLine.KIT_PRICE.with(PRICE, kit.getPriceFormatted()).get(p);
            }
            buttons.add(button);
        }

        return BedrockForms.send(p, cat.getCategoryTitle(p), "", buttons, id -> {
            if (id < 0 || id >= kits.size() || p.getState() != RSWPlayer.PlayerState.CAGE) {
                return;
            }

            //the same checks as a click in the shop inventory
            RSWKit kit = (RSWKit) kits.get(id);
            if (kit.isBought(p).getKey()) {
                p.sendMessage(TranslatableLine.SHOP_ALREADY_BOUGHT.with(NAME, kit.getDisplayName()).get(p, true));
            } else if (!ShopGUI.canBuy(p, kit)) {
                TranslatableLine.SHOP_NO_PERM.send(p, true);
            } else {
                ShopGUI.buyKitForMatch(p, kit);
            }
        });
    }

    // ---------------------------------------------------------------- vote

    public static boolean openVote(RSWPlayer p) {
        List<RSWMap.VoteType> types = Arrays.asList(RSWMap.VoteType.CHESTS, RSWMap.VoteType.TIME, RSWMap.VoteType.PROJECTILES);
        List<TranslatableLine> titles = Arrays.asList(TranslatableLine.MENU_CHESTS_TITLE, TranslatableLine.MENU_TIME_TITLE, TranslatableLine.MENU_PROJECTILES_TITLE);

        List<String> buttons = new ArrayList<>();
        for (TranslatableLine title : titles) {
            buttons.add(title.get(p));
        }

        return BedrockForms.send(p, TranslatableLine.MENU_VOTE_TITLE.get(p), "", buttons, id -> {
            if (id >= 0 && id < types.size()) {
                openVoteOptions(p, types.get(id), titles.get(id));
            }
        });
    }

    private static void openVoteOptions(RSWPlayer p, RSWMap.VoteType type, TranslatableLine title) {
        //the first form may have stayed open past the vote window
        if (!VoteGUI.canVoteNow(p)) {
            return;
        }

        TranslatableLine[] options = VoteGUI.optionLines(type);
        List<String> buttons = new ArrayList<>();
        for (TranslatableLine option : options) {
            buttons.add(option.get(p));
        }

        BedrockForms.send(p, title.get(p), "", buttons, id -> {
            if (id >= 0 && id < options.length && VoteGUI.canVoteNow(p)) {
                VoteGUI.castVote(p, type, id + 1);
            }
        });
    }

    // ---------------------------------------------------------------- maps

    public static boolean openMaps(RSWPlayer p) {
        List<RSWMap> maps = new ArrayList<>(RealSkywarsAPI.getInstance().getMapManagerAPI().getMapsForPlayer(p));
        maps.removeIf(map -> map instanceof PlaceholderMode);

        //the first button changes the filter, as in the inventory; forms scroll, so there are no pages
        List<String> buttons = new ArrayList<>();
        buttons.add(p.getPlayerMapViewerPref().getDisplayName(p));
        for (RSWMap map : maps) {
            buttons.add(TranslatableLine.ITEM_MAP_NAME.with(MAP, map.getName()).with(DISPLAYNAME, map.getDisplayName()).with(MODE, map.getGameMode().getDisplayName(p)).get(p)
                    + (map.isRanked() ? " &bRANKED" : "")
                    + "\n" + map.getState().getDisplayName(p) + " &8(" + map.getPlayerCount() + "/" + map.getMaxPlayers() + ")");
        }

        String title = TranslatableLine.MENU_MAPS_TITLE.get(p, false) + ": " + p.getPlayerMapViewerPref().getDisplayName(p);
        return BedrockForms.send(p, title, "", buttons, id -> {
            if (id == 0) {
                MapsListGUI.cycleViewerPref(p);
                MapsListGUI.open(p);
            } else if (id > 0 && id <= maps.size()) {
                //the form may have been answered after joining some other way
                if (p.getMatch() != null) {
                    TranslatableLine.CMD_ALREADY_IN_MATCH.send(p, true);
                    return;
                }
                maps.get(id - 1).addPlayer(p);
            }
        });
    }
}
