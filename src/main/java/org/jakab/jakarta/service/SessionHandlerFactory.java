package org.jakab.jakarta.service;

public class SessionHandlerFactory {

    private static SessionHandlerService handler;

    public static SessionHandlerService getHandler() {
        if (handler == null) handler = new SessionHandlerService();
        return handler;
    }
}