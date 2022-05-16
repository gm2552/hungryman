package com.java.example.tanzu.hungryman.searcher.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.java.example.tanzu.hungryman.config.StaticDiningAvailability;
import com.java.example.tanzu.hungryman.model.Availability;
import com.java.example.tanzu.hungryman.model.SearchCriteria;
import com.java.example.tanzu.hungryman.searcher.Searcher;

import reactor.core.publisher.Flux;

@Component
public class LocalRandomSearcher implements Searcher
{

	static final long HOUR = 3600 * 1000;
	
	static final long HALF_HOUR = HOUR / 2;

	@Autowired
	protected StaticDiningAvailability staticDining;
	
	@Override
	public Flux<Availability> search(SearchCriteria crit) 
	{		
		// if there is a list of dining names, we want to pick from that list
		final var diningFlux = createRandomDinings(crit);
			
		return diningFlux.map(dining -> createRandomAvailability(dining, crit))	;		
	}

	protected Flux<StaticDiningAvailability.Establishment> createRandomDinings(SearchCriteria crit)
	{
		/*
		 * TODO: If the search criteria has dining names, only choose
		 * random dinings that match the search criteria
		 */
		
		// decide how many we will find
		var rn = new Random();
		final var numDinings = rn.nextInt(staticDining.getEstablishments().size()) + 1;
		
		// create bucket to select from using the dining names
		final List<StaticDiningAvailability.Establishment> bucket = new ArrayList<>(staticDining.getEstablishments());
		
		// create a random list of dining options
		final List<StaticDiningAvailability.Establishment> dinings = new ArrayList<>();
		
		// create our random list
		for (int i = 0; i < numDinings; ++i)
		{
			final var bucketEntryIdx = rn.nextInt(bucket.size());
			dinings.add(bucket.remove(bucketEntryIdx));
		}

		return Flux.fromIterable(dinings);
	}
	
	protected Availability createRandomAvailability(StaticDiningAvailability.Establishment dining, SearchCriteria crit)
	{
		var rn = new Random();
		
		// the chance that no reservations are available will be 20%
		final var isTimesAvailable = (rn.nextInt(10) > 1);
		
		final var avail = new Availability();
		
		if (isTimesAvailable)
		{
			// create a random availability based on the search window
			final var times = new long[2];
			
			// select a random start time... the start time should not be less than an hour before the end time
			var windowSize = crit.getEndTime() - crit.getStartTime();
			
			// if the start and stop times are less than a 1 hour window, then just select
			// the start and end times as the window
			if (windowSize <= HOUR)
			{
				times[0] =  crit.getStartTime();
				times[1] = crit.getEndTime();
			}
			else
			{
				// find a random start time
				var max = crit.getEndTime() - HOUR;
				var min = crit.getStartTime();
				var startTime = min + (long) (Math.random() * (max - min));
				startTime = roundToHalfHour(startTime - HALF_HOUR);
				
				// find a random end time time
				max = crit.getEndTime();
				min = startTime + HALF_HOUR;
				var endTime = min + (long) (Math.random() * (max - min));
				endTime = roundToHalfHour(endTime - HALF_HOUR);
				
				times[0] = startTime;
				times[1] = endTime;	
			}
			
			avail.setAvailabilityWindows(Collections.singletonList(new Availability.AvailabilityWindow(times[0], times[1])));
		}
		
		avail.setSearchName(crit.getName());
		avail.setDiningName(dining.getDiningName());
		avail.setAddress(dining.getAddress());
		avail.setLocality(dining.getLocality());
		avail.setPhoneNumber(dining.getPhoneNumber());
		avail.setPostalCode(dining.getPostalCode());
		avail.setRegion(dining.getRegion());
		avail.setReservationURL(dining.getReservationURL());
		
		return avail;
	}
	
	protected long roundToHalfHour(long time)
	{
		if (time % HALF_HOUR == 0)
			return time;
		
		return (HALF_HOUR - (time % HALF_HOUR)) + time;
	}
}