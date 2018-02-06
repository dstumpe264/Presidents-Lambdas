package com.skilldistillery.history;

public class PresidentPartyMatcher implements PresidentMatcher {
	@Override
	public boolean matches(President pres, String string) {
		return pres.getParty().equalsIgnoreCase(string);
	}

}