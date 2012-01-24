package com.alien_roger.court_deadlines.ui;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import actionbarcompat.ActionBarActivity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TimePicker;
import android.widget.Toast;

import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.db.DBConstants;
import com.alien_roger.court_deadlines.db.DBDataManager;
import com.alien_roger.court_deadlines.entities.CourtCase;
import com.alien_roger.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.court_deadlines.services.AlarmReceiver;
import com.alien_roger.court_deadlines.statics.StaticData;
import com.alien_roger.court_deadlines.tasks.GetTrialsTask;
import com.alien_roger.court_deadlines.tasks.LoadTrials;
import com.alien_roger.court_deadlines.ui.adapters.CaseSpinnerAdapter;
import com.alien_roger.court_deadlines.ui.adapters.TrialsSpinnerAdapter;
import com.alien_roger.court_deadlines.utils.CommonUtils;




/**
 * TaskDetailsActivity class
 *
 * @author alien_roger
 * @created at: 24.12.11 13:03
 */
public class TaskDetailsActivity extends ActionBarActivity implements DataLoadInterface<Object>,
		DialogInterface.OnClickListener, OnTouchListener, OnClickListener {

	private static final int SET_FROM_DATE = 1;
	private static final int SET_FROM_TIME = 2;
	private static final int SET_TO_DATE = 3;
	private static final int SET_TO_TIME = 4;
	private TimePickerDialog fromTimePickerDialog;
	private TimePickerDialog toTimePickerDialog;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	private Calendar fromCalendar;
	private Calendar toCalendar;
	private DateFormat df;

	private DatePickerDialog fromDatePickerDialog;
	private DatePickerDialog toDatePickerDialog;
	private EditText customerEdt;

	private EditText courtDateEdt;
	private EditText proposalDateEdt;
	private EditText courtTimeEdt;
	private EditText proposalTimeEdt;
	private EditText notesEdt;

	private Spinner typeSpinner1;
	private Spinner typeSpinner2;
	private Spinner trialSpinner1;
	private Spinner trialSpinner2;
	private Spinner trialSpinner3;
	private Spinner trialSpinner4;
	private List<Spinner> spinnersList;

	private Context context;
	private SpinnerSelectedListener spinnerSelectedListener;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_screen);

		setTitle(R.string.customer_hint);
		widgetsInit();

		context = this;

		fromCalendar = Calendar.getInstance();
		toCalendar = Calendar.getInstance();
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

		courtDateEdt.setText(df.format(fromCalendar.getTime()));

		fromDatePickerDialog = new DatePickerDialog(this, fromDateSetListener, fromCalendar.get(Calendar.YEAR),
				fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH));
		toDatePickerDialog = new DatePickerDialog(this, toDateSetListener, toCalendar.get(Calendar.YEAR),
				toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH));
		fromTimePickerDialog = new TimePickerDialog(this, fromTimeSetListener, fromCalendar.get(Calendar.HOUR_OF_DAY),
				fromCalendar.get(Calendar.MINUTE), true);
		toTimePickerDialog = new TimePickerDialog(this, toTimeSetListener, toCalendar.get(Calendar.HOUR_OF_DAY),
				toCalendar.get(Calendar.MINUTE), true);

		// updateSpinnerListener = new UpdateSpinner();
		spinnerSelectedListener = new SpinnerSelectedListener();
		init();
	}

	private void init() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean dataSaved = preferences.getBoolean(StaticData.SHP_DATA_SAVED, false);

		if (dataSaved) {
			new LoadTrials(this).execute(StaticData.FIRST_LEVEL);
		}
		enableSpinners(dataSaved);
	}

	private class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
		public SpinnerSelectedListener() {
		}

		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
			boolean haveChild = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_HAVE_CHILD)) > 0;
			int depthLevel = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_DEPTH_LEVEL));

			if (haveChild) {
				long currLevel = cursor.getLong(cursor.getColumnIndex(DBConstants.TRIAL_CURRENT_LEVEL));
				new LoadTrials(TaskDetailsActivity.this).execute(currLevel);
				disableSpinners(depthLevel);
			} else {
				setProposalDate(cursor);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	private void disableSpinners(int depthLevel) {
		for (int i = depthLevel; i < spinnersList.size(); i++) {
			spinnersList.get(i).setEnabled(false);
		}
	}

	@Override
	public void onDataReady(List<Object> cases) {
		// enableSpinners(true);
		// new LoadTrials(updateSpinnerListener).execute(13);
		new LoadTrials(this).execute(StaticData.FIRST_LEVEL);
	}

	private void enableSpinners(boolean enable) {
		for (Spinner spinner : spinnersList) {
			spinner.setEnabled(enable);
			spinner.setOnItemSelectedListener(spinnerSelectedListener);
		}
	}

	@Override
	public void onDataLoaded(Cursor cursor) {
		cursor.moveToFirst();
		int depthLevel = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_DEPTH_LEVEL)) - 1;
		boolean haveChild = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_HAVE_CHILD)) > 0;

		if (haveChild) {
			adjustSpinner(spinnersList.get(depthLevel), new TrialsSpinnerAdapter(context, cursor));
		} else {
			adjustSpinner(spinnersList.get(depthLevel), new CaseSpinnerAdapter(context, cursor));
			setProposalDate(cursor);
		}
	}

	private void setProposalDate(Cursor cursor) {
		String string = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));

		if (string.indexOf(StaticData.CHILD_DELIMITER) < 0)
			return;

		String code = string.substring(string.indexOf(StaticData.CHILD_DELIMITER)
				+ StaticData.CHILD_DELIMITER.length());

		Log.d("setProposalDate", " code = " + code + " length = " + code.length());

		Calendar cal = Calendar.getInstance();
		try {
			cal = CommonUtils.getDateByCode(fromCalendar, code);
		} catch (NumberFormatException e) {
			Log.d("setProposalDate", e.toString());
			new AlertDialog.Builder(context).setTitle(R.string.error).setMessage(R.string.number_format_msg).setPositiveButton(android.R.string.ok, this).setNegativeButton(android.R.string.cancel, this).show();

		}
		proposalDateEdt.setText(df.format(cal.getTime()));
	}

	private void adjustSpinner(Spinner spinner, SpinnerAdapter adapter) {
		spinner.setEnabled(true);
		spinner.setAdapter(adapter);
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case SET_FROM_TIME:
			return fromTimePickerDialog;
		case SET_FROM_DATE:
			return fromDatePickerDialog;
		case SET_TO_TIME:
			return toTimePickerDialog;
		case SET_TO_DATE:
			return toDatePickerDialog;
		default:
			return null;
		}
	}

	private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			fromCalendar.set(Calendar.YEAR, year);
			fromCalendar.set(Calendar.MONTH, monthOfYear);
			fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			Calendar currentDay = Calendar.getInstance();

			// drop down other values
			fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
			fromCalendar.set(Calendar.MINUTE, 0);
			fromCalendar.set(Calendar.SECOND, 0);
			fromCalendar.set(Calendar.MILLISECOND, 0);
			currentDay.set(Calendar.HOUR_OF_DAY, 0);
			currentDay.set(Calendar.MINUTE, 0);
			currentDay.set(Calendar.SECOND, 0);
			currentDay.set(Calendar.MILLISECOND, 0);
			if (fromCalendar.before(currentDay)) {
				showToast(R.string.unable_to_set_past_date);
				return;
			}

			courtDateEdt.setText(df.format(fromCalendar.getTime()));
			toDatePickerDialog.updateDate(toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH));
		}
	};

	private DatePickerDialog.OnDateSetListener toDateSetListener = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
			toCalendar.set(Calendar.YEAR, year);
			toCalendar.set(Calendar.MONTH, monthOfYear);
			toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			Calendar currentDay = Calendar.getInstance();

			// drop down other values
			toCalendar.set(Calendar.HOUR_OF_DAY, 0);
			toCalendar.set(Calendar.MINUTE, 0);
			toCalendar.set(Calendar.SECOND, 0);
			toCalendar.set(Calendar.MILLISECOND, 0);
			currentDay.set(Calendar.HOUR_OF_DAY, 0);
			currentDay.set(Calendar.MINUTE, 0);
			currentDay.set(Calendar.SECOND, 0);
			currentDay.set(Calendar.MILLISECOND, 0);
			if (toCalendar.before(currentDay)) {
				showToast(R.string.unable_to_set_past_date);
				return;
			}

			proposalDateEdt.setText(df.format(toCalendar.getTime()));
		}
	};

	private TimePickerDialog.OnTimeSetListener fromTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			fromCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			fromCalendar.set(Calendar.MINUTE, minute);
			fromCalendar.set(Calendar.SECOND, 0);
			fromCalendar.set(Calendar.MILLISECOND,0);
			courtTimeEdt.setText(timeFormat.format(fromCalendar.getTime()));
			// increase "to time" calendar
			toCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			toCalendar.set(Calendar.MINUTE, minute);
			toCalendar.add(Calendar.HOUR_OF_DAY, 1);// TODO set default value of event length
			toTimePickerDialog.updateTime(toCalendar.get(Calendar.HOUR_OF_DAY), minute);
			proposalTimeEdt.setText(timeFormat.format(toCalendar.getTime()));
		}
	};

	private TimePickerDialog.OnTimeSetListener toTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			toCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			toCalendar.set(Calendar.MINUTE, minute);
			toCalendar.set(Calendar.SECOND, 0);
			toCalendar.set(Calendar.MILLISECOND,0);
			proposalTimeEdt.setText(timeFormat.format(toCalendar.getTime()));
		}
	};

	@Override
	public void onClick(DialogInterface arg0, int which) {
		switch (which) {
		case DialogInterface.BUTTON_POSITIVE:
			// TODO send email
			break;
		default:
			break;
		}
	}

	private void showToast(String msg) {
		Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
	}

	private void showToast(int id) {
		Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
	}

	private void setReminderTime(long triggerAtTime, String taskTitle, int id) {
		long msBefore = 1000;
		long time2Set = triggerAtTime - msBefore;
		Intent statusUpdate = new Intent(context, AlarmReceiver.class);
		statusUpdate.putExtra(StaticData.TASK_TITLE, taskTitle);
		statusUpdate.putExtra(StaticData.REQUEST_CODE, id);
		Log.d("setReminderTime", "id = " + id);
		Log.d("setReminderTime", "triggerAtTime = " + triggerAtTime);
		Log.d("setReminderTime", "time2Set = " + time2Set);

		PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, statusUpdate, PendingIntent.FLAG_UPDATE_CURRENT);

		// schedule the service for updating
		AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		alarms.setRepeating(AlarmManager.RTC_WAKEUP, time2Set, StaticData.REMIND_ALARM_INTERVAL, pendingIntent);
	}

	@Override
	public void showProgress(boolean show) {
		getActionBarHelper().setRefreshActionItemState(show);
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(dataUpdateReceiver, new IntentFilter(StaticData.BROADCAST_ACTION));
	}

	@Override
	protected void onPause() {
		unregisterReceiver(dataUpdateReceiver);
		super.onPause();
	}

	private final BroadcastReceiver dataUpdateReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			int code = intent.getIntExtra(StaticData.SHP_DATA_SAVED, RESULT_CANCELED);
			if (code == RESULT_OK) {
				onDataReady(null);
			} else if (code == RESULT_CANCELED) {
				new GetTrialsTask(TaskDetailsActivity.this).execute(StaticData.LOAD_FILE);
			}
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.details_main, menu);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_refresh:

			// fill task params
			CourtCase courtCase = new CourtCase();
			courtCase.setCaseName("");
			courtCase.setCustomer(customerEdt.getText().toString().trim());
			courtCase.setCourtDate(fromCalendar);
			courtCase.setProposalDate(fromCalendar);
			courtCase.setNotes(notesEdt.getText().toString().trim());
			courtCase.setCourtType("indictment");

			// create task in DB
			Uri uri = getContentResolver().insert(DBConstants.TASKS_CONTENT_URI, DBDataManager.fillCourtCase(courtCase));
			long id = ContentUris.parseId(uri);
			setReminderTime(toCalendar.getTimeInMillis(), courtCase.getCustomer(), (int) id);

			finish();
			break;

		case R.id.menu_cancel:
			Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
			finish();
			break;
		case R.id.menu_preferences:
			startActivity(new Intent(this, SettingsActivity.class));
			overridePendingTransition(R.anim.activity_fade, R.anim.activity_hold);

			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private void widgetsInit() {
		findViewById(R.id.soundBtn).setOnClickListener(this);

		customerEdt = (EditText) findViewById(R.id.customerEdt);
		notesEdt = (EditText) findViewById(R.id.notesEdt);

		courtTimeEdt = (EditText) findViewById(R.id.courtTimeEdt);
		courtTimeEdt.setOnTouchListener(this);

		courtDateEdt = (EditText) findViewById(R.id.courtDateEdt);
		courtDateEdt.setOnTouchListener(this);

		proposalDateEdt = (EditText) findViewById(R.id.proposalDateEdt);
		proposalDateEdt.setOnTouchListener(this);

		proposalTimeEdt = (EditText) findViewById(R.id.proposalTimeEdt);
		proposalTimeEdt.setOnTouchListener(this);

		spinnersList = new ArrayList<Spinner>();

		typeSpinner1 = (Spinner) findViewById(R.id.selectType1);
		typeSpinner2 = (Spinner) findViewById(R.id.selectType2);
		trialSpinner1 = (Spinner) findViewById(R.id.selectTrial1);
		trialSpinner2 = (Spinner) findViewById(R.id.selectTrial2);
		trialSpinner3 = (Spinner) findViewById(R.id.selectTrial3);
		trialSpinner4 = (Spinner) findViewById(R.id.selectTrial4);

		spinnersList.add(typeSpinner1);
		spinnersList.add(typeSpinner2);
		spinnersList.add(trialSpinner1);
		spinnersList.add(trialSpinner2);
		spinnersList.add(trialSpinner3);
		spinnersList.add(trialSpinner4);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if(view.getId() == R.id.courtTimeEdt) {
			showDialog(SET_FROM_TIME);
			return true;
		}else if(view.getId() == R.id.courtDateEdt) {
			showDialog(SET_FROM_DATE);
			return true;
		}else if(view.getId() == R.id.proposalTimeEdt) {
			showDialog(SET_TO_TIME);
			return true;
		}else if(view.getId() == R.id.proposalDateEdt) {
			showDialog(SET_TO_DATE);
			return true;
		}
		return false;
	}

	@Override
	public void onError() {
	}

	@Override
	public Context getMeContext() {
		return this;
	}

	@Override
	public void onClick(View view) {
		if(view.getId() == R.id.soundBtn) {
            Intent intent = new Intent( RingtoneManager.ACTION_RINGTONE_PICKER);
            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound");
            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_RINGTONE_URI);
            intent.putExtra( RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_RINGTONE_URI);
            startActivityForResult( intent, 55);
		}
	}



	// private class UpdateSpinner extends AbstractDataUpdater {
	// public UpdateSpinner() {
	// super(TaskDetailsActivity.this, getActionBarHelper());
	// }
	//
	// @Override
	// public void onTaskLoaded(Cursor cursor) {
	// cursor.moveToFirst();
	// int depthLevel =
	// cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_DEPTH_LEVEL)) -1;
	// boolean haveChild =
	// cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_HAVE_CHILD)) > 0;
	//
	// if (haveChild) {
	// adjustSpinner(spinnersList.get(depthLevel), new
	// TrialsSpinnerAdapter(context, cursor));
	// } else {
	// adjustSpinner(spinnersList.get(depthLevel), new
	// CaseSpinnerAdapter(context, cursor));
	// setProposalDate(cursor);
	// }
	// }
	// }
}