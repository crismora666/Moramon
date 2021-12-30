package ec.sekai.moramon;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database_Moramon extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "moramon.db";
	private static final int DATABASE_VERSION = 1;
	
	public Database_Moramon(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION); 
	}

	// Method is called during creation of the database
	//@Override
	public void onCreate(SQLiteDatabase db) {
	//	Table_Actman.onCreate(db);
	}

	// Method is called during an upgrade of the database,
	  // e.g. if you increase the database version
	//@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	//	Table_Actman.onUpgrade(db, oldVersion, newVersion);
	}
}