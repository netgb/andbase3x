package com.andbase.library.http.model;

import com.andbase.library.util.AbJsonUtil;

import java.util.HashMap;

/**
 * JSON请求参数
 */
public class AbJsonRequestParams extends AbJsonParams {

    /** url参数. */
    private HashMap<String, String> urlParams = new HashMap<String, String>();

    /** json参数. */
    private HashMap<String, Object> jsonParams = new HashMap<String, Object>();


	public AbJsonRequestParams() {
		super();
	}

    public void put(String key,String value) {
        this.urlParams.put(key,value);
    }

    public void put(String key,int value) {
        this.urlParams.put(key,String.valueOf(value));
    }

    public String get(String  key) {
        return urlParams.get(key);
    }

    public Object getJsonParam(String  key) {
        return jsonParams.get(key);
    }

    public void putJsonParam(String key,Object value) {
        this.jsonParams.put(key,value);
    }

    public String getUrlParam(String  key) {
        return urlParams.get(key);
    }

    public void putUrlParam(String key,String value) {
        this.urlParams.put(key,value);
    }

    public HashMap<String, String> getUrlParams() {
        return urlParams;
    }

    public void setUrlParams(HashMap<String, String> urlParams) {
        this.urlParams = urlParams;
    }

    public HashMap<String, Object> getJsonParams() {
        return jsonParams;
    }

    public void setJsonParams(HashMap<String, Object> jsonParams) {
        this.jsonParams = jsonParams;
    }

    @Override
    public String getJson() {
        return AbJsonUtil.toJson(jsonParams);
    }
}
