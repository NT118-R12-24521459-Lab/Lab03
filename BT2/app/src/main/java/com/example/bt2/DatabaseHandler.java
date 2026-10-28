package com.example.bt2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public final class DatabaseHandler extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "studentManager";
    private static final int DATABASE_VERSION = 3;
    private static final String TABLE = "students";

    public DatabaseHandler(Context context) {
        this(context, DATABASE_NAME);
    }

    DatabaseHandler(Context context, String databaseName) {
        super(context.getApplicationContext(), databaseName, null, DATABASE_VERSION);
    }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE students (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, "
                + "major TEXT NOT NULL DEFAULT '')");
        db.insertOrThrow(TABLE, null, values(new Student("24521459", "Võ Minh Quân")));
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("CREATE TABLE students_new (id TEXT PRIMARY KEY NOT NULL, name TEXT NOT NULL, "
                    + "major TEXT NOT NULL DEFAULT '')");
            db.execSQL("INSERT INTO students_new (id, name, major) SELECT id, name, major FROM students "
                    + "WHERE id NOT IN ('DEMO01', 'DEMO02')");
            db.execSQL("DROP TABLE students");
            db.execSQL("ALTER TABLE students_new RENAME TO students");
        } else if (oldVersion < 3) {
            db.execSQL("ALTER TABLE students ADD COLUMN major TEXT NOT NULL DEFAULT ''");
        }
    }

    private ContentValues values(Student student) {
        ContentValues values = new ContentValues();
        values.put("id", student.getId());
        values.put("name", student.getName());
        values.put("major", student.getMajor());
        return values;
    }

    /** Returns false for a duplicate primary key; other SQLite failures are propagated. */
    public boolean addStudent(Student student) {
        try {
            getWritableDatabase().insertOrThrow(TABLE, null, values(student));
            return true;
        } catch (SQLiteConstraintException duplicate) {
            if (getStudent(student.getId()) != null) return false;
            throw duplicate;
        }
    }

    public Student getStudent(String id) {
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, "id = ?",
                new String[]{id}, null, null, null)) {
            return cursor.moveToFirst() ? readStudent(cursor) : null;
        }
    }

    public List<Student> getAllStudents() {
        List<Student> students = new ArrayList<>();
        try (Cursor cursor = getReadableDatabase().query(TABLE, null, null, null, null,
                null, "name COLLATE NOCASE ASC, id ASC")) {
            while (cursor.moveToNext()) students.add(readStudent(cursor));
        }
        return students;
    }

    public int updateStudent(Student student) {
        return getWritableDatabase().update(TABLE, values(student), "id = ?", new String[]{student.getId()});
    }

    public int deleteStudent(String id) {
        return getWritableDatabase().delete(TABLE, "id = ?", new String[]{id});
    }

    private Student readStudent(Cursor cursor) {
        return new Student(cursor.getString(cursor.getColumnIndexOrThrow("id")),
                cursor.getString(cursor.getColumnIndexOrThrow("name")),
                cursor.getString(cursor.getColumnIndexOrThrow("major")));
    }
}
