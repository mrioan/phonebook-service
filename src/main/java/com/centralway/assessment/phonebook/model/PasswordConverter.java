package com.centralway.assessment.phonebook.model;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Encrypts passwords so that they are not stored in plain-text in the DB.
 */
@Converter
public class PasswordConverter implements AttributeConverter<String, String> {

    private PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    @Override
    public String convertToDatabaseColumn(String plainTextPassword) {
        return passwordEncoder.encode(plainTextPassword);
    }

    @Override
    public String convertToEntityAttribute(String encryptedPassword) {
        //we do not want to decode passwords :)
        return encryptedPassword;
    }

}