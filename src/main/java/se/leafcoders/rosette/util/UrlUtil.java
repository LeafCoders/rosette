package se.leafcoders.rosette.util;

public class UrlUtil {

    public static String withoutEndingSlash(String url) {
        if (url.endsWith("/")) {
            return url.substring(0, url.length() - 1);
        } else {
            return url;
        }
    }
}
