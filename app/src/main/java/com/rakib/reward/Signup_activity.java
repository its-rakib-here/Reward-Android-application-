package com.rakib.reward;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Signup_activity extends AppCompatActivity {

    EditText name, phone, password;
    Button signupBtn;

    String url = "https://varadibo.net/reward/signup.php"; // 🔥 change this

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        // init views
        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        password = findViewById(R.id.password);
        signupBtn = findViewById(R.id.signupBtn);

        signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(name.getText().toString().isEmpty() ||
                        phone.getText().toString().isEmpty() ||
                        password.getText().toString().isEmpty()) {

                    Toast.makeText(Signup_activity.this,
                            "All fields required",
                            Toast.LENGTH_SHORT).show();
                    return;
                }

                signupUser();
            }
        });
    }

    private void signupUser() {

        StringRequest request = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject obj = new JSONObject(response);

                        boolean status = obj.getBoolean("status");
                        String message = obj.getString("message");

                        if(status){
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                            // go to login page
                            startActivity(new Intent(this, login_activity.class));
                            finish();

                        }else{
                            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
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
                params.put("name", name.getText().toString().trim());
                params.put("phone", phone.getText().toString().trim());
                params.put("password", password.getText().toString().trim());

                return params;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }
}