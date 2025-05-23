package com.kafka.retry.publisher;

import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import com.kafka.retry.dto.User;

import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
public class KafkaMessagePublisher
{
	private final KafkaTemplate<String, Object> kafkaTemplate;
	private final String topicName;

	public KafkaMessagePublisher(KafkaTemplate<String, Object> kafkaTemplate, @Value("${app.topic.name}") String topicName)
	{
		this.kafkaTemplate = kafkaTemplate;
		this.topicName = topicName;
	}

	public void sendEvents(User user)
	{
		try
		{
			CompletableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicName, user);
			future.whenComplete((result, ex) ->
			{
				if(ex == null)
				{
					log.info("Sent message=[" + user.toString() +
							"] with offset=[" + result.getRecordMetadata().offset() + "]");
				}
				else
				{
					log.error("Unable to send message=[" +
							user.toString() + "] due to : " + ex.getMessage());
				}
			});
		}
		catch(Exception ex)
		{
			log.error(ex.getMessage());
		}
	}

}
