package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PushbackInputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class LoginPage extends AppCompatActivity implements AdapterView.OnItemSelectedListener, TextWatcher {

    private EditText traderid,server_ip,server_port;
    private EditText name;
    private EditText password;
    private Spinner SSL;
    private Button login;
    private int counter = 3;
    private String[] ssl_option={"True" , "False"};
    public  static   String ssl_choice,ip,port;
    public static String xmlString;
    Intent intent1;
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

        SSL.setAdapter(new ArrayAdapter<>(LoginPage.this, android.R.layout.simple_spinner_dropdown_item,ssl_option));
        SSL.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ssl_choice=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

       /* SSL.setOnItemSelectedListener(this);
        ArrayAdapter aa = new ArrayAdapter(this,android.R.layout.simple_spinner_item,ssl_option);
        aa.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SSL.setAdapter(aa);
*/

        server_ip.addTextChangedListener(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate(traderid.getText().toString() ,name.getText().toString(), password.getText().toString());
            }
        });
    }
    private static Handler myHandler;
    private void validate(String traderid, String userName, String userPassword){
        port= server_port.getText().toString();
        int port_int = Integer.parseInt(port);
        ip=server_ip.getText().toString();
        if(   isValidIPAddress(ip) && isValidPort(port_int) ){

            getxmlfile(traderid,userName,userPassword,ip,port,ssl_choice);


            myHandler = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    Log.d("logon---text 1", "doInd: "+ xmlString);
                    Intent intent1 = new Intent(LoginPage.this, MainActivity.class);
                    intent1.putExtra("xmlfiledata",xmlString);
                    startActivity(intent1);
                }
            };
            /*while(true){
                if(xmlString.length()>1){
                    break;
                }
            }
            Intent intent1 = new Intent(LoginPage.this, MainActivity.class);
            intent1.putExtra("xmlfiledata",xmlString);
            startActivity(intent1);*/

        }else{
            counter--;

            Toast.makeText(getApplicationContext(), "Wrong Credentials",Toast.LENGTH_SHORT).show();
            //Info.setText("No of attempts remaining: " + String.valueOf(counter));

            if(counter == 0){
                login.setEnabled(false);
            }
        }
    }
    private void getxmlfile(String login, String username, String password,String ip, String port, String ssl_choice) {
        /*String login = "DEMO_MASTER";
        String username= "master-user1";
        String password= "testtrader";*/

         new xmlExecuteTask().execute(login,username, password ,ip,port,ssl_choice);
        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());
        String filename="logon" + currentDate +".txt";
        String res ="";
        res = PostData(login ,username,password);
        if (res.isEmpty() ){
            Log.d("logon failed", "doInBackground: "+ filename);
        }
        else {
            Log.d("logon file", "doInBackground: " + filename+res);

            try {
                FileOutputStream fOut = openFileOutput(filename, MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(res);

                osw.flush();
                osw.close();

                Log.i("File Reading stuff", "success ");

            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
            try {
                FileOutputStream fOut = openFileOutput("Credentials", MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);
                osw.write(params[0]+"\n"+params[1]+"\n"+params[2]  );

                osw.flush();
                osw.close();

                Log.i("File credentials", "success "+params[0]+"\n"+params[1]+"\n"+params[2]  );

            } catch (IOException ioe)
            {ioe.printStackTrace();}

        }
        return res;*/

    }
    class xmlExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());
            String filename="logon" + currentDate +".txt";
            String res ="";
            res = PostData(params);
            if (res.isEmpty() ){
                Log.d("logon failed", "doInBackground: "+ filename);
            }
            else {
                Log.d("logon file", "doInBackground: " + filename+res);

                try {
                    FileOutputStream fOut = openFileOutput(filename, MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write(res);

                    osw.flush();
                    osw.close();

                    Log.i("File Reading stuff", "success ");

                } catch (IOException ioe) {
                    ioe.printStackTrace();
                }
                try {
                    FileOutputStream fOut = openFileOutput("Credentials", MODE_PRIVATE);
                    OutputStreamWriter osw = new OutputStreamWriter(fOut);
                    osw.write(params[0]+"\n"+params[1]+"\n"+params[2] +"\n"+params[3] +"\n"+params[4] +"\n"+params[5]  );

                    osw.flush();
                    osw.close();

                    Log.i("File credentials", "success "+params[0]+"\n"+params[1]+"\n"+params[2] +"\n"+params[3] +"\n"+params[4] +"\n"+params[5]   );

                } catch (IOException ioe)
                {ioe.printStackTrace();}

            }
            myHandler.sendEmptyMessage(0);
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            System.out.println("     XML\n" + result );
            xmlString=result;
            //readdatafromxml();

        }

    }
    public String PostData(String[] valuse) {
        String s="";
        String login= valuse[0];
        String username= valuse[1];
        String password=valuse[2];

        /*String login = "DEMO_MASTER";
        String username= "master-user1";
        String password= "testtrader";*/

        Log.i("File credentials 3", "success "+login+username+password  );

        /*String ip=valuse[3];
        String port=valuse[4];
        String ssl=valuse[5];*/
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://204.93.141.72/oms/OMSLogin.asmx/GetOMSLogin?loginId="+login+"&pass="+password+"&username="+username);

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("loginId", valuse[0]));
            list.add(new BasicNameValuePair("pass",valuse[2]));
            list.add(new BasicNameValuePair("username",valuse[1]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=  httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            s= readResponse(httpResponse);

        }
        catch(Exception exception)  {Log.i("File credentials 4", "success "+ exception );}
        return s;
    }
    public String readResponse(HttpResponse res) {
        InputStream is,ins=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            ins = checkForUtf8BOMAndDiscardIfAny(is);
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(ins));
            String line="";

            StringBuffer sb=new StringBuffer();
            while ((line=bufferedReader.readLine())!=null)
            {
                sb.append(line);
            }

            return_text=sb.toString();

        } catch (Exception e)
        {
            Log.i("File credentials 5", "su "+e  );
        }

        return return_text;

    }
    private static InputStream checkForUtf8BOMAndDiscardIfAny(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bom = new byte[3];
        if (pushbackInputStream.read(bom) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread(bom);
                System.out.println("   BOM<--> ");
            }
        }
        return pushbackInputStream; }


    // Function to validate the IPs address.
    private static boolean isValidIPAddress(String ip){

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