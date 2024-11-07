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
            // 初始化字符计数
            tv_char_count.setText(et_content.getText().length() + " 字");
        } else {
            tv_main_title.setText(" 添加心事 ");
            tv_time.setVisibility(View.GONE);
            // 初始化字符计数
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
                // Update character count
                tv_char_count.setText(s.length() + " 字");
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed after text changes
            }
        });
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId(); // 获取点击的视图 ID

        if (viewId == R.id.tv_back) { // 返回键的点击事件
            finish(); // 结束当前活动
        } else if (viewId == R.id.iv_del) { // "清空" 按钮的点击事件
            et_content.setText(""); // 清空内容输入框
        } else if (viewId == R.id.iv_save) { // "保存" 按钮的点击事件
            // 获取输入的事件标题
            String title = et_title.getText().toString().trim();
            // 获取输入的事件内容
            String content = et_content.getText().toString().trim();

            if (id != 0) { // 修改界面
                if (content.length() > 0) { // 检查内容是否为空
                    if (dbUtils.updateNote(id, title, content, DBUtils.getTime())) {
                        showToast("修改成功"); // 提示修改成功
                        setResult(2); // 设置结果码
                        finish(); // 结束当前活动
                    } else {
                        showToast("修改失败"); // 提示修改失败
                    }
                } else {
                    showToast("修改内容不能为空!"); // 提示内容不能为空
                }
            } else { // 添加记录界面
                if (content.length() > 0) { // 检查内容是否为空
                    if (dbUtils.saveNote(title, content, DBUtils.getTime())) {
                        showToast("保存成功"); // 提示保存成功
                        setResult(2); // 设置结果码
                        finish(); // 结束当前活动
                    } else {
                        showToast("保存失败"); // 提示保存失败
                    }
                } else {
                    showToast("添加的心事不能为空!"); // 提示内容不能为空
                }
            }
        }
    }

    public void showToast(String message) {
        Toast.makeText(RecordActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}