package com.andbase.library.http;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.andbase.library.asynctask.AbTask;
import com.andbase.library.asynctask.AbTaskItem;
import com.andbase.library.asynctask.AbTaskListener;
import com.andbase.library.cache.disk.AbDiskCacheEntry;
import com.andbase.library.cache.disk.AbDiskCacheImpl;
import com.andbase.library.cache.http.AbHttpCacheResponse;
import com.andbase.library.app.global.AbAppConfig;
import com.andbase.library.http.entity.mine.content.StringBody;
import com.andbase.library.http.listener.AbByteArrayHttpResponseListener;
import com.andbase.library.http.listener.AbFileHttpResponseListener;
import com.andbase.library.http.listener.AbHttpHeaderCreateListener;
import com.andbase.library.http.listener.AbHttpResponseListener;
import com.andbase.library.http.listener.AbStringHttpResponseListener;
import com.andbase.library.http.model.AbHttpException;
import com.andbase.library.http.model.AbHttpStatus;
import com.andbase.library.http.model.AbJsonParams;
import com.andbase.library.http.model.AbJsonRequestParams;
import com.andbase.library.http.model.AbOutputStreamProgress;
import com.andbase.library.http.model.AbRequestParams;
import com.andbase.library.http.ssl.NoSSLTrustManager;
import com.andbase.library.util.AbAppUtil;
import com.andbase.library.util.AbFileUtil;
import com.andbase.library.util.AbLogUtil;
import com.andbase.library.util.AbStrUtil;

import org.apache.http.HttpEntity;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

/**
 * Copyright upu173.com
 * Author 还如一梦中
 * Date 2016/6/14 17:54
 * Email 396196516@qq.com
 * Info Http执行工具类，可处理get，post，以及异步处理文件的上传下载
 */

public class AbHttpUtil {

    /** 上下文. */
    private static Context context;

    /** 请求类型. */
    private static final String HTTP_GET = "GET";
    private static final String HTTP_POST = "POST";
    private static final String HTTP_PUT = "PUT";
    private static final String HTTP_DELETE = "DELETE";

    /** 磁盘缓存. */
    private AbDiskCacheImpl diskCache = null;

    /**################# HTTP Message常量################################*/
    /** 成功. */
    public static final int SUCCESS_MESSAGE = 0;

    /** 失败. */
    public static final int FAILURE_MESSAGE = 1;

    /** 开始. */
    public static final int START_MESSAGE = 4;

    /** 进行中. */
    public static final int PROGRESS_MESSAGE = 6;

    /** 任务. */
    private List<AbTask> taskList = null;

    /** Session ID  全局的，新的HttpUtil会自动使用这个值. */
    public static String sessionId = null;

    /** 请求头. */
    private HashMap<String,String> headerMap = null;

    /** 请求自定义. */
    private AbHttpHeaderCreateListener httpHeaderCreateListener = null;

    /**
     * 构造函数，初始化.
     * @param context the context
     */
    public AbHttpUtil(Context context) {
        this.context = context;
        this.diskCache = AbDiskCacheImpl.getInstance(context);
        this.taskList = new ArrayList<AbTask>();
        this.headerMap = new HashMap<String,String>();
        this.headerMap.put("SecurityCode",AbAppConfig.httpSecurityCode);
    }

    /**
     * 获取实例.
     *
     * @param context the context
     * @return single instance of AbHttpUtil
     */
    public static AbHttpUtil getInstance(Context context){
        return new AbHttpUtil(context);
    }


    /**
     * 发送get请求.
     *
     * @param url the url
     * @param responseListener the response listener
     */
    public AbTask get(final String url,final AbHttpResponseListener responseListener){
       return get(url,null,responseListener);
    }


    /**
     * 发送get请求.
     * @param url the url
     * @param params the params
     * @param responseListener the response listener
     */
    public AbTask get(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {

        responseListener.setHandler(new ResponderHandler(responseListener));
        responseListener.onStart();
        AbTask task = AbTask.newInstance();
        taskList.add(task);
        AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListener(){
            @Override
            public void get() {
                try {
                    doRequest(url,HTTP_GET,params,responseListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        task.execute(item);
        return task;
    }

    /**
     * 发送post请求
     * @param url
     * @param responseListener
     * @return
     */
    public AbTask post(final String url,final AbHttpResponseListener responseListener) {
        return request(url,HTTP_POST,null,responseListener);
    }

    /**
     * 发送post请求
     * @param url
     * @param params
     * @param responseListener
     * @return
     */
    public AbTask post(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {
        return request(url,HTTP_POST,params,responseListener);
    }

    /**
     * 发送put请求
     * @param url
     * @param params
     * @param responseListener
     * @return
     */
    public AbTask put(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {
        return request(url,HTTP_PUT,params,responseListener);
    }

    /**
     * 发送delete请求.
     * @param url the url
     * @param params the params
     * @param responseListener the response listener
     */
    public AbTask delete(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {
        return request(url,HTTP_DELETE,params,responseListener);
    }

    /**
     * 发送post请求
     * @param url
     * @param params
     * @param responseListener
     * @return
     */
    public AbTask postJson(final String url,final String params,final AbStringHttpResponseListener responseListener) {
        return postJsonValue(url,HTTP_POST,params,responseListener);
    }

    /**
     * 发送Json POST请求
     * @param url
     * @return
     */
    public AbTask postJson(final String url,final AbJsonParams params, final AbStringHttpResponseListener responseListener) {
        return requestJson(url,HTTP_POST,params,responseListener);
    }

    /**
     * 发送Json PUT请求
     * @param url
     * @return
     */
    public AbTask putJson(final String url,final AbJsonParams params, final AbStringHttpResponseListener responseListener) {
        return requestJson(url,HTTP_PUT,params,responseListener);
    }

    /**
     * 发送请求
     * @param url
     * @param requestMethod
     * @param params
     * @param responseListener
     * @return
     */
    public AbTask request(final String url,final String requestMethod,final AbRequestParams params,final AbHttpResponseListener responseListener) {
        responseListener.setHandler(new ResponderHandler(responseListener));
        responseListener.onStart();
        final AbTask task = AbTask.newInstance();
        taskList.add(task);
        AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListener(){
            @Override
            public void get() {
                try {
                    doRequest(url,requestMethod,params,responseListener);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void update() {
                taskList.remove(task);
            }
        });
        task.execute(item);

        return task;
    }

    /**
     * 发送get请求(有缓存).
     * @param url the url
     * @param responseListener the response listener
     */
    public AbTask getWithCache(final String url,final AbHttpResponseListener responseListener) {
        return getWithCache(url,null,responseListener);
    }


    /**
     * 发送get请求(有缓存).
     * @param url the url
     * @param params the params
     * @param responseListener the response listener
     */
    public AbTask getWithCache(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {

        responseListener.setHandler(new ResponderHandler(responseListener));
        responseListener.onStart();
        final AbTask task = AbTask.newInstance();
        taskList.add(task);
        AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListener(){

            @Override
            public void update() {
                taskList.remove(task);
            }

            @Override
            public void get() {
                String httpUrl = url;
                try {
                    if(params!=null && params.size()[0] > 0){

                        for (ConcurrentHashMap.Entry<String, String> entry : params.getUrlParams().entrySet()) {
                            String key = "{" + entry.getKey() +"}";
                            if(httpUrl.contains(key)){
                                httpUrl = httpUrl.replace(key,entry.getValue());
                                params.getUrlParams().remove(entry.getKey());
                            }
                        }

                        if(params!=null && params.size()[0] > 0){

                            if(params.getUrlParams().size() > 0  && httpUrl.indexOf("?")==-1){
                                httpUrl += "?";
                            }
                            httpUrl += params.getParamString();
                        }

                    }

                    AbLogUtil.e(context,"[HTTP]:on start:" + httpUrl);

                    //查看本地缓存
                    final String cacheKey = diskCache.getCacheKey(httpUrl);

                    //看磁盘
                    AbDiskCacheEntry entry = diskCache.get(cacheKey);

                    if(!AbAppUtil.isNetworkAvailable(context)){
                        //没网络

                        if(entry == null){
                            //缓存不存在
                            AbLogUtil.i(AbHttpUtil.class, "无网络，磁盘中无缓存文件");

                            Thread.sleep(200);
                            responseListener.sendFailureMessage(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION, new AbHttpException(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION));
                            return;
                        }else{
                            AbLogUtil.i(AbHttpUtil.class, "无网络，磁盘中有缓存文件");
                            //磁盘中有数据
                            byte [] httpData = entry.data;
                            String responseBody = new String(httpData);
                            ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                            AbLogUtil.i(context, "[HTTP GET CACHED]:"+httpUrl+",result："+responseBody);

                        }
                    }else{

                        //有网络先下载，下载失败返回缓存
                        AbHttpCacheResponse response = diskCache.getCacheResponse(httpUrl,sessionId);

                        if(response!=null){
                            String responseBody = new String(response.data);
                            AbLogUtil.i(context, "[HTTP GET]:"+httpUrl+",result："+responseBody);
                            AbDiskCacheEntry entryNew = diskCache.parseCacheHeaders(response,AbAppConfig.DISK_CACHE_EXPIRES_TIME);
                            if(entryNew!=null){
                                diskCache.put(cacheKey,entryNew);
                                AbLogUtil.i(context, "HTTP 缓存成功");
                            }else{
                                AbLogUtil.i(context, "HTTP 缓存失败，parseCacheHeaders失败");
                            }

                            ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                        }else{
                            if(entry == null){
                                //缓存不存在
                                AbLogUtil.i(AbHttpUtil.class, "有网络，磁盘中无缓存文件");
                                responseListener.sendFailureMessage(AbHttpStatus.SERVER_FAILURE_CODE, AbAppConfig.REMOTE_SERVICE_EXCEPTION,new AbHttpException(AbHttpStatus.SERVER_FAILURE_CODE, AbAppConfig.REMOTE_SERVICE_EXCEPTION));
                            }else{
                                if(entry.isExpired()){
                                    //缓存过期
                                    AbLogUtil.i(AbHttpUtil.class, "有网络，磁盘中缓存已经过期");
                                    responseListener.sendFailureMessage(AbHttpStatus.SERVER_FAILURE_CODE, AbAppConfig.REMOTE_SERVICE_EXCEPTION,new AbHttpException(AbHttpStatus.SERVER_FAILURE_CODE, AbAppConfig.REMOTE_SERVICE_EXCEPTION));
                                }else{
                                    //磁盘中有数据
                                    byte [] httpData = entry.data;
                                    String responseBody = new String(httpData);
                                    ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                                    AbLogUtil.i(context, "[HTTP GET CACHED]:"+httpUrl+",result："+responseBody);
                                }

                            }

                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        task.execute(item);

        return task;
    }

    /**
     * 发送get请求(有缓存),根据超时时间.
     *
     * @param url the url
     * @param params the params
     * @param responseListener the response listener
     */
    public AbTask getWithCacheLess(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {

        responseListener.setHandler(new ResponderHandler(responseListener));
        responseListener.onStart();
        final AbTask task = AbTask.newInstance();
        taskList.add(task);
        AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListener(){

            @Override
            public void update() {
                taskList.remove(task);
            }


            @Override
            public void get() {
                try {

                    String httpUrl = url;
                    if(params!=null && params.size()[0] > 0){

                        for (ConcurrentHashMap.Entry<String, String> entry : params.getUrlParams().entrySet()) {
                            String key = "{" + entry.getKey() +"}";
                            if(httpUrl.contains(key)){
                                httpUrl = httpUrl.replace(key,entry.getValue());
                                params.getUrlParams().remove(entry.getKey());
                            }
                        }

                        if(params!=null && params.size()[0] > 0){

                            if(params.getUrlParams().size() > 0  && httpUrl.indexOf("?")==-1){
                                httpUrl += "?";
                            }
                            httpUrl += params.getParamString();
                        }
                    }

                    AbLogUtil.e(context,"[HTTP]:on start:" + httpUrl);

                    //查看本地缓存
                    final String cacheKey = diskCache.getCacheKey(httpUrl);

                    //看磁盘
                    AbDiskCacheEntry entry = diskCache.get(cacheKey);

                    if(!AbAppUtil.isNetworkAvailable(context)){
                        //没网络

                        if(entry == null){
                            //缓存不存在
                            AbLogUtil.i(AbHttpUtil.class, "无网络，磁盘中无缓存文件");

                            Thread.sleep(200);
                            responseListener.sendFailureMessage(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION, new AbHttpException(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION));
                            return;
                        }else{
                            AbLogUtil.i(AbHttpUtil.class, "无网络，磁盘中有缓存文件");
                            //磁盘中有数据
                            byte [] httpData = entry.data;
                            String responseBody = new String(httpData);
                            ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                            AbLogUtil.i(context, "[HTTP GET CACHED]:"+httpUrl+" ,result："+responseBody);

                        }
                    }else{

                        if(entry == null || entry.isExpired()){

                            //有网络先下载，下载失败返回缓存
                            AbHttpCacheResponse response = diskCache.getCacheResponse(httpUrl,sessionId);

                            if(response!=null){
                                String responseBody = new String(response.data);
                                AbLogUtil.i(context, "[HTTP GET]:"+httpUrl+" ,result："+responseBody);
                                AbDiskCacheEntry entryNew = diskCache.parseCacheHeaders(response,AbAppConfig.DISK_CACHE_EXPIRES_TIME);
                                if(entryNew!=null){
                                    diskCache.put(cacheKey,entryNew);
                                    AbLogUtil.i(context, "HTTP 缓存成功");
                                }else{
                                    AbLogUtil.i(context, "HTTP 缓存失败，parseCacheHeaders失败");
                                }

                                ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                            }else{
                                if(entry == null){
                                    //缓存不存在
                                    AbLogUtil.i(AbHttpUtil.class, "有网络，磁盘中无缓存文件");
                                    responseListener.sendFailureMessage(AbHttpStatus.SERVER_FAILURE_CODE, AbAppConfig.REMOTE_SERVICE_EXCEPTION,new AbHttpException(AbHttpStatus.SERVER_FAILURE_CODE, AbAppConfig.REMOTE_SERVICE_EXCEPTION));
                                }else{

                                    //磁盘中有数据
                                    byte [] httpData = entry.data;
                                    String responseBody = new String(httpData);
                                    ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                                    AbLogUtil.i(context, "[HTTP GET CACHED]:"+httpUrl+" ,result："+responseBody);

                                }

                            }
                        }else{
                            //磁盘中有数据
                            byte [] httpData = entry.data;
                            String responseBody = new String(httpData);
                            ((AbStringHttpResponseListener)responseListener).sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, responseBody);
                            AbLogUtil.i(context, "[HTTP GET CACHED]:"+httpUrl+" ,result："+responseBody);
                        }


                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        });
        task.execute(item);

        return task;
    }



    /**
     * 发送Json POST请求
     * @param url
     * @param params
     * @return
     */
    public AbTask requestJson(final String url,final String requestMethod,final AbJsonParams params, final AbStringHttpResponseListener responseListener) {

        responseListener.setHandler(new ResponderHandler(responseListener));
        responseListener.onStart();

        final AbTask task = AbTask.newInstance();
        taskList.add(task);
        AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListener(){

            @Override
            public void update() {
                taskList.remove(task);
            }

            @Override
            public void get() {
                HttpURLConnection httpURLConnection = null;
                InputStream inputStream = null;
                String httpUrl = url;
                try {
                    if(!AbAppUtil.isNetworkAvailable(context)){
                        Thread.sleep(200);
                        responseListener.sendFailureMessage(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION, new AbHttpException(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION));
                        return;
                    }

                    String resultString = null;

                    //支持符号匹配参数
                    if(params!=null && params instanceof AbJsonRequestParams){
                        AbJsonRequestParams jsonRequestParams = (AbJsonRequestParams)params;
                        if(jsonRequestParams.getUrlParams().size() > 0){

                            for (HashMap.Entry<String,String> entry : jsonRequestParams.getUrlParams().entrySet()) {
                                String key = "{" + entry.getKey() +"}";
                                if(httpUrl.contains(key)){
                                    httpUrl = httpUrl.replace(key,entry.getValue());
                                }
                            }
                        }
                    }

                    AbLogUtil.e(context,"[HTTP]:on start:" + httpUrl);

                    httpURLConnection = openConnection(httpUrl);
                    httpURLConnection.setRequestMethod(requestMethod);

                    //请求头
                    if(httpHeaderCreateListener!=null){
                        HashMap<String,String> headerMore = httpHeaderCreateListener.onCreateHeader(httpUrl,requestMethod.toLowerCase());
                        if(headerMore!= null){
                            headerMap.putAll(headerMore);
                        }
                    }
                    Iterator iterator = headerMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String key = (String)entry.getKey();
                        String val = (String)entry.getValue();
                        httpURLConnection.setRequestProperty(key,val);
                    }
                    if(!AbStrUtil.isEmpty(sessionId)){
                        httpURLConnection.setRequestProperty("Cookie", "JSESSIONID="+sessionId);
                    }
                    httpURLConnection.setConnectTimeout(AbAppConfig.DEFAULT_CONNECT_TIMEOUT);
                    httpURLConnection.setReadTimeout(AbAppConfig.DEFAULT_READ_TIMEOUT);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type","application/json");
                    StringBody body = null;
                    if(params!=null){
                        httpURLConnection.setRequestProperty("connection", "keep-alive");
                        body = StringBody.create(params.getJson(),"application/json", Charset.forName("UTF-8"));
                        body.writeTo(httpURLConnection.getOutputStream(),null);
                    }else{
                        httpURLConnection.connect();
                    }

                    if(body!=null){
                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,body:"+params.getJson());
                    }else{
                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,body:无");
                    }

                    int code = httpURLConnection.getResponseCode();
                    if (code == AbHttpStatus.SUCCESS_CODE){
                        inputStream = httpURLConnection.getInputStream();
                        resultString = readString(httpURLConnection,responseListener,true);

                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,result:" + resultString);
                        responseListener.sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, resultString);
                    }else{
                        inputStream = httpURLConnection.getErrorStream();
                        if(inputStream!= null){
                            resultString = readString(inputStream,null,false);
                        }else{
                            resultString = readString(httpURLConnection,null,false);
                        }
                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,failure:" + resultString);
                        AbHttpException exception = new AbHttpException(code,resultString);
                        responseListener.sendFailureMessage(exception.getCode(),exception.getMessage(),exception);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,error："+e.getMessage());
                    //发送失败消息
                    AbHttpException exception = new AbHttpException(e);
                    responseListener.sendFailureMessage(exception.getCode(),exception.getMessage(),exception);
                } finally {
                    try{
                        if(inputStream!=null){
                            inputStream.close();
                        }
                    }catch(Exception e){
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }

        });
        task.execute(item);
        taskList.add(task);
        return task;

    }

    /**
     * 发送Json POST请求
     * @param url
     * @param params
     * @return
     */
    public AbTask postJsonValue(final String url,final String requestMethod,final String params, final AbStringHttpResponseListener responseListener) {

        responseListener.setHandler(new ResponderHandler(responseListener));
        responseListener.onStart();

        final AbTask task = AbTask.newInstance();
        taskList.add(task);
        AbTaskItem item = new AbTaskItem();
        item.setListener(new AbTaskListener(){

            @Override
            public void update() {
                taskList.remove(task);
            }

            @Override
            public void get() {
                HttpURLConnection httpURLConnection = null;
                InputStream inputStream = null;
                String httpUrl = url;
                try {
                    if(!AbAppUtil.isNetworkAvailable(context)){
                        Thread.sleep(200);
                        responseListener.sendFailureMessage(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION, new AbHttpException(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION));
                        return;
                    }

                    String resultString = null;

                    AbLogUtil.e(context,"[HTTP]:on start:" + httpUrl);


                    httpURLConnection = openConnection(httpUrl);
                    httpURLConnection.setRequestMethod(requestMethod);

                    //请求头
                    if(httpHeaderCreateListener!=null){
                        HashMap<String,String> headerMore = httpHeaderCreateListener.onCreateHeader(httpUrl,requestMethod.toLowerCase());
                        if(headerMore!= null){
                            headerMap.putAll(headerMore);
                        }
                    }
                    Iterator iterator = headerMap.entrySet().iterator();
                    while (iterator.hasNext()) {
                        Map.Entry entry = (Map.Entry) iterator.next();
                        String key = (String)entry.getKey();
                        String val = (String)entry.getValue();
                        httpURLConnection.setRequestProperty(key,val);
                    }
                    if(!AbStrUtil.isEmpty(sessionId)){
                        httpURLConnection.setRequestProperty("Cookie", "JSESSIONID="+sessionId);
                    }
                    httpURLConnection.setConnectTimeout(AbAppConfig.DEFAULT_CONNECT_TIMEOUT);
                    httpURLConnection.setReadTimeout(AbAppConfig.DEFAULT_READ_TIMEOUT);
                    httpURLConnection.setDoOutput(true);
                    httpURLConnection.setRequestProperty("Content-Type","application/json");
                    StringBody body = null;
                    if(params!=null){
                        httpURLConnection.setRequestProperty("connection", "keep-alive");
                        body = StringBody.create(params,"application/json", Charset.forName("UTF-8"));
                        body.writeTo(httpURLConnection.getOutputStream(),null);
                    }else{
                        httpURLConnection.connect();
                    }

                    if(body!=null){
                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,body:"+params);
                    }else{
                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,body:无");
                    }

                    int code = httpURLConnection.getResponseCode();
                    if (code == AbHttpStatus.SUCCESS_CODE){
                        inputStream = httpURLConnection.getInputStream();
                        resultString = readString(httpURLConnection,responseListener,true);

                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,result:" + resultString);
                        responseListener.sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, resultString);
                    }else{
                        inputStream = httpURLConnection.getErrorStream();
                        if(inputStream!= null){
                            resultString = readString(inputStream,null,false);
                        }else{
                            resultString = readString(httpURLConnection,null,false);
                        }
                        AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,failure:" + resultString);
                        AbHttpException exception = new AbHttpException(code,resultString);
                        responseListener.sendFailureMessage(exception.getCode(),exception.getMessage(),exception);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,error："+e.getMessage());
                    //发送失败消息
                    AbHttpException exception = new AbHttpException(e);
                    responseListener.sendFailureMessage(exception.getCode(),exception.getMessage(),exception);
                } finally {
                    try{
                        if(inputStream!=null){
                            inputStream.close();
                        }
                    }catch(Exception e){
                    }
                    if (httpURLConnection != null) {
                        httpURLConnection.disconnect();
                    }
                }
            }

        });
        task.execute(item);
        taskList.add(task);
        return task;

    }

    /**
     * 发送get/post请求
     * @param url
     * @param params
     * @param responseListener
     */
    public void doRequest(final String url,final String requestMethod, final AbRequestParams params, final AbHttpResponseListener responseListener) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        String httpUrl = url;
        try {
            if(!AbAppUtil.isNetworkAvailable(context)){
                Thread.sleep(200);
                responseListener.sendFailureMessage(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION, new AbHttpException(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION));
                return;
            }

            if(params!=null && params.size()[0] > 0){

                for (ConcurrentHashMap.Entry<String, String> entry : params.getUrlParams().entrySet()) {
                    String key = "{" + entry.getKey() +"}";
                    if(httpUrl.contains(key)){
                        httpUrl = httpUrl.replace(key,entry.getValue());
                        params.getUrlParams().remove(entry.getKey());
                    }
                }

                if(requestMethod == HTTP_GET){
                    if(params!=null && params.size()[0] > 0){

                        if(params.getUrlParams().size() > 0  && httpUrl.indexOf("?")==-1){
                            httpUrl += "?";
                        }
                        httpUrl += params.getParamString();
                    }
                }
            }

            AbLogUtil.e(context,"[HTTP]:on start:" + httpUrl);

            httpURLConnection = openConnection(httpUrl);
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setUseCaches(false);

            //请求头
            if(httpHeaderCreateListener!=null){
                HashMap<String,String> headerMore = httpHeaderCreateListener.onCreateHeader(httpUrl,requestMethod.toLowerCase());
                if(headerMore!= null){
                    this.headerMap.putAll(headerMore);
                }

            }

            Iterator iterator = this.headerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String)entry.getKey();
                String val = (String)entry.getValue();
                httpURLConnection.setRequestProperty(key,val);
            }

            if(!AbStrUtil.isEmpty(sessionId)){
                httpURLConnection.setRequestProperty("Cookie", "JSESSIONID="+sessionId);
            }

            httpURLConnection.setConnectTimeout(AbAppConfig.DEFAULT_CONNECT_TIMEOUT);
            httpURLConnection.setReadTimeout(AbAppConfig.DEFAULT_READ_TIMEOUT);
            httpURLConnection.setRequestProperty("connection", "keep-alive");
            if(params != null && (requestMethod == HTTP_POST || requestMethod == HTTP_PUT)){
                httpURLConnection.setDoOutput(true);
                //使用NameValuePair来保存要传递的Post参数设置字符集
                HttpEntity httpEntity = params.getEntity();
                //请求httpRequest
                if(params.getFileParams().size()>0){
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + params.boundaryString());
                    long totalSize = httpEntity.getContentLength();
                    httpEntity.writeTo(new AbOutputStreamProgress(httpURLConnection.getOutputStream(),totalSize,responseListener,true));
                }else{
                    //没文件
                    httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    httpEntity.writeTo(httpURLConnection.getOutputStream());
                }
            }else{
                //没参数
                httpURLConnection.connect();
            }

            int code = httpURLConnection.getResponseCode();
            String resultString = "";
            AbLogUtil.e(context,"[HTTP]:Response Code = " + code);
            if (code == AbHttpStatus.SUCCESS_CODE){
                if(responseListener instanceof AbStringHttpResponseListener){
                    //字符串
                    AbStringHttpResponseListener stringHttpResponseListener =  (AbStringHttpResponseListener)responseListener;
                    resultString = readString(httpURLConnection ,responseListener,true);
                    stringHttpResponseListener.sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, resultString);

                }else if(responseListener instanceof AbByteArrayHttpResponseListener){
                    //字节
                    AbByteArrayHttpResponseListener binaryHttpResponseListener =  (AbByteArrayHttpResponseListener)responseListener;
                    byte[] resultByte = readByteArray(httpURLConnection ,responseListener,true);
                    binaryHttpResponseListener.sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, resultByte);

                }else if(responseListener instanceof AbFileHttpResponseListener){
                    //文件
                    AbFileHttpResponseListener fileHttpResponseListener =  (AbFileHttpResponseListener)responseListener;
                    String fileName = AbFileUtil.getCacheFileNameFromUrl(url, httpURLConnection);
                    writeToFile(context,httpURLConnection,fileName,fileHttpResponseListener,true);
                    fileHttpResponseListener.sendSuccessMessage(AbHttpStatus.SUCCESS_CODE, fileName);
                }
                AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,result:" + resultString);
            }else{
                inputStream = httpURLConnection.getErrorStream();
                if(inputStream!= null){
                    resultString = readString(inputStream,null,false);
                }else{
                    resultString = readString(httpURLConnection,null,false);
                }
                if(AbStrUtil.isEmpty(resultString)){
                    if(code ==404){
                        resultString = AbAppConfig.NOT_FOUND_EXCEPTION;
                    }else if(code == 500){
                        resultString = AbAppConfig.REMOTE_SERVICE_EXCEPTION;
                    }else{
                        resultString = AbAppConfig.UNTREATED_EXCEPTION;
                    }
                }
                AbLogUtil.e(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,failure:"+resultString);
                AbHttpException exception = new AbHttpException(code,resultString);
                responseListener.sendFailureMessage(exception.getCode(),exception.getMessage(),exception);
            }

        } catch (Exception e) {
            e.printStackTrace();
            AbLogUtil.e(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,error:"+e.getMessage());
            AbHttpException exception = new AbHttpException(e);
            responseListener.sendFailureMessage(exception.getCode(),exception.getMessage(),exception);
        } finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch(Exception e){
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }

    /**
     * 发送get/post请求(无线程)
     * @param url
     * @param params
     * @param responseListener
     */
    public void doRequestWithoutThread(final String url,final String requestMethod, final AbRequestParams params, final AbHttpResponseListener responseListener) {
        HttpURLConnection httpURLConnection = null;
        InputStream inputStream = null;
        responseListener.onStart();
        String httpUrl = url;
        try {
            if(!AbAppUtil.isNetworkAvailable(context)){
                Thread.sleep(200);
                responseListener.onFailure(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION, new AbHttpException(AbHttpStatus.CONNECT_FAILURE_CODE,AbAppConfig.CONNECT_EXCEPTION));
                return;
            }

            if(params!=null && params.size()[0] > 0){

                for (ConcurrentHashMap.Entry<String, String> entry : params.getUrlParams().entrySet()) {
                    String key = "{" + entry.getKey() +"}";
                    if(httpUrl.contains(key)){
                        httpUrl = httpUrl.replace(key,entry.getValue());
                        params.getUrlParams().remove(entry.getKey());
                    }
                }

                if(requestMethod == HTTP_GET){
                    if(params!=null && params.size()[0] > 0){

                        if(params.getUrlParams().size() > 0  && httpUrl.indexOf("?")==-1){
                            httpUrl += "?";
                        }
                        httpUrl += params.getParamString();
                    }
                }
            }

            AbLogUtil.e(context,"[HTTP]:on start:" + httpUrl);

            httpURLConnection = openConnection(httpUrl);
            httpURLConnection.setRequestMethod(requestMethod);
            httpURLConnection.setUseCaches(false);

            //请求头
            if(httpHeaderCreateListener!=null){
                HashMap<String,String> headerMore = httpHeaderCreateListener.onCreateHeader(httpUrl,requestMethod.toLowerCase());
                if(headerMore!= null){
                    headerMap.putAll(headerMore);
                }
            }
            Iterator iterator = this.headerMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String key = (String)entry.getKey();
                String val = (String)entry.getValue();
                httpURLConnection.setRequestProperty(key,val);
            }
            if(!AbStrUtil.isEmpty(sessionId)){
                httpURLConnection.setRequestProperty("Cookie", "JSESSIONID="+sessionId);
            }
            httpURLConnection.setConnectTimeout(AbAppConfig.DEFAULT_CONNECT_TIMEOUT);
            httpURLConnection.setReadTimeout(AbAppConfig.DEFAULT_READ_TIMEOUT);
            httpURLConnection.setRequestProperty("connection", "keep-alive");
            if(params != null && (requestMethod == HTTP_POST || requestMethod == HTTP_PUT)){
                httpURLConnection.setDoOutput(true);
                //使用NameValuePair来保存要传递的Post参数设置字符集
                HttpEntity httpEntity = params.getEntity();
                //请求httpRequest
                if(params.getFileParams().size()>0){
                    httpURLConnection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + params.boundaryString());
                    long totalSize = httpEntity.getContentLength();
                    httpEntity.writeTo(new AbOutputStreamProgress(httpURLConnection.getOutputStream(),totalSize,responseListener,false));
                }else{
                    //没文件
                    httpURLConnection.setRequestProperty("Content-Type","application/x-www-form-urlencoded");
                    httpEntity.writeTo(httpURLConnection.getOutputStream());
                }
            }else{
                //没参数
                httpURLConnection.connect();
            }

            int code = httpURLConnection.getResponseCode();
            String resultString = "";
            AbLogUtil.e(context,"[HTTP]:Response Code = " + code);
            if (code == AbHttpStatus.SUCCESS_CODE){
                AbLogUtil.e(context,"[HTTP]:onSuccess");
                if(responseListener instanceof AbStringHttpResponseListener){
                    //字符串
                    resultString = readString(httpURLConnection ,responseListener,false);
                    AbStringHttpResponseListener stringHttpResponseListener =  (AbStringHttpResponseListener)responseListener;
                    stringHttpResponseListener.onSuccess(AbHttpStatus.SUCCESS_CODE, resultString);

                }else if(responseListener instanceof AbByteArrayHttpResponseListener){
                    //字节
                    byte[] resultByte = readByteArray(httpURLConnection ,responseListener,false);
                    AbByteArrayHttpResponseListener binaryHttpResponseListener =  (AbByteArrayHttpResponseListener)responseListener;
                    binaryHttpResponseListener.onSuccess(AbHttpStatus.SUCCESS_CODE, resultByte);

                }else if(responseListener instanceof AbFileHttpResponseListener){
                    //文件
                    String fileName = AbFileUtil.getCacheFileNameFromUrl(url, httpURLConnection);
                    AbFileHttpResponseListener fileHttpResponseListener =  (AbFileHttpResponseListener)responseListener;
                    writeToFile(context,httpURLConnection,fileName,fileHttpResponseListener,false);

                    fileHttpResponseListener.onSuccess(AbHttpStatus.SUCCESS_CODE, fileName);
                }

                AbLogUtil.i(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,result:" + resultString);

            }else{

                inputStream = httpURLConnection.getErrorStream();
                if(inputStream!= null){
                    resultString = readString(inputStream,null,false);
                }else{
                    resultString = readString(httpURLConnection,null,false);
                }
                if(AbStrUtil.isEmpty(resultString)){
                    if(code == 404){
                        resultString = AbAppConfig.NOT_FOUND_EXCEPTION;
                    }else if(code == 500){
                        resultString = AbAppConfig.REMOTE_SERVICE_EXCEPTION;
                    }else{
                        resultString = AbAppConfig.UNTREATED_EXCEPTION;
                    }
                }
                AbLogUtil.e(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,failure:"+resultString);
                AbHttpException exception = new AbHttpException(code,resultString);
                AbLogUtil.e(context,"[HTTP]:onFailure");
                responseListener.onFailure(exception.getCode(),exception.getMessage(),exception);
            }
            AbLogUtil.e(context,"[HTTP]:onFinish");
            responseListener.onFinish();
        } catch (Exception e) {
            e.printStackTrace();
            AbLogUtil.e(context, "[HTTP "+requestMethod+"]:"+httpUrl+" ,error:"+e.getMessage());
            //发送失败消息
            AbHttpException exception = new AbHttpException(e);
            responseListener.onFailure(exception.getCode(),exception.getMessage(),exception);
        } finally {
            try{
                if(inputStream!=null){
                    inputStream.close();
                }
            }catch(Exception e){
            }
            if (httpURLConnection != null) {
                httpURLConnection.disconnect();
            }
        }
    }


    /**
     * 将流的数据写入文件并回调进度.
     * @param context the context
     * @param httpURLConnection the httpURLConnection
     * @param name the name
     * @param responseListener the response listener
     */
    private void writeToFile(Context context,HttpURLConnection httpURLConnection,String name,AbFileHttpResponseListener responseListener,boolean isThread){

        if(httpURLConnection == null){
            return;
        }

        responseListener.setFile(context,name);

        InputStream inStream = null;
        FileOutputStream outStream = null;
        try {
            inStream = httpURLConnection.getInputStream();
            int contentLength = httpURLConnection.getContentLength();
            outStream = new FileOutputStream(responseListener.getFile());
            if (inStream != null) {

                byte[] tmp = new byte[1024];
                int l, count = 0;
                while ((l = inStream.read(tmp)) != -1 && !Thread.currentThread().isInterrupted()) {
                    count += l;
                    outStream.write(tmp, 0, l);
                    if(isThread){
                        responseListener.sendProgressMessage(count, contentLength);
                    }else{
                        responseListener.onProgress(count,contentLength);
                    }
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                if(inStream!=null){
                    inStream.close();
                }
                if(outStream!=null){
                    outStream.flush();
                    outStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 从流中读取字符串
     * @param httpURLConnection
     * @param responseListener
     * @return
     */
    private String readString(HttpURLConnection httpURLConnection,AbHttpResponseListener responseListener,boolean isThread) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            InputStream is = httpURLConnection.getInputStream();
            if (is != null) {
                //一定要是字符流  否则中文被截取成字节后会坏掉
                InputStreamReader isr = new InputStreamReader(is,"UTF-8");
                int len = 0;
                char[] buffer = new char[1024];
                while((len = isr.read(buffer)) > 0){
                    stringBuffer.append(new String(buffer, 0,len));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    /**
     * 从流中读取字符串
     * @param inputStream
     * @param responseListener
     * @return
     */
    private String readString(InputStream inputStream,AbHttpResponseListener responseListener,boolean isThread) {
        StringBuffer stringBuffer = new StringBuffer();
        try {
            if (inputStream != null) {
                //一定要是字符流  否则中文被截取成字节后会坏掉
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                int len = 0;
                char[] buffer = new char[1024];
                while((len = inputStreamReader.read(buffer)) > 0){
                    stringBuffer.append(new String(buffer, 0,len));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return stringBuffer.toString();
    }

    /**
     * 从流中获取字节数据
     * @param httpURLConnection
     * @param responseListener
     * @return
     */
    private byte[] readByteArray(HttpURLConnection httpURLConnection,AbHttpResponseListener responseListener,boolean isThread) {

        InputStream inStream = null;
        ByteArrayOutputStream outSteam = null;
        try {
            inStream = httpURLConnection.getInputStream();
            outSteam = new ByteArrayOutputStream();
            int contentLength = httpURLConnection.getContentLength();
            if (inStream != null) {
                int l, count = 0;
                byte[] buffer = new byte[1024];
                while((l = inStream.read(buffer))!=-1){
                    count += l;
                    outSteam.write(buffer,0,l);
                    if(isThread){
                        responseListener.sendProgressMessage(count, contentLength);
                    }else{
                        responseListener.onProgress(count,contentLength);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(inStream!=null){
                    inStream.close();
                }
                if(outSteam!=null){
                    outSteam.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return outSteam.toByteArray();

    }

    /**
     * 响应处理
     */
    private static class ResponderHandler extends Handler {

        /** 响应数据. */
        private Object[] response;

        /** 响应消息监听. */
        private AbHttpResponseListener responseListener;

        /**
         * 响应消息处理.
         *
         * @param responseListener the response listener
         */
        public ResponderHandler(AbHttpResponseListener responseListener) {
            this.responseListener = responseListener;
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {

                case SUCCESS_MESSAGE:
                    AbLogUtil.e(context,"[HTTP]:onSuccess");
                    response = (Object[]) msg.obj;

                    if (response != null){
                        if(responseListener instanceof AbStringHttpResponseListener){
                            AbStringHttpResponseListener stringHttpResponseListener =  (AbStringHttpResponseListener)responseListener;
                            if(response.length >= 2){
                                stringHttpResponseListener.onSuccess((Integer) response[0],(String)response[1]);
                            }else{
                                AbLogUtil.i(context, "SUCCESS_MESSAGE "+AbAppConfig.MISSING_PARAMETERS);
                            }

                        }else if(responseListener instanceof AbByteArrayHttpResponseListener){
                            AbByteArrayHttpResponseListener binaryHttpResponseListener =  (AbByteArrayHttpResponseListener)responseListener;
                            if(response.length >= 2){
                                binaryHttpResponseListener.onSuccess((Integer) response[0],(byte[])response[1]);
                            }else{
                                AbLogUtil.i(context, "SUCCESS_MESSAGE "+AbAppConfig.MISSING_PARAMETERS);
                            }
                        }else if(responseListener instanceof AbFileHttpResponseListener){
                            AbFileHttpResponseListener fileHttpResponseListener =  (AbFileHttpResponseListener)responseListener;
                            if(response.length >= 1){
                                fileHttpResponseListener.onSuccess((Integer) response[0],fileHttpResponseListener.getFile());
                            }else{
                                AbLogUtil.i(context, "SUCCESS_MESSAGE "+AbAppConfig.MISSING_PARAMETERS);
                            }

                        }
                    }
                    AbLogUtil.e(context,"[HTTP]:onFinish");
                    responseListener.onFinish();
                    break;
                case FAILURE_MESSAGE:

                    response = (Object[]) msg.obj;
                    if (response != null && response.length >= 3){
                        //异常转换为可提示的
                        AbHttpException exception = new AbHttpException((Exception) response[2]);
                        AbLogUtil.e(context,"[HTTP]:onFailure");
                        responseListener.onFailure((Integer) response[0], (String) response[1], exception);
                    }else{
                        AbLogUtil.i(context, "FAILURE_MESSAGE "+AbAppConfig.MISSING_PARAMETERS);
                    }
                    AbLogUtil.e(context,"[HTTP]:onFinish");
                    responseListener.onFinish();

                    break;
                case START_MESSAGE:
                    AbLogUtil.e(context,"[HTTP]:onStart");
                    responseListener.onStart();
                    break;
                case PROGRESS_MESSAGE:
                    response = (Object[]) msg.obj;
                    if (response != null && response.length >= 2){
                        responseListener.onProgress((Long) response[0], (Long) response[1]);
                    }else{
                        AbLogUtil.i(context, "PROGRESS_MESSAGE "+AbAppConfig.MISSING_PARAMETERS);
                    }
                    break;
                default:
                    break;
            }
        }

    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 发送get请求（无线程）.
     *
     * @param url the url
     * @param params the params
     * @param responseListener the response listener
     */
    public void getWithoutThread(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {
        doRequestWithoutThread(url,HTTP_GET,params,responseListener);
    }

    /**
     * 发送post请求（无线程）.
     *
     * @param url the url
     * @param params the params
     * @param responseListener the response listener
     */
    public void postWithoutThread(final String url,final AbRequestParams params,final AbHttpResponseListener responseListener) {
        doRequestWithoutThread(url,HTTP_POST,params,responseListener);
    }

    /**
     * 取消当前所有
     */
    public void cancelCurrentTask(){
        try{
            AbTask task = null;
            for(int i =0;i<taskList.size();i++){
                task = taskList.get(i);
                task.cancel(true);
                taskList.remove(task);
                i--;
            }
            AbLogUtil.e("AbHttpUtil","[AbHttpUtil]取消了当前任务");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 获取连接 自验证  验证
     * @param url
     * @return
     * @throws Exception
     */
    public static HttpURLConnection openConnection(String url) throws Exception {
        HttpURLConnection httpURLConnection = null;
        URL requestUrl = new URL(url);
        if (!requestUrl.getProtocol().toLowerCase().equals("https")) {
            httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
            return httpURLConnection;
        }

        if(AbAppConfig.trustMode == 0){
            TrustManager[] trustAllCerts = new TrustManager[1];
            TrustManager tm = new NoSSLTrustManager();
            trustAllCerts[0] = tm;
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((HostnameVerifier) tm);

        }else if(AbAppConfig.trustMode == 1){
            SSLContext sslContext = SSLContext.getInstance("TLS");
            TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            trustManagerFactory.init(getKeyStore(AbAppConfig.caRes));
            sslContext.init( null, trustManagerFactory.getTrustManagers(), new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession sslsession) {

                    if("localhost".equals(hostname)){
                        return true;
                    } else {
                        return false;
                    }
                }
            });

        }

        httpURLConnection = (HttpURLConnection) requestUrl.openConnection();
        return httpURLConnection;

    }


    public static KeyStore getKeyStore(int raw){
        KeyStore keyStore = null;
        InputStream inputStream = null;
        try {
            keyStore = KeyStore.getInstance("BKS");
            // 从资源文件中读取你自己创建的那个包含证书的 keystore 文件
            //这个参数改成你的 keystore 文件名
            inputStream = context.getResources().openRawResource(raw);
            // 用 keystore 的密码跟证书初始化 trusted
            keyStore.load(inputStream, AbAppConfig.caPassword.toCharArray());
        }catch(Exception e){
            e.printStackTrace();
        } finally {
            try {
                inputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return keyStore;
    }

    public void addHeader(String key,String value){
        this.headerMap.put(key,value);
    }

    public void removeHeader(String key){
        this.headerMap.remove(key);
    }

    public void clearHeaders(){
        this.headerMap.clear();
    }

    public void setHttpHeaderCreateListener(AbHttpHeaderCreateListener httpHeaderCreateListener) {
        this.httpHeaderCreateListener = httpHeaderCreateListener;
    }
}
