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
 * @author José Rodrigues © 2019-2025
 * @link https://github.com/joserodpt/RealSkywars
 */

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.block.implementation.Section;
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import joserodpt.realskywars.api.config.RSWLanguage;
import joserodpt.realskywars.api.config.RSWLanguagesOldConfig;
import joserodpt.realskywars.api.managers.LanguageManagerAPI;
import joserodpt.realskywars.api.player.RSWPlayer;
import joserodpt.realskywars.api.utils.ServerVersionUtil;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;

public class LanguageManager extends LanguageManagerAPI {

    private static final String FALLBACK_LANGUAGE = "en_us";
    private static final String[] BUNDLED_LANGUAGES = {"en_us", "pt_pt", "es_es", "fr_fr", "zh_cn", "pl_pl"};

    final RealSkywarsAPI rsa;

    public LanguageManager(RealSkywarsAPI rsa) {
        this.rsa = rsa;
    }

    @Override
    public void loadLanguages() {
        this.getLanguages().clear();

        //check if "languages" folder exists
        File languagesFolder = new File(rsa.getPlugin().getDataFolder(), "languages");
        if (!languagesFolder.exists()) {
            languagesFolder.mkdirs();

            //if folder doesn't exist, the old pre 1.1 language.yml file still exists
            if (RSWLanguagesOldConfig.file() != null && RSWLanguagesOldConfig.file().contains("Languages")) {
                RSWLanguagesOldConfig.file().getSection("Languages").getRoutesAsStrings(false).forEach(lang -> {
                    rsa.getLogger().info("Converting language file " + lang + " to the new format...");
                    Section section = RSWLanguagesOldConfig.file().getSection("Languages." + lang);

                    String name = section.getString("Language-Specific.Translation-Key");
                    try {
                        YamlDocument doc = YamlDocument.create(new File(languagesFolder, name + ".yml"));
                        doc.set(name, section);
                        doc.save();
                        rsa.getLogger().warning("Language file " + lang + " converted successfully!");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }

        //copy any bundled language file that isn't there yet - this runs on every startup so a
        //language added by a later release also lands on servers that already have the folder
        for (String langFile : BUNDLED_LANGUAGES) {
            File file = new File(languagesFolder, langFile + ".yml");
            if (!file.exists()) {
                try {
                    Files.copy(Objects.requireNonNull(rsa.getPlugin().getResource("languages/" + langFile + ".yml")), file.toPath());
                } catch (IOException e) {
                    rsa.getLogger().severe("Could not copy language file " + langFile + " -> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        //loop through files in the languages folder
        for (File file : Objects.requireNonNull(languagesFolder.listFiles())) {
            if (file.getName().endsWith(".yml") && !file.getName().equals("version.yml")) {
                String key = file.getName().replace(".yml", "");

                //language files are read with a plain YamlConfiguration, so nothing merges new keys
                //into them on an update - do it here before the file is loaded
                try {
                    mergeMissingKeys(file, key);
                } catch (Exception e) {
                    rsa.getLogger().severe("Could not add the missing keys to language file " + file.getName() + " -> " + e.getMessage());
                    Debugger.print(LanguageManager.class, e.getMessage());
                }

                try {
                    RSWLanguage l = new RSWLanguage(file);
                    this.langList.put(l.getKey(), l);
                } catch (Exception e) {
                    rsa.getLogger().severe("Could not load language file " + file.getName() + " -> " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }

        String simpleVersion = ServerVersionUtil.getSimpleServerVersion();
        File folder = new File(rsa.getPlugin().getDataFolder(), "languages");
        File translationVersionFile = new File(folder, "version.yml");

        // Check if the language file exists
        if (translationVersionFile.exists()) {
            try {
                String version = new String(Files.readAllBytes(translationVersionFile.toPath()));
                if (!version.equals(simpleVersion)) {
                    rsa.getLogger().info("Updating language files from " + version + " to " + simpleVersion + ". Downloading new versions...");
                    for (RSWLanguage language : getLanguages()) {
                        try {
                            language.downloadLanguageFile();
                        } catch (Exception e) {
                            rsa.getLogger().severe("Could not update language file " + language.getKey() + " -> " + e.getMessage());
                            Debugger.print(LanguageManager.class, e.getMessage());
                        }
                    }

                    Files.write(translationVersionFile.toPath(), simpleVersion.getBytes());
                }
            } catch (IOException e) {
                rsa.getLogger().severe("Could not update language files -> " + e.getMessage());
                Debugger.print(LanguageManager.class, e.getMessage());
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

    /**
     * Adds every route the bundled language files define but this file is missing, without ever
     * touching a route that is already there - existing translations and admin edits are kept.
     * <p>
     * en_us is the source of truth for the set of routes: a language whose bundled file has not
     * been translated yet, and a language file the admin wrote themselves (for which the jar has no
     * counterpart at all), both end up with every route filled instead of returning null.
     */
    private void mergeMissingKeys(File file, String key) throws IOException {
        InputStream fallbackIn = rsa.getPlugin().getResource("languages/" + FALLBACK_LANGUAGE + ".yml");
        if (fallbackIn == null) {
            return;
        }

        YamlConfiguration fallback;
        try (InputStreamReader reader = new InputStreamReader(fallbackIn, StandardCharsets.UTF_8)) {
            fallback = YamlConfiguration.loadConfiguration(reader);
        }

        YamlConfiguration own = null;
        InputStream ownIn = rsa.getPlugin().getResource("languages/" + key + ".yml");
        if (ownIn != null) {
            try (InputStreamReader reader = new InputStreamReader(ownIn, StandardCharsets.UTF_8)) {
                own = YamlConfiguration.loadConfiguration(reader);
            }
        }

        YamlConfiguration user = YamlConfiguration.loadConfiguration(file);

        int added = 0;
        for (String route : fallback.getKeys(true)) {
            //only leaves - copying a whole section would clobber the user's edits inside it
            if (fallback.isConfigurationSection(route)) {
                continue;
            }

            String target = key + route.substring(FALLBACK_LANGUAGE.length());
            if (user.contains(target)) {
                continue;
            }

            user.set(target, own != null && own.contains(target) ? own.get(target) : fallback.get(route));
            ++added;
        }

        if (added > 0) {
            user.save(file);
            rsa.getLogger().info("Added " + added + " missing key(s) to languages/" + file.getName() + ".");
        }
    }

    @Override
    public String getDefaultLanguage() {
        String configuredLanguage = RSWConfig.file().getString("Config.Languages.Default-Language");
        if (configuredLanguage == null || !this.langList.containsKey(configuredLanguage)) {
            configuredLanguage = RSWConfig.file().getString("Config.Default-Language");
        }

        return configuredLanguage != null && this.langList.containsKey(configuredLanguage)
                ? configuredLanguage
                : this.langList.keySet().stream().findFirst().get();
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
        return Text.color(RSWConfig.file().getString("Config.Prefix"));
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
