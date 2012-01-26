package com.alien_roger.court_deadlines.ui;

import android.content.Intent;
import android.net.Uri;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.alien_roger.court_deadlines.R;
import com.alien_roger.court_deadlines.db.DBConstants;
import com.alien_roger.court_deadlines.db.DBDataManager;
import com.alien_roger.court_deadlines.entities.CourtCase;
import com.alien_roger.court_deadlines.statics.StaticData;




/**
 * TaskDetailsActivity class
 *
 * @author alien_roger
 * @created at: 24.12.11 13:03
 */
public class TaskDetailsViewActivity extends TaskDetailsActivity {


//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.details_screen);
//
//		setTitle(R.string.customer_hint);
//		widgetsInit();
//
//		context = this;
//
//		fromCalendar = Calendar.getInstance();
//		toCalendar = Calendar.getInstance();
//		df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
//
//		courtDateEdt.setText(df.format(fromCalendar.getTime()));
//
//		fromDatePickerDialog = new DatePickerDialog(this, fromDateSetListener, fromCalendar.get(Calendar.YEAR),
//				fromCalendar.get(Calendar.MONTH), fromCalendar.get(Calendar.DAY_OF_MONTH));
//		toDatePickerDialog = new DatePickerDialog(this, toDateSetListener, toCalendar.get(Calendar.YEAR),
//				toCalendar.get(Calendar.MONTH), toCalendar.get(Calendar.DAY_OF_MONTH));
//		fromTimePickerDialog = new TimePickerDialog(this, fromTimeSetListener, fromCalendar.get(Calendar.HOUR_OF_DAY),
//				fromCalendar.get(Calendar.MINUTE), true);
//		toTimePickerDialog = new TimePickerDialog(this, toTimeSetListener, toCalendar.get(Calendar.HOUR_OF_DAY),
//				toCalendar.get(Calendar.MINUTE), true);
//
//		// updateSpinnerListener = new UpdateSpinner();
//		spinnerSelectedListener = new SpinnerSelectedListener();
//		init();
//	}

	private CourtCase courtCase;

	@Override
	protected void onResume() {
		super.onResume();
		courtCase = (CourtCase) getIntent().getExtras().getSerializable(StaticData.COURT_CASE);

		// useless
		courtCase.setCaseName("");
		courtCase.setCourtType("indictment");

		customerEdt.setText(courtCase.getCustomer());

		fromCalendar = courtCase.getCourtDate();
		toCalendar = courtCase.getProposalDate();

		notesEdt.setText(courtCase.getNotes());

		proposalDateEdt.setText(df.format(toCalendar.getTime()));
		courtDateEdt.setText(df.format(fromCalendar.getTime()));

		proposalTimeEdt.setText(timeFormat.format(toCalendar.getTime()));
		courtTimeEdt.setText(timeFormat.format(fromCalendar.getTime()));

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.edit_details, menu);

		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(this, "Tapped home", Toast.LENGTH_SHORT).show();
			break;

		case R.id.menu_refresh:

			// fill task params
			courtCase.setCaseName("");
			courtCase.setCustomer(customerEdt.getText().toString().trim());
			courtCase.setCourtDate(fromCalendar);
			courtCase.setProposalDate(toCalendar);
			courtCase.setNotes(notesEdt.getText().toString().trim());
			courtCase.setCourtType("indictment");

			// create task in DB
			int cnt = getContentResolver().update(Uri.parse(DBConstants.TASKS_CONTENT_URI.toString() + "/"+courtCase.getId() ),
					DBDataManager.fillCourtCase2ContentValues(courtCase), null, null);
			setReminderTime(toCalendar.getTimeInMillis(), courtCase.getCustomer(), (int) courtCase.getId());

			finish();
			break;

		case R.id.menu_delete:
			getContentResolver().delete(Uri.parse(DBConstants.TASKS_CONTENT_URI.toString() + "/"+courtCase.getId() ), null, null);
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