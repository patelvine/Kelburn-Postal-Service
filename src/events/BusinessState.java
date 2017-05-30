package events;

import java.util.Date;

/**
 * This class represents a snapshot in time for the business financially. There
 * is a public field which holds the most up to date BusinessState which should
 * be used for updating the state of the business
 *
 * @author hawkinchri
 *
 */
public class BusinessState {
	/**
	 * The total money the business has taken in
	 */
	public final double totMoneyIn;

	/**
	 * The total money the business has spent
	 */
	public final double totMoneyOut;

	/**
	 * The total number of deliveries
	 */
	public final int numDeliveries;

	/**
	 * The total time spent delivering mail
	 */
	public final double totDeliveryTime;

	/**
	 * The date this BusinessState was created
	 */
	public final Date date;

	/**
	 * The last BusinessState
	 */
	public final BusinessState previousState;

	/**
	 * The most recent business state in the system
	 */
	public static BusinessState currentState = null;

	/**
	 * gets the index of the business state (eg 0th state 3rd state)
	 *
	 * @return The index of the BusinessState
	 */
	public int getStateNum() {
		BusinessState prev = this.previousState;
		int count = 0;
		while (prev != null) {
			count++;
			prev = prev.previousState;
		}
		return count;
	}

	/**
	 * Gets the very first BusinessState for the system.
	 *
	 * @param date
	 * @return
	 */
	public static BusinessState startState(Date date) {
		BusinessState bs = new BusinessState(0, 0, 0, 0, null, date);
		return bs;
	}

	/**
	 * Private constructor to make a new Business State.
	 *
	 * @param moneyIn
	 * @param moneyOut
	 * @param totalDeliveryTime
	 * @param numDeliveries
	 * @param previous
	 * @param date
	 */
	private BusinessState(double moneyIn, double moneyOut,
			double totalDeliveryTime, int numDeliveries,
			BusinessState previous, Date date) {
		this.totMoneyIn = moneyIn;
		this.totMoneyOut = moneyOut;
		this.totDeliveryTime = totalDeliveryTime;
		this.numDeliveries = numDeliveries;
		this.previousState = previous;
		this.date = date;

		// calculate average delivery time
	}

	/**
	 * Updates the state of the business with a new mail delivery of specified
	 * time, cost and revenue. This method updates the currentState with the
	 * correct new updated state.
	 *
	 * @param moneyIn
	 *            The money gained from the customer
	 * @param moneyOut
	 *            The money spent on transport
	 * @param deliveryTime
	 *            The time it tooke to deliver
	 */
	public void updateWithMail(double moneyIn, double moneyOut,
			double deliveryTime, Date date) {
		// sum new and existing data
		double mIn = moneyIn + totMoneyIn;
		double mOut = moneyOut + totMoneyOut;
		int numD = numDeliveries + 1;
		double totDeliv = deliveryTime + totDeliveryTime;

		// make new businessState
		BusinessState bs = new BusinessState(mIn, mOut, totDeliv, numD,
				currentState, date);

		// update current state
		currentState = bs;
	}

	/**
	 * Calculate the average delivery time for all mail
	 *
	 * @return The average delivery time
	 */
	public double getAverageDeliveryTime() {
		if (numDeliveries == 0)
			return 0;
		else
			return this.totDeliveryTime / this.numDeliveries;
	}

	/**
	 * Calculate the net profit for KPS
	 *
	 * @return The net profit
	 */
	public double getNetProfit() {
		return this.totMoneyIn - this.totMoneyOut;
	}

	public String toString() {
		String str = "BusinessState";
		str += "(tot money in:" + this.totMoneyIn + ", tot money out"
				+ this.totMoneyOut + ", tot delivery time"
				+ this.totDeliveryTime + ", tot deliveries"
				+ this.numDeliveries + ")";
		return str;
	}

}
