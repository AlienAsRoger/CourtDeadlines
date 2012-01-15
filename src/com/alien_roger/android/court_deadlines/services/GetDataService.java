package com.alien_roger.android.court_deadlines.services;

import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import com.alien_roger.android.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.android.court_deadlines.tasks.GetTrialsTask;

public class GetDataService extends Service implements DataLoadInterface<Object>{



	@Override
	public void onCreate() {
		super.onCreate();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new GetTrialsTask(this).execute("d2.txt");
		return START_STICKY_COMPATIBILITY;
	}

	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

	@Override
	public void showProgress(boolean show) {
	}

	@Override
	public void onDataReady(List<Object> cases) {
//		Intent broadcast = new Intent(StaticData.BROADCAST_ACTION);
//		broadcast.putExtra(StaticData.SHP_DATA_SAVED, Activity.RESULT_OK);
//		sendBroadcast(broadcast);
		stopSelf();
	}

	@Override
	public void onTaskLoaded(Cursor cursor) {

	}

	@Override
	public void onError() {
		Log.e("GetDataService", "Error happend while loading data");
	}

	@Override
	public Context getMeContext() {
		return null;
	}

}
