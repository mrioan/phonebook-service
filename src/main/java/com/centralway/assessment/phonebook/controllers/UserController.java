package com.centralway.assessment.phonebook.controllers;

import com.centralway.assessment.phonebook.model.User;
import com.centralway.assessment.phonebook.model.View;
import com.centralway.assessment.phonebook.services.UserService;
import com.fasterxml.jackson.annotation.JsonView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.AuthorizationRequest;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthorizationServerTokenServices;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.logging.Logger;

/**
 * A RESTFul controller providing account-related operations like registration and authentication.
 */
@RestController
@RequestMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
public class UserController {

    @Value("#{ @environment['phonebook.client.clientid'] ?: 'trusted-app' }")
    private String clientId;

	protected Logger log = Logger.getLogger(UserController.class.getName());

	@Autowired
	protected UserService userService;

	@Autowired
	AuthorizationServerTokenServices defaultAuthorizationServerTokenServices;

	@Autowired
    ClientDetailsService clientDetailsService;

    /**
     * Endpoint that, given a username and password, returns a token required for restricted operations
     * like listing, updating or creating contacts.
     * @param user the user requesting the token
     * @return a JSON like this: { "token": "eyJleHAiOjE0OTgwMTA2OTAsInVzZXJfbmFtZSI6InBldGVyQGVt" }
     */
	@PostMapping
	@JsonView(View.Id.class)
	@ResponseStatus(value = HttpStatus.CREATED)
	@ResponseBody
	public String authenticate(@RequestBody final User user) {
		log.info(String.format("Updating contact %s", user.getUsername()));
		UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword(), Collections.emptyList());
		OAuth2Authentication auth = new OAuth2Authentication(new AuthorizationRequest(clientId, clientDetailsService.loadClientByClientId(clientId).getScope()).createOAuth2Request(), authenticationToken);
		OAuth2AccessToken token = defaultAuthorizationServerTokenServices.createAccessToken(auth);
	    return "{ \"token\": \"" + token.getValue() + "\"}";
	}

    /**
     * New users can be registered via this operation.
     * @param user the user to be created
     * @return the id of the newly created {@link User}.
     */
    @PostMapping("/register")
    @JsonView(View.Id.class)
    @ResponseStatus(value = HttpStatus.CREATED)
    @ResponseBody
    public User create(@RequestBody final User user) {
        Assert.isNull(user.getId(), "Invalid user data: 'id' must be empty in order for the creation to proceed.");
        User newUser = userService.save(user);
        log.info(String.format("A user with username '%s' has been successfully registered.", newUser.getUsername()));
        return newUser;
    }
}
