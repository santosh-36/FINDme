package com.santosh.findme;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MemAdapter extends PagerAdapter {

    private Context context;
    private LayoutInflater inflater;
    private Integer [] images = {R.drawable.img1,R.drawable.img2,R.drawable.img3,R.drawable.img4};
    @Override
    public int getCount() {
        return images.length;
    }

    public MemAdapter(Context context) {
        this.context = context;
    }

    @Override
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        container.removeView((View)object);
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, final int position) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_slider_layout, null);
        ImageView imageView = view.findViewById(R.id.slider_imageView);
        imageView.setImageResource(images[position]);

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (position){
                    case 0:payMoney(100);break;
                    case 1:payMoney(300);break;
                    case 2:payMoney(500);break;
                    case 3:payMoney(900);break;
                    default:
                        Toast.makeText(context, "Clicked title number "+(position+1), Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        container.addView(view,0);
        return view;
    }

    private void payMoney(int txn_amount) {
        SharedPreferences sp = context.getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String a = sp.getString("img_url","");
        Intent intent = new Intent(context, PaymentSplashActivity.class);
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        intent.putExtra("order_id",df.format(new Date()));
        intent.putExtra("customer_id", sp.getString("uid",null));
        intent.putExtra("txn_amount", txn_amount+"");
        intent.putExtra("parent_activity","MainActivity");
        context.startActivity(intent);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
