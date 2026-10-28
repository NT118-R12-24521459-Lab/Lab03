package com.example.bt2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class MainActivity extends DatabaseActivity {
    private StudentAdapter adapter;
    private View empty;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_main);
        applyInsets();

        empty = findViewById(R.id.emptyView);
        RecyclerView list = findViewById(R.id.studentList);
        list.setLayoutManager(new LinearLayoutManager(this));
        adapter = new StudentAdapter(student -> startActivity(new Intent(this,
                StudentDetailActivity.class).putExtra(EXTRA_STUDENT_ID, student.getId())));
        list.setAdapter(adapter);
        findViewById(R.id.addButton).setOnClickListener(view ->
                startActivity(new Intent(this, StudentEditorActivity.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        tasks.<List<Student>>submit(database::getAllStudents, students -> {
            adapter.replace(students);
            empty.setVisibility(students.isEmpty() ? View.VISIBLE : View.GONE);
        });
    }
}
