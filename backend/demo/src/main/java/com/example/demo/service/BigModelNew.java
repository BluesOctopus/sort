// 这个文件是星火大模型自带的实例。。。源文件是可以直接运行的与Spark4.0 Ultra直接对话的控制台程序。我基于这个把main函数删除后嵌入进了项目中。。
// 直接把apiKey给放进了该文件，没做加密处理，请勿滥用。。。


// 文件作用：与gpt对话的主要文件，内含消息处理，生成鉴权url，对话历史管理，回调接口。。这里面用的加密法，多线程，内部类等功能我不是很懂，，，不过能用


package com.example.demo.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;

@Service
public class BigModelNew extends WebSocketListener {
    // 各版本的hostUrl及其对应的domian参数，具体可以参考接口文档 https://www.xfyun.cn/doc/spark/Web.html
    // Spark Lite      https://spark-api.xf-yun.com/v1.1/chat      domain参数为lite
    // Spark Pro       https://spark-api.xf-yun.com/v3.1/chat      domain参数为generalv3
    // Spark Pro-128K  https://spark-api.xf-yun.com/chat/pro-128k  domain参数为pro-128k
    // Spark Max       https://spark-api.xf-yun.com/v3.5/chat      domain参数为generalv3.5
    // Spark Max-32K   https://spark-api.xf-yun.com/chat/max-32k   domain参数为max-32k
    // Spark4.0 Ultra  https://spark-api.xf-yun.com/v4.0/chat      domain参数为4.0Ultra

    public static final String hostUrl = "https://spark-api.xf-yun.com/v4.0/chat";
    public static final String domain = "4.0Ultra";
    public static final String appid = "d26aa433";
    public static final String apiSecret = "OWVjMjJlNjEyY2IwZDVhYWQxMjhkYWFm";
    public static final String apiKey = "8d2ca284cc21928775573e0b85593b28";

    public static List<RoleContent> historyList = new ArrayList<>(); // 对话历史存储集合

    public static String totalAnswer = ""; // 大模型的答案汇总
    public static String newQuestion = "";

    public static final Gson gson = new Gson();

    // 个性化参数
    private String userId;
    private Boolean wsCloseFlag;

    private static Boolean totalFlag = true; // 控制提示用户是否输入
    private final String newProperty;

    // 回调接口
    public interface ResponseCallback {
        void onResponse(String response);
        void onError(String error);
    }

    private ResponseCallback callback;

    public void setResponseCallback(ResponseCallback callback) {
        this.callback = callback;
    }

    // 构造函数，带一个参数
    public BigModelNew(@Value("${bigmodel.newProperty}") String newProperty) {
        this.newProperty = newProperty;
        // 如有必要，初始化其他字段
    }

    // 添加一个公开的方法来发送消息
    public void sendMessage(String question) {
        // 清空之前的答案
        totalAnswer = "";

        // 设置新问题
        newQuestion = question;
        // 构建鉴权 URL
        try {
            String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
            OkHttpClient client = new OkHttpClient.Builder().build();
            String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
            Request request = new Request.Builder().url(url).build();
            client.newWebSocket(request, this);
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        System.out.println("WebSocket 连接已建立。");
        
        // 启动一个新线程来发送消息
        MyThread myThread = new MyThread(webSocket);
        myThread.start();
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        if (myJsonParse.header.code != 0) {
            String errorMsg = "发生错误，错误码为：" + myJsonParse.header.code +
                              "\n本次请求的sid为：" + myJsonParse.header.sid;
            System.out.println(errorMsg);

            if (callback != null) {
                callback.onError(errorMsg);
            }
            webSocket.close(1000, "");
            return;
        }
        List<Text> textList = myJsonParse.payload.choices.text;
        for (Text temp : textList) {
            totalAnswer += temp.content;
        }


        if (myJsonParse.header.status == 2) {
            if (callback != null) {
                callback.onResponse(totalAnswer);
            }

            // 关闭连接并释放资源
            if (canAddHistory()) {
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            } else {
                historyList.remove(0);
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("assistant");
                roleContent.setContent(totalAnswer);
                historyList.add(roleContent);
            }
            wsCloseFlag = true;
            totalFlag = true;
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        super.onFailure(webSocket, t, response);
        try {
            if (null != response) {
                int code = response.code();
                System.out.println("onFailure 代码:" + code);
                System.out.println("onFailure body:" + response.body().string());
                if (101 != code) {
                    System.out.println("连接失败");
                    System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        // 时间
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        // 拼接
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        // SHA256加密
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        // Base64加密
        String sha = Base64.getEncoder().encodeToString(hexDigits);
        // 拼接
        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        // 拼接地址
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder()
                .addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8)))
                .addQueryParameter("date", date)
                .addQueryParameter("host", url.getHost())
                .build();

        return httpUrl.toString();
    }

    // 返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }

    // 定义 RoleContent 为 public static 类
    public static class RoleContent {
        private String role;
        private String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public String getResponse(List<String> messages) {
        if (messages == null || messages.isEmpty()) {
            return "没有收到消息。";
        }
        String latestMessage = messages.get(messages.size() - 1);
        return "消息已发送到 GPT。";
    }

    private boolean canAddHistory() {
        final int MAX_HISTORY_SIZE = 50; // 设置历史记录的最大大小
        return historyList.size() < MAX_HISTORY_SIZE;
    }

    // 内部类，用于处理发送消息
    private class MyThread extends Thread {
        private final WebSocket webSocket;

        public MyThread(WebSocket webSocket) {
            this.webSocket = webSocket;
        }

        @Override
        public void run() {
            try {
                JSONObject requestJson = new JSONObject();

                JSONObject header = new JSONObject();  // header参数
                header.put("app_id", appid);
                header.put("uid", UUID.randomUUID().toString().substring(0, 10));

                JSONObject parameter = new JSONObject(); // parameter参数
                JSONObject chat = new JSONObject();
                chat.put("domain", domain);
                chat.put("temperature", 0.5);
                chat.put("max_tokens", 4096);
                parameter.put("chat", chat);

                JSONObject payload = new JSONObject(); // payload参数
                JSONObject message = new JSONObject();
                JSONArray text = new JSONArray();

                // 最新问题
                RoleContent roleContent = new RoleContent();
                roleContent.setRole("user");
                roleContent.setContent(newQuestion);
                text.add(JSON.toJSON(roleContent));

                message.put("text", text);
                payload.put("message", message);

                requestJson.put("header", header);
                requestJson.put("parameter", parameter);
                requestJson.put("payload", payload);
                webSocket.send(requestJson.toString());
                // 等待服务端返回完毕后关闭
                while (!wsCloseFlag) {
                    Thread.sleep(200);
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}