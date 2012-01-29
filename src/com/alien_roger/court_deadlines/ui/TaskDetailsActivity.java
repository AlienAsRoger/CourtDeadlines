package com.alien_roger.court_deadlines.ui;

import actionbarcompat.ActionBarActivity;
import android.app.*;
import android.content.*;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.*;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.*;
import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.db.DBConstants;
import com.alien_roger.court_deadlines.db.DBDataManager;
import com.alien_roger.court_deadlines.entities.CourtCase;
import com.alien_roger.court_deadlines.entities.PriorityObject;
import com.alien_roger.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.court_deadlines.services.AlarmReceiver;
import com.alien_roger.court_deadlines.statics.StaticData;
import com.alien_roger.court_deadlines.tasks.GetTrialsTask;
import com.alien_roger.court_deadlines.tasks.LoadTrials;
import com.alien_roger.court_deadlines.ui.adapters.CaseSpinnerAdapter;
import com.alien_roger.court_deadlines.ui.adapters.PrioritiesAdapter;
import com.alien_roger.court_deadlines.ui.adapters.TrialsSpinnerAdapter;
import com.alien_roger.court_deadlines.utils.CommonUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * TaskDetailsActivity class
 *
 * @author alien_roger
 * @created at: 24.12.11 13:03
 */
public class TaskDetailsActivity extends ActionBarActivity implements DataLoadInterface<Object>,
		DialogInterface.OnClickListener, OnTouchListener, OnClickListener {

	protected static final int SET_FROM_DATE = 1;
	protected static final int SET_FROM_TIME = 2;
	protected static final int SET_TO_DATE = 3;
	protected static final int SET_TO_TIME = 4;
	protected TimePickerDialog fromTimePickerDialog;
	protected TimePickerDialog toTimePickerDialog;
	protected SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
	protected SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm");

	protected Calendar fromCalendar;
	protected Calendar toCalendar;
	protected DateFormat df;

	protected DatePickerDialog fromDatePickerDialog;
	protected DatePickerDialog toDatePickerDialog;
	protected EditText customerEdt;

	protected EditText courtDateEdt;
	protected EditText proposalDateEdt;
	protected EditText courtTimeEdt;
	protected EditText proposalTimeEdt;
	protected EditText notesEdt;

	protected List<Spinner> spinnersList;

	protected Context context;
	protected SpinnerSelectedListener spinnerSelectedListener;
	protected boolean need2update;
	protected Spinner remindSpinner;
	private ReminderSelectedListenr reminderSelectedListener;
	private PrioritySelectedListenr prioritySelectedListenr;
	protected int reminderSelectedPos;
	protected int[] remindTimes;
	protected String remindSound;
	protected Button soundBtn;
	protected boolean userChangedCalendar;
	protected List<PriorityObject> prioritiesList;
	protected Spinner prioritiesSpinner;
	protected int priority;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.details_screen);

		setTitle(R.string.customer_hint);

		context = this;

		spinnerSelectedListener = new SpinnerSelectedListener();
		reminderSelectedListener = new ReminderSelectedListenr();

		widgetsInit();


		fromCalendar = Calendar.getInstance();
		toCalendar = Calendar.getInstance();
		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());

		courtDateEdt.setText(df.format(fromCalendar.getTime()));
		courtTimeEdt.setText(timeFormat.format(fromCalendar.getTime()));

		fromDatePickerDialog = new DatePickerDialog(this, fromDateSetListener, fromCalendar.get(Calendar.YEAR),
				fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH));
		toDatePickerDialog = new DatePickerDialog(this, toDateSetListener, toCalendar.get(Calendar.YEAR),
				toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH));
		fromTimePickerDialog = new TimePickerDialog(this, fromTimeSetListener, fromCalendar.get(Calendar.HOUR_OF_DAY),
				fromCalendar.get(Calendar.MINUTE), true);
		toTimePickerDialog = new TimePickerDialog(this, toTimeSetListener, toCalendar.get(Calendar.HOUR_OF_DAY),
				toCalendar.get(Calendar.MINUTE), true);

		need2update = true;
	}

	private void init() {
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		boolean dataSaved = preferences.getBoolean(StaticData.SHP_DATA_SAVED, false);

		if (dataSaved) {
			new LoadTrials(this).execute(StaticData.FIRST_LEVEL);
		} else {
			showProgress(true);
		}
		enableSpinners(dataSaved);

		String[] prioritiesStrings = getResources().getStringArray(R.array.priorities);

		prioritiesList = new ArrayList<PriorityObject>();
		for (int h = 0; h < prioritiesStrings.length; h++) {
			PriorityObject priorityObject = new PriorityObject();
			priorityObject.setValue(h);
			priorityObject.setText(prioritiesStrings[h]);
			prioritiesList.add(priorityObject);
		}

		prioritiesSpinner.setAdapter(new PrioritiesAdapter(this,prioritiesList));
	}

	protected class SpinnerSelectedListener implements AdapterView.OnItemSelectedListener {
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
				if (!userChangedCalendar)
					setProposalDate(cursor);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	protected class ReminderSelectedListenr implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			reminderSelectedPos = i;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	protected class PrioritySelectedListenr implements AdapterView.OnItemSelectedListener {
		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
			priority = i;
		}

		@Override
		public void onNothingSelected(AdapterView<?> adapterView) {
		}
	}

	private void disableSpinners(int depthLevel) {
		for (int i = depthLevel; i < spinnersList.size(); i++) {
			spinnersList.get(i).setEnabled(false);
		}
	}

	@Override
	public void onDataReady(List<Object> cases) {
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
		need2update = false;
		cursor.moveToFirst();
		int depthLevel = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_DEPTH_LEVEL)) - 1;
		boolean haveChild = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_HAVE_CHILD)) > 0;

		if (haveChild) {
			adjustSpinner(spinnersList.get(depthLevel), new TrialsSpinnerAdapter(context, cursor));
		} else {
			adjustSpinner(spinnersList.get(depthLevel), new CaseSpinnerAdapter(context, cursor));
			if (!userChangedCalendar)
				setProposalDate(cursor);
		}
	}

	protected void setProposalDate(Cursor cursor) {
		String string = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));

		if (string.indexOf(StaticData.CHILD_DELIMITER) < 0)
			return;

		String code = string.substring(string.indexOf(StaticData.CHILD_DELIMITER)
				+ StaticData.CHILD_DELIMITER.length());

		Log.d("setProposalDate", " code = " + code + " length = " + code.length());

		try {
			toCalendar = CommonUtils.getDateByCode(fromCalendar, code);
		} catch (NumberFormatException e) {
			Log.d("setProposalDate", e.toString());
			new AlertDialog.Builder(context).setTitle(R.string.error).setMessage(R.string.number_format_msg).setPositiveButton(android.R.string.ok, this).setNegativeButton(android.R.string.cancel, this).show();

		}
		proposalDateEdt.setText(df.format(toCalendar.getTime()));
		proposalTimeEdt.setText(timeFormat.format(toCalendar.getTime()));

		toDatePickerDialog.updateDate(toCalendar.get(Calendar.YEAR), toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH));
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

	protected DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
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
			userChangedCalendar = true;
		}
	};

	private TimePickerDialog.OnTimeSetListener fromTimeSetListener = new TimePickerDialog.OnTimeSetListener() {
		@Override
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			fromCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
			fromCalendar.set(Calendar.MINUTE, minute);
			fromCalendar.set(Calendar.SECOND, 0);
			fromCalendar.set(Calendar.MILLISECOND, 0);
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
			toCalendar.set(Calendar.MILLISECOND, 0);
			proposalTimeEdt.setText(timeFormat.format(toCalendar.getTime()));
			userChangedCalendar = true;
		}
	};

	@Override
	public void onClick(DialogInterface arg0, int which) {
		switch (which) {
			case DialogInterface.BUTTON_POSITIVE: // dissmiss calendar warning dialog
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

	protected void setReminderTime(long triggerAtTime, String taskTitle, int id) {
		long msBefore = remindTimes[reminderSelectedPos] * 1000;

		long time2Set = triggerAtTime - msBefore;
		Intent statusUpdate = new Intent(context, AlarmReceiver.class);
		statusUpdate.putExtra(StaticData.TASK_TITLE, taskTitle);
		statusUpdate.putExtra(StaticData.REQUEST_CODE, id);
		statusUpdate.putExtra(StaticData.TASK_SOUND, remindSound);

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
		init();
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
				onBackPressed();
//			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
				break;

			case R.id.menu_refresh:

				// fill task params
				CourtCase courtCase = new CourtCase();
				fillCourtCaseObject(courtCase);

				// create task in DB
				Uri uri = getContentResolver().insert(DBConstants.TASKS_CONTENT_URI, DBDataManager.fillCourtCase2ContentValues(courtCase));
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

	protected void fillCourtCaseObject(CourtCase courtCase) {
		courtCase.setCaseName("");
		courtCase.setCustomer(customerEdt.getText().toString().trim());
		courtCase.setCourtDate(fromCalendar);
		courtCase.setProposalDate(toCalendar);
		courtCase.setNotes(notesEdt.getText().toString().trim());
		Cursor cursor = (Cursor) spinnersList.get(0).getSelectedItem();

//		cursor.getString(cursor.getColumnIndex(DBCons))
		courtCase.setCourtType(cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE)));
		courtCase.setReminderSound(remindSound);
		courtCase.setReminderTimePosition(reminderSelectedPos);
		int pPos = prioritiesSpinner.getSelectedItemPosition();
		Log.d("TEST", "Priority set = " + pPos);
		courtCase.setPriority(pPos);
	}

	private void widgetsInit() {
		soundBtn = (Button) findViewById(R.id.soundBtn);
		soundBtn.setOnClickListener(this);
		Uri defaultSoundUri = SettingsActivity.getAlarmRingtone(context);
		remindSound = defaultSoundUri.toString();
		soundBtn.setText(RingtoneManager.getRingtone(context, defaultSoundUri).getTitle(context));


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

		spinnersList.add((Spinner) findViewById(R.id.selectType1));
		spinnersList.add((Spinner) findViewById(R.id.selectType2));
		spinnersList.add((Spinner) findViewById(R.id.selectTrial1));
		spinnersList.add((Spinner) findViewById(R.id.selectTrial2));
		spinnersList.add((Spinner) findViewById(R.id.selectTrial3));
		spinnersList.add((Spinner) findViewById(R.id.selectTrial4));

		remindTimes = getResources().getIntArray(R.array.reminder_update_time_entry_int_values);
		remindSpinner = (Spinner) findViewById(R.id.remindSpinner);
		remindSpinner.setOnItemSelectedListener(reminderSelectedListener);

		prioritiesSpinner = (Spinner) findViewById(R.id.prioritySpinner);
		prioritiesSpinner.setOnItemSelectedListener(prioritySelectedListenr);
	}

	@Override
	public boolean onTouch(View view, MotionEvent motionEvent) {
		if (view.getId() == R.id.courtTimeEdt) {
			showDialog(SET_FROM_TIME);
			return true;
		} else if (view.getId() == R.id.courtDateEdt) {
			showDialog(SET_FROM_DATE);
			return true;
		} else if (view.getId() == R.id.proposalTimeEdt) {
			showDialog(SET_TO_TIME);
			return true;
		} else if (view.getId() == R.id.proposalDateEdt) {
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
		if (view.getId() == R.id.soundBtn) {
			Intent intent = new Intent(RingtoneManager.ACTION_RINGTONE_PICKER);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, RingtoneManager.TYPE_NOTIFICATION);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_TITLE, "Select Notification Sound");
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_DEFAULT_URI, Settings.System.DEFAULT_RINGTONE_URI);
			intent.putExtra(RingtoneManager.EXTRA_RINGTONE_EXISTING_URI, Settings.System.DEFAULT_RINGTONE_URI);
			startActivityForResult(intent, StaticData.PICK_SOUND);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
//		need2update = false;
		if (resultCode == RESULT_OK) {
			if (requestCode == StaticData.PICK_SOUND) {
				Uri uri = data.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI);
				if (uri != null) {
//					Ringtone ringtone = RingtoneManager.getRingtone(context, uri);
//					String name = ringtone.getTitle(context);
					remindSound = uri.toString();
					soundBtn.setText(RingtoneManager.getRingtone(context, uri).getTitle(context));
				}
			}
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