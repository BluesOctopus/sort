package com.example.demo.service;

import org.springframework.stereotype.Service;
import com.example.demo.model.ChatRequest;
import com.example.demo.controller.ChatWebSocketHandler;
import java.util.Collections;
import java.util.List;

@Service
public class BigModelService {

    private final BigModelNew bigModelNew;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public BigModelService(BigModelNew bigModelNew, ChatWebSocketHandler chatWebSocketHandler) {
        this.bigModelNew = bigModelNew;
        this.chatWebSocketHandler = chatWebSocketHandler;
        this.bigModelNew.setResponseCallback(new BigModelNew.ResponseCallback() {
            @Override
            public void onResponse(String response) {
                chatWebSocketHandler.sendMessage(response);
            }

            @Override
            public void onError(String error) {
                chatWebSocketHandler.sendMessage("Error: " + error);
            }
        });
    }

    public void processChat(ChatRequest chatRequest) {
        List<String> messages = chatRequest.getMessages();
        if (messages == null || messages.isEmpty()) {
            throw new IllegalArgumentException("没有收到消息。");
        }
        String latestMessage = messages.get(messages.size() - 1);
        bigModelNew.sendMessage(latestMessage);
    }
}