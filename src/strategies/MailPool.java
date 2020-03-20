/**
 * SWEN30006 Software Modelling and Design
 * Project Part A - Mailbot Blues
 * MailPool.java 
 * Student Name	: Ivan Ken Weng Chee
 * Student ID	: 736901
 */

package strategies;

import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Set;

import automail.MailItem;
import automail.Building;
import automail.IMailPool;

/**
 * Class for storing mail
 * Stores mail in three different LinkedHashMaps based on their
 * destination floor
 */
public class MailPool implements IMailPool {
	
	/**
	 * LinkedHashMaps representing three different mail pools
	 * mailUpPool : Mail items due on floors above the mailroom
	 * mailMidPool : Mail items due on the same floor as the mailroom
	 * mailDownPool : Mail items due on floors below the mailroom
	 */
	private LinkedHashMap<String, MailItem> mailUpPool;
	private LinkedHashMap<String, MailItem> mailMidPool;
	private LinkedHashMap<String, MailItem> mailDownPool;
	
	/**
	 * Constructor
	 */
	public MailPool() {
		this.mailUpPool = new LinkedHashMap<String, MailItem>();
		this.mailMidPool = new LinkedHashMap<String, MailItem>();
		this.mailDownPool = new LinkedHashMap<String, MailItem>();
	}

	/**
	 * Hashes mail into the pool using mail ids as keys
	 * @param mailItem : MailItem
	 */
	@Override
	public void addToPool(MailItem mailItem) {
		if (mailItem.getDestFloor() > Building.MAILROOM_LOCATION) {
			this.mailUpPool.put(mailItem.getId(), mailItem);
		} else if (mailItem.getDestFloor() == Building.MAILROOM_LOCATION) {
			this.mailMidPool.put(mailItem.getId(), mailItem);
		} else {
			this.mailDownPool.put(mailItem.getId(), mailItem);
		}
	}
	
	/**
	 * Gets mail out of all three pools if key found
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem get(String key) {
		return (this.mailUpPool.containsKey(key)) ? this.getUp(key)
				: (this.mailMidPool.containsKey(key)) ? this.getMid(key)
				: this.getDown(key);
	}
	
	/**
	 * Gets mail out of the upper pool
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem getUp(String key) {
		return this.mailUpPool.get(key);
	}
	
	/**
	 * Gets mail out of the middle pool
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem getMid(String key) {
		return this.mailMidPool.get(key);
	}
	
	/**
	 * Gets mail out of the lower pool
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem getDown(String key) {
		return this.mailDownPool.get(key);
	}
	
	/**
	 * Removes mail from all three pools if key found
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem removeFromPool(String key) {
		return (this.mailUpPool.containsKey(key)) ? this.removeUp(key)
				: (this.mailMidPool.containsKey(key)) ? this.removeMid(key)
				: this.removeDown(key);
	}
	
	/**
	 * Removes mail from the upper pool
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem removeUp(String key) {
		return this.mailUpPool.remove(key);
	}
	
	/**
	 * Removes mail from the middle pool
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem removeMid(String key) {
		return this.mailMidPool.remove(key);
	}
	
	/**
	 * Removes mail from the lower pool
	 * @param key : String
	 * @return : MailItem
	 */
	public MailItem removeDown(String key) {
		return this.mailDownPool.remove(key);
	}
	
	/**
	 * Returns a union of keys from all three pools
	 * @return : Set<String>
	 */
	public Set<String> keys() {
		Set<String> set = new HashSet<String>();
		set.addAll(this.upKeys());
		set.addAll(this.midKeys());
		set.addAll(this.downKeys());
		return set;
	}
	
	/**
	 * Returns keys from the upper pool
	 * @return : Set<String>
	 */
	public Set<String> upKeys() {
		return this.mailUpPool.keySet();
	}
	
	/**
	 * Returns keys from the middle pool
	 * @return : Set<String>
	 */
	public Set<String> midKeys() {
		return this.mailMidPool.keySet();
	}
	
	/**
	 * Returns keys from the lower pool
	 * @return : Set<String>
	 */
	public Set<String> downKeys() {
		return this.mailDownPool.keySet();
	}
	
	/**
	 * Returns the total size of all three pools combined
	 * @return : int
	 */
	public int size() {
		return this.upSize() + this.midSize() + this.downSize();
	}
	
	/**
	 * Returns the size of the upper pool
	 * @return : int
	 */
	public int upSize() {
		return this.mailUpPool.size();
	}
	
	/**
	 * Returns the size of the middle pool
	 * @return : int
	 */
	public int midSize() {
		return this.mailMidPool.size();
	}
	
	/**
	 * Returns the size of the lower pool
	 * @return : int
	 */
	public int downSize() {
		return this.mailDownPool.size();
	}
	
	/**
	 * Returns true if all three pools are empty
	 * @return : boolean
	 */
	public boolean isEmpty() {
		return this.isUpEmpty() && this.isMidEmpty() && this.isDownEmpty();
	}
	
	/**
	 * Returns true if the upper pool is empty
	 * @return : boolean
	 */
	public boolean isUpEmpty() {
		return this.mailUpPool.isEmpty();
	}
	
	/**
	 * Returns true if the middle pool is empty
	 * @return : boolean
	 */
	public boolean isMidEmpty() {
		return this.mailMidPool.isEmpty();
	}
	
	/**
	 * Returns true if the lower pool is empty
	 * @return : boolean
	 */
	public boolean isDownEmpty() {
		return this.mailDownPool.isEmpty();
	}
	
}

