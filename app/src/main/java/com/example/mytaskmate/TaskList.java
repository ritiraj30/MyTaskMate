package com.example.mytaskmate;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TaskList extends AppCompatActivity {
    private RecyclerView recyclerView;
    private TaskAdapter adapter;
    private List<CreateTask.Task1> taskList = new ArrayList<>();

    private EditText editFilterUser;
    private Spinner spinnerFilterStatus;
    private Button buttonFilter;

    private DatabaseReference taskRef;
    private String currentUserId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_task_list);

        recyclerView = findViewById(R.id.recycler_tasks);
        editFilterUser = findViewById(R.id.edit_filter_user);
        spinnerFilterStatus = findViewById(R.id.spinner_filter_status);
        buttonFilter = findViewById(R.id.button_filter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
       // adapter = new TaskAdapter(taskList, this::onUpdateClicked, this::onDeleteClicked);
        recyclerView.setAdapter(adapter);

        currentUserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        taskRef = FirebaseDatabase.getInstance().getReference("Tasks");

        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item,
                Arrays.asList("All", "Pending", "In Progress", "Completed"));
        spinnerFilterStatus.setAdapter(statusAdapter);

        buttonFilter.setOnClickListener(v -> loadTasks());
        loadTasks(); // Initial load
    }

    private void loadTasks() {
        String filterUid = editFilterUser.getText().toString().trim();
        String filterStatus = spinnerFilterStatus.getSelectedItem().toString();

        taskRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                taskList.clear();
                for (DataSnapshot taskSnap : snapshot.getChildren()) {
                    CreateTask.Task1 task = taskSnap.getValue(CreateTask.Task1.class);
                    if (task == null) continue;

                    boolean matchUid = TextUtils.isEmpty(filterUid) || task.assignedTo.equals(filterUid);
                    boolean matchStatus = filterStatus.equals("All") || task.status.equals(filterStatus);

                    if (matchUid && matchStatus) {
                        taskList.add(task);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(TaskList.this, "Error loading tasks", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void onUpdateClicked(CreateTask.Task1 task) {
        Intent intent = new Intent(this, UpdateTaskActivity.class);
        intent.putExtra("taskId", task.taskId);
        startActivity(intent);
    }

    private void onDeleteClicked(CreateTask.Task1 task) {
        if (task.createdBy.equals(currentUserId)) {
            taskRef.child(task.taskId).removeValue()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(this, "Task deleted", Toast.LENGTH_SHORT).show();
                        loadTasks();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, "Failed to delete", Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(this, "You can only delete your own tasks", Toast.LENGTH_SHORT).show();
        }
    }
    public interface TaskClickListener {
        void onUpdate(CreateTask.Task1 task);
        void onDelete(CreateTask.Task1 task);
    }
}