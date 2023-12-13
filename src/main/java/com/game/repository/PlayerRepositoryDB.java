package com.game.repository;

import com.game.entity.Player;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.query.NativeQuery;
import org.springframework.stereotype.Repository;

import javax.annotation.PreDestroy;
import java.util.List;
import java.util.Optional;

@Repository(value = "db")
public class PlayerRepositoryDB implements IPlayerRepository {

    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            StandardServiceRegistry standartServiceRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml").build();
            Metadata metadata = new MetadataSources(standartServiceRegistry).getMetadataBuilder().build();
            return metadata.getSessionFactoryBuilder().build();
        } catch (HibernateException he) {
            System.out.println("Session Factory creation failure");
            throw he;
        }
    }

    @Override
    public List<Player> getAll(int pageNumber, int pageSize) {

//        String.format("SELECT * FROM player LIMIT")
        Session session = SESSION_FACTORY.getCurrentSession();
        session.beginTransaction();
        NativeQuery<Player> nativeQuery = session.createNativeQuery("SELECT * FROM player LIMIT :limit OFFSET :offset", Player.class);
        nativeQuery.setParameter("limit", pageSize);
        nativeQuery.setParameter("offset", pageNumber * pageSize);
        List<Player> resultList = nativeQuery.getResultList();
        session.getTransaction().commit();
        return resultList;
    }

    @Override
    public int getAllCount() {
        Session session = SESSION_FACTORY.getCurrentSession();
        session.beginTransaction();
        //String string = "SELECT count(*) FROM player";
        int countUser = session.createNamedQuery("Count_User", Long.class).getSingleResult().intValue();
        session.getTransaction().commit();
        return countUser;
    }

    @Override
    public Player save(Player player) {
        //            PlayerEntity playerSaved = new PlayerEntity();
        Session session = SESSION_FACTORY.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.save(player);
        transaction.commit();
        return player;
    }


    @Override
    public Player update(Player player) {
        Session session = SESSION_FACTORY.getCurrentSession();
        Transaction transaction = session.beginTransaction();
        session.update(player);
        transaction.commit();
        return player;
    }

    @Override
    public Optional<Player> findById(long id) {
        Session session = SESSION_FACTORY.getCurrentSession();
        session.beginTransaction();
        Player player = session.find(Player.class, id);
        session.getTransaction().commit();
        return Optional.of(player);
    }

    @Override
    public void delete(Player player) {
        Session session = SESSION_FACTORY.getCurrentSession();
        session.beginTransaction();
        session.remove(player);

        session.getTransaction().commit();

    }

    @PreDestroy
    public void beforeStop() {
        SESSION_FACTORY.close();
    }
}