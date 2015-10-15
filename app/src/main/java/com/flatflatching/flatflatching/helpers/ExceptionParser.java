package com.flatflatching.flatflatching.helpers;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by rafael on 14.10.2015.
 */
public final class ExceptionParser {
    private ExceptionParser() {

    }

    public static final Map<Integer, String> EXCEPTION_MAP = new HashMap<>();
    static {
        EXCEPTION_MAP.put(1, "Authentisierung fehlgeschlagen");
        EXCEPTION_MAP.put(2, "Authentisierung fehlgeschlagen, du bist kein Admin");
        EXCEPTION_MAP.put(11, "User wurde nicht gefunden");
        EXCEPTION_MAP.put(17, "Verteilschlüssel ungültig");
        EXCEPTION_MAP.put(111, "Du hast keine Strasse angegeben");
        EXCEPTION_MAP.put(112, "Du hast keine Hausnummer angegeben");
        EXCEPTION_MAP.put(113, "Du hast keine Ortschaft angegeben");
        EXCEPTION_MAP.put(114, "Du hast keine Postleitzahl angegeben");
        EXCEPTION_MAP.put(115, "Du hast kein Land angegeben");
        EXCEPTION_MAP.put(201, "Keine Einladung gefunden");
        EXCEPTION_MAP.put(202, "WG wurde nicht gefunden");
        EXCEPTION_MAP.put(301, "Offene Finanzposten");
        EXCEPTION_MAP.put(701, "Ausgabe nicht gefunden");
    }
}
