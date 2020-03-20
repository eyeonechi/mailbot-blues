/**
 * SWEN30006 Software Modelling and Design
 * Project Part A - Mailbot Blues
 * MailSorter.java 
 * Student Name	: Ivan Ken Weng Chee
 * Student ID	: 736901
 */

package strategies;

import java.util.ArrayList;
import java.util.Set;

import automail.IMailSorter;
import automail.MailItem;
import automail.Building;
import automail.Clock;
import automail.StorageTube;
import exceptions.TubeFullException;

/**
 * Class for sorting mail
 * Extracts mail from the mail pool and uses dynamic programming to
 * determine the best combination to deliver at a particular moment
 * based on the scoring function calculateValue()
 */
public class MailSorter implements IMailSorter {

	/** Threshold for comparing doubles */
	private final double EPSILON = 0.001;

	/** Mail pool used for retrieving mail */
	MailPool mailPool;

	/**
	 * Constructor
	 * @param mailPool : MailPool
	 */
	public MailSorter(MailPool mailPool) {
		this.mailPool = mailPool;
	}

	/**
	 * Fills the storage tube with mail and sends the robot off to deliver
	 * @param tube : StorageTube
	 * @return : boolean
	 */
	@Override
	public boolean fillStorageTube(StorageTube tube) {
		/** Case where mailPool is empty */
		if (this.mailPool.isEmpty()) {
			return false;
		}
		/** Add selected mail to the tube */
		for (String key : selectMail(tube.MAXIMUM_CAPACITY)) {
			try {
				/** Try adding another mail item into the tube */
				tube.addItem(this.mailPool.get(key));
				this.mailPool.removeFromPool(key);
			} catch (TubeFullException e) {
				e.printStackTrace();
				/**
				 * Re-insert the overflowing mail into the pool
				 * and asks the robot to deliver the rest
				 * (This section should not be invoked)
				 */
				this.mailPool.addToPool(tube.pop());
			}
		}
		return true;
	}

	/** Uses dynamic programming to select the best combination of
	 * mail items to fit into the tube based on their calculated score
	 * @param capacity : int
	 * @return : ArrayList<String>
	 */
	private ArrayList<String> selectMail(int capacity) {
		/** ArrayLists of mail keys and 2D score array of respective pools */
		ArrayList<String> upKeys = new ArrayList<String>();
		ArrayList<String> midKeys = new ArrayList<String>();
		ArrayList<String> downKeys = new ArrayList<String>();
		double[][] upTable = new double[this.mailPool.upSize() + 1]
				[capacity + 1];
		double[][] midTable = new double[this.mailPool.midSize() + 1]
				[capacity + 1];
		double[][] downTable = new double[this.mailPool.downSize() + 1]
				[capacity + 1];

		/** Converts keys to lists and constructs dynamic matrices */
		upKeys = convertToArrayList(this.mailPool.upKeys());
		midKeys = convertToArrayList(this.mailPool.midKeys());
		downKeys = convertToArrayList(this.mailPool.downKeys());
		upTable = constructTable(upTable, upKeys);
		midTable = constructTable(midTable, midKeys);
		downTable = constructTable(downTable, downKeys);

		/**
		 * Bottom-right entry in the calculated table is the optimum
		 * score for mail which can fit into the tube
		 */
		double upScore = upTable[upTable.length - 1][capacity];
		double midScore = midTable[midTable.length - 1][capacity];
		double downScore = downTable[downTable.length - 1][capacity];

		/** Return mail from highest scoring pool */
		if (Math.abs(upScore
				- max(upScore, max(midScore, downScore))) < EPSILON) {
			return retraceMail(upTable, upKeys);
		} else if (Math.abs(midScore
				- max(upScore, max(midScore, downScore))) < EPSILON) {
			return retraceMail(midTable, midKeys);
		}
		return retraceMail(downTable, downKeys);
	}

	/**
	 * Converts a Set into an ArrayList
	 * @param keySet : Set<String>
	 * @return ArrayList<String>
	 */
	private ArrayList<String> convertToArrayList(Set<String> keySet) {
		ArrayList<String> keyList = new ArrayList<String>();
		for (String key : keySet) {
			keyList.add(key);
		}
		return keyList;
	}

	/**
	 * Constructs the dynamic programming matrix
	 * Values are iteratively determined based on whether a greater
	 * score is achieved with or without the current item
	 * @param table : double[][]
	 * @param keys : ArrayList<String>
	 * @return : double[][]
	 */
	private double[][] constructTable(
			double[][] table, ArrayList<String> keys) {
		for (int j = 0; j < table[0].length; j++) {
			table[0][j] = 0.0;
		}
		for (int i = 0; i < table.length; i++) {
			table[i][0] = 0.0;
		}
		for (int i = 1; i < table.length; i++) {
			for (int j = 1; j < table[i].length; j++) {
				MailItem mail = this.mailPool.get(keys.get(i - 1));
				if (j < mail.getSize()) {
					table[i][j] = table[i - 1][j];
				} else {
					table[i][j] = max(calculateValue(mail)
							+ table[i - 1][j - mail.getSize()],
							table[i - 1][j]);
				}
			}
		}
		return table;
	}

	/**
	 * Backtracks along the table to retrieve corresponding mail
	 * contributing to the score
	 * @param table : double[][]
	 * @param keys : ArrayList<String>
	 * @return : ArrayList<String>
	 */
	private ArrayList<String> retraceMail(
			double[][] table, ArrayList<String> keys) {
		ArrayList<String> mailItems = new ArrayList<String>();
		int y = table.length - 1;
		int x = table[0].length - 1;
		while (y > 0 && x > 0) {
			if (Math.abs(table[y][x] - table[y - 1][x]) > EPSILON) {
				MailItem mail = this.mailPool.get(keys.get(y - 1));
				mailItems.add(keys.get(y - 1));
				x -= mail.getSize();
			}
			y -= 1;
		}
		return mailItems;
	}

	/**
	 * Returns the maximum of two doubles
	 * @param x : double
	 * @param y : double
	 * @return : double
	 */
	private double max(double x, double y) {
		return (x > y) ? x : y;
	}

	/**
	 * Calculates the value of a mail item based on its properties
	 * (Changing the return value in this function can significantly
	 *  affect the score, but I have yet to find an optimum equation)
	 * @param mailItem : MailItem
	 * @return : double
	 */
	public double calculateValue(MailItem mailItem) {
		double distance = 1 + calculateDistance(mailItem.getDestFloor());
		double size = calculateSize(mailItem.getSize());
		double time = 1 + calculateTime(mailItem.getArrivalTime());
		double priority = calculatePriority(mailItem.getPriorityLevel());
		return (priority)/(2 * distance + 1/size * 1/time);
	}

	/**
	 * Returns the absolute distance of the mailroom floor to the
	 * mail item's destination floor
	 * @param destFloor : int
	 * @return : double
	 */
	private double calculateDistance(int destFloor) {
		return Math.abs(destFloor - Building.MAILROOM_LOCATION);
	}

	/**
	 * Returns a score based on the priority of the mail item
	 * @param priority : String
	 * @return : double
	 */
	private double calculatePriority(String priority) {
		return (priority == "HIGH") ? 2.0
				: (priority == "LOW") ? 1.0
						: 1.5;
	}

	/**
	 * Returns a score based on the size of the mail item for equal
	 * weightage in the tube
	 * @param size : int
	 * @return : double
	 */
	private double calculateSize(int size) {
		return (size == 1) ? 1.0
				: (size == 2) ? 2.0
						: 4.0;
	}

	/**
	 * Returns the difference in current time and mail arrival time
	 * @param arrivalTime : int
	 * @return : double
	 */
	private double calculateTime(int arrivalTime) {
		return Clock.Time() - arrivalTime;
	}

}
