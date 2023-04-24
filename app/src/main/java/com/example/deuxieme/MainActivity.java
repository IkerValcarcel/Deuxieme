package com.example.deuxieme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Se crea el canal de notificaciones
        NotificationChannel channel = new NotificationChannel("expenses", "Expenses", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);

        Intent intent;
        if (isUserLogged()) {
            intent = new Intent(this, HomeScreen.class);
        } else {
            intent = new Intent(this, Login.class);
        }
        startActivity(intent);
        finish();
    }
    private boolean isUserLogged() {
       /* SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
        String user = preferences.getString("user", null);
        return user != null;*/
        return false;
    }
}