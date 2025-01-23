package com.javatechie.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.javatechie.dto.User;
import com.javatechie.publisher.KafkaMessagePublisher;
import com.javatechie.util.CsvReaderUtils;

@RestController
@RequestMapping("/producer-app")
public class EventController
{
	private final KafkaMessagePublisher publisher;
	private final CsvReaderUtils csvReaderUtils;

	public EventController(KafkaMessagePublisher publisher, CsvReaderUtils csvReaderUtils)
	{
		this.publisher = publisher;
		this.csvReaderUtils = csvReaderUtils;
	}

	@PostMapping("/publishNew")
	public ResponseEntity<?> publishEvent(@RequestBody User user)
	{
		try
		{
			List<User> users = csvReaderUtils.readDataFromCsv();
			users.forEach(usr -> publisher.sendEvents(usr));
			return ResponseEntity.ok("Message published successfully");
		}
		catch(Exception exception)
		{
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
					.build();
		}
	}
}
