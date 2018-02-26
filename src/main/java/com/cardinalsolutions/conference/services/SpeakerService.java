package com.cardinalsolutions.conference.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cardinalsolutions.conference.model.Speaker;
import com.cardinalsolutions.conference.repository.SpeakerRepository;

@RestController
public class SpeakerService {

	@Autowired
	private SpeakerRepository speakerRepo;
	
	@RequestMapping("/speakers")
	public List<Speaker> speakers() {
		
		
		System.out.println("In call to speakers");
		
		List<Speaker> speakers = speakerRepo.findByLastName("Morgan");
		return speakers;
	}
}
