package com.e.numberfacts;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class AnsweringActivity extends AppCompatActivity {

    TextView Facts;
    String str,num;
    Button FindMoreFacts;
    ImageButton Back;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_answer);

        str = getIntent().getStringExtra("fact").toString();
        Facts = (TextView) findViewById(R.id.txt_fact);
        FindMoreFacts = (Button) findViewById(R.id.morefacts_btn);
        Back = (ImageButton) findViewById(R.id.back_btn);

        Back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        Facts.setText(str);

        num = "";
        for(int i=0;i<str.length();i++)
        {
            if(str.charAt(i)!=' ')
            {
                num=num+str.charAt(i);
            }
            else
                break;
        }
        System.out.println("Number: "+num);
        FindMoreFacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String url = "http://numbersapi.com/"+num+"?json";

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    String str = response.getString("text");
                                    System.out.println("url: "+str);
                                    Facts.setText(str);
                                }
                                catch(Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                System.out.println("Error");
                            }
                        });

                // Access the RequestQueue through your singleton class.
                MainActivity.MySingleton.getInstance(AnsweringActivity.this).addToRequestQueue(jsonObjectRequest);
            }
        });

    }
}
