package com.example.sqliteexample;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private RecyclerView  mRv_todo;
    private FloatingActionButton mBtn_write;
    private ArrayList<TodoItem> mTodoItems;
    private DBHelper mDBHelper;
    private CustomAdapter mAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setInit();
    }

    private void setInit() {

        mDBHelper = new DBHelper(this);
        mRv_todo = findViewById(R.id.rv_todo);
        mBtn_write = findViewById(R.id.btn_wirte);
        mTodoItems = new ArrayList<>();

        // load recent db
        loadResentDB();

        // MainActivity Layout에 있는 버튼를 눌렀을 때 이벤트 발생
        mBtn_write.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view)
            {
                // 팝업 창 띄우기
                Dialog dialog = new Dialog(MainActivity.this, android.R.style.Theme_Material_Light_Dialog);
                // 레이아웃 불러오기
                dialog.setContentView(R.layout.dialog_edit);

                EditText et_title = dialog.findViewById(R.id.et_title);
                EditText et_content = dialog.findViewById(R.id.et_content);

                Button btn_ok = dialog.findViewById(R.id.btn_ok);

                btn_ok.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view)
                    {
                        // Insert DB
                        String currentTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());            // 현재 시간 연월일시분초 받아오기
                        mDBHelper.InsertTodo(et_title.getText().toString(), et_content.getText().toString(), currentTime);     // BD에 테이블에 넣기

                        // Insert UI
                        TodoItem item = new TodoItem();
                        item.setTitle(et_title.getText().toString());
                        item.setContent(et_content.getText().toString());
                        item.setWriteDate(currentTime);

                        mAdapter.addItem(item);
                        mRv_todo.smoothScrollToPosition(0);
                        dialog.dismiss();
                        Toast.makeText(MainActivity.this,"할일 목록에 추가 되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
                dialog.show();

            }
        });



    }

    private void loadResentDB()
    {
        // 저장되어 있던  DB를 가져온다.
        mTodoItems = mDBHelper.getTodoList();
        if(mAdapter == null)
        {
            mAdapter = new CustomAdapter(mTodoItems, this);
            mRv_todo.setHasFixedSize(true);
            mRv_todo.setAdapter(mAdapter);
        }

    }
}