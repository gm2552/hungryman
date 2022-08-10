package com.java.example.tanzu.hungryman.test;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Value;

import com.java.example.tanzu.hungryman.model.Search;

import lombok.val;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

@Slf4j
public class LoadTester extends SpringBaseTest
{
	@Value("${hungryman.loadTest.searchesToCreate:100}")
	protected int searchesToCreate;
	
	protected Search createRandomSearch()
	{
		var search = new Search();
		
		search.setName(UUID.randomUUID().toString());
		search.setPostalCode("58349");
		search.setRadius(10);
		search.setStartTime(Instant.now().toEpochMilli());
		search.setEndTime(Instant.now().plus(24, ChronoUnit.HOURS).toEpochMilli());
		
		return search;
	}
	
	@Test
	public void createRunValidateLoadTest()
	{
		/* 
		 * Setup a source of randomly created searches.  No delay, just sent them
		 * as fast as possiblity.
		 */
		final Flux<Search> searches = Flux.generate((SynchronousSink<Search> synchronousSink) -> {
			   synchronousSink.next(createRandomSearch());
			});
		
		
		// Send the searches and collect them
		log.info("Creating and sending {} searches", searchesToCreate);
		
		
		val newSearches = searches.take(searchesToCreate)
		.flatMap(search -> 
		{
			return searchClient.addSearch(search);
		}).collectList().block();
				
		assertEquals(searchesToCreate, newSearches.size());
		
		log.info("Search creation complete.  Sleeping to let some searches process.");
		
		/*
		 *	Wait a few seconds for the system to process some searches.  It is
		 *  not expected that all searches will be processed yet.
		 */
		try 
		{
		    TimeUnit.SECONDS.sleep(5);
		} 
		catch (InterruptedException ie) 
		{
		    Thread.currentThread().interrupt();
		}
		
		/*
		 * Start hitting the availability API.  It's OK if nothing is returned.  
		 */
		
		log.info("Waking up and checking for availability.");
		
		Flux.fromIterable(newSearches)
		.flatMap(search -> 
		{
			return availClient.getSearchAvailabilty(search.getName())
			.doOnNext(avail -> log.info("Search {} found {} potential openings at {}", search.getName(), avail.getAvailabilityWindows().size(), avail.getDiningName()))
			.onErrorResume(e -> 
			{
				log.error("Error condition observed for search {}: {}",  search.getName(), e.getMessage());
				return Mono.empty();
			});
			
		}).collectList().block();
		
	}
}
