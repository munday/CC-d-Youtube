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

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

public class YoutubeSearchResult {

	long id;
	String videoLink = "";
	String feedId = "";
	String author = "";
	String title = "";
	String desc = "";
	String thumb = "";
	float rating = 0;
	
	public void setId(String id){
		feedId = id;
		rating = getRating(id);
	}
	
	private float getRating(String id){
			RatingLookupItem r = null;
			String URL = "http://data.munday.ws/lastcall_db/search/?vidId=" + id;
			RatingLookupXMLHandler lookup = new RatingLookupXMLHandler();
			lookup.URL = URL;
			try {
				r = lookup.parse();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return r.rating;
	}
}
