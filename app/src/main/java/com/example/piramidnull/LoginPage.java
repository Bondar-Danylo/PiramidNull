package com.example.piramidnull;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class LoginPage extends AppCompatActivity {

    EditText username, password;
    Button loginBtn;

    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.login_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.loginPage), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        dbHelper = new DatabaseHelper(this);

        username = findViewById(R.id.username);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);

        loginBtn.setOnClickListener(view -> {
            if(username.getText().toString().isEmpty() || password.getText().toString().isEmpty()) {
                Toast.makeText(LoginPage.this, "Please fill both field!", Toast.LENGTH_SHORT).show();
            }

            if(dbHelper.checkUser(username.getText().toString(), password.getText().toString())) {
                startActivity(new Intent(LoginPage.this, MainPage.class));
            }else {
                Toast.makeText(LoginPage.this, "Error! \nPlease check your username or password", Toast.LENGTH_LONG).show();
            }
        });


        ImageView backBtn = findViewById(R.id.backBtn);
        backBtn.setOnClickListener(view -> startActivity(new Intent(LoginPage.this, LandingPage.class)));
    }
}