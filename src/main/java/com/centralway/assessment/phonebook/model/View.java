package com.centralway.assessment.phonebook.model;

/**
 * Class to be used by the @JsonView annotation in order to indicate the view(s) where the annotated property/method is part of.
 */
public class View {
    public interface Summary {}
    public interface Id {}
}
