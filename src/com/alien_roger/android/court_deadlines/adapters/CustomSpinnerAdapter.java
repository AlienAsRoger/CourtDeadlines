package com.alien_roger.android.court_deadlines.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.db.DBConstants;

public class CustomSpinnerAdapter extends CursorAdapter {

	private Context myContext;

//	public CustomSpinnerAdapter(Context context, int layout, Cursor c, String[] from, int[] to) {
////	    super(context, layout, c, from, to);
//	    myContext = context;
//	}

	public CustomSpinnerAdapter(Context context, Cursor cursor){
		super(context, cursor);
		myContext = context;

	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
//	    super.bindView(view, context, cursor);

	    int nameColumn = cursor.getColumnIndex(DBConstants.TRIAL_VALUE);
	    String getName = cursor.getString(nameColumn);
	    TextView name = (TextView)view.findViewById(R.id.nameTxt);
	    name.setText(getName);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
//	    super.newView(context, cursor, parent);
	    View view = View.inflate(context, R.layout.default_list_item, null);
	    return view;
	}

	@Override
	public View newDropDownView(Context context, Cursor cursor, ViewGroup parent) {
	    super.newDropDownView(context, cursor, parent);

	    View view = View.inflate(context, R.layout.drop_down_list_item, null);
	    bindView(view, context, cursor);
//	    int nameColumn = cursor.getColumnIndex("name");
//	    String getName = cursor.getString(nameColumn);
//	    TextView name = (TextView)view.findViewById(R.id.nameTxt);
//	    name.setText(getName);

//	    int loviColumn = cursor.getColumnIndex("lovibond");
//	    String getLovi = cursor.getString(loviColumn);
//	    TextView lovi = (TextView)view.findViewById(R.id.GrainSpinnerLovibond);
//	    lovi.setText(getLovi);
//
//	    int gravityColumn = cursor.getColumnIndex("gravity");
//	    String getGravity = cursor.getString(gravityColumn);
//	    TextView gravity = (TextView)view.findViewById(R.id.GrainSpinnerGravity);
//	    gravity.setText(getGravity);

	    return view;
	}
}