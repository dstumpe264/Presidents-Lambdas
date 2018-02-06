package com.skilldistillery.history;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class PresidentApp {
	private static final String fileName = "presidents.tsv";
	private List<President> presidents = new ArrayList<>();

	public static void main(String[] args) {
		PresidentApp app = new PresidentApp();
		app.start();
	}

	public void start() {

//		this.printPresidents(sortByWhyLeftAndTerm(this.getPresidents()));
		
//		this.printPresidents(this.filter("Woodrow", new PresidentFirstNameMatcher()));

		this.printPresidents(this.filter("c", new PresidentMatcher() {
			public boolean matches(President pres, String string) {
				return pres.getLastName().startsWith(string);
			}
		}));
		System.out.println();
		System.out.println("====Democrat====");
		this.printPresidents(this.filter("Democrat", new PresidentMatcher() {
			public boolean matches(President pres, String string) {
				return pres.getParty().contains(string);
			}
		}));
		System.out.println();
		System.out.println("===DIED===");
		this.printPresidents(this.filter(("Died"), new PresidentMatcher() {
			public boolean matches(President pres, String string) {
				return pres.getWhyLeftOffice().startsWith(string);
			}
		}));
		System.out.println();
		
		System.out.println("==== OneTerm ====");
		this.printPresidents(this.filter(("1"), new PresidentMatcher() {
			public boolean matches(President pres, String string) {
				return pres.getElectionsWon() == Integer.parseInt(string);
			}
		}));
		System.out.println();
		
		System.out.println("==== Started in the 19th Century ====");
		this.printPresidents(this.filter("19", new PresidentMatcher() {
			public boolean matches(President pres, String string) {
				LocalDate tStart = pres.getTermStart();
				String termStr = "" + (tStart.getYear() + 100);
				
				return termStr.startsWith(string);
			}
		}));
	}

	// class PresFirstName implements PresidentMatcher {
	// @Override
	// public boolean matches(President pres, String string) {
	// return (pres.getLastName().charAt(0) == 'C');
	// }
	// }
	// for (President president : presidents) {
	// if (new PresFirstName().matches(president, president.getLastName())) {
	// System.out.println(president);
	// }
	// }
	// this.printPresidents(this.getPresidents());
	//
	// PresidentMatcher matcher = new PresidentPartyMatcher();
	// List<President> filtered = this.filter("whig", matcher);
	// one liner VvVvV
	// this.printPresidents(this.filter("whig",new PresidentPartyMatcher()));
	// System.out.println();this.printPresidents(presidents);
	// System.out.println();this.printPresidents(presidents);
	// PresidentMatcher pressMatch = new PresFirstName();System.out.println(new
	// PresFirstName().matches(presidents.get(0),"Woodrow"));

	private class PartyAndTerm implements Comparator<President> {
		@Override
		public int compare(President o1, President o2) {
			String party1 = o1.getParty();
			String party2 = o2.getParty();
			int retVal = party2.compareTo(party1);
			if (retVal == 0) {
				retVal = o1.getTermNumber() - o2.getTermNumber();
			}
			return retVal;
		}

	}

	public PresidentApp() {
		this.loadPresidents(fileName);
	}

	public List<President> getPresidents() {
		return this.presidents;
	}

//	List<President> sortByWhyLeftAndTerm(List<President> list) {
//		class WhyLeftTermComparator implements Comparator<President> {
//			@Override
//			public int compare(President o1, President o2) {
//				String party1 = o1.getWhyLeftOffice();
//				String party2 = o2.getWhyLeftOffice();
//				int retVal = party2.compareTo(party1);
//				return retVal;
//			}
//		}
//		List<President> sorted = new ArrayList<President>(list);
//		Collections.sort(WhyLeftTermComparator.sorted);
//		return sorted;
//	}

	public void printPresidents(List<President> pres) {

		// Comparator<President> comp = new PartyAndTermComparator();
		Collections.sort(pres, new Comparator<President>() {
			// @Override
			public int compare(President o1, President o2) {
				String party1 = o1.getLastName();
				String party2 = o2.getLastName();
				int retVal = party2.compareTo(party1);
				return retVal;
			}
		});

		for (President p : pres) {
			System.out.println(p);
		}

	}

	public List<President> filter(String string, PresidentMatcher matcher) {
		List<President> filtered = new ArrayList<>();
		for (President p : presidents) {
			if (matcher.matches(p, string)) {
				filtered.add(p);
			}
		}
		return filtered;
	}

	private void loadPresidents(String fileName) {
		// File format (tab-separated):
		// # First Middle Last Inaugurated Left office Elections won Reason left office
		// Party
		// 1 George Washington July 1, 1789 March 4, 1797 2 Did not seek re-election
		// Independent
		try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM d, yyyy");
			String record = reader.readLine(); // Read and discard header line
			while ((record = reader.readLine()) != null) {
				String[] col = record.split("\\t");

				int term = Integer.parseInt(col[0]);
				String fName = col[1];
				String mName = col[2];
				String lName = col[3];
				String termStartString = col[4]; // Date term began.
				String termEndString = col[5]; // Date term ended.
				int won = Integer.parseInt(col[6]);
				String whyLeft = col[7];
				String party = col[8];
				termStartString = termStartString.trim();
				termEndString = termEndString.trim();
				LocalDate termStart;
				termStart = LocalDate.parse(termStartString, formatter);
				LocalDate termEnd = null;
				if (termEndString != null && termEndString.length() > 0) {
					termEnd = LocalDate.parse(termEndString, formatter);
				}

				President pres = new President(term, fName, mName, lName, termStart, termEnd, won, whyLeft, party);
				presidents.add(pres);

			}
		} catch (IOException ioe) {
			ioe.printStackTrace();
			System.exit(1);
		}
	}

	/*
	 * Add a new method to President: getTermLength(). This should return a Period
	 * object representing the interval between the term begin and end dates. If the
	 * president has no term end date (that is, the current president) it should
	 * return the difference between term start and the current date. Include this
	 * in the president's toString().
	 */

}