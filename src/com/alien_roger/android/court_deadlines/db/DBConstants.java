package com.alien_roger.android.court_deadlines.db;

import android.net.Uri;


public class DBConstants {

	public static final String PROVIDER_NAME = "com.alien_roger.android.courtdeadlines.case_provider";
	/*
	 * DB table names
	 */
    static final String DATABASE_NAME 	 = "Court Cases";
    static final String COURT_CASE_TABLE = "court_cases";
    static final String COURT_TRIALS_TABLE = "court_trials";


	// Content URI
    public static final Uri TASKS_CONTENT_URI    		= Uri.parse("content://"+ PROVIDER_NAME + "/" + COURT_CASE_TABLE);
    public static final Uri TRIALS_CONTENT_URI    		= Uri.parse("content://"+ PROVIDER_NAME + "/" + COURT_TRIALS_TABLE);

    // uri paths
    public static final int COURT_CASES = 0;
    public static final int COURT_CASE_ID = 1;

    public static final int COURT_TRIALS = 2;
    public static final int COURT_TRIAL_ID = 3;

//    public static final int COURT_CASES = 0;
//    public static final int COURT_CASE_ID = 1;
//
//    public static final int COURT_TRIALS = 2;
//    public static final int COURT_TRIAL_ID = 3;

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


    public static final String TRIAL_DEPTH_LEVEL	= "trial_depth_level";
    public static final String TRIAL_PARENT_LEVEL	= "trial_parent_level";
    public static final String TRIAL_CURRENT_LEVEL	= "trial_current_level";
    public static final String TRIAL_HAVE_CHILDS	= "trial_have_childs";
    public static final String TRIAL_VALUE	    	= "trial_value";


    /**
     * DB creation params
     */
    static final int DATABASE_VERSION 	= 4;

    static final String TASKS_TABLE_CREATE =
        "create table " + COURT_CASE_TABLE +
        " (_id integer primary key autoincrement, "
        + COURT_TYPE 		+ " TEXT not null,"
        + CUSTOMER 			+ " TEXT not null,"
        + CASE_NAME 		+ " TEXT not null,"
        + COURT_DATE 		+ " LONG not null,"
        + PROPOSAL_DATE 	+ " LONG not null,"
        + NOTES 	+ " TEXT not null);";

    static final String TRIALS_TABLE_CREATE =
        "create table " + COURT_TRIALS_TABLE +
        " (_id integer primary key autoincrement, "
        + TRIAL_DEPTH_LEVEL 		+ " INTEGER not null,"
        + TRIAL_HAVE_CHILDS 		+ " INTEGER not null,"
        + TRIAL_PARENT_LEVEL 		+ " INTEGER not null,"
        + TRIAL_CURRENT_LEVEL 		+ " INTEGER not null,"
        + TRIAL_VALUE 				+ " TEXT not null);";

}
