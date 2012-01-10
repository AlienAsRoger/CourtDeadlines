//
//  Apphuset AS
//  Copyright 2011 Oslo Lufthavn AS
//  All Rights Reserved.
//
//  Author: Yuri V. Suhanov - Azoft
//
package com.alien_roger.android.court_deadlines.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.alien_roger.android.court_deadlines.entities.CourtCase;
import com.alien_roger.android.court_deadlines.entities.CourtObj;


public class DBDataManager {

	private final static String TAG = DBDataManager.class.getSimpleName();

	private volatile static DBDataManager instance;
    public static DBDataManager getInstance() {
        if (instance == null) {
            synchronized (DBDataManager.class) {
                if (instance == null) {
                    instance = new DBDataManager();
                }
            }
        }
        return instance;
    }

	private DBDataManager(){
	}

	public static ContentValues fillCourtCase(CourtCase courtCase){
		ContentValues values = new ContentValues();
        values.put(DBConstants.CUSTOMER, courtCase.getCustomer());
        values.put(DBConstants.CASE_NAME, courtCase.getCaseName());
        values.put(DBConstants.COURT_DATE, courtCase.getCourtDateLong());
        values.put(DBConstants.PROPOSAL_DATE, courtCase.getProposalDateLong());
        values.put(DBConstants.NOTES, courtCase.getNotes());
        values.put(DBConstants.COURT_TYPE, courtCase.getCourtType());
		return values;
	}

	public static void fillCourtCaseObjValues(CourtCase courtCase,Cursor cursor ){
        courtCase.setCaseName(getString(cursor,DBConstants.CASE_NAME));
        courtCase.setCustomer(getString(cursor,DBConstants.CUSTOMER));
        courtCase.setCourtDate(cursor.getLong(cursor.getColumnIndex(DBConstants.COURT_DATE)));
        courtCase.setProposalDate(cursor.getLong(cursor.getColumnIndex(DBConstants.PROPOSAL_DATE)));
        courtCase.setNotes(getString(cursor,DBConstants.NOTES));
        courtCase.setCourtType(getString(cursor,DBConstants.COURT_TYPE));
	}

	public static ContentValues fillCourtObj(CourtObj dataObj){
		ContentValues values = new ContentValues();
        values.put(DBConstants.TRIAL_HAVE_CHILDS, 0); // TODO
        values.put(DBConstants.TRIAL_LEVEL, dataObj.getLevel());
        values.put(DBConstants.TRIAL_PARENT, dataObj.getParent());
        values.put(DBConstants.TRIAL_VALUE, dataObj.getValue());
		return values;
	}

	public static void fillCourtObjObjValues(CourtObj dataObj,Cursor cursor ){
        dataObj.setLevel(getInt(cursor,DBConstants.TRIAL_LEVEL));
        dataObj.setParent(getInt(cursor,DBConstants.TRIAL_PARENT));
        dataObj.setValue(getString(cursor,DBConstants.TRIAL_VALUE));

        dataObj.setHaveChilds(false); // TODO
	}

    private static String getString(Cursor cursor,String column){
        return cursor.getString(cursor.getColumnIndex(column));
    }

    private static int getInt(Cursor cursor,String column){
        return cursor.getInt(cursor.getColumnIndex(column));
    }
}
