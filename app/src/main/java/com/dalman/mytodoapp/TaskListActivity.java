package com.dalman.mytodoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class TaskListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String KEY_TASKS = "tasks";
    private TasksAdapter tasksAdapter;
    private int selectId  = R.id.action_filter_all;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        TaskStorageHelper storageHelper = TaskStorageHelper.getInstance();

        if (savedInstanceState == null) {
            storageHelper.initStorage(this);
            FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(view.getContext(), TaskDetailActivity.class);
                    startActivity(intent);
                }
            });
        }

        RecyclerView tasksList = (RecyclerView) findViewById(R.id.task_list);
        tasksAdapter = new TasksAdapter();
        tasksList.setAdapter(tasksAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        showFilteredTasks(selectId);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.filters, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        showFilteredTasks(id);

        return true;

    }

    private void showFilteredTasks(int id) {
        selectId = id;
        List<Task> tasks = TaskStorageHelper.getInstance().getTasks();
        ArrayList<Task> filtered = new ArrayList<>();
        for (Task task : tasks) {
            if (id == R.id.action_filter_all) {
                filtered.add(task);

            } else if (id == R.id.action_filter_ongoing) {
                if (!task.isCompleted() && !task.isArchived()){
                    filtered.add(task);
                }

            } else if (id == R.id.action_filter_completed){
                if (task.isCompleted()) {
                    filtered.add(task);
                }
            } else if (id == R.id.action_filter_archived){
                    if (task.isArchived()){
                        filtered.add(task);
                }

            }
        }
        tasksAdapter.setTasks(filtered);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item)
    {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if (id == R.id.nav_tasks) {
            drawer.closeDrawer(GravityCompat.START);
        }
        else if (id == R.id.nav_add_task) {
            Intent intent = new Intent(this, TaskDetailActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_statistic) {
            Intent statisticIntent = new Intent(this, StatisticsActivity.class );
            ArrayList<Task> tasks = new ArrayList<>(TaskStorageHelper.getInstance().getTasks());
            statisticIntent.putParcelableArrayListExtra(KEY_TASKS, tasks);
            startActivity(statisticIntent);


        } else if (id == R.id.nav_information) {
            Intent intent = new Intent(this, InformationActivity.class);
            startActivity(intent);
        }


        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private class TasksAdapter extends RecyclerView.Adapter<TaskListActivity.TasksAdapter.TaskViewHolder> {
        private List<Task> tasks = new ArrayList<>();

        public void setTasks(List<Task> tasks) {
            this.tasks = tasks;
            notifyDataSetChanged();
        }

        @Override
        public TaskListActivity.TasksAdapter.TaskViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater inflater = LayoutInflater.from(parent.getContext());
            View itemView = inflater.inflate(R.layout.task_item, parent, false);
            return new TaskListActivity.TasksAdapter.TaskViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(TaskListActivity.TasksAdapter.TaskViewHolder holder, int position) {
            Task task = tasks.get(position);
            holder.title.setText(task.getTitle());
            holder.description.setText(task.getDescription());
            holder.completed.setChecked(task.isCompleted());
        }

        @Override
        public int getItemCount() {
            return tasks.size();
        }

        class TaskViewHolder extends RecyclerView.ViewHolder {
            final TextView title;
            final TextView description;
            final CheckBox completed;

            TaskViewHolder(View itemView) {
                super(itemView);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int position = getAdapterPosition();
                        Task task = tasks.get(position);
                        Intent intent = new Intent(v.getContext(), TaskDetailActivity.class);
                        intent.putExtra(TaskDetailActivity.KEY_TASK, task);
                        startActivity(intent);
                    }
                });

                title = (TextView) itemView.findViewById(R.id.title);
                description = (TextView) itemView.findViewById(R.id.description);
                completed = (CheckBox) itemView.findViewById(R.id.completed);

                completed.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        int position = getAdapterPosition();
                        Task task = tasks.get(position);

                        if (task.isCompleted() != isChecked){
                            task.setCompleted(isChecked);
                            TaskStorageHelper.getInstance().saveTask(task);
                            showFilteredTasks(selectId);


                        }


                    }
                });
            }
        }
    }

}
