package com.alien_roger.android.court_deadlines.interfaces;

import java.util.List;

import android.content.Context;
import android.database.Cursor;

public interface DataLoadInterface<T> {
	void showProgress(boolean show);
	void onDataReady(List<T> cases);
	void onTaskLoaded(Cursor cursor);
    void onError();
	Context getMeContext();
}
