package id.ac.itats.skripsi.astarku.view;

import id.ac.itats.skripsi.astarku.R;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PlaceActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {

	private Uri uri;
	private TextView tvName;
	private TextView tvCategory;
	private TextView tvAddress;
	private TextView tvLatlon;
	private Button button;
	String[] latlon;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_place);

		Intent intent = getIntent();
		uri = intent.getData();

		tvName = (TextView) findViewById(R.id.tv_name);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvCategory = (TextView) findViewById(R.id.tv_category);
		tvLatlon = (TextView) findViewById(R.id.tv_latlon);
		button = (Button) findViewById(R.id.button1);
		
		button.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v) {
				Intent in = new Intent(PlaceActivity.this, MainActivity.class);
				in.putExtra("latlon", latlon);
				startActivity(in);
				
			}
			
		});
		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getBaseContext(), uri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (cursor.moveToFirst()) {
			// "PLACE_ID", "NAME", "ADDRESS", "LAT_LON", "CATEGORY"
			tvName.setText("Name : " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
			tvAddress.setText("Address : " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
			tvLatlon.setText("LatLon : " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
			tvCategory.setText("Category : " + cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));

			latlon = cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))).split(",");

			
			System.out.println(latlon[0] + " " + latlon[1]);
		}

	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
