package com.nathit.cryptotacker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.nathit.cryptotacker.Adapter.AdapterHl;
import com.nathit.cryptotacker.Model.ModelHl;

import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    String myUid;
    List<ModelHl> HlList;
    AdapterHl adapterHl;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        init_screen();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        myUid = firebaseUser.getUid();

        recyclerView = findViewById(R.id.hisRecyclerview);

        loadHistory();
    }

    private void loadHistory() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(true);
        recyclerView.setLayoutManager(layoutManager);
        HlList = new ArrayList<ModelHl>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("User")
                .child(myUid).child("History");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                HlList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    ModelHl modelHl = ds.getValue(ModelHl.class);

                    HlList.add(modelHl);

                    adapterHl = new AdapterHl(HistoryActivity.this, HlList);
                    recyclerView.setAdapter(adapterHl);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HistoryActivity.this, "Something is wrong! no User details",
                        Toast.LENGTH_SHORT).show();

            }
        });
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_home) {
            Intent intent = new Intent(HistoryActivity.this, CryptoActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_user) {
            Intent intent = new Intent(HistoryActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);

        }
        if (id == R.id.nav_history) {
            Intent intent = new Intent(HistoryActivity.this, HistoryActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        if (id == R.id.nav_logout) {
            firebaseAuth.signOut();
            Intent intent = new Intent(HistoryActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
            item.setEnabled(false);
        }
        return super.onOptionsItemSelected(item);
    }

    int backPressed = 0;
    @Override
    public void onBackPressed() {
        backPressed++;
        if (backPressed == 1) {
            Intent intent = new Intent(HistoryActivity.this, UserProfileActivity.class);
            startActivity(intent);
            finish();
        }
        super.onBackPressed();
    }
}