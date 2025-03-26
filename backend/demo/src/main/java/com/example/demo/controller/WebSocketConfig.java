//处理指定路径的ws连接，并注册处理器

package com.example.demo.controller;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.lang.NonNull;

@Configuration//配置类
@EnableWebSocket//开启websocket支持
public class WebSocketConfig implements WebSocketConfigurer {

    private final WebSocketVisualizer webSocketVisualizer;
    private final ChatWebSocketHandler chatWebSocketHandler;

    public WebSocketConfig(WebSocketVisualizer webSocketVisualizer, ChatWebSocketHandler chatWebSocketHandler) {
        this.webSocketVisualizer = webSocketVisualizer;
        this.chatWebSocketHandler = chatWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(@NonNull WebSocketHandlerRegistry registry) {
        registry.addHandler(webSocketVisualizer, "/sort-updates")
                .setAllowedOrigins("*"); 

        registry.addHandler(chatWebSocketHandler, "/chat-updates")
                .setAllowedOrigins("*"); 
    }
}
