package com.example.notepad;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.notepad.utils.DBUtils;

public class RecordActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView tv_time, tv_main_title, tv_back, tv_char_count;
    private EditText et_title, et_content;
    private ImageView iv_del, iv_save;
    private DBUtils dbUtils;
    private int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        initView();
        initData();
        setupTextWatcher();
    }

    private void initView() {
        tv_back = findViewById(R.id.tv_back);
        tv_back.setVisibility(View.VISIBLE);
        et_title = findViewById(R.id.et_title);
        et_content = findViewById(R.id.et_content);
        tv_time = findViewById(R.id.tv_time);
        iv_del = findViewById(R.id.iv_del);
        iv_save = findViewById(R.id.iv_save);
        tv_main_title = findViewById(R.id.tv_main_title);
        tv_char_count = findViewById(R.id.tv_char_count);

        tv_back.setOnClickListener(this);
        iv_del.setOnClickListener(this);
        iv_save.setOnClickListener(this);
    }

    private void initData() {
        dbUtils = new DBUtils(this);
        Intent intent = getIntent();
        id = intent.getIntExtra("id", 0);
        if (id != 0) {
            tv_main_title.setText(" 修改心事 ");
            et_title.setText(intent.getStringExtra("title"));
            et_content.setText(intent.getStringExtra("content"));
            tv_time.setText(intent.getStringExtra("time"));
            tv_time.setVisibility(View.VISIBLE);
            tv_char_count.setText(et_content.getText().length() + " 字");
        } else {
            tv_main_title.setText(" 添加心事 ");
            tv_time.setVisibility(View.GONE);
            tv_char_count.setText("0 字");
        }
    }

    private void setupTextWatcher() {
        et_content.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed before text changes
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                tv_char_count.setText(s.length() + " 字");
                autoSave();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
    }

    private void autoSave() {
        String title = et_title.getText().toString().trim();
        String content = et_content.getText().toString().trim();
        String time = DBUtils.getTime();

        if (id != 0) {
            if (content.length() > 0) {
                dbUtils.updateNote(id, title, content, time);
            }
        } else {
            if (content.length() > 0) {
                id = dbUtils.saveNoteAndGetId(title, content, time);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.tv_back) {
            autoSave();
            finish();
        } else if (viewId == R.id.iv_del) {
            et_content.setText("");
        } else if (viewId == R.id.iv_save) {
            autoSave();
            showToast("保存成功");
            setResult(2);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        autoSave();
        super.onBackPressed();
    }

    public void showToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}