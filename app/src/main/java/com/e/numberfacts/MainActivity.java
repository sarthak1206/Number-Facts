package com.e.numberfacts;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.LruCache;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    Button Proceed;
    ImageButton Quit;
    EditText Number;
    public int flag=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Number = (EditText) findViewById(R.id.number_txt);
        Proceed = (Button) findViewById(R.id.proceed_btn);
        Quit = (ImageButton) findViewById(R.id.quit_btn);

        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        Proceed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = Number.getText().toString();
                if(flag==0)
                {
                    final String url = "http://numbersapi.com/"+str+"?json";

                    JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                            (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                                @Override
                                public void onResponse(JSONObject response) {
                                    try {
                                        String str = response.getString("text");
                                        flag=1;
                                        Intent intent = new Intent(MainActivity.this, AnsweringActivity.class);
                                        intent.putExtra("fact", str);
                                        startActivity(intent);
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
                    MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
                }
            }
        });

    }

    public static class MySingleton {
        private static MySingleton instance;
        private RequestQueue requestQueue;
        private ImageLoader imageLoader;
        private Context ctx;

        private MySingleton(Context context) {
            ctx = context;
            requestQueue = getRequestQueue();

            imageLoader = new ImageLoader(requestQueue,
                    new ImageLoader.ImageCache() {
                        private final LruCache<String, Bitmap>
                                cache = new LruCache<String, Bitmap>(20);

                        @Override
                        public Bitmap getBitmap(String url) {
                            return cache.get(url);
                        }

                        @Override
                        public void putBitmap(String url, Bitmap bitmap) {
                            cache.put(url, bitmap);
                        }
                    });
        }

        public static synchronized MySingleton getInstance(Context context) {
            if (instance == null) {
                instance = new MySingleton(context);
            }
            return instance;
        }

        public RequestQueue getRequestQueue() {
            if (requestQueue == null) {
                // getApplicationContext() is key, it keeps you from leaking the
                // Activity or BroadcastReceiver if someone passes one in.
                requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
            }
            return requestQueue;
        }

        public <T> void addToRequestQueue(Request<T> req) {
            getRequestQueue().add(req);
        }

        public ImageLoader getImageLoader() {
            return imageLoader;
        }
    }
}
