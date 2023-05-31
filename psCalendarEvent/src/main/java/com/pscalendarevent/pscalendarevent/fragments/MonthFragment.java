package com.pscalendarevent.pscalendarevent.fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.pscalendarevent.pscalendarevent.adapters.CalendarAdapter;
import com.pscalendarevent.pscalendarevent.views.MonthView;
import com.pscalendarevent.pscalendarevent.vo.MonthData;

public class MonthFragment extends Fragment {
    private MonthData monthData;
    private int cellView = -1;
    private int markView = -1;
    private boolean hasTitle = true;

    public void setData(MonthData monthData, int cellView, int markView) {
        this.monthData = monthData;
        this.cellView = cellView;
        this.markView = markView;
    }

    public void setTitle(boolean hasTitle) {
        this.hasTitle = hasTitle;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        LinearLayout ret = new LinearLayout(getContext());
        ret.setOrientation(LinearLayout.VERTICAL);
        ret.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        ret.setGravity(Gravity.TOP);

        if ((monthData != null) && (monthData.getDate() != null)) {
            if (hasTitle) {
                TextView textView = new TextView(getContext());
               // textView.(R.style.tvFontBold);
                textView.setTextColor(Color.BLACK);
                textView.setText("< " + monthData.getDate().getMonthName() + " " + monthData.getDate().getYear() + " >");
                ret.addView(textView);
            }

            Log.e("@@@", " #" + (monthData.getData().get(7)));
            MonthView monthView = new MonthView(getContext());
            monthView.setAdapter(new CalendarAdapter(getContext(),
                    1, monthData.getData()).setCellViews(cellView, markView));
            ret.addView(monthView);
        }
        return ret;
    }
}
