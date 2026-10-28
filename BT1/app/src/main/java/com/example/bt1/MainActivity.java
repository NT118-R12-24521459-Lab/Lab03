package com.example.bt1;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private DatabaseHandler database;
    private DatabaseTasks tasks;
    private ContactAdapter adapter;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (view, insets) -> {
            Insets bars = insets.getInsets(WindowInsetsCompat.Type.systemBars()
                    | WindowInsetsCompat.Type.displayCutout());
            view.setPadding(bars.left, bars.top, bars.right, bars.bottom);
            return insets;
        });

        database = new DatabaseHandler(this);
        tasks = new DatabaseTasks(this, database);
        adapter = new ContactAdapter(this);
        ListView list = findViewById(R.id.contactList);
        list.setAdapter(adapter);
        list.setEmptyView(findViewById(R.id.emptyView));
        list.setOnItemLongClickListener((parent, view, position, id) -> {
            Contact contact = adapter.getItem(position);
            tasks.submit(() -> database.deleteContact(contact), rows -> {
                Toast.makeText(this, rows == 1 ? R.string.deleted : R.string.missing_contact,
                        Toast.LENGTH_SHORT).show();
                reload();
            });
            return true;
        });
        reload();
    }

    private void reload() {
        tasks.submit(database::getAllContacts, adapter::replace);
    }

    @Override protected void onDestroy() {
        tasks.close();
        super.onDestroy();
    }
}
