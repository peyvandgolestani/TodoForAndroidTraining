package com.dalman.mytodoapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class StatisticsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);
        ArrayList<Task>  tasks = getIntent().getParcelableArrayListExtra(TaskListActivity.KEY_TASKS);
        int total = tasks.size();
        int completed = 0;
        int archived = 0;
        for (Task task : tasks) {
            if (task.isCompleted()){
                completed++;
            }
            if (task.isArchived()){
                archived++;
            }
        }
        TextView tasksTotal = (TextView) findViewById(R.id.tasks_total);
        tasksTotal.setText(String.valueOf(total));
        TextView tasksCompleted = (TextView) findViewById(R.id.tasks_completed);
        tasksCompleted.setText(String.valueOf(completed));
        TextView tasksArchived = (TextView) findViewById(R.id.tasks_archived);
        tasksArchived.setText((String.valueOf(archived)));


    }

}
