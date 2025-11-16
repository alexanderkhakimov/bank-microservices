package com.bank.kafka.producer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class KafkaMessageProducer<T> {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public void publish(String topic, String key, T event) {
        try {
            String json = mapper.writeValueAsString(event);
            kafkaTemplate.send(topic, key, json)
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.info("Опубликовано [{}] в {}", event.getClass().getSimpleName(), topic);
                        } else {
                            log.error("Ошибка публикации {}: {}", topic, ex.getMessage());
                        }
                    });
                    } catch(Exception e){
                log.error("Serialization error", e);
                throw new RuntimeException("Failed to send message", e);
            }
        }
    }