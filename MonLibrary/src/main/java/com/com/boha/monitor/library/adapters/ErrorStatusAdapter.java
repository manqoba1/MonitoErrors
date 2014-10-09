package com.com.boha.monitor.library.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.boha.monitor.library.R;
import com.com.boha.monitor.library.dto.ErrorStoreDTO;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.Util;
import com.google.android.gms.internal.co;

import java.util.Date;
import java.util.List;

/**
 * Created by CodeTribe1 on 2014-10-02.
 */
public class ErrorStatusAdapter extends ArrayAdapter {
    private Context ctx;
    private int mResource;
    private List<ErrorStoreDTO> mList;
    private LayoutInflater inflater;

    public ErrorStatusAdapter(Context context, int resource, List<ErrorStoreDTO> list) {
        super(context, resource, list);
        mResource = resource;
        mList = list;
        ctx = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    static class Holder {
        TextView ES_count, ES_origin, ES_date, ES_message;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = inflater.inflate(mResource, null);
            h = new Holder();
            h.ES_count = (TextView) convertView.findViewById(R.id.ES_count);
            h.ES_date = (TextView) convertView.findViewById(R.id.ES_date);
            h.ES_origin = (TextView) convertView.findViewById(R.id.ES_origin);
            h.ES_message = (TextView) convertView.findViewById(R.id.ES_message);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }
        ErrorStoreDTO dto = mList.get(position);
        h.ES_message.setText(dto.getMessage());
        h.ES_count.setText("" + (position + 1));
        h.ES_origin.setText(dto.getOrigin());
        h.ES_date.setText(Util.getLongDateTime(new Date(dto.getDateOccured())));
        Statics.setRobotoItalic(ctx, h.ES_message);
        Statics.setRobotoFontBold(ctx,h.ES_origin);
        Util.animateScaleY(h.ES_count,500);
        animateView(convertView);
        return (convertView);
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.push_down_in_no_alpha);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }
}
