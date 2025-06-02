package joserodpt.realskywars.plugin.commands;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.utils.Text;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BaseCommandWA extends BaseCommand {

    private final Map<String, String> commandUsages = new HashMap<>();

    public BaseCommandWA() {
        for (Method method : this.getClass().getMethods()) {
            if (method.isAnnotationPresent(WrongUsage.class) && method.isAnnotationPresent(SubCommand.class)) {
                String usage = method.getAnnotation(WrongUsage.class).value();

                SubCommand subCommand = method.getAnnotation(SubCommand.class);

                String commandName = subCommand.value();
                commandUsages.put(commandName, usage);
                for (String alias : subCommand.alias()) {
                    commandUsages.put(alias, usage);
                }
            }
        }
    }

    public String getWrongUsage(@NotNull String subCommand) {
        return this.commandUsages.getOrDefault(subCommand, RealSkywarsAPI.getInstance().getLanguageManagerAPI().getPrefix() + Text.color("&cWrong usage for the command!"));
    }
}