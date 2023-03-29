package com.example.irblaster;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;

public class IRBlasterActivity extends AppCompatActivity {

    private static final String TAG = IRBlasterActivity.class.getSimpleName();

    private static final int MENU_ITEM_LOGOUT = 0;

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
        setContentView(R.layout.activity_irblaster);

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

        MaterialButton buttonAdd = findViewById(R.id.button_add);
        buttonAdd.setOnClickListener(v -> {
            espTouchLauncher.launch(new Intent(IRBlasterActivity.this, EspTouchActivity.class));
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
}