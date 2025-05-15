package com.example.course.web.rest;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/api")
public class ChatbotResource {

    private static final Logger log = LoggerFactory.getLogger(ChatbotResource.class);

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate;

    public ChatbotResource(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/chat")
    public ResponseEntity<Map<String, String>> chat(@RequestBody Map<String, List<Map<String, String>>> request) {
        List<Map<String, String>> messages = request.get("messages");

        if (messages == null || messages.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Xabarlar ro'yxati bo'sh bo'lmasligi kerak"));
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");
            headers.set("Authorization", "Bearer " + openAiApiKey);

            // API so‘rovini tuzish
            Map<String, Object> body = Map.of(
                "model",
                "gpt-3.5-turbo",
                "messages",
                List.of(
                    Map.of(
                        "role",
                        "system",
                        "content",
                        "Sen foydalanuvchiga bu sayt haqida tushuntiradigan chatbot ekansan. Savollariga aniq, foydali va qisqa javob ber."
                    ),
                    messages
                )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            // OpenAI API’ga so‘rov yuborish
            ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.openai.com/v1/chat/completions",
                HttpMethod.POST,
                entity,
                Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("choices")) {
                log.error("OpenAI API javobi noto‘g‘ri: {}", responseBody);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'choices' kaliti yo‘q"));
            }

            // Choices ro‘yxatini olish
            Object choicesObj = responseBody.get("choices");
            if (!(choicesObj instanceof List)) {
                log.error("OpenAI API javobi noto‘g‘ri: 'choices' ro‘yxat emas: {}", choicesObj);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'choices' ro‘yxat emas"));
            }

            List<?> choices = (List<?>) choicesObj;
            if (choices.isEmpty()) {
                log.error("OpenAI API javobi noto‘g‘ri: 'choices' ro‘yxati bo‘sh");
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'choices' ro‘yxati bo‘sh"));
            }

            // Birinchi choice’ni olish
            Object choice = choices.get(0);
            if (!(choice instanceof Map)) {
                log.error("OpenAI API javobi noto‘g‘ri: 'choice' Map emas: {}", choice);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'choice' Map emas"));
            }

            Map<?, ?> choiceMap = (Map<?, ?>) choice;
            if (!choiceMap.containsKey("message")) {
                log.error("OpenAI API javobi noto‘g‘ri: 'message' kaliti yo‘q: {}", choiceMap);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'message' kaliti yo‘q"));
            }

            // Message’ni olish
            Object messageObj = choiceMap.get("message");
            if (!(messageObj instanceof Map)) {
                log.error("OpenAI API javobi noto‘g‘ri: 'message' Map emas: {}", messageObj);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'message' Map emas"));
            }

            Map<?, ?> messageMap = (Map<?, ?>) messageObj;
            if (!messageMap.containsKey("content")) {
                log.error("OpenAI API javobi noto‘g‘ri: 'content' kaliti yo‘q: {}", messageMap);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'content' kaliti yo‘q"));
            }

            // Content’ni olish
            Object content = messageMap.get("content");
            if (!(content instanceof String)) {
                log.error("OpenAI API javobi noto‘g‘ri: 'content' String emas: {}", content);
                return ResponseEntity.status(500).body(Map.of("message", "Javob topilmadi: 'content' String emas"));
            }

            String message = (String) content;
            return ResponseEntity.ok(Map.of("message", message));
        } catch (Exception e) {
            log.error("OpenAI API xatosi: {}", e.getMessage(), e);
            return ResponseEntity.status(500).body(Map.of("message", "Serverda xatolik yuz berdi: " + e.getMessage()));
        }
    }
}
