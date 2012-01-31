package com.alien_roger.court_deadlines.ui.adapters;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.db.DBConstants;

public class TaskListAdapter extends CursorAdapter{

	private LayoutInflater inflater;
	private SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd' 'MMMM");
	private Calendar fromCalendar;
	private Calendar toCalendar;
//	private Cursor cursor;
//	private Context context;
	private int idColIndex = 0;
	private int[] drawables;
	private int[] drawablesBack;

	public TaskListAdapter(Context context, Cursor c, boolean autoRequery) {
		super(context, c, autoRequery);
		init(context,c);
	}

	public TaskListAdapter(Context context, Cursor c) {
		super(context, c);
		init(context,c);
	}

	private void init(Context context,Cursor c){
		inflater = LayoutInflater.from(context);
		fromCalendar = Calendar.getInstance();
		toCalendar = Calendar.getInstance();
//		cursor = c;
		idColIndex = c.getColumnIndex(DBConstants._ID);
		drawables = new int[]{
				R.drawable.priority_0_selector,
				R.drawable.priority_1_selector,
				R.drawable.priority_2_selector/*,
				R.drawable.priority_3_selector,
				R.drawable.priority_4_selector*/};
		drawablesBack = new int[]{
				R.drawable.priority_0,
				R.drawable.priority_1,
				R.drawable.priority_2/*,
				R.drawable.priority_3,
				R.drawable.priority_4*/
		};

	}

    private String getDate(long calendarTime){
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(calendarTime);
        return dateTimeFormat.format(calendar.getTime());
    }

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		ViewHolder	holder = (ViewHolder) view.getTag();
		int priorValue = cursor.getInt(cursor.getColumnIndex(DBConstants.PRIOTIY));
		view.setBackgroundResource(drawables[priorValue]);
//		setBackground(view, cursor);

        holder.customer.setText(cursor.getString(cursor.getColumnIndex(DBConstants.CUSTOMER)));

        holder.courtDate.setText(getDate(cursor.getLong(cursor.getColumnIndex(DBConstants.COURT_DATE))));
        holder.proposalDate.setText(getDate(cursor.getLong(cursor.getColumnIndex(DBConstants.PROPOSAL_DATE))));
        holder.courtType.setText(cursor.getString(cursor.getColumnIndex(DBConstants.COURT_TYPE)));
		holder.priorityView.setBackgroundResource(drawablesBack[priorValue]);
//		view.setOnClickListener(itemClickListener);
		view.setTag(holder);
	}

	private void setBackground(View view, Cursor cursor){
		int priorValue = cursor.getInt(cursor.getColumnIndex(DBConstants.PRIOTIY));
		view.setBackgroundResource(drawables[priorValue]);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View convertView = createView(parent,cursor /*,cursor.getPosition()*/);
		return convertView;
	}

	private View createView(ViewGroup parent,Cursor cursor/*int position*/){
		ViewHolder holder = new ViewHolder();
		View view = inflater.inflate(R.layout.task_list_item, parent, false);
        holder.customer = (TextView) view.findViewById(R.id.customerTxt);
        holder.courtDate = (TextView) view.findViewById(R.id.courtDateTxt);
        holder.proposalDate = (TextView) view.findViewById(R.id.proposalDateTxt);
        holder.courtType = (TextView) view.findViewById(R.id.courtTypeTxt);
		holder.priorityView = view.findViewById(R.id.priorityView);
		view.setTag(holder);
		return view;
	}

	@Override
	public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (getFilterQueryProvider() != null) {
        	return getFilterQueryProvider().runQuery(constraint);
    	}
        return getCursor();
    }

//	private View.OnClickListener itemClickListener = new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
////			long id = (Long) v.getTag(R.id.checkbox_item_id);
////			Toast.makeText(context, "id = " + id, Toast.LENGTH_SHORT).show();
////			Intent intent = new Intent(context, TaskDetailsScreen.class);
////			intent.putExtra("id",id);
////			context.startActivity(intent);
//		}
//	};

	private OnItemClickListener listItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> adapter, View parent, int position, long id) {
//			Intent intent = new Intent(context, TaskDetailsScreen.class);
//			Cursor cursor =  (Cursor) adapter.getItemAtPosition(position);
//
//			intent.putExtra("id",cursor.getLong(cursor.getColumnIndex(DBConstants._ID)));
//			context.startActivity(intent);
		}
	};

	private static class ViewHolder{
		public View priorityView;
		public TextView customer;
//        public TextView caseName;
        public TextView courtDate;
		public TextView proposalDate;
		public TextView courtType;
	}

}
