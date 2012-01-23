package com.alien_roger.court_deadlines.ui;

import java.util.List;

import actionbarcompat.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.alien_roger.court_deadlines.AppConstants;
import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.ui.adapters.TaskListAdapter;
import com.alien_roger.court_deadlines.db.DBConstants;
import com.alien_roger.court_deadlines.db.DBDataProvider2;
import com.alien_roger.court_deadlines.entities.CourtCase;
import com.alien_roger.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.court_deadlines.services.GetDataService;
import com.alien_roger.court_deadlines.statics.StaticData;
import com.alien_roger.court_deadlines.tasks.LoadTasks;

public class TaskListActivity extends ActionBarActivity implements DataLoadInterface<CourtCase>, AdapterView.OnItemClickListener {
    private ListView listView;
    private Cursor cursor;
    private SharedPreferences preferences;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task_list_screen);

        widgetsInit();
        init();
    }

    private void init(){
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dataSaved = preferences.getBoolean(StaticData.SHP_DATA_SAVED, false);

        String prevFileName = preferences.getString(StaticData.SHP_LOAD_FILE, "");
        if(!prevFileName.equals(StaticData.LOAD_FILE)){
        	dataSaved = false;
        	getContentResolver().delete(DBConstants.TRIALS_CONTENT_URI, null, null);
        }

        if(DBDataProvider2.getDbVersion() != preferences.getInt(StaticData.SHP_DB_VERSION, 0)) {
        	dataSaved = false;
        }

        if(!dataSaved) {
        	startService(new Intent(this,GetDataService.class));
        	Editor editor = preferences.edit();
        	editor.putBoolean(StaticData.SHP_DATA_SAVED, false);
        	editor.putInt(StaticData.SHP_DB_VERSION, DBDataProvider2.getDbVersion());
        	editor.commit();
        }

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
    public void onDataLoaded(Cursor cursor) {
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