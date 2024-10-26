package com.example.projectovd;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;  // Add this import
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText username, password;
    Button loginButton;
    DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginButton = findViewById(R.id.loginButton);
        databaseHelper = new DatabaseHelper(this);

        // Insert test data (you can remove this later)
        databaseHelper.insertUser("admin", "123");

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = username.getText().toString().trim();
                String pass = password.getText().toString().trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Por favor preencha todos os campos", Toast.LENGTH_SHORT).show();
                } else {
                    boolean result = databaseHelper.checkUser(user, pass);
                    if (result) {
                        Toast.makeText(MainActivity.this, "Sucesso", Toast.LENGTH_SHORT).show();

                        // Navigate to HomeActivity
                        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Dados errados", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
