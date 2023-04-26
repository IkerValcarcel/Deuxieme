package com.example.deuxieme;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.deuxieme.DataBase.ImagesDB;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HomeScreen extends AppCompatActivity implements View.OnClickListener {

    public static final int CAMERA_PERMISSION_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private Button logoutBTN;
    private Button logExpenseBTN;
    private Button openCameraBTN;
    private ImagesDB db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_screen);

        db = new ImagesDB();

        // Obtenemos los elementos de la interfaz
        logoutBTN = findViewById(R.id.log_out);
        logExpenseBTN = findViewById(R.id.log_expense);
        openCameraBTN = findViewById(R.id.open_camera);
        // // Se definenen los listeners en los botones
        logoutBTN.setOnClickListener(this);
        logExpenseBTN.setOnClickListener(this);
        openCameraBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            // Se clica en abrir la camara
            case R.id.open_camera:
                requestCameraPermissions();
                break;
            // Se clica en CERRAR SESION
            case R.id.log_out:
                logout();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            saveImage(data);
        }
    }

    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA))
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE );
        else
            Toast.makeText(this, "Esta dispositivo no dispone de cámara", Toast.LENGTH_SHORT).show();
    }

    private void saveImage(Intent data) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
        LocalDateTime now = LocalDateTime.now();
        // String name = dtf.format(now);
        String name = "test";
        Bitmap img = (Bitmap) data.getExtras().get("data");
        UploadImage task = new UploadImage(this, name, img);
        task.execute();
    }

    private void requestCameraPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        else
            openCamera();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            openCamera();
        else
            Toast.makeText(this, "Es necesario permitir el uso de la cámara para usar esta funcionalidad", Toast.LENGTH_LONG);
    }

    private void logout() {
        // Se borra la session de shared preferences
        SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        preferences.edit().remove("user").commit();
        // Se abre la pestaña de inicio de sesion y se cierra el menu principal
        Intent intent = new Intent(this, Login.class);
        startActivity(intent);
        finish();
    }

    private class UploadImage extends AsyncTask<Void, Void, String> {

        private Context context;
        private String name;
        private Bitmap image;

        public UploadImage(Context context, String name, Bitmap image) {
            this.context = context;
            this.name = name;
            this.image = image;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                return db.uploadImage(image, name);
            } catch (Exception e) {
                return "Error al conectarse a la base de datos";
            }
        }

        @Override
        protected void onPostExecute(String output) {
            Toast.makeText(context, output, Toast.LENGTH_SHORT).show();
        }
    }

}