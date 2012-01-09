package com.alien_roger.android.court_deadlines.db;

import android.net.Uri;


public class DBConstants {
	
	public static final String PROVIDER_NAME = "com.alien_roger.android.courtdeadlines.case_provider";
	/*
	 * DB table names
	 */
    static final String DATABASE_NAME 	 = "Court Cases";
    static final String COURT_CASE_TABLE = "court_cases";

	
	// Content URI
    public static final Uri TASKS_CONTENT_URI    		= Uri.parse("content://"+ PROVIDER_NAME + "/" + COURT_CASE_TABLE);
    
    // uri paths
    public static final int COURT_CASES = 0;
    public static final int COURT_CASE_ID = 1;
    
    // general fields
    public static final String _ID = "_id";
    public static final String _COUNT = "_count";


    // Flight fields
    public static final String CUSTOMER	        = "customer";
    public static final String CASE_NAME	    = "case_name";
    public static final String COURT_DATE	    = "court_date";
    public static final String PROPOSAL_DATE	= "proposal_date";
    public static final String NOTES	        = "notes";
    public static final String COURT_TYPE	    = "court_type";


    /**
     * DB creation params
     */
    static final int DATABASE_VERSION 	= 1;
    
    static final String TASKS_TABLE_CREATE =
        "create table " + COURT_CASE_TABLE +
        " (_id integer primary key autoincrement, "
        + COURT_TYPE 		+ " TEXT not null,"
        + CUSTOMER 			+ " TEXT not null,"
        + CASE_NAME 		+ " TEXT not null,"
        + COURT_DATE 		+ " LONG not null,"
        + PROPOSAL_DATE 	+ " LONG not null,"
        + NOTES 	+ " TEXT not null);";
}
