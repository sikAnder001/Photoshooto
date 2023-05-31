package com.photoshooto.ui.job.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;

import com.photoshooto.R;
import com.photoshooto.domain.model.SpinnerModel;
import com.photoshooto.ui.job.interfaces.JobInterface;

import java.util.ArrayList;

public class SpinnerCustomAdapter extends BaseAdapter {
    private final JobInterface jobInterface;
    ArrayList<SpinnerModel> listData;
    Context context;

    public SpinnerCustomAdapter(Context context, ArrayList<SpinnerModel> listData,
                                JobInterface jobInterface) {
        this.context = context;
        this.listData = listData;
        this.jobInterface = jobInterface;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(context).inflate(R.layout.item_spinner, null);

        RadioButton radioButton = convertView.findViewById(R.id.radio);
        radioButton.setText(listData.get(position).getBaseName());
        radioButton.setChecked(listData.get(position).isSelected());

        radioButton.setOnCheckedChangeListener((compoundButton, select) -> {
            if (compoundButton.isPressed() && select) {
                jobInterface.callback(position);
            }
        });
        return convertView;
    }
}