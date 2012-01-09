package com.alien_roger.android.court_deadlines.tasks;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.interfaces.TaskLoadInterface;

public class LoadTasks extends AsyncTask<Long, Void, Boolean> {

	private TaskLoadInterface taskLoadFace;
	private ContentResolver resolver;
	private Cursor tasksCursor;
	
	public LoadTasks(TaskLoadInterface taskLoadFace){
		this.taskLoadFace = taskLoadFace;
		resolver = taskLoadFace.getMeContext().getContentResolver();
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		taskLoadFace.showProgress(true);
	}
	
	@Override
	protected Boolean doInBackground(Long... ids) {
		if(ids != null && ids.length > 0){
			tasksCursor = resolver.query(Uri.parse(DBConstants.TASKS_CONTENT_URI.toString() + "/"+ids[0] ), null, null, null, "");
		}else{
			tasksCursor = resolver.query(DBConstants.TASKS_CONTENT_URI, null, null, null, "");			
		}

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
