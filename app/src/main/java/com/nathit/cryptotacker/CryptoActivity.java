package com.nathit.cryptotacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nathit.cryptotacker.Adapter.AdapterCrypto;
import com.nathit.cryptotacker.Model.ModelCrypto;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CryptoActivity extends AppCompatActivity {

    private EditText searchEdt;
    private RecyclerView currenciesRV;
    private ArrayList<ModelCrypto> modelCryptos;
    private AdapterCrypto adapterCrypto;
    private ProgressBar progressBar;
    private FirebaseAuth firebaseAuth;
    private long backPressedTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crypto);

        init_screen();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();

        searchEdt = findViewById(R.id.idEdtSearch);
        currenciesRV = findViewById(R.id.idRVCurrencies);
        progressBar = findViewById(R.id.progressBar);
        modelCryptos = new ArrayList<>();
        adapterCrypto = new AdapterCrypto(modelCryptos,this);
        currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        currenciesRV.setAdapter(adapterCrypto);
        getCurrencyData();

        searchEdt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                filterCurrencies(s.toString());
            }
        });

    }

    private void filterCurrencies(String currency) {
        ArrayList<ModelCrypto> filteredList = new ArrayList<>();
        for (ModelCrypto item : modelCryptos) {
            if (item.getName().toLowerCase().contains(currency.toLowerCase())) {
                filteredList.add(item);
            }
            adapterCrypto.filterList(filteredList);
        }

    }

    private void getCurrencyData() {
        progressBar.setVisibility(View.VISIBLE);
        String url = "https://pro-api.coinmarketcap.com/v1/cryptocurrency/listings/latest";
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                progressBar.setVisibility(View.GONE);
                try {
                    JSONArray dataArray = response.getJSONArray("data");
                    for (int i=0; i<dataArray.length(); i++) {
                        JSONObject dataObj = dataArray.getJSONObject(i);
                        String name = dataObj.getString("name");
                        String symbol = dataObj.getString("symbol");
                        JSONObject quote = dataObj.getJSONObject("quote");
                        JSONObject USD = quote.getJSONObject("USD");
                        double price = USD.getDouble("price");
                        modelCryptos.add(new ModelCrypto(name, symbol, price));
                    }
                    adapterCrypto.notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(CryptoActivity.this, "Fail to extract json data...", Toast.LENGTH_SHORT).show();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(CryptoActivity.this, "Fail to get the data.", Toast.LENGTH_SHORT).show();
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> header = new HashMap<>();
                header.put("X-CMC_PRO_API_KEY","09ad07a4-3302-4b4d-8c71-8c3873638e33");
                return header;
            }
        };
        requestQueue.add(jsonObjectRequest);
    }

    private void init_screen() {
        final int flags = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        getWindow().getDecorView().setSystemUiVisibility(flags);

        final View view = getWindow().getDecorView();
        view.setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if ((visibility & View.SYSTEM_UI_FLAG_FULLSCREEN) == 0) {
                    view.setSystemUiVisibility(flags);
                }
            }
        });
    }

    //menu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(CryptoActivity.this, CryptoActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_user) {
            Intent intent = new Intent(CryptoActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);

        }
        if (id == R.id.nav_history) {
            Intent intent = new Intent(CryptoActivity.this, HistoryActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(CryptoActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    //onBackPressed

    @Override
    public void onBackPressed() {
        if (backPressedTime + 2000 > System.currentTimeMillis()) {
            moveTaskToBack(true);
            System.exit(0);
        } else {
            Toast.makeText(this, "Do to again to quit the application.", Toast.LENGTH_SHORT).show();
        }
        super.onBackPressed();
    }
}