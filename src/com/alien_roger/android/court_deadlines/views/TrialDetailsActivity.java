package com.alien_roger.android.court_deadlines.views;

import actionbarcompat.ActionBarActivity;
import android.content.Intent;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.alien_roger.android.court_deadlines.R;
import com.alien_roger.android.court_deadlines.statics.StaticData;
import com.alien_roger.android.court_deadlines.xml_parsers.HtmlHelper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;

import java.io.IOException;
import java.io.InputStream;

/**
 * TrialDetailsActivity class
 * 
 * @author alien_roger
 * @created at: 06.01.12 16:30
 */
public class TrialDetailsActivity extends ActionBarActivity {
	private final String TAG = "SelectTrialActivity";
	private ProgressBar progressBar;
	private String description;
	private String baseUrl = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setResult(RESULT_CANCELED);
		setContentView(R.layout.trial_details_screen);
		progressBar = (ProgressBar) findViewById(R.id.progressBar);
		baseUrl = getIntent().getExtras().getString(StaticData.URL_PATH);
		new Get().execute(baseUrl);
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
		case R.id.menu_done:
            Intent intent = new Intent();
            intent.putExtra(StaticData.TRIAL_DATA,description);
            setResult(RESULT_OK,intent);

			// save trial detail
//                CourtCase courtCase = new CourtCase();
//                courtCase.setCaseName(caseNameEdt.getText().toString().trim());
//                courtCase.setCustomer(caseNameEdt.getText().toString().trim());
//                courtCase.setCourtDate(fromCalendar);
//                courtCase.setProposalDate(fromCalendar);
//                courtCase.setNotes(notesEdt.getText().toString().trim());
//                courtCase.setCourtType("indictment");
//
//                // create task in DB
//                getContentResolver().insert(DBConstants.TASKS_CONTENT_URI, DBDataManager.fillCourtCase(courtCase));
			finish();
			break;

		case R.id.menu_cancel:
			Toast.makeText(this, "Canceled", Toast.LENGTH_SHORT).show();
			finish();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	private class Get extends AsyncTask<String, Void, Boolean> {
		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
			getActionBarHelper().setRefreshActionItemState(true);
		}

		@Override
		protected Boolean doInBackground(String... ids) {
			boolean conn = false;
			try {
				conn = parseDataFromHtml(ids[0]);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return conn;
		}

		@Override
		protected void onPostExecute(Boolean connected) {
			super.onPostExecute(connected);
			progressBar.setVisibility(View.INVISIBLE);
			getActionBarHelper().setRefreshActionItemState(false);

			if (connected) {
				((WebView) findViewById(R.id.data)).loadDataWithBaseURL(baseUrl, description, "text/html", "UTF-8",
						null);
			}
		}
	}

	public Boolean parseDataFromHtml(String urlString) throws Exception {
		final HttpClient client = AndroidHttpClient
				.newInstance("Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/535.7 (KHTML, like Gecko) Chrome/16.0.912.63 Safari/535.7");
		Log.d(TAG, "retreiving from url = " + urlString);
		boolean connectedFlag = false;
		final HttpGet httpost = new HttpGet(urlString);
		try {
			HttpResponse response = client.execute(httpost);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.e("TAG", "Error " + statusCode + " while retrieving dat from " + urlString);
				return false;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					HtmlHelper helper = new HtmlHelper(inputStream);

					description = helper.getDescription();
					connectedFlag = true;
				} catch (Exception e) {
					httpost.abort();
					Log.d(TAG, e.toString());
					e.printStackTrace();

				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (IOException e) {
			httpost.abort();
			Log.d(TAG, e.toString());
		} catch (IllegalStateException e) {
			httpost.abort();
			Log.d(TAG, e.toString());
		} catch (Exception e) {
			httpost.abort();
			Log.d(TAG, e.toString());
		} finally {
			if ((client instanceof AndroidHttpClient)) {
				((AndroidHttpClient) client).close();
			}
		}
		return connectedFlag;
	}

}