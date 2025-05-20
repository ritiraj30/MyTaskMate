package com.example.mytaskmate;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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

import java.util.ArrayList;
import java.util.List;

public class UserDashboard extends AppCompatActivity {
    private ListView taskListView;
    private ArrayAdapter<String> adapter;
    private List<String> taskTitles = new ArrayList<>();
    private List<String> taskIds = new ArrayList<>();

    private DatabaseReference taskRef;
    private String userId ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_user_dashboard);
        taskListView = findViewById(R.id.taskListView);
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, taskTitles);
        taskListView.setAdapter(adapter);

        taskRef = FirebaseDatabase.getInstance().getReference("tasks");
        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        loadAssignedTasks();

        taskListView.setOnItemClickListener((parent, view, position, id) -> {
            String selectedTaskId = taskIds.get(position);
            Intent intent = new Intent(this, TaskDetail.class);
            intent.putExtra("taskId", selectedTaskId);
            startActivity(intent);
        });
    }

    private void loadAssignedTasks() {
        taskRef.orderByChild("assignedTo").equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        taskTitles.clear();
                        taskIds.clear();
                        for (DataSnapshot taskSnapshot : snapshot.getChildren()) {
                            String title = taskSnapshot.child("title").getValue(String.class);
                            taskTitles.add(title);
                            taskIds.add(taskSnapshot.getKey());
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        Toast.makeText(UserDashboard.this, "Failed to load tasks", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}