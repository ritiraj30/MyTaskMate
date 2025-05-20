package com.example.mytaskmate;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UpdateTaskActivity extends AppCompatActivity {
    private EditText editTitle, editDescription, editDueDate;
    private Spinner spinnerPriority, spinnerStatus;
    private Button buttonUpdate;

    private String taskId;
    private DatabaseReference tasksRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_update_task);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editDueDate = findViewById(R.id.edit_due_date);
        spinnerPriority = findViewById(R.id.spinner_priority);
        spinnerStatus = findViewById(R.id.spinner_status);
        buttonUpdate = findViewById(R.id.button_update);

        ArrayAdapter<String> priorityAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Low", "Medium", "High"});
        spinnerPriority.setAdapter(priorityAdapter);

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                new String[]{"Pending", "In Progress", "Completed"});
        spinnerStatus.setAdapter(statusAdapter);

        taskId = getIntent().getStringExtra("taskId");
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");

        buttonUpdate.setOnClickListener(v -> updateTask());
    }

    private void updateTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String dueDate = editDueDate.getText().toString().trim();
        String priority = spinnerPriority.getSelectedItem().toString();
        String status = spinnerStatus.getSelectedItem().toString();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Title required", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(taskId)) {
            Toast.makeText(this, "Task ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        tasksRef.child(taskId).child("title").setValue(title);
        tasksRef.child(taskId).child("description").setValue(description);
        tasksRef.child(taskId).child("dueDate").setValue(dueDate);
        tasksRef.child(taskId).child("priority").setValue(priority);
        tasksRef.child(taskId).child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Task updated", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Update failed", Toast.LENGTH_SHORT).show());
    }
}