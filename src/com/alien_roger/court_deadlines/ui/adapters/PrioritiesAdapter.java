package com.alien_roger.court_deadlines.ui.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.entities.PriorityObject;

import java.util.List;

/**
 * TrialsAdapter class
 *
 * @author alien_roger
 * @created at: 29.12.11 6:06
 */
public class PrioritiesAdapter extends AbstractBaseSpinnerAdapter<PriorityObject>
		implements View.OnClickListener {

	private int[] drawables;
	private int[] drawablesBack;

    public PrioritiesAdapter(Context context, List<PriorityObject> listObjs){
		super(context, listObjs);
		this.context = context;
        this.listObjs = listObjs;
        inflater = LayoutInflater.from(context);
		drawables = new int[]{R.drawable.priority_0_selector,
				R.drawable.priority_1_selector,
				R.drawable.priority_2_selector,
				R.drawable.priority_3_selector,
				R.drawable.priority_4_selector};
		drawablesBack = new int[]{
				R.drawable.priority_0,
				R.drawable.priority_1,
				R.drawable.priority_2,
				R.drawable.priority_3,
				R.drawable.priority_4
		};
    }

	protected View createView( ViewGroup viewGroup){
		ViewHolder holder = new ViewHolder();
		View view = inflater.inflate(R.layout.spinner_item_priority, viewGroup, false);
		holder.priorityView = view.findViewById(R.id.priorityView);
		holder.name = (TextView) view.findViewById(R.id.nameTxt);
//		holder.radioButton = (ToggleButton) view.findViewById(R.id.radioBtn);
		view.setTag(holder);
		return view;
	}

	@Override
	protected View createDropDownView(ViewGroup viewGroup) {
		ViewHolder holder = new ViewHolder();
		View view = inflater.inflate(R.layout.drop_down_list_item_priotiy, viewGroup, false);
		holder.priorityView = view.findViewById(R.id.priorityView);
		holder.name = (TextView) view.findViewById(R.id.nameTxt);
//		view.setOnClickListener(this);
		view.setTag(holder);
		return view;
	}

	protected void bindView(int position, View view){
		ViewHolder holder = (ViewHolder) view.getTag();
		listObjs.get(position).getValue();
		holder.name.setText(listObjs.get(position).getText());
		Log.d("TEST", "Priorities bind = " +listObjs.get(position).getText());
		view.setBackgroundResource(drawables[position]);
		holder.priorityView.setBackgroundResource(drawablesBack[position]);
//		holder.priorityView.setBackgroundResource(R.drawable.priority_4);
//		if(holder.radioButton != null){
//			holder.radioButton.setChecked(listObjs.get(i).isChecked());
//			if(listObjs.get(i).isChecked())
//				holder.radioButton.setVisibility(View.VISIBLE);
//			else
//				holder.radioButton.setVisibility(View.INVISIBLE);
//		}
			
	}

	@Override
	public void onClick(View view) {
		  int i = 22;
	}

	private static class ViewHolder{
//        public ToggleButton radioButton;
        public TextView name;
		public View priorityView;
    }
}
