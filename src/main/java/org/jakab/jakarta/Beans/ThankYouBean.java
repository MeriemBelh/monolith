package org.jakab.jakarta.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.annotation.security.DeclareRoles;
import jakarta.annotation.security.RolesAllowed;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.jakab.jakarta.service.OrderService;
import org.jakab.jakarta.service.SessionHandlerService;

import java.text.DecimalFormat;
import java.util.Map;

@Named("thankYou")
@RequestScoped
public class ThankYouBean {

    private Long orderId;

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    private String customer;
    private String status;

    private Double total;

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    private String accessToken;

    @Inject
    private OrderService service;



    @PostConstruct
    public void init() {

        FacesContext facesContext = FacesContext.getCurrentInstance();
        HttpSession session = (HttpSession) facesContext.getExternalContext().getSession(true);
        HttpServletRequest request = (HttpServletRequest) facesContext.getExternalContext().getRequest();
        Long id = (Long) session.getAttribute("orderId");
        String token = (String) session.getAttribute("accessToken");
        String user = (String) session.getAttribute("customer");
        setCustomer(user);
        setOrderId(id);
        setAccessToken(token);
        status = service.getStatus(id);
        total = service.getOrderTotal(id);

    }

    public String getStatus() {
        return status;
    }

    public Double getTotal() {
        return total;
    }

    public String getTotalFormatted() {
        DecimalFormat df = new DecimalFormat("#.##"); // Define the format to have two decimal places
        return df.format(total);
    }

    public String refreshStatus(){
        return service.getStatus(orderId);
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Long getOrderId() {
        return orderId;
    }

}