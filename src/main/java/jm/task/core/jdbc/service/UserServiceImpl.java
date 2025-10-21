package jm.task.core.jdbc.service;

import jm.task.core.jdbc.dao.UserDao;
import jm.task.core.jdbc.dao.UserDaoHibernateImpl;
import jm.task.core.jdbc.model.User;

import javax.transaction.SystemException;
import java.util.List;

public class UserServiceImpl implements UserService {
    private static UserServiceImpl instance;
    private UserDao userDao;
    private LoggerService logger;

    public UserServiceImpl() {
        this.userDao = UserDaoHibernateImpl.getInstance();
        this.logger = LoggerService.getInstance();
    }

    public static synchronized UserServiceImpl getInstance() {
        if (instance == null) {
            instance = new UserServiceImpl();
        }
        return instance;
    }

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
        this.logger = LoggerService.getInstance();
    }

    @Override
    public void createUsersTable() throws SystemException {
        logger.debug("Вызов создания таблицы users", "UserServiceImpl", "createUsersTable", null);
        userDao.createUsersTable();
    }

    @Override
    public void dropUsersTable() throws SystemException {
        logger.debug("Вызов удаления таблицы users", "UserServiceImpl", "dropUsersTable", null);
        userDao.dropUsersTable();
    }

    @Override
    public void saveUser(String name, String lastName, byte age) throws SystemException {
        logger.debug("Вызов сохранения пользователя: " + name + " " + lastName,
                "UserServiceImpl", "saveUser", null);
        userDao.saveUser(name, lastName, age);
    }

    @Override
    public void removeUserById(long id) throws SystemException {
        logger.debug("Вызов удаления пользователя с ID: " + id,
                "UserServiceImpl", "removeUserById", id);
        userDao.removeUserById(id);
    }

    @Override
    public List<User> getAllUsers() throws SystemException {
        logger.debug("Вызов получения всех пользователей", "UserServiceImpl", "getAllUsers", null);
        List<User> users = userDao.getAllUsers();
        for (User user : users) {
            System.out.println(user);
        }
        return users;
    }

    @Override
    public void cleanUsersTable() throws SystemException {
        logger.debug("Вызов очистки таблицы users", "UserServiceImpl", "cleanUsersTable", null);
        userDao.cleanUsersTable();
    }
}

