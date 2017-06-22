package com.centralway.assessment.phonebook.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonView;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

/**
 * Persistent Contact entity with JPA and Json markup.
 */
@Entity
@Table(name="CONTACT")
public class Contact {

    @Id
    @GeneratedValue
    @JsonView({View.Summary.class, View.Id.class})
    private long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name="USER_ID")
    private User user;

    @NotNull
    @Column(nullable = false)
    @JsonView(View.Summary.class)
    @JsonProperty("first_name")
    private String firstName;

    @JsonView(View.Summary.class)
    @JsonProperty("last_name")
    private String lastName;

    @OneToMany(mappedBy="owner", cascade = CascadeType.ALL)
    @JsonView(View.Summary.class)
    private List<Phone> phones;

    public List<Phone> getPhones() {
        return phones;
    }

    public void addPhone(Phone phone) {
        if (this.phones == null) {
            this.phones = new ArrayList<>();
        }
        this.phones.add(phone);
        if (phone.getOwner() != this) {
            phone.setOwner(this);
        }
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }

}