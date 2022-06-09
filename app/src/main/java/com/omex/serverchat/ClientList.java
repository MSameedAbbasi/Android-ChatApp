package com.omex.serverchat;

import static android.content.ContentValues.TAG;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ClientList extends AppCompatActivity {

    ListView listView;
    /*ArrayList<String>*/ String[] userlist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_client_list);

        listView = (ListView) findViewById(R.id.client_listview);
        Log.d("cli list", "onCreate: if 0");
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userlist = extras.getStringArray("username_list");
            Log.d("cli list", "onCreate: if 0");
            //The key argument here must match that used in the other activity
        }
        ArrayList<String> arrayList = new ArrayList<>();

        for (int i=0 ; i< userlist.length;i++){
            if (arrayList.contains(userlist[i]) || userlist[i].equals(MainActivity.user_name) || userlist[i].equals("")){

            }else{
                arrayList.add(userlist[i]);
            }
        }

        arrayList.remove("");
        arrayList.remove("\n");
        arrayList.remove(null);

        /*
        arrayList.add("Sameed");
        arrayList.add("Kashif");
        arrayList.add("Daniyal");
*/
        ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_activated_1,arrayList);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                MainActivity.chat_partner_name= arrayList.get(position);
                /*MainActivity a = MainActivity.getInstance();
                a.on_openchat();*/
                finish();
                /*
                Intent intent =new Intent(ClientList.this,MainActivity.class);
                intent.putExtra("username", arrayList.get(position));
                startActivity(intent);*/

            }
        });

    }
}