package com.example.ossp.adapters;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Our system's expected data format for student authentication.
 */
@Getter
@Setter
@AllArgsConstructor
public class StudentAuthData {
    private String matricolNumber;
    private String fullName;
    private String email;
}
