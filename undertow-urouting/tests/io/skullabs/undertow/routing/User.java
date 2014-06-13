package io.skullabs.undertow.routing;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

public class User {

	public String name;

	@XmlElementWrapper( name="addresses" )
	@XmlElement(name = "address")
	public List<Address> addresses;

	public User( String name, Address...addresses ) {
		this.name = name;
		this.addresses = new ArrayList<User.Address>();
		for ( Address address : addresses )
			this.addresses.add(address);
	}

	public static class Address {

		public String street;
		public int number;
		
		public Address( String street, int number ) {
			this.street = street;
			this.number = number;
		}
	}
}

