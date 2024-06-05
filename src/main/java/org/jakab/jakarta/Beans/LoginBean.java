package org.jakab.jakarta.Beans;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import jakarta.enterprise.context.SessionScoped;
import jakarta.inject.Inject;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.http.HttpSession;
import org.jakab.jakarta.service.SessionHandlerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

@Named
@RequestScoped
public class LoginBean implements Serializable {

    private String username;
    private String role;


    private String usernameInput;
    private String passwordInput;

    private String accessToken;



    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    private String refreshToken;

    private static final Logger logger = LoggerFactory.getLogger(LoginBean.class);
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @Inject
    private SessionHandlerService handler;

    @PostConstruct
    public void init(){
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        try{
            username = request.getUserPrincipal().getName();
            accessToken = getAccessToken();
            refreshToken = getRefreshToken();


        }catch (NullPointerException e){
            username = "";
        }

    }

    public String login() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {

            request.login(this.usernameInput, this.passwordInput);

            HttpSession session = request.getSession();
            String sessionId = session.getId();




            boolean isUser = request.isUserInRole("user");
            if (isUser) {
                setRole("user");
            }


            String jsonPayload = "{\"sessionId\":\"" + sessionId + "\", \"username\":\"" + usernameInput + "\", \"role\":\"" + role + "\"}";
            try {
                // Make an HTTP POST request to the microservice endpoint
                URL url = new URL("http://localhost:9002/api/sessions");
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type", "application/json");
                connection.setDoOutput(true);

                OutputStream os = connection.getOutputStream();
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);

                // Get the response code (optional)
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStream responseStream = connection.getInputStream();
                    ObjectMapper mapper = new ObjectMapper();
                    //TokenResponse tokenResponse = mapper.readValue(responseStream, TokenResponse.class);
                    Map<String, String> responseMap = mapper.readValue(responseStream, Map.class);
                    this.accessToken = responseMap.get("accessToken");
                    this.refreshToken = getRefreshTokenFromResponse(connection);
                    setAccessToken(accessToken);
                    setRefreshToken(refreshToken);
                    context.getExternalContext().getSessionMap().put("accessToken", accessToken);
                    context.getExternalContext().getSessionMap().put("refreshToken", refreshToken);
                    session.setAttribute("customer", usernameInput);
                    connection.disconnect();
                } else {
                    // Handle unsuccessful response
                    System.out.println("Failed to obtain access token. Response code: " + responseCode);
                }
            } catch (Exception e) {
                // Handle exception
            }
            return "/index.xhtml?faces-redirect=true";

        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Login failed."));
            return "error";

        }

    }

    private static String getRefreshTokenFromResponse(HttpURLConnection connection) {
        String refreshToken = null;
        for (String header : connection.getHeaderFields().keySet()) {
            if (header != null && header.equalsIgnoreCase("Set-Cookie")) {
                for (String cookie : connection.getHeaderFields().get(header)) {
                    if (cookie.startsWith("refreshToken")) {
                        refreshToken = cookie.split(";")[0].split("=")[1];

                    }
                }
            }
        }
        return refreshToken;
    }




    public String logout() {
        FacesContext context = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();

        try {
            request.logout();
            return "/index.xhtml?faces-redirect=true";
        } catch (ServletException e) {
            context.addMessage(null, new FacesMessage("Logout failed."));
            return "error";
        }
    }

    public String getUsernameInput() {
        return usernameInput;
    }

    public void setUsernameInput(String usernameInput) {
        this.usernameInput = usernameInput;
    }

    public String getPasswordInput() {
        return passwordInput;
    }

    public void setPasswordInput(String passwordInput) {
        this.passwordInput = passwordInput;
    }


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

}