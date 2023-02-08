/*
 * Copyright (c) 2012 Jonathan J. Halliday
 * (csc3103dev@the-transcend.com)
 * for the School of Computing Science, Newcastle University, UK.
 * (http://www.cs.ncl.ac.uk)
 */
package uk.ac.ncl.cs.csc3103.webstore.web;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import org.eclipse.jetty.server.Request;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import uk.ac.ncl.cs.csc3103.webstore.datamodel.*;

/**
 * The application controller component of the
 * web store's MVC architecture.
 *
 * @author Jonathan J. Halliday (csc3103dev@the-transcend.com)
 * @since 2012-09
 */
@SuppressWarnings("serial")
public class StoreController extends HttpServlet {
    // http://docs.oracle.com/javaee/6/api/index.html?javax/servlet/http/HttpServlet.html

    private static final CompactDiscDAO compactDiscDAO = new CompactDiscDAO();
    
    //Boolean for redirecting when a search if performed
    private static boolean redir = false;    
    
    //Hashtable for caching search results
    private static Hashtable<String, Integer> cacheSearches = new Hashtable<String, Integer>();    
    
    //Hashtable for caching non search page results - cache by R2
    private static Hashtable<Integer, Integer> cachepages = new Hashtable<Integer, Integer>();
   
    //List for recently visited
    private static ArrayList<CompactDisc> visited = new ArrayList<CompactDisc>();
    
    //Current position in the table
    private static int listpos = 0;
    
    //First iteration of visited - add first time round, set the next time
    private static boolean it = false;    
    
    /**
     * @inheritDoc
     */
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    /**
     * @inheritDoc
     */
    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
        doGet(req, res);
    }    
    
    /**
     * @inheritDoc
     */
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse res)
            throws ServletException, IOException {
    	
    	//These values are reset for each request
    	
    	//The string we're searching for
    	String search = "";
    	
    	//Redirected
    	redir = false;    	
    	
    	//Display from this row
    	int r1 = 1;    	
    	//To this row
    	int r2 = 10;    	
    	
    	//Matches (number of CDs to compare rows against)
    	int matches = 0;    	
    	
        // determine forwarding based on requested URL:
        String forwardTo = null;
        
        //When the user visits a specific page
        if (req.getServletPath().equals("/detail.html")) {
            CompactDisc item = compactDiscDAO.getCompactDisc(req.getParameter("id"));               
            
            //Check our list position, reset to 0 if needed
            if(listpos == 3){
            	
            	//After first 3 indexes are filled set it to true
            	it = true;                       
            	listpos = 0;
            }
            
            //Make sure we've not already added this item to visited
            if(visited.contains(item) == false){
            	            	
            	//If we've already iterated through once, then replace current items
            	if(it == true){
            		visited.set(listpos, item);
            	}
            	//Otherwise add items to the empty list
            	else{
            		visited.add(listpos, item);
            	}
            	//Increment position
            	listpos++;
            }            
            
            req.setAttribute("item", item);
            forwardTo = "/views/item.jsp";

        } else {
        	
            forwardTo = "/views/listing.jsp";

            //Parse the query string (if there is one and set r1, r2)
            String qstring = req.getQueryString();
            
            //If we have a query string
            if(qstring != null){
            	
            	//First check if we're searching - ?q=....
            	if(qstring.startsWith("q")){
            		
            		//Extract this string
            		search = qstring.substring(2);    		   
                                 
            		//Set redir to true so we don't send another response after this method ends
            		redir = true;
            		
            		//Redirect to the following url - and deal with this request with searchresults method
                    res.sendRedirect("/?first=1&last=10&q=" + search);                    
            	}
            	            	
            	//If we're moving through the pages of a search result /?first=1&last=11&q=search
            	if(qstring.contains("q=") && qstring.startsWith("first=")){              		            		     
            		
            		searchResults(qstring, search, r1, r2, matches, req);                      	
            	}      	            	
            	
            	//If we're not looking through search results
            	if(qstring.contains("q=") == false){
            	            		
            		results(qstring, r1, r2, matches, req);		     		            		
            	}            	
            }
            
            //If requesting the homepage, no query
            if(qstring == null){           	
            	
            	homePage(req, r1, r2);               
            }            
            
            //Assign recentItems to visited ArrayList
            List<CompactDisc> recentItems = visited; 
            //Set attribute
            req.setAttribute("recentItems", recentItems);
        }
        
        //If we haven't redirected (search query no received)
        if(redir == false){
        	
        	//Send the response as normal
        	getServletContext().getRequestDispatcher(forwardTo).forward(req, res);
        }
    }        
    
    
    //Methods for handling specific requests    
    
    //Handles browsing through the catalog
    public void results(String qstring, int r1, int r2, int matches, HttpServletRequest req){
    	
    	//Split the query at the first = and before the &
    	String q1 = qstring.substring(qstring.indexOf("=") + 1, qstring.indexOf("&"));
    	//Convert the string to an integer
    	r1 = Integer.parseInt(q1);    	
    	
    	//And after the last = 
    	String q2 = qstring.substring(qstring.lastIndexOf("=")+1);
    	//Convert the string to an integer
    	r2 = Integer.parseInt(q2);                    	
    	
        //Get results
        List<CompactDisc> compactDiscs = compactDiscDAO.getCompactDiscs(r1, r2);
        
        //Check if we've been to this page
        if(cachepages.containsKey(r2) == true){        	
        	
        	matches = cachepages.get(r2);   	
        }
        else{              	
        	
            //Get number of results        	
            matches = (int) compactDiscDAO.getNumberOfCompactDiscs();
            cachepages.put(r2, matches);                  	
        }                  

        //Calculate rows
        long totalRows = r2 - (r1-1);
        
        //Set listing attributes
        setAttribs("", totalRows, compactDiscs, req);
    		
        setNextPrev(r1, r2, matches, req);        	
    }    
    
    //Handles requests which move through search results
    public void searchResults(String qstring, String search, int r1, int r2, int matches, HttpServletRequest req){
    	
		//Get the search string again
		String searching = qstring.substring(qstring.lastIndexOf("=")+1);
		
		//Strip any ampersands
		searching = searching.replace("&", ""); 
		
		//Create end of url for next/previous buttons
		search = "&q=" + searching;            		
		
    	//Split the query at the first = and before the &
    	String q1 = qstring.substring(qstring.indexOf("=") + 1, qstring.indexOf("&"));
    	
    	//Convert the string to an integer
    	r1 = Integer.parseInt(q1);                            	          	 	
    	
    	//And after the last = 
    	String q2 = qstring.substring(qstring.indexOf("&") + 6, qstring.lastIndexOf("q")-1);
    	
    	//Convert the string to an integer
    	r2 = Integer.parseInt(q2); 	              	
    	
		//List results
		List<CompactDisc> compactDiscs = compactDiscDAO.getMatchingCompactDiscs(searching, r1, r2);
		
		//Check the cache for no. of matches
		if(cacheSearches.containsKey(searching) == true){			
			
			matches = (int) cacheSearches.get(searching);            			
		}
		else{            		
			//Get the number of matches - stops us displaying empty pages with next
			matches = (int) compactDiscDAO.getNumberOfMatchingCompactDiscs(searching);         		
		
			//Store in hashtable			
			cacheSearches.put(searching, matches);            			
		}        
        
        //Calculate number of rows
        long totalRows = r2 - (r1-1);                                 
        //Set attributes for listing
        setAttribs(search, totalRows, compactDiscs, req);       
		
        //Determine Next/Previous row values
        setNextPrev(r1, r2, matches, req);   	    	
    }    
    
    public void homePage(HttpServletRequest preq, int p1, int p2){
    	
    	//Get the first 10 rows
        List<CompactDisc> compactDiscs = compactDiscDAO.getCompactDiscs(p1, p2);
        
        //Calculate number of rows
        long totalRows = p2 - (p1-1);                
        
        setAttribs("", totalRows, compactDiscs, preq);                
        
        //Set next/previous
        preq.setAttribute("r1", p1);
        preq.setAttribute("r2", p2);
        preq.setAttribute("Upr2", p2 + 10);
        preq.setAttribute("Upr1", p1 + 10);
        preq.setAttribute("Backr1", p1);
        preq.setAttribute("Backr2", p2);	    	
    }    
    
    
    //Sets next/back row values for jsp
    public void setNextPrev(int p1, int p2, int check, HttpServletRequest preq){
    	
    	if(p2 + 10 <= check){
        	preq.setAttribute("Upr2", (p2 + 10));
        	preq.setAttribute("Upr1", (p1 + 10));
        }               
        else{
        	preq.setAttribute("Upr2", p2);
        	preq.setAttribute("Upr1", p1);
        	            	
        }            
        
         if(p1 - 10 < check){
        	preq.setAttribute("Backr1", p1 - 10);
        	preq.setAttribute("Backr2", p2 - 10);
        }
        
        if(p1 - 10 < 0){
        	preq.setAttribute("Backr1", p1);
        	preq.setAttribute("Backr2", p2);     	
        	
        }    	
    }
       
    //Sets the request attributes to be used by listing
    public void setAttribs(String psearch, long prows, List<CompactDisc> pdiscs, HttpServletRequest preq){
    	    	
        preq.setAttribute("search", psearch);
        preq.setAttribute("items", pdiscs);
        preq.setAttribute("totalRows", prows);   	
    	
    }
    
    
    
}