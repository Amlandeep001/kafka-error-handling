package com.kafka.retry.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.kafka.retry.dto.User;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;

@Component
public class CsvReaderUtils
{
	public List<User> readDataFromCsv()
	{
		try(BufferedReader reader = new BufferedReader(new InputStreamReader(new ClassPathResource("users.csv").getInputStream())))
		{
			CsvToBean<User> csvToBean = new CsvToBeanBuilder<User>(reader)
					.withType(User.class)
					.build();

			return csvToBean.parse();
		}
		catch(IOException e)
		{
			e.printStackTrace();
			// Handle the exception as needed
			return null;
		}
	}
}
