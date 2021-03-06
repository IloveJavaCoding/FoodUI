package com.example.delicious.Activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.delicious.Adapters.Myadapter_myCart;
import com.example.delicious.R;
import com.example.delicious.Self_class.Controls;
import com.example.delicious.Self_class.Item_info;
import com.example.delicious.Self_class.MySingleton;
import com.example.delicious.Self_class.Order_record;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MyCart_page extends AppCompatActivity implements Myadapter_myCart.InListener {
    private ListView listView;
    private LinearLayout l_find, l_home, l_my;
    private Myadapter_myCart myadaptermyCart;
    private ImageView I_cart;
    private Button BAll;
    private TextView Tadd_more, Tnum, Tprice, Torprice, Tshow, tv_cart;
    private final double discount = 0.9;

    private static Item_info[] items;
    private Order_record[] orders;

    Controls Lock;
    private  String path;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        Get_data();
        Init();
        Flash();
        SetListener();
    }
    private void Get_data(){
        if(Lock.getLock2()==0){
            Intent intent = this.getIntent();
            Bundle bundle = intent.getExtras();
            Item_info[] item  = (Item_info[])bundle.getSerializable("food") ;
            items = Get_choosed_items(item);
            Lock.setLock2(1);
        }

        path = Lock.getRoot() + "get_all_order.php";
        Get_all_order(path);
    }

    private Item_info[] Get_choosed_items(Item_info[] item ){
        List<Item_info> list = new ArrayList<Item_info>();
        int num=0;
        for(int i=0; i<item.length; i++){
            if(item[i].getNumber()>0){
                list.add(item[i]);
                num++;
            }
        }
        Item_info[] ite = list.toArray(new Item_info[num]);
        return ite;
    }

    private void Init(){
        listView = (ListView) findViewById(R.id.LVc);
        myadaptermyCart = new Myadapter_myCart(this, items,this);
        listView.setAdapter(myadaptermyCart);

        BAll = (Button) findViewById(R.id.Ball);
        Tadd_more = (TextView) findViewById(R.id.Tadd_more);
        Tnum = (TextView) findViewById(R.id.Tnum);
        Tprice = (TextView) findViewById(R.id.Tprice);
        Torprice = (TextView) findViewById(R.id.Torip);
        Tshow = (TextView)findViewById(R.id.Tshow);

        I_cart = findViewById(R.id.icon_cart);
        I_cart.setImageDrawable(getResources().getDrawable(R.drawable.icon_cart2));
        tv_cart = findViewById(R.id.tv_cart);
        tv_cart.setTextColor(Color.argb(255,26,132,216));

        l_home = findViewById(R.id.l_home);
        l_find = findViewById(R.id.l_find);
        l_my = findViewById(R.id.l_my);

        if(items.length<1){
            Tshow.setVisibility(View.VISIBLE);
            Tshow.setText("There is nothing in your cart!!!");
        }
    }

    private void SetListener(){
        l_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCart_page.this, Homepage.class);
                startActivity(intent);
                finish();
            }
        });
        l_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCart_page.this, Find_page.class);
                startActivity(intent);
                finish();
            }
        });
        l_my.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCart_page.this, User_page.class);
                startActivity(intent);
                finish();
            }
        });
        BAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(items.length>0){
                    Lock.setLock2(0);
                    Intent intent = new Intent(MyCart_page.this, Generate_order_page.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("foods",items);
                    bundle.putSerializable("orders",orders);
                    intent.putExtras(bundle);

                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(getApplicationContext(),"You have chosen nothing!!!",Toast.LENGTH_SHORT).show();
                }
            }
        });
        Tadd_more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyCart_page.this, Homepage.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void Flash(){
        int total_items = 0;
        double total_price = 0;
        double total_price2;//original price;
        for(int i=0; i<items.length; i++){
            total_items+=items[i].getNumber();
            total_price+=(items[i].getPrice() * items[i].getNumber());
        }
        total_price2 = total_price;

        if(total_items>=2){
            if(total_price>50){
                total_price *= discount;
            }
            total_price = Math.floor(total_price);
        }

        Tnum.setText(Integer.toString(total_items));
        Tprice.setText(Double.toString(total_price));

        if(total_price>0){
            Torprice.setVisibility(View.VISIBLE);
            Torprice.setText(Double.toString(total_price2));
        }else{
            Torprice.setVisibility(View.INVISIBLE);
        }
    }

    private void Get_all_order(String path){
        final JSONObject request = null;
        JsonObjectRequest jsArrayRequest = new JsonObjectRequest(Request.Method.GET, path, request,new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                JSONArray acc = null;
                try {
                    if (response.getInt("state") ==1) {
                        acc = response.getJSONArray("order");
                        orders = new Order_record[acc.length()];
                        for(int i=0;i<acc.length();i++){
                            orders[i] = new Order_record();
                            JSONObject c = acc.getJSONObject(i);

                            String order_number = c.getString("order_number");
                            String username = c.getString("username");
                            String shop_name = c.getString("shop_name");
                            String item_name = c.getString("item_name");
                            double price = c.getDouble("price");
                            int number = c.getInt("number");
                            String time = c.getString("time");
                            int tag = c.getInt("tag");
                            String item_tag = c.getString("item_tag");

                            orders[i].setIteam_name(item_name);
                            orders[i].setUsername(username);
                            orders[i].setOrder_number(order_number);
                            orders[i].setShop_name(shop_name);
                            orders[i].setPrice(price);
                            orders[i].setNumber(number);
                            orders[i].setReceive_time(time);
                            orders[i].setTag(tag);
                            orders[i].setItem_tag(item_tag);
                        }
                    }
                    else {
                        orders = new Order_record[0];
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),"Not internet",Toast.LENGTH_SHORT).show();
            }
        });

        MySingleton.getInstance(this).addToRequestQueue(jsArrayRequest);
    }

    @Override
    public void itemClick(View v) {
        int position = (Integer) v.getTag();
        switch(v.getId()){
            case R.id.Imin:
                int temp =  items[position].getNumber();
                if(temp>0){
                    items[position].setNumber(items[position].getNumber()-1);
                    myadaptermyCart.notifyDataSetChanged();
                    Flash();
                }else{
                    Toast.makeText(getApplicationContext(),"That have been zero!",Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.Iadd:
                items[position].setNumber(items[position].getNumber()+1);
                myadaptermyCart.notifyDataSetChanged();
                Flash();
                break;
            default:
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);//continue other things
    }
}
