package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.irblaster.databinding.ActivityTvRemoteBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONException;
import org.json.JSONObject;

public class TvRemoteActivity extends AppCompatActivity {

    private FirebaseFunctions mFunctions;
    private ActivityTvRemoteBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityTvRemoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.onOffButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_ONOFF));
        binding.chUpButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_CHANNEL_UP));
        binding.chDownButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_CHANNEL_DOWN));
        binding.volUpButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_VOLUME_UP));
        binding.volDownButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_VOLUME_DOWN));
        binding.menuButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_MENU));
        binding.okButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_OK));
        binding.navUpButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_NAVIGATION_UP));
        binding.navDownButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_NAVIGATION_DOWN));
        binding.navLeftButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_NAVIGATION_LEFT));
        binding.navRightButton.setOnClickListener(view -> sendMessage(Constants.REMOTE_TV_NAVIGATION_RIGHT));

        mFunctions = FirebaseFunctions.getInstance("asia-southeast2");

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle(getIntent().getStringExtra("name"));
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

    private Task<String> addMessage(JSONObject data) {
        binding.progressIndicator.show();
        return mFunctions
                .getHttpsCallable("sendMessage")
                .call(data)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    private void sendMessage(int code) {
        JSONObject data = new JSONObject();
        JSONObject payload = new JSONObject();
        try {
            data.put("deviceId", getIntent().getStringExtra("deviceId"));
            data.put("commandType", Constants.COMMAND_TYPE_REMOTE);
            payload.put("remoteType", Constants.REMOTE_TYPE_TV);
            payload.put("remoteId", Integer.parseInt(getIntent().getStringExtra("remoteId")));
            payload.put("remoteCode", code);
            data.put("commandPayload", payload);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addMessage(data)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(TvRemoteActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                        }
                        binding.progressIndicator.hide();
                    }
                });
    }

}