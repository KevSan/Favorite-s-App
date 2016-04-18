package org.turntotech.sqlitesample;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.view.MenuInflater;
import android.view.Gravity;

public class MainActivity extends Activity {

	private SQLiteDatabase db;
	private ArrayList<HashMap<String, String>> data;
	private SimpleAdapter adapter;

	private EditText fieldName;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
        Log.i("TurnToTech", "Project Name - SQLiteSample");

		fieldName = (EditText) findViewById(R.id.editText);

		ListView listView = (ListView) findViewById(R.id.listView);
		registerForContextMenu(listView);

		db = openOrCreateDatabase("androidsqlite.db", Context.MODE_PRIVATE, null);
		db.execSQL("CREATE TABLE IF NOT EXISTS Student ( id INTEGER PRIMARY KEY, stud_name TEXT)");

		data = new ArrayList<HashMap<String, String>>();
		adapter = new SimpleAdapter(this, data, R.layout.row_layout,
				new String[] { "stud_id", "stud_name" }, new int[] {
						R.id.stud_id, R.id.stud_name });
		listView.setAdapter(adapter);

		populateList();

		Context context = getApplicationContext();
		CharSequence text = "Type www.*site*.com when adding a site to the list";
		int duration = Toast.LENGTH_LONG;

		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.TOP, 0, 0);
		toast.show();}


    public void buttonClicked(View view){

        ConnectivityManager cmanager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo networkInfoObj = cmanager.getActiveNetworkInfo();

        String networkType = null;
        if (networkInfoObj != null && networkInfoObj.isConnected()) {

            if (networkInfoObj.getType() == ConnectivityManager.TYPE_WIFI)
                networkType = "WI-FI will be used for Internet";
            else if (networkInfoObj.getType() == ConnectivityManager.TYPE_MOBILE)
                networkType = "Cell phone data will be used for Internet";
            else
                networkType = "Not cell or WI-FI";}

        else
            networkType = "Not Connected";

        Toast.makeText(MainActivity.this, networkType, Toast.LENGTH_LONG)
                .show();}


	private void populateList(){
		Cursor cursor = db.rawQuery("SELECT stud_id,stud_name FROM Student",
				null);
		data.clear();
		while (cursor.moveToNext()) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("stud_id", cursor.getString(0));
			map.put("stud_name", cursor.getString(1));
			data.add(map);}

		cursor.close();
		adapter.notifyDataSetChanged();}


	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
									ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_menu, menu);}


	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
		switch (item.getItemId()) {
            case R.id.ViewGame:
                Intent i = new Intent(MainActivity.this, URLActivity.class);
                String site = data.get(info.position).get("stud_name");
                Bundle b = new Bundle();
                b.putString("key", site);
                i.putExtras(b);
                startActivity(i);
                break;
            case R.id.Delete:
                info = (AdapterView.AdapterContextMenuInfo) item
                        .getMenuInfo();
                db.execSQL("delete from Student where stud_id=?", new String[]{data
                        .get(info.position).get("stud_id")});
                populateList();
                return true;}
				return super.onContextItemSelected(item);}


	public void addClicked(View view){
		String name = fieldName.getText().toString();
		db.execSQL("insert into Student(stud_id,stud_name) values(?,?)",
				new String[] { String.valueOf(new Date().getTime()), name });
		fieldName.setText("");
		populateList();}


	@Override
	protected void onDestroy(){
		db.close();
		super.onDestroy();}

}
