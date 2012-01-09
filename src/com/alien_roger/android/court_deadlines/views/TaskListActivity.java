package com.alien_roger.android.court_deadlines.views;

import actionbarcompat.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;
import com.alien_roger.android.court_deadlines.AppConstants;
import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.adapters.TaskListAdapter;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.entities.CourtCase;
import com.alien_roger.android.court_deadlines.interfaces.TaskLoadInterface;
import com.alien_roger.android.court_deadlines.tasks.LoadTasks;

import java.util.List;

public class TaskListActivity extends ActionBarActivity implements TaskLoadInterface, AdapterView.OnItemClickListener {
    private ListView listView;
    private Cursor cursor;
    
    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list_screen);


        widgetsInit();
    }

    @Override
    protected void onResume() {
        super.onResume();
        new LoadTasks(this).execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        // Calling super after populating the menu is necessary here to ensure that the
        // action bar helpers have a chance to handle this event.
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menu_refresh:
                startActivity(new Intent(this,TaskDetailsActivity.class));
//                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
//                getActionBarHelper().setRefreshActionItemState(true);
//                getWindow().getDecorView().postDelayed(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                getActionBarHelper().setRefreshActionItemState(false);
//                            }
//                        }, 1000);
                break;

            case R.id.menu_search:
                Toast.makeText(this, "Tapped search", Toast.LENGTH_SHORT).show();
                break;

//            case R.id.menu_share:
//                Toast.makeText(this, "Tapped share", Toast.LENGTH_SHORT).show();
//                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//
//        menu.add(0,UPLOAD_MENU,0," ")
//            .setIcon(R.drawable.ic_action_share)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//
//        menu.add(0,EDIT_MENU,0," ")
//            .setIcon(R.drawable.ic_action_edit)
//            .setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
//
//        return super.onCreateOptionsMenu(menu);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if(item.getItemId() == EDIT_MENU){
//            startActivity(new Intent(this,TaskDetailsActivity.class));
//        }else if(item.getItemId() == UPLOAD_MENU){
//            Intent sendIntent = new Intent(Intent.ACTION_SEND);
//
//            sendIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.share_subj));
//            sendIntent.putExtra(Intent.EXTRA_TEXT,formShareLetter());
//
//            sendIntent.setType("text/plain");
//            startActivity(Intent.createChooser(sendIntent, getString(R.string.share_msg_title)));
//        }
//        return super.onOptionsItemSelected(item);
//    }

    private String formShareLetter(){
        StringBuilder builder = new StringBuilder();
        if (cursor.moveToFirst()) {
            do{
                builder.append(composeShareMsg(cursor)).append(AppConstants.NEW_STR_SYMBOL).append(AppConstants.NEW_STR_SYMBOL);
            }while (cursor.moveToNext());
        }
        return builder.toString();
    }

    private String composeShareMsg(Cursor cursorCase){
        return cursor.getString(cursorCase.getColumnIndex(DBConstants.CASE_NAME));
    }

    private void widgetsInit(){
        listView = (ListView) findViewById(R.id.listView);
        listView.setEmptyView(findViewById(android.R.id.empty));
        listView.setOnItemClickListener(this);
//        stubTxt = (TextView) findViewById(R.id.stubTxt);
//        listView.setOnItemClickListener(new TaskItemClickListener());
    }

//    private class TaskItemClickListener implements AdapterView.OnItemClickListener{
//        @Override
//        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//
//        }
//    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int id){
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showProgress(boolean show) {

    }

    @Override
    public void onDataReady(List<CourtCase> cases) {

    }

    @Override
    public void onTaskLoaded(Cursor cursor) {
        this.cursor = cursor;
//        stubTxt.setVisibility(View.INVISIBLE);
        listView.setAdapter(new TaskListAdapter(this,cursor));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cursor != null)
            cursor.close();
    }

    @Override
    public void onError() {
//        stubTxt.setVisibility(View.VISIBLE);
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Context getMeContext() {
        return this;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long id) {
        showToast("pos = " + i);

    }
}
