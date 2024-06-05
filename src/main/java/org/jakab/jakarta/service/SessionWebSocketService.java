package org.jakab.jakarta.service;


import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.jakab.jakarta.data.MenuDao;
import org.jakab.jakarta.data.MenuDaoFactory;
import org.jakab.jakarta.domain.Order;
import org.json.JSONException;
import org.json.JSONObject;

@ServerEndpoint("/SessionManagement")
public class SessionWebSocketService {

    @OnOpen
    public void open(Session session) {
        SessionHandlerService handler = SessionHandlerFactory.getHandler();
        handler.addSession(session);

    }


    @OnClose
    public void close(Session session) {

        SessionHandlerService handler = SessionHandlerFactory.getHandler();
        handler.removeSession(session);
    }


    @OnError
    public void onError(Throwable error) {
        throw new RuntimeException(error);
    }


    @OnMessage
    public void handleMessage(String message, Session session)
    {
        JSONObject json = null;
        try {
            json = new JSONObject(message);
            Long id = json.getLong("id");
            String status = json.getString("status");
            MenuDao menuDao = MenuDaoFactory.getMenuDao();
            menuDao.updateOrderStatus(id,status);

            Order order = menuDao.getOrder(id);
            SessionHandlerService handler = SessionHandlerFactory.getHandler();
            handler.amendOrder(order);

        } catch (JSONException e) {
            throw new RuntimeException(e);
        }


    }
}