package com.cardinalsolutions.conference.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import lombok.Getter;
import lombok.Setter;

@Entity
public class Speaker {

	@Getter @Setter @Id @GeneratedValue(strategy=GenerationType.AUTO) private Long id;
	@Getter @Setter @Column(name="firstname") private String firstName;
	@Getter @Setter @Column(name="lastname") private String lastName;
	@Getter @Setter @Column(name="bio") private String bio;

}
