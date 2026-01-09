package org.example.springstarterproject.exception;

public class EntityNotFoundException extends RuntimeException {

    public EntityNotFoundException(Long id) {
    }

    public EntityNotFoundException(String message) {
    }
}

