package com.omex.serverchat;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
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
    private EditText quantity,price;
    private List<String> ecn_name, ecn_short_name,  ecn_order_types , account_nick, account_auto_num;
    private List<String> side_name, side_value, tif_name, tif_value;
    private  String order_account,order_side,order_type,order_tif,order_destination;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_order);

       // File file = new File( "LogonDetails-22062022.xml");

        ecn_name=new ArrayList<String>();
        ecn_short_name=new ArrayList<String>();
        ecn_order_types = new ArrayList<String>();
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


        try {
            // Get Document
            //Document document = builder.parse(new File("trader.xml"));

            AssetManager assetManager = getAssets();
            InputStream is = assetManager.open("trader.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document document = dBuilder.parse(is);

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


                    //if (detailElement.getTagName().equals("ecn_is_for_stock") && detailElement.getTextContent().equals("1")){}
                    //if (tagname.contains("ecn_is_for_stock")) {
                        if (textcontext.get(tagname.indexOf("ecn_is_for_stock")).equals("1")) {
                            ecn_name.add(textcontext.get(tagname.indexOf("ecn_name")));
                            ecn_short_name.add(textcontext.get(tagname.indexOf("ecn_short_name")));
                            ecn_order_types.add(textcontext.get(tagname.indexOf("ecn_order_types")));
                            System.out.println("     " + ecn_name.size() + "<==>" + ecn_short_name.size());

                        }
                    //}
                }
            }
 /*for(int i = 0; i <ecn_name.size(); i++){
                System.out.println("         ecn_name-------->" + ecn_name.get(i) );
                System.out.println("         ecn_short_name-->" + ecn_short_name.get(i) );
            }  //for debugging*/


            NodeList tablelist2 = document.getElementsByTagName("Table2");
            Node table2;
            for(int i = 0; i <tablelist2.getLength(); i++) {
                table2 = tablelist2.item(i);
                System.out.println("tableitem ---" + i );
                if(table2.getNodeType() == Node.ELEMENT_NODE) {

                    Element tableelement = (Element) table2;
                    System.out.println("  TAble Name: ___" + tableelement.getNodeName());

                    NodeList tabledetails =  table2.getChildNodes();

                    List<String> tagname = new ArrayList<String>(),textcontext=new ArrayList<String>();
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
                            //textcontext.add(detailElement.getTextContent());
                            System.out.println("     " + detailElement.getTagName() +">>>"+detailElement.getTextContent());

                        }
                    }
                    System.out.println("\n \n ----------------");



                    //account_auto_num.add(textcontext.get(tagname.indexOf("account_auto_num")));
                    //System.out.println("     " + account_nick.size() + "<==>" + account_auto_num.size());

                }
            }

        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        account = (Spinner) findViewById(R.id.account);
        side= (Spinner) findViewById(R.id.side);
        quantity = (EditText)findViewById(R.id.quantity);
        price= (EditText)findViewById(R.id.price);
        type= (Spinner) findViewById(R.id.type);
        tif = (Spinner) findViewById(R.id.tif);
        destination = (Spinner) findViewById(R.id.destination);


        getxmlfile();




        destination.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item,ecn_name));
        destination.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_destination=parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

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

        type.setAdapter(new ArrayAdapter<>(MakeOrder.this, android.R.layout.simple_spinner_dropdown_item,ecn_order_types));
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                order_type=parent.getItemAtPosition(position).toString();
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

    private void getxmlfile() {
        String login = "DEMO_MASTER";
        String username= "master-user1";
        String password= "testtrader";
        AsyncTask<String, Integer, String> res = new ExecuteTask().execute(login,password ,username);
        System.out.println("  --->>"+res);
    }
    class ExecuteTask extends AsyncTask<String, Integer, String>
    {

        @Override
        protected String doInBackground(String... params) {

            String res=PostData(params);

            return res;
        }

        @Override
        protected void onPostExecute(String result) {
            //progressBar.setVisibility(View.GONE);
            //progess_msz.setVisibility(View.GONE);
            Toast.makeText(getApplicationContext(), result, Toast.LENGTH_SHORT).show();
        }

    }

    public String PostData(String[] valuse) {
        String s="";
        try
        {
            HttpClient httpClient=new DefaultHttpClient();
            HttpPost httpPost=new HttpPost("http://204.93.141.72/oms/OMSLogin.asmx?op=GetOMSLogin");

            List<NameValuePair> list=new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("loginId:", valuse[0]));
            list.add(new BasicNameValuePair("pass",valuse[1]));
            list.add(new BasicNameValuePair("username",valuse[2]));
            httpPost.setEntity(new UrlEncodedFormEntity(list));
            HttpResponse httpResponse=  httpClient.execute(httpPost);

            HttpEntity httpEntity=httpResponse.getEntity();
            s= readResponse(httpResponse);

        }
        catch(Exception exception)  {}
        return s;


    }
    public String readResponse(HttpResponse res) {
        InputStream is=null;
        String return_text="";
        try {
            is=res.getEntity().getContent();
            BufferedReader bufferedReader=new BufferedReader(new InputStreamReader(is));
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

    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
