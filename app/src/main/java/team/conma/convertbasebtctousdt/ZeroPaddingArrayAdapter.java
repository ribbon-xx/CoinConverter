package team.conma.convertbasebtctousdt;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * Created by longdinh Dinh on 9/4/17.
 */

public class ZeroPaddingArrayAdapter<T> extends ArrayAdapter<T> {

	public ZeroPaddingArrayAdapter(Context context, int textViewResourceId, T[] objects) {
		super(context, textViewResourceId, objects);
	}

	public static ArrayAdapter<CharSequence> createFromResource(Context context, int textArrayResId, int textViewResId) {
		CharSequence[] strings = context.getResources().getTextArray(textArrayResId);
		return new ZeroPaddingArrayAdapter<CharSequence>(context, textViewResId, strings);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = super.getView(position, convertView, parent);
		view.setPadding(0, view.getPaddingTop(), 0, view.getPaddingBottom());
		return view;
	}

}
