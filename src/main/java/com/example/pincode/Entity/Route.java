package com.example.pincode.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Route {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
   
	private String fromPincode;
    private String toPincode;
    private double distance; 
    private String duration; 
    private String routeInfo; 
    
    public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getFromPincode() {
		return fromPincode;
	}
	public void setFromPincode(String fromPincode) {
		this.fromPincode = fromPincode;
	}
	public String getToPincode() {
		return toPincode;
	}
	public void setToPincode(String toPincode) {
		this.toPincode = toPincode;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(double distance) {
		this.distance = distance;
	}
	public String getDuration() {
		return duration;
	}
	public void setDuration(String duration) {
		this.duration = duration;
	}
	public String getRouteInfo() {
		return routeInfo;
	}
	public void setRouteInfo(String routeInfo) {
		this.routeInfo = routeInfo;
	}
	 @Override
		public String toString() {
			return "Route [id=" + id + ", fromPincode=" + fromPincode + ", toPincode=" + toPincode + ", distance="
					+ distance + ", duration=" + duration + ", routeInfo=" + routeInfo + "]";
		}
	public Route( String fromPincode, String toPincode, double distance, String duration, String routeInfo) {
		super();
		
		this.fromPincode = fromPincode;
		this.toPincode = toPincode;
		this.distance = distance;
		this.duration = duration;
		this.routeInfo = routeInfo;
	}
	public Route() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	 
	
}

