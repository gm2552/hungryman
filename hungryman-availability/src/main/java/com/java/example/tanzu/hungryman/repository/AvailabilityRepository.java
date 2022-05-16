package com.java.example.tanzu.hungryman.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.java.example.tanzu.hungryman.entity.Availability;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AvailabilityRepository extends ReactiveCrudRepository<Availability, Long>
{
	public Mono<Availability> findBySearchNameAndDiningName(String searchName, String diningName);
	
	public Flux<Availability> findBySearchName(String searchName);
}
