package id.ac.itats.skripsi.databuilder;

import id.ac.itats.skripsi.orm.DaoMaster;
import id.ac.itats.skripsi.orm.DaoMaster.DevOpenHelper;
import id.ac.itats.skripsi.orm.DaoSession;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DataBaseHelper {

	private String TAG = "DataBaseHelper";
	private static DataBaseHelper instance;

	private SQLiteDatabase dataBase;
	private String DB_NAME = "astarDB-1";
	private String DB_PATH = "";

	private Context context;

	private DevOpenHelper openHelper;
	private DaoMaster daoMaster;

	private DataBaseHelper(Context context) {
		this.context = context;
		prepareConnection();
	}

	public static synchronized DataBaseHelper getInstance(Context context) {
		if (instance == null) {
			instance = new DataBaseHelper(context);
		}
		return instance;
	}

	private void prepareConnection() {
		// prepareDatabase


		DB_PATH = context.getApplicationInfo().dataDir + "/databases/";

		openHelper = new DevOpenHelper(context, DB_NAME, null);
		boolean databaseExist = checkDataBase();

		if (!databaseExist) {
			openHelper.getReadableDatabase();
			openHelper.close();
			try {
				// copyDatabase
				copyDatabase();

				Log.i(TAG, "Database connected");
			} catch (IOException e) {
				throw new Error("Database connection failure !", e);
			}

		}
		dataBase = openHelper.getReadableDatabase();
		daoMaster = new DaoMaster(dataBase);
	}

	private void copyDatabase() throws IOException {
		InputStream mInput = context.getAssets().open(DB_NAME);
		String outFileName = DB_PATH + DB_NAME;
		OutputStream mOutput = new FileOutputStream(outFileName);
		byte[] mBuffer = new byte[1024];
		int mLength;
		while ((mLength = mInput.read(mBuffer)) > 0) {
			mOutput.write(mBuffer, 0, mLength);
		}
		mOutput.flush();
		mOutput.close();
		mInput.close();
	}

	private boolean checkDataBase() {
		File dbFile = new File(DB_PATH + DB_NAME);
		return dbFile.exists();
	}

	public DaoSession openSession() {
		return daoMaster.newSession();
	}

	public SQLiteDatabase getDataBase() {
		return dataBase;
	}
	
	public void closeSession() {
		if (dataBase != null)
			dataBase.close();
		openHelper.close();
	}

}