//
//  Apphuset AS
//  Copyright 2011 Oslo Lufthavn AS
//  All Rights Reserved.
//
//  Author: Yuri V. Suhanov - Azoft
//
package com.alien_roger.android.court_deadlines.xml_parsers;


import android.util.Log;
import com.alien_roger.android.court_deadlines.entities.CourtType;
import com.alien_roger.android.court_deadlines.entities.CourtTypeLevels;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;

public class CourtsParser extends DefaultHandler {

    private CourtTypeLevels currentMessages;
    private CourtType currentMessage;
    private List<CourtType> messages;

    private static final String BODY 	    = "body";
    private static final String STRONG 	    = "strong";
    private static final String LINK 	    = "a";
    private static final String HREF 	    = "href";
    private static final String SPAN 	    = "span";
    private static final String STYLE 	    = "style";
    private static final String LEVEL 	= "margin-left:";
    private static final int levelLength = "margin-left:10".length();
    
    private StringBuilder builder;

	private boolean doneFlag = false;
    private boolean strongFound = false;
    private boolean hasLink = false;
    private boolean in_span_tag = false;
    private String itemLink = "";

    public List<CourtType> getMessages(){
		return messages;
	}

    public CourtType getMessage(){
        return currentMessage;
    }


    /** Gets be called on closing tags like:
     * </tag> */
	@Override
	public void endElement(String uri, String localName, String name) throws SAXException {
		super.endElement(uri, localName, name);
		
		if(doneFlag)return;

		if (currentMessage != null){
            if(localName.equalsIgnoreCase(STRONG)){
                currentMessage.setBody(builder.toString());
                currentMessages.addBody(currentMessage.getLevel(),
                        currentMessage.getBody(),null);
                messages.add(currentMessage);
            }else if(localName.equalsIgnoreCase(SPAN) && !hasLink){
                currentMessage.setBody(builder.toString());
                currentMessages.addBody(currentMessage.getLevel(),
                        currentMessage.getBody(), null);
                messages.add(currentMessage);
            }else if(localName.equalsIgnoreCase(LINK) && in_span_tag){
                currentMessage.setBody(builder.toString());
                currentMessages.addBody(currentMessage.getLevel(),
                        currentMessage.getBody(), null);
                currentMessage.setLink(itemLink);
                messages.add(currentMessage);

                hasLink = false;
                in_span_tag = false;
                currentMessage = null;
            }else if(localName.equalsIgnoreCase(STRONG)){
            }
		}

		builder.setLength(0);
	}

	
	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
        currentMessages = new CourtTypeLevels();
        messages = new ArrayList<CourtType>();
		builder = new StringBuilder();
	}
	
    /** Gets be called on opening tags like:
     * <tag>
     * Can provide attribute(s), when xml was like:
     * <tag attribute="attributeValue">*/
	@Override
	public void startElement(String uri, String localName, String name,Attributes attributes) throws SAXException {
		super.startElement(uri, localName, name, attributes);
		if(doneFlag)return;
		
		if (localName.equalsIgnoreCase(STRONG)){
			currentMessage = new CourtType();
            currentMessage.setLevel(0);
            strongFound = true;
		}else if(localName.equalsIgnoreCase(SPAN)){
            currentMessage = new CourtType();
            currentMessage.setLevel(getLevel(attributes.getValue(STYLE)));
            in_span_tag = true;
        }else if(localName.equalsIgnoreCase(LINK)){
            itemLink = attributes.getValue(HREF);
            hasLink = true;
        }
		
		
//		if(strongFound){
//			if(localName.equalsIgnoreCase(STATUS)){
//				if(attributes.getValue(CODE) != null)
//					currentMessage.setFlightStatus(attributes.getValue(CODE));
//	            if(attributes.getValue(TIME) != null)
//	            	currentMessage.setFlightStatus_date_time(attributes.getValue(TIME));
//			}
//		}
	}

    private int getLevel(String attr){
        if(attr.contains(LEVEL)){
            String subStr = attr.substring(attr.indexOf(LEVEL), attr.indexOf(LEVEL) + levelLength);
//        margin-left:10px
            String level = subStr.substring(subStr.indexOf(":")+1,subStr.indexOf(":") + 3);
            Log.d("CC","level = " + level);
            return Integer.parseInt(level);
        }
        return 0;
    }
    
    /*
        <strong>Πολιτική Δίκη</strong><br/>
        <span style="display:block;clear:both;float:left;margin-left:10px;">Τακτική Διαδικασία</span>
        <span style="margin-left:20px;float:left;display:block;clear:both;">
        <a href="prothesmies-dikasthriwn.html?sid=1" style="color: #457BBB;font: 12px/19px Tahoma,Arial,Verdana; text-decoration: none;}">Ενώπιον Ειρηνοδικείου</a><br/>
        </span>	<span style="margin-left:20px;float:left;display:block;clear:both;">
        <a href="prothesmies-dikasthriwn.html?sid=4" style="color: #457BBB;font: 12px/19px Tahoma,Arial,Verdana; text-decoration: none;}">Ενώπιον Μονομελούς Πρωτοδικείου</a><br/>
    */
	


		
	
    /** Gets be called on the following structure:
     * <tag>characters</tag> */
    @Override
	public void characters(char[] ch, int start, int length) throws SAXException {
    	super.characters(ch, start, length);
    	
		if(doneFlag)return;
    	
    	for (int i = start; i < start + length; i++) {
    	    switch (ch[i]) {
    	    case '\\': break;
    	    case '"': break;
    	    case '\n': break;
    	    case '\r': break;
    	    case '\t':  break;
    	    default:{
    	    	builder.append(ch[i]);
    	    	}break;
    	    }
    	}    
    }

    
}
