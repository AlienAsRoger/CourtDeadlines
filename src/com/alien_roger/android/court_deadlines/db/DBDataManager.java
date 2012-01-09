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

    private static String getString(Cursor cursor,String column){
        return cursor.getString(cursor.getColumnIndex(column));
    }
}
