package com.example.bt1;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;

/** Serializes database work off the UI thread and closes it after pending jobs. */
final class DatabaseTasks implements AutoCloseable {
    private final ExecutorService worker = Executors.newSingleThreadExecutor();
    private final Handler main = new Handler(Looper.getMainLooper());
    private final SQLiteOpenHelper database;
    private final Context context;
    private volatile boolean closed;

    DatabaseTasks(Context context, SQLiteOpenHelper database) {
        this.context = context.getApplicationContext();
        this.database = database;
    }

    <T> void submit(Callable<T> query, Consumer<T> success) {
        submit(query, success, () -> { });
    }

    <T> void submit(Callable<T> query, Consumer<T> success, Runnable failure) {
        if (closed) return;
        worker.execute(() -> {
            try {
                T value = query.call();
                main.post(() -> { if (!closed) success.accept(value); });
            } catch (Exception error) {
                Log.e("Lab03Database", "Database operation failed", error);
                main.post(() -> {
                    if (!closed) {
                        Toast.makeText(context, R.string.database_error, Toast.LENGTH_LONG).show();
                        failure.run();
                    }
                });
            }
        });
    }

    @Override public void close() {
        if (closed) return;
        closed = true;
        worker.execute(database::close);
        worker.shutdown();
    }
}
