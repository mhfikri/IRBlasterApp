package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ModelActivity extends AppCompatActivity {
    private static final String TAG = IRBlasterActivity.class.getSimpleName();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_model);

        ListView listView = findViewById(R.id.model_list);

        String applianceType = getIntent().getStringExtra("applianceType");

        db = FirebaseFirestore.getInstance();

        Map<String, Map<String, Object>> modelCollection = new HashMap<>();

        ArrayAdapter adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String id = (String) adapterView.getItemAtPosition(i);
                Map<String, Object> model = modelCollection.get(id);
                String remoteId = (String) model.get("remoteId");
                String name = (String) model.get("name");

                Intent intent = null;
                if (applianceType.equals("tv")) {
                    intent = new Intent(ModelActivity.this, TvRemoteActivity.class);
                } else if (applianceType.equals("ac")) {
                    intent = new Intent(ModelActivity.this, AcRemoteActivity.class);
                }
                if (intent != null) {
                    intent.putExtra("deviceId", getIntent().getStringExtra("deviceId"));
                    intent.putExtra("remoteId", remoteId);
                    intent.putExtra("name", name);
                    startActivity(intent);
                }
            }
        });

        db.collection("appliances")
                .document(applianceType)
                .collection("model")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList list = new ArrayList<>();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                String name = doc.getString("name");
                                Log.d(TAG, doc.getId() + " => " + doc.getData());
                                list.add(name);
                                modelCollection.put(name, doc.getData());
                            }
                            adapter.clear();
                            adapter.addAll(list);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}