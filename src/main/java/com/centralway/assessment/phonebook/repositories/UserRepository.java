package com.centralway.assessment.phonebook.repositories;

import com.centralway.assessment.phonebook.model.User;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for User data implemented using Spring Data JPA.
 */
public interface UserRepository extends CrudRepository<User, Long> {

    User findOneByUsername(String username);

}