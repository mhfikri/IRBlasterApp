package com.example.irblaster;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.HapticFeedbackConstants;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.irblaster.databinding.ActivityIrblasterBinding;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.vdurmont.semver4j.Semver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class IRBlasterActivity extends AppCompatActivity {
    private static final String TAG = IRBlasterActivity.class.getSimpleName();
    private static final int MENU_ITEM_LOGOUT = 0;

    private ActivityIrblasterBinding mBinding;
    private FirebaseFirestore db;
    private FirebaseFunctions functions;
    private String uuid;
    private int deviceCount;
    private String latestVersion;
    private String binaryUrl;

    private final ActivityResultLauncher<Intent> espTouchLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String macAddress = result.getData().getStringExtra("mac_address");
                        String deviceId = "IRB_" + macAddress.substring(6).toUpperCase();
                        addDevice(deviceId);
                    }
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityIrblasterBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        mBinding.addButton.setOnClickListener(v -> onAddClick());

        String username = getIntent().getStringExtra("username");
        String emailAddress = getIntent().getStringExtra("emailAddress");

        if (username != null) {
            Toast.makeText(this, "Welcome " + username + "!", Toast.LENGTH_SHORT).show();
        } else if (emailAddress != null) {
            Toast.makeText(this, "Welcome " + emailAddress + "!", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Welcome!", Toast.LENGTH_SHORT).show();
        }

        uuid = getIntent().getStringExtra("uuid");

        db = FirebaseFirestore.getInstance();
        functions = FirebaseFunctions.getInstance("asia-southeast2");

        checkFirmwareUpdate();

        mBinding.irBlasterList.setLayoutManager(new GridLayoutManager(this, 2));
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        }
    }

    private void attachRecyclerViewAdapter() {
        String uuid = getIntent().getStringExtra("uuid");
        RecyclerView.Adapter adapter = newAdapter(uuid);
        deviceCount = adapter.getItemCount();
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

    private void checkFirmwareUpdate() {
        RequestQueue queue = Volley.newRequestQueue(IRBlasterActivity.this);
        String url = "https://ir-blaster-376915.web.app/firmware/version.json";
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    latestVersion = response.getString("version");
                    binaryUrl = response.getString("bin");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.toString());
            }
        });
        queue.add(request);
    }

    private void showAboutDialog(String deviceId, String firmwareVersion) {
        CharSequence[] items = new CharSequence[]{
                getString(R.string.about_device_id, deviceId),
                getString(R.string.about_firmware_version, firmwareVersion)
        };
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle(R.string.menu_item_about)
                .setIcon(R.drawable.baseline_info_black_24)
                .setItems(items, null)
                .show();
    }

    private void addDevice(String deviceId) {
        Map<String, Object> data = new HashMap<>();
        data.put("deviceId", deviceId);
        data.put("deviceName", "IR Blaster " + (deviceCount + 1));
        data.put("uuid", uuid);
        data.put("online", false);
        data.put("temperature", "-");
        data.put("humidity", "-");

        db.collection("devices").document(deviceId)
                .set(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(IRBlasterActivity.this, "Device added", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void updateDevice(String deviceId, Map<String, Object> data) {
        db.collection("devices").document(deviceId)
                .update(data)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(IRBlasterActivity.this, "Device updated", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void removeDevice(String deviceId) {
        db.collection("devices").document(deviceId)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(IRBlasterActivity.this, "Device removed", Toast.LENGTH_SHORT).show();
                    }
                });
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

    private RecyclerView.Adapter newAdapter(String uuid) {
        Query query = FirebaseFirestore.getInstance()
                .collection("devices")
                .whereEqualTo("uuid", uuid)
                .orderBy("deviceName", Query.Direction.ASCENDING);

        FirestoreRecyclerOptions<IRBlaster> options =
                new FirestoreRecyclerOptions.Builder<IRBlaster>()
                        .setQuery(query, IRBlaster.class)
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
                    Toast.makeText(IRBlasterActivity.this, model.getDeviceName(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(IRBlasterActivity.this, ApplianceActivity.class);
                    intent.putExtra("deviceId", model.getDeviceId());
                    startActivity(intent);
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        PopupMenu popup = new PopupMenu(IRBlasterActivity.this, view);
                        popup.getMenuInflater().inflate(R.menu.irblaster_popup_menu, popup.getMenu());
                        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(MenuItem menuItem) {
                                if (menuItem.getItemId() == R.id.renameDevice) {
                                    final EditText editText = new EditText(IRBlasterActivity.this);
                                    editText.setInputType(InputType.TYPE_CLASS_TEXT);
                                    editText.setHint("Name");

                                    AlertDialog.Builder dialog = new AlertDialog.Builder(IRBlasterActivity.this);
                                    dialog.setTitle("Rename");
                                    dialog.setView(editText);
                                    dialog.setPositiveButton("SAVE", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            String inputName = editText.getText().toString();
                                            if (TextUtils.isEmpty(inputName)) {
                                                Toast.makeText(IRBlasterActivity.this, "Device name cannot be empty", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Map<String, Object> data = new HashMap<>();
                                                data.put("deviceName", inputName);
                                                updateDevice(model.getDeviceId(), data);
                                            }
                                        }
                                    });
                                    dialog.setNegativeButton("CANCEL", null);
                                    dialog.show();
                                } else if (menuItem.getItemId() == R.id.removeDevice) {
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(IRBlasterActivity.this);
                                    dialog.setTitle("Remove");
                                    dialog.setMessage("Are you sure you want to remove " + model.getDeviceName() + "?");
                                    dialog.setPositiveButton("Yes", (dialog1, i) -> {
                                        JSONObject data = new JSONObject();
                                        try {
                                            data.put("deviceId", model.getDeviceId());
                                            data.put("commandType", Constants.COMMAND_TYPE_RESET);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                        addMessage(data).addOnCompleteListener(task -> removeDevice(model.getDeviceId()));
                                    });
                                    dialog.setNegativeButton("No", null);
                                    dialog.show();
                                } else if (menuItem.getItemId() == R.id.firmwareUpdate) {
                                    checkFirmwareUpdate();
                                    Log.d(TAG, latestVersion);
                                    Semver currentVersion = new Semver(model.getFirmwareVersion());
                                    if (TextUtils.isEmpty(latestVersion)) {
                                        Toast.makeText(IRBlasterActivity.this, "Unable to check update", Toast.LENGTH_SHORT).show();
                                    } else if (currentVersion.isGreaterThanOrEqualTo(latestVersion)) {
                                        Toast.makeText(IRBlasterActivity.this, "Your device is up-to-date", Toast.LENGTH_SHORT).show();
                                    } else {
                                        AlertDialog.Builder dialog = new AlertDialog.Builder(IRBlasterActivity.this);
                                        dialog.setTitle("Firmware update");
                                        dialog.setMessage("IR Blaster v" + latestVersion + " is now available.");
                                        dialog.setPositiveButton("Update", (dialogInterface, i) -> {
                                            JSONObject data = new JSONObject();
                                            JSONObject payload = new JSONObject();
                                            try {
                                                data.put("deviceId", model.getDeviceId());
                                                data.put("commandType", Constants.COMMAND_TYPE_OTA);
                                                payload.put("binaryUrl", binaryUrl);
                                                data.put("commandPayload", payload);
                                            } catch (JSONException e) {
                                                e.printStackTrace();
                                            }
                                            addMessage(data).addOnCompleteListener(task -> {
                                                Map<String, Object> data1 = new HashMap<>();
                                                data1.put("online", false);
                                                updateDevice(model.getDeviceId(), data1);
                                                Toast.makeText(IRBlasterActivity.this, "Updating", Toast.LENGTH_SHORT).show();
                                            });
                                        });
                                        dialog.setNegativeButton("Cancel", null);
                                        dialog.show();
                                    }
                                } else if (menuItem.getItemId() == R.id.about) {
                                    CharSequence[] items = new CharSequence[]{
                                            getString(R.string.about_device_id, model.getDeviceId()),
                                            getString(R.string.about_firmware_version, model.getFirmwareVersion())
                                    };
                                    AlertDialog.Builder dialog = new AlertDialog.Builder(IRBlasterActivity.this);
                                    dialog.setTitle("About IR Blaster");
                                    dialog.setIcon(R.drawable.baseline_info_black_24);
                                    dialog.setItems(items, null);
                                    dialog.show();
                                }
                                return false;
                            }
                        });
                        popup.show();
                        view.performHapticFeedback(HapticFeedbackConstants.LONG_PRESS);

                        return false;
                    }
                });
            }
        };
    }
}