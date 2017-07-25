package co.fanstories.android.pages;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

/**
 * Created by mohsal on 7/3/17.
 */

public class Pages {
    public static final String TAG = "Pages";

    public static class Page {
        public String name;
        public String id;
        public String pageId;
        public boolean verified;
        public String blogUrl;

        public ArrayList<String> arrayListPageNames = new ArrayList<>();

        public Page(String id, String name, String pageId, String verified, String blogUrl) {
            this.name = name;
            this.id = id;
            this.pageId = pageId;
            this.verified = (verified.equals("1") ? true : false);
            this.blogUrl = blogUrl;
        }

        public Page(JSONObject jsonObject) {
            try {
                this.name = jsonObject.getString("name");
                this.id = jsonObject.getString("id");
                this.pageId = jsonObject.getString("page_id");
                this.verified = jsonObject.getString("verified").equals("1") ? true : false;
                this.blogUrl = jsonObject.getString("blog_url");
                //Log.d(TAG, name + " " + " " + String.valueOf(verified));
                arrayListPageNames.add(name.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

        public static ArrayList fetchPageNames(JSONArray jsonArray) {
            ArrayList<String> pageNames = new ArrayList<>();

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    if(jsonArray.getJSONObject(i).get("verified").equals("1")) {
                        pageNames.add(jsonArray.getJSONObject(i).getString("name"));
                    }
                }

                catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return pageNames;
        }

        public static HashMap<String, Page> hashFromJson(JSONArray jsonArray, HashMap<String, Page> hashMapPageDetails) {
            //Log.d(TAG, jsonArray.toString());

            System.out.print("JSON ARRAY PRINTED");
            System.out.println(jsonArray);

            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    hashMapPageDetails.put(jsonArray.getJSONObject(i).getString("name"), new Page(jsonArray.getJSONObject(i)));
                }

                catch (Exception e) {
                    e.printStackTrace();
                }
            }

            return hashMapPageDetails;
        }

    }
}
