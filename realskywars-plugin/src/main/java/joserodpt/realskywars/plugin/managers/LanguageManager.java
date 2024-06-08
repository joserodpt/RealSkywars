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

import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.Text;

import java.util.ArrayList;
import java.util.List;

public class LanguageManager extends LanguageManagerAPI {

    private final ArrayList<String> langList = new ArrayList<>();

    @Override
    public void loadLanguages() {
        this.langList.clear();
        this.langList.addAll(RSWLanguagesConfig.file().getSection("Languages").getRoutesAsStrings(false));
    }

    @Override
    public String getDefaultLanguage() {
        return this.langList.contains(RSWConfig.file().getString("Config.Default-Language")) ? RSWConfig.file().getString("Config.Default-Language") : langList.get(0);
    }

    @Override
    public boolean areLanguagesEmpty() {
        return langList.isEmpty();
    }

    @Override
    public List<String> getLanguages() {
        return langList;
    }

    @Override
    public String getPrefix() {
        return Text.color(RSWLanguagesConfig.file().getString("Strings.Prefix"));
    }

    @Override
    public List<String> getList(RSWPlayer p, TL tl) {
        String lang = p.getLanguage();
        List<String> trad = new ArrayList<>();

        switch (tl) {
            case ARENA_START:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.Start"));
                break;
            case ARENA_END:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.End-Log"));
                break;
            case INITSETUP_ARENA:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.Arena.Init-Setup"));
                break;
            case SCOREBOARD_LOBBY_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Lobby.Lines"));
                break;
            case SCOREBOARD_CAGE_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Cage.Lines"));
                break;
            case SCOREBOARD_PLAYING_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Game.Lines"));
                break;
            case SCOREBOARD_SPECTATOR_LINES:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Scoreboards.Spectate.Lines"));
                break;
            case ITEMS_MAP_DESCRIPTION:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Itens.Map.Description"));
                break;
            case TITLE_ROOMJOIN:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.Titles.Join-Room"));
                break;
            case STATS_ITEM_LORE:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Itens.Statistics.Lore"));
                break;
            case REFILL_EVENT_TITLE:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.Events.Refill"));
                break;
            case TNTRAIN_EVENT_TITLE:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.Arena.Events.TNTRain"));
                break;
            case TAB_HEADER_MATCH:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.InGame.Header"));
                break;
            case TAB_FOOTER_MATCH:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.InGame.Footer"));
                break;
            case TAB_HEADER_OTHER:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.Other.Header"));
                break;
            case TAB_FOOTER_OTHER:
                trad = Text.color(RSWLanguagesConfig.file().getList("Languages." + lang + ".Messages.System.TAB.Other.Footer"));
                break;
            default:
                trad.add("List not found (" + tl.name() + ")");
        }

        return trad;
    }

}
