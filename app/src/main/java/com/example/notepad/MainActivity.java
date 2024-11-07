package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.notepad.adapter.NoteAdapter;
import com.example.notepad.bean.NoteBean;
import com.example.notepad.utils.DBUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<NoteBean> list;
    private DBUtils dbUtils;
    private NoteAdapter adapter;
    private RecyclerView rv_list;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("小熊软糖记事本");

        ImageView ivAdd = findViewById(R.id.iv_add);
        ivAdd.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RecordActivity.class);
            startActivity(intent);
        });

        init();
    }

    private void init() {
        dbUtils = new DBUtils(this);
        rv_list = findViewById(R.id.rv_list);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);

        rv_list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new NoteAdapter(this, new NoteAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View v, int position) {
                NoteBean bean = list.get(position);
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                intent.putExtra("id", bean.getId());
                intent.putExtra("title", bean.getTitle());
                intent.putExtra("content", bean.getContent());
                intent.putExtra("time", bean.getTime());
                startActivity(intent);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                // Handle long click
            }

            @Override
            public void onItemDelete(int position) {
                // Handle delete
            }
        });

        swipeRefreshLayout.setOnRefreshListener(() -> {
            // Handle refresh
            swipeRefreshLayout.setRefreshing(false);
        });

        showQueryData();
    }

    private void showQueryData() {
        if (list != null) {
            list.clear();
        }
        list = dbUtils.queryNote();
        adapter.setData(list);
        rv_list.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}