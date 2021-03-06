package com.alien_roger.court_deadlines.ui;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.db.DBConstants;
import com.alien_roger.court_deadlines.db.DBDataManager;
import com.alien_roger.court_deadlines.entities.CourtCase;
import com.alien_roger.court_deadlines.services.AlarmReceiver;
import com.alien_roger.court_deadlines.statics.StaticData;

import java.util.Calendar;




/**
 * TaskDetailsActivity class
 *
 * @author alien_roger
 * @created at: 24.12.11 13:03
 */
public class TaskDetailsViewActivity extends TaskDetailsActivity {

	private CourtCase courtCase;

	@Override
	protected void onResume() {
		super.onResume();
		courtCase = (CourtCase) getIntent().getExtras().getSerializable(StaticData.COURT_CASE);

		setTitle(courtCase.getCustomer());
		// useless
//		courtCase.setCaseName("");
//		courtCase.setCourtType("indictment");

		customerEdt.setText(courtCase.getCustomer());

		fromCalendar = courtCase.getProposalDate();
		toCalendar = courtCase.getCourtDate();

		notesEdt.setText(courtCase.getNotes());

		courtDateEdt.setText(df.format(toCalendar.getTime()));
		proposalDateEdt.setText(df.format(fromCalendar.getTime()));

		toDatePickerDialog.updateDate(
				toCalendar.get(Calendar.YEAR),
				toCalendar.get(Calendar.MONTH),
				toCalendar.get(Calendar.DAY_OF_MONTH));

		fromDatePickerDialog.updateDate(
				fromCalendar.get(Calendar.YEAR),
				fromCalendar.get(Calendar.MONTH),
				fromCalendar.get(Calendar.DAY_OF_MONTH));

//		proposalTimeEdt.setText(timeFormat.format(toCalendar.getTime()));
//		courtTimeEdt.setText(timeFormat.format(fromCalendar.getTime()));

		soundBtn.setText(courtCase.getReminderSound());
		remindSound = courtCase.getReminderSound();
//		         s
		remindSpinner.setSelection(courtCase.getReminderTimePosition());
		userChangedProposalCalendar = true;

		prioritiesList.get(courtCase.getPriority()).setChecked(true);
//		prioritiesSpinner.setSelection(courtCase.getPriority());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.edit_details, menu);
		boolean retValue = false;
		retValue |= getActionBarHelper().onCreateOptionsMenu(menu);
		return retValue;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			onBackPressed();
			break;

		case R.id.menu_refresh:

			fillCourtCaseObject(courtCase);
			// create task in DB
			int cnt = getContentResolver().update(Uri.parse(DBConstants.TASKS_CONTENT_URI.toString()
					+ "/"+courtCase.getId() ),
					DBDataManager.putCourtCase2Values(courtCase), null, null);
			setReminderTime(fromCalendar.getTimeInMillis(), courtCase.getCustomer(), (int) courtCase.getId());

			finish();
			break;

		case R.id.menu_delete:
			getContentResolver().delete(Uri.parse(DBConstants.TASKS_CONTENT_URI.toString() + "/"+courtCase.getId() ), null, null);

			int ID =(int) courtCase.getId();
			Log.d("TaskListActivity", "id = " + ID);
			Intent statusUpdate = new Intent(this, AlarmReceiver.class);
			PendingIntent pendingIntent = PendingIntent.getBroadcast(this, ID, statusUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
			AlarmManager alarms = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
			alarms.cancel(pendingIntent);

			finish();
			break;
		case R.id.menu_preferences:
			startActivity(new Intent(this, SettingsActivity.class));
			overridePendingTransition(R.anim.activity_fade, R.anim.activity_hold);

			break;
		}
		return true;
	}







}