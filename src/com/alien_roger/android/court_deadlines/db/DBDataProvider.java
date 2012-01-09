package com.alien_roger.android.court_deadlines.db;

import android.content.*;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;


public class DBDataProvider extends ContentProvider{
    private static final UriMatcher uriMatcher;
    
    static{
       uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
       uriMatcher.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_CASE_TABLE, 		DBConstants.COURT_CASES);
       uriMatcher.addURI(DBConstants.PROVIDER_NAME, DBConstants.COURT_CASE_TABLE + "/#", DBConstants.COURT_CASE_ID);
    }
    private DatabaseHelper dbHelper;

    @Override
	public boolean onCreate() {
		Context context = getContext();
		dbHelper = new DatabaseHelper(context);
		TaskRings_DB = dbHelper.getWritableDatabase();
	      
	    return (TaskRings_DB == null)? false:true;
	}
	
	@Override
	public String getType(Uri uri){
		switch (uriMatcher.match(uri)){
		case DBConstants.COURT_CASES:
			return "vnd.android.cursor.dir/vnd.alien_roger.courtdeadlines";
        case DBConstants.COURT_CASE_ID:
           return "vnd.android.cursor.item/vnd.alien_roger.courtdeadlines";
		default:
		    throw new IllegalArgumentException("Unsupported URI: " + uri);
		}    		
	}	
	
	
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
		
		switch (uriMatcher.match(uri)){
		case DBConstants.COURT_CASES:
		    sqlBuilder.setTables(DBConstants.COURT_CASE_TABLE);
		    break;
		case DBConstants.COURT_CASE_ID:
		    sqlBuilder.setTables(DBConstants.COURT_CASE_TABLE);
			sqlBuilder.appendWhere(DBConstants._ID + " = " + uri.getPathSegments().get(1));
			break;
		default: throw new IllegalArgumentException("Unsupported URI: " + uri);
		}
        Cursor c = sqlBuilder.query(TaskRings_DB, projection, selection, selectionArgs, null, null, sortOrder);
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;	

	}
	
	@Override
	public int update(Uri uri, ContentValues values, String selection,String[] selectionArgs){
	      int count = 0;
	      switch (uriMatcher.match(uri)){
	         case DBConstants.COURT_CASES:
	            count = TaskRings_DB.update(DBConstants.COURT_CASE_TABLE,values,selection,selectionArgs);
	         break;
	         case DBConstants.COURT_CASE_ID:
	             count = TaskRings_DB.update(DBConstants.COURT_CASE_TABLE, values,
	            		 DBConstants._ID + " = " + uri.getPathSegments().get(1) + 
	            		 (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), 
	            		 selectionArgs);
	             break;
	         default: throw new IllegalArgumentException("Unknown URI " + uri);
	      }
	      getContext().getContentResolver().notifyChange(uri, null);
	      return count;
	}	
	
	@Override
	public Uri insert(Uri uri, ContentValues values) {
		switch (uriMatcher.match(uri)){
		case DBConstants.COURT_CASES:
		case DBConstants.COURT_CASE_ID:
			long rowID = TaskRings_DB.insert(DBConstants.COURT_CASE_TABLE, "", values);
		    //---if added successfully---
		    if (rowID > 0){
		         Uri _uri = ContentUris.withAppendedId(DBConstants.TASKS_CONTENT_URI, rowID);
		         getContext().getContentResolver().notifyChange(_uri, null);    
		         return _uri;                
		    }        
		break;
		default:throw new IllegalArgumentException("Unsupported URI: " + uri);
		}    		
	    throw new SQLException("Failed to insert row into " + uri);
	}
	
	@Override
	public int delete(Uri arg0, String arg1, String[] arg2){
	      // arg0 = uri  arg1 = selection  arg2 = selectionArgs
	      int count=0;
	      switch (uriMatcher.match(arg0)){
	      	 case DBConstants.COURT_CASES:{
      		 	count = TaskRings_DB.delete(DBConstants.COURT_CASE_TABLE,arg1,arg2);
	      	 }break;
	      	 case DBConstants.COURT_CASE_ID:
				String id = arg0.getPathSegments().get(1);
				count = TaskRings_DB.delete(
						DBConstants.COURT_CASE_TABLE,
						DBConstants._ID + " = " + id +(!TextUtils.isEmpty(arg1) ? " AND (" +arg1 + ')' : ""), 
						arg2);
				break;	      	 
	         default: throw new IllegalArgumentException("Unknown URI " + arg0);
	      }       
	      getContext().getContentResolver().notifyChange(arg0, null);
	      return count;  
	}
	
	/**
	 * Retrieve version of DB to sync data, and exclude null data request from DB
	 * @return DATABASE_VERSION integer value
	 */
	public static int getDB_version(){
		return DBConstants.DATABASE_VERSION;
	}
	

	private SQLiteDatabase TaskRings_DB;


    private static class DatabaseHelper extends SQLiteOpenHelper{ 
		DatabaseHelper(Context context) {
			super(context, DBConstants.DATABASE_NAME, null, DBConstants.DATABASE_VERSION);
		}

		@Override
		public void onCreate(SQLiteDatabase db){
		    db.execSQL(DBConstants.TASKS_TABLE_CREATE);
		}
		  
		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, 
		int newVersion) {
		   Log.w("Content provider database", 
		        "Upgrading database from version " + 
		        oldVersion + " to " + newVersion + 
		        ", which will destroy all old data");
		   db.execSQL("DROP TABLE IF EXISTS " + DBConstants.COURT_CASE_TABLE);

		   onCreate(db);
		}
   }	
}
