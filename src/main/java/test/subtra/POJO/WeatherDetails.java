package test.subtra.POJO;

public class WeatherDetails {

	public WeatherDetails() {
		super();
	}

	public WeatherDetails(double degree, int humidity) {
		super();
		this.degree = degree;
		this.humidity = humidity;
	}

	private double degree;
	private int humidity;

	public double getDegree() {
		return degree;
	}

	public void setDegree(double degree) {
		this.degree = degree;
	}

	public int getFahrenheit() {
		return humidity;
	}

	public void setFahrenheit(int fahrenheit) {
		this.humidity = fahrenheit;
	}

	public static double kelvinToDegree(double kelvin) {
		return (kelvin - 273.15);
	}

}
