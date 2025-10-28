package com.example.ossp.entities;

import java.time.LocalDate;

public class TransferRequestBuilder {
    private TransferRequest request = new TransferRequest();

    public TransferRequestBuilder setId(Long id) { request.setId(id); return this; }
    public TransferRequestBuilder setStudent(Student student) { request.setStudent(student); return this; }
    public TransferRequestBuilder setCurrentCourse(OptionalCourse currentCourse) { request.setCurrentCourse(currentCourse); return this; }
    public TransferRequestBuilder setRequestedCourse(OptionalCourse requestedCourse) { request.setRequestedCourse(requestedCourse); return this; }
    public TransferRequestBuilder setRequestDate(LocalDate requestDate) { request.setRequestDate(requestDate); return this; }
    public TransferRequestBuilder setStatus(TransferRequest.TransferStatus status) { request.setStatus(status); return this; }

    public TransferRequest build() { return request; }
}
