package com.company;

public class Task {
    private String request;
    private Client client;

    Task(String request, Client client) {
        this.request = request;
        this.client = client;
    }

    public String getRequest() {
        return request;
    }

    public Client getClient() {
        return client;
    }
}
