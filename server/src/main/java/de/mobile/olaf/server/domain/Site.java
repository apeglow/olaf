package de.mobile.olaf.server.domain;


public class Site {
	
	private final String name;
	private final Country country;
	private final SiteType type;
	
	public Site(String name, Country country, SiteType type){
		this.name = name;
		this.country = country;
		this.type = type;
	}
	
	public Country getCountry() {
		return country;
	}
	
	public String getName() {
		return name;
	}
	
	public SiteType getType() {
		return type;
	}
	
	@Override
	public String toString() {
		return name + " ("+type+" in "+country+")";
	}
	

}
