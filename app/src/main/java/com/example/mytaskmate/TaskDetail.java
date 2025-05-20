package com.example.mytaskmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TaskDetail extends AppCompatActivity {
    private TextView tvTitle, tvDescription, tvDueDate, tvPriority;
    private Spinner spinnerStatus;
    private Button btnUpdateStatus, logoutButton;

    private String taskId;
    private DatabaseReference taskRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_detail);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvDueDate = findViewById(R.id.tvDueDate);
        tvPriority = findViewById(R.id.tvPriority);
        spinnerStatus = findViewById(R.id.spinnerStatus);
        btnUpdateStatus = findViewById(R.id.btnUpdateStatus);
        logoutButton = findViewById(R.id.button_logout);


        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "In Progress", "Completed"});
        spinnerStatus.setAdapter(statusAdapter);

        taskId = getIntent().getStringExtra("taskId");
        taskRef = FirebaseDatabase.getInstance().getReference("tasks").child(taskId);

        loadTaskDetails();

        btnUpdateStatus.setOnClickListener(v -> updateTaskStatus());
        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(TaskDetail.this, Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }



    private void loadTaskDetails() {
        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                tvTitle.setText("Title: " + snapshot.child("title").getValue(String.class));
                tvDescription.setText("Description: " + snapshot.child("description").getValue(String.class));
                tvDueDate.setText("Due Date: " + snapshot.child("dueDate").getValue(String.class));
                tvPriority.setText("Priority: " + snapshot.child("priority").getValue(String.class));

                String status = snapshot.child("status").getValue(String.class);
                if (status != null) {
                    int pos = ((ArrayAdapter<String>) spinnerStatus.getAdapter()).getPosition(status);
                    spinnerStatus.setSelection(pos);
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Toast.makeText(TaskDetail.this, "Failed to load task", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void updateTaskStatus() {
        String newStatus = spinnerStatus.getSelectedItem().toString();
        taskRef.child("status").setValue(newStatus).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(this, "Status updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show();
            }
        });
    }
}


