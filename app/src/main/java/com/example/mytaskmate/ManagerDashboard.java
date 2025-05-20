package com.example.mytaskmate;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class ManagerDashboard extends AppCompatActivity {

    Button btnViewUser, btnCreateTask, btnListMyTask,btnLogout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_manager_dashboard);
        btnViewUser = findViewById(R.id.btnViewUser);
        btnCreateTask = findViewById(R.id.btnCreateTask);
        btnListMyTask = findViewById(R.id.btnListMyTask);
        btnLogout = findViewById(R.id.Logout);
        btnViewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to View User activity
                Intent intent = new Intent(ManagerDashboard.this, ViewUserActivity.class);
                startActivity(intent);
            }
        });

        btnCreateTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to Create Task activity
                Intent intent = new Intent(ManagerDashboard.this, CreateTask.class);
                startActivity(intent);
            }
        });

        btnListMyTask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Navigate to List My Task activity
                Intent intent = new Intent(ManagerDashboard.this, TaskList.class);
                startActivity(intent);
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
                Intent intent = new Intent(ManagerDashboard.this, Login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Clear back stack
                startActivity(intent);
                finish();
            }
        });
    }
}