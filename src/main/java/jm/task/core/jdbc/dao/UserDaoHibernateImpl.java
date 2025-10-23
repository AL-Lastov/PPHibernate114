package jm.task.core.jdbc.dao;

import jm.task.core.jdbc.model.User;
import jm.task.core.jdbc.util.Util;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import java.util.List;

public class UserDaoHibernateImpl implements UserDao {
    private final Util util;


    private static final String CREATE_TABLE =
            "CREATE TABLE IF NOT EXISTS users (" +
                    "id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY, " +
                    "name VARCHAR(50) NOT NULL, " +
                    "last_name VARCHAR(50) NOT NULL, " +
                    "age TINYINT)";

    private static final String DROP_TABLE = "DROP TABLE IF EXISTS users";
    private static final String TRUNCATE_TABLE = "TRUNCATE TABLE users";
    private static final String GET_ALL_USERS = "SELECT * FROM users"; // Изменил на HQL


    public UserDaoHibernateImpl() {
        this.util = Util.getInstance();
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
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
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
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
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
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
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
            }
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }

        }
    }

    @Override
    public List<User> getAllUsers() {
        try (Session session = getSessionFactory().openSession()) {
            return session.createQuery("FROM User", User.class).getResultList();
        } catch (Exception e) {
            throw new RuntimeException("Нет Юзеров для вывода", e);
        }
    }

    @Override
    public void cleanUsersTable() {
        Transaction transaction = null;
        try (Session session = getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.createNativeQuery(TRUNCATE_TABLE).executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null && transaction.isActive()) {
                transaction.rollback();
            }
        }
    }
}

