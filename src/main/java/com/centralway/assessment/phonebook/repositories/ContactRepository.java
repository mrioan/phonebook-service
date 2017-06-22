package com.centralway.assessment.phonebook.repositories;

import com.centralway.assessment.phonebook.model.Contact;
import com.centralway.assessment.phonebook.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Repository for Contact data implemented using Spring Data JPA.
 */
public interface ContactRepository extends CrudRepository<Contact, Long> {

    List<Contact> findAllByUser(User user);
}