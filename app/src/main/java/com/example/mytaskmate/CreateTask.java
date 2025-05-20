package com.example.mytaskmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreateTask extends AppCompatActivity {
    EditText editTitle, editDescription, editDueDate;
    Spinner spinnerPriority, spinnerStatus, spinnerAssignedUser;
    Button btnCreateTask, btnBack;

    DatabaseReference tasksRef, usersRef;
    FirebaseAuth mAuth;
    Map<String, String> userNameToUidMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_task);
        editTitle = findViewById(R.id.edit_title);
        editDescription = findViewById(R.id.edit_description);
        editDueDate = findViewById(R.id.edit_due_date);
        spinnerPriority = findViewById(R.id.spinner_priority);
        spinnerStatus = findViewById(R.id.spinner_status);
        spinnerAssignedUser = findViewById(R.id.spinner_assigned_user);
        btnCreateTask = findViewById(R.id.btn_create_task);
        btnBack= findViewById(R.id.btn_back);

        mAuth = FirebaseAuth.getInstance();
        tasksRef = FirebaseDatabase.getInstance().getReference("tasks");
        usersRef = FirebaseDatabase.getInstance().getReference("Users");

        loadPriorityOptions();
        loadStatusOptions();
        loadUsers();

        btnCreateTask.setOnClickListener(v -> createTask());

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateTask.this, ManagerDashboard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void loadPriorityOptions() {
        String[] priorities = {"Low", "Medium", "High"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, priorities);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerPriority.setAdapter(adapter);
    }

    private void loadStatusOptions() {
        String[] statuses = {"Pending", "In Progress", "Completed"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statuses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapter);
    }

    private void loadUsers() {
        usersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> userNames = new ArrayList<>();
                for (DataSnapshot userSnap : snapshot.getChildren()) {
                    String uid = userSnap.getKey();
                    String name = userSnap.child("name").getValue(String.class);
                    String role = userSnap.child("role").getValue(String.class);

                    // Only include users with role "User"
                    if (uid != null && name != null && "User".equals(role)) {
                        userNameToUidMap.put(name, uid);
                        userNames.add(name);
                    }
                }

                if (userNames.isEmpty()) {
                    Toast.makeText(CreateTask.this, "No users available for assignment", Toast.LENGTH_SHORT).show();
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CreateTask.this, android.R.layout.simple_spinner_item, userNames);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerAssignedUser.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(CreateTask.this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void createTask() {
        String title = editTitle.getText().toString().trim();
        String description = editDescription.getText().toString().trim();
        String dueDate = editDueDate.getText().toString().trim();

        Object priorityObj = spinnerPriority.getSelectedItem();
        Object statusObj = spinnerStatus.getSelectedItem();
        Object assignedUserObj = spinnerAssignedUser.getSelectedItem();

        if (TextUtils.isEmpty(title)) {
            Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show();
            return;
        }

        if (assignedUserObj == null || priorityObj == null || statusObj == null) {
            Toast.makeText(this, "Please select all dropdown values", Toast.LENGTH_SHORT).show();
            return;
        }


        String priority = priorityObj.toString();
        String status = statusObj.toString();
        String assignedToName = assignedUserObj.toString();

        String assignedToUid = userNameToUidMap.get(assignedToName);
        if (assignedToUid == null) {
            Toast.makeText(this, "Selected user is invalid", Toast.LENGTH_SHORT).show();
            return;
        }

        String createdBy = mAuth.getCurrentUser() != null ? mAuth.getCurrentUser().getUid() : "unknown";

        long timestamp = System.currentTimeMillis();

        String taskId = tasksRef.push().getKey();

        Task1 task = new Task1(taskId, title, description, dueDate, priority, status,
                assignedToUid, createdBy, timestamp);

        tasksRef.child(taskId).setValue(task)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Task Created", Toast.LENGTH_SHORT).show();
                    clearFields();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to Create Task", Toast.LENGTH_SHORT).show());
    }
    private void clearFields() {
        editTitle.setText("");
        editDescription.setText("");
        editDueDate.setText("");
        spinnerPriority.setSelection(0);
        spinnerStatus.setSelection(0);
        spinnerAssignedUser.setSelection(0);
    }
    public class Task1 {
        public String taskId;
        public String title;
        public String description;
        public String dueDate;
        public String priority;
        public String status;
        public String assignedTo;
        public String createdBy;
        public long timestamp;

        // Default constructor required for Firebase
        public Task1() {
        }

        public Task1(String taskId, String title, String description, String dueDate,
                    String priority, String status, String assignedTo,
                    String createdBy, long timestamp) {
            this.taskId = taskId;
            this.title = title;
            this.description = description;
            this.dueDate = dueDate;
            this.priority = priority;
            this.status = status;
            this.assignedTo = assignedTo;
            this.createdBy = createdBy;
            this.timestamp = timestamp;
        }
    }
    }
