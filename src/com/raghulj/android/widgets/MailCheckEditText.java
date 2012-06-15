package com.raghulj.android.widgets;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.widget.EditText;

public class MailCheckEditText extends EditText {

	private Context mContext;

	public MailCheckEditText(Context context) {
		super(context);
		mContext = context;
		initialize();
	}

	public MailCheckEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		initialize();
	}

	public MailCheckEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		initialize();
	}

	private void initialize() {
		setInputType(InputType.TYPE_CLASS_TEXT
				| InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
		setPadding(2, 2, 2, 2);

	}

	/**
	 * Suggests an email address based on the entered email address using a
	 * fuzzy logic
	 */
	public String suggest() {
		String emailId = this.getText().toString();
		return suggestEmailId(emailId, defaultDomains, defaultTopLevelDomains);

	}

	private String suggestEmailId(String email, String[] domains,
			String[] topLevelDomains) {
		email = email.toLowerCase();
		String emailAddress = email;

		String[] emailParts = splitEmail(email);

		if (emailParts == null) {
			return null;
		}
		String closestDomain = findClosestDomain(emailParts[1], domains);
		if (closestDomain != null) {
			if (closestDomain != emailParts[1]) {
				// The email address closely matches one of the supplied
				// domains; return a suggestion
				emailAddress = emailParts[2] + "@" + closestDomain;
				return emailAddress;
			}
		} else {
			// The email address does not closely match one of the supplied
			// domains
			String closestTopLevelDomain = findClosestDomain(emailParts[0],
					topLevelDomains);
			if (emailParts[1] != null && closestTopLevelDomain != null
					&& closestTopLevelDomain != emailParts[0]) {
				// The email address may have a mispelled top-level domain;
				// return a suggestion
				String domain = emailParts[1];
				String[] domainName = domain.split("\\.");

				// find the closest of the domain names
				domainName[0] = findClosestDomain(domainName[0],
						defaultDomainNames);
				domain = stringJoin(domainName, ".");

				// concatenate the domain name with the top level domain name
				closestDomain = domain.substring(0,
						domain.lastIndexOf(emailParts[0]))
						+ closestTopLevelDomain;
				emailAddress = emailParts[2] + "@" + closestDomain;
			}
		}
		/*
		 * The email address exactly matches one of the supplied domains, does
		 * not closely match any domain and does not appear to simply have a
		 * mispelled top-level domain, or is an invalid email address; do not
		 * return a suggestion.
		 */
		return emailAddress;
	}

	/**
	 * Method compares two string and find the accuracy percentage from that.
	 * Based on
	 * http://siderite.blogspot.com/2007/04/super-fast-and-accurate-string
	 * -distance.html
	 * 
	 * @param s1
	 * @param s2
	 * @param maxOffset
	 * @return
	 */
	private float shift3Distance(String s1, String s2, int maxOffset) {
		if (isNullOrBlank(s1))
			return isNullOrBlank(s2) ? 0 : s2.length();
		if (isNullOrBlank(s2))
			return s1.length();
		int c = 0;
		int offset1 = 0;
		int offset2 = 0;
		int lcs = 0;
		while ((c + offset1 < s1.length()) && (c + offset2 < s2.length())) {
			if (s1.charAt(c + offset1) == s2.charAt(c + offset2))
				lcs++;
			else {
				offset1 = 0;
				offset2 = 0;
				for (int i = 0; i < maxOffset; i++) {
					if ((c + i < s1.length())
							&& (s1.charAt(c + i) == s2.charAt(c))) {
						offset1 = i;
						break;
					}
					if ((c + i < s2.length())
							&& (s1.charAt(c) == s2.charAt(c + i))) {
						offset2 = i;
						break;
					}
				}
			}
			c++;
		}
		return (s1.length() + s2.length()) / 2 - lcs;
	}

	/** Split the email id to username domain name and top level domain names */
	private String[] splitEmail(String email) {

		String[] parts = email.split("@");

		if (parts.length < 2) {
			return null;
		}

		for (int i = 0; i < parts.length; i++) {
			if (parts[i] == "") {
				return null;
			}
		}

		String domain = parts[1];
		String[] domainParts = domain.split("\\.");
		String tld = "";

		if (domainParts.length == 0) {
			// The address does not have a top-level domain
			return null;
		} else if (domainParts.length == 1) {
			// The address has only a top-level domain (valid under RFC)
			tld = domainParts[0];
		} else {
			// The address has a domain and a top-level domain
			for (int i = 1; i < domainParts.length; i++) {
				tld += domainParts[i] + '.';
			}
			if (domainParts.length >= 2) {
				tld = tld.substring(0, tld.length() - 1);
			}
		}

		String[] arr = { tld, domain, parts[0] };
		return arr;
	}

	/**
	 * Find the closest domain match for the given domain name in the email with
	 * the set of domain names
	 */
	private String findClosestDomain(String domain, String[] domains) {
		float dist;
		int minDist = 99;
		int threshold = 3;
		String closestDomain = null;

		if (domain == null || domains == null) {
			return null;
		}

		for (int i = 0; i < domains.length; i++) {
			if (domain == domains[i]) {
				return domain;
			}
			dist = shift3Distance(domain, domains[i], minDist);
			if (dist < minDist) {
				minDist = (int) dist;
				closestDomain = domains[i];
			}
		}

		if (minDist <= threshold && closestDomain != null) {
			return closestDomain;
		} else {
			return null;
		}
	}

	// Util methods
	private boolean isNull(String str) {
		return str == null ? true : false;
	}

	private boolean isNullOrBlank(String param) {
		if (isNull(param) || param.trim().length() == 0) {
			return true;
		}
		return false;
	}

	private String stringJoin(String[] inputArray, String glueString) {

		/** Output variable */
		String output = "";

		if (inputArray.length > 0) {
			StringBuilder sb = new StringBuilder();
			sb.append(inputArray[0]);

			for (int i = 1; i < inputArray.length; i++) {
				sb.append(glueString);
				sb.append(inputArray[i]);
			}

			output = sb.toString();
		}

		return output;
	}

	// Default domain names to be checked
	private final String[] defaultDomains = { "yahoo.com", "google.com",
			"hotmail.com", "gmail.com", "me.com", "aol.com", "mac.com",
			"live.com", "comcast.net", "googlemail.com", "msn.com",
			"hotmail.co.uk", "yahoo.co.uk", "facebook.com", "verizon.net",
			"sbcglobal.net", "att.net", "gmx.com", "mail.com" };

	private final String[] defaultTopLevelDomains = { "co.uk", "com", "net",
			"org", "info", "edu", "gov", "mil" };
	private final String[] defaultDomainNames = { "yahoo", "google", "hotmail",
			"gmail", "me", "aol", "mac", "live", "comcast", "googlemail",
			"msn", "hotmail", "yahoo", "facebook", "verizon", "sbcglobal",
			"att", "gmx", "mail" };

}
