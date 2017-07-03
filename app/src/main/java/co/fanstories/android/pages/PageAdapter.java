package co.fanstories.android.pages;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import co.fanstories.android.R;

/**
 * Created by mohsal on 7/3/17.
 */

public class PageAdapter extends ArrayAdapter {
    public PageAdapter(@NonNull Context context, ArrayList<Pages.Page> pages) {
        super(context, 0, pages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Pages.Page page = (Pages.Page) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pages_list_item, parent, false);
        }

        TextView pageName = (TextView) convertView.findViewById(R.id.page_list_item_name);
        pageName.setText(page.name);

        return convertView;
    }
}
