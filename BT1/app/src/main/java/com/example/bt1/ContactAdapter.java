package com.example.bt1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

final class ContactAdapter extends ArrayAdapter<Contact> {
    ContactAdapter(Context context) { super(context, 0, new ArrayList<>()); }

    void replace(List<Contact> contacts) {
        setNotifyOnChange(false);
        clear();
        addAll(contacts);
        notifyDataSetChanged();
    }

    @NonNull @Override public View getView(int position, @Nullable View recycled,
                                          @NonNull ViewGroup parent) {
        View row = recycled;
        if (row == null) {
            row = LayoutInflater.from(getContext()).inflate(R.layout.item_contact, parent, false);
            row.setTag(new Holder(row));
        }
        Holder holder = (Holder) row.getTag();
        Contact contact = getItem(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhoneNumber());
        holder.id.setText(String.valueOf(contact.getId()));
        row.setBackgroundColor(getContext().getColor(position % 2 == 0
                ? R.color.surface : R.color.row_alt));
        return row;
    }

    private static final class Holder {
        final TextView id, name, phone;
        Holder(View row) {
            id = row.findViewById(R.id.contactId);
            name = row.findViewById(R.id.contactName);
            phone = row.findViewById(R.id.contactPhone);
        }
    }
}
