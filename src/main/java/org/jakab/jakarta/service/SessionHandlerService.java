package org.jakab.jakarta.service;

import jakarta.websocket.Session;
import org.jakab.jakarta.data.MenuDao;
import org.jakab.jakarta.data.MenuDaoFactory;
import org.jakab.jakarta.domain.Order;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SessionHandlerService
{
    private List<Session> sessions = new ArrayList<>();

    public void addSession(Session session) {
        sessions.add(session);
        sendAllOrders(session);
    }

    public void removeSession(Session session) {
        sessions.remove(session);
    }

    public void sendMessage(JSONObject message) {
        for (Session session : sessions) {
            try {
                session.getBasicRemote().sendText(message.toString());
            } catch (IOException e) {
                removeSession(session);
            }
        }
    }

    public void sendMessage(JSONObject message, Session session) {
        try {
            session.getBasicRemote().sendText(message.toString());
        } catch (IOException e) {
            removeSession(session);
        }

    }

    public JSONObject generateJSONOrder(Order order) {
        JSONObject json = new JSONObject();
        try {
            json.put("id", order.getId().toString());
            json.put("status", order.getStatus());
            json.put("content", order.toString());
            json.put("action", "add");
            json.put("update", new Date().toString());
            return json;
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

    }



    public void newOrder(Order order) {
        sendMessage(generateJSONOrder(order));
    }

    public void amendOrder(Order order) {
        JSONObject json = new JSONObject();

        try {
            json.put("action", "remove");
            json.put("id", order.getId().toString());
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        sendMessage(json);
        if (!order.getStatus().equals("ready for collection"))
            newOrder(order);
    }

    private void sendAllOrders (Session session) {
        MenuDao dao = MenuDaoFactory.getMenuDao();
        List<Order> orders = dao.getAllOrders();
        for (Order order: orders) {
            if (!order.getStatus().equals("ready for collection"))
                sendMessage(generateJSONOrder(order), session);

        }

    }
}