package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.PushbackInputStream;
import java.io.StringReader;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;

public class MakeOrder extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private Spinner account, side,type,tif,destination;
    private EditText symbol,quantity,price;
    private List<String> ecn_name, sorted_ecn_name , ecn_short_name, account_nick, account_auto_num , ecn_order_types, type_list_option_name, type_list_option_value;
    private List<String> side_name, side_value, tif_name, tif_value;
    private  String order_account,order_side,order_type,order_tif,order_destination;
    private Button order_btn;
    private String xmlString,xmlstring1;
    String hostname = "192.168.0.57";
    int port = Integer.parseInt("12000");
    Socket socket ;
    OutputStream output;
    PrintWriter writer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            xmlstring1 = extras.getString("xmlfiledata");
            Log.d("xml bundle str", "onCreate: "+xmlstring1);
        }


        setTitle("OMEX Order");
        xmlString=null;
       // File file = new File( "LogonDetails-22062022.xml");

        ecn_name=new ArrayList<String>();
        sorted_ecn_name = new ArrayList<String>();
        ecn_short_name=new ArrayList<String>();
        ecn_order_types = new ArrayList<String>();
        type_list_option_name = new ArrayList<String>();
        type_list_option_value = new ArrayList<String>();
        account_nick=new ArrayList<String>();
        account_auto_num=new ArrayList<String>();

        side_name=new ArrayList<String>();
        side_value=new ArrayList<String>();
        tif_name=new ArrayList<String>();
        tif_value=new ArrayList<String>();

        side_name.add("BUY"); side_value.add("1");
        side_name.add("SELL"); side_value.add("2");

        tif_name.add("Day"); tif_value.add("0");
        tif_name.add("GTC"); tif_value.add("1");
        tif_name.add("OPG"); tif_value.add("2");
        tif_name.add("IOC"); tif_value.add("3");
        tif_name.add("FOK"); tif_value.add("4");
        tif_name.add("GTX"); tif_value.add("5");
        tif_name.add("CLO"); tif_value.add("7");

        //getxmlfile();
        readdatafromxml();


        symbol = (EditText)findViewById(R.id.symbol);
        //symbol.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
        account = (Spinner) findViewById(R.id.account);
        side= (Spinner) findViewById(R.id.side);
        quantity = (EditText)findViewById(R.id.quantity);
        price= (EditText)findViewById(R.id.price);
        type= (Spinner) findViewById(R.id.type);
        tif = (Spinner) findViewById(R.id.tif);
        destination = (Spinner) findViewById(R.id.destination);
        order_btn = (Button)findViewById(R.id.send_order);

        account.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item,account_nick));
        account.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_account=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        destination.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item, sorted_ecn_name));
        destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_destination=parent.getItemAtPosition(position).toString();
                System.out.println("ORDERdestination  ---" +order_destination );
                type_list_option_name.clear();
                type_list_option_value.clear();
                decodeliststring( ecn_order_types.get(ecn_name.indexOf(order_destination)));
                for(int i = 0; i <ecn_name.size(); i++){
                    System.out.println("ECN NAME     ---" +ecn_name.get(i) );
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        side.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item,side_name));
        side.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_side=parent.getItemAtPosition(position).toString();
                    /*if (order_side.equals("BUY")){
                        order_btn.setBackgroundColor(Color.GREEN);
                    }
                    if (order_side.equals("SELL")){
                        order_btn.setBackgroundColor(Color.RED);
                    }*/
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        tif.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item,tif_name));
        tif.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_tif=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void readdatafromxml() {
        try {
            // Get Document
            //Document document = builder.parse(new File("trader.xml"));
            //String readxml = logonread();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());
            String filename="logon" + currentDate +".txt";
            AssetManager assetManager = getAssets();
            //InputStream is = assetManager.open(filename);
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(new InputSource(new StringReader(LoginPage.xmlString)));
            //Document document = dBuilder.parse(filename);
            document.getDocumentElement().normalize();


            // Get all the element by the tag name
            NodeList tablelist = document.getElementsByTagName("Table1");
            Node table;
            for(int i = 0; i <tablelist.getLength(); i++) {
                table = tablelist.item(i);
                System.out.println("tableitem -----" + i );
                if(table.getNodeType() == Node.ELEMENT_NODE) {

                    Element tableelement = (Element) table;
                    System.out.println("  TAble Name: ///" + tableelement.getNodeName());

                    NodeList tabledetails =  table.getChildNodes();

                    List<String> tagname = new ArrayList<String>(),textcontext=new ArrayList<String>();
                    for(int j = 0; j < tabledetails.getLength(); j++){
                        Node detail = tabledetails.item(j);

                        if(detail.getNodeType() == Node.ELEMENT_NODE) {
                            Element detailElement = (Element) detail;

                            tagname.add(detailElement.getTagName());
                            textcontext.add(detailElement.getTextContent());
                            System.out.println("     " + detailElement.getTagName() +"===="+detailElement.getTextContent());

                            //System.out.println("     " + detailElement.getTagName() +"___"+detailElement.getTextContent());

                        }
                    }
                    System.out.println("\n \n ----------------");
                    if (textcontext.get(tagname.indexOf("ecn_is_for_stock")).equals("1")) {
                        ecn_name.add(textcontext.get(tagname.indexOf("ecn_name")));
                        sorted_ecn_name.add(textcontext.get(tagname.indexOf("ecn_name")));
                        ecn_short_name.add(textcontext.get(tagname.indexOf("ecn_short_name")));
                        ecn_order_types.add(textcontext.get(tagname.indexOf("ecn_order_types")));
                        System.out.println("     " + ecn_name.size() + "<==>" + ecn_short_name.size());

                    }

                }
            }

            NodeList tablelist2 = document.getElementsByTagName("Table2");
            Node table2;
            for(int i = 0; i <tablelist2.getLength(); i++) {
                table2 = tablelist2.item(i);
                System.out.println("tableitem ---" + i );
                if(table2.getNodeType() == Node.ELEMENT_NODE) {

                    Element tableelement = (Element) table2;
                    System.out.println("  TAble Name: ___" + tableelement.getNodeName());

                    NodeList tabledetails =  table2.getChildNodes();

                    //List<String> tagname = new ArrayList<String>(),textcontext=new ArrayList<String>();
                    for(int j = 0; j < tabledetails.getLength(); j++){
                        Node detail = tabledetails.item(j);

                        if(detail.getNodeType() == Node.ELEMENT_NODE) {
                            Element detailElement = (Element) detail;

                            if (detailElement.getTagName().equals("account_nick")){
                                account_nick.add(detailElement.getTextContent());
                            }
                            if (detailElement.getTagName().equals("account_auto_num")){
                                account_auto_num.add(detailElement.getTextContent());
                            }

                            System.out.println("     " + detailElement.getTagName() +">>>"+detailElement.getTextContent());

                        }
                    }
                    System.out.println("\n \n ----------------");


                }
            }

            Collections.sort(sorted_ecn_name);





        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private String logonread(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String currentDate = sdf.format(new Date());
        String filename="logon" + currentDate +".txt";
        try{
            FileInputStream fIn = openFileInput(filename);
            InputStreamReader isr = new InputStreamReader(fIn);

            /* Prepare a char-Array that will
             * hold the chars we read back in. */
            char[] inputBuffer = new char[isr.toString().length()];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);


            Log.i("File Rlogon---", "success =>" + readString);
            return readString;
        } catch (IOException ioe)
        {ioe.printStackTrace();}
        return null;
    }
    private  void decodeliststring(String list){
        String[] listoptions , temp;

        listoptions = list.split(",");
        for(int i = 0; i <listoptions.length; i++){
            temp = listoptions[i].split("/");
            type_list_option_name.add(temp[0]); type_list_option_value.add(temp[1]);
        }
        type.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item, type_list_option_name));
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_type=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }
    public void send_order(View view){      //send button
        String symbol_str,quantity_str, price_str;
        String account_str, side_str, type_str, tif_str, destination_str;
        String  final_order_string;

        symbol_str = symbol.getText().toString();
        quantity_str = quantity.getText().toString();
        price_str = price.getText().toString();

        type_str= type_list_option_value.get(type_list_option_name.indexOf(order_type));
        account_str= account_auto_num.get(account_nick.indexOf(order_account));
        side_str=side_value.get(side_name.indexOf(order_side));
        tif_str= tif_value.get(tif_name.indexOf(order_tif));
        destination_str = ecn_short_name.get(ecn_name.indexOf(order_destination));


        final_order_string= "1="+account_str+  "#54="+side_str+  "#55="+symbol_str+
                "#38="+quantity_str  +"#44="+price_str+  "#40="+type_str+  "#59="+ tif_str+
                "#1010="+destination_str+  "#553=DEMO_MASTER"+  "#1070=master-user1"+
                "#58=MOBILE_ORDER_SENDING_APP#"+"||"+LoginPage.ip +","+LoginPage.port+","+LoginPage.ssl_choice;

        System.out.println("\n     ----order sent string----  \n"+final_order_string);
        Thread sender_thread = new Thread(new  thread_message(  final_order_string));
        sender_thread.start();
        Toast.makeText(getApplicationContext(),"Order Sent Successful",Toast.LENGTH_SHORT).show();
    }
    class thread_message implements Runnable {
        String text;

        public thread_message(String msg){
            text = msg;
        }
        public void run()
        {
            try {
                //socket = new Socket(hostname, port);
                socket = MainActivity.socket;
                output = socket.getOutputStream();
                writer = new PrintWriter(output, true);
                writer.println(text);
                output.flush();
                Log.d("thread_order sent", "run: order text>>>" + text);
                //Toast.makeText(getApplicationContext(),"Order Sent Successful",Toast.LENGTH_SHORT).show();
            }catch(Exception e){
                System.out.println("order sent" + e.getMessage());
            }
        }
    }

    /*private void getxmlfile() {
        String login = "DEMO_MASTER";
        String username= "master-user1";
        String password= "testtrader";
        new ExecuteTask().execute(login, password,username );

    }*/
   /* class ExecuteTask extends AsyncTask<String, Integer, String> {

        @Override
        protected String doInBackground(String... params) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
            String currentDate = sdf.format(new Date());

            String filename="logon" + currentDate +".txt";
            String res = PostData(params);

            Log.d("logon file", "doInBackground: "+ filename);
            try {
                // catches IOException below
                //final String TESTSTRING = new String("Hello Android");

                *//* We have to use the openFileOutput()-method
                 * the ActivityContext provides, to
                 * protect your file from others and
                 * This is done for security-reasons.
                 * We chose MODE_WORLD_READABLE, because
                 *  we have nothing to hide in our file *//*
                FileOutputStream fOut = openFileOutput(filename, MODE_PRIVATE);
                OutputStreamWriter osw = new OutputStreamWriter(fOut);

                osw.write(res);

                osw.flush();
                osw.close();

                Log.i("File Reading stuff", "success " );

            } catch (IOException ioe)
            {ioe.printStackTrace();}
            return res;
        }

        @Override
        protected void onPostExecute(String result) {

            System.out.println("         XML\n" + result );
            xmlString=result;
            readdatafromxml();
        }

    }*/

   /* public String PostData(String[] valuse) {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://204.93.141.72/oms/OMSLogin.asmx/GetOMSLogin?loginId=DEMO_MASTER&pass=testtrader&username=master-user1");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("loginId", valuse[0]));
            list.add(new BasicNameValuePair("pass",valuse[1]));
            list.add(new BasicNameValuePair("username",valuse[2]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=  httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            s= readResponse(httpResponse);

        }
        catch(Exception exception)  {}
        return s;


    }*/
   /* public String readResponse(HttpResponse res) {
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

        }
        return return_text;

    }*/

    /*private static InputStream checkForUtf8BOMAndDiscardIfAny(InputStream inputStream) throws IOException {
        PushbackInputStream pushbackInputStream = new PushbackInputStream(new BufferedInputStream(inputStream), 3);
        byte[] bom = new byte[3];
        if (pushbackInputStream.read(bom) != -1) {
            if (!(bom[0] == (byte) 0xEF && bom[1] == (byte) 0xBB && bom[2] == (byte) 0xBF)) {
                pushbackInputStream.unread(bom);
                System.out.println("   BOM<--> ");
            }
        }
        return pushbackInputStream; }*/

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
