package com.alien_roger.android.court_deadlines.adapters;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.TextView;

import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.statics.StaticData;

public class CaseSpinnerAdapter extends TrialsSpinnerAdapter {


	public CaseSpinnerAdapter(Context context, Cursor cursor){
		super(context, cursor);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {

		String string = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));

		if(string.contains(StaticData.CHILD_DELIMITER)){
			/*Service - AT60*/
			String name = string.substring(0,string.indexOf(StaticData.CHILD_DELIMITER));
			TextView nameTxt = (TextView)view.findViewById(R.id.nameTxt);
			nameTxt.setText(name);
		}else{
			/*Service -*/
			super.bindView(view,context,cursor);
		}

	}




}
