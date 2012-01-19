package com.alien_roger.android.court_deadlines.tasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.db.DBDataManager;
import com.alien_roger.android.court_deadlines.interfaces.DataLoadInterface;

public class LoadTrials extends AsyncTask<Integer, Void, Boolean> {

	private DataLoadInterface<Object> taskLoadFace;
	private ContentResolver resolver;
	private Cursor tasksCursor;

	public LoadTrials(DataLoadInterface<Object> taskLoadFace){
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
		final String[] arguments1 = new String[1];
		arguments1[0] = String.valueOf(ids[0]);
		tasksCursor = resolver.query(DBConstants.TRIALS_CONTENT_URI,
				null,
				DBDataManager.parentLevelSelection,
				arguments1, null);
//		tasksCursor = resolver.query(DBConstants.TRIALS_CONTENT_URI,
//				null,
//				null,
//				null, "");
		return tasksCursor.getCount() > 0;

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
