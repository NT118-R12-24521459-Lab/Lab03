package com.example.bt2;

import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

/** Shared screen setup; each screen owns and closes its database connection. */
public abstract class DatabaseActivity extends AppCompatActivity {
    public static final String EXTRA_STUDENT_ID = "com.example.bt2.STUDENT_ID";
    protected DatabaseHandler database;
    protected DatabaseTasks tasks;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        database = new DatabaseHandler(this);
        tasks = new DatabaseTasks(this, database);
    }

    protected void applyInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout() | WindowInsetsCompat.Type.ime());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });
    }

    protected void toast(int message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show(); }
    @Override protected void onDestroy() {
        tasks.close();
        super.onDestroy();
    }
}
