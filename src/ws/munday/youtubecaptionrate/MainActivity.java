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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;

public class MainActivity extends ListActivity {
	
	VideoAdapter adapter = null;
	ProgressDialog d = null;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
		Button b = (Button) findViewById(R.id.search_btn);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
						
						d=new ProgressDialog(MainActivity.this);
						d.setMessage(getString(R.string.searching));
						d.setIndeterminate(true);
						d.show();
						
						EditText query = (EditText) findViewById(R.id.query_text);
						String q = query.getText().toString();
						String args[] = {q};
						new SearchTask().execute(args);
						
					
				
			}
		});
		
		ListView l = getListView();
		l.setOnItemClickListener(itemClick);
    
    }
    
    private final OnItemClickListener itemClick = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> a, View v, int position,
				long id) {
			
			YoutubeSearchResult r = (YoutubeSearchResult) adapter.getItem(position);
			GetActionsDialog(r.feedId).show();
		}
	};
    
	private Dialog GetActionsDialog(String vid){
    	final String vidId = vid;
    	return new AlertDialog.Builder(this).setTitle("Video Actions")
    	.setItems(R.array.video_actions, new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				
				final int w = which;
				
				runOnUiThread(new Runnable() {
					public void run() {
						doAction(w, vidId);
					}
				});
				
				dialog.dismiss();
			}
		})
    	.create();
    }
	
	public void doAction(int which, final String vidId){
		
		switch(which){
		case 0:
			YoutubeSearchResult r = (YoutubeSearchResult) adapter.getItem(vidId);
			startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(r.videoLink)));
			
			break;
		case 1:
			getRatingDialog(vidId).show();
			break;
			
		case 2:
			YoutubeSearchResult rs = (YoutubeSearchResult) adapter.getItem(vidId);
			final Intent intent = new Intent(Intent.ACTION_SEND);

			intent.setType("text/plain");
			intent.putExtra(Intent.EXTRA_SUBJECT, "Check out this captioned video on Youtube!");
			intent.putExtra(Intent.EXTRA_TEXT, rs.title + " - " + rs.videoLink);

			startActivity(Intent.createChooser(intent, getString(R.string.share)));
			break;
		}
	
	}
	
	public Dialog getRatingDialog(String vid){
		
		final String vidId = vid;
		Context mContext = MainActivity.this;
		
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.rating_dialog, (ViewGroup) findViewById(R.id.layout_root));

		RatingBar rb = (RatingBar)layout.findViewById(R.id.rating_bar);
		rb.setOnRatingBarChangeListener(new OnRatingChanger(vidId));
		
		
		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		builder.setView(layout).setTitle(R.string.rate_captions_title).setPositiveButton("Done", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});
		
		return builder.create();
		
	}
	
	public class OnRatingChanger implements OnRatingBarChangeListener{

		private final String vidId;
		
		public OnRatingChanger(String VidId){
			vidId = VidId;
		}
		
		public Object getFromWeb(String address) throws MalformedURLException,IOException {
			URL url = new URL(address);
			Object content = url.getContent();
			return content;
		}
		
		@Override
		public void onRatingChanged(RatingBar ratingBar, final float rating,
				boolean fromUser) {
			
			YoutubeSearchResult r = (YoutubeSearchResult) adapter.getItem(vidId);
			r.rating = rating;
			adapter.notifyDataSetChanged();
			
			new Thread(){
				public void run(){
					try {
						getFromWeb("http://data.munday.ws/lastcall_db/add/?vidId=" + vidId + "&r=" + rating);
					} catch (MalformedURLException e) {
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}.start();
			
		}
	
		
		
	}
	
	class SearchTask extends UserTask<String,Void,ArrayList<YoutubeSearchResult>> {

		@Override
		public ArrayList<YoutubeSearchResult> doInBackground(
				String... params) {
			YoutubeSearchXMLHandler h = new YoutubeSearchXMLHandler();
	        h.URL = "http://gdata.youtube.com/feeds/api/videos?q=" + Uri.encode(params[0]) + "&caption&v=2&fields=entry[link/@rel='http://gdata.youtube.com/schemas/2007%23mobile']";
	        
	        ArrayList<YoutubeSearchResult> results = null;
			try {
				results = h.parse();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
	        
			return results;
			
		}
	
		public  void onPostExecute(ArrayList<YoutubeSearchResult> results){
			if(results!=null){
				adapter = new VideoAdapter(results, getApplicationContext());
				getListView().setAdapter(adapter);	
			}
			d.dismiss();
		}
	}
}