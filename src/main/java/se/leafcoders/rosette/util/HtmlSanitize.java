package se.leafcoders.rosette.util;

public class HtmlSanitize {

    public static String sanitize(String plainText) {
        if (plainText == null) {
            return "";
        }
        return plainText
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("’", "&apos;")
                .replace("\"", "&quot;");
    }
    
    public static String sanitizeAndConvertNewline(String plainText) {
        return HtmlSanitize.sanitize(plainText).replace("\n", "<br>");
    }

}
