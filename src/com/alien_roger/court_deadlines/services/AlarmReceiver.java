package com.alien_roger.court_deadlines.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.statics.StaticData;
import com.alien_roger.court_deadlines.ui.SettingsActivity;
import com.alien_roger.court_deadlines.ui.TaskListActivity;


public class AlarmReceiver extends BroadcastReceiver {

	private NotificationManager notifyManager;

	@Override
	public void onReceive(Context context, Intent intent) {
		notifyManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		Bundle extras = intent.getExtras();
		int id = extras.getInt(StaticData.REQUEST_CODE);
		String sound = extras.getString(StaticData.TASK_SOUND);
		Log.d("AlarmReceiver", "id = " + id);
		showNotification(context, extras.getString(StaticData.TASK_TITLE), id,sound);
	}

	public void showNotification(Context context, String taskTitle, int id, String sound) {
		// Set the icon, for boarding flight status
		Notification notification = new Notification(R.drawable.ic_stat_alarm, taskTitle, System.currentTimeMillis());
		notification.sound = Uri.parse(sound); // SettingsActivity.getAlarmRingtone(context);
		notification.flags |= Notification.FLAG_AUTO_CANCEL;
		if (SettingsActivity.vibrate4Alarm(context)) {
			notification.defaults = Notification.DEFAULT_VIBRATE;
		}
		Intent openList = new Intent(context, TaskListActivity.class);
		openList.putExtra(StaticData.CLEAR_ALARM, true);
		openList.putExtra(StaticData.REQUEST_CODE, id);
		openList.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
				|Intent.FLAG_ACTIVITY_NEW_TASK
				|Intent.FLAG_ACTIVITY_SINGLE_TOP
				|Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(context, id, openList, PendingIntent.FLAG_ONE_SHOT); // TODO use flags

		notification.setLatestEventInfo(context, taskTitle, context.getText(R.string.notification_screen_status_message), contentIntent);

		notifyManager.notify(R.string.notification_screen_status_message, notification);

	}

}
