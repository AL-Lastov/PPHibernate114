package jm.task.core.jdbc;


import jm.task.core.jdbc.model.User;

import jm.task.core.jdbc.service.UserService;
import jm.task.core.jdbc.service.UserServiceImpl;
import jm.task.core.jdbc.util.Util;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        UserService userService = null;

        try {
            userService = new UserServiceImpl();

            userService.createUsersTable();


            userService.saveUser("hhh", "uuu", (byte) 3);
            userService.saveUser("yyy", "ttt", (byte) 4);
            userService.saveUser("ddd", "mmm", (byte) 200);
            userService.saveUser("", "", (byte) 200);


            System.out.println("Все:");
            List<User> users = userService.getAllUsers();

            System.out.println("Удаляем:");
            userService.removeUserById(1L);


            System.out.println("После удаления:");
            userService.getAllUsers();


            userService.cleanUsersTable();


            Util.getInstance().closeSessionFactory();

        } catch (Exception e) {
            System.err.println("Ошибка при работе с приложением: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
