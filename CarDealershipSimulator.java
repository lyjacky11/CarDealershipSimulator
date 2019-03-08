import java.util.ArrayList;
import java.util.Scanner;
import java.util.Collections;

public class CarDealershipSimulator 
{
  /**
   * @param header string for use with displayInventory()
   */
  public static void addHeader(String header) {
	System.out.println();
	System.out.println(header);
	for (int i = 0; i < header.length() + 2; i++) {
		System.out.print("-");
	}
	System.out.println();
	}
	
  public static void main(String[] args)
  {
	// Create a CarDealership object
	CarDealership newDealer = new CarDealership();
	// Then create an (initially empty) array list of type Car
	ArrayList<Car> cars = new ArrayList<Car>();
	// Then create some new car objects of different types
	Car car1 = new Car("Honda", "red", Car.GAS_ENGINE, 4, Car.SPORTS, 450, 9.2, 30000, false);
	Car car2 = new Car("Toyota", "blue", Car.GAS_ENGINE, 4, Car.SEDAN, 500, 9.5, 25000, false);
	ElectricCar car3 = new ElectricCar("Tesla", "red", Car.ELECTRIC_MOTOR, 4, Car.SEDAN, 425, 9.1, 85000, true, 55, "Lithium");
	// See the cars file for car object details
	// Add the car objects to the array list
	cars.add(car1);
	cars.add(car2);
	cars.add(car3);
    // The ADD command should hand this array list to CarDealership object via the addCars() method
	// Create a scanner object
	String line = "", command = "";
	String header = "Pos  Brand   Color  Model   MaxR  SR    Price ($)  AWD    RT  Battery";
	Scanner input = new Scanner(System.in);
	System.out.print("Enter a command (Q to quit): ");
	// while the scanner has another line
	while (input.hasNextLine()) {
	//  read the input line
		line = input.nextLine();
	//  create another scanner object (call it "commandLine" or something) using the input line instead of System.in
		Scanner commandLine = new Scanner(line);
	//  read the next word from the commandLine scanner
		command = commandLine.next();
		//	check if the word (i.e. string) is equal to one of the commands and if so, call the appropriate method via the CarDealership object  
		switch (command) {
			case "L":
				if (!newDealer.isEmpty) {
					addHeader(header);
					newDealer.displayInventory();
					System.out.println("\nInventory loaded successfully.");
				}
				else
					System.out.println("\nERROR: Inventory is empty!");
				break;
			case "Q":
				commandLine.close();
				System.out.println("\nThank you for shopping at our dealership!");
				return;
			case "BUY":
				int index = commandLine.nextInt();
				Car currentCar = newDealer.buyCar(index);
				if (currentCar != null) {
					System.out.println("\nCar Details:");
					addHeader(header);
					System.out.printf("%-4d %s\n", index, currentCar.display());
					System.out.println("\nCar at position " + index + " bought successfully!");
				}
				else
					System.out.println("\nERROR: Invalid car selection!");
				break;
			case "RET":
				Car returnCar = newDealer.carLastBought;
				newDealer.returnCar(returnCar);
				if (returnCar != null)
					System.out.println("\nReturned last car bought to inventory.");
				else
					System.out.println("\nERROR: No car found to return to inventory.");
				break;
			case "ADD":
				newDealer.addCars(cars);
				System.out.println("\nAdded cars to dealership inventory.");
				break;
			case "SPR":
				newDealer.sortByPrice();
				System.out.println("\nInventory sorted by price.");
				break;
			case "SSR":
				newDealer.sortBySafetyRating();
				System.out.println("\nInventory sorted by safety rating.");
				break;
			case "SMR":
				newDealer.sortByMaxRange();
				System.out.println("\nInventory sorted by max range.");
				break;
			case "FPR":
				if (commandLine.hasNextInt()) {
					int minPrice = commandLine.nextInt();
					if (commandLine.hasNextInt()) {
						int maxPrice = commandLine.nextInt();
						newDealer.filterByPrice(minPrice, maxPrice);
						System.out.println("\nInventory filtered by price between $" + minPrice + " and $" + maxPrice + ".");
					}
					else
					System.out.println("\nERROR: Max price not specified!");
				}
				else
					System.out.println("\nERROR: No price range specified!");
				break;
			case "FEL":
				newDealer.filterByElectric();
				System.out.println("\nInventory filtered by electric cars.");
				break;
			case "FAW":
				newDealer.filterByAWD();
				System.out.println("\nInventory filtered by AWD cars.");
				break;
			case "FCL":
				newDealer.FiltersClear();
				System.out.println("\nFilters cleared successfully.");
				break;
			default:
				System.out.println("\nERROR: Unknown command. Please try again!");
				break;
			}
			commandLine.close();
			System.out.print("\nEnter another command (Q to quit): ");
		}
		input.close();
	}
}

class CarDealership {
	private ArrayList<Car> cars;
	private ArrayList<Integer> filterCars;
	private double minPrice, maxPrice;
	private boolean AWD, electric, price;
	public boolean isEmpty;
	public Car carLastBought;

	public CarDealership () {
		cars = new ArrayList<Car>();
		filterCars = new ArrayList<Integer>();
		this.isEmpty = true;
	}

	/**
	 * @param newCars ArrayList of Car objects
	 */
	public void addCars(ArrayList<Car> newCars) {
		cars.addAll(newCars);
		this.isEmpty = false;
		for (int i = 0; i < cars.size(); i++) {
			filterCars.add(i);
		}
	}

	/**
	 * @param index of the car to buy
	 * @return carLastBought Car object
	 */
	public Car buyCar(int index) {
		if (index < cars.size()) {
			carLastBought = cars.get(index);
			cars.remove(index);
			filterCars.remove(filterCars.size() - 1);
			if (cars.size() <= 0)
				this.isEmpty = true;
			return carLastBought;
		}
		return null;
	}
	
	/**
	 * @param car to return
	 */
	public void returnCar(Car car) {
		if (car != null) {
			cars.add(car);
			filterCars.add(new Integer(cars.indexOf(car)));
			this.isEmpty = false;
			carLastBought = null;
		}
	}
	public boolean checkFilterEmpty() {
		if (filterCars.size() <= 0)
			return true;
		return false;
	}

	/* TO DO - Filter has bugs!!*/
	public void displayInventory() {
		for (int i = 0; i < cars.size(); i++) {
			Car currentCar = cars.get(i);
			String output = String.format("%-4d %s", i, currentCar.display());
			if (filterCars.contains(i)) {
				if (electric) {
					if(currentCar.getPower() == Car.ELECTRIC_MOTOR)
						System.out.println(output);
					else
						filterCars.remove(new Integer(i));
				}
				else if (AWD) {
					if(currentCar.isAWD())
						System.out.println(output);
				}
				else if (price) {
					if(currentCar.getPrice() >= minPrice && currentCar.getPrice() <= maxPrice)
						System.out.println(output);
					else
						filterCars.remove(new Integer(i));
				}
				else if (electric && AWD) {
					if(currentCar.getPower() == Car.ELECTRIC_MOTOR && currentCar.isAWD())
						System.out.println(output);
					else
						filterCars.remove(new Integer(i));
				}
				else if (AWD && price) {
					if (currentCar.isAWD() && currentCar.getPrice() >= minPrice && currentCar.getPrice() <= maxPrice)
						System.out.println(output);
					else
						filterCars.remove(new Integer(i));
				}
				else if (electric && price) {
					if (currentCar.getPower() == Car.ELECTRIC_MOTOR && currentCar.getPrice() >= minPrice && currentCar.getPrice() <= maxPrice)
						System.out.println(output);
					else
						filterCars.remove(new Integer(i));
				}
				else if (electric && AWD && price) {
					if (currentCar.getPower() == Car.ELECTRIC_MOTOR && currentCar.isAWD() && currentCar.getPrice() >= minPrice && currentCar.getPrice() <= maxPrice)
						System.out.println(output);
					else
						filterCars.remove(new Integer(i));
				}
				else
					System.out.println(output);
			}
		}
	}

	public void filterByElectric() {
		this.electric = true;
	}

	public void filterByAWD() {
		this.AWD = true;
	}

	/**
	 * @param minPrice
	 * @param maxPrice
	 */
	public void filterByPrice(double minPrice, double maxPrice) {
		this.price = true;
		this.minPrice = minPrice;
		this.maxPrice = maxPrice;
	}

	public void FiltersClear() {
		this.AWD = false;
		this.electric = false;
		this.price = false;
		this.minPrice = 0.0;
		this.maxPrice = 0.0;
		filterCars.clear();
		for (int i = 0; i < cars.size(); i++) {
			filterCars.add(i);
		}
	}

	public void sortByPrice() {
		Collections.sort(cars);
	}

	/* TO DO */
	public void sortBySafetyRating() {

	}

	/* TO DO */
	public void sortByMaxRange() {
		
	}
}

class Vehicle {
	private String mfr, color, powerString;
	private int power, numWheels;
	public static final int ELECTRIC_MOTOR = 0;
	public static final int GAS_ENGINE = 1;
	
	/**
	 * @param mfr
	 * @param color
	 * @param power
	 * @param numWheels
	 */
	public Vehicle (String mfr, String color, int power, int numWheels) {
		this.mfr = mfr;
		this.color = color;
		this.power = power;
		this.numWheels = numWheels;
	}

	/**
	 * @return the mfr
	 */
	public String getMfr() {
		return mfr;
	}
	/**
	 * @param mfr the mfr to set
	 */
	public void setMfr(String mfr) {
		this.mfr = mfr;
	}

	/**
	 * @return the color
	 */
	public String getColor() {
		return color;
	}
	/**
	 * @param color the color to set
	 */
	public void setColor(String color) {
		this.color = color;
	}

	/**
	 * @return the power
	 */
	public int getPower() {
		return power;
	}
	/**
	 * @param power the power to set
	 */
	public void setPower(int power) {
		this.power = power;
	}

	/**
	 * @return the numWheels
	 */
	public int getNumWheels() {
		return numWheels;
	}
	/**
	 * @param numWheels the numWheels to set
	 */
	public void setNumWheels(int numWheels) {
		this.numWheels = numWheels;
	}

	/**
	 * @param power
	 * @return powerString
	 */
	public String checkPower(int power) {
		switch (power) {
			case Car.ELECTRIC_MOTOR:
				powerString = "ELECTRIC_MOTOR";
				break;
			case Car.GAS_ENGINE:
				powerString = "GAS_ENGINE";
				break;
			default:
				break;
		}
		return powerString;
	}

	/**
	 * @return the equality of two vehicles
	 */
	public boolean equals(Object other) {
		Vehicle otherVehicle = (Vehicle) other;
		return (this.mfr.equals(otherVehicle.mfr)) && (this.power == otherVehicle.power) && (this.numWheels == otherVehicle.numWheels);
	}

	/** 
	 * @return manufacturer name and color
	 */
	public String display() {
		return String.format("%-7s %-6s", mfr, color);
	}
}

class Car extends Vehicle implements Comparable<Car> {
	private int model;
	private int maxRange;
	private double safetyRating, price;
	private boolean AWD;
	private String modelString;
	public static final int SEDAN = 0;
	public static final int SUV = 1;
	public static final int SPORTS = 2;
	public static final int MINIVAN = 3;

	/**
	 * @param mfr
	 * @param color
	 * @param power
	 * @param numWheels
	 * @param model
	 * @param maxRange
	 * @param safetyRating
	 * @param price
	 * @param aWD
	 */
	public Car (String mfr, String color, int power, int numWheels, int model, int maxRange, double safetyRating, double price, boolean aWD) {
		super(mfr, color, power, numWheels);
		this.model = model;
		this.maxRange = maxRange;
		this.safetyRating = safetyRating;
		this.price = price;
		this.setAWD(aWD);
	}

	/**
	 * @return the model
	 */
	public int getModel() {
		return model;
	}
	/**
	 * @param model the model to set
	 */
	public void setModel(int model) {
		this.model = model;
	}

	/**
	 * @return the maxRange
	 */
	public int getMaxRange() {
		return maxRange;
	}
	/**
	 * @param maxRange the maxRange to set
	 */
	public void setMaxRange(int maxRange) {
		this.maxRange = maxRange;
	}
	
	/**
	 * @return the safetyRating
	 */
	public double getSafetyRating() {
		return safetyRating;
	}
	/**
	 * @param safetyRating the safetyRating to set
	 */
	public void setSafetyRating(double safetyRating) {
		this.safetyRating = safetyRating;
	}

	/**
	 * @return the price
	 */
	public double getPrice() {
		return price;
	}
	/**
	 * @param price the price to set
	 */
	public void setPrice(double price) {
		this.price = price;
	}
	
	/**
	 * @return the aWD
	 */
	public boolean isAWD() {
		return AWD;
	}
	/**
	 * @param aWD the aWD to set
	 */
	public void setAWD(boolean aWD) {
		this.AWD = aWD;
	}

	/**
	 * @return equality of Car object
	 */
	public boolean equals(Object other) {
		Car otherCar = (Car) other;
		return super.equals(otherCar) && this.model == otherCar.model && (this.AWD == otherCar.AWD);
	}

	/**
	 * @return comparsion value of two Car objects
	 */
	public int compareTo(Car other) {
		return new Double(this.price).compareTo(other.price);
	}
	
	/**
	 * @param model
	 * @return modelString
	 */
	public String checkModel(int model) {
		switch (model) {
			case Car.SEDAN:
				modelString = "SEDAN";
				break;
			case Car.SUV:
				modelString = "SUV";
				break;
			case Car.SPORTS:
				modelString = "SPORTS";
				break;
			case Car.MINIVAN:
				modelString = "MINIVAN";
				break;
			default:
				break;
		}
		return modelString;
	}

	/**
	 * @return the Car object specifications
	 */
	public String display() {
		return String.format("%s %-7s %-5d %-5.2f %-10.2f %-6b", super.display(), checkModel(model), maxRange, safetyRating, price, AWD);
	}
}

class ElectricCar extends Car {
	private int rechargeTime;
	private String batteryType;

	/**
	 * @param mfr
	 * @param color
	 * @param power
	 * @param numWheels
	 * @param model
	 * @param maxRange
	 * @param safetyRating
	 * @param price
	 * @param aWD
	 * @param rechargeTime
	 * @param batteryType
	 */
	public ElectricCar (String mfr, String color, int power, int numWheels, int model, int maxRange, double safetyRating, double price, boolean aWD, int rechargeTime, String batteryType) {
			super(mfr, color, power, numWheels, model, maxRange, safetyRating, price, aWD);
			this.rechargeTime = rechargeTime;
			this.setBatteryType(batteryType);
	}
	/**
	 * @return the rechargeTime
	 */
	public int getRechargeTime() {
		return rechargeTime;
	}
	/**
	 * @param rechargeTime the rechargeTime to set
	 */
	public void setRechargeTime(int rechargeTime) {
		this.rechargeTime = rechargeTime;
	}

	/**
	 * @return the batteryType
	 */
	public String getBatteryType() {
		return batteryType;
	}
	/**
	 * @param batteryType the batteryType to set
	 */
	public void setBatteryType(String batteryType) {
		this.batteryType = batteryType;
	}

	public String display() {
		return String.format("%s %-3d %-5s", super.display(), rechargeTime, batteryType);
	}
}