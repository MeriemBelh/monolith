package org.jakab.jakarta.data;

import java.sql.SQLException;

public class MenuDaoFactory {
    private static MenuDao menuDao;

    public static MenuDao getMenuDao(){
        if (menuDao == null) {
            menuDao = new MenuDao();
        }
        return menuDao;
    }
}
