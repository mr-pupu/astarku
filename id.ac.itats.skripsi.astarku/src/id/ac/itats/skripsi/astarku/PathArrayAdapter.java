package id.ac.itats.skripsi.astarku;

import id.ac.itats.skripsi.shortestpath.model.Path;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class PathArrayAdapter extends ArrayAdapter<Path> {
	private Context context;
	private ArrayList<Path> items;
	
	public PathArrayAdapter(Context context, int resource, ArrayList<Path> items) {
		super(context,resource, items);
		this.context = context;
		this.items=items;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		View pathView = convertView;
		
		if(pathView == null){
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			pathView = inflater.inflate(R.layout.list_item, null);
		}
		
		Path item = this.items.get(position);
		
//		ImageView ivTurn = (ImageView) pathView.findViewById(R.id.iv_turn);
		TextView tvPathStep = (TextView) pathView.findViewById(R.id.tv_pathstep);
		TextView tvPathName = (TextView) pathView.findViewById(R.id.tv_pathname);
		TextView tvPathLenght = (TextView) pathView.findViewById(R.id.tv_pathlenght);
		
		
//		ivTurn.setImageResource(item.getTurnImage());
		tvPathStep.setText(String.valueOf(item.getPathStep()));
		tvPathName.setText(item.getPathName());
		tvPathLenght.setText(item.getPathLenght());
		
		return pathView;
	}
}
