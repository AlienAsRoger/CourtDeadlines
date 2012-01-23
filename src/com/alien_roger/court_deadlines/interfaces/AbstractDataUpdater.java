package com.alien_roger.court_deadlines.interfaces;

import java.util.List;

import actionbarcompat.ActionBarHelper;
import android.content.Context;

public abstract class AbstractDataUpdater implements DataLoadInterface<Object>{

	private ActionBarHelper actionBarHelper;
	private Context context;
	public AbstractDataUpdater(Context context,ActionBarHelper actionBarHelper){
		this.actionBarHelper = actionBarHelper;
		this.context = context;
	}


	@Override
	public void showProgress(boolean show) {
		actionBarHelper.setRefreshActionItemState(show);
	}

	@Override
	public void onDataReady(List<Object> cases) {}


	@Override
	public void onError() {}

	@Override
	public Context getMeContext() {
		return context;
	}

}
