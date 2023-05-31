package com.pscalendarevent.pscalendarevent.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

import com.pscalendarevent.pscalendarevent.adapters.CalendarAdapter;
import com.pscalendarevent.pscalendarevent.vo.MonthData;

public class MonthView extends GridView {
    private MonthData monthData;
    private CalendarAdapter adapter;

    public MonthView(Context context) {
        super(context);
        this.setNumColumns(7);
    }

    public MonthView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setNumColumns(7);
    }

    public MonthView displayMonth(MonthData monthData){
        adapter = new CalendarAdapter(getContext(), 1, monthData.getData());
        return this;
    }

}
