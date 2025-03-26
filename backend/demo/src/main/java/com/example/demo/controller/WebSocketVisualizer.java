// 实现排序可视化向前端发送的内容


package com.example.demo.controller;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import com.example.demo.sort.SortingVisualizer;

import org.springframework.lang.NonNull;

@Component
public class WebSocketVisualizer extends TextWebSocketHandler implements SortingVisualizer {

    private WebSocketSession session;

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        this.session = session;
    }
//实现SortingVisualizer
    @Override
    public void onSwap(int i, int j) {
        sendMessage("Swapped:" + i + "," + j);
    }

    @Override
    public void onCompare(int i, int j) {
        sendMessage("Compared:" + i + "," + j);
    }

    @Override
    public void onWrite(int index, int value) {
        sendMessage("Wrote:" + index + "," + value);
    }

    public void sendMessage(String message) {
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
} 