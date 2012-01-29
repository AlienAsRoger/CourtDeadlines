package com.alien_roger.court_deadlines.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ToggleButton;
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
		/*implements View.OnClickListener */{


    public PrioritiesAdapter(Context context, List<PriorityObject> listObjs){
		super(context, listObjs);
		this.context = context;
        this.listObjs = listObjs;
        inflater = LayoutInflater.from(context);
    }

	protected View createView( ViewGroup viewGroup){
		ViewHolder holder = new ViewHolder();
		View view = inflater.inflate(R.layout.spinner_item_priority, viewGroup, false);
		holder.priorityView = view.findViewById(R.id.priorityView);
		holder.name = (TextView) view.findViewById(R.id.nameTxt);
		holder.radioButton = (ToggleButton) view.findViewById(R.id.radioBtn);
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

	protected void bindView(int i, View view){
		ViewHolder holder = (ViewHolder) view.getTag();
		listObjs.get(i).getValue();
		holder.name.setText(listObjs.get(i).getText());
		holder.priorityView.setBackgroundResource(R.drawable.priority_4);
		if(holder.radioButton != null)
			holder.radioButton.setChecked(listObjs.get(i).isChecked());
	}

//	@Override
//	public void onClick(View view) {
//
//	}

	private static class ViewHolder{
        public ToggleButton radioButton;
        public TextView name;
		public View priorityView;
    }
}
