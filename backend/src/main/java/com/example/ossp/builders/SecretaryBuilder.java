package com.example.ossp.builders;

import com.example.ossp.entities.Secretary;

public class SecretaryBuilder {
    private final Secretary instance = new Secretary();

    public static SecretaryBuilder builder() {
        return new SecretaryBuilder();
    }

    public SecretaryBuilder id(Long id) {
        instance.setId(id);
        return this;
    }

    public SecretaryBuilder firstName(String firstName) {
        instance.setFirstName(firstName);
        return this;
    }

    public SecretaryBuilder lastName(String lastName) {
        instance.setLastName(lastName);
        return this;
    }

    public SecretaryBuilder email(String email) {
        instance.setEmail(email);
        return this;
    }

    public Secretary build() {
        return instance;
    }
}
