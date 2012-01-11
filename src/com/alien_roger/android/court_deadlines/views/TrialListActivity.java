package com.alien_roger.android.court_deadlines.views;

import java.util.List;

import actionbarcompat.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.adapters.TrialsCursorAdapter;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.entities.CourtCase;
import com.alien_roger.android.court_deadlines.interfaces.TaskLoadInterface;
import com.alien_roger.android.court_deadlines.statics.StaticData;
import com.alien_roger.android.court_deadlines.tasks.LoadTrials;

/**
 * SelectTrialActivity class
 *
 * @author alien_roger
 * @created at: 29.12.11 5:38
 */
public class TrialListActivity extends ActionBarActivity implements TaskLoadInterface, AdapterView.OnItemClickListener {
    private String TAG = "SelectTrialActivity";
    private ProgressBar progressBar;
    private ListView listView;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.trial_list_screen);

        setTitle(getIntent().getExtras().getString(StaticData.TITLE));

        widgetsInit();
        new LoadTrials(this).execute(getIntent().getExtras().getInt(StaticData.ID));
    }

    private void widgetsInit(){
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(findViewById(android.R.id.empty));

        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    	Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
    	int curLevel = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_CURRENT_LEVEL));
    	int parLevel = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_PARENT_LEVEL));
    	String title = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));
        showToast(String.valueOf(curLevel));
        Log.d(TAG,"curLevel = " + curLevel + "paren = " + parLevel);
//        if(cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_DEPTH_LEVEL)) <= 6 ){
            Intent intent = new Intent(this,TrialListActivity.class);
            intent.putExtra(StaticData.ID, curLevel);
            intent.putExtra(StaticData.TITLE, title);
            startActivity(intent);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void showProgress(boolean show) {
		progressBar.setVisibility(show? View.VISIBLE: View.INVISIBLE);
	}

	@Override
	public void onDataReady(List<CourtCase> cases) {}

	@Override
	public void onTaskLoaded(Cursor cursor) {
		listView.setAdapter(new TrialsCursorAdapter(this, cursor));
	}

	@Override
	public void onError() {

	}

	@Override
	public Context getMeContext() {
		return this;
	}
}
