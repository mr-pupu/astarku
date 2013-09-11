package id.ac.itats.skripsi.astarku.view;

import id.ac.itats.skripsi.astarku.R;
import id.ac.itats.skripsi.databuilder.PlaceContentProvider;
import android.app.SearchManager;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class SearchActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor>{

	ListView lvPlace;
	SimpleCursorAdapter mCursorAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(R.style.Theme_Styled);
		setContentView(R.layout.activity_searchable);
		
		lvPlace = (ListView) findViewById(R.id.lv_places);
		
		lvPlace.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent placeIntent = new Intent(getApplicationContext(), PlaceActivity.class);
				
				Uri data = Uri.withAppendedPath(PlaceContentProvider.CONTENT_URI, String.valueOf(id));
				System.out.println(id);
				placeIntent.setData(data);
				
				startActivity(placeIntent);
			}
			
			
		});
		
		mCursorAdapter = new SimpleCursorAdapter(getBaseContext(),
				android.R.layout.simple_list_item_1, 
				null, 
				new String[] {SearchManager.SUGGEST_COLUMN_TEXT_1},
				new int[]{android.R.id.text1},0);
		
		lvPlace.setAdapter(mCursorAdapter);
		
		Intent intent = getIntent();
		
		if(intent.getAction().equals(Intent.ACTION_VIEW)){
			Intent placeIntent = new Intent(this,PlaceActivity.class);
			placeIntent.setData(intent.getData());
			startActivity(placeIntent);
			finish();
		} else if (intent.getAction().equals(Intent.ACTION_SEARCH)){
			String query = intent.getStringExtra(SearchManager.QUERY);
			doSearch(query);
		}
		
	}
	
	private void doSearch(String query){
		Bundle data = new Bundle();
		data.putString("query", query);
		getSupportLoaderManager().initLoader(1, data, this);		
	}
	
	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle data) {
		Uri uri = PlaceContentProvider.CONTENT_URI;		
		return new CursorLoader(getBaseContext(), uri, null, null , new String[]{data.getString("query")}, null);	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		mCursorAdapter.swapCursor(c);
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}


}
