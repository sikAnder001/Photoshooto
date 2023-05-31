package com.pscalendarevent.pscalendarevent.views;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.TextView;

import com.pscalendarevent.pscalendarevent.CellConfig;
import com.pscalendarevent.pscalendarevent.MarkStyleExp;
import com.pscalendarevent.pscalendarevent.vo.DayData;

public class DefaultCellView extends BaseCellView {
    public TextView textView;
    private AbsListView.LayoutParams matchParentParams;

    public DefaultCellView(Context context) {
        super(context);
        initLayout();
    }

    public DefaultCellView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initLayout();
    }

    private void initLayout() {
        matchParentParams = new AbsListView.LayoutParams((int) CellConfig.cellWidth, (int) CellConfig.cellHeight);
        this.setLayoutParams(matchParentParams);
        this.setOrientation(VERTICAL);
        textView = new TextView(getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setFocusable(false);
        textView.setClickable(false);
        textView.setLongClickable(false);
        textView.setFocusableInTouchMode(false);
        textView.setEnabled(false);
        //textView.(R.style.tvFontSemiBold);
        textView.setTypeface(Typeface.createFromAsset(getContext().getAssets(), "poppins_500.ttf"));
        textView.setLayoutParams(new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 0, (float) 1.0));
        this.addView(textView);
    }

    @Override
    public void setDisplayText(DayData day) {
        textView.setTextColor(Color.BLACK);
        textView.setText(day.getText());
    }

    @Override
    protected void onMeasure(int measureWidthSpec, int measureHeightSpec) {
        super.onMeasure(measureWidthSpec, measureHeightSpec);
    }

    public boolean setDateChoose() {
        setBackgroundDrawable(MarkStyleExp.choose);
        textView.setTextColor(Color.BLACK);
        return true;
    }

    public void setDateToday() {
        setBackgroundDrawable(MarkStyleExp.today);
        textView.setTextColor(Color.rgb(105, 75, 125));
    }

    public void setDateNormal() {
        textView.setTextColor(Color.BLACK);
        setBackgroundDrawable(null);
    }

    public void setTextColor(String text, int color) {
        textView.setText(text);
        if (color != 0) {
            textView.setTextColor(color);
        }
    }
}
