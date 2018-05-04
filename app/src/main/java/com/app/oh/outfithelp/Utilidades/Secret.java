package com.app.oh.outfithelp.Utilidades;

import android.content.Context;

/**
 * Created by Usuario on 02/05/2018.
 */

public class Secret {
    private static Secret miInstancia;
    private static Context miContext;

    private Secret (Context c)
    {
        miContext = c;
    }

    public static synchronized Secret getInstancia(Context c) {
        if (miInstancia == null) {
            miInstancia = new Secret(c);
        }
        return miInstancia;
    }

    public String code(String user, String pass)
    {
        user = user.trim();
        pass = pass.trim();
        user = user.replace("1", "&b").replace("2", "&c").replace("3", "&d").replace("4", "&f").replace("5", "&g").replace("6", "&h").replace("7", "&j").replace("8", "&k").replace("9", "&x").replace("0", "&z");
        user = user.replace("a", "$5").replace("e", "$4").replace("i", "$3").replace("o","$2").replace("u", "$1");
        pass = pass.replace("1", "&b").replace("2", "&c").replace("3", "&d").replace("4", "&f").replace("5", "&g").replace("6", "&h").replace("7", "&j").replace("8", "&k").replace("9", "&x").replace("0", "&z");
        pass = pass.replace("a", "$5").replace("e", "$4").replace("i", "$3").replace("o", "$2").replace("u", "$1");
        return user + "!$%" + pass;
    }
}
