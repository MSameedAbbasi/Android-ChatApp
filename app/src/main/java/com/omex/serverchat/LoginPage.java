package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginPage extends AppCompatActivity {

    private EditText traderid;
    private EditText name;
    private EditText password;
    private Button login;
    private int counter = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        traderid = (EditText)findViewById(R.id.traderid);
        name = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        login = (Button)findViewById(R.id.loginbtn);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(traderid.getText().toString() ,name.getText().toString(), password.getText().toString());
            }
        });
    }
    private void validate(String traderid, String userName, String userPassword){
        if(traderid.equals("1111") && userName.equals("Admin") && userPassword.equals("1234")){
            Intent intent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(intent);
        }else{
            counter--;

            Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
            //Info.setText("No of attempts remaining: " + String.valueOf(counter));

            if(counter == 0){
                login.setEnabled(false);
            }
        }
    }
}