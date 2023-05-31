package com.pscalendarevent.pscalendarevent.views;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.fragment.app.FragmentActivity;
import androidx.viewpager.widget.ViewPager;

import com.pscalendarevent.pscalendarevent.CellConfig;
import com.pscalendarevent.pscalendarevent.MarkStyle;
import com.pscalendarevent.pscalendarevent.adapters.CalendarViewExpAdapter;
import com.pscalendarevent.pscalendarevent.listeners.OnDateClickListener;
import com.pscalendarevent.pscalendarevent.listeners.OnMonthChangeListener;
import com.pscalendarevent.pscalendarevent.listeners.OnMonthScrollListener;
import com.pscalendarevent.pscalendarevent.utils.CurrentCalendar;
import com.pscalendarevent.pscalendarevent.vo.DateData;
import com.pscalendarevent.pscalendarevent.vo.MarkedDates;

import java.util.Calendar;

public class ExpCalendarView extends ViewPager {
    private final int dateCellViewResId = -1;
    private final View dateCellView = null;
    private final int markedStyle = -1;
    private final int markedCellResId = -1;
    private final View markedCellView = null;
    private boolean hasTitle = true;

    private boolean initted = false;

    private DateData currentDate;
    private CalendarViewExpAdapter adapter;

    private int width, height;
    private int currentIndex;

    public ExpCalendarView(Context context) {
        super(context);
        if (context instanceof FragmentActivity) {
            init((FragmentActivity) context);
        }
    }

    public ExpCalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (context instanceof FragmentActivity) {
            init((FragmentActivity) context);
        }
    }

    public void init(FragmentActivity activity) {
        if (initted) {
            return;
        }
        initted = true;
        if (currentDate == null) {
            currentDate = CurrentCalendar.getCurrentDateData();
        }
        // TODO: 15/8/28 Will this cause trouble when achieved?
        if (this.getId() == View.NO_ID) {
            // this.setId(R.id.calendarViewPager);
        }
        adapter = new CalendarViewExpAdapter(activity.getSupportFragmentManager()).setDate(currentDate);
        this.setAdapter(adapter);
        this.setCurrentItem(500);
//        addBackground();
        float density = getContext().getResources().getSystem().getDisplayMetrics().density;
        CellConfig.cellHeight = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 7 / density;
        CellConfig.cellWidth = getContext().getResources().getSystem().getDisplayMetrics().widthPixels / 7 / density;

//        this.addOnPageChangeListener(new );
    }

    //// TODO: 15/8/28 May cause trouble when invoked after inited
    public ExpCalendarView travelTo(DateData dateData) {
        // 获得当前页面的年月（position=500）
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH);
        int realPosition = 500 + (dateData.getYear() - thisYear) * 12 + (dateData.getMonth() - thisMonth - 1);
        if (realPosition > 1000 || realPosition < 0)
            throw new RuntimeException("Please travelto a right date: today-500~today~today+500");

        // 来个步进滑动？因为一次滑个几百页，界面有时候不刷新（蛋疼）
        for (int i = getCurrentItem(); i < realPosition; i = i + 50) {
            setCurrentItem(i);
            Log.i("step", " " + i);
        }
        for (int i = getCurrentItem(); i > realPosition; i = i - 50) {
            setCurrentItem(i);
            Log.i("step", " " + i);
        }
        setCurrentItem(realPosition);
        // 标记
        // MarkedDates.getInstance().add(dateData);
        return this;
    }

    public void nextMonth(int selectedYear, int selectedMonth) {

        if (selectedMonth == 12) {
            selectedMonth = 1;
            selectedYear++;
        } else {
            selectedMonth++;
        }
        travelTo(new DateData(selectedYear, selectedMonth, 1));
    }

    public void prevMonth(int selectedYear, int selectedMonth) {
        if (selectedMonth == 1) {
            selectedMonth = 12;
            selectedYear--;
        } else {
            selectedMonth--;
        }
        travelTo(new DateData(selectedYear, selectedMonth, 1));
    }

    public void expand() {
        adapter.notifyDataSetChanged();
    }

    public void shrink() {
        adapter.notifyDataSetChanged();
    }


    public ExpCalendarView markDate(int year, int month, int day) {
        MarkedDates.getInstance().add(new DateData(year, month, day));
        return this;
    }

    public ExpCalendarView unMarkDate(int year, int month, int day) {
        MarkedDates.getInstance().remove(new DateData(year, month, day));
        return this;
    }

    public ExpCalendarView markDate(DateData date) {
        MarkedDates.getInstance().add(date);
        return this;
    }

    public ExpCalendarView unMarkDate(DateData date) {
        MarkedDates.getInstance().remove(date);
        return this;
    }

    public MarkedDates getMarkedDates() {
        return MarkedDates.getInstance();
    }

    public ExpCalendarView setDateCell(int resId) {
        adapter.setDateCellId(resId);
        return this;
    }

    public ExpCalendarView setMarkedStyle(int style, int color) {
        MarkStyle.current = style;
        MarkStyle.defaultColor = color;
        return this;
    }

    public ExpCalendarView setMarkedStyle(int style) {
        MarkStyle.current = style;
        return this;
    }

    public ExpCalendarView setMarkedCell(int resId) {
        adapter.setMarkCellId(resId);
        return this;
    }

    public ExpCalendarView setOnMonthChangeListener(OnMonthChangeListener listener) {
        this.addOnPageChangeListener(listener);
        return this;
    }

    public ExpCalendarView setOnMonthScrollListener(OnMonthScrollListener listener) {
        this.addOnPageChangeListener(listener);
        return this;
    }

    public ExpCalendarView setOnDateClickListener(OnDateClickListener onDateClickListener) {
        OnDateClickListener.instance = onDateClickListener;
        return this;
    }

    public ExpCalendarView hasTitle(boolean hasTitle) {
        this.hasTitle = true;
        adapter.setTitle(hasTitle);
        return this;
    }

    @Override
    protected void onMeasure(int measureWidthSpec, int measureHeightSpec) {
        width = measureWidth(measureWidthSpec);
        height = measureHeight(measureHeightSpec);
        measureHeightSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);
        super.onMeasure(measureWidthSpec, measureHeightSpec);
    }

    private int measureWidth(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        int result = 0;
        if (specMode == MeasureSpec.AT_MOST) {
            float destiney = getContext().getResources().getSystem().getDisplayMetrics().density;
            result = (int) (CellConfig.cellWidth * 7 * destiney);
        } else if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) CellConfig.cellHeight;
        }
        return result;
    }

    private int measureHeight(int measureSpec) {
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);
        float density = getContext().getResources().getSystem().getDisplayMetrics().density;
        if (specMode == MeasureSpec.AT_MOST) {
            if (CellConfig.ifMonth)
                return (int) (CellConfig.cellHeight * 7 * density);
            else
                return (int) (CellConfig.cellHeight * density);
        } else if (specMode == MeasureSpec.EXACTLY) {
            return specSize;
        } else {
            return (int) CellConfig.cellHeight;
        }
    }

    public void measureCurrentView(int currentIndex) {
        this.currentIndex = currentIndex;
        requestLayout();
    }
}

