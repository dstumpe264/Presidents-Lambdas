package com.skilldistillery.history;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;

public class PresidentAppLambda {
	private static final String fileName = "presidents.tsv";
	private List<President> presidents = new ArrayList<>();
	public static void main(String[] args) {
		PresidentAppLambda app = new PresidentAppLambda();
		app.start();
	}
	public void start() {
		System.out.println("=====Sort====");
		this.printPresidents(presidents);
		System.out.println("====Whig's====");
		this.printPresidents(this.filter("Whig", (pres, string) -> pres.getParty().equals(string)));
		System.out.println("====Starts with Jo====");
		this.printPresidents(this.filter("Jo", (pres, string) -> pres.getFirstName().matches(string)));
		System.out.println("====Woodrow====");
		this.printPresidents(this.filter("Woodrow", (pres, string) -> pres.getFirstName().equals(string)));
		System.out.println("\n====Contains C=====");
		this.printPresidents(this.filter("c", (pres, string) -> pres.getLastName().contains(string)));
		System.out.println("\n====Democrat====");
		this.printPresidents(this.filter("Democrat", (pres, string) -> pres.getParty().contains(string)));
		System.out.println("\n====DIED====");
		this.printPresidents(this.filter("Died", (pres, string) -> pres.getWhyLeftOffice().startsWith(string)));
		System.out.println("\n==== OneTerm ====");
		this.printPresidents(this.filter("1", (pres, string) -> pres.getElectionsWon() == Integer.parseInt(string)));
		System.out.println("\n==== Started in the 19th Century ====");
		this.printPresidents(this.filter("19", (pres, string) -> {
			LocalDate tStart = pres.getTermStart();
			String termStr = "" + (tStart.getYear() + 100);
			return termStr.startsWith(string);
		}));
	}
	public PresidentAppLambda() {
		this.loadPresidents(fileName);
	}
	public List<President> getPresidents() {
		return this.presidents;
	}
	public void printPresidents(List<President> pres) {
		List<President> copy = new ArrayList<>(pres);
		// Collections.sort(copy, (p1, p2) -> p2.getParty().compareTo(p1.getParty()));
		// Collections.sort(copy, (p1, p2) -> p2.getWhyLeftOffice().compareTo(p1.getWhyLeftOffice()));
		Collections.sort(copy, (p1, p2) -> p2.getLastName().compareTo(p1.getLastName()));
		for (President p : copy) {
			System.out.println(p);
		}
	}

	//	public List<President> filter(String string, PresidentMatcher matcher) {
		public List<President> filter(String string, BiPredicate<President,String> matcher) {
		List<President> filtered = new ArrayList<>();
		for (President p : presidents) {
			if (matcher.test(p, string)) {
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
}