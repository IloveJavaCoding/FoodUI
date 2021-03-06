package com.example.delicious.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.delicious.R;
import com.example.delicious.Self_class.MySingleton;
import com.example.delicious.Self_class.Controls;
import com.example.delicious.Self_class.Shop_Info;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Loading_page extends AppCompatActivity {
    private TextView show;
    private int statue = 1;
    Shop_Info[] sh;
    Controls Lock;

    private String url;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);

        Init();
        Jump();
    }
    private void Init(){
        url = Lock.getRoot() + "get_all_shop.php";
        Get_all_shop(url);
        show = (TextView)findViewById(R.id.tv);
    }

    private void Jump(){
        if(statue==1){
            new Thread(){
                @Override
                public void run() {
                    super.run();
                    try {
                        Thread.sleep(500);
                        Intent intent = new Intent(Loading_page.this, Homepage.class);

                        Bundle bundle = new Bundle();
                        bundle.putSerializable("shops",sh);
                        intent.putExtras(bundle);

                        startActivityForResult(intent,0);
                        finish();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }.start();
        }else{
            show.setText("No Internet!!!");
        }
    }

    private void Get_all_shop(String path){
        final JSONObject request = null;
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, path, request,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray acc;
                try {
                    if (response.getInt("state") ==1) {
                        acc = response.getJSONArray("shop");
                        sh = new Shop_Info[acc.length()];
                        for(int i=0;i<acc.length();i++){
                            JSONObject c = acc.getJSONObject(i);

                            sh[i] = new Shop_Info();
                            String id = c.getString("id");
                            String shop_name = c.getString("shop_name");
                            String genre = c.getString("genre");
                            double rate =  c.getDouble("rate");
                            String open_time = c.getString("open_time");
                            String logo_link = c.getString("logo_link");
                            String bg_link = c.getString("bg_link");
                            String genre_link = c.getString("genre_link");

                            sh[i].setId(id);
                            sh[i].setShop_name(shop_name);
                            sh[i].setGenre(genre);
                            sh[i].setRate(rate);
                            sh[i].setOpen_time(open_time);
                            sh[i].setLogo_link(logo_link);
                            sh[i].setBg_link(bg_link);
                            sh[i].setGenre_link(genre_link);
                        }
                    }
                    else {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                statue = 0;
                Toast.makeText(getApplicationContext(),"No internet",Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }
}
