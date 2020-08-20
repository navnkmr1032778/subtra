package test.subtra.POJO;

public class WeatherVarianceComparator {

	public WeatherVarianceComparator() {
		super();
	}

	public WeatherVarianceComparator(int degreeVariance, int humidityVariance) {
		super();
		this.degreeVariance = degreeVariance;
		this.humidityVariance = humidityVariance;
	}

	private int degreeVariance;
	private int humidityVariance;

	public int getHumidityVariance() {
		return humidityVariance;
	}

	public void setHumidityVariance(int humidityVariance) {
		this.humidityVariance = humidityVariance;
	}

	public int getDegreeVariance() {
		return degreeVariance;
	}

	public void setDegreeVariance(int variance) {
		this.degreeVariance = variance;
	}

	public boolean compareVarianceDegree(WeatherDetails ui, WeatherDetails api) {
		if (degreeVariance < 0)
			return true;
		double varient = ui.getDegree() - api.getDegree();
		varient = Math.abs(varient);
		if (varient <= degreeVariance)
			return true;
		else
			return false;
	}

	public boolean compareVarianceHumidity(WeatherDetails ui, WeatherDetails api) {
		if (humidityVariance < 0)
			return true;
		int varient = ui.getFahrenheit() - api.getFahrenheit();
		varient = Math.abs(varient);
		if (varient <= humidityVariance)
			return true;
		else
			return false;
	}
}
