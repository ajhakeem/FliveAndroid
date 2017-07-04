package co.fanstories.android.pages;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.security.PublicKey;
import java.util.ArrayList;

import co.fanstories.android.R;

/**
 * Created by mohsal on 7/3/17.
 */

public class PageAdapter extends ArrayAdapter {
    TextView pageName, blogUrl;
    private static final String TAG = "PageAdapter";

    public PageAdapter(@NonNull Context context, ArrayList<Pages.Page> pages) {
        super(context, 0, pages);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final Pages.Page page = (Pages.Page) getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.pages_list_item, parent, false);
        }

        pageName = (TextView) convertView.findViewById(R.id.page_list_item_name);
        pageName.setText(page.name);
        if(page.verified) { pageName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green_24dp, 0); }

        blogUrl = (TextView) convertView.findViewById(R.id.page_list_item_blog_url);
        blogUrl.setText(page.verified && page.blogUrl != null && page.blogUrl.length() > 0 ? "http://" + page.blogUrl : "");

        if(page.verified && page.blogUrl != null && page.blogUrl.length() > 0) {
            Button goLiveButton = (Button) convertView.findViewById(R.id.page_go_live_btn);
            goLiveButton.setVisibility(View.VISIBLE);
            goLiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "In development. Selected page: " + page.name, Toast.LENGTH_SHORT).show();
                }
            });
        }

        return convertView;
    }
}
