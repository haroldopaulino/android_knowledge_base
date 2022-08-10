package com.prpinfo.bancodesolucoes;

import java.io.ByteArrayOutputStream;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.graphics.Bitmap;
import android.util.Log;

public class SqliteCore {

	private final String DATABASE_NAME = "solutions.db";
    private final int DATABASE_VERSION = 1;
    private final String SETTINGS_TABLE = "table_settings";

	private final SQLiteDatabase db;

	private final SQLiteStatement insertStmtSettings;
    private final SQLiteStatement deleteStmtSettings;


	public SqliteCore(Context context) {
		OpenHelper openHelper = new OpenHelper(context);
    	this.db = openHelper.getWritableDatabase();
		String INSERT_SETTING = "insert into " + SETTINGS_TABLE + "(setting_name,setting_value) values (?,?)";
		this.insertStmtSettings = this.db.compileStatement(INSERT_SETTING);
		String DELETE_SETTING = "delete from " + SETTINGS_TABLE + " where setting_name = ?";
		this.deleteStmtSettings = this.db.compileStatement(DELETE_SETTING);
    }
    
    public long parseTimestamp(String input) {
    	String[] tmp1 = input.split(" ");
    	long output;
    	try { 
    		output = Long.parseLong(tmp1[0].replaceAll("-", "") + tmp1[1].replaceAll(":", ""));
    	} catch ( NumberFormatException nfe ) { 
			output = 0; 
		}
		
		return output;
    }
    
    @SuppressWarnings("unused")
	private byte[] bitmaptoByteArray(Bitmap bmp) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
		return stream.toByteArray();
    }
    
    public void deleteAllSettings() {
    	this.db.delete(SETTINGS_TABLE, null, null);
    }
    
    public String getSetting(String inputSetting) {
    	String output = "";
    	
    	Cursor cursor = db.query(SETTINGS_TABLE, new String[] { "setting_value" }, "setting_name = ?", new String[] { inputSetting }, null, null, null);
    	if (cursor.moveToFirst()) {
    		do {
    				output = cursor.getString(0).trim();
    			} while (cursor.moveToNext());
    	}
    	
    	if (!cursor.isClosed()) {
    		cursor.close();
    	}
    	
    	return output;
	}
    
    public long createSetting(String settingName, String settingValue) {
		deleteSetting(settingName);
    	this.insertStmtSettings.bindString(1, settingName);
    	this.insertStmtSettings.bindString(2, settingValue);
    	return this.insertStmtSettings.executeInsert();
    }
    
    public long deleteSetting(String settingName) {
    	this.deleteStmtSettings.bindString(1, settingName);
    	return this.deleteStmtSettings.executeInsert();
    }
    
    public boolean updateSetting(String settingName, String settingValue) {
    	ContentValues args = new ContentValues();
    	args.put("setting_value", settingValue);
    	String[] whereArgs = {settingName};
    	return (db.update(SETTINGS_TABLE, args, "setting_name=?", whereArgs) > 0);
    }
    
    private class OpenHelper extends SQLiteOpenHelper {
    	OpenHelper(Context context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	    }
	    
	    @Override
	    public void onOpen(final SQLiteDatabase db) {
	    	//checkDefaultSettings();
	    }
	    
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	//TRY TO CREATE THE DATABASE TABLES, BUT IT WILL ONLY SUCCEED IF THEY DONT EXIST
	    	db.execSQL("CREATE TABLE " + SETTINGS_TABLE + " (setting_name TEXT, setting_value TEXT)");
		}
	    
	    @Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	    	Log.w("Solutions", "Upgrading database, this will drop tables and recreate.");
	    	//db.execSQL("DROP TABLE IF EXISTS " + NOTES_TABLE);
	    	onCreate(db);
	    	//Toast.makeText(context, "created", Toast.LENGTH_LONG);
		}
    }
}
