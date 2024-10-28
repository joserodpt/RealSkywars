package joserodpt.realskywars.api.utils;

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
 * @author José Rodrigues © 2019-2024
 * @link https://github.com/joserodpt/RealSkywars
 */

import joserodpt.realskywars.api.RealSkywarsAPI;
import joserodpt.realskywars.api.config.RSWConfig;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class Text {

    public static String anonName() {
        List<String> nicks = RSWConfig.file().getStringList("Config.Random-Nicknames");
        return nicks.get(RealSkywarsAPI.getInstance().getRandom().nextInt(nicks.size())) + " #" + RealSkywarsAPI.getInstance().getRandom().nextInt(99);
    }

    public static String getDateAndTime() {
        DateFormat dateFormat = new SimpleDateFormat(RSWConfig.file().getString("Config.Time.Formatting"));
        Calendar cal = Calendar.getInstance();
        cal.setTime(new Date());
        cal.add(Calendar.HOUR_OF_DAY, RSWConfig.file().getInt("Config.Time.Offset"));
        cal.getTime();
        return dateFormat.format(cal.getTime());
    }

    public static String color(String string) {
        if (string == null) {
            return "";
        }
        return ChatColor.translateAlternateColorCodes('&', string);
    }

    public static String strip(String s) {
        return ChatColor.stripColor(s);
    }

    public static List<String> color(List<?> list) {
        return list.stream()
                .map(element -> Text.color((String) element))
                .collect(Collectors.toList());
    }

    public static String formatSeconds(int n) {
        int hours = n / 3600;
        int minutes = (n % 3600) / 60;
        int seconds = n % 60;

        if (hours == 0) {
            if (minutes == 0) {
                return String.format("%ds", seconds);
            } else {
                if (seconds == 0) {
                    return String.format("%dm", minutes);
                }
                return String.format("%dm%ds", minutes, seconds);
            }
        } else {
            if (minutes == 0) {
                if (seconds == 0) {
                    return String.format("%dh", hours);
                }
                return String.format("%dh%ds", hours, seconds);
            }
            return String.format("%dh%dm%ds", hours, minutes, seconds);
        }
    }

    public static void sendList(Player p, List<String> list) {
        list.forEach(s -> p.sendMessage(color(s)));
    }

    public static void sendList(CommandSender p, List<String> list) {
        list.forEach(s -> p.sendMessage(color(s)));
    }

    public static void send(CommandSender p, String s) {
        p.sendMessage(color(s));
    }

    public static CharSequence makeSpace() {
        return Text.color(randSp() + "&b" + randSp() + "&c" + randSp());
    }

    private static String randSp() {
        List<String> sp = Arrays.asList("&6", "&7", "&8", "&9", "&5", "&f", "&e", "&a", "&b");
        return sp.get(RealSkywarsAPI.getInstance().getRandom().nextInt(sp.size()));
    }

    public static List<String> replaceVarInList(List<String> list, String rep, String var) {
        return list.stream()
                .map(s -> s.replace(rep, var))
                .collect(Collectors.toList());
    }

    public static String beautifyEnumName(String name) {
        if (name == null || name.isEmpty()) {
            return "Unknown";
        }

        String[] parts = name.split("_");
        StringBuilder formattedString = new StringBuilder();

        for (String part : parts) {
            if (!part.isEmpty()) {
                formattedString.append(part.substring(0, 1).toUpperCase());
                if (part.length() > 1) {
                    formattedString.append(part.substring(1).toLowerCase());
                }
            }
            formattedString.append(" ");
        }
        return formattedString.toString().trim();
    }

    private static final int CENTER_PX = 154;

    public static String centerMessage(String message) {
        String[] lines = color(message).split("\n", 40);
        StringBuilder returnMessage = new StringBuilder();

        for (String line : lines) {
            int messagePxSize = 0;
            boolean previousCode = false;
            boolean isBold = false;

            for (char c : line.toCharArray()) {
                if (c == '§') {
                    previousCode = true;
                } else if (previousCode) {
                    previousCode = false;
                    isBold = c == 'l';
                } else {
                    DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                    messagePxSize = isBold ? messagePxSize + dFI.getBoldLength() : messagePxSize + dFI.getLength();
                    ++messagePxSize;
                }
            }
            int toCompensate = CENTER_PX - messagePxSize / 2;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while (compensated < toCompensate) {
                sb.append(" ");
                compensated += spaceLength;
            }
            returnMessage.append(sb).append(line).append("\n");
        }

        return returnMessage.toString();
    }

    public static String formatDouble(Double gameBalance) {
        String formattedBalance = gameBalance % 1 == 0 ? String.valueOf(gameBalance.intValue()) : String.valueOf(gameBalance);

        if (gameBalance >= 1000) {
            formattedBalance = formatLargeNumber(gameBalance);
        }

        return formattedBalance;
    }

    private static String formatLargeNumber(Double number) {
        String[] suffixes = {"", "k", "M", "B", "T", "Q"};
        int index = 0;

        while (number >= 1000 && index < suffixes.length - 1) {
            number /= 1000;
            index++;
        }

        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        return decimalFormat.format(number) + suffixes[index];
    }

    public enum DefaultFontInfo {

        A('A', 5), a('a', 5), B('B', 5), b('b', 5), C('C', 5), c('c', 5), D('D', 5), d('d', 5), E('E', 5), e('e', 5), F('F', 5), f('f', 4), G('G', 5), g('g', 5), H('H', 5), h('h', 5), I('I', 3), i('i', 1), J('J', 5), j('j', 5), K('K', 5), k('k', 4), L('L', 5), l('l', 1), M('M', 5), m('m', 5), N('N', 5), n('n', 5), O('O', 5), o('o', 5), P('P', 5), p('p', 5), Q('Q', 5), q('q', 5), R('R', 5), r('r', 5), S('S', 5), s('s', 5), T('T', 5), t('t', 4), U('U', 5), u('u', 5), V('V', 5), v('v', 5), W('W', 5), w('w', 5), X('X', 5), x('x', 5), Y('Y', 5), y('y', 5), Z('Z', 5), z('z', 5), NUM_1('1', 5), NUM_2('2', 5), NUM_3('3', 5), NUM_4('4', 5), NUM_5('5', 5), NUM_6('6', 5), NUM_7('7', 5), NUM_8('8', 5), NUM_9('9', 5), NUM_0('0', 5), EXCLAMATION_POINT('!', 1), AT_SYMBOL('@', 6), NUM_SIGN('#', 5), DOLLAR_SIGN('$', 5), PERCENT('%', 5), UP_ARROW('^', 5), AMPERSAND('&', 5), ASTERISK('*', 5), LEFT_PARENTHESIS('(', 4), RIGHT_PERENTHESIS(')', 4), MINUS('-', 5), UNDERSCORE('_', 5), PLUS_SIGN('+', 5), EQUALS_SIGN('=', 5), LEFT_CURL_BRACE('{', 4), RIGHT_CURL_BRACE('}', 4), LEFT_BRACKET('[', 3), RIGHT_BRACKET(']', 3), COLON(':', 1), SEMI_COLON(';', 1), DOUBLE_QUOTE('"', 3), SINGLE_QUOTE('\'', 1), LEFT_ARROW('<', 4), RIGHT_ARROW('>', 4), QUESTION_MARK('?', 5), SLASH('/', 5), BACK_SLASH('\\', 5), LINE('|', 1), TILDE('~', 5), TICK('`', 2), PERIOD('.', 1), COMMA(',', 1), SPACE(' ', 3), DEFAULT('a', 4);

        private final char character;
        private final int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
        }

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }

        public char getCharacter() {
            return this.character;
        }

        public int getLength() {
            return this.length;
        }

        public int getBoldLength() {
            if (this == DefaultFontInfo.SPACE) return this.getLength();
            return this.length + 1;
        }
    }
}
