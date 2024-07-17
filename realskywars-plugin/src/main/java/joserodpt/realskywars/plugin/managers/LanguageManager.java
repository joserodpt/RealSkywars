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

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWLanguage;
import joserodpt.realskywars.api.config.RSWLanguagesConfig;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class LanguageManager extends LanguageManagerAPI {

    final RealSkywarsAPI rsa;

    public LanguageManager(RealSkywarsAPI rsa) {
        this.rsa = rsa;
    }

    @Override
    public void loadLanguages() {
        this.getLanguages().clear();

        for (String language : RSWLanguagesConfig.file().getSection("Languages").getRoutesAsStrings(false)) {
            String displayName = RSWLanguagesConfig.file().getString("Languages." + language + ".Language-Specific.Displayname");
            String translationKey = RSWLanguagesConfig.file().getString("Languages." + language + ".Language-Specific.Translation-Key");
            this.langList.put(language, new RSWLanguage(language, displayName, translationKey, Itens.renameItem(ItemStackSpringer.getItemDeSerialized(sectionToMap("Languages." + language + ".Language-Specific.Icon")), "&e&l" + displayName, language)));
        }

        String simpleVersion = rsa.getSimpleServerVersion();
        File folder = new File(rsa.getPlugin().getDataFolder(), "translations");
        File translationVersionFile = new File(folder, "version.yml");

        // Check if the language file exists
        if (translationVersionFile.exists()) {
            try {
                String version = new String(Files.readAllBytes(translationVersionFile.toPath()));
                if (!version.equals(simpleVersion)) {
                    rsa.getLogger().info("Updating language files from " + version + " to " + simpleVersion + ". Downloading new versions...");
                    getLanguages().forEach(RSWLanguage::downloadLanguageFile);
                    Files.write(translationVersionFile.toPath(), simpleVersion.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Files.createDirectories(folder.toPath());
                Files.write(translationVersionFile.toPath(), simpleVersion.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Map<String, Object> sectionToMap(String section) {
        Map<String, Object> newMap = new HashMap<>();
        RSWLanguagesConfig.file().getSection(section).getRoutesAsStrings(false).forEach(route -> {
            newMap.put(route, RSWLanguagesConfig.file().get(section + "." + route));
        });

        return newMap;
    }

    @Override
    public String getDefaultLanguage() {
        return this.langList.get(RSWConfig.file().getString("Config.Default-Language")) != null ? RSWConfig.file().getString("Config.Default-Language") : this.langList.keySet().stream().findFirst().get();
    }

    public RSWLanguage getDefaultLanguageObject() {
        return this.langList.get(getDefaultLanguage());
    }

    @Override
    public boolean areLanguagesEmpty() {
        return getLanguages().isEmpty();
    }

    @Override
    public Collection<RSWLanguage> getLanguages() {
        return this.langList.values();
    }

    @Override
    public Map<String, RSWLanguage> getLanguagesMap() {
        return this.langList;
    }

    @Override
    public String getPrefix() {
        return Text.color(RSWLanguagesConfig.file().getString("Strings.Prefix"));
    }

    @Override
    public String getMaterialName(RSWPlayer p, Material mat) {
        return this.langList.containsKey(p.getLanguage()) ? this.langList.get(p.getLanguage()).getMaterialName(mat) : getDefaultLanguageObject().getMaterialName(mat);
    }

    @Override
    public String getMaterialName(Material mat) {
        return getDefaultLanguageObject().getMaterialName(mat);
    }

    @Override
    public String getEnchantmentName(RSWPlayer p, Enchantment ench) {
        return this.langList.containsKey(p.getLanguage()) ? this.langList.get(p.getLanguage()).getEnchantmentName(ench) : getDefaultLanguageObject().getEnchantmentName(ench);
    }

    @Override
    public String getEntityName(RSWPlayer p, EntityType type) {
        return this.langList.containsKey(p.getLanguage()) ? this.langList.get(p.getLanguage()).getEntityName(type) : getDefaultLanguageObject().getEntityName(type);
    }

    @Override
    public RSWLanguage getLanguage(String language) {
        return this.langList.get(language);
    }

}
