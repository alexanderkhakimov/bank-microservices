package com.bank.kafka.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

@RequiredArgsConstructor
@Slf4j
public abstract class KafkaMessageConsumer<T> {

    private final ObjectMapper mapper = new ObjectMapper();

    @KafkaListener(
            topics = "${kafka.topic}",
            groupId = "${kafka.group}",
            containerFactory = "kafkaListenerContainerFactory"
    )
    public void listen(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            T event = mapper.readValue(record.value(), getEventType());
            log.info("Получено событие [{}] из топика {}", event.getClass().getSimpleName(), record.topic());
            process(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Ошибка обработки события, будет повтор", e);
        }
    }

    protected abstract void process(T event);
    protected abstract Class<T> getEventType();
}
