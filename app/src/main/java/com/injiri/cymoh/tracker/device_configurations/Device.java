package com.injiri.cymoh.tracker.device_configurations;

public class Device {

	private String lastupdated;

	private String deviceId;
	private String deviceName;
	private String geoRadius;
	private double deviceLat;
	private double deviceLon;
	private String updateFrequency;
	private String batteryStatus;
	private double ownerLatitude;
	private double ownerLongitude;

	public Device() {
	}

	public Device(String lastupdated, String deviceId, String deviceName, String geoRadius, double deviceLat, double deviceLon, String updateFrequency, String batteryStatus, double ownerLatitude, double ownerLongitude) {
		this.lastupdated = lastupdated;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.geoRadius = geoRadius;
		this.deviceLat = deviceLat;
		this.deviceLon = deviceLon;
		this.updateFrequency = updateFrequency;
		this.batteryStatus = batteryStatus;
		this.ownerLatitude = ownerLatitude;
		this.ownerLongitude = ownerLongitude;
	}

	public void setLastupdated(String lastupdated) {
		this.lastupdated = lastupdated;
	}

	public String getLastupdated() {
		return lastupdated;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getGeoRadius() {
		return geoRadius;
	}

	public void setGeoRadius(String geoRadius) {
		this.geoRadius = geoRadius;
	}

	public double getDeviceLat() {
		return deviceLat;
	}

	public void setDeviceLat(double deviceLat) {
		this.deviceLat = deviceLat;
	}

	public double getDeviceLon() {
		return deviceLon;
	}

	public void setDeviceLon(double deviceLon) {
		this.deviceLon = deviceLon;
	}

	public String getUpdateFrequency() {
		return updateFrequency;
	}

	public void setUpdateFrequency(String updateFrequency) {
		this.updateFrequency = updateFrequency;
	}

	public String getBatteryStatus() {
		return batteryStatus;
	}

	public void setBatteryStatus(String batteryStatus) {
		this.batteryStatus = batteryStatus;
	}

	public double getOwnerLatitude() {
		return ownerLatitude;
	}

	public void setOwnerLatitude(double ownerLatitude) {
		this.ownerLatitude = ownerLatitude;
	}

	public double getOwnerLongitude() {
		return ownerLongitude;
	}

	public void setOwnerLongitude(double ownerLongitude) {
		this.ownerLongitude = ownerLongitude;
	}

}

