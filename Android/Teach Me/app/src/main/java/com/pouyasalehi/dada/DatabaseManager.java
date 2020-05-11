package com.pouyasalehi.dada;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Environment;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

/**
 * Created by Belal on 10/20/2017.
 */

//The class is extending SQLiteOpenHelper
public class DatabaseManager extends SQLiteOpenHelper {

    /*
     * This time we will not be using the hardcoded string values
     * Instead here we are defining all the Strings that is required for our database
     * for example databasename, table name and column names.
     * */
    private static final String DATABASE_NAME = "AndroidJSonTable";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_NAME = "user";


    /*
     * We need to call the super i.e. parent class constructur
     * And we need to pass 4 parameters
     * 1. Context context -> It is the context object we will get it from the activity while creating the instance
     * 2. String databasename -> It is the name of the database and here we are passing the constant that we already defined
     * 3. CursorFactory cursorFactory -> If we want a cursor to be initialized on the creation we can use cursor factory, it is optionall and that is why we passed null here
     * 4. int version -> It is an int defining our database version
     * */
    public DatabaseManager(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        /*
         * The query to create our table
         * It is same as we had in the previous post
         * The only difference here is we have changed the
         * hardcoded string values with String Variables
         * */


          String sql2="CREATE TABLE " + TABLE_NAME + " (" +
                  "username" + " varchar(200) NOT NULL,\n" +
                  "email" + " varchar(200) NOT NULL," +
                  "usertype" + " integer NOT NULL," +
                  "points" + " integer NOT NULL)";


        /*
         * Executing the string to create the table
         * */
        sqLiteDatabase.execSQL(sql2);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        String sql2="DROP TABLE IF EXISTS " + TABLE_NAME;
        sqLiteDatabase.execSQL(sql2);
        onCreate(sqLiteDatabase);
    }

    /*
     * CREATE OPERATION
     * ====================
     * This is the first operation of the CRUD.
     * This method will create a new employee in the table
     * Method is taking all the parameters required
     *
     * Operation is very simple, we just need a content value objects
     * Inside this object we will put everything that we want to insert.
     * So each value will take the column name and the value that is to inserted
     * for the column name we are using the String variables that we defined already
     * And that is why we converted the hardcoded string to variables
     *
     * Once we have the contentValues object with all the values required
     * We will call the method getWritableDatabase() and it will return us the SQLiteDatabase object and we can write on the database using it.
     *
     * With this object we will call the insert method it takes 3 parameters
     * 1. String -> The table name where the value is to be inserted
     * 2. String -> The default values of null columns, it is null here as we don't have any default values
     * 3. ContentValues -> The values that is to be inserted
     *
     * insert() will return the inserted row id, if there is some error inserting the row
     * it will return -1
     *
     * So here we are returning != -1, it will be true of record is inserted and false if not inserted
     * */

    public  boolean addEmployee(String username, String email, int usertype) {
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        contentValues.put("email", email);
        contentValues.put("usertype", usertype);
        contentValues.put("points", 0);
        SQLiteDatabase db = getWritableDatabase();
        return db.insert(TABLE_NAME, null, contentValues) != -1;
    }


    /*
     * READ OPERATION
     * =================
     * Here we are reading values from the database
     * First we called the getReadableDatabase() method it will return us the SQLiteDatabase instance
     * but using it we can only perform the read operations.
     *
     * We are running rawQuery() method by passing the select query.
     * rawQuery takes two parameters
     * 1. The query
     * 2. String[] -> Arguments that is to be binded -> We use it when we have a where clause in our query to bind the where value
     *
     * rawQuery returns a Cursor object having all the data fetched from database
     * */
    public Cursor getAllEmployees() {
        SQLiteDatabase db = getReadableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE_NAME+""+";", null);

    }

    /*
     * UPDATE OPERATION
     * ==================
     * Here we are performing the update operation. The proecess is same as the Create operation.
     * We are first getting a database instance using getWritableDatabase() method as the operation we need to perform is a write operation
     * Then we have the contentvalues object with the new values
     *
     * to update the row we use update() method. It takes 4 parameters
     * 1. String -> It is the table name
     * 2. ContentValues -> The new values
     * 3. String -> Here we pass the column name = ?, the column we want to use for putting the where clause
     * 4. String[] -> The values that is to be binded with the where clause
     * */
    public  void updateEmployee(String username) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("username", username);
        db.update(TABLE_NAME, contentValues, "username" + "=?", new String[]{username});
    }
    public  void updateEmployee2(int point,String username) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("points", point);
        db.update(TABLE_NAME, contentValues, "username" + "=?", new String[]{username});
    }
    private StorageReference riversRef2;
    private StorageReference mStorageRef;


    /*
     * DELETE OPERATION
     * ======================
     *
     * This is the last delete operation.  To delete again we need a writable database using getWritableDatabase()
     * Then we will call the delete method. It takes 3 parameters
     * 1. String -> Table name
     * 2. String -> The where clause passed as columnname = ?
     * 3. String[] -> The values to be binded on the where clause
     * */
   public void deleteEmployee(String id) {
        SQLiteDatabase db = getWritableDatabase();
        db.execSQL("delete from "+ TABLE_NAME);
    }
    }
