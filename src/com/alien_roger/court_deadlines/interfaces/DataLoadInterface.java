package com.alien_roger.court_deadlines.interfaces;

import java.util.List;

import android.content.Context;
import android.database.Cursor;

public interface DataLoadInterface<T> {
	void showProgress(boolean show);
	void onDataReady(List<T> cases);
	void onDataLoaded(Cursor cursor);
    void onError();
	Context getMeContext();
}
