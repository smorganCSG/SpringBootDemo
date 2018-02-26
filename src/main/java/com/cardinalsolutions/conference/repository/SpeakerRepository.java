/**
 * 
 */
package com.cardinalsolutions.conference.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;


import com.cardinalsolutions.conference.model.Speaker;

/**
 * @author scott
 *
 */

public interface SpeakerRepository extends CrudRepository<Speaker, Long> {

	List<Speaker> findByLastName(String lastName);
}
