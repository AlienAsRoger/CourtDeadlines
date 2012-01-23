package com.alien_roger.court_deadlines.xml_parsers;

import android.util.Log;
import android.util.Xml;
import com.alien_roger.court_deadlines.entities.CourtType;
import org.htmlcleaner.ContentNode;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * HtmlHelper class
 *
 * @author alien_roger
 * @created at: 06.01.12 17:53
 */
public class HtmlHelper {
    TagNode rootNode;

    String TAG = "HtmlHelper";

    private static final String HOME 	    = "home";
    private static final String BODY 	    = "body";
    private static final String STRONG 	    = "strong";
    private static final String LINK 	    = "a";
    private static final String HREF 	    = "href";
    private static final String SPAN 	    = "span";
    private static final String STYLE 	    = "style";
    private static final String LEVEL 	= "margin-left:";
    private static final int levelLength = "margin-left:10".length();

    public HtmlHelper(InputStream htmlPage) throws IOException{
        HtmlCleaner cleaner = new HtmlCleaner();
        rootNode = cleaner.clean(htmlPage);
    }

    public String getDescription(){
        StringWriter writer = new StringWriter();
        //we create a XmlSerializer in order to write xml data
        XmlSerializer serializer = Xml.newSerializer();
        
        String data = "";
        TagNode[] mainEl  = rootNode.getElementsByName("div", true);
        for (TagNode item: mainEl){
            if(item.getAttributeByName("id") != null && item.getAttributeByName("id").equalsIgnoreCase("container")){
                TagNode div = item.findElementByAttValue("id","main",false,false)
                        .findElementByAttValue("class","mainarea",false,false)
                        .findElementByAttValue("class","tristilo",false,false)
                        .findElementByAttValue("class","distilo",false,false)
                        .findElementByAttValue("class","listcol",false,false)
                        .findElementByAttValue("class","colstwo",false,false)
                        .findElementByAttValue("class","colstwol",false,false);

                try {
                    //we set the StringWriter as output for the serializer, using UTF-8 encoding
                    serializer.setOutput(writer);
                    serializer.startDocument("UTF-8", true);
                    //set indentation option
                    serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
                    //start a tag called "location"
                    serializer.startTag(null, HOME);
                    serializer.startTag(null, BODY);
                    setAttrs(serializer,div);
//                    for (TagNode node: div.getChildTags()){
//                        Map<String,String> map = node.getAttributes();
//                        Set<String> set = map.keySet();
//
//                        // start tag
//                        serializer.startTag(null,node.getName());
//                        for(String key: set){ // add attributes
//                            serializer.attribute(null,key, (String) map.get(key));
//
//                        }
//                        TagNode[] childTags = node.getChildTags();
//                        if(childTags.length > 0){
//                            for (){
//
//                            }
//                        }
//                        // set text
//                        serializer.text(node.getText().toString());
//                        serializer.endTag(null,node.getName());
//
//                    }

                    serializer.endTag(null, BODY);
                    serializer.endTag(null, HOME);
                    serializer.endDocument();
                    //write xml data into the StringWriter
                    serializer.flush();
                } catch (IOException e) {
                    Log.e("Exception", "error occurred while creating xml file");
                }                
//                data = div.getText().toString();
                Log.d(TAG,"data = " + data);
                break;
            }
        }


//        return Html.fromHtml(data).toString();
        return writer.toString();
    }

    private void setAttrs(XmlSerializer serializer, TagNode div) throws IOException{
        TagNode[] childTags = div.getChildTags();
        List childrens = div.getChildren();// TagList() T ags();

        if(childrens.size() > 0){
            for(Object child: childrens){
                if(child instanceof ContentNode){
                    ContentNode node = (ContentNode) child;
                    String text = node.getContent().toString().trim();
                    if(!text.equals("")){
                        serializer.startTag(null,"br");
                        serializer.text(text);
                        serializer.endTag(null,"br");
                    }
                }else if(child instanceof TagNode){
                    TagNode node = (TagNode) child;
                    Map<String,String> map = node.getAttributes();
                    Set<String> set = map.keySet();

                    // start tag
                    serializer.startTag(null,node.getName());
                    if(node.getName().equals("br")){
//                        ContentNode curContNode = (ContentNode)node.getChildren().get(0);
                        Log.d(TAG,"br text" + node. getText().toString());
                    }
                    for(String key: set){ // add attributes
                        serializer.attribute(null,key, map.get(key));
                    }
                    setAttrs(serializer,node);
                    serializer.endTag(null,node.getName());
                }
            }
        }                
                
                
//        if(childTags.length > 0){
//            for (TagNode node: div.getChildTags()){
//                Map<String,String> map = node.getAttributes();
//                Set<String> set = map.keySet();
//
//                // start tag
//                serializer.startTag(null,node.getName());
//                if(node.getName().equals("br")){
//                    ContentNode curContNode = (ContentNode)node.getChildren().get(0);
//                    Log.d(TAG,"br text" + node. getText().toString());
//                }
//                for(String key: set){ // add attributes
//                    serializer.attribute(null,key, (String) map.get(key));
//                }
//                setAttrs(serializer,node);
//                // set text
//                serializer.endTag(null,node.getName());
//            }
//        }else{
//            serializer.text(div.getText().toString());
//        }
    }

    public List<CourtType> getCourtTypes(){
        CourtType currentMessage;
        List<CourtType> messages = new ArrayList<CourtType>();
        Log.d(TAG, "start");
//        id container
//        id main
//        class        mainarea
//        class tristilo
//        class        distilo
//        class listcol
//        class        colstwo
//        class colstwol
        TagNode[] mainEl  = rootNode.getElementsByName("div", true);
        for (TagNode item: mainEl){
            if(item.getAttributeByName("id") != null && item.getAttributeByName("id").equalsIgnoreCase("container")){
                TagNode div = item.findElementByAttValue("id","main",false,false)
                        .findElementByAttValue("class","mainarea",false,false)
                        .findElementByAttValue("class","tristilo",false,false)
                        .findElementByAttValue("class","distilo",false,false)
                        .findElementByAttValue("class","listcol",false,false)
                        .findElementByAttValue("class","colstwo",false,false)
                        .findElementByAttValue("class","colstwol",false,false);
                String firstTitle = div.findElementByName("div", false).findElementByName(STRONG,false).getText().toString();
                currentMessage = new CourtType();
                currentMessage.setLevel(0);
                currentMessage.setBody(firstTitle);
                messages.add(currentMessage);
                TagNode[] spans = div.findElementByName("div",false).getElementsByName(SPAN,false);
                for (TagNode span : spans) {
                    currentMessage = new CourtType();
                    if (span.getAttributeByName(STYLE).contains(LEVEL)) {
                        currentMessage.setLevel(getLevel(span.getAttributeByName(STYLE)));
                        TagNode aTag = span.findElementByName(LINK, false);
                        if (aTag == null) {
                            currentMessage.setBody(span.getText().toString().trim());
                        } else {
                            currentMessage.setBody(aTag.getText().toString().trim());
                            currentMessage.setLink(aTag.getAttributeByName(HREF));
                        }
                    }
                    messages.add(currentMessage);
                }
                break;
            }
        }
        return messages;
    }

    private int getLevel(String attr){
        if(attr.contains(LEVEL)){
            String subStr = attr.substring(attr.indexOf(LEVEL), attr.indexOf(LEVEL) + levelLength);
//        margin-left:10px
            String level = subStr.substring(subStr.indexOf(":")+1,subStr.indexOf(":") + 3);
//            Log.d("CC","level = " + level);
            return Integer.parseInt(level);
        }
        return 0;
    }
}
