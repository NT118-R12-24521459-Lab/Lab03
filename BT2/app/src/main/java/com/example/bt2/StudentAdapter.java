package com.example.bt2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

final class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.Holder> {
    private final Consumer<Student> onSelected;
    private final List<Student> students = new ArrayList<>();

    StudentAdapter(Consumer<Student> onSelected) {
        this.onSelected = onSelected;
    }

    void replace(List<Student> newStudents) {
        students.clear();
        students.addAll(newStudents);
        notifyDataSetChanged();
    }

    @NonNull @Override public Holder onCreateViewHolder(@NonNull ViewGroup parent, int type) {
        View row = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_student, parent, false);
        return new Holder(row);
    }

    @Override public void onBindViewHolder(@NonNull Holder holder, int position) {
        Student student = students.get(position);
        holder.name.setText(student.getName());
        holder.code.setText(holder.itemView.getContext()
                .getString(R.string.student_id, student.getId()));
        holder.major.setText(student.getMajor());
        holder.major.setVisibility(student.getMajor().isEmpty() ? View.GONE : View.VISIBLE);
        holder.itemView.setOnClickListener(view -> onSelected.accept(student));
    }

    @Override public int getItemCount() {
        return students.size();
    }

    static final class Holder extends RecyclerView.ViewHolder {
        final TextView name;
        final TextView code;
        final TextView major;

        Holder(View item) {
            super(item);
            name = item.findViewById(R.id.studentName);
            code = item.findViewById(R.id.studentCode);
            major = item.findViewById(R.id.studentMajor);
        }
    }
}
