package com.example.irblaster;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.irblaster.databinding.ActivityApplianceBinding;

public class ApplianceActivity extends AppCompatActivity {
    private static final String[] APPLIANCE_TYPES = {
            "Television",
            "Air conditioner"
    };

    private static final int[] APPLIANCE_IMAGES = {
            R.drawable.ic_tv_80dp,
            R.drawable.ic_ac_unit_80dp
    };

    private ActivityApplianceBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliance);

        RecyclerView recyclerView = findViewById(R.id.appliance_list);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        recyclerView.setAdapter(new ApplianceAdapter());

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

    private class ApplianceHolder extends RecyclerView.ViewHolder {
        private int position;
        private TextView type;
        private ImageView image;

        ApplianceHolder(@NonNull View itemView) {
            super(itemView);
            type = itemView.findViewById(R.id.appliance_type);
            image = itemView.findViewById(R.id.appliance_image);

            itemView.setOnClickListener(v -> {
                switch (position) {
                    case 0:
                        Toast.makeText(ApplianceActivity.this, "TV Activity", Toast.LENGTH_SHORT).show();
                        break;
                    case 1:
                        Toast.makeText(ApplianceActivity.this, "AC Activity", Toast.LENGTH_SHORT).show();
                        break;
                }
            });
        }
    }

    private class ApplianceAdapter extends RecyclerView.Adapter<ApplianceHolder> {

        @NonNull
        @Override
        public ApplianceHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = getLayoutInflater().inflate(R.layout.appliance, parent, false);
            return new ApplianceHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ApplianceHolder holder, int position) {
            holder.position = holder.getBindingAdapterPosition();
            holder.type.setText(APPLIANCE_TYPES[position]);
            holder.image.setImageResource(APPLIANCE_IMAGES[position]);
        }

        @Override
        public int getItemCount() {
            return APPLIANCE_TYPES.length;
        }
    }
}