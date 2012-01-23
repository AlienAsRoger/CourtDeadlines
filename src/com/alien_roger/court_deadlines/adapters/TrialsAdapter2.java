package com.alien_roger.court_deadlines.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.court_deadlines.entities.CourtObj;

/**
 * TrialsAdapter class
 *
 * @author alien_roger
 * @created at: 29.12.11 6:06
 */
public class TrialsAdapter2 extends BaseAdapter {

    private Context context;
    private List<CourtObj> listObjs;
    private LayoutInflater inflater;

    public TrialsAdapter2(Context context, List<CourtObj> listObjs){
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
        ViewHolder holder;
        if(view == null){
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.court_type_list_item, viewGroup, false);
            holder.groupName = (TextView) view.findViewById(R.id.groupNameTxt);
//            holder.name = (TextView) view.findViewById(R.id.nameTxt);
            view.setTag(holder);
        }else{
            holder = (ViewHolder) view.getTag();
        }

//        if(listObjs.get(i).getLevel() == 10 || listObjs.get(i).getLevel() == 0 ){
        holder.groupName.setText(listObjs.get(i).getValue());
        holder.groupName.setVisibility(View.VISIBLE);
//        holder.name.setVisibility(View.INVISIBLE);
//        holder.name.setText("");
//        holder.name.setTextColor(context.getResources().getColor(android.R.color.black));
//        /*}else*/ if(listObjs.get(i).getLevel() == 20){
//            holder.groupName.setVisibility(View.INVISIBLE);
//            holder.groupName.setText("");
//            holder.name.setVisibility(View.VISIBLE);
//            holder.name.setText(listObjs.get(i).getBody());
//            holder.name.setTextColor(context.getResources().getColor(R.color.list_selected_1));
//        }

        return view;
    }

    private static class ViewHolder{
        public TextView groupName;
//        public TextView name;
    }
}
