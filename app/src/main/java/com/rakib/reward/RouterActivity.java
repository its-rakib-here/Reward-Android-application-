package com.rakib.reward;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class RouterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);

        boolean isLoggedIn = sp.getBoolean("isLoggedIn", false);
        String role = sp.getString("role", "");

        if(!isLoggedIn){
            startActivity(new Intent(this, login_activity.class));
            finish();
            return;
        }

        if(role.equals("admin")){
            startActivity(new Intent(this, admin_activity.class));
        }
        else{
            startActivity(new Intent(this, MainActivity.class));
        }

        finish();
    }
}