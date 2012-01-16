package com.alien_roger.android.court_deadlines.views;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import actionbarcompat.ActionBarActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.Toast;

import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.adapters.CustomSpinnerAdapter;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.db.DBDataManager;
import com.alien_roger.android.court_deadlines.entities.CourtCase;
import com.alien_roger.android.court_deadlines.interfaces.AbstractDataUpdater;
import com.alien_roger.android.court_deadlines.interfaces.DataLoadInterface;
import com.alien_roger.android.court_deadlines.statics.StaticData;
import com.alien_roger.android.court_deadlines.tasks.GetTrialsTask;
import com.alien_roger.android.court_deadlines.tasks.LoadTrials;

/**
 * TaskDetailsActivity class
 *
 * @author alien_roger
 * @created at: 24.12.11 13:03
 */
public class TaskDetailsActivity extends ActionBarActivity implements DataLoadInterface<Object> {

    private static final int SET_FROM_DATE2 	= 1;
    private static final int SET_FROM_DATE 	= 2;
    private Calendar fromCalendar;
    private Calendar toCalendar;
    private DateFormat df;

    private DatePickerDialog fromDatePickerDialog;
    private DatePickerDialog fromDatePickerDialog2;
    private EditText customerEdt;

    private EditText courtDateEdt;
    private EditText proposalDateEdt;
    private EditText notesEdt;

    private Spinner typeSpinner1;
    private Spinner typeSpinner2;
    private Spinner trialSpinner1;
    private Spinner trialSpinner2;
    private LinearLayout additionSpinnersView;

    private Context context;


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

        fromDatePickerDialog = new DatePickerDialog(this, fromDateSetListener,
                fromCalendar.get(Calendar.YEAR),fromCalendar.get(Calendar.MONTH),fromCalendar.get(Calendar.DAY_OF_MONTH));
        fromDatePickerDialog = new DatePickerDialog(this, fromDateSetListener2,
        		toCalendar.get(Calendar.YEAR),toCalendar.get(Calendar.MONTH),toCalendar.get(Calendar.DAY_OF_MONTH));

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean dataSaved = preferences.getBoolean(StaticData.SHP_DATA_SAVED, false);


        if(!dataSaved){
        	trialSpinner1.setEnabled(false);
        	trialSpinner2.setEnabled(false);
        	typeSpinner1.setEnabled(false);
        	typeSpinner2.setEnabled(false);
			new GetTrialsTask(this).execute("d2.txt");
        }else{
        	// set adapters
        	new LoadTrials(new DataUpdater(typeSpinner1)).execute(13);
        }
    }

    private class DataUpdater extends AbstractDataUpdater{
    	private Spinner spinner;
		public DataUpdater(Spinner spinner) {
			super(TaskDetailsActivity.this, getActionBarHelper());
			this.spinner = spinner;
		}

		@Override
		public void onTaskLoaded(Cursor cursor) {

        	adjustSpinner(spinner, new CustomSpinnerAdapter(context, cursor));
		}
    }

    private class DataUpdater2 extends AbstractDataUpdater{

		public DataUpdater2() {
			super(TaskDetailsActivity.this, getActionBarHelper());
		}

		@Override
		public void onTaskLoaded(Cursor cursor) {
			if(cursor.getCount() > 0){
				cursor.moveToFirst();
				String string = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));

				Log.d("onTaskLoaded","String "  +string);
				notesEdt.setText(string);
			}
		}
    }


	@Override
	protected void onResume() {
		super.onResume();
		registerReceiver(dataUpdateReceiver,new IntentFilter(StaticData.BROADCAST_ACTION));
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
			Log.d("BC","BC received data = " + code);

			if(code == RESULT_OK){
	        	// set adapters
	        	new LoadTrials(new DataUpdater(typeSpinner1)).execute(13);

			}else if(code == RESULT_CANCELED){
				new GetTrialsTask(TaskDetailsActivity.this).execute("d2.txt");
	        }

		}
	};

    private class SpinnerSeletedListener implements AdapterView.OnItemSelectedListener{
    	private Spinner spinner;

    	public SpinnerSeletedListener(Spinner spinner){
    		this.spinner = spinner;
    	}

		@Override
		public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
	    	Cursor cursor = (Cursor) adapterView.getItemAtPosition(i);
//	        showToast(cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE)));
//	    	String title = cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE));
	    	int currLevel = cursor.getInt(cursor.getColumnIndex(DBConstants.TRIAL_CURRENT_LEVEL));
	    	if(spinner.equals(typeSpinner1)){
	    		new LoadTrials(new DataUpdater(typeSpinner2)).execute(currLevel);
	    	}else if(spinner.equals(typeSpinner2)){
	    		new LoadTrials(new DataUpdater(trialSpinner1)).execute(currLevel);
	    	}else if(spinner.equals(trialSpinner1)){
	    		new LoadTrials(new DataUpdater(trialSpinner2)).execute(currLevel);
	    	}else if(spinner.equals(trialSpinner2)){
	    		new LoadTrials(new DataUpdater2()).execute(currLevel);
//	    		notesEdt.setText(cursor.getString(cursor.getColumnIndex(DBConstants.TRIAL_VALUE)));
	    	}


//	        if(i > 0){
//	            Intent intent = new Intent(this,SelectTrialActivity.class);
//	            intent.putExtra(StaticData.URL_PATH, StaticData.DEFAULT_URL + StaticData.PART_1 + i);
//	            startActivityForResult(intent,StaticData.GET_TRIAL_DATA);
//	        }
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {}

    }



    private void widgetsInit(){
        customerEdt = (EditText) findViewById(R.id.customerEdt);
        notesEdt = (EditText) findViewById(R.id.notesEdt);


        courtDateEdt = (EditText) findViewById(R.id.courtDateEdt);
        courtDateEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialog(SET_FROM_DATE);
                return true;
            }
        });

        proposalDateEdt = (EditText) findViewById(R.id.deadlineDateEdt);
        proposalDateEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialog(SET_FROM_DATE2);
                return true;
            }
        });

        trialSpinner1 = (Spinner) findViewById(R.id.selectTrial1);
        trialSpinner2 = (Spinner) findViewById(R.id.selectTrial2);
        typeSpinner1 = (Spinner) findViewById(R.id.selectType1);
        typeSpinner2 = (Spinner) findViewById(R.id.selectType2);

        additionSpinnersView = (LinearLayout) findViewById(R.id.additionSpinnersView);

//        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
//                this, R.array.trial_types, android.R.layout.simple_spinner_item);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        adjustSpinner(trialSpinner2, adapter);
//        adjustSpinner(typeSpinner1, adapter);
//        adjustSpinner(typeSpinner2, adapter);

//    	loadingLayout = getLayoutInflater().inflate(R.layout.loading_layout, null);
//		addContentView(loadingLayout,
//				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,
//				LinearLayout.LayoutParams.WRAP_CONTENT ));
//        Animation animation = AnimationUtils.loadAnimation(this, R.anim.shrink_to_middle);
//        loadingLayout.setAnimation(animation);

//        showCourtTypesBtn = (Button) findViewById(R.id.selectTrial);
//        showCourtTypesBtn.setOnClickListener(this);
    }

    private void adjustSpinner(Spinner spinner,SpinnerAdapter adapter){
    	spinner.setOnItemSelectedListener(new SpinnerSeletedListener(spinner));
    	spinner.setAdapter(adapter);
    }

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
                getContentResolver().insert(DBConstants.TASKS_CONTENT_URI, DBDataManager.fillCourtCase(courtCase));
                finish();
//                Toast.makeText(this, "Fake refreshing...", Toast.LENGTH_SHORT).show();
//                getActionBarHelper().setRefreshActionItemState(true);
//                getWindow().getDecorView().postDelayed(
//                        new Runnable() {
//                            @Override
//                            public void run() {
//                                getActionBarHelper().setRefreshActionItemState(false);
//                            }
//                        }, 1000);
                break;

            case R.id.menu_cancel:
                Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id){
        case SET_FROM_DATE:
            return fromDatePickerDialog;
        case SET_FROM_DATE2:
            return fromDatePickerDialog2;
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener fromDateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
            fromCalendar.set(Calendar.YEAR, year);
            fromCalendar.set(Calendar.MONTH, monthOfYear);
            fromCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Calendar currentDay = Calendar.getInstance();

            // drop down other values
            fromCalendar.set(Calendar.HOUR_OF_DAY, 0);
            fromCalendar.set(Calendar.MINUTE, 0);
            fromCalendar.set(Calendar.SECOND, 0);
            fromCalendar.set(Calendar.MILLISECOND,0);
            currentDay.set(Calendar.HOUR_OF_DAY, 0);
            currentDay.set(Calendar.MINUTE, 0);
            currentDay.set(Calendar.SECOND, 0);
            currentDay.set(Calendar.MILLISECOND,0);
            if (fromCalendar.before(currentDay)){
                showToast(R.string.unable_to_set_past_date);
                return;
            }

//            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            courtDateEdt.setText(df.format(fromCalendar.getTime()));
        }
    };

    private DatePickerDialog.OnDateSetListener fromDateSetListener2 = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year,int monthOfYear, int dayOfMonth) {
        	toCalendar.set(Calendar.YEAR, year);
        	toCalendar.set(Calendar.MONTH, monthOfYear);
        	toCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            Calendar currentDay = Calendar.getInstance();

            // drop down other values
            toCalendar.set(Calendar.HOUR_OF_DAY, 0);
            toCalendar.set(Calendar.MINUTE, 0);
            toCalendar.set(Calendar.SECOND, 0);
            toCalendar.set(Calendar.MILLISECOND,0);
            currentDay.set(Calendar.HOUR_OF_DAY, 0);
            currentDay.set(Calendar.MINUTE, 0);
            currentDay.set(Calendar.SECOND, 0);
            currentDay.set(Calendar.MILLISECOND,0);
            if (toCalendar.before(currentDay)){
                showToast(R.string.unable_to_set_past_date);
                return;
            }


            proposalDateEdt.setText(df.format(toCalendar.getTime()));
        }
    };

    private void showToast(String msg){
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    private void showToast(int id){
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    // TODO: reuse!
//    private void setReminderTime(long triggerAtTime,String taskTitle,int id){
//        long msBefore = 1000;
//        long time2Set = triggerAtTime - msBefore;
//        Intent statusUpdate = new Intent(context, AlarmReceiver.class);
//        statusUpdate.putExtra(StaticData.TASK_TITLE, taskTitle);
//        statusUpdate.putExtra(StaticData.REQUEST_CODE, id);
//        Log.d("setReminderTime", "id = " + id);
//        Log.d("setReminderTime","triggerAtTime = " + triggerAtTime);
//        Log.d("setReminderTime","time2Set = " + time2Set);
//
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, id, statusUpdate, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        // schedule the service for updating
//        AlarmManager alarms = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        alarms.setRepeating(AlarmManager.RTC_WAKEUP, time2Set, StaticData.REMIND_ALARM_INTERVAL, pendingIntent);
//    }




	@Override
	public void showProgress(boolean show) {
		getActionBarHelper().setRefreshActionItemState(show);
	}

	@Override
	public void onDataReady(List<Object> cases) {

    	trialSpinner1.setEnabled(true);
    	trialSpinner2.setEnabled(true);
    	typeSpinner1.setEnabled(true);
    	typeSpinner2.setEnabled(true);

		new LoadTrials(new DataUpdater(typeSpinner1)).execute(13);
	}

	@Override
	public void onTaskLoaded(Cursor cursor) {
        adjustSpinner(trialSpinner1, new CustomSpinnerAdapter(this, cursor));
	}

	@Override
	public void onError() {

	}

	@Override
	public Context getMeContext() {
		return this;
	}
}