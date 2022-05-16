package com.java.example.tanzu.hungryman.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.java.example.tanzu.hungryman.entity.Search;

import reactor.core.publisher.Mono;

public interface SearchRepository extends ReactiveCrudRepository<Search, Long>
{
	public Mono<Search> findByNameIgnoreCase(String name);
} 
