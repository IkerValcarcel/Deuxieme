package com.example.deuxieme.DataBase;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class UsersDB {
    final static private String DB_URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/ivalcarcel003/WEB/";

    public String login(String username, String password) throws Exception{
        // Definimos la direccion del endpoint que va a crear el usuario nuevo.
        final String DB_URL_LOGIN = DB_URL + "login.php";

        // Abrimos la conexion a la URL
        URL url = new URL(DB_URL_LOGIN);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Definimos las propiedades de la peticion
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // Definimos los datos que se van a mandar en la peticion
        String data = String.format("{\"username\":\"%s\",\"password\":\"%s\"}", username, password);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);
            wr.write(postData);
            wr.flush();
        }

        try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null) {
                response.append(line);
            }
            return response.toString();
        } catch (Exception e){
            return "{\"error\":\"Error al conectarse al servidor\"}";
        }
    }

    public String register(String email, String username, String password) throws Exception {
        // Definimos la direccion del endpoint que va a crear el usuario nuevo.
        final String DB_URL_INSERT_USER = DB_URL + "insert-user.php";

        // Abrimos la conexion a la URL
        URL url = new URL(DB_URL_INSERT_USER);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();

        // Definimos las propiedades de la peticion
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        con.setDoOutput(true);

        // Definimos los datos que se van a mandar
        String data = String.format("{\"email\":\"%s\",\"username\":\"%s\",\"password\":\"%s\"}", email, username, password);
        try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
            byte[] postData = data.getBytes(StandardCharsets.UTF_8);
            wr.write(postData);
            wr.flush();
        }

        // Leemos la peticion
        int responseCode = con.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()))) {
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = in.readLine()) != null) {
                    response.append(line);
                }
                return response.toString();
            }
        } else {
            return "{\"error\":\"Error al conectarse al servidor\"}";
        }
    }
}
