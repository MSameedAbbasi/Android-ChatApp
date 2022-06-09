package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.app.slice.Slice;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
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
import java.lang.reflect.Array;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    String hostname = "192.168.0.57";
    int port = Integer.parseInt("12000");
    static String user_name = "Andy2";
    EditText chat_box;
    Socket socket ;
    OutputStream output;
    PrintWriter writer;
    BufferedReader reader;
    InputStream input;
    public static String chat_partner_name;

    static MainActivity instance;

    //update_chatbox updateChatbox;

    /*boolean printlock;
    private String got_msg_update;*/


    ArrayList<String> active_users;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        instance=this;



        //printlock =false;


        /*try
        {socket = new Socket(hostname, port);
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);


        } catch (UnknownHostException ex) {
            System.out.println("Server not found: " + ex.getMessage());
        } catch (IOException ex) {
            System.out.println("I/O error hai: " + ex.getMessage());
        }*/
        /*
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            chat_partner_name = extras.getString("username");
            if (chat_partner_name!= null){
                getSupportActionBar().setTitle(chat_partner_name);
            }
            //The key argument here must match that used in the other activity
        }*/ //bundle
       /* try {
            socket = new Socket(hostname, port);
            output = socket.getOutputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        try  {
            socket = new Socket(hostname, port);
            output = socket.getOutputStream();
            writer = new PrintWriter(output, true);
            input = socket.getInputStream();

            /*InputStream input = socket.getInputStream();
            reader = new BufferedReader(new InputStreamReader(input));
           */
        } catch (UnknownHostException ex) {

            System.out.println("Server not found: " + ex.getMessage());

        } catch (IOException ex) {

            System.out.println("I/O error hai: " + ex.getMessage());
        }


        /*Thread msg_update_thread = new Thread(new  update_chatbox());
        msg_update_thread.start();*/


        chat_box =  findViewById(R.id.chat_box);
        listen_to_messages();
        register_name();

        ////// sockets /connections close ker k dekhnay hai output.close

        Log.d("oncreate", "onCreate: run: register name end");
        //listen_to_messages();
        Log.d("oncreate", "onCreate run: finished");
    }

    /*public static MainActivity getInstance() {
        return instance;
    }*/
    @Override
    protected void onResume()
    {
        super.onResume();
        on_openchat();
    }

    public void on_openchat(){
        getSupportActionBar().setTitle(chat_partner_name);
        chat_box.setText("");
    }
    Handler handler= new Handler();
    /*private void update_chatbox(){
        while (true){
            if (printlock){
                //chat_box.setText(chat_box.getText()+got_msg);
                chat_box.append(System.getProperty("line.separator") + got_msg_update);
                got_msg_update="";
            }
        }
    }*/
    /*private class update_chatbox implements Runnable {
        public void run() {
            while (true){
                if (printlock){
                    //chat_box.setText(chat_box.getText()+got_msg);
                    chat_box.append(System.getProperty("line.separator") + got_msg_update);
                    got_msg_update="";
                }
            }
        }

    }*/
    private void listen_to_messages() {
        Log.d("listen to msgs", "listen_to_messages:run: ");
        Thread msg_listener_thread = new Thread(new  message_listener());
        msg_listener_thread.start();
    }
    private class message_listener implements Runnable {
        public void run() {
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String charsRead = "";
                char[] buffer = new char[1000];
                while (true) {

                    //BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    Log.i("Message from Server 0", "run: 0 " );

                    charsRead = in.readLine();

                    //in.close();
                    String serverMessage = charsRead;//new String(buffer).substring(0, charsRead);
                    if (serverMessage != null) {
                        Log.i("Message from Server", "run: " + serverMessage.toString());
                        String got_msg = chk_msg(serverMessage);
                        Log.i("Message from Server 2", "run: " + got_msg);



                        ///updateChatbox = new update_chatbox();
                        //updateChatbox.execute(got_msg);

                        /*got_msg_update=got_msg;
                        printlock=true;*/

                        handler.post(new Runnable() {
                            public void run() {
                                //chat_box.setText(chat_box.getText()+got_msg);
                                chat_box.append(System.getProperty("line.separator") + got_msg);
                            }
                        });

                        //chat_box.append(System.getProperty("line.separator") + got_msg);
                        Log.i("Message from Server 3", "run: " + got_msg);

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*public void run() {

           //while (true)
            try{
                Log.d("msg listener", "run:0 "+ socket.isConnected());
                //InputStream input = socket.getInputStream();
                Log.d("msg listener", "run:1 "+ socket.isConnected());

               // BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                Log.d("text  msg  listener ", "run: msg listener 3//");
                String returned_msg = null;
                while(true){
                    if (reader.ready()


                    && (returned_msg=reader.readLine()) != null) {
                        Log.d("msg listener", "run:2 "+ socket.isConnected());
                        do{
                            returned_msg = reader.readLine();
                        }
                        while(returned_msg!= null);


                        returned_msg=reader.readLine();
                        Log.d("text msg listener","<<run: /msg listener3.5 "+ returned_msg);


                        //returned_msg = chk_msg(returned_msg);

                        Log.d("text msg listener", "run: msg listener 4 >>" + returned_msg);
                        chat_box.append( System.getProperty("line.separator") + returned_msg);
                        break;
                    }
                }
                Log.d("msg listener", "run: 3 "+ socket.isConnected());
                //input.close();
                Log.d("msg listener", "run: 4"+ socket.isConnected());
            }catch(Exception e){
                System.out.println("msg listener" + e.getMessage());
            }

        }*/
    }

/*
    private class update_chatbox extends AsyncTask <String,Void,Void>{


        protected void onProgressUpdate(String... got_msg) {
            chat_box.append(System.getProperty("line.separator") + got_msg);
        }

        @Override
        protected Void doInBackground(String... got_msg) {
            publishProgress();
            return null;
        }
    }*/


    private void register_name() {

        Thread name_thread = new Thread(new thread_send_name());
        name_thread.start();

    }
    class thread_send_name implements Runnable {

        public thread_send_name(){

        }
        public void run()
        {

            try  {

                Log.d("send name", "run: sock 1 "+ socket.isConnected());
                String text = "&$##*" + user_name;

                writer.println(text);
                writer.flush();
                output.flush();

                //InputStream input = socket.getInputStream();
                Log.d("send name", "run: sock2 "+ socket.isConnected());
                //reader = new BufferedReader(new InputStreamReader(input));
                Log.d("name send", "run: Rread text name3 /" + text);
                String returned_msg =""; //= reader.readLine();

                /*BufferedReader reader = new BufferedReader(new InputStreamReader(input));

                while(true) {
                    if (reader.ready()) {
                        Log.d("send name", "run: sock3 "+ socket.isConnected());
                        returned_msg = reader.readLine();

                        Log.d("name send", "run: read text name 3 /" + returned_msg);
                        returned_msg = chk_msg(returned_msg);

                        Log.d("name send", "run: read text name4 /" + returned_msg);
                        chat_box.append(returned_msg);
                        chat_box.append(System.getProperty("line.separator"));

                        //input.close();
                        break;

                    }
                    //Log.d("name send", "run: read text name 4.55 //" + returned_msg);
                }
                Log.d("send name", "run: sock4 "+ socket.isConnected());
                //listen_to_messages();
                Log.d("name send", "run: sock5 read text name5 /" + returned_msg);
                //output.close();
                */
            } catch (UnknownHostException ex) {

                System.out.println("Server not found: " + ex.getMessage());

            } catch (IOException ex) {

                System.out.println("I/O error hai: " + ex.getMessage());
            }

        }

       /* private String chk_msg(String msg){
            if (msg.startsWith("#"))
            {
                String[] clientset = msg.split("#",0);
                // list nikalni hai
                int lenght= clientset.length;
                return clientset[ lenght - 1];
            }
            return msg;
        }*/
    }


    String[] clientset;
    private String chk_msg(String msg){

        Log.d("AG",msg+"//chk_msg :>");
        if (msg.startsWith("#"))
        {
            clientset = msg.split("#");

            Log.d("AG",msg+"//chk_msg 2 :>");

            /*for (int count = 1; count<clientset.length -1; count++){
                if (active_users.indexOf(clientset[count])==-1){
                    active_users.add(clientset[count]);
                    Log.d("server activeuser", "chk_msg : 2.5 userlist"+ clientset[count]);
                }
            }*/
            // list nikalni hai
            int lenght= clientset.length;
            Log.d("AG",msg+"//chk_msg: 3 >/"+clientset[0] );
            return clientset[lenght-1];

        }
        return msg;
    }


    public void userlistbtn(View v2){


        Intent intent = new Intent(MainActivity.this,ClientList.class);
        if (clientset!= null) {
            String[] templist = Arrays.copyOfRange(clientset, 0, clientset.length - 1);
            intent.putExtra("username_list", templist /*active_users*/);
        }
        startActivity(intent);
        //this.finish();

    }


    public void sendbtn(View v1){

        String rcvrcode ="$%$";
        String send_to = "Friend";



        if (chat_partner_name!= null){
            send_to=chat_partner_name;
        }



        Log.d("sendbtn", "sendbtn: before thread");

        EditText message_box_text =  findViewById(R.id.message_box);
        //String text = txt1.getText().toString();

        for (int i =  send_to.length(); i < 10; i++)        //test it
        {
            send_to += " ";
        }

        String msg_to_be_sent = rcvrcode + send_to + user_name + ": " + message_box_text.getText().toString();
        Log.d("send btn message", "sendbtn run: >>>"+msg_to_be_sent);

        chat_box.setText(new StringBuilder().append(chat_box.getText().toString()).append(
                System.getProperty("line.separator")).append(message_box_text.getText().toString()).toString());


        Thread sender_thread = new Thread(new thread_message(msg_to_be_sent));
        sender_thread.start();
        Log.d("send btn", "run: left  read text //" );
        message_box_text.getText().clear();


        //sendbtnasync();
    }
    class thread_message implements Runnable {
        String text;
        EditText txt1;
        public thread_message(String msg){
            text = msg;
        }
        public void run()
        {
            try {

                writer.println(text);
                output.flush();
                Log.d("thread_message snd", "run: read text 1///" + text);


                /*BufferedReader reader = new BufferedReader(new InputStreamReader(input));
                Log.d("thread_message reader", "run: read text 3//" + text);
                String returned_msg="";
                while(true){
                    if (reader.ready()) {

                        returned_msg = reader.readLine();
                        Log.d("thread_message reader",returned_msg+"//chk_msg://");

                        //returned_msg = chk_msg(returned_msg);

                        Log.d("thread_message reader", "run: returned text msg 4 >>" + returned_msg);
                        chat_box.append( System.getProperty("line.separator") + returned_msg);
                        output.flush();
                        break;
                    }
                }*/
                //output.close();
                //input.close();
                //Log.d("thread_message reader", "run: returned msg 5 ");


            }catch(Exception e){
                System.out.println("text reader" + e.getMessage());
            }
        }
    }

}