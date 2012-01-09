package com.alien_roger.android.court_deadlines.entities;

import java.util.HashMap;
import java.util.List;

/**
 * CourtTypeLevels class
 *
 * @author alien_roger
 * @created at: 26.12.11 23:39
 */
public class CourtTypeLevels {
    private String level_0 = "";
    private String level_1 = "";
    private String level_2 = "";


    private HashMap<String,List<String>> firstLevelMap = new HashMap<String, List<String>>();
    private HashMap<String,List<String>> secondLevelMap = new HashMap<String, List<String>>();
    private HashMap<String,List<String>> thirdLevelMap = new HashMap<String, List<String>>();
    
    public void addBody(int level,String body,List<String> list){
        switch (level){
            case 0:
                firstLevelMap.put(body,list);
                break;
            case 1:
                secondLevelMap.put(body,list);
                break;
            case 2:
                thirdLevelMap.put(body,list);
                break;
            default: break;
        }
    }
    
    
    public String getLevel_0() {
        return level_0;
    }

    public void setLevel_0(String level_0) {
        this.level_0 = level_0;
    }

    public String getLevel_1() {
        return level_1;
    }

    public void setLevel_1(String level_1) {
        this.level_1 = level_1;
    }

    public String getLevel_2() {
        return level_2;
    }

    public void setLevel_2(String level_2) {
        this.level_2 = level_2;
    }

}
