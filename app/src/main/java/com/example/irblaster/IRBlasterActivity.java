package com.example.irblaster;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.irblaster.databinding.ActivityIrblasterBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class IRBlasterActivity extends AppCompatActivity {
    private static final String TAG = IRBlasterActivity.class.getSimpleName();

    private static final int MENU_ITEM_LOGOUT = 0;

    private static final CollectionReference sDeviceCollection =
            FirebaseFirestore.getInstance().collection("devices");

    private static final Query sDeviceQuery =
            sDeviceCollection.orderBy("name", Query.Direction.ASCENDING);

    private ActivityIrblasterBinding mBinding;

    private final ActivityResultLauncher<Intent> espTouchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Log.d(TAG, result.getData().getStringExtra("mac_address"));
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityIrblasterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.addButton.setOnClickListener(v -> onAddClick());

        String userName = getIntent().getStringExtra("user_name");
        String userEmail = getIntent().getStringExtra("user_email");
        String userPhone = getIntent().getStringExtra("user_phone");
        Log.d(TAG, "userName: " + userName);
        Log.d(TAG, "userEmail: " + userEmail);
        Log.d(TAG, "userPhone: " + userPhone);
        if (userName != null) {
            Toast.makeText(this, "Welcome " + userName + "!", Toast.LENGTH_SHORT).show();
        } else if (userPhone != null) {
            Toast.makeText(this, "Welcome " + userPhone + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        }

        mBinding.irBlasterList.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    private void attachRecyclerViewAdapter() {
        final RecyclerView.Adapter adapter = newAdapter();
        mBinding.irBlasterList.setAdapter(adapter);
    }

    private void onAddClick() {
        Intent intent = new Intent(IRBlasterActivity.this, EspTouchActivity.class);
        espTouchLauncher.launch(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(Menu.NONE, MENU_ITEM_LOGOUT, 0, R.string.irblaster_menu_item_logout)
                .setShowAsAction(MenuItem.SHOW_AS_ACTION_NEVER);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == MENU_ITEM_LOGOUT) {
            signOut();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    private boolean isSignedIn() {
        return FirebaseAuth.getInstance().getCurrentUser() != null;
    }

    public void signOut() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    public void onComplete(@NonNull Task<Void> task) {
                        startActivity(new Intent(IRBlasterActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

    private RecyclerView.Adapter newAdapter() {
        FirestoreRecyclerOptions<IRBlaster> options =
                new FirestoreRecyclerOptions.Builder<IRBlaster>()
                        .setQuery(sDeviceQuery, IRBlaster.class)
                        .setLifecycleOwner(this)
                        .build();

        return new FirestoreRecyclerAdapter<IRBlaster, IRBlasterHolder>(options) {
            @NonNull
            @Override
            public IRBlasterHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                return new IRBlasterHolder(LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.irblaster, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull IRBlasterHolder holder, int position, @NonNull IRBlaster model) {
                holder.bind(model);
                holder.itemView.setOnClickListener(v -> {
//                    Toast.makeText(IRBlasterActivity.this, model.toString(), Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(IRBlasterActivity.this, ApplianceActivity.class));
                });
            }
        };
    }
}