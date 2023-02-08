/*
 * Copyright (c) 2003, 2005, 2008, 2012 Jonathan J. Halliday
 * (csc3103dev@the-transcend.com)
 * for the School of Computing Science, Newcastle University, UK.
 * (http://www.cs.ncl.ac.uk)
 */
package uk.ac.ncl.cs.csc3103.webstore.datamodel;

import java.util.Collections;
import java.util.Map;

import javax.persistence.*;

/**
 * Provides utility methods for managing JPA EntityManagers on a per-thread basis.
 * This is a JPA variation on the popular HibernateUtil model for supporting the
 * ThreadLocal Session pattern.
 *
 * @author Jonathan J. Halliday (csc3103dev@the-transcend.com)
 * @since 2003
 */
public final class PersistenceUtil {
    private static final EntityManagerFactory entityManagerFactory;

    private static ThreadLocal<EntityManager> threadEntityManager = new ThreadLocal<EntityManager>();

    static {
        try {
            Map<String, String> configOverrides = Collections.emptyMap();
            entityManagerFactory = Persistence.createEntityManagerFactory("csc3103", configOverrides);
        } catch (Throwable ex) {
            ex.printStackTrace();
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Shut down the factory. After calling this method the class is unusable until reloaded.
     */
    public static void closeFactory() {
        entityManagerFactory.close();
    }

    /**
     * Provide an EntityManager, with a transaction active on it.
     * Note: JPA does not behave well unless it has
     * a transaction active, so providing an EntityManager without
     * one just causes hassle most of the time.
     *
     * @return a valid, transacted JPA session
     */
    public static EntityManager getEntityManager() {
        EntityManager entityManager = threadEntityManager.get();
        if (entityManager == null) {
            entityManager = entityManagerFactory.createEntityManager();
            threadEntityManager.set(entityManager);
        }

        ensureTransaction(entityManager);

        return entityManager;
    }

    /**
     * Cleanup a session. Terminates the transaction first if there is one.
     *
     * @param commitTransaction If true, an attempt will be made to
     *                          commit the transaction, otherwise it will be rolled back.
     */
    public static void close(boolean commitTransaction) {
        EntityManager entityManager = threadEntityManager.get();
        threadEntityManager.set(null);
        if (entityManager != null && entityManager.isOpen()) {

            EntityTransaction entityTransaction = entityManager.getTransaction();
            if (entityTransaction.isActive()) {
                if (commitTransaction) {
                    entityTransaction.commit();
                } else {
                    entityTransaction.rollback();
                }
            }

            entityManager.close();
        }
    }

    /**
     * Ensure a transaction is active on the entity Manager by starting one if needed.
     *
     * @param entityManager The EntityManager to check.
     */
    private static void ensureTransaction(EntityManager entityManager) {
        EntityTransaction entityTransaction = entityManager.getTransaction();
        if (!entityTransaction.isActive()) {
            entityTransaction.begin();
        }
    }

    /**
     * Set pagination limits on the provided query i.e. filter to retrieve only a contiguous subset of results.
     *
     * @param query          the Query to set pagination on
     * @param firstRowNumber counting from one
     * @param lastRowNumber  counting from one
     */
    public static void setPagination(Query query, int firstRowNumber, int lastRowNumber) {
        
    	//So set the first result to be the firstRowNumber - 1
    	query.setFirstResult(firstRowNumber-1);
    	
    	//Set the max number of results to be lastRowNumber - (firstRowNumber - 1)
    	//E.g. Rows 2 to 10 (9 rows in total) 10 - 1 = 9
    	query.setMaxResults(lastRowNumber - (firstRowNumber - 1));
    }
}