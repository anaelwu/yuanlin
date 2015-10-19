package com.ninetowns.tootooplus.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ninetowns.tootooplus.R;
import com.ninetowns.tootooplus.bean.GoodsAddressBean;

public class AddAddressSpinnerAdapter extends BaseAdapter {
	
	private Context context;
	
	private List<GoodsAddressBean> addressBeans;
	
	public AddAddressSpinnerAdapter(Context context, List<GoodsAddressBean> addressBeans){
		this.context = context;
		this.addressBeans = addressBeans;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		if(addressBeans != null && addressBeans.size() > 0){
			return addressBeans.size();
		} else {
			return 0;
		}
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return addressBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder vh = null;
		if(convertView == null){
			vh = new ViewHolder();
			convertView = LayoutInflater.from(context).inflate(R.layout.address_spinner_item, null);
			vh.add_spinner_item_cont = (TextView)convertView.findViewById(R.id.add_spinner_item_cont);
			convertView.setTag(vh);
		} else {
			vh = (ViewHolder)convertView.getTag();
		}
		
		if(addressBeans.get(position).getGoods_address_name() != null){
			vh.add_spinner_item_cont.setText(addressBeans.get(position).getGoods_address_name());
		} else {
			vh.add_spinner_item_cont.setText("");
		}
		
		
		return convertView;
	}
	
	static class ViewHolder{
		TextView add_spinner_item_cont;
	}

}
