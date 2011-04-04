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

	import java.io.IOException;
	import java.io.InputStream;
	import java.net.URL;

	import javax.xml.parsers.ParserConfigurationException;
	import javax.xml.parsers.SAXParser;
	import javax.xml.parsers.SAXParserFactory;

	import org.xml.sax.helpers.DefaultHandler;
	import org.xml.sax.SAXException; 



	public class RatingLookupXMLHandler extends DefaultHandler {

		public String URL;
		RatingLookupItem itm;
		StringBuilder _sb = new StringBuilder();
		
		public RatingLookupItem parse() throws ParserConfigurationException, SAXException, IOException {
	        InputStream urlInputStream = null;
	        SAXParserFactory spf = null;
	        SAXParser sp = null;
	        URL url = new URL(this.URL);
	        urlInputStream = url.openConnection().getInputStream();           
	        spf = SAXParserFactory.newInstance();
	        if (spf != null) {
	            sp = spf.newSAXParser();
	            sp.parse(urlInputStream, this);
	        }
	        if (urlInputStream != null) urlInputStream.close();
	        return itm;
		}
	    
		
		@Override 
	    public void startDocument() throws SAXException { 
	         itm = new RatingLookupItem();
	    } 

	    @Override 
	    public void endDocument() throws SAXException { 
	    	// 
	    } 
 
	    @Override 
	    public void endElement(String namespaceURI, String localName, String qName) 
	              throws SAXException { 
	    	if (localName.equals("rating")) { 
	            this.itm.rating=Float.valueOf(_sb.toString());
	    	}else if(localName.equals("vidId")){
	    		this.itm.vidId = _sb.toString();
	    	}
	    	
	    	this._sb.setLength(0);
	    } 
	     
	    @Override 
	    public void characters(char ch[], int start, int length) { 
	    	_sb.append(ch,start,length);
	    
	    }  
	    
	}

