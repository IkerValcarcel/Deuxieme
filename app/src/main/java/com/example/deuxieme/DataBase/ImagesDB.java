package com.example.deuxieme.DataBase;

import android.content.ContentValues;
import android.graphics.Bitmap;
import android.util.Base64;

import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class ImagesDB {

    final static private String DB_URL = "http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/ivalcarcel003/WEB/";

    public String uploadImage(Bitmap image, String name) throws IOException {
        URL url = new URL("http://ec2-54-93-62-124.eu-central-1.compute.amazonaws.com/ivalcarcel003/WEB/upload-picture.php");

        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setDoOutput(true);

        // Establecer los parámetros de la petición
        String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

        // Escribir los parámetros en el cuerpo de la petición
        OutputStream outputStream = connection.getOutputStream();
        PrintWriter writer = new PrintWriter(new OutputStreamWriter(outputStream, "UTF-8"), true);
        writer.append("--" + boundary + "\r\n");
        writer.append("Content-Disposition: form-data; name=\"name\"\r\n\r\n" + name + "\r\n");
        writer.append("--" + boundary + "\r\n");
        writer.append("Content-Disposition: form-data; name=\"image\"; filename=\"" + name + ".jpg\"\r\n");
        writer.append("Content-Type: image/jpeg\r\n\r\n");

        // Escribir la imagen en el cuerpo de la petición
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
        byte[] imageData = byteArrayOutputStream.toByteArray();
        outputStream.write(imageData);

        writer.append("\r\n");
        writer.append("--" + boundary + "--\r\n");
        writer.close();

        // Obtener la respuesta del servidor
        int responseCode = connection.getResponseCode();
        InputStream inputStream;
        if (responseCode >= 400) {
            inputStream = connection.getErrorStream();
        } else {
            inputStream = connection.getInputStream();
        }

        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder stringBuilder = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            stringBuilder.append(line);
        }
        reader.close();

        return stringBuilder.toString();
    }
}
