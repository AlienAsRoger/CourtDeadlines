//
//  Apphuset AS
//  Copyright 2011 Oslo Lufthavn AS
//  All Rights Reserved.
//
//  Author: Yuri V. Suhanov - Azoft
//
package com.alien_roger.court_deadlines.db;

import android.content.ContentValues;
import android.database.Cursor;
import com.alien_roger.court_deadlines.entities.CourtCase;
import com.alien_roger.court_deadlines.entities.CourtObj;

import java.util.Calendar;


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



//   private static final String[] PROJECTION_ID_IID = new String[] {
//           BooksStore.Book._ID, BooksStore.Book.INTERNAL_ID
//   };
//   private static final String[] PROJECTION_ID = new String[] { BooksStore.Book._ID };
//
//   static {
//       StringBuilder selection = new StringBuilder();
//       selection.append(BooksStore.Book.INTERNAL_ID);
//       selection.append("=?");
//       sBookIdSelection = selection.toString();
//
//       selection = new StringBuilder();
//       selection.append(sBookIdSelection);
//       selection.append(" OR ");
//       selection.append(BooksStore.Book.EAN);
//       selection.append("=? OR ");
//       selection.append(BooksStore.Book.ISBN);
//       selection.append("=?");
//       sBookSelection = selection.toString();
//   }

    public static String parentLevelSelection;

//    private static String[] sArguments3 = new String[3];

    public static final String[] PROJECTION_PARENT_LEVEL = new String[] {
    	DBConstants.TRIAL_PARENT_LEVEL
	};

    static {
    	StringBuilder selection = new StringBuilder();
        selection.append(DBConstants.TRIAL_PARENT_LEVEL);
//        selection.append("=? AND ");
//        selection.append(DBConstants.SCHEDULE_DATE_TIME);
//        selection.append("=? AND ");
//        selection.append(DBConstants.ARR_DEP);
        selection.append("=?");

        parentLevelSelection = selection.toString();
    }

	public static ContentValues fillCourtCase2ContentValues(CourtCase courtCase){
		ContentValues values = new ContentValues();
        values.put(DBConstants.CUSTOMER, courtCase.getCustomer());
        values.put(DBConstants.CASE_NAME, courtCase.getCaseName());
        values.put(DBConstants.COURT_DATE, courtCase.getCourtDateLong());
        values.put(DBConstants.PROPOSAL_DATE, courtCase.getProposalDateLong());
        values.put(DBConstants.NOTES, courtCase.getNotes());
        values.put(DBConstants.COURT_TYPE, courtCase.getCourtType());

		values.put(DBConstants.REMIND_SOUND, courtCase.getReminderSound());
		values.put(DBConstants.REMIND_TIME, courtCase.getReminderTimePosition());

		return values;
	}

	public static void fillCourtCase2Object(CourtCase courtCase,Cursor cursor ){
        courtCase.setCaseName(getString(cursor,DBConstants.CASE_NAME));
        courtCase.setCustomer(getString(cursor,DBConstants.CUSTOMER));

        courtCase.setCourtDate(Calendar.getInstance());
        courtCase.setProposalDate(Calendar.getInstance());

        courtCase.setCourtDate(cursor.getLong(cursor.getColumnIndex(DBConstants.COURT_DATE)));
        courtCase.setProposalDate(cursor.getLong(cursor.getColumnIndex(DBConstants.PROPOSAL_DATE)));

        courtCase.setNotes(getString(cursor,DBConstants.NOTES));
        courtCase.setCourtType(getString(cursor,DBConstants.COURT_TYPE));

		courtCase.setReminderSound(getString(cursor,DBConstants.REMIND_SOUND));
		courtCase.setReminderTimePosition(getInt(cursor,DBConstants.REMIND_TIME));
	}

	public static ContentValues fillCourtObj2ContentValues(CourtObj dataObj){
		ContentValues values = new ContentValues();
        values.put(DBConstants.TRIAL_HAVE_CHILD, dataObj.doesHaveChild()); // TODO
        values.put(DBConstants.TRIAL_DEPTH_LEVEL, dataObj.getDepthLevel());
        values.put(DBConstants.TRIAL_PARENT_LEVEL, dataObj.getParentLevel());
        values.put(DBConstants.TRIAL_CURRENT_LEVEL, dataObj.getCurrentLevel());
        values.put(DBConstants.TRIAL_VALUE, dataObj.getValue());
		return values;
	}

	public static void fillCourtObj2Object(CourtObj dataObj,Cursor cursor ){
        dataObj.setDepthLevel(getInt(cursor,DBConstants.TRIAL_DEPTH_LEVEL));
        dataObj.setParentLevel(getInt(cursor,DBConstants.TRIAL_PARENT_LEVEL));
        dataObj.setCurrentLevel(getInt(cursor,DBConstants.TRIAL_CURRENT_LEVEL));
        dataObj.setValue(getString(cursor,DBConstants.TRIAL_VALUE));

        dataObj.setHaveChild(getInt(cursor,DBConstants.TRIAL_HAVE_CHILD)); // TODO
	}

    private static String getString(Cursor cursor,String column){
        return cursor.getString(cursor.getColumnIndex(column));
    }

    private static int getInt(Cursor cursor,String column){
        return cursor.getInt(cursor.getColumnIndex(column));
    }
}
