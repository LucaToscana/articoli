package com.gestione.articoli.utils;

import com.gestione.articoli.dto.UserDto;

public class UserUtils {

    /**
     * Nasconde i campi sensibili di un UserDto (password e ruoli)
     * @param user UserDto da modificare
     * @return stesso oggetto con password e ruoli null
     */
    public static UserDto hideSensitiveData(UserDto user) {
        if (user != null) {
           // user.setPassword(null);
            user.setRoles(null);
        }
        return user;
    }
}