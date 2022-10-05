package com.java.example.tanzu.hungryman.searcher.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.java.example.tanzu.hungryman.feign.CrawlerClient;
import com.java.example.tanzu.hungryman.model.Availability;
import com.java.example.tanzu.hungryman.model.SearchCriteria;
import com.java.example.tanzu.hungryman.searcher.Searcher;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;

@Component
@Profile("crawler")
@Primary
@Slf4j
public class CrawlerSearcher implements Searcher
{

	@Autowired
	protected CrawlerClient crawlClient;
	
	@Override
	public Flux<Availability> search(SearchCriteria crit) 
	{
		log.info("Making crawler dining search for dining search {}", crit.getName());
		
		return crawlClient.search(crit.getDiningNames(), crit.getDiningTypes(), crit.getStartTime(), crit.getEndTime())
			.map(avail -> 
			{
				avail.setSearchName(crit.getName());
				avail.setRequestSubject(crit.getRequestSubject());
				
				return avail;
			});
			
	}

}
