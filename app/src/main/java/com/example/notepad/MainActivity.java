package com.example.notepad;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.adapter.NoteAdapter;
import com.example.notepad.bean.NoteBean;
import com.example.notepad.utils.DBUtils;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    private List<NoteBean> list;
    private DBUtils dbUtils;
    private NoteAdapter adapter;
    private RecyclerView rv_list;
    private ImageView iv_add;
    private TextView tv_main_title;
    private ActivityResultLauncher<Intent> launcher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLauncher();
        init();
        setupGlobalTouchListener();
    }

    private void initLauncher() {
        launcher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        int resultCode = result.getResultCode();
                        if (resultCode == 2) {
                            showQueryData();
                        }
                    }
                }
        );
    }

    private void init() {
        dbUtils = new DBUtils(this);
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_main_title.setText("小熊软糖心事本");
        rv_list = findViewById(R.id.rv_list);
        iv_add = findViewById(R.id.iv_add);
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
                launcher.launch(intent);
            }

            @Override
            public void onItemLongClick(View v, int position) {
                AlertDialog dialog;
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.iv_del)
                        .setTitle("删除提示")
                        .setMessage("是否删除此记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteBean noteBean = list.get(position);
                                if (dbUtils.deleteNote(noteBean.getId())) {
                                    list.remove(position);
                                    adapter.notifyDataSetChanged();
                                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                dialog = builder.create();
                dialog.show();
            }
            @Override
            public void onItemDelete(int position) {
                NoteBean noteBean = list.get(position);
                if (dbUtils.deleteNote(noteBean.getId())) {
                    list.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, list.size());
                    Toast.makeText(MainActivity.this, "删除成功", Toast.LENGTH_SHORT).show();

                    // Check if the list is empty and update the visibility of the initial message and RecyclerView
                    TextView tvInitialMessage = findViewById(R.id.tv_initial_message);
                    if (list.isEmpty()) {
                        tvInitialMessage.setVisibility(View.VISIBLE);
                        rv_list.setVisibility(View.GONE);
                    } else {
                        tvInitialMessage.setVisibility(View.GONE);
                        rv_list.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        iv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RecordActivity.class);
                launcher.launch(intent);
            }
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

        TextView tvInitialMessage = findViewById(R.id.tv_initial_message);
        if (list.isEmpty()) {
            tvInitialMessage.setVisibility(View.VISIBLE);
            rv_list.setVisibility(View.GONE);
        } else {
            tvInitialMessage.setVisibility(View.GONE);
            rv_list.setVisibility(View.VISIBLE);
        }
    }

    private void setupGlobalTouchListener() {
        View rootView = findViewById(android.R.id.content);
        rootView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (adapter.getOpenSwipeLayout() != null) {
                    adapter.getOpenSwipeLayout().close(true);
                    adapter.setOpenSwipeLayout(null);
                }
                return false;
            }
        });
    }
}