package iband.com.ibandplayerdemo.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import java.util.ArrayList;
import iband.com.ibandplayerdemo.R;

/**
 * Created by yossibarel on 18/07/2017.
 */

public class VariantAdapter extends ArrayAdapter<VariantHolder> {

    private int mSelected;
    private int mActive;
    public VariantAdapter(@NonNull Context context, ArrayList<VariantHolder> variants) {
        super(context, R.layout.item_variant, variants);
    }

    private static class ViewHolder {
        TextView tvVariant;
        View vCurrentVariant;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        VariantHolder variant = getItem(position);
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_variant, parent, false);
            viewHolder.tvVariant = (TextView) convertView.findViewById(R.id.tv_variant);
            viewHolder.vCurrentVariant = convertView.findViewById(R.id.v_current_variant);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.tvVariant.setText(variant.getLabel());
        if (mSelected == position)
            viewHolder.tvVariant.setTextColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        else
            viewHolder.tvVariant.setTextColor(Color.WHITE);
        if (mActive == variant.getIndex())
            viewHolder.vCurrentVariant.setVisibility(View.VISIBLE);
        else
            viewHolder.vCurrentVariant.setVisibility(View.GONE);
        return convertView;
    }

    public void setActive(int index) {
        mActive = index;
    }

    public void setSelected(int selected) {
        mSelected = selected;
    }
}
