package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.irblaster.databinding.ActivityAcRemoteBinding;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class AcRemoteActivity extends AppCompatActivity {
    private static final String TAG = AcRemoteActivity.class.getSimpleName();

    private static final int AC_SWING_OFF = 0;
    private static final int AC_SWING_ON = 1;
    private static final int AC_FAN_AUTO = 0;
    private static final int AC_FAN_LOW = 1;
    private static final int AC_FAN_MEDIUM = 2;
    private static final int AC_FAN_HIGH = 3;
    private static final int AC_TEMP_MAX = 30;
    private static final int AC_TEMP_MIN = 18;

    private FirebaseFirestore firestore;
    private FirebaseFunctions functions;
    private ActivityAcRemoteBinding binding;
    private String deviceId;
    private String remoteId;

    private String docId;

    private boolean isAcOn;
    private int currentFan;
    private int currentSwing;
    private int currentTemperature;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAcRemoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firestore = FirebaseFirestore.getInstance();
        functions = FirebaseFunctions.getInstance("asia-southeast2");

        deviceId = getIntent().getStringExtra("deviceId");
        remoteId = getIntent().getStringExtra("remoteId");

        binding.onOffButton.setOnClickListener(view -> {
            if (!isAcOn) {
                sendMessage(Constants.REMOTE_AC_ON);
                setAcOn();
            } else {
                sendMessage(Constants.REMOTE_AC_OFF);
                setAcOff();
            }
        });

        binding.tempUpButton.setOnClickListener(view -> {
            if (currentTemperature < AC_TEMP_MAX) {
                sendMessage(Constants.REMOTE_AC_TEMP_UP);
                currentTemperature++;
                setAcOn();
            }
        });

        binding.tempDownButton.setOnClickListener(view -> {
            if (currentTemperature > AC_TEMP_MIN) {
                sendMessage(Constants.REMOTE_AC_TEMP_DOWN);
                currentTemperature--;
                setAcOn();
            }
        });

        binding.fanView.setFactory(() -> {
            TextView textView = new TextView(AcRemoteActivity.this);
            textView.setGravity(Gravity.END);
            return textView;
        });

        binding.fanButton.setOnClickListener(view -> {
            currentFan++;
            if (currentFan > Constants.REMOTE_AC_FAN_SPEED.length - 1) {
                currentFan = AC_FAN_AUTO;
            }
            sendMessage(Constants.REMOTE_AC_FAN_SPEED[currentFan]);
            setAcOn();
        });

        binding.swingButton.setOnClickListener(view -> {
            if (currentSwing == AC_SWING_OFF) {
                sendMessage(Constants.REMOTE_AC_SWING_ON);
                currentSwing = AC_SWING_ON;
            } else {
                sendMessage(Constants.REMOTE_AC_SWING_OFF);
                currentSwing = AC_SWING_OFF;
            }
            setAcOn();
        });

        binding.autoOnSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                String temp = binding.autoOnTemp.getText().toString();
                if (isChecked) {
                    if (TextUtils.isEmpty(temp)) {
                        Toast.makeText(AcRemoteActivity.this, "Please enter on temperature", Toast.LENGTH_SHORT).show();
                        binding.autoOnSwitch.setChecked(false);
                        return;
                    }
                }
                Map<String, Object> data = new HashMap<>();
                data.put("autoOn", isChecked);
                data.put("autoOnTemp", Integer.parseInt(temp));
                updateSettings(data);
            }
        });

        binding.autoOffSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed()) {
                String temp = binding.autoOffTemp.getText().toString();
                if (isChecked) {
                    if (TextUtils.isEmpty(temp)) {
                        Toast.makeText(AcRemoteActivity.this, "Please enter off temperature", Toast.LENGTH_SHORT).show();
                        binding.autoOffSwitch.setChecked(false);
                        return;
                    }
                }
                Map<String, Object> data = new HashMap<>();
                data.put("autoOff", isChecked);
                data.put("autoOffTemp", Integer.parseInt(temp));
                updateSettings(data);
            }
        });

        initValue();
        loadSettings();

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

    private void initValue() {
        currentFan = AC_FAN_LOW;
        currentSwing = AC_SWING_OFF;
        currentTemperature = 24;

        setAcOff();
    }

    private void setAcOn() {
        isAcOn = true;

        binding.tempView.setText(formatTemperature(currentTemperature));
        binding.fanView.setCurrentText(Constants.AC_FAN_SPEED_TEXT[currentFan]);

        binding.tempView.setVisibility(View.VISIBLE);
        binding.fanView.setVisibility(View.VISIBLE);
        if (currentSwing == AC_SWING_ON) {
            binding.swingView.setVisibility(View.VISIBLE);
        } else {
            binding.swingView.setVisibility(View.INVISIBLE);
        }
    }

    private void setAcOff() {
        isAcOn = false;

        binding.tempView.setVisibility(View.INVISIBLE);
        binding.fanView.setVisibility(View.INVISIBLE);
        binding.swingView.setVisibility(View.INVISIBLE);
    }

    private String formatTemperature(int temperature) {
        return temperature + "\u2103";
    }

    private void loadSettings() {
        firestore.collection("settings")
                .whereEqualTo("remoteId", remoteId)
                .whereEqualTo("deviceId", deviceId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                Log.d(TAG, doc.getId() + " => " + doc.getData());
                                docId = doc.getId();
                                if (doc.getBoolean("autoOn") != null) {
                                    binding.autoOnSwitch.setChecked(doc.getBoolean("autoOn"));
                                    binding.autoOnTemp.setText(String.valueOf(doc.getLong("autoOnTemp").intValue()));
                                }
                                if (doc.getBoolean("autoOff") != null) {
                                    binding.autoOffSwitch.setChecked(doc.getBoolean("autoOff"));
                                    binding.autoOffTemp.setText(String.valueOf(doc.getLong("autoOffTemp").intValue()));
                                }
                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    private void addSettings(Map<String, Object> data) {
        data.put("deviceId", deviceId);
        data.put("remoteId", remoteId);

        firestore.collection("settings")
                .add(data)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        docId = documentReference.getId();
                    }
                });
    }

    private void updateSettings(Map<String, Object> data) {
        if (docId == null) {
            addSettings(data);
        } else {
            firestore.collection("settings").document(docId)
                    .update(data)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(AcRemoteActivity.this, "Settings updated", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private Task<String> addMessage(JSONObject data) {
        return functions
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

    private void sendMessageAuto() {
        JSONObject data = new JSONObject();
        JSONObject payload = new JSONObject();
        try {
            data.put("deviceId", deviceId);
            data.put("commandType", Constants.COMMAND_TYPE_REMOTE);
            data.put("commandPayload", payload);

            payload.put("remoteType", Constants.REMOTE_TYPE_AC);
            payload.put("remoteId", Integer.parseInt(remoteId));
            payload.put("autoOn", binding.autoOnSwitch.isChecked() ? 1 : 0);
            payload.put("autoOff", binding.autoOffSwitch.isChecked() ? 1 : 0);
            payload.put("autoOnTemp", Integer.parseInt(binding.autoOnTemp.getText().toString()));
            payload.put("autoOffTemp", Integer.parseInt(binding.autoOffTemp.getText().toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendMessage(int code) {
        JSONObject data = new JSONObject();
        JSONObject payload = new JSONObject();
        try {
            data.put("deviceId", deviceId);
            data.put("commandType", Constants.COMMAND_TYPE_REMOTE);
            data.put("commandPayload", payload);

            payload.put("remoteType", Constants.REMOTE_TYPE_AC);
            payload.put("remoteId", Integer.parseInt(remoteId));
            payload.put("remoteCode", code);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addMessage(data)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AcRemoteActivity.this, "Message sent", Toast.LENGTH_SHORT).show();
                        } else {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                        }
                    }
                });
    }
}