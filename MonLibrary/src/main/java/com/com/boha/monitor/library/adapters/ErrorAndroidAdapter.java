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
import com.com.boha.monitor.library.dto.ErrorStoreAndroidDTO;
import com.com.boha.monitor.library.util.Statics;
import com.com.boha.monitor.library.util.Util;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by CodeTribe1 on 2014-09-30.
 */
public class ErrorAndroidAdapter extends ArrayAdapter<ErrorStoreAndroidDTO> {
    private final LayoutInflater mInflater;
    private final int mLayoutRes;
    private List<ErrorStoreAndroidDTO> mList;
    private Context ctx;

    public ErrorAndroidAdapter(Context context, int textViewResourceId,
                               List<ErrorStoreAndroidDTO> list) {
        super(context, textViewResourceId, list);
        this.mLayoutRes = textViewResourceId;
        mList = list;
        ctx = context;
        this.mInflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    View v;

    static class Holder {
        TextView PP_count, PP_package, PP_date;
        TextView PP_brand, PP_model, PP_version, PP_stack, PP_logCat;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder h;
        if (convertView == null) {
            convertView = mInflater.inflate(mLayoutRes, null);
            h = new Holder();
            h.PP_brand = (TextView) convertView.findViewById(R.id.PP_brand);
            h.PP_count = (TextView) convertView.findViewById(R.id.PP_count);
            h.PP_date = (TextView) convertView.findViewById(R.id.PP_date);
            h.PP_logCat = (TextView) convertView.findViewById(R.id.PP_logCat);
            h.PP_model = (TextView) convertView.findViewById(R.id.PP_model);
            h.PP_package = (TextView) convertView.findViewById(R.id.PP_package);
            h.PP_stack = (TextView) convertView.findViewById(R.id.PP_stack);
            h.PP_version = (TextView) convertView.findViewById(R.id.PP_version);
            convertView.setTag(h);
        } else {
            h = (Holder) convertView.getTag();
        }
        ErrorStoreAndroidDTO dto = mList.get(position);
        h.PP_version.setText("Version: " + dto.getAndroidVersion());
        h.PP_stack.setText(dto.getStackTrace());
        h.PP_package.setText(dto.getPackageName());
        h.PP_brand.setText(dto.getBrand());
        h.PP_date.setText(Util.getLongDateTime(new Date(dto.getErrorDate())));
        h.PP_logCat.setText(dto.getLogCat());
        h.PP_model.setText(dto.getPhoneModel());
        h.PP_count.setText("" + (position + 1));

        Statics.setRobotoItalic(ctx, h.PP_logCat);
        Statics.setRobotoItalic(ctx, h.PP_stack);
        Statics.setRobotoFontBold(ctx,h.PP_package);


      //  animateView(convertView);
       // Util.animateScaleX(h.PP_count, 100);
        return convertView;
    }

    public void animateView(final View view) {
        Animation a = AnimationUtils.loadAnimation(ctx, R.anim.grow_fade_in_center);
        a.setDuration(500);
        if (view == null)
            return;
        view.startAnimation(a);
    }

}
