package org.jakab.jakarta.Beans;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jakab.jakarta.domain.Order;
import org.jakab.jakarta.service.OrderService;
import org.jakab.jakarta.service.SessionHandlerFactory;
import org.jakab.jakarta.service.SessionHandlerService;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.net.URL;
import java.io.*;


@Named("orderReceived")
@RequestScoped
public class OrderReceivedBean {

    private Map<Integer, String> itemQuantities;

    @Inject
    private OrderService service;

    @Inject
    private SessionHandlerService handler;


    @PostConstruct
    public void init() {
        handler = SessionHandlerFactory.getHandler();
        itemQuantities = new HashMap<>();
    }



    public String placeOrder() {
        // Convert itemQuantities to JSON format
        // Convert itemQuantities to JSON format
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        String customerName = request.getUserPrincipal().getName();
        String jwtToken = (String) context.getExternalContext().getSessionMap().get("accessToken");
        String refreshToken = (String) context.getExternalContext().getSessionMap().get("refreshToken");
        System.out.println(jwtToken);
        String items = convertToJson(itemQuantities, customerName ); // Use a method to convert Integer keys to Long
        System.out.println("Order JSON: " + items);

        //check if jwtToken is not expired
        if (isTokenExpired(jwtToken)) {
            jwtToken = refreshAccessToken(refreshToken);
            if (jwtToken == null) {
                return "login?faces-redirect=true"; // Redirect to login if token refresh fails
            }
            context.getExternalContext().getSessionMap().put("accessToken", jwtToken);
        }
        try {
            //String url = "http://localhost:8081/orderConfirmation";
            //String queryParams = "?items=" + URLEncoder.encode(items, "UTF-8") + "&token=" + URLEncoder.encode(jwtToken, "UTF-8");
            // Make an HTTP POST request to the microservice endpoint
            //URL url = new URL(reactAppUrl + queryParams);
            URL url = new URL("http://localhost:9001/api/orders");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + jwtToken);
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);


            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = items.getBytes("utf-8");
                os.write(input, 0, input.length);
            }


            // Get the response code (optional)
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Successfully placed order in microservice
            } else {
                // Handle unsuccessful response
            }
        } catch (Exception e) {
            // Handle exception
        }

        // Proceed with your existing logic
        Order order = service.placeOrder(itemQuantities);
        handler.newOrder(order);

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        session.setAttribute("orderId", order.getId());

        return "thankYou?faces-redirect=true";
    }

    public String convertToJson(Map<Integer, String> itemQuantities, String customerName) {
        Map<String, Object> jsonMap = new HashMap<>();
        Map<Integer, String> filteredItems = new HashMap<>();

        // Filter items with quantity > 0
        for (Map.Entry<Integer, String> entry : itemQuantities.entrySet()) {
            Integer itemId = entry.getKey();
            String quantity = entry.getValue();
            if (!quantity.isEmpty() && Integer.parseInt(quantity) > 0) {
                filteredItems.put(itemId, quantity);
            }
        }

        jsonMap.put("items", filteredItems);
        jsonMap.put("customer", customerName);

        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.writeValueAsString(jsonMap);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            return null;
        }
    }

    private boolean isTokenExpired(String token) {
            try {
                URL url = new URL("http://localhost:9002/api/auth/isExpired");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                String jsonPayload = "{\"token\":\"" + token + "\"}";
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonPayload.getBytes("utf-8");
                    os.write(input, 0, input.length);
                }

                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    try (InputStream responseStream = connection.getInputStream()) {
                        ObjectMapper mapper = new ObjectMapper();
                        Map<String, Boolean> responseMap = mapper.readValue(responseStream, Map.class);
                        return responseMap.get("isExpired");
                    }
                } else {
                    System.out.println("Failed to check token expiration. Response code: " + responseCode);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true; // If anything goes wrong, treat the token as expired

    }

    private String refreshAccessToken(String refreshToken) {

        try {
            URL url = new URL("http://localhost:9002/api/authorisations/refresh");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            String jsonPayload = "{\"refreshToken\":\"" + refreshToken + "\"}";
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                try (InputStream responseStream = connection.getInputStream()) {
                    ObjectMapper mapper = new ObjectMapper();
                    Map<String, String> responseMap = mapper.readValue(responseStream, Map.class);
                    return responseMap.get("accessToken");
                }
            } else {
                System.out.println("Failed to refresh token. Response code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }








    public Map<Integer, String> getItemQuantities() {
        return itemQuantities;
    }

    public void setItemQuantities(Map<Integer, String> itemQuantities) {
        this.itemQuantities = itemQuantities;
    }
}