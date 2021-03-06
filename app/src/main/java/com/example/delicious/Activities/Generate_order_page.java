package com.example.delicious.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.delicious.R;
import com.example.delicious.Self_class.Controls;
import com.example.delicious.Self_class.Item_info;
import com.example.delicious.Self_class.MySingleton;
import com.example.delicious.Self_class.Order_record;
import com.example.delicious.Self_class.SessionHandler;
import com.example.delicious.Self_class.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;

public class Generate_order_page extends AppCompatActivity {
    private Button Bhome;
    private TextView Tordernum;
    private SessionHandler session;
    private User user;
    private Item_info[] items;
    private String order_num;
    private Order_record[] orders;
    private List<String> ordernumber;

    Controls Lock;
    private String path;
    private String path2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_state);

        Get_data();
        Init();
        SetListener();
        Create();
    }

    private void Get_data(){
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        items  = (Item_info[])bundle.getSerializable("foods");//number of the item > 0;
        orders = (Order_record[])bundle.getSerializable("orders");
        if(orders.length>0){
            ordernumber = Get_ordernum_list(orders);
        }
    }

    private void Init(){
        path = Lock.getRoot() + "create_order.php";
        path2 = Lock.getRoot() + "create_orders.php";

        session = new SessionHandler(Generate_order_page.this);
        user = session.getUserDetails();
        Bhome = (Button)findViewById(R.id.Btohome);
        Tordernum = (TextView)findViewById(R.id.Tordernum);

        order_num = Generate_order_num();//get a random order number;
        if(orders.length>0){
            while(ordernumber.contains(order_num)){
                order_num = Generate_order_num();//get a random order number;
            }
        }
        Tordernum.setText(order_num);
    }

    private void Create(){
        //Create_orders(user.getUsername(),order_num, items, path2);
        new Thread(){
            @Override
            public void run() {
                super.run();
                for(int i=0; i<items.length; i++){
                    Create_Order(user.getUsername(),order_num,items[i].getShop_name(),items[i].getIteam_name(),items[i].getPrice(),items[i].getNumber(),items[i].getLink(),path);
                }
            }
        }.start();
    }

    private String Generate_order_num() {
        String number = "";
        String head = items[0].getShop_name().substring(0,4);
        int num = MyRandom(1,999999);
        String tail = "";
        if(num<10){
            tail = "00000" + num;
        }else if(num<100){
            tail = "0000" + num;
        }else if(num<1000){
            tail = "000" + num;
        }else if(num<10000){
            tail = "00" + num;
        }else if(num<100000){
            tail = "0" + num;
        }else{
            tail = Integer.toString(num);
        }
        number = head + tail;

        return number;
    }

    private int MyRandom(int a, int b){
        Random random = new Random();
        return random.nextInt(b-a+1)+a;
    }

    private void SetListener(){
        Bhome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Intent intent = new Intent(Generate_order_page.this, Homepage.class);
            startActivity(intent);
            finish();
            }
        });
    }

    private List<String> Get_ordernum_list(Order_record[] orders){
        List<String> list = new ArrayList<>();
        int num=0;
        for(int i=0; i<orders.length; i++){
            if(!list.contains(orders[i].getOrder_number())){
                list.add(orders[i].getOrder_number());
                num++;
            }
        }
        return list;
    }

    private void Create_Order(String username,String ordernum,String shopname,String itemname,double price,int number,String itemtag,String path){
        final JSONObject request = new JSONObject();
        try {
            request.put("username", username);
            request.put("ordernumber", ordernum);
            request.put("shopname", shopname);
            request.put("itemname", itemname);
            request.put("price", price);
            request.put("number", number);
            request.put("itemtag", itemtag);
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, path, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("state") ==1) {
                        //Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
            }
        });
        MySingleton.getInstance(Generate_order_page.this).addToRequestQueue(jsArrayRequest);
    }

    private void Create_orders(String username,String ordernum,Item_info[] items, String path){
        int len = items.length;
        final JSONObject request = new JSONObject();
        try {
            request.put("username", username);
            request.put("ordernumber", ordernum);
            request.put("len", len);
            for(int i=0; i<len; i++){
                String name1 = "tag"+ (i*5 +1);
                String name2 = "tag"+ (i*5 +2);
                String name3 = "tag"+ (i*5 +3);
                String name4 = "tag"+ (i*5 +4);
                String name5 = "tag"+ (i*5 +5);

                request.put(name1,items[i].getShop_name());
                request.put(name2,items[i].getIteam_name());
                request.put(name3,items[i].getPrice());
                request.put(name4,items[i].getNumber());
                request.put(name5,items[i].getLink());
            }
        }
        catch(JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.POST, path, request, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    if (response.getInt("state") ==1) {
                        Toast.makeText(getApplicationContext(), response.getString("message"), Toast.LENGTH_SHORT).show();
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
                Toast.makeText(getApplicationContext(),"No Internet",Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);//continue other things
    }
}
