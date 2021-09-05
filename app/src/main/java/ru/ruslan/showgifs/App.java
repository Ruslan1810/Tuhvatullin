package ru.ruslan.showgifs;

import android.app.Application;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;;


public class App extends Application {

    private static QuestApi questApi;
    private Retrofit retrofit;

    @Override
    public void onCreate() {
        super.onCreate();

        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        final OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        OkHttpClient okHttpClient = httpClient.addInterceptor(loggingInterceptor).build();

        retrofit = new Retrofit.Builder()
                .baseUrl("http://developerslife.ru")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        questApi = retrofit.create(QuestApi.class);
    }

    public static QuestApi getApi() {
        return questApi;
    }
}
