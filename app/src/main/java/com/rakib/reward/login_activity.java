package com.rakib.reward;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class login_activity extends AppCompatActivity {

    EditText phone, password;
    Button loginBtn;
    TextView goSignup;

    String url = "https://varadibo.net/reward/login.php"; // 🔥 change this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);

        // init views
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        loginBtn = findViewById(R.id.loginBtn);
        goSignup = findViewById(R.id.goSignup);

        goSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(login_activity.this,Signup_activity.class));
            }
        });

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(phone.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty()){

                    Toast.makeText(login_activity.this,
                            "Fill all fields",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                loginUser();
            }
        });
    }

    private void loginUser() {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {

                        JSONObject obj = new JSONObject(response);

                        if(obj.getBoolean("status")){

                            JSONObject user = obj.getJSONObject("user");

                            String name = user.getString("name");
                            String role = user.getString("role");
                            String phoneNo = user.getString("phone");
                            String id = user.getString("id");

                            // 🔐 SAVE SESSION
                            SharedPreferences sp = getSharedPreferences("user", MODE_PRIVATE);
                            SharedPreferences.Editor ed = sp.edit();

                            ed.putString("id", user.getString("id"));
                            ed.putString("name", user.getString("name"));
                            ed.putString("phone", user.getString("phone"));
                            ed.putString("role", user.getString("role"));

                            /* 🔐 NEW */
                            ed.putString("token", user.getString("token"));
                            ed.putBoolean("isLoggedIn", true);

                            ed.apply();

                            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();

                            // 🚀 REDIRECT BASED ON ROLE
                            if(role.equals("admin")){
                                startActivity(new Intent(this, admin_activity.class));
                            }else{
                                startActivity(new Intent(this, MainActivity.class));
                            }

                            finish();

                        }else{
                            Toast.makeText(this,
                                    obj.getString("message"),
                                    Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(this, "Parsing error", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(this, "Server error", Toast.LENGTH_SHORT).show()
        ){
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new HashMap<>();
                params.put("phone", phone.getText().toString().trim());
                params.put("password", password.getText().toString().trim());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}