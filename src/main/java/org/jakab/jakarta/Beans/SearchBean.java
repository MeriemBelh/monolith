package org.jakab.jakarta.Beans;

import jakarta.annotation.PostConstruct;
import jakarta.ejb.Stateless;
import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.annotation.ManagedProperty;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.ws.rs.PathParam;
import org.jakab.jakarta.data.MenuDao;
import org.jakab.jakarta.data.MenuDaoFactory;
import org.jakab.jakarta.domain.MenuItem;
import org.jakab.jakarta.service.OrderService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Named("search")
@RequestScoped
public class SearchBean {


    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    @PathParam(value="#{param.searchTerm}")
    private String searchTerm;

    public List<MenuItem> getItems() {
        return items;
    }

    private List<MenuItem> items;

    @Inject
    private OrderService service;


    @PostConstruct
    public void init() {

        items = service.searchMenuByItem("chicken");
    }


}
