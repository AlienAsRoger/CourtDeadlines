package com.alien_roger.court_deadlines.services;

import android.app.*;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;
import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.court_deadlines.statics.StaticData;
import com.alien_roger.court_deadlines.tasks.GetTrialsTask;
import com.alien_roger.court_deadlines.ui.TaskListActivity;

import java.util.List;

public class GetDataService extends Service implements DataLoadInterface<Object> {

	private NotificationManager notifyManager;


	@Override
	public void onCreate() {
		super.onCreate();
		notifyManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		new GetTrialsTask(this).execute(StaticData.LOAD_FILE);
		showNotification(this,
				getString(R.string.notification_update_status_title),
				getString(R.string.notification_update_status_message),
				getString(R.string.notification_update_message));
		return START_STICKY_COMPATIBILITY;
	}

	public void showNotification(Context context, String title, String body, String ticker) {
		Notification notification = new Notification(R.drawable.ic_stat_updating,
				ticker,
				System.currentTimeMillis());
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		Intent openList = new Intent(context, TaskListActivity.class);
		PendingIntent contentIntent = PendingIntent.getActivity(context, 2, openList, 0);

		notification.setLatestEventInfo(context, title, body, contentIntent);

		notifyManager.notify(2, notification);
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
		Intent broadcast = new Intent(StaticData.BROADCAST_ACTION);
		broadcast.putExtra(StaticData.SHP_DATA_SAVED, Activity.RESULT_OK);
		sendBroadcast(broadcast);

		showNotification(this,
				getString(R.string.notification_update_status_title_finished),
				getString(R.string.notification_update_status_message_finished),
				getString(R.string.notification_update_message_finished));
		stopSelf();
		notifyManager.cancel(2);
	}

	@Override
	public void onDataLoaded(Cursor cursor) {
	}

	@Override
	public void onError() {
		Log.e("GetDataService", "Error happend while loading data");
	}

	@Override
	public Context getMeContext() {
		return this;
	}

}
