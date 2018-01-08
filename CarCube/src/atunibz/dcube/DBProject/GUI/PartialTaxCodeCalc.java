package atunibz.dcube.DBProject.GUI;

import java.util.ArrayList;

public class PartialTaxCodeCalc {
	
	private String name;
	private String surname;
	private ArrayList<String> vowels = new ArrayList<>();
	private String output;
	
	public PartialTaxCodeCalc(String name, String surname) {
		
		this.name = name.trim().toLowerCase();
		this.surname = surname.trim().toLowerCase();
		vowels.add("a");
		vowels.add("e");
		vowels.add("i");
		vowels.add("o");
		vowels.add("u");
	}
	
	public String refactor() {
		String nameLetters = "";
		String surnameLetters = "";
		String surnameCons = this.getConsonants(surname);
		String surnameVow = this.getVowels(surname);
		String nameCons = this.getConsonants(name);
		String nameVow = this.getVowels(name);
		/*
		while(surnameCons.length() < 3) {
			surnameCons += "x";
		}
		while(nameCons.length() < 3) {
			nameCons += "x";
		}
		*/
		int surIndex = (surnameCons.toCharArray().length >= 3) ? 3 : surnameCons.toCharArray().length;
		
		for(int i = 0; i < surIndex; i++) {
			surnameLetters += surnameCons.toCharArray()[i];
		}
		int k = 0;
		while(surnameLetters.length() < surIndex) {
			surnameLetters += surnameVow.toCharArray()[k];
			k++;
		}
		while(surnameLetters.length() < 3) {
			surnameLetters += "x";
		}
		
		
		//if the name has at least 4 consonants
		if(nameCons.length() >= 4) {
			nameLetters += nameVow.toCharArray()[0];
			nameLetters += nameVow.toCharArray()[2];
			nameLetters += nameVow.toCharArray()[3];
		}
		else {
			int namIndex = (nameCons.toCharArray().length >= 3) ? 3 : nameCons.toCharArray().length;
			for(int j = 0; j < namIndex; j++) {
				nameLetters += nameCons.toCharArray()[j];
			}
		}
		int l = 0;
		while(nameLetters.length() < 3) {
			nameLetters += nameVow.toCharArray()[l];
			l++;
		}
		while(nameLetters.length() < 3) {
			nameLetters += "x";
		}
		return ((output = surnameLetters+nameLetters).toUpperCase());
		
	}
	
	private boolean isVowel(String s) {
		if(vowels.contains(s))
			return true;
		return false;
	}
	
	private boolean isConsonant(String s) {
		if(vowels.contains(s))
			return false;
		return true;
	}
	
	private String getVowels(String s) {
		char[] chars = s.toCharArray();
		String res = "";
		for(char c : chars) {
			String cur = "" + c;
			if(this.isVowel(cur))
				res += cur;
		}
		return res;
	}
	
	private String getConsonants(String s) {
		char[] chars = s.toCharArray();
		String res = "";
		for(char c : chars) {
			String cur = "" + c;
			if(!this.isVowel(cur))
				res += cur;
		}
		return res;
	}
	
	
	
	public static void main(String[] args) {
		
		PartialTaxCodeCalc c = new PartialTaxCodeCalc("Davide", "Perez Cuevas");
		
		System.out.println(c.refactor());
		
		
	}
	
	
	
	
	
}

