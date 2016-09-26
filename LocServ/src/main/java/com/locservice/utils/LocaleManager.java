package com.locservice.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Vahagn Gevorgyan
 * 10 February 2016
 * vahagngevorgyan1989@gmail.com
 * LocService
 */
public class LocaleManager {

    /**
     * Method for getting locale string
     * @return
     */
    public static String getLocaleLang() {
        return "ru";
    }

    public static List<String> getLocaleLangs() {
        List<String> langs = new ArrayList<String>();
        langs.add("en");
        langs.add("de");
        langs.add("fr");
        langs.add("ru");
        langs.add("am");

        return langs;
    }
}
