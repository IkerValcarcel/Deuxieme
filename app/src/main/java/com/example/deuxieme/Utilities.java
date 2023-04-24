package com.example.deuxieme;

import java.util.HashMap;

public class Utilities {

    public static HashMap<String, String> parseJson(String jsonString) {
        HashMap<String, String> resultMap = new HashMap<>();
        // Elimina los espacios en blanco de la cadena JSON
        String json = jsonString.trim();
        // Comprueba si la cadena JSON empieza y termina con llaves
        if (json.startsWith("{") && json.endsWith("}")) {
            // Elimina las llaves
            json = json.substring(1, json.length() - 1);
            // Divide la cadena en pares clave-valor
            String[] pairs = json.split(",");
            for (String pair : pairs) {
                // Divide cada par en clave y valor
                String[] keyValue = pair.split(":");
                if (keyValue.length == 2) {
                    // Añade el par clave-valor al mapa de resultados
                    String key = keyValue[0].replaceAll("\"", "");
                    String value = keyValue[1].replaceAll("\"", "");
                    resultMap.put(key, value);
                }
            }
        }
        return resultMap;
    }

}
