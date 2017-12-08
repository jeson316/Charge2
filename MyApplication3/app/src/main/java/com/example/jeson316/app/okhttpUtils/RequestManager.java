package com.example.jeson316.app.okhttpUtils;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by jeson316 on 2017/10/17.
 */

public class RequestManager {

    private Context context;
    private static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final MediaType MEDIA_TYPE_MARKDOWN = MediaType.parse("text/x-markdown; charset=utf-8");//mdiatype 这个需要和服务端保持一致
    private static final String TAG = RequestManager.class.getSimpleName();
    private static final String BASE_URL = "http://xxx.com/";//主地址，根地址
    private static volatile RequestManager mInstance;//单利引用
    private OkHttpClient mOkHttpClient;//okHttpClient 实例
    private Handler okHttpHandler;//全局处理子线程和主线程通信

    /**
     * 初始化RequestManager的实例
     *
     * @param context
     */
    public RequestManager(Context context) {
        this.context = context;
        mOkHttpClient = new OkHttpClient().newBuilder()
                .connectTimeout(10, TimeUnit.SECONDS) //设置超时时间
                .readTimeout(10, TimeUnit.SECONDS)  //设置读取超时时间
                .writeTimeout(10, TimeUnit.SECONDS) //设置写入超时时间
                .build();
        okHttpHandler = new Handler(context.getMainLooper());
    }


    /**
     * 获取单利引用
     *
     * @param context
     * @return
     */
    public static RequestManager getInstance(Context context) {
        RequestManager requestManager = mInstance;
        if (requestManager == null) {
            synchronized (RequestManager.class) {  // 使用锁保证单例的实现和稳定
                requestManager = new RequestManager(context.getApplicationContext());
                mInstance = requestManager;
            }
        }
        return mInstance;
    }

    //创建请求并添加必须的头信息
    private Request.Builder getRequestBuilder() {
        Request.Builder builder = new Request.Builder()
                .addHeader("Connection", "keep-alive")
                .addHeader("platform", "2")
                .addHeader("phoneModel", Build.MODEL)
                .addHeader("systemVersion", Build.VERSION.RELEASE);
//                .addHeader("appVersion", "1.0.0");

        return builder;
    }


    //封装请求方式 get/post
    public void requsetMethodSyn(String urlPath, RequestType requestType, HashMap<String, String> params) {
        switch (requestType) {
            case TYPE_GET:
                requestGetBySyn(urlPath, params);
                break;
            case TYPE_POST_JSON:
                requestPostBySyn(urlPath, params);
                break;
            case TYPE_POST_FORM:
                requestPostBySynWithForm(urlPath, params);
                break;

        }
    }

    private void requestPostBySynWithForm(String urlPath, HashMap<String, String> params) {

    }

    private void requestPostBySyn(String urlPath, HashMap<String, String> params) {
        try {
            StringBuilder tempParams = new StringBuilder();
            int pos = 0;
            for (String key : params.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                tempParams.append(String.format("%s=%s", key, URLEncoder.encode(params.get(key), "utf-8")));
                pos++;
            }
            //封装url
            String realUrl = String.format("%s%s", BASE_URL, urlPath);
            //创建请求实体对象
            RequestBody requestBody = RequestBody.create(MEDIA_TYPE_JSON, tempParams.toString());
            //创建请求对象
            Request request = getRequestBuilder().url(realUrl).post(requestBody).build();
            //创建一个Call
            Call call = mOkHttpClient.newCall(request);
            //发送请求
            Response response = call.execute();
            if (response.isSuccessful()) {
                //获取返回数据 可以是String，bytes ,byteStream
                Log.e(TAG, "response ----->" + response.body().string());
            }

        } catch (Exception e) {
            Log.e(TAG, "requestPostBySyn: " + e.toString());
        }

    }

    private void requestGetBySyn(String urlPath, HashMap<String, String> params) {
        StringBuilder tempParams = new StringBuilder();
        try {
            //拼接参数
            int pos = 0;
            for (String key : params.keySet()) {
                if (pos > 0) {
                    tempParams.append("&");
                }
                //格式化参数并转码
                tempParams.append(
                        String.format("%s=%s", key, URLEncoder.encode(params.get(key), "utf-8")));
                pos++;
            }
            //封装url
            String requestUrl = String.format("%s/%s/?%s", BASE_URL, urlPath, tempParams.toString());
            //创建请求
            Request request = getRequestBuilder().url(requestUrl).build();
            //创建回调
            final Call call = mOkHttpClient.newCall(request);
            //发送请求
            final Response response = call.execute();
            response.body().toString();

        } catch (Exception e) {
            Log.e(TAG, "requestGetBySyn: " + e.toString());
        }
    }


}
