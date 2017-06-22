package com.centralway.assessment.phonebook.controllers;

import java.util.logging.Logger;

import com.centralway.assessment.phonebook.model.Contact;
import com.centralway.assessment.phonebook.model.Phone;
import com.centralway.assessment.phonebook.model.View;
import com.centralway.assessment.phonebook.services.ContactService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

/**
 * A RESTFul controller for accessing {@link Contact} information.
 */
@RestController
@RequestMapping(path = "/contacts", produces = MediaType.APPLICATION_JSON_VALUE)
public class ContactController {

	protected Logger log = Logger.getLogger(ContactController.class.getName());

	@Autowired
	protected ContactService contactService;

	@GetMapping
	@JsonView(View.Summary.class)
	public @ResponseBody Iterable<Contact> getAll(@AuthenticationPrincipal String principal) {
		log.info("Retrieving the list of contacts for " + principal);
		return contactService.getAllFor(principal);
	}

	@PostMapping
	@JsonView(View.Id.class)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public Contact create(@RequestBody final Contact contact) {
		log.info("A new contact is about to be created...");
		return contactService.save(contact);
	}

    @PostMapping("/{contacts_id}")
	@JsonView(View.Id.class)
	@ResponseStatus(value = HttpStatus.OK)
	@ResponseBody
	public Contact update(@PathVariable("contacts_id") Long id, @RequestBody final Contact contact) {
		log.info(String.format("Updating contact %d", id));
		Contact retrievedContact = contactService.get(id);
		retrievedContact.setFirstName(contact.getFirstName());
		retrievedContact.setLastName(contact.getLastName());
		return contactService.save(retrievedContact);
	}

    @DeleteMapping("/{contacts_id}")
    @ResponseStatus(value = HttpStatus.OK)
    @ResponseBody
    public void delete(@PathVariable("contacts_id") Long id) {
        log.info(String.format("Deleting contact %d", id));
        contactService.delete(id);
    }

    @PostMapping("/{contact_id}/entries")
    @ResponseStatus(value = HttpStatus.CREATED)
    @JsonView(View.Id.class)
    @ResponseBody
    public Contact addPhone(@PathVariable("contact_id") Long id, @RequestBody final Phone phone) {
        log.info(String.format("Inserting new phones to contact %d", id));
        return contactService.addPhone(id, phone);
    }

}
