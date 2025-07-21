package ru.tbank.safedeckteam.safedeckllm.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.tbank.safedeckteam.safedeckllm.dto.QuestionCheckRequestDTO;
import ru.tbank.safedeckteam.safedeckllm.dto.ResponseDTO;

import java.util.logging.Handler;

@Service
public class LLMService {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ResponseDTO checkAnswer(QuestionCheckRequestDTO request) {
        String url = "https://semionmur-safe-deck-llm.hf.space/llm/check-answer";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String json;
        try {
            json = new ObjectMapper().writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        HttpEntity entity = new HttpEntity(json, headers);
        RestTemplate restTemplate = new RestTemplate();
        String res = restTemplate.postForObject(url, entity, String.class);
        JsonNode root;
        try {
            root = objectMapper.readTree(res);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        double ans = root.path("output").asDouble();
        return ResponseDTO.builder().result(ans > 0.9).build();
    }
}
