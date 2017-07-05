package co.fanstories.android.pages;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.text.Layout;
import android.text.Spannable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.PublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import co.fanstories.android.R;
import co.fanstories.android.http.Callback;
import co.fanstories.android.live.LiveGateway;
import co.fanstories.android.liveVideoBroadcaster.LiveVideoBroadcasterActivity;
import co.fanstories.android.utils.json.JSONUtils;
import co.fanstories.android.utils.text.TextUtils;

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

        if(page.verified) {
            pageName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, R.drawable.ic_check_circle_green_24dp, 0);
         } else {
            pageName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, 0, 0);
        }

        blogUrl = (TextView) convertView.findViewById(R.id.page_list_item_blog_url);
        Button goLiveButton = (Button) convertView.findViewById(R.id.page_go_live_btn);
        if(page.verified && page.blogUrl != null && page.blogUrl.length() > 0) {
            blogUrl.setText(page.verified && page.blogUrl != null && page.blogUrl.length() > 0 ? "http://" + page.blogUrl : "");
            TextUtils.removeUnderlines((Spannable) blogUrl.getText());
            blogUrl.setVisibility(View.VISIBLE);

            goLiveButton.setVisibility(View.VISIBLE);
            goLiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(getContext(), "In development. Selected page: " + page.name, Toast.LENGTH_SHORT).show();
                    LiveGateway liveGateway = new LiveGateway(getContext());
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("page_id", page.pageId);
                    liveGateway.getStreamKey(params, new Callback() {
                        @Override
                        public void onSuccess(JSONObject response) throws JSONException {
                            Map<String, Object> res = JSONUtils.jsonToMap(response);
                            Intent intent = new Intent(getContext(), LiveVideoBroadcasterActivity.class);
                            intent.putExtra("streamKey", ((Map)res.get("data")).get("stream_key").toString());
                            getContext().startActivity(intent);
                        }

                        @Override
                        public void Onerror(VolleyError error) {
                            Toast.makeText(getContext(), "Could not get key. Contact support", Toast.LENGTH_SHORT);
                        }
                    });
                }
            });
        } else {
            blogUrl.setVisibility(View.GONE);
            goLiveButton.setVisibility(View.GONE);
        }

        return convertView;
    }
}
