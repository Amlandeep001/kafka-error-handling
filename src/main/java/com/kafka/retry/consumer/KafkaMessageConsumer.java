package com.kafka.retry.consumer;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.retry.dto.User;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaMessageConsumer
{
	private final ObjectMapper objectMapper;

	public KafkaMessageConsumer(ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	@RetryableTopic(attempts = "4") // 3 topic N-1
	@KafkaListener(topics = "${app.topic.name}", groupId = "${spring.kafka.consumer.groupId}")
	public void consumeEvents(User user, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset)
	{
		try
		{
			log.info("Received: {} from {} offset {}", objectMapper.writeValueAsString(user), topic, offset);
			// validate restricted IP before process the records
			List<String> restrictedIpList = Stream.of("32.241.244.236", "15.55.49.164", "81.1.95.253", "126.130.43.183").collect(Collectors.toList());
			if(restrictedIpList.contains(user.getIpAddress()))
			{
				throw new RuntimeException("Invalid IP Address received !");
			}
		}
		catch(JsonProcessingException e)
		{
			e.printStackTrace();
		}
	}

	@DltHandler
	public void listenDLT(User user, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic, @Header(KafkaHeaders.OFFSET) long offset)
	{
		log.info("DLT Received : {} , from {} , offset {}", user.getFirstName(), topic, offset);
	}
}
