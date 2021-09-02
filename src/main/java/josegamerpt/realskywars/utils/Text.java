package josegamerpt.realskywars.utils;

import josegamerpt.realskywars.RealSkywars;
import josegamerpt.realskywars.configuration.Config;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class Text {

    private static int CENTER_PX = 154;

    public static String centerMessage(String message){
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
                    messagePxSize++;
                }
            }
            int toCompensate = CENTER_PX - messagePxSize / 2;
            int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
            int compensated = 0;
            StringBuilder sb = new StringBuilder();
            while(compensated < toCompensate){
                sb.append(" ");
                compensated += spaceLength;
            }
            returnMessage.append(sb).append(line).append("\n");
        }

        return returnMessage.toString();
    }

    public static String anonName() {
        List<String> nicks = Config.file().getStringList("Config.Random-Nicknames");
        return nicks.get(RealSkywars.getRandom().nextInt(nicks.size())) + " #" + RealSkywars.getRandom().nextInt(99);
    }

    public enum DefaultFontInfo {

        A('A', 5),
        a('a', 5),
        B('B', 5),
        b('b', 5),
        C('C', 5),
        c('c', 5),
        D('D', 5),
        d('d', 5),
        E('E', 5),
        e('e', 5),
        F('F', 5),
        f('f', 4),
        G('G', 5),
        g('g', 5),
        H('H', 5),
        h('h', 5),
        I('I', 3),
        i('i', 1),
        J('J', 5),
        j('j', 5),
        K('K', 5),
        k('k', 4),
        L('L', 5),
        l('l', 1),
        M('M', 5),
        m('m', 5),
        N('N', 5),
        n('n', 5),
        O('O', 5),
        o('o', 5),
        P('P', 5),
        p('p', 5),
        Q('Q', 5),
        q('q', 5),
        R('R', 5),
        r('r', 5),
        S('S', 5),
        s('s', 5),
        T('T', 5),
        t('t', 4),
        U('U', 5),
        u('u', 5),
        V('V', 5),
        v('v', 5),
        W('W', 5),
        w('w', 5),
        X('X', 5),
        x('x', 5),
        Y('Y', 5),
        y('y', 5),
        Z('Z', 5),
        z('z', 5),
        NUM_1('1', 5),
        NUM_2('2', 5),
        NUM_3('3', 5),
        NUM_4('4', 5),
        NUM_5('5', 5),
        NUM_6('6', 5),
        NUM_7('7', 5),
        NUM_8('8', 5),
        NUM_9('9', 5),
        NUM_0('0', 5),
        EXCLAMATION_POINT('!', 1),
        AT_SYMBOL('@', 6),
        NUM_SIGN('#', 5),
        DOLLAR_SIGN('$', 5),
        PERCENT('%', 5),
        UP_ARROW('^', 5),
        AMPERSAND('&', 5),
        ASTERISK('*', 5),
        LEFT_PARENTHESIS('(', 4),
        RIGHT_PERENTHESIS(')', 4),
        MINUS('-', 5),
        UNDERSCORE('_', 5),
        PLUS_SIGN('+', 5),
        EQUALS_SIGN('=', 5),
        LEFT_CURL_BRACE('{', 4),
        RIGHT_CURL_BRACE('}', 4),
        LEFT_BRACKET('[', 3),
        RIGHT_BRACKET(']', 3),
        COLON(':', 1),
        SEMI_COLON(';', 1),
        DOUBLE_QUOTE('"', 3),
        SINGLE_QUOTE('\'', 1),
        LEFT_ARROW('<', 4),
        RIGHT_ARROW('>', 4),
        QUESTION_MARK('?', 5),
        SLASH('/', 5),
        BACK_SLASH('\\', 5),
        LINE('|', 1),
        TILDE('~', 5),
        TICK('`', 2),
        PERIOD('.', 1),
        COMMA(',', 1),
        SPACE(' ', 3),
        DEFAULT('a', 4);

        private char character;
        private int length;

        DefaultFontInfo(char character, int length) {
            this.character = character;
            this.length = length;
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

        public static DefaultFontInfo getDefaultFontInfo(char c) {
            for (DefaultFontInfo dFI : DefaultFontInfo.values()) {
                if (dFI.getCharacter() == c) return dFI;
            }
            return DefaultFontInfo.DEFAULT;
        }
    }

    public static String color(String string) {
        return ChatColor.translateAlternateColorCodes('&', string);
    }
    public static ArrayList<String> color(List<?> list) {
        ArrayList<String> color = new ArrayList<>();
        for (Object s : list) {
            color.add(Text.color((String) s));
        }
        return color;
    }

    public static String formatSeconds(int n) {
        int secs = n % 60;
        int horas = n / 60;
        int mins = horas % 60;
        horas = horas / 60;
        if (horas == 0 && mins != 0)
        {
            return mins + "m " + secs + "s";
        }
        if (mins == 0)
        {
            return secs + "s";
        }
        return horas + "h " + mins + "m " + secs + "s";

    }

    public static void sendList(Player p, List<String> list) { list.forEach(s -> p.sendMessage(color(s))); }
    public static void sendList(Player p, ArrayList<String> list, Object var) { list.forEach(s ->  p.sendMessage(color(s).replace("%CAGES%", var + ""))); }
    public static void sendList(CommandSender p, List<String> list) { list.forEach(s -> p.sendMessage(color(s))); }
    public static void sendList(CommandSender p, ArrayList<String> list) { list.forEach(s -> p.sendMessage(color(s))); }
    public static void send(CommandSender p, String s) { p.sendMessage(color(s)); }

    public static CharSequence makeSpace() {
        return Text.color(randSp() + "&b" +  randSp() + "&c" + randSp());
    }

    private static String randSp() {
        List<String> sp = Arrays.asList("&6", "&7", "&8", "&9", "&5", "&f", "&e", "&a", "&b");
        return sp.get(RealSkywars.getRandom().nextInt(sp.size()));
    }

    public static ArrayList<String> replaceVarInList(ArrayList<String> list, String rep, String var) {
        ArrayList<String> color = new ArrayList<>();
        for (String s : list) {
            color.add(s.replace(rep, var));
        }
        return color;
    }
}
