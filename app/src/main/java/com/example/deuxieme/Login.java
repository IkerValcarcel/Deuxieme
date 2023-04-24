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

public class Login extends AppCompatActivity implements View.OnClickListener{

    private UsersDB db;
    private EditText usernameET, passwordET;
    private Button loginBTN, registerBTN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        db = new UsersDB();

        // Obtenemos los elementos de la interfaz
        usernameET = findViewById(R.id.username);
        passwordET = findViewById(R.id.password);
        loginBTN = findViewById(R.id.login);
        registerBTN = findViewById(R.id.register);

        // Se definenen los listeners en los botones
        loginBTN.setOnClickListener(this);
        registerBTN.setOnClickListener(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // Call finish to close the activity
        if ((intent.getFlags() & Intent.FLAG_ACTIVITY_CLEAR_TOP) != 0) {
            finish();
        }

    }

    @Override
    public void onClick(View view){
        switch (view.getId()) {
            case R.id.login:
                // Se clica el boton de login
                try { login(); }
                catch (Exception e) { e.printStackTrace(); }
                break;
            case R.id.register:
                // Se clica el boton de registro
                register();
                break;
        }
    }

    protected void login() throws Exception {
        LoginAsyncTask task = new LoginAsyncTask(this);
        String inUsername = usernameET.getText().toString().trim();
        String inPassword = passwordET.getText().toString().trim();
        task.execute(inUsername, inPassword);

    }

    protected void register() {
        // Nos movemos al activity del registro
        Intent intent = new Intent(this, Register.class);
        startActivity(intent);
    }

    public class LoginAsyncTask extends AsyncTask<String, Void, String> {

        private final Context context;
        private final UsersDB db;

        public LoginAsyncTask(Context context) {
            this.context = context;
            db = new UsersDB();
        }

        @Override
        protected String doInBackground(String... params) {
            String inUsername = params[0];
            String inPassword = params[1];
            try {
                return db.login(inUsername, inPassword);
            } catch (Exception e) {
                e.printStackTrace();
                return "Error al conectarse a la base de datos";
            }
        }

        @Override
        protected void onPostExecute(String loginOutput) {
            // Si el login ha sido exitoso damos la sesion como iniciada
            HashMap<String, String> mapOutput = Utilities.parseJson(loginOutput);
            String outMessage;
            if (loginOutput.contains("Login exitoso")) {
                SharedPreferences preferences = context.getSharedPreferences("credentials", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                String inUsername = mapOutput.get("username");
                editor.putString("user", inUsername);
                editor.apply();
                Intent intent = new Intent(context, HomeScreen.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                context.startActivity(intent);
                outMessage = mapOutput.get("message");
            } else {
                outMessage = mapOutput.get("error");
            }
            Toast.makeText(context, outMessage, Toast.LENGTH_SHORT).show();
        }
    }
}