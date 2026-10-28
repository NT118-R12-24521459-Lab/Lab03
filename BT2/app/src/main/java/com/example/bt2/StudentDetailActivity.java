package com.example.bt2;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

public class StudentDetailActivity extends DatabaseActivity {
    private String studentId;
    private Student current;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_student_detail);
        applyInsets();
        studentId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        if (studentId == null) { finish(); return; }
        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        findViewById(R.id.editButton).setEnabled(false);
        findViewById(R.id.deleteButton).setEnabled(false);
        findViewById(R.id.editButton).setOnClickListener(view ->
                startActivity(new Intent(this, StudentEditorActivity.class).putExtra(EXTRA_STUDENT_ID, studentId)));
        findViewById(R.id.deleteButton).setOnClickListener(view -> confirmDelete());
    }

    @Override protected void onResume() {
        super.onResume();
        if (studentId == null) return;
        tasks.<Student>submit(() -> database.getStudent(studentId), student -> {
            if (student == null) { toast(R.string.missing_student); finish(); return; }
            current = student;
            setText(R.id.detailName, student.getName());
            setText(R.id.detailCode, student.getId());
            setText(R.id.detailMajor, student.getMajor().isEmpty()
                    ? getString(R.string.major_not_selected) : student.getMajor());
            findViewById(R.id.editButton).setEnabled(true);
            findViewById(R.id.deleteButton).setEnabled(true);
        });
    }

    private void setText(int id, String text) { ((TextView) findViewById(id)).setText(text); }

    private void confirmDelete() {
        if (current == null) return;
        new MaterialAlertDialogBuilder(this).setTitle(R.string.delete_title)
                .setMessage(getString(R.string.delete_message, current.getName(), current.getId()))
                .setNegativeButton(R.string.cancel, null)
                .setPositiveButton(R.string.delete, (dialog, which) -> {
                    findViewById(R.id.deleteButton).setEnabled(false);
                    tasks.<Integer>submit(() -> database.deleteStudent(studentId), rows -> {
                        toast(rows == 1 ? R.string.deleted : R.string.missing_student);
                        finish();
                    }, () -> findViewById(R.id.deleteButton).setEnabled(true));
                }).show();
    }
}
