package ws.munday.youtubecaptionrate;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

public class VideoAdapter extends BaseAdapter{

	private ArrayList<YoutubeSearchResult> vids;
	private final LayoutInflater inflater;
	private final Context context;
	
	public VideoAdapter(ArrayList<YoutubeSearchResult> vids, Context c) {
		this.context = c;
		this.inflater = LayoutInflater.from(this.context);
		this.vids = vids;
		
	}

	public ArrayList<YoutubeSearchResult> getVids() {
		if(vids!=null)
				return vids;
		else
			return null;
	}

	public void SetVids(ArrayList<YoutubeSearchResult> v) {
			vids = v;
	}

		
	@Override
	public int getCount() {
		if(vids!=null)
				return vids.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if(vids!=null){
				if(vids.size()>position)
					return vids.get(position);
		}
		
		return null;
	}
	
	public Object getItem(String vidId) {
		if(vids!=null){
			for(YoutubeSearchResult r: vids){
				if(r.feedId.equals(vidId)){
					return r;
				}
			}
		}
		
		return null;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		YoutubeSearchResult v = null;
		synchronized (vids) {
			if(vids.size()>position)
				v = vids.get(position);
			else
				return null;
		// When convertView is not null, we can reuse it directly, there is no
		// need
		// to reinflate it. We only inflate a new View when the convertView
		// supplied
		// by ListView is null.
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.video_item, null);

			// Creates a ViewHolder and store references to the two children
			// views
			// we want to bind data to.
			holder = new ViewHolder();
			holder.videoName = (TextView) convertView.findViewById(R.id.vid_title);
			holder.videoDesc = (TextView) convertView.findViewById(R.id.vid_desc);
			holder.thumb = (ImageView) convertView.findViewById(R.id.vid_thumb);
			holder.rating = (RatingBar) convertView.findViewById(R.id.ratingbar);
			
			
			convertView.setTag(holder);
			
		} else {
			// Get the ViewHolder back to get fast access to the TextView
			// and the ImageView.
			holder = (ViewHolder) convertView.getTag();
		}

		// Bind the data efficiently with the holder.
		holder.videoName.setText(v.title);
		holder.videoDesc.setText(v.author);
		
		Drawable img = getWebImageDrawable(v.thumb);
		
		if(img!=null){
			holder.thumb.setImageDrawable(img);
		}
		
		holder.rating.setRating(v.rating);
		
		return convertView;
		
		}
	}

	
	private Drawable getWebImageDrawable(String url) {
		try {
			InputStream is = (InputStream) this.getFromWeb(url);
			Drawable d = Drawable.createFromStream(is, "src");
			return d;
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	public Object getFromWeb(String address) throws MalformedURLException,IOException {
		URL url = new URL(address);
		Object content = url.getContent();
		return content;
	}
	
	final class ViewHolder {
		ImageView thumb;
		TextView videoName;
		TextView videoDesc;
		RatingBar rating;
		
	}
	
	/*
	public class OnRatingChanger implements OnRatingBarChangeListener{

		private final String vidId;
		
		public OnRatingChanger(String VidId){
			vidId = VidId;
		}
		
		@Override
		public void onRatingChanged(RatingBar ratingBar, final float rating,
				boolean fromUser) {
			
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
	*/

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}
	
}
