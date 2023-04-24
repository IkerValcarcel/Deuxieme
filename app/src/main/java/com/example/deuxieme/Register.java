package com.example.deuxieme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.deuxieme.DataBase.UsersDB;

import java.util.HashMap;

public class Register extends AppCompatActivity implements View.OnClickListener {

    private UsersDB db;
    EditText emailET, usernameET, passwordET;
    Button registerBTN, returnBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailET = findViewById(R.id.email);
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        registerBTN = findViewById(R.id.register);
        returnBTN = findViewById(R.id.return_button);

        // Solo en desarrollo. Borrar!!!
        emailET.setText("ikervalcarcel@gmail.com");
        usernameET.setText("IkerValcarcel");
        passwordET.setText("12345678");
        // Solo en desarrollo. Borrar!!!

        registerBTN.setOnClickListener(this);
        returnBTN.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.register:
                RegisterAsyncTask task = new RegisterAsyncTask(this);
                task.execute();
                break;
            case R.id.return_button:
                finish();
                break;
        }
    }

    private boolean isValidEmail(String email){
        // Se comprueba si el formato del correo es correcto.
        if (!email.contains("@"))
            return false;
        String[] splitEmail = email.split("@");
        return splitEmail[0].length() != 0 && splitEmail[1].length() != 0;
    }

    private class RegisterAsyncTask extends AsyncTask<String, Void, String> {

        private String email, username, password;
        private final Context context;
        private final UsersDB db;

        private RegisterAsyncTask(Context context) {
            this.context = context;
            db = new UsersDB();
        }

        @Override
        protected void onPreExecute() {
            email = emailET.getText().toString();
            username = usernameET.getText().toString();
            password = passwordET.getText().toString();
        }

        @Override
        protected String doInBackground(String... strings) {
            if (!isValidEmail(email))
                return "El formato del correo electrónico es incorrecto.";
            else if (username.length() < 4)
                return "El nombre de usuario es demasiado corto (>= 4).";
            else if (password.length() < 8)
                return "La contraseña es demasiado corta (>= 8).";
            else {
                // Se añade el usuario
                try {
                    return db.register(email, username, password);
                } catch (Exception e) {
                    e.printStackTrace();
                    return "Error";
                }
            }
        }

        @Override
        protected void onPostExecute(String registerOutput) {
            HashMap<String, String> mapOutput = Utilities.parseJson(registerOutput);
            String outMessage;
            if (registerOutput.contains("Usuario insertado correctamente")) {
                // Se añade a las preferencias las credenciales del usuario
                SharedPreferences preferences = getSharedPreferences("credentials", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("user", username);
                editor.commit();
                // Se inicia la pantalla de menu y se cierra el registro y el login
                Intent intent = new Intent(Register.this, HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                outMessage = mapOutput.get("message");
            } else
                outMessage = mapOutput.get("error");
            Toast.makeText(Register.this, outMessage, Toast.LENGTH_SHORT).show();
        }
    }
}
