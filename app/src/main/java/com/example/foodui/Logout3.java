package com.example.foodui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

public class Logout3 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logout3);
		//change
        new Thread(){
            @Override
            public void run() {
                super.run();
                try {
                    Thread.sleep(3000);
                    Intent intent = new Intent(Logout3.this, MainActivity.class);
                    intent.putExtra("EXIT_TAG","SINGLETASK");
                    startActivity(intent);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode==KeyEvent.KEYCODE_BACK){
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);//coontinue other things
    }
}
