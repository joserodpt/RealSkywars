package joserodpt.realskywars.api.utils.bedrock;

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

import org.geysermc.cumulus.form.SimpleForm;
import org.geysermc.floodgate.api.FloodgateApi;

import java.util.List;
import java.util.UUID;
import java.util.function.IntConsumer;

/**
 * The only class that touches Floodgate and Cumulus. It is only loaded once {@link BedrockForms}
 * knows Floodgate is installed, so servers without it never hit a NoClassDefFoundError.
 */
final class FloodgateHook {

    private FloodgateHook() {
    }

    static boolean isBedrock(UUID uuid) {
        return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
    }

    //the click handler runs on a Floodgate thread
    static boolean sendList(UUID uuid, String title, String content, List<String> buttons, IntConsumer onClick) {
        SimpleForm.Builder form = SimpleForm.builder().title(title).content(content);
        for (String button : buttons) {
            form.button(button);
        }
        form.validResultHandler(response -> onClick.accept(response.clickedButtonId()));
        return FloodgateApi.getInstance().sendForm(uuid, form);
    }
}
