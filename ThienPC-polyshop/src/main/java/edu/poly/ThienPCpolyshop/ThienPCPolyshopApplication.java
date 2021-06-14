package edu.poly.ThienPCpolyshop;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

import edu.poly.ThienPCpolyshop.config.StorageProperties;
import edu.poly.ThienPCpolyshop.service.StorageService;
import groovyjarjarpicocli.CommandLine.Command;

@SpringBootApplication
@EnableConfigurationProperties(StorageProperties.class)
public class ThienPCPolyshopApplication {

	public static void main(String[] args) {
		SpringApplication.run(ThienPCPolyshopApplication.class, args);
	}
	
	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args->{
			storageService.init();
		});
	}

}
