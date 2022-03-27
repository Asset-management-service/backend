package com.backend.moamoa;

import com.backend.moamoa.global.config.properties.AppProperties;
import com.backend.moamoa.global.config.properties.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({
		CorsProperties.class,
		AppProperties.class
})
public class MoamoaApplication {

	public static void main(String[] args) {
		SpringApplication.run(MoamoaApplication.class, args);
	}

}
