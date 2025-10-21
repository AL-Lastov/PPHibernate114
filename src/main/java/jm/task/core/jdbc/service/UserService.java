package jm.task.core.jdbc.service;

import jm.task.core.jdbc.model.User;
import javax.transaction.SystemException;
import java.util.List;

public interface UserService {
    void createUsersTable() throws SystemException;

    void dropUsersTable() throws SystemException;

    void saveUser(String name, String lastName, byte age) throws SystemException;

    void removeUserById(long id) throws SystemException;

    List<User> getAllUsers() throws SystemException;

    void cleanUsersTable() throws SystemException;
}
