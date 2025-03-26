package com.example.demo.controller;

import com.example.demo.service.BigModelService;
import com.example.demo.model.ChatRequest;
import com.example.demo.model.ChatResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final BigModelService bigModelService;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    public ChatController(BigModelService bigModelService) {
        this.bigModelService = bigModelService;
    }

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest chatRequest) {
        logger.info("收到来自前端的消息: {}", chatRequest.getMessages());
        try {
            bigModelService.processChat(chatRequest);
            return ResponseEntity.ok(new ChatResponse("消息已发送到 GPT。"));
        } catch (Exception e) {
            logger.error("处理聊天请求时发生错误: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ChatResponse("抱歉，服务器遇到错误。"));
        }
    }
} 