package com.example.bt2;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Spinner;
import androidx.annotation.NonNull;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;

public class StudentEditorActivity extends DatabaseActivity {
    private EditText code, name;
    private Spinner major;
    private TextInputLayout codeField, nameField;
    private MaterialButton save;
    private String editingId;
    private boolean loading;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.activity_student_editor);
        applyInsets();
        code = findViewById(R.id.codeInput);
        name = findViewById(R.id.nameInput);
        major = findViewById(R.id.majorInput);
        codeField = findViewById(R.id.codeField);
        nameField = findViewById(R.id.nameField);
        save = findViewById(R.id.saveButton);
        editingId = getIntent().getStringExtra(EXTRA_STUDENT_ID);
        findViewById(R.id.backButton).setOnClickListener(view -> finish());
        save.setOnClickListener(view -> saveStudent());
        if (editingId != null) {
            ((TextView) findViewById(R.id.editorTitle)).setText(R.string.edit_student);
            codeField.setEnabled(false);
            codeField.setHelperText(getString(R.string.fixed_code_note));
            if (state == null) loadStudent();
        }
    }

    private void loadStudent() {
        loading = true;
        save.setEnabled(false);
        tasks.<Student>submit(() -> database.getStudent(editingId), student -> {
            loading = false;
            if (student == null) { toast(R.string.missing_student); finish(); return; }
            code.setText(student.getId());
            name.setText(student.getName());
            for (int i = 0; i < major.getCount(); i++) {
                if (major.getItemAtPosition(i).toString().equals(student.getMajor())) {
                    major.setSelection(i);
                    break;
                }
            }
            save.setEnabled(true);
        }, this::finish);
    }

    private String value(EditText input) { return input.getText().toString().trim(); }

    private void saveStudent() {
        if (loading) return;
        String id = editingId == null ? value(code) : editingId;
        boolean validCode = id.matches("[A-Za-z0-9_-]{1,20}");
        boolean validName = !value(name).isEmpty();
        codeField.setError(validCode ? null : getString(R.string.code_invalid));
        nameField.setError(validName ? null : getString(R.string.name_required));
        if (!validCode || !validName) return;
        String selectedMajor = major.getSelectedItemPosition() == 0
                ? "" : major.getSelectedItem().toString();
        Student student = new Student(id, value(name), selectedMajor);
        save.setEnabled(false);
        save.setText(R.string.saving);
        tasks.<Boolean>submit(() -> editingId == null ? database.addStudent(student)
                : database.updateStudent(student) == 1, saved -> {
            if (saved) {
                toast(R.string.saved);
                finish();
            } else if (editingId == null) {
                codeField.setError(getString(R.string.duplicate_code));
                resetSaveButton();
            } else {
                toast(R.string.missing_student);
                finish();
            }
        }, this::resetSaveButton);
    }

    private void resetSaveButton() { save.setEnabled(true); save.setText(R.string.save); }

    @Override protected void onSaveInstanceState(@NonNull Bundle state) {
        state.putBoolean("loading", loading);
        super.onSaveInstanceState(state);
    }

    @Override protected void onRestoreInstanceState(@NonNull Bundle state) {
        super.onRestoreInstanceState(state);
        // Đọc lại nếu xoay màn hình khi đang tải; giữ nội dung đã nhập khi tải xong.
        if (state.getBoolean("loading")) loadStudent();
        else resetSaveButton();
    }
}
