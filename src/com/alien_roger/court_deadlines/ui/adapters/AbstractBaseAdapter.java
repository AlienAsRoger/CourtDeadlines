package com.alien_roger.court_deadlines.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import java.util.List;

/**
 * TrialsAdapter class
 *
 * @author alien_roger
 * @created at: 29.12.11 6:06
 */
public abstract class AbstractBaseAdapter<T> extends BaseAdapter {

    protected Context context;
	protected List<T> listObjs;
	protected LayoutInflater inflater;

    public AbstractBaseAdapter(Context context, List<T> listObjs){
        this.context = context;
        this.listObjs = listObjs;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return listObjs.size();
    }

    @Override
    public Object getItem(int i) {
        return listObjs.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
		if (view == null) view = createView(viewGroup);
		bindView(i, view);
        return view;
    }

	protected abstract View createView(ViewGroup viewGroup);
	protected abstract void bindView(int i, View view);

//	@Override
//	public View getDropDownView(int position, View convertView, ViewGroup parent) {
////		super.getDropDownView(position, convertView, parent);
//		return createView(parent);
//	}

}
