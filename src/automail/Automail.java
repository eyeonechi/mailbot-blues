/**
 * SWEN30006 Software Modelling and Design
 * Project Part A - Mailbot Blues
 * Automail.java 
 * Student Name	: Ivan Ken Weng Chee
 * Student ID	: 736901
 */

package automail;

import strategies.*;

public class Automail {
	      
    public Robot robot;
    public IMailPool mailPool;
    
    Automail(IMailDelivery delivery) {
    	
    /** CHANGE NOTHING ABOVE HERE */
    	
    	/** Initialize the MailPool */
        // SimpleMailPool simpleMailPool = new SimpleMailPool();
    	// mailPool = simpleMailPool;
    	this.mailPool = new MailPool();
    	
    	/** Initialize the MailSorter */
        // IMailSorter sorter = new SimpleMailSorter(simpleMailPool);
    	IMailSorter sorter = new MailSorter((MailPool) this.mailPool);
    	
    /** CHANGE NOTHING BELOW HERE */
    	
    	/** Initialize robot */
    	robot = new Robot(sorter, delivery);
    	
    }
    
}
