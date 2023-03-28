package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class IRBlasterActivity extends AppCompatActivity {

    private static final String TAG = IRBlasterActivity.class.getSimpleName();

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
        getMenuInflater().inflate(R.menu.irblaster_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.item_logout) {
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