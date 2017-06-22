package com.centralway.assessment.phonebook.services;

import com.centralway.assessment.phonebook.exception.ContactNotFoundException;
import com.centralway.assessment.phonebook.exception.OperationNotAllowedException;
import com.centralway.assessment.phonebook.model.Contact;
import com.centralway.assessment.phonebook.model.Phone;
import com.centralway.assessment.phonebook.model.User;
import com.centralway.assessment.phonebook.repositories.ContactRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.logging.Logger;

/**
 * {@link Contact}-related operations.
 */
@Service
public class ContactService {

    private static final Logger log = Logger.getLogger(ContactService.class.getName());

    @Autowired
    protected ContactRepository contactRepository;

    @Autowired
    UserService userService;

    /**
     * Retrieves the {@link Contact contacts} owned by the given user.
     * @param username the user whose contacts are been sought.
     * @return the list of contacts owned by the given user.
     */
    public Iterable<Contact> getAllFor(String username) {
        User user = userService.findByUsername(username);
        return contactRepository.findAllByUser(user);
    }

    public Contact save(final Contact contact) {
        User user = userService.findByUsername((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        if (contact.getUser() != null && !contact.getUser().getId().equals(user.getId())) {
            throw new OperationNotAllowedException("You are not authorized to perform this operation.");
        }
        contact.setUser(user);
        Contact savedContact = contactRepository.save(contact);
        log.info("Contact has been successfully saved");
        return savedContact;
    }

    public Contact get(Long id) throws ContactNotFoundException {
        User user = userService.findByUsername((String) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Contact contact = contactRepository.findOne(id);
        if (contact == null) {
            throw new ContactNotFoundException(id);
        }
        if (contact.getUser() != null && !contact.getUser().getId().equals(user.getId())) {
            throw new OperationNotAllowedException("You are not authorized to perform this operation.");
        }
        return contact;
    }

    public void delete(Long id) throws ContactNotFoundException {
        this.get(id); //enforces security check
        contactRepository.delete(id);
        log.info(String.format("Contact %d has been successfully deleted.", id));
    }

    public Contact addPhone(Long contactId, Phone phone) throws ContactNotFoundException {
        Contact contact = this.get(contactId); //enforces security check
        contact.addPhone(phone);
        contact = contactRepository.save(contact);
        log.info("Phone has been successfully added to contact " + contactId);
        return contact;
    }

}
