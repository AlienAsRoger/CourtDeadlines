package com.alien_roger.court_deadlines.services;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.IBinder;

import com.alien_roger.court_deadlines.db.DBConstants;
import com.alien_roger.court_deadlines.db.DBDataManager;
import com.alien_roger.court_deadlines.entities.CourtCase;

public class UpdatePriorityService extends Service {

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		//get all tasks

		new UpdatePrioritiesTask().execute();


		return START_STICKY_COMPATIBILITY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	private class UpdatePrioritiesTask extends AsyncTask<Void, Void, Void>{

		@Override
		protected Void doInBackground(Void... params) {
			Cursor cursor = getContentResolver().query(DBConstants.TASKS_CONTENT_URI, null, null, null, null);

			if (cursor.moveToFirst()) {
				do {
					CourtCase courtCase = new CourtCase();
					DBDataManager.getCourtCaseFromCursor(courtCase, cursor);
					courtCase.updatePriority(courtCase.getCourtDate());
					getContentResolver().update(Uri.parse(DBConstants.TASKS_CONTENT_URI.toString()
							+ "/"+courtCase.getId() ),
							DBDataManager.putCourtCase2Values(courtCase), null, null);
				} while (cursor.moveToNext());
			}
			return null;
		}

	}


}
