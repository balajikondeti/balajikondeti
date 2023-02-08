/*
 * Copyright (c) 2012 Jonathan J. Halliday
 * (csc3103dev@the-transcend.com)
 * for the School of Computing Science, Newcastle University, UK.
 * (http://www.cs.ncl.ac.uk)
 */
package uk.ac.ncl.cs.csc3103.webstore.datamodel;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import java.util.Collections;
import java.util.List;

/**
 * Functions for data retrieval operations on Artist instances.
 * <p/>
 * Functions on this class may throw runtime exceptions in accordance with
 * JPA EntityManager semantics.
 * <p/>
 * Instances of this class are stateless and hence thread-safe.
 *
 * @author Jonathan J. Halliday (csc3103dev@the-transcend.com)
 * @since 2012-09
 */
public class ArtistDAO {

    /**
     * Returns the Artist corresponding to the provided id.
     *
     * @param id the unique item id.
     * @return a Artist if one exists with the specified id, otherwise null.
     */
    public Artist getArtist(int id) {
        EntityManager entityManager = PersistenceUtil.getEntityManager();
        return entityManager.find(Artist.class, id);
    }

    /**
     * Returns a collection of Artists.
     *
     * @param firstRowNumber of the results to include, counting from one.
     * @param lastRowNumber  of the results to include, counting from one.
     * @return a List of Artists, ordered by id.
     */
    public List<Artist> getArtists(int firstRowNumber, int lastRowNumber) {
        TypedQuery<Artist> query = PersistenceUtil.getEntityManager().createQuery("from Artist a order by a.id", Artist.class);
        PersistenceUtil.setPagination(query, firstRowNumber, lastRowNumber);
        return query.getResultList();
    }

    /**
     * Returns the total number of Artists in the catalog.
     *
     * @return the number of Artists
     */
    public long getNumberOfArtists() {
        TypedQuery<Long> query = PersistenceUtil.getEntityManager().createQuery("select count(*) from Artist", Long.class);
        return query.getSingleResult();
    }

    /**
     * Returns a collection of Artists, populated with the specified
     * section of the results with matching artist's name.
     *
     * @param toFind         the artist's name, or part thereof, to look for.
     * @param firstRowNumber of the results to include, counting from one.
     * @param lastRowNumber  of the results to include, counting from one.
     * @return a List of Artists, ordered by id.
     */
    public List<Artist> getMatchingArtists(String toFind, int firstRowNumber, int lastRowNumber) {
        
    	//First create a parameterised query
    	Query query = PersistenceUtil.getEntityManager().createQuery("SELECT a FROM Artist a WHERE a.name LIKE :toFind");
    	
    	//Set the parameter
    	query.setParameter("toFind", "%" + toFind + "%");
    	
    	//Paginate the query
    	PersistenceUtil.setPagination(query, firstRowNumber, lastRowNumber);
    	
    	//Return the result       	
        return query.getResultList();
    }

    /**
     * Returns the number of Artist matching a search for the specified name.
     *
     * @param toFind the artist's name, or part thereof, to look for.
     * @return the count of matching results.
     */
    public long getNumberOfMatchingArtists(String toFind) {
    	
    	//First create a parameterised query
    	Query query = PersistenceUtil.getEntityManager().createQuery("SELECT a FROM Artist a WHERE a.name LIKE :toFind");
    	
    	//Set the parameter
    	query.setParameter("toFind", "%" + toFind+ "%");    	
    	    
    	//Return the length of the results list
        return query.getResultList().size();
    }
}
