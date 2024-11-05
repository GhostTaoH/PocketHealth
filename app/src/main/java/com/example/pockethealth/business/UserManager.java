package com.example.pockethealth.business;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.example.pockethealth.data.DatabaseHelper;
import com.example.pockethealth.security.PasswordEncryptor;

import java.util.List;

public class UserManager {
    private DatabaseHelper databaseHelper;

    public UserManager(Context context){
        databaseHelper=new DatabaseHelper(context);
    }

    //使用 SQLiteStatement 进行批量用户插入
    public void addUsersUsingPreparedStatements(List<User> users){

        Log.d("TAG", "addUsersUsingPreparedStatements: here");
        SQLiteDatabase db = databaseHelper.getWritableDatabase();

        //可以进行预编译sql语句
        String sql = "INSERT INTO " + DatabaseHelper.TABLE_USERS + " ("
                + DatabaseHelper.COLUMN_USER_NAME + ", "
                + DatabaseHelper.COLUMN_USER_EMAIL + ", "
                + DatabaseHelper.COLUMN_USER_PASSWORD + ") VALUES (?, ?, ?)";

        //预编译后得到SQLiteStatement
        SQLiteStatement statement = db.compileStatement(sql);

        //使用事务进行读写操作
        db.beginTransaction();
        try{
            for (User user:users) {
                //清除statement残留数据
                statement.clearBindings();

                //为statement绑定数据
                statement.bindString(1, user.getUsername());  // 绑定用户名
                statement.bindString(2, user.getEmail());     // 绑定邮箱
                statement.bindString(3, user.getPassword());  // 绑定密码

                //将数据插入到数据库
                statement.execute();
            }
            //操作成功，关闭事务
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        db.close();
    }
    public boolean loginVerification(String email,String password){
        SQLiteDatabase db=databaseHelper.getReadableDatabase();
        //查询用户
        String query="SELECT * FROM users WHERE email=?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        //检查用户是否存在
        if(cursor!=null&&cursor.moveToFirst()){
            @SuppressLint("Range")
            String hashPassword = cursor.getString(cursor.getColumnIndex("password"));
            return PasswordEncryptor.checkPassword(password, hashPassword);
        }

        if (cursor != null) {
            cursor.close();
        }
        return false;
    }

}
