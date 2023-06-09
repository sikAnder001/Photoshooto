package com.pscalendarevent.pscalendarevent.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.pscalendarevent.pscalendarevent.MarkStyle;
import com.pscalendarevent.pscalendarevent.listeners.OnDateClickListener;
import com.pscalendarevent.pscalendarevent.utils.CurrentCalendar;
import com.pscalendarevent.pscalendarevent.views.BaseCellView;
import com.pscalendarevent.pscalendarevent.views.BaseMarkView;
import com.pscalendarevent.pscalendarevent.views.DefaultCellView;
import com.pscalendarevent.pscalendarevent.views.DefaultMarkView;
import com.pscalendarevent.pscalendarevent.vo.DayData;
import com.pscalendarevent.pscalendarevent.vo.MarkedDates;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

public class CalendarExpAdapter extends ArrayAdapter implements Observer {
    private ArrayList data;
    private int cellView = -1;
    private int markView = -1;

    public CalendarExpAdapter(Context context, int resource, ArrayList data) {
        super(context, resource);
        this.data = data;
        MarkedDates.getInstance().addObserver(this);
    }

    public CalendarExpAdapter setCellViews(int cellView, int markView) {
        this.cellView = cellView;
        this.markView = markView;
        return this;
    }


    public View getView(int position, View convertView, ViewGroup viewGroup) {
        View ret = null;
        DayData dayData = (DayData) data.get(position);
        MarkStyle style = MarkedDates.getInstance().check(dayData.getDate());
        boolean marked = style != null;
        if (marked) {
            dayData.getDate().setMarkStyle(style);
            if (markView > 0) {
                BaseMarkView baseMarkView = (BaseMarkView) View.inflate(getContext(), markView, null);
                baseMarkView.setDisplayText(dayData);
                ret = baseMarkView;
            } else {
                ret = new DefaultMarkView(getContext());
                ((DefaultMarkView) ret).setDisplayText(dayData);
            }
        } else {
            if (cellView > 0) {
                BaseCellView baseCellView = (BaseCellView) View.inflate(getContext(), cellView, null);
                baseCellView.setDisplayText(dayData);
                ret = baseCellView;
            } else {
                ret = new DefaultCellView(getContext());
                ((DefaultCellView) ret).setTextColor(dayData.getText(), dayData.getTextColor());
            }
        }
        ((BaseCellView) ret).setDate(dayData.getDate());
        if (OnDateClickListener.instance != null) {
            ((BaseCellView) ret).setOnDateClickListener(OnDateClickListener.instance);
        }
        if (dayData.getDate().equals(CurrentCalendar.getCurrentDateData()) &&
                ret instanceof DefaultCellView) {
            ((DefaultCellView) ret).setDateToday();

        }
        return ret;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public void update(Observable observable, Object data) {
        this.notifyDataSetChanged();
    }
}