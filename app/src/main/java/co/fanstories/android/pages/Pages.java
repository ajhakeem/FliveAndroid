package co.fanstories.android.pages;

import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * Created by mohsal on 7/3/17.
 */

public class Pages {
    public static final String TAG = "Pages";
    public static class Page {
        public String name;
        public String id;
        public String pageId;
        public Page(String id, String name, String pageId) {
            this.name = name;
            this.id = id;
            this.pageId = pageId;
        }

        public Page(JSONObject jsonObject) {
            try {
                this.name = jsonObject.getString("name");
                this.id = jsonObject.getString("id");
                this.pageId = jsonObject.getString("page_id");
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        public static ArrayList fromJson(JSONArray jsonArray) {
            Log.d(TAG, jsonArray.toString());
            ArrayList<Page> pages = new ArrayList<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    pages.add(new Page(jsonArray.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return pages;
        }
    }
}
