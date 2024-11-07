package com.example.sqliteexample;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder>
{
    // 전역변수 설정
    private final ArrayList<TodoItem> mTodoItems;
    private final Context mContext;
    private final DBHelper mDBHelper;

    // 생성자 선언
    public CustomAdapter(ArrayList<TodoItem> mTodoItems, Context mContext)
    {
        this.mTodoItems = mTodoItems;
        this.mContext = mContext;
        mDBHelper = new DBHelper(mContext);
    }

    @NonNull
    @Override
    public CustomAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        View holder = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        return new ViewHolder(holder);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomAdapter.ViewHolder holder, int position)
    {
        holder.tv_title.setText(mTodoItems.get(position).getTitle());
        holder.tv_content.setText(mTodoItems.get(position).getContent());
        holder.tv_writeDate.setText(mTodoItems.get(position).getWriteDate());
    }

    @Override
    public int getItemCount()
    {
        return mTodoItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder
    {

        private final TextView tv_title;
        private final TextView tv_content;
        private final TextView tv_writeDate;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            tv_title = itemView.findViewById(R.id.tv_title);
            tv_content = itemView.findViewById(R.id.tv_content);
            tv_writeDate = itemView.findViewById(R.id.tv_date);

            // Layout item_list에 있는 리스트 뷰를 의미한 (item View)
            itemView.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    // curPos 목적은 리스트 아이템 의 클릭한 위치
                    int curPos = getAdapterPosition();
                    TodoItem todoItem = mTodoItems.get(curPos);

                    String[] strChoiceItems =  {"수정하기", "삭제하기"};
                    AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                    builder.setTitle("원하는 작업을 선택해주세요");
                    builder.setItems(strChoiceItems, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int position)
                        {
                            // 여기서 positiond은 수정하기 삭제하기 배열의 위치임
                            if(position == 0){
                                // 수정 하기 옵션 영역
                                // 팝업 창 띄우기
                                Dialog dialog = new Dialog(mContext, android.R.style.Theme_Material_Light_Dialog);
                                dialog.setContentView(R.layout.dialog_edit);
                                EditText et_title = dialog.findViewById(R.id.et_title);
                                EditText et_content = dialog.findViewById(R.id.et_content);
                                Button btn_ok = dialog.findViewById(R.id.btn_ok);

                                et_title.setText(todoItem.getTitle());
                                et_content.setText(todoItem.getContent());

                                et_title.setSelection(et_title.getText().length());
                                
                                btn_ok.setOnClickListener(new View.OnClickListener()
                                {
                                    @Override
                                    public void onClick(View view)
                                    {
                                        // Update table
                                        String title = et_title.getText().toString();
                                        String content = et_content.getText().toString();
                                        String currentTime = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss").format(new Date());
                                        String beforeTime = todoItem.getWriteDate();

                                        try {
                                            mDBHelper.UpdateTodo(title, content, currentTime, beforeTime);
                                            // UI 업데이트
                                            todoItem.setTitle(title);
                                            todoItem.setContent(content);
                                            todoItem.setWriteDate(currentTime);
                                            notifyItemChanged(curPos); // UI에 변경사항 반영
                                            dialog.dismiss();
                                            Toast.makeText(mContext, "목록 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                        } catch (Exception e) {
                                            Toast.makeText(mContext, "수정 중 오류가 발생했습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                                dialog.show();

                            }
                            else if(position == 1){
                                // delete table
                                String beforeTime = todoItem.getWriteDate();
                                mDBHelper.deleteTodo(beforeTime);
                                // delete UI
                                mTodoItems.remove(curPos);
                                notifyItemRemoved(curPos);
                                Toast.makeText(mContext, "목록을 삭제했습니다.", Toast.LENGTH_SHORT).show();

                            }
                        }
                    });
                    builder.show();


                }
            });
        }
    }

    public void addItem(TodoItem _item){
        mTodoItems.add(0, _item);
        notifyItemInserted(0);

    }


}
