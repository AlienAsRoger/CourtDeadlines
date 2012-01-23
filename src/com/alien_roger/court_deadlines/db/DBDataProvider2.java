package com.alien_roger.court_deadlines.db;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class DBDataProvider2 extends ContentProvider{
    private static final UriMatcher uriMatcher;
    private static final UriMatcher uriMatcherIds;

    static{
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcherIds = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_CASE_TABLE, 		DBConstants.COURT_CASES);
		uriMatcher.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_TRIALS_TABLE, 		DBConstants.COURT_TRIALS);

		uriMatcherIds.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_CASE_TABLE + "/#", DBConstants.COURT_CASES);
		uriMatcherIds.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_TRIALS_TABLE + "/#", DBConstants.COURT_TRIALS);
//		uriMatcherIds.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_CASE_TABLE + "/#", DBConstants.COURT_CASE_ID);
//		uriMatcherIds.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_TRIALS_TABLE + "/#", DBConstants.COURT_TRIAL_ID);
    }

    private DatabaseHelper dbHelper;

    @Override
	public boolean onCreate() {
 		Context context = getContext();
		dbHelper = new DatabaseHelper(context);
		CourtDeadlinesDB = dbHelper.getWritableDatabase();

	    return (CourtDeadlinesDB == null)? false:true;
	}


    private static Uri[] uriArray = new Uri[]{
    		DBConstants.TASKS_CONTENT_URI,
    		DBConstants.TRIALS_CONTENT_URI
    };

    private static int[] pathsArray = new int[]{
    		DBConstants.COURT_CASES,
    		DBConstants.COURT_TRIALS
    };

//    private static int[] pathsIdsArray = new int[]{
//    		DBConstants.COURT_CASE_ID,
//    		DBConstants.COURT_TRIAL_ID
//    };

    private static String[] tablesArray = new String[]{
    		DBConstants.COURT_CASE_TABLE,
    		DBConstants.COURT_TRIALS_TABLE
    };

    private static String[] createTablesArray = new String[]{
    		DBConstants.TASKS_TABLE_CREATE,
    		DBConstants.TRIALS_TABLE_CREATE
    };

	@Override
	public String getType(Uri uri){
		for (int element : pathsArray) {
			if(uriMatcher.match(uri) == element){
				return "vnd.android.cursor.dir/vnd.alien_roger.courtdeadlines";
			}else if(uriMatcherIds.match(uri) == element){
				return "vnd.android.cursor.item/vnd.alien_roger.courtdeadlines";
			}
		}
		throw new IllegalArgumentException("Unsupported URI: " + uri);

//		switch (uriMatcher.match(uri)){
//		case DBConstants.COURT_CASES:
//		case DBConstants.COURT_TRIALS:
//			return "vnd.android.cursor.dir/vnd.alien_roger.courtdeadlines";
//        case DBConstants.COURT_CASE_ID:
//        case DBConstants.COURT_TRIAL_ID:
//           return "vnd.android.cursor.item/vnd.alien_roger.courtdeadlines";
//		default:
//		    throw new IllegalArgumentException("Unsupported URI: " + uri);
//		}
	}


	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();

		boolean found = false;
		for(int i=0; i < pathsArray.length;  i++){
			if(uriMatcher.match(uri) == pathsArray[i]){
			    sqlBuilder.setTables(tablesArray[i]);
			    found = true;
			}else if(uriMatcherIds.match(uri) == pathsArray[i]){
			    sqlBuilder.setTables(tablesArray[i]);
				sqlBuilder.appendWhere(DBConstants._ID + " = " + uri.getPathSegments().get(1));
			    found = true;
			}

			if(found){
				Cursor c = sqlBuilder.query(CourtDeadlinesDB, projection, selection, selectionArgs, null, null, sortOrder);
		        c.setNotificationUri(getContext().getContentResolver(), uri);
		        return c;
			}
		}
		throw new IllegalArgumentException("Unsupported URI: " + uri);
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs){
		int count = 0;

		boolean found = false;
		for(int i=0; i < pathsArray.length;  i++){
			if(uriMatcher.match(uri) == pathsArray[i]){
			    count = CourtDeadlinesDB.update(tablesArray[i],values,selection,selectionArgs);
			    found = true;
			}else if(uriMatcherIds.match(uri) == pathsArray[i]){
		         count = CourtDeadlinesDB.update(tablesArray[i], values,
		        		 DBConstants._ID + " = " + uri.getPathSegments().get(1) +
		        		 (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
		        		 selectionArgs);
			    found = true;
			}

			if(found){
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
		}
		throw new IllegalArgumentException("Unknown URI " + uri);
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {

		for(int i=0; i < pathsArray.length;  i++){
			if(uriMatcher.match(uri) == pathsArray[i] || uriMatcherIds.match(uri) == pathsArray[i]){
				long rowID = CourtDeadlinesDB.insert(tablesArray[i], "", values);
			    //---if added successfully---
			    if (rowID > 0){
			         Uri _uri = ContentUris.withAppendedId(uriArray[i], rowID);
			         getContext().getContentResolver().notifyChange(_uri, null);
			         return _uri;
			    }
			}
		}
		throw new IllegalArgumentException("Unsupported URI: " + uri);

//		switch (uriMatcher.match(uri)){
//		case DBConstants.COURT_CASES:
//		case DBConstants.COURT_CASE_ID:
//			long rowID = CourtDeadlines_DB.insert(DBConstants.COURT_CASE_TABLE, "", values);
//		    //---if added successfully---
//		    if (rowID > 0){
//		         Uri _uri = ContentUris.withAppendedId(DBConstants.TASKS_CONTENT_URI, rowID);
//		         getContext().getContentResolver().notifyChange(_uri, null);
//		         return _uri;
//		    }
//		break;
//		default:throw new IllegalArgumentException("Unsupported URI: " + uri);
//		}
//	    throw new SQLException("Failed to insert row into " + uri);
	}

	@Override
	public int delete(Uri uri, String arg1, String[] arg2){
	      // arg0 = uri  arg1 = selection  arg2 = selectionArgs
		int count=0;
		boolean found = false;
		for(int i=0; i < pathsArray.length;  i++){
			if(uriMatcher.match(uri) == pathsArray[i]){
      		 	count = CourtDeadlinesDB.delete(tablesArray[i],arg1,arg2);
			    found = true;
			}else if(uriMatcherIds.match(uri) == pathsArray[i]){
				String id = uri.getPathSegments().get(1);
				count = CourtDeadlinesDB.delete(
						tablesArray[i],
						DBConstants._ID + " = " + id +(!TextUtils.isEmpty(arg1) ? " AND (" +arg1 + ')' : ""),
						arg2);
			    found = true;
			}

			if(found){
				getContext().getContentResolver().notifyChange(uri, null);
				return count;
			}
		}
		throw new IllegalArgumentException("Unknown URI " + uri);




//	      switch (uriMatcher.match(uri)){
//	      	 case DBConstants.COURT_CASES:{
//      		 	count = CourtDeadlines_DB.delete(DBConstants.COURT_CASE_TABLE,arg1,arg2);
//	      	 }break;
//	      	 case DBConstants.COURT_CASE_ID:
//				String id = uri.getPathSegments().get(1);
//				count = CourtDeadlines_DB.delete(
//						DBConstants.COURT_CASE_TABLE,
//						DBConstants._ID + " = " + id +(!TextUtils.isEmpty(arg1) ? " AND (" +arg1 + ')' : ""),
//						arg2);
//				break;
//	         default: throw new IllegalArgumentException("Unknown URI " + uri);
//	      }
//	      getContext().getContentResolver().notifyChange(uri, null);
//	      return count;
	}

	/**
	 * Retrieve version of DB to sync data, and exclude null data request from DB
	 * @return DATABASE_VERSION integer value
	 */
	public static int getDbVersion(){
		return DBConstants.DATABASE_VERSION;
	}


	private SQLiteDatabase CourtDeadlinesDB;


    private static class DatabaseHelper extends SQLiteOpenHelper{
		DatabaseHelper(Context context) {
			super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db){
			for (String createTableCall :createTablesArray) {
				db.execSQL(createTableCall);
			}
//		    db.execSQL(DBConstants.TASKS_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion,int newVersion) {
		   Log.w("Content provider database",
		        "Upgrading database from version " +
		        oldVersion + " to " + newVersion +
		        ", which will destroy all old data");
			for (String createTableCall :tablesArray) {
				db.execSQL("DROP TABLE IF EXISTS " + createTableCall);
			}
//		   db.execSQL("DROP TABLE IF EXISTS " + DBConstants.COURT_CASE_TABLE);

		   onCreate(db);
		}
   }
}
