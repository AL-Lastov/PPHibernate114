package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.service.LoggerService;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import javax.transaction.SystemException;
import java.util.ArrayList;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "age TINYINT)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS users";
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE users";
    private static final String GET_ALL_USERS = "SELECT * FROM users"; // Изменил на HQL

    private static UserDaoHibernateImpl instance;
    private final Util util;
    private final LoggerService logger;

    private UserDaoHibernateImpl() {
        this.util = Util.getInstance();
        this.logger = LoggerService.getInstance();
    }

    public static synchronized UserDaoHibernateImpl getInstance() {
        if (instance == null) {
            instance = new UserDaoHibernateImpl();
        }
        return instance;
    }

    private SessionFactory getSessionFactory() {
        return util.getSessionFactory();
    }

    @Override
    public void createUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(CREATE_TABLE).executeUpdate();
            transaction.commit();
            logger.info("Таблица users создана", "UserDaoHibernateImpl", "createUsersTable", null);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            try {
                logger.error("Таблица не создана:" + e.getMessage(),
                        "UserDaoHibernateImpl", "createUsersTable", null);
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void dropUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(DROP_TABLE).executeUpdate();
            transaction.commit();
            logger.info("Таблица users удалена", "UserDaoHibernateImpl", "dropUsersTable", null);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            try {
                logger.error("Таблица не удалена" + e.getMessage(),
                        "UserDaoHibernateImpl", "dropUsersTable", null);
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public void saveUser(String name, String lastName, byte age) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = new User(name, lastName, age);
            session.persist(user);
            transaction.commit();
            logger.info("User с именем " + name + " добавлен с ID: " + user.getId(),
                    "UserDaoHibernateImpl", "saveUser", user.getId());
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            try {
                logger.error("User не сохранен" + e.getMessage(),
                        "UserDaoHibernateImpl", "saveUser", null);
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException(e);
        }
    }

    @Override
    public void removeUserById(long id) {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.get(User.class, id);
            if (user != null) {
                session.remove(user);
                logger.info("User с ID " + id + " удален...",
                        "UserDaoHibernateImpl", "removeUserById", id);
            } else {
                logger.warn("User а с ID " + id + " нет",
                        "UserDaoHibernateImpl", "removeUserById", id);
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            try {
                logger.error("User не удален" + e.getMessage(),
                        "UserDaoHibernateImpl", "removeUserById", id);
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
        }
    }

    @Override
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            users = session.createNativeQuery(GET_ALL_USERS, User.class).getResultList();
            transaction.commit();
            logger.info("Получили " + users.size() + " Users",
                    "UserDaoHibernateImpl", "getAllUsers", null);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            try {
                logger.error("Не нашли Users" + e.getMessage(),
                        "UserDaoHibernateImpl", "getAllUsers", null);
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
        }
        return users;
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(TRUNCATE_TABLE).executeUpdate();
            transaction.commit();
            logger.info("Таблица users очищена", "UserDaoHibernateImpl", "cleanUsersTable", null);
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
            try {
                logger.error("Таблица users не очищена" + e.getMessage(),
                        "UserDaoHibernateImpl", "cleanUsersTable", null);
            } catch (SystemException ex) {
                throw new RuntimeException(ex);
            }
        }
    }
}
