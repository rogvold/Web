package ru.cardio.core.services;

import com.google.gson.Gson;

/**
 *
 * @author Shaykhlislamov Sabir (email: sha-sabir@yandex.ru)
 */
public class GsonService {

    public static Gson gson = null;

    public static Gson getInstance() {
        if (gson == null) {
            gson = new Gson();
            return gson;
        } else {
            return gson;
        }
    }
}
