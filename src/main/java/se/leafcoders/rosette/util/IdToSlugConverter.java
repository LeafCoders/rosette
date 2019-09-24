package se.leafcoders.rosette.util;

import java.util.Arrays;

public class IdToSlugConverter {

    private static final char[] accepted = new char[1024];
    private static final char[] replacers = new char[1024];
    
    static {
        Arrays.asList('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-').forEach(c -> accepted[c] = c);
        replacers['å'] = 'a';
        replacers['ä'] = 'a';
        replacers['ö'] = 'o';
        replacers['è'] = 'e';
        replacers['é'] = 'e';
        replacers['æ'] = 'e';
        replacers['ü'] = 'u';
        replacers[' '] = '-';
    }

    // Prefix shall be exactly 2 chars
    public static String convertIdToSlug(long id, String name, String prefix) {
        String slug = slugify(name) + '-' + prefix;
        long idAndSlugValue = id + calcStringSum(slug);
        return slug + Long.toUnsignedString(idAndSlugValue, 20);
    }

    public static Long convertSlugToId(String slug) {
        int lastPartPos = slug.lastIndexOf('-');
        if (lastPartPos > 0) {
            String nameAndPrefixPart = slug.substring(0, lastPartPos + 3);
            String idPart = slug.substring(lastPartPos + 3);
            int slugAndPrefixValue = calcStringSum(nameAndPrefixPart);
            return Long.parseLong(idPart, 20) - slugAndPrefixValue;
        }
        return null;
    }

    private static int calcStringSum(String s) {
        int value = 0;
        for (int i = 0; i < s.length(); ++i) {
            value += s.charAt(i);
        }
        return value;
    }

    private static String slugify(String s) {
        char[] chars = s.toLowerCase().toCharArray();
        StringBuilder sb = new StringBuilder();
        char lastChar = '-';
        for (int i = 0; i < chars.length; ++i) {
            char c = chars[i];
            char repl = replacers[c];
            if (repl > 0) {
                c = repl;
            }
            c = accepted[c];
            if (c > 0) {
                if (lastChar != '-' || c != '-') {
                    sb.append(c);
                }
                lastChar = c;                
            }
        }
        return sb.toString().replaceAll("-+$", ""); // Trim - from end of text
    }

}
