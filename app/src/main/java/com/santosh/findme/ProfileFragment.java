package com.santosh.findme;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.Objects;

public class ProfileFragment extends Fragment {

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Objects.requireNonNull(getActivity()).setTitle("Profile");
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doJob(view);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    private void doJob(View view) {
        TextView tv1 = view.findViewById(R.id.profile_tv_name);
        TextView tv2 = view.findViewById(R.id.profile_tv_email);
        TextView tv3 = view.findViewById(R.id.profile_tv_user_since);
        ImageView iv = view.findViewById(R.id.profile_iv);
        SharedPreferences sp = this.getActivity().getSharedPreferences("userdata", Context.MODE_PRIVATE);
        String a = sp.getString("img_url","");
        Picasso.with(getContext()).load(a).placeholder(R.drawable.launcher).into(iv);
        tv1.setText(sp.getString("name","FINDme"));
        tv2.setText(sp.getString("email","FINDme"));
        tv3.setText("User since : "+sp.getString("user_since","FINDme"));
    }
}
