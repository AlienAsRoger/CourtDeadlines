package com.alien_roger.android.court_deadlines.tasks;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.db.DBDataManager;
import com.alien_roger.android.court_deadlines.entities.CourtObj;
import com.alien_roger.android.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.android.court_deadlines.statics.StaticData;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class GetTrialsTask extends AsyncTask<String, Void, Boolean>{
	private static final String TAG = "GetTrialsTask";

	private Context context;
	private DataLoadInterface<Object> dataFace;

	public GetTrialsTask(DataLoadInterface<Object> dataFace){
    	this.context = dataFace.getMeContext();
    	this.dataFace = dataFace;
		context = dataFace.getMeContext();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		dataFace.showProgress(true);
	}

    @Override
    protected Boolean doInBackground(String... dataName) {
        List<CourtObj> courtObjs = new ArrayList<CourtObj>();
        try {
            InputStream inputStream = context.getAssets().open(dataName[0]);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader buffreader = new BufferedReader(inputreader);
            String line;
            while (( line = buffreader.readLine()) != null ) {
            	if(line.length() == 0) continue;
            	courtObjs.add(parseText(line));
            }
        } catch (IOException e) {
        	Log.d(TAG,e.toString());
            return null;
        }

        // save
        for (CourtObj courtObj : courtObjs) {
        	context.getContentResolver().insert(DBConstants.TRIALS_CONTENT_URI, DBDataManager.fillCourtObj(courtObj));
		}

        return true;
    }

    private CourtObj parseText(String line){
    	CourtObj courtObj = new CourtObj();
    	int numbs =   line.lastIndexOf(StaticData.LEVEL_DELIMITER);

    	String value = line.substring(numbs + 1);
    	String levelLine = line.substring(0,numbs);
    	String[] levels = levelLine.split(Pattern.quote(StaticData.LEVEL_DELIMITER));

    	int parentLevel = 0;
    	int currentLevel = 0;
    	for (int i = 0; i < levels.length; i++) {
    		currentLevel += Integer.parseInt(levels[i]) + 10 *(i+1);
    		if(i < levels.length - 1){
    			parentLevel += Integer.parseInt(levels[i]) + 10 *(i+1);
    		}
		}

		courtObj.setHaveChild(!value.contains(StaticData.CHILD_DELIMITER));

        int level = numbs/2;
//    	Log.d(TAG,"parent = " + parentLevel + "current = " + currentLevel + "\nlevel = " + level + "\nvalue = " + value );

        courtObj.setDepthLevel(level);
        courtObj.setValue(value.trim());
        courtObj.setParentLevel(parentLevel);
        courtObj.setCurrentLevel(currentLevel);
        return courtObj;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        super.onPostExecute(result);
        dataFace.showProgress(false);
        if(result){
        	SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        	Editor editor = preferences.edit();
			editor.putBoolean(StaticData.SHP_DATA_SAVED, true);
			editor.putString(StaticData.SHP_LOAD_FILE, StaticData.LOAD_FILE);
        	editor.commit();
        }
        dataFace.onDataReady(null);
    }
}
