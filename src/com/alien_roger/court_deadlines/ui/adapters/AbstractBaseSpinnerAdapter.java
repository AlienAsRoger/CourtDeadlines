package com.alien_roger.court_deadlines.ui.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * TrialsAdapter class
 *
 * @author alien_roger
 * @created at: 29.12.11 6:06
 */
public abstract class AbstractBaseSpinnerAdapter<T> extends AbstractBaseAdapter<T> {


	public AbstractBaseSpinnerAdapter(Context context, List<T> listObjs) {
		super(context, listObjs);
	}

	protected abstract View createView(ViewGroup viewGroup);
	protected abstract View createDropDownView(ViewGroup viewGroup);
	protected abstract void bindView(int i, View view);

	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		return createDropDownView(parent);
	}

}
