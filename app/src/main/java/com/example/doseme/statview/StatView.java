package com.example.doseme.statview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.example.doseme.R;

import java.util.ArrayList;
import java.util.Locale;

public class StatView extends View {

    public float getMaxval() {
        return maxval;
    }

    public boolean isShow_alt_label() {
        return show_alt_label;
    }

    public void setShow_alt_label(boolean show_alt_label) {
        this.show_alt_label = show_alt_label;
    }

    public boolean isShow_label() {
        return show_label;
    }

    public void setShow_label(boolean show_label) {
        this.show_label = show_label;
        if(show_label) show_min_label = false;
    }

    public boolean isShow_min_label() {
        return show_min_label;
    }

    public void setShow_min_label(boolean show_min_label) {
        this.show_min_label = show_min_label;
        if(show_min_label) show_label = false;
    }

    private boolean show_alt_label = false;
    private boolean show_label = false;



    private boolean show_min_label = false;

    Paint p = new Paint();
    Paint txt = new Paint();
    protected float maxval = 1;
    protected int nr_dset = 1;

    protected StatViewable data;
    protected boolean has_data = false;



    public StatView(Context context, AttributeSet attr) {
        super(context, attr);
    }

    public void attachViewable(StatViewable sv) {
        data = sv;
        nr_dset = data.get_nr_dsets();
        has_data = true;

        invalidate();
    }

    public void attachViewable(StatViewable sv, boolean show_lbl) {
        show_label = show_lbl;
        attachViewable(sv);
    }


    @Override
    public void onDraw(@NonNull Canvas canvas) {
        super.onDraw(canvas);
        if(has_data) {
            int h = getHeight()-128;
            int w_offset = 128;
            int w = getWidth()-w_offset;

            p.setColor(ContextCompat.getColor(StatView.this.getContext(), R.color.STVW_bg));
            canvas.drawRect(w_offset,0,w+w_offset,h,p);

            p.setColor(ContextCompat.getColor(StatView.this.getContext(), R.color.STVW_line));
            txt.setColor(ContextCompat.getColor(StatView.this.getContext(), R.color.STVW_txt));
            txt.setTextSize(28);

            canvas.drawLine(w_offset, 0, w_offset, h, p);
            for (int i = 0; i < nr_dset; i++) {
                canvas.drawLine(w_offset+((i + 1) * (w / (float) nr_dset)), 0, w_offset+((i + 1) * (w / (float) nr_dset)), h, p);
            }

            canvas.drawText(String.format(Locale.getDefault(),"%.2f", data.get_maxval()), 16, 20, txt);
            canvas.drawLine(w_offset, 0, w+w_offset, 0, p);
            canvas.drawText(String.format(Locale.getDefault(),"%.2f", data.get_maxval()/2), 16, (float)(h/2.0), txt);
            canvas.drawLine(w_offset, (float)h/2, w+w_offset, (float)h/2, p);
            canvas.drawText("0", 16, h-20, txt);
            canvas.drawLine(w_offset, h, w+w_offset, h, p);

            p.setColor(ContextCompat.getColor(StatView.this.getContext(), R.color.STVW_bar));
            double max = data.get_maxval();
            for (int i = 0; i < nr_dset; i++) {
                String label = data.get_label(i);
                double val = data.get_val(i);

                canvas.drawRect(w_offset+((i) * (w / (float) nr_dset)+4), (float) ((max-val) / max)*h, w_offset+((i + 1) * (w / (float) nr_dset)-4), h, p);
                if(show_label) canvas.drawText(label, w_offset+((i) * (w / (float) nr_dset)+8), h+32, txt);
                else if(show_min_label) canvas.drawText(String.valueOf(data.get_min_label(i)), w_offset+((i) * (w / (float) nr_dset)+8), h+32, txt);
                if(show_alt_label) canvas.drawText(data.get_alt_label(i), w_offset+((i) * (w / (float) nr_dset)+8), h+64, txt);
            }
        }
    }
}
