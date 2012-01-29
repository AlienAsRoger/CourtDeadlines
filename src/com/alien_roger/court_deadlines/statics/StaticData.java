package com.alien_roger.court_deadlines.statics;


/**
 * StaticData class
 *
 * @author alien_roger
 * @created at: 06.01.12 16:37
 */
public class StaticData {
    public static String DEFAULT_URL = "http://www.lawnet.gr/";
    public static String PART_1 = "prothesmies-dikasthriwn.html?id=20&cat=";
    public static final String ID = "id";
    public static final String URL_PATH = "url_path";
	public static final Long FIRST_LEVEL = 3L;
    public static final String TRIAL_DATA = "trial_data";

	public static final String SHP_DATA_SAVED = "trials_list_data_saved";
	public static final String SHP_LOAD_FILE = "file_to_load";
	public static final String TITLE = "title";
	public static final String BROADCAST_ACTION = "com.alien_roger.court_deadlines.data_ready";
	public static final String LOAD_FILE = "d1.txt";
	public static final String CHILD_DELIMITER = " - ";
	public static final String LEVEL_DELIMITER = ".";

	public static final String D_AFTER_T 	= "AT";
	public static final String D_BEFORE_T 	= "BT";
	public static final String D_AFTER_C 	= "AC";
	public static final String D_BEFORE_C 	= "BC";
	public static final String D_WORK 		= "C";
	public static final String D_CALENDAR	= "W";

	public static final String D_TIME_1	= "(";
	public static final String D_TIME_2	= ")";
	public static final String D_TIME_am	= "am";
	public static final String D_TIME_pm	= "pm";

	public static final String SHP_DB_VERSION = "db_version";
	public static final String TASK_SOUND = "task_sound";
	public static final String TASK_TITLE = "task_title";
	public static final String CLEAR_ALARM = "clear_alarm";

	public static final String REQUEST_CODE = "pending_intent_request_code";
	public static final int REMIND_ALARM_INTERVAL = 5*60*1000; // 5 minutes
//	public static final int REMIND_ALARM_INTERVAL = 20*1000; // 20 minutes


	public static final String NOTIFICATION_SOUND_DEFAULT = "notification_sound_default";
	public static final String NOTIFICATION_VIBRO_DEFAULT = "notification_vibrate_default";
	public static final String COURT_CASE = "court_case";

	public static final int PICK_SOUND = 55;
}
