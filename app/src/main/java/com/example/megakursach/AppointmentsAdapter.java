package com.example.megakursach;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

public class AppointmentsAdapter extends BaseAdapter {
    private Context context;
    private List<Appointment> appointments;

    public AppointmentsAdapter(Context context, List<Appointment> appointments) {
        this.context = context;
        this.appointments = appointments;
    }

    @Override
    public int getCount() {
        return appointments.size();
    }

    @Override
    public Object getItem(int position) {
        return appointments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return appointments.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.list_item_appointment, parent, false);
        }

        Appointment appointment = appointments.get(position);

        TextView tvTitle = convertView.findViewById(R.id.tvTitle);
        TextView tvDescription = convertView.findViewById(R.id.tvDescription);
        TextView tvDateTime = convertView.findViewById(R.id.tvDateTime);

        tvTitle.setText(appointment.getTitle());
        tvDescription.setText(appointment.getDescription());
        String dateTime = appointment.getDate() + " " + appointment.getTime();
        tvDateTime.setText(dateTime);

        return convertView;
    }
}
