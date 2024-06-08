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
import joserodpt.realskywars.api.utils.Text;

import java.util.ArrayList;
import java.util.List;

public class LanguageManager extends LanguageManagerAPI {

    private final List<String> langList = new ArrayList<>();

    @Override
    public void loadLanguages() {
        this.getLanguages().clear();
        this.getLanguages().addAll(RSWLanguagesConfig.file().getSection("Languages").getRoutesAsStrings(false));
    }

    @Override
    public String getDefaultLanguage() {
        return this.getLanguages().contains(RSWConfig.file().getString("Config.Default-Language")) ? RSWConfig.file().getString("Config.Default-Language") : langList.get(0);
    }

    @Override
    public boolean areLanguagesEmpty() {
        return getLanguages().isEmpty();
    }

    @Override
    public List<String> getLanguages() {
        return this.langList;
    }

    @Override
    public String getPrefix() {
        return Text.color(RSWLanguagesConfig.file().getString("Strings.Prefix"));
    }

}
