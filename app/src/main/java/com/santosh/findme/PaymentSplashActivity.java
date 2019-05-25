package com.santosh.findme;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Objects;

public class PaymentSplashActivity extends AppCompatActivity {

    TextView text_progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_splash);
        Toolbar toolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        try{
        final Intent intent = new Intent(PaymentSplashActivity.this, PaytmCheckSum.class);
        Intent intent_from = getIntent();
        String order_id = intent_from.getExtras().getString("order_id");
        String customer_id = intent_from.getExtras().getString("customer_id");
        String transaction_amount = intent_from.getExtras().getString("txn_amount");
        String parent_activity = intent_from.getExtras().getString("parent_activity");
        intent.putExtra("order_id",order_id);
        intent.putExtra("customer_id", customer_id);
        intent.putExtra("txn_amount", transaction_amount);

        text_progress = findViewById(R.id.payment_tv);
        ImageView image = findViewById(R.id.payment_iv);
        ImageView gif = findViewById(R.id.payment_gif);
            if(parent_activity.equals("MainActivity")){
                mysleep(900,"Preparing your order");
                mysleep(300,"Redirecting to PayTM gateway");
                Runnable launchTask = new Runnable() {

                    @Override
                    public void run() {
                        startActivity(intent);
                        PaymentSplashActivity.this.finish();
                    }
                };
                text_progress.postDelayed(launchTask,1000);
            }else if(parent_activity.equals("PaytmActivity")){
                mysleep(0,"Verifying transaction status");
                String code = intent_from.getExtras().getString("status") == null ? "31" : intent_from.getExtras().getString("status");
                //
                // Toast.makeText(getApplicationContext(),"___"+code+"___",Toast.LENGTH_LONG).show();
                if(code.equals("01")){
                    gif.setVisibility(View.INVISIBLE);
                    image.setImageResource(R.drawable.accept_logo);
                    image.setVisibility(View.VISIBLE);
                    mysleep(1100,"Your booking is successful!");
                }else{
                    gif.setVisibility(View.INVISIBLE);
                    image.setImageResource(R.drawable.reject_logo);
                    image.setVisibility(View.VISIBLE);
                    mysleep(1100,"Booking failed. Try again!");
                }
                Runnable launchTask = new Runnable() {

                    @Override
                    public void run() {
                        startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        PaymentSplashActivity.this.finish();
                    }
                };
                text_progress.postDelayed(launchTask,2500);

            }
        }catch (Exception e){
            Log.e("payment",e.getMessage());
        }
    }

    private void mysleep(final int time, final String text) {
        Thread t = new Thread(){
            @Override
            public void run() {
                try {
                    Thread.sleep(time);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (text != null)
                                text_progress.setText(text);
                        }
                    });
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();
    }



    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}