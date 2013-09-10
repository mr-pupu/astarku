package id.ac.itats.skripsi.routingengine.limasatu;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;

public class PlaceActivity extends SherlockFragmentActivity implements LoaderCallbacks<Cursor> {

	private Uri mUri;
	private TextView tvName;
	private TextView tvCategory;
	private TextView tvAddress;
	private TextView tvLatlon;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.activity_place);

		Intent intent = getIntent();
		mUri = intent.getData();

		tvName = (TextView) findViewById(R.id.tv_name);
		tvAddress = (TextView) findViewById(R.id.tv_address);
		tvCategory = (TextView) findViewById(R.id.tv_category);
		tvLatlon = (TextView) findViewById(R.id.tv_latlon);

		getSupportLoaderManager().initLoader(0, null, this);
	}

	@Override
	public Loader<Cursor> onCreateLoader(int arg0, Bundle arg1) {
		return new CursorLoader(getBaseContext(), mUri, null, null, null, null);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor cursor) {
		if (cursor.moveToFirst()) {
			tvName.setText("Name : " 
					+ cursor.getString(cursor.getColumnIndex(cursor.getColumnName(1))));
			tvAddress.setText("Address : "
					+ cursor.getString(cursor.getColumnIndex(cursor.getColumnName(2))));
			tvLatlon.setText("LatLon : "
					+ cursor.getString(cursor.getColumnIndex(cursor.getColumnName(3))));
			tvCategory.setText("Address : "
					+ cursor.getString(cursor.getColumnIndex(cursor.getColumnName(4))));
		}
//"PLACE_ID", "NAME", "ADDRESS", "LAT_LON", "CATEGORY"
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub

	}

}
