package com.example.sqliteexample;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.google.ai.client.generativeai.common.shared.Content;

import java.util.ArrayList;

public class DBHelper extends SQLiteOpenHelper {
    // 전역 변수 선언
    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "hongdroid.db";
    public DBHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    // 데이터 베이스가 생성될 때 호출
    @Override
    public void onCreate(SQLiteDatabase db) {
        // 데이터베이스 -> 테이블 -> 컬럼 -> 값
        // 테이블 명 TodoList
        db.execSQL("CREATE TABLE IF NOT EXISTS   TodoList(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, content TEXT NOT NULL, writeDate TEXT NOT NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }


    // SELLECT 문 (할일 목록들을 조회)
    public ArrayList<TodoItem> getTodoList(){
        ArrayList<TodoItem> todoItems = new ArrayList<>();
        // DB를 읽어 오는 메소드
        SQLiteDatabase db = getReadableDatabase();
        // 가리키는 커서라는 객체
        Cursor cursor = db.rawQuery("SELECT * FROM TodoList ORDER BY writeDate DESC", null);
        if(cursor.getCount() != 0){
            // 조회온 데이터가  있을 때 내부 수행
            while(cursor.moveToNext()){
                @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex("id"));
                @SuppressLint("Range") String title = cursor.getString(cursor.getColumnIndex("title"));
                @SuppressLint("Range") String content = cursor.getString(cursor.getColumnIndex("content"));
                @SuppressLint("Range") String writeDate = cursor.getString(cursor.getColumnIndex("writeDate"));

                TodoItem todoItem = new TodoItem();
                todoItem.setId(id);
                todoItem.setTitle(title);
                todoItem.setContent(content);
                todoItem.setWriteDate(writeDate);
                todoItems.add(todoItem);
            }
        }
        cursor.close();
        return todoItems;
    }

    // INSERT 문 (할일 목록을 DB에 넣는다.)
    public void InsertTodo(String _title, String _content, String _writeDate){
        // DB에 쓸 수 있게끔 선언 자주 나올 메소드
        SQLiteDatabase db = getWritableDatabase();
        // DB 값을 실제 작성할 수 있는
        db.execSQL("INSERT INTO TodoList (title, content, writeDate) VALUES('" + _title + "','" + _content + "','" + _writeDate + "');");
    }

    // UPDATE 문 (할일 목록을 수정 한다.)
    public void UpdateTodo(String _title, String _content, String _writeDate, String _beforeDate){
        SQLiteDatabase db = getWritableDatabase();
        // key 값을 이용해서 업데이트 하는 명령어 작성
        db.execSQL("UPDATE TodoList SET title='" + _title + "', content='" + _content + "', writeDate='" + _writeDate + "' WHERE writeDate='" + _beforeDate + "'");
    }

    // DELETE 문 (할일 목록을 제거 한다.)
    public void deleteTodo(String _beforeDate){
        SQLiteDatabase db =  getWritableDatabase();
        db.execSQL("DELETE FROM TodoList WHERE writeDate='" + _beforeDate + "'");
    }

}