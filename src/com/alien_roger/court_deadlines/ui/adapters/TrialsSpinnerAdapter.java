package com.alien_roger.court_deadlines.ui.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.db.DBConstants;

public class TrialsSpinnerAdapter extends CursorAdapter {

	protected Context myContext;
	private LayoutInflater layoutInflater;

	public TrialsSpinnerAdapter(Context context, Cursor cursor){
		super(context, cursor);
		myContext = context;
		layoutInflater = LayoutInflater.from(context);

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
	    int nameColumn = cursor.getColumnIndex(DBConstants.TRIAL_VALUE);
	    String getName = cursor.getString(nameColumn);
	    TextView name = (TextView)view.findViewById(R.id.nameTxt);
		name.setText(getName);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
	    View view = layoutInflater.inflate(R.layout.spinner_item, parent, false);
	    return view;
	}

	@Override
	public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
	    super.newDropDownView(context, cursor, parent);

	    View view = layoutInflater.inflate(R.layout.drop_down_list_item, parent, false);
	    bindView(view, context, cursor);
	    return view;
	}
}
