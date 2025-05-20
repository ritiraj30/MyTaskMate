package com.example.mytaskmate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class TaskAdapter extends RecyclerView.Adapter<TaskAdapter.TaskViewHolder> {

    public interface TaskClickListener {
        void onUpdate(CreateTask.Task1 task);
        void onDelete(CreateTask.Task1 task);
    }

    private List<CreateTask.Task1> taskList;
    private TaskClickListener listener;

    public TaskAdapter(List<CreateTask.Task1> taskList, TaskClickListener updateListener, TaskClickListener deleteListener) {
        this.taskList = taskList;
        this.listener = new TaskClickListener() {
            @Override public void onUpdate(CreateTask.Task1 task) { updateListener.onUpdate(task); }
            @Override public void onDelete(CreateTask.Task1 task) { deleteListener.onDelete(task); }
        };
    }

    @NonNull
    @Override
    public TaskViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_task, parent, false);
        return new TaskViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TaskViewHolder holder, int position) {
        CreateTask.Task1 task = taskList.get(position);
        holder.bind(task, listener);
    }

    @Override
    public int getItemCount() {
        return taskList.size();
    }

    static class TaskViewHolder extends RecyclerView.ViewHolder {
        TextView txtTitle, txtStatus;
        Button btnUpdate, btnDelete;

        public TaskViewHolder(@NonNull View itemView) {
            super(itemView);
            txtTitle = itemView.findViewById(R.id.text_task_title);
            txtStatus = itemView.findViewById(R.id.text_task_status);
            btnUpdate = itemView.findViewById(R.id.button_update_task);
            btnDelete = itemView.findViewById(R.id.button_delete_task);
        }

        public void bind(CreateTask.Task1 task, TaskClickListener listener) {
            txtTitle.setText(task.title);
            txtStatus.setText("Status: " + task.status);

            btnUpdate.setOnClickListener(v -> listener.onUpdate(task));
            btnDelete.setOnClickListener(v -> listener.onDelete(task));
        }
    }
}