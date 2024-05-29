package com.booking_service.util;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class StringUtil {

    private static final String TG_PREFIX_AT = "@";
    private static final String TG_PREFIX_HTTPS = "https://t.me/";

    public static String formatTelegramLink(String link) {
        if (link.startsWith(TG_PREFIX_AT)) {
            return TG_PREFIX_HTTPS.concat(link.substring(1));
        } else if (!link.startsWith(TG_PREFIX_HTTPS)) {
            return TG_PREFIX_HTTPS.concat(link);
        }
        return link;
    }

    public static String toLowerCaseAndTrim(String string) {
        return string.toLowerCase().trim();
    }
}

