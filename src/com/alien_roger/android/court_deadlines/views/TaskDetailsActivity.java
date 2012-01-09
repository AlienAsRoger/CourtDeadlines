package com.alien_roger.android.court_deadlines.views;

import actionbarcompat.ActionBarActivity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.db.DBConstants;
import com.alien_roger.android.court_deadlines.db.DBDataManager;
import com.alien_roger.android.court_deadlines.entities.CourtCase;
import com.alien_roger.android.court_deadlines.statics.StaticData;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * TaskDetailsActivity class
 *
 * @author alien_roger
 * @created at: 24.12.11 13:03
 */
public class TaskDetailsActivity extends ActionBarActivity implements AdapterView.OnItemSelectedListener {

    private static final int SET_FROM_TIME 	= 1;
    private static final int SET_FROM_DATE 	= 2;
    private Calendar fromCalendar;
    private DatePickerDialog fromDatePickerDialog;
    private EditText customerEdt;
    private EditText caseNameEdt;
    private EditText courtDateEdt;
    private EditText proposalDateEdt;
    private EditText courtTypeEdt;
    private EditText notesEdt;
    private Button showCourtTypesBtn;
    private Spinner trialSpinner;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_screen);

        widgetsInit();

        fromCalendar = Calendar.getInstance();
        fromDatePickerDialog = new DatePickerDialog(this, fromDateSetListener,
                fromCalendar.get(Calendar.YEAR),fromCalendar.get(Calendar.MONTH),fromCalendar.get(Calendar.DAY_OF_MONTH));
    }

    private void widgetsInit(){
        customerEdt = (EditText) findViewById(R.id.customerEdt);
        caseNameEdt = (EditText) findViewById(R.id.caseNameEdt);
        proposalDateEdt = (EditText) findViewById(R.id.proposalDateEdt);
        notesEdt = (EditText) findViewById(R.id.notesEdt);

        courtDateEdt = (EditText) findViewById(R.id.courtDateEdt);
        courtDateEdt.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                showDialog(SET_FROM_DATE);
                return true;
            }
        });

        trialSpinner = (Spinner) findViewById(R.id.selectTrial);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.trial_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        trialSpinner.setAdapter(adapter);
        trialSpinner.setOnItemSelectedListener(this);
//        showCourtTypesBtn = (Button) findViewById(R.id.selectTrial);
//        showCourtTypesBtn.setOnClickListener(this);
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

            case R.id.menu_done:
                // fill task params
                CourtCase courtCase = new CourtCase();
                courtCase.setCaseName(caseNameEdt.getText().toString().trim());
                courtCase.setCustomer(caseNameEdt.getText().toString().trim());
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

            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            courtDateEdt.setText(df.format(fromCalendar.getTime()));
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
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if(i > 0){
            Intent intent = new Intent(this,SelectTrialActivity.class);
            intent.putExtra(StaticData.URL_PATH, StaticData.DEFAULT_URL + StaticData.PART_1 + i);
            startActivityForResult(intent,StaticData.GET_TRIAL_DATA);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}