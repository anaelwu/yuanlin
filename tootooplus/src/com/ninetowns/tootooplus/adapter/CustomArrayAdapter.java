package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetowns.tootooplus.R;
/**
 * 
* @ClassName: CustomArrayAdapter 
* @Description: 热门搜索
* @author wuyulong
* @date 2015-4-25 下午4:32:35 
*
 */
public class CustomArrayAdapter extends BaseAdapter {
	private List<String> list;
	private Context context;
	public CustomArrayAdapter(Context context,List<String> list) {
		this.list=list;
		this.context=context;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Holder holder;

        if (convertView == null) {
            convertView =LayoutInflater.from(context).inflate(R.layout.custom_data_view, parent, false);
            holder = new Holder();
            holder.textView = (TextView) convertView.findViewById(R.id.textView);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        // Populate the text
        holder.textView.setText(list.get(position));

        // Set the color

        return convertView;
    }

    /** View holder for the views we need access to */
    private static class Holder {
        public TextView textView;
    }
	
	
}
