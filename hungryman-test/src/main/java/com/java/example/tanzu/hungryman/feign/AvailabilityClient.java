package com.java.example.tanzu.hungryman.feign;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.java.example.tanzu.hungryman.model.Availability;

import reactivefeign.spring.config.ReactiveFeignClient;
import reactor.core.publisher.Flux;

@ReactiveFeignClient(name = "${hungryman.availability.service.name:hungryman-availability}", url = "${hungryman.availability.service.url:}")
public interface AvailabilityClient 
{
	@GetMapping("/availability/{searchName}")
	public Flux<Availability> getSearchAvailabilty(@PathVariable("searchName") String searchName);
}
