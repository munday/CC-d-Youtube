/*
 * Copyright (C) 2011 Matt Munday
 *
 * This program is free software; you can redistribute it and/or modify it under the terms 
 * of the GNU General Public License as published by the Free Software Foundation; either 
 * version 2 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; 
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. 
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with this program; 
 * if not, write to the Free Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
*/
package ws.munday.youtubecaptionrate;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;


import ws.munday.youtubecaptionrate.WebRequest;


public class YoutubeSearchXMLHandler extends DefaultHandler {
	public String URL;
	ArrayList<YoutubeSearchResult> results;
	YoutubeSearchResult itm;
	StringBuilder _sb = new StringBuilder();
	boolean _is_item = false;
	boolean _is_author = false;
	int _itm_limit = 1000;
	
	public ArrayList<YoutubeSearchResult> parse() throws ParserConfigurationException, SAXException, IOException {
       
		SAXParserFactory spf = null;
        SAXParser sp = null;
        spf = SAXParserFactory.newInstance();
        String ret = new WebRequest().Get(this.URL);
        ByteArrayInputStream bs = new ByteArrayInputStream(ret.getBytes());
        
        if (spf != null) {
            sp = spf.newSAXParser();
            sp.parse(bs, this);
        }
        
        return results;
	}
    
	@Override 
    public void startDocument() throws SAXException { 
		results = new ArrayList<YoutubeSearchResult>();
        itm = new YoutubeSearchResult();         
	} 

    @Override 
    public void endDocument() throws SAXException { 
    	// 
    } 

    @Override 
    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException { 
    	
    	Log.d("ytc",localName);
    	if (localName.toLowerCase().equals("entry")){
    		_is_item = true;
    	}
    	
    	if(localName.equals("author")){
    		_is_author = true;
    	}
    	
    	if(localName.toLowerCase().equals("thumbnail")){
    		if(atts.getValue("width").equals("120")){
    			itm.thumb = atts.getValue("url");
    		}
    	}
    	
    	
    	if(localName.toLowerCase().equals("player")){
    			itm.videoLink = atts.getValue("url");
    	}
    } 
     
    @Override 
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException { 
    	if(results.size() < _itm_limit){
	    	if (localName.toLowerCase().equals("entry")) { 
	    		results.add(itm);
	    		itm = new YoutubeSearchResult();
	    		_is_item =false;
	    	}else if (localName.toLowerCase().equals("author")) {
	    		_is_author = false;
	    	}else if(_is_author && localName.toLowerCase().equals("name")){
	    		itm.author = _sb.toString();
	    	}else if( _is_item && localName.toLowerCase().equals("description")){
	    		itm.desc = _sb.toString().trim();
	    	}else if(_is_item && localName.toLowerCase().equals("videoid")){
       			itm.setId(_sb.toString().trim());
       		}else if(_is_item && localName.toLowerCase().equals("title")){
       			itm.title = _sb.toString().trim();
       		}
    	}
    	
    	this._sb.setLength(0);
    } 
     
   @Override 
   public void characters(char ch[], int start, int length) { 
	   if(results.size() < _itm_limit){
		    _sb.append(ch,start,length);
	   }
   }
}
