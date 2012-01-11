package com.alien_roger.android.court_deadlines.tasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;

import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.db.DBDataManager;
import com.alien_roger.android.court_deadlines.interfaces.TaskLoadInterface;

public class LoadTrials extends AsyncTask<Integer, Void, Boolean> {

	private TaskLoadInterface taskLoadFace;
	private ContentResolver resolver;
	private Cursor tasksCursor;

	public LoadTrials(TaskLoadInterface taskLoadFace){
		this.taskLoadFace = taskLoadFace;
		resolver = taskLoadFace.getMeContext().getContentResolver();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		taskLoadFace.showProgress(true);
	}

	@Override
	protected Boolean doInBackground(Integer... ids) {
//		if(ids != null && ids.length > 0){
//			tasksCursor = resolver.query(Uri.parse(DBConstants.TRIALS_CONTENT_URI.toString() + "/"+ids[0] ), null, null, null, "");
//		}else{
			final String[] arguments1 = new String[1];
			arguments1[0] = String.valueOf(ids[0]);
			tasksCursor = resolver.query(DBConstants.TRIALS_CONTENT_URI,
					null,
					DBDataManager.parentLevelSelection,
					arguments1, null);
          if (tasksCursor.getCount() > 0) {
    	  	return true;
          }
//		}

//        final String[] arguments3 = new String[3];
//        arguments3[0] = futureMessage.getFlight_id();
//        arguments3[1] = futureMessage.getSchedule_date_time();
//        arguments3[2] = futureMessage.getArrival_Departure();
//
//        cursor = resolver.query(DB_constants.FLIGHTS_CONTENT_URI, PROJECTION_FLIGHT_ID_AND_DATETIME,
//    			futureFlightSelection, arguments3, null);
//                if (cursor.getCount() > 0) {
//                	return true;
//                }

		return tasksCursor.getCount()>0;
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		taskLoadFace.showProgress(false);
        if(result)
		    taskLoadFace.onTaskLoaded(tasksCursor);
        else
            taskLoadFace.onError();
	}
}
