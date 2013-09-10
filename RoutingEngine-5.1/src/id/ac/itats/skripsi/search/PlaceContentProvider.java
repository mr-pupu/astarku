package id.ac.itats.skripsi.search;

import id.ac.itats.skripsi.databuilder.DataBaseHelper;
import android.app.SearchManager;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class PlaceContentProvider extends ContentProvider {

	public static final String AUTHORITY = "id.ac.itats.skripsi.search.PlaceContentProvider";
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/places");

	LocationAdapter locationAdapter = null;

	private static final int SUGGESTIONS_PLACE = 1;
	private static final int SEARCH_PLACE = 2;
	private static final int GET_PLACE = 3;

	UriMatcher uriMatcher = buildUriMatcher();

	private UriMatcher buildUriMatcher() {
		UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
		uriMatcher.addURI(AUTHORITY, SearchManager.SUGGEST_URI_PATH_QUERY, SUGGESTIONS_PLACE);
		uriMatcher.addURI(AUTHORITY, "places", SEARCH_PLACE);
		uriMatcher.addURI(AUTHORITY, "places/#", GET_PLACE);
		return uriMatcher;
	}

	@Override
	public boolean onCreate() {
		SQLiteDatabase database = DataBaseHelper.getInstance(getContext()).getDataBase();
		locationAdapter = new LocationAdapter(database);
		return true;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		Cursor c = null;
		switch (uriMatcher.match(uri)) {
		case SUGGESTIONS_PLACE:
			c = locationAdapter.getPlaces(selectionArgs);
			break;
		case SEARCH_PLACE:
			c = locationAdapter.getPlaces(selectionArgs);
			break;
		case GET_PLACE:
			String id = uri.getLastPathSegment();
			c = locationAdapter.getPlace(id);
			break;
		}
		return c;
	}

	@Override
	public int delete(Uri arg0, String arg1, String[] arg2) {
		throw new UnsupportedOperationException();
	}

	@Override
	public String getType(Uri uri) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		throw new UnsupportedOperationException();
	}

}
