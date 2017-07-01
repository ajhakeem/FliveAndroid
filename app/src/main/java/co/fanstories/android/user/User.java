package co.fanstories.android.user;

/**
 * Created by mohsal on 7/1/17.
 */

import android.util.Log;

import org.json.JSONException;

import java.util.Map;

/**
 * Created by mohsal on 6/2/17.
 */

public class User {
    public String email;
    public String type;
    public Boolean hasSocialAccount;
    public Boolean hasPages;
    public Boolean isSuspended;
    public long exp;
    public String role;

    public User(Token token) throws Exception {
        Map<String, Object> tokenMap = token.decode();
        this.email = tokenMap.get("email").toString();
        this.type = tokenMap.get("type").toString();
        this.hasSocialAccount = tokenMap.get("has_social_account").toString() == "1";
        this.hasPages = tokenMap.get("has_pages").toString() == "1";;
        this.isSuspended = tokenMap.get("isSuspended").toString() == "1";;
        this.exp = Long.parseLong(tokenMap.get("exp").toString());
        this.role = tokenMap.get("role").toString();
    }

    public String getEmail() {
        return email;
    }

    public String getType() {
        return type;
    }

    public Boolean getHasSocialAccount() {
        return hasSocialAccount;
    }

    public Boolean getHasPages() {
        return hasPages;
    }

    public Boolean getIsSuspended() {
        return isSuspended;
    }

    public long getExp() {
        return exp;
    }

    public String getRole() {
        return role;
    }
}

