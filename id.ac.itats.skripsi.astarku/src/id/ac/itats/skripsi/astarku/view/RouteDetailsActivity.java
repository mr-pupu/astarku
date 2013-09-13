package id.ac.itats.skripsi.astarku.view;

import java.util.ArrayList;

import id.ac.itats.skripsi.astarku.PathArrayAdapter;
import id.ac.itats.skripsi.astarku.R;
import id.ac.itats.skripsi.shortestpath.model.Path;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

public class RouteDetailsActivity extends SherlockFragmentActivity {

	private ListView lvRouteDetails;
	private TextView tvTotal;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_route_details);
		setupActionBar();
		lvRouteDetails = (ListView) findViewById(R.id.lvRouteDetails);
		tvTotal = (TextView) findViewById(R.id.tv_route_total);
		
		ArrayList<Path> pathItems = getIntent().getParcelableArrayListExtra("pathItems");
		PathArrayAdapter adapter = new PathArrayAdapter(this, R.layout.list_item, pathItems);
		lvRouteDetails.setAdapter(adapter);
		tvTotal.setText(getIntent().getStringExtra("total"));
		
	}

	private void setupActionBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getSupportMenuInflater().inflate(R.menu.route_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			
			NavUtils.navigateUpFromSameTask(this);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	public ArrayList<Path> getData(){
		ArrayList<Path> data = new ArrayList<Path>();
		data.add(new Path("1",1, "jalan arif rahman hakim", "65 m"));
		data.add(new Path("2",2, "jalan arif rahman hakim", "65 m"));
		data.add(new Path("3",3, "jalan arif rahman hakim", "65 m"));
		data.add(new Path("4",4, "jalan arif rahman hakim", "65 m"));
		return data;
	}

}
