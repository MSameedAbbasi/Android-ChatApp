package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher {

    private EditText traderid,server_ip,server_port;
    private EditText name;
    private EditText password;
    private Spinner SSL;
    private Button login;
    private int counter = 3;
    private String[] ssl_option={"True" , "False"};
    private  String ssl_choice;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        traderid = (EditText)findViewById(R.id.traderid);
        name = (EditText)findViewById(R.id.username);
        password = (EditText)findViewById(R.id.password);
        server_ip = (EditText)findViewById(R.id.server_ip);
        server_port = (EditText)findViewById(R.id.server_port);
        SSL = (Spinner) findViewById(R.id.SSL);
        login = (Button)findViewById(R.id.loginbtn);

        SSL.setOnItemSelectedListener(this);

        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ssl_option);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SSL.setAdapter(aa);

        server_ip.addTextChangedListener(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(traderid.getText().toString() ,name.getText().toString(), password.getText().toString());
            }
        });
    }

    private void validate(String traderid, String userName, String userPassword){
        String port= server_port.getText().toString();
        int port_int = Integer.parseInt(port);

        if(     traderid.equals("1111") &&
                userName.equals("Admin") &&
                userPassword.equals("1234") &&
                isValidIPAddress(server_ip.getText().toString()) &&
                isValidPort(port_int)    ){

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



    // Function to validate the IPs address.
    private static boolean isValidIPAddress(String ip)
    {

        // Regex for digit from 0 to 255.
        String zeroTo255
                = "(\\d{1,2}|(0|1)\\"
                + "d{2}|2[0-4]\\d|25[0-5])";

        // Regex for a digit from 0 to 255 and
        // followed by a dot, repeat 4 times.
        // this is the regex to validate an IP address.
        String regex
                = zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255 + "\\."
                + zeroTo255;

        // Compile the ReGex
        Pattern p = Pattern.compile(regex);

        // If the IP address is empty
        // return false
        if (ip == null) {
            return false;
        }

        // Pattern class contains matcher() method
        // to find matching between given IP address
        // and regular expression.
        Matcher m = p.matcher(ip);

        // Return if the IP address
        // matched the ReGex
        return m.matches();
    }
    private static boolean isValidPort(int port){
        if (port<10_000 && port>0){
            return true;
        }
        return false;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        ssl_choice = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}