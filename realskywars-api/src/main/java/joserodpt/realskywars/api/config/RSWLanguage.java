package joserodpt.realskywars.api.config;

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

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import joserodpt.realskywars.api.Debugger;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.utils.ItemStackSpringer;
import joserodpt.realskywars.api.utils.Itens;
import joserodpt.realskywars.api.utils.Text;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class RSWLanguage {

    private final String key;
    private final String displayName;
    private final ItemStack icon;

    private final File folder = new File(RealSkywarsAPI.getInstance().getPlugin().getDataFolder(), "languages");
    private final File translationFile;
    private final YamlConfiguration config;
    private JsonObject json = null;


    private final String version = RealSkywarsAPI.getInstance().getSimpleServerVersion();

    public RSWLanguage(File configFile) {
        this.key = configFile.getName().replace(".yml", "");

        // load language config file
        this.config = YamlConfiguration.loadConfiguration(configFile);

        this.displayName = this.getConfig().getString(this.getKey() + ".Language-Specific.Displayname");

        this.icon = Itens.renameItem(Objects.requireNonNull(ItemStackSpringer.getItemDeSerialized(sectionToMap(this.getKey() + ".Language-Specific.Icon"))), "&e&l" + this.getDisplayName(), generateLore());
        translationFile = new File(folder, this.getKey() + ".json");

        // download the language file from https://assets.mcasset.cloud/1.21/assets/minecraft/lang/{getTranslationKey()}.json to the translations folder
        if (folder.exists() && !translationFile.exists()) {
            try {
                downloadLanguageFile();
            } catch (Exception e) {
                Debugger.print(RSWLanguage.class, "Could not load language " + this.key + " - " + this.displayName + " -> Exception: " + e.getMessage());
            }
        }
    }

    private List<String> generateLore() {
        List<String> lore = new ArrayList<>();
        lore.add(this.getString(".Menus.Language.Select"));

        List<String> authors = this.getStringList(".Language-Specific.Authors");
        if (authors != null && !authors.isEmpty()) {
            lore.add("&7Authors: ");
            authors.forEach(author -> lore.add("&7- &f" + author));
        }

        return lore;
    }

    private Map<String, Object> sectionToMap(String section) {
        Map<String, Object> newMap = new HashMap<>();
        this.getConfig().getConfigurationSection(section).getKeys(false).forEach(route -> {
            newMap.put(route, getConfig().get(section + "." + route));
        });

        return newMap;
    }

    public YamlConfiguration getConfig() {
        return config;
    }

    public ItemStack getIcon() {
        return icon;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getKey() {
        return this.key;
    }

    public void downloadLanguageFile() throws Exception {
        RealSkywarsAPI.getInstance().getLogger().info("Downloading minecraft language file for " + getKey() + " (" + version + ") ...");

        String fileName = getKey() + ".json";
        String urlString = "https://assets.mcasset.cloud/" + version + "/assets/minecraft/lang/" + fileName;

        try {
            // Ensure the translations folder exists
            if (!folder.exists()) {
                folder.mkdir();
            }

            // Open a connection to the URL
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Check if the response code is OK (200)
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                // Get the input stream from the connection
                try (InputStream inputStream = connection.getInputStream()) {
                    // Write the input stream to the file
                    Files.copy(inputStream, translationFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }
            } else {
                RealSkywarsAPI.getInstance().getLogger().severe("Failed to download language file for " + getKey() + "(" + version + ") -> Response: " + connection.getResponseMessage());
            }

            // Disconnect the connection
            connection.disconnect();

            RealSkywarsAPI.getInstance().getLogger().info("Downloaded minecraft language file for " + getKey() + " (" + version + ")!");
        } catch (IOException e) {
            // RealSkywarsAPI.getInstance().getLogger().severe("An error occurred while downloading the language file: " + e.getMessage());
            throw e;
        }
    }

    private void loadJsonFile() throws Exception {
        // download the language file from https://assets.mcasset.cloud/1.21/assets/minecraft/lang/{getTranslationKey()}.json to the translations folder
        if (folder.exists() && !translationFile.exists()) {
            downloadLanguageFile();
        }

        if (json != null) {
            return;
        }

        parseJsonFile(translationFile);
    }

    private void parseJsonFile(File file) throws IOException {
        if (file.exists()) {
            try (InputStreamReader streamReader = new InputStreamReader(Files.newInputStream(file.toPath()));
                 BufferedReader reader = new BufferedReader(streamReader)) {
                Gson gson = new Gson();
                this.json = gson.fromJson(reader, JsonObject.class);
            }
        }
    }

    //available data from minecraft language files
    public String getMaterialName(Material mat) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.getKey() + " - " + this.getDisplayName() + " -> Exception: " + e.getMessage());
            return RealSkywarsAPI.getInstance().getNMS().getItemName(mat);
        }

        String name = mat.getKey().getKey();
        if (name.contains("wall_")) name = name.replace("wall_", "");

        return getLocalizedString((mat.isBlock() ? "block.minecraft." : "item.minecraft.") + name);
    }

    public String getEnchantmentName(Enchantment ench) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.getKey() + " - " + this.getDisplayName() + " -> Exception: " + e.getMessage());
            return Text.beautifyEnumName(ench.getKey().getKey());
        }

        return getLocalizedString("enchantment.minecraft." + ench.getKey().getKey());
    }

    public String getEntityName(EntityType type) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.getKey() + " - " + this.getDisplayName() + " -> Exception: " + e.getMessage());
            return Text.beautifyEnumName(type.name());
        }

        String name = type.name();
        if (name == null) return getLocalizedString("entity.notFound");
        return getLocalizedString("entity.minecraft." + name.toLowerCase());
    }

    private String getLocalizedString(String key) {
        try {
            loadJsonFile();
        } catch (Exception e) {
            Debugger.print(RSWLanguage.class, "Could not load language " + this.getKey() + " - " + this.getDisplayName() + " -> Exception: " + e.getMessage());
            return key;
        }

        if (json == null) return "Language file " + getKey() + " not loaded!";
        return json.get(key).getAsString();
    }

    public List<String> getStringList(String s) {
        return this.getConfig().getStringList(this.getKey() + s);
    }

    public String getString(String s) {
        return this.getConfig().getString(this.getKey() + s);
    }
}
