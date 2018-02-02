package com.cardinalsolutions.conference.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cardinalsolutions.conference.model.Speaker;

@RestController
public class SpeakerService {

	@RequestMapping("/speakers")
	public List<Speaker> speakers() {
		
		List<Speaker> speakers = new ArrayList<Speaker>();
		Speaker speaker = new Speaker();
		speaker.setFirstName("Scott");
		speaker.setLastName("Morgan");
		speakers.add(speaker);
		
		speaker = new Speaker();
		speaker.setFirstName("Rowan");
		speaker.setLastName("Morgan");
		speakers.add(speaker);
		
		speaker = new Speaker();
		speaker.setFirstName("Georgia");
		speaker.setLastName("Morgan");
		speakers.add(speaker);
		
		speaker = new Speaker();
		speaker.setFirstName("Aaron");
		speaker.setLastName("Morgan");
		speakers.add(speaker);
		
		return speakers;
	}
}
