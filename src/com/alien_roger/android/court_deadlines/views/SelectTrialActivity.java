package com.alien_roger.android.court_deadlines.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import actionbarcompat.ActionBarActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.AssetManager;
import android.database.Cursor;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.adapters.TrialsCursorAdapter;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.db.DBDataManager;
import com.alien_roger.android.court_deadlines.entities.CourtObj;
import com.alien_roger.android.court_deadlines.entities.CourtType;
import com.alien_roger.android.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.android.court_deadlines.statics.StaticData;
import com.alien_roger.android.court_deadlines.tasks.LoadTrials;
import com.alien_roger.android.court_deadlines.xml_parsers.HtmlHelper;

/**
 * SelectTrialActivity class
 *
 * @author alien_roger
 * @created at: 29.12.11 5:38
 */
public class SelectTrialActivity extends ActionBarActivity implements DataLoadInterface<Object>, AdapterView.OnItemClickListener {
    private List<CourtType> courtTypes;
    private String TAG = "SelectTrialActivity";
    private ProgressBar progressBar;
    private ListView listView;
    private AssetManager assetManager;
    private SharedPreferences preferences;

    @Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
        setContentView(R.layout.trial_list_screen);

        assetManager = getAssets();
        preferences = PreferenceManager.getDefaultSharedPreferences(this);
        widgetsInit();
        boolean dataSaved = preferences.getBoolean(StaticData.SHP_DATA_SAVED, false);

        if(!dataSaved)
        	new Get().execute(getIntent().getExtras().getString(StaticData.URL_PATH));
        else
        	new LoadTrials(SelectTrialActivity.this).execute(0);

    }

    private void widgetsInit(){
        listView = (ListView) findViewById(android.R.id.list);
        listView.setOnItemClickListener(this);
        listView.setEmptyView(findViewById(android.R.id.empty));
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
    }

    private class Get extends AsyncTask<String, Void, Boolean>{
    	private List<CourtObj> courtObjs;

    	public Get(){
    		courtObjs = new ArrayList<CourtObj>();
    	}
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            getActionBarHelper().setRefreshActionItemState(true);
        }

        @Override
        protected Boolean doInBackground(String... ids) {
            try {
                InputStream inputStream = assetManager.open("d2.txt");
                InputStreamReader inputreader = new InputStreamReader(inputStream);
                BufferedReader buffreader = new BufferedReader(inputreader);
                String line;
                StringBuilder text = new StringBuilder();

                while (( line = buffreader.readLine()) != null ) {
                	if(line.length() == 0) continue;
                	courtObjs.add(parseText(line));
                    text.append(line);
                    text.append('\n');
                }
            } catch (IOException e) {
            	Log.d(TAG,e.toString());
                return null;
            }

            // save
            for (CourtObj courtObj : courtObjs) {
            	getContentResolver().insert(DBConstants.TRIALS_CONTENT_URI, DBDataManager.fillCourtObj(courtObj));
			}
            return true;
        }

        private CourtObj parseText(String line){
        	CourtObj courtObj = new CourtObj();
        	int numbs =   line.lastIndexOf(".");
        	String value = line.substring(numbs + 1);
        	String levelLine = line.substring(0,numbs);
        	String[] levels = levelLine.split(Pattern.quote("."));
        	int parentLevel = 0;
        	int currentLevel = 0;
        	for (int i = 0; i < levels.length; i++) {
        		currentLevel += Integer.parseInt(levels[i]) + 10 *(i+1);
			}

        	for (int i = 0; i < levels.length - 1; i++) {
				parentLevel += Integer.parseInt(levels[i]) + 10 *(i+1);
			}

            int level =   numbs/2;
        	Log.d(TAG,"parent = " + parentLevel + "current = " + currentLevel + "\nlevel = " + level + "\nvalue = " + value );

//            Log.d(TAG,"level" + level);
            courtObj.setHaveChilds(false);
            courtObj.setDepthLevel(level);
            courtObj.setValue(value.trim());
            courtObj.setParentLevel(parentLevel);
            courtObj.setCurrentLevel(currentLevel);
            return courtObj;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            progressBar.setVisibility(View.INVISIBLE);
            getActionBarHelper().setRefreshActionItemState(false);
            if(aBoolean){
            	Editor editor = preferences.edit();
            	editor.putBoolean(StaticData.SHP_DATA_SAVED, true);
            	editor.commit();
//                listView.setAdapter(new TrialsAdapter(SelectTrialActivity.this, courtTypes));
            	new LoadTrials(SelectTrialActivity.this).execute(0);
//                listView.setAdapter(new TrialsAdapter2(SelectTrialActivity.this, courtObjs));
            }
        }
    }


    public Boolean parseDataFromHtml(String urlString) throws Exception {
        final HttpClient client = AndroidHttpClient.newInstance("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.63 Safari/535.7");
        Log.d(TAG, "retreiving from url = " + urlString);
        boolean connectedFlag = false;
        final HttpGet httpost = new HttpGet(urlString);
        try {
            HttpResponse response = client.execute(httpost);
            final int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK){
                Log.e("TAG", "Error " + statusCode +" while retrieving dat from " + urlString);
                return false;
            }

            final HttpEntity entity = response.getEntity();
            if (entity != null) {
                InputStream inputStream = null;
                try {
                    inputStream = entity.getContent();
                    HtmlHelper helper = new HtmlHelper( inputStream);

                    courtTypes = helper.getCourtTypes();
                    connectedFlag = true;
                }catch (Exception e){
                    httpost.abort();
                    Log.d(TAG, e.toString());
                    e.printStackTrace();

                } finally {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                    entity.consumeContent();
                }
            }
        } catch (IOException e) {
            httpost.abort();
            Log.d(TAG, e.toString());
        } catch (IllegalStateException e) {
            httpost.abort();
            Log.d(TAG, e.toString());
        } catch (Exception e) {
            httpost.abort();
            Log.d(TAG,e.toString());
        } finally {
            if ((client instanceof AndroidHttpClient)) {
                ((AndroidHttpClient) client).close();
            }
        }
        return connectedFlag;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
    	Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
        showToast(cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE)));
    	String title = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));

//        if(cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_DEPTH_LEVEL)) <= 6 ){
            Intent intent = new Intent(this,TrialListActivity.class);
            intent.putExtra(StaticData.ID, cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_CURRENT_LEVEL)));
            intent.putExtra(StaticData.TITLE, title);
            startActivity(intent);
//        }
    }

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

	@Override
	public void showProgress(boolean show) {
		progressBar.setVisibility(show? View.VISIBLE: View.INVISIBLE);
	}

	@Override
	public void onDataReady(List<Object> cases) {}

	@Override
	public void onTaskLoaded(Cursor cursor) {
		listView.setAdapter(new TrialsCursorAdapter(this, cursor));
	}

	@Override
	public void onError() {}

	@Override
	public Context getMeContext() {
		return this;
	}
}
