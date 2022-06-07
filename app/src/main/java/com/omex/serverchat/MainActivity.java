package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.BufferedReader;
import java.io.Console;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

        StrictMode.setThreadPolicy(policy);

        EditText mEdit = (EditText) findViewById(R.id.editTextTextPersonName);
        EditText mEdit1 = (EditText) findViewById(R.id.editTextTextPersonName2);
        Button yourButton = (Button) findViewById(R.id.button);
        Log.d("crash1" ,"onCreate: crash");
    }


    public void sendbtn(View v1){
        Log.d("sendbtn", "sendbtn: before thread");
        Thread object
                = new Thread(new MultithreadingDemo());
        object.start();
        Log.d("sendbtn", "sendbtn: AFTER-- thread");
        //sendbtnasync();
    }
    void sendbtnasync(){
        String args= findViewById(R.id.editTextTextPersonName).toString();
        String hostname = "192.168.0.57";
        int port = Integer.parseInt("12000");

        try (Socket socket = new Socket(hostname, port)) {

            OutputStream output = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(output, true);



            Console console = System.console();
            String text;

            do {
                text = findViewById(R.id.editTextTextPersonName).toString();

                writer.println(text);

                InputStream input = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                String returnedmsg = reader.readLine();

                System.out.println(returnedmsg);
                EditText editText = (EditText)findViewById(R.id.editTextTextPersonName2);
                editText.setText("Username");//set the text in edit text

            } while (!text.equals("bye"));

            socket.close();

        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error: " + ex.getMessage());
        }
    }


    class MultithreadingDemo implements Runnable {
        public void run()
        {
            String hostname = "192.168.0.57";
            int port = Integer.parseInt("12000");

            try (Socket socket = new Socket(hostname, port)) {

                OutputStream output = socket.getOutputStream();
                PrintWriter writer = new PrintWriter(output, true);



                Console console = System.console();
                String text;
                EditText txt1;

                do {
                    text = findViewById(R.id.editTextTextPersonName).toString();
                    txt1 =  findViewById(R.id.editTextTextPersonName).toString();

                    writer.println(text);
                    Log.d("text reader", "run: read text///"+text);

                    InputStream input = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                    String returnedmsg = reader.readLine();

                    System.out.println(returnedmsg);
                    EditText editText = (EditText)findViewById(R.id.editTextTextPersonName2);
                    editText.setText("Username");//set the text in edit text

                } while (!text.equals("bye"));

                socket.close();

            } catch (UnknownHostException ex) {

                System.out.println("Server not found hai: " + ex.getMessage());

            } catch (IOException ex) {

                System.out.println("I/O error hai: " + ex.getMessage());
            }
        }
    }



}