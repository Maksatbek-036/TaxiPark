package com.example.taxiclient;

import com.google.gson.Gson;

import java.io.IOException;
import java.util.Dictionary;
import java.util.Hashtable;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkClient extends  OkHttpClient {
private OkHttpClient client;
String url="https://localhost:5001/client";
public String requestLogin(String login,String password) throws IOException {
    String url="https://localhost:5001/client?login="+login+"&password="+password;
    Request request=new Request.Builder().url(url).build();
    Response response=client.newCall(request).execute();
    return response.body().string();
}
    public void requestRegister(String login,String password) {



        Gson json=new Gson();
        var data=new Data(login,password);

        RequestBody body = RequestBody.create(json.toJson(data), MediaType.get("application/json; charset=utf-8"));
       try {
           Request request = new Request.Builder()
                   .url(url)
                   .post(body)
                   .build();
       } catch (Exception e) {
           throw new RuntimeException(e);
       }



    }


}
class Data {
    private final String login;
    private  final String password;


    Data(String login, String password) {
        this.login = login;
        this.password = password;
    }
}

