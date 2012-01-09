package com.alien_roger.android.court_deadlines.interfaces;

import android.content.Context;
import android.database.Cursor;
import com.alien_roger.android.court_deadlines.entities.CourtCase;

import java.util.List;


public interface TaskLoadInterface {
	void showProgress(boolean show);
	void onDataReady(List<CourtCase> cases);
	void onTaskLoaded(Cursor cursor);
    void onError();
	Context getMeContext();
}
