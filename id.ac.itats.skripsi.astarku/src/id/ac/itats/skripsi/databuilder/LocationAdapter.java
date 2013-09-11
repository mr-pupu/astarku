package id.ac.itats.skripsi.databuilder;

import id.ac.itats.skripsi.orm.PlaceDao;

import java.util.HashMap;

import android.app.SearchManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;

public class LocationAdapter {
	protected final String TAG = "LocationAdapter";

	private SQLiteDatabase database;
	
	private static final String PLACE_ID = PlaceDao.Properties.PlaceID.columnName;
	private static final String PLACE_NAME = PlaceDao.Properties.Name.columnName;
	private static final String PLACE_TABLE_NAME = PlaceDao.TABLENAME;

	private HashMap<String, String> aliasMap;

	public LocationAdapter(SQLiteDatabase database) {
		this.database = database;

		aliasMap = new HashMap<String, String>();

		aliasMap.put("_ID", PLACE_ID + " as " + "_id");

		aliasMap.put(SearchManager.SUGGEST_COLUMN_TEXT_1, PLACE_NAME + " as " + SearchManager.SUGGEST_COLUMN_TEXT_1);

		aliasMap.put(SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID, PLACE_ID + " as "
				+ SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID);
	}

	public Cursor getPlaces(String[] selectionArgs) {
		String selection = PLACE_NAME + " like ? ";

		if (selectionArgs != null) {
			selectionArgs[0] = "%" + selectionArgs[0] + "%";
		}

		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setProjectionMap(aliasMap);
		queryBuilder.setTables(PLACE_TABLE_NAME);

		Cursor c = queryBuilder.query(database, new String[] { "_ID", SearchManager.SUGGEST_COLUMN_TEXT_1,
				SearchManager.SUGGEST_COLUMN_INTENT_DATA_ID }, selection, selectionArgs, null, null, PLACE_NAME
				+ " asc ", "10");

		return c;
	}

	public Cursor getPlace(String id) {
		SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
		queryBuilder.setTables(PLACE_TABLE_NAME);
		Cursor c = queryBuilder.query(database, new String[] { "PLACE_ID", "NAME", "ADDRESS", "LAT_LON", "CATEGORY" },
				"PLACE_ID = ?", new String[] { id }, null, null, null, "1");
		return c;
	}

}
