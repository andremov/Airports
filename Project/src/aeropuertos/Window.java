/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuertos;

import javax.swing.JFrame;

/**
 *
 * @author Andres
 */
public class Window extends JFrame {

	public static int CANVAS_X = 8;
	public static int CANVAS_Y = 31;
	public static int WINDOW_X = 800;
	public static int WINDOW_Y = 800;
	
	static Display screen;
	static City[] cities;
	static int numCities;
	static int totalCost;
	
	public Window() {
		setLayout(null);
		setSize(WINDOW_X+CANVAS_X,WINDOW_Y+CANVAS_Y);
		setLocationRelativeTo(null);
		setTitle("Aeropuertos [AMovilla & AVasquez]");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setResizable(false);
		
		init();
		
		setVisible(true);
		
		new Thread(screen).start();
	}
	
	public static void setCities(City[] pCities) {
		if (cities != null) {
			for (int i = 0; i < Integer.min(pCities.length, cities.length); i++) {
				pCities[i].setX(cities[i].getCenterX());
				pCities[i].setY(cities[i].getCenterY());
			}
		}
		cities = pCities;
	}
	
	public static void addCity(int x, int y) {
		if (cities != null) {
			cities[numCities%cities.length].setX(x);
			cities[numCities%cities.length].setY(y);
			numCities++;
		}
	}
	
	public static void solve() {
		System.out.println("Starting...");
		
		while(numCitiesMissing() > 0) {
			case1();
			case2();
			case3();
			case8();
			case4();
			case5();
			case6();
			case7();
			case9();
		}
		
		System.out.println("Finished.");
		
		totalCost = 0;
		for (int i = 0; i < cities.length; i++) {
			if (cities[i].isAirport()) {
				totalCost = totalCost + cities[i].getAirportCost();
				for (int j = 0; j < cities.length; j++) {
					if (cities[i].getConnection(j)){
						totalCost = totalCost + cities[i].getTravelCost(j);
					}
				}
			}
		}
	}
	
	/**
	 * IF A CITY HAS ABSOLUTELY NO POSSIBLE CONNECTIONS, IT HAS TO BE AN AIRPORT.
	 */
	private static void case1() {
		System.out.println("1");
		for (int i = 0; i < cities.length; i++) {
			if (!cities[i].isDone()) {
				if (cities[i].numValidWays() == 0) {
					cities[i].setState(City.STATE_AIR);
					System.out.println("CASE 1: "+cities[i].getName()+" is now an airport!");
				}
			}
		}
	}
	
	/**
	 * IF A CITY HAS ONLY ONE POSSIBLE CONNECTION, AND THE AIRPORT IS CHEAPER, 
	 * MAKE THE AIRPORT
	 * 
	 * IF THE CONNECTION IS CHEAPER, AND THE OTHER AIRPORT IS CHEAPER THAN THIS ONE,
	 * BUILD THE OTHER AIRPORT
	 */
	private static void case2() {
		System.out.println("2");
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				if (cities[i].numValidWays() == 1) {
					int airportCost = cities[i].getAirportCost();
					int validWayIndex = -1;
					int validWayCost = -1;
					for (int j = 0; j < numCities; j++) {
						if (cities[i].isValidConnection(j)) {
							validWayCost = cities[i].getTravelCost(j);
							validWayIndex = j;
						}
					}
					
					if (airportCost <= validWayCost) {
						cities[i].setState(City.STATE_AIR);
						System.out.println("CASE 2: "+cities[i].getName()+" is now an airport!");
					} else {
						int otherAirportCost = cities[validWayIndex].getAirportCost();
						if (otherAirportCost <= airportCost) {
							System.out.println("CASE 2: "+cities[validWayIndex].getName()+" is now an airport!");
							System.out.println("CASE 2: "+cities[validWayIndex].getName()+" is now connected!");
							cities[i].setState(City.STATE_CON);
							cities[i].setConnection(validWayIndex, true);

							cities[validWayIndex].setState(City.STATE_AIR);
							cities[validWayIndex].setConnection(i, true);
	//					} else {
							// PONER AEROPUERTO EN CIUDAD DESCONECTADA?
						}
					}
				}
			}
		}
	}
	
	/**
	 * IF THE AIRPORT IS THE CHEAPEST THING FOR A CITY, BUILD THE AIRPORT
	 */
	private static void case3() {
		System.out.println("3");
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				if (cities[i].numCheaperWays() == 0) {
					cities[i].setState(City.STATE_AIR);
					System.out.println("CASE 3: "+cities[i].getName()+" is now an airport!");
				}
			}
		}
	}
	
	/**
	 * IF A CITY HAS ONLY ONE CONNECTION CHEAPER THAN AN AIRPORT, AND THE OTHER
	 * CITY HAS AN AIRPORT, CONNECT THEM
	 * 
	 * IF THE OTHER CITY DOES NOT HAVE AN AIRPORT, COMPARE AIRPORT VALUES WITH
	 * THE OTHER CITIES
	 */
	private static void case4() {
		System.out.println("4");
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				if (cities[i].numCheaperWays() == 1) {
					int airportCost = cities[i].getAirportCost();
					int validWayIndex = -1;
					int validWayCost = -1;
					for (int j = 0; j < cities.length; j++) {
						if (cities[i].isCheapConnection(j)) {
							validWayCost = cities[i].getTravelCost(j);
							validWayIndex = j;
						}
					}
					if (cities[validWayIndex].isAirport()) {
						System.out.println("CASE 4: "+cities[i].getName()+" is now connected!");
						cities[i].setState(City.STATE_CON);
						cities[i].setConnection(validWayIndex, true);
					} else {
						int otherAirportCost = cities[validWayIndex].getAirportCost();
						if (otherAirportCost <= airportCost) {
							System.out.println("CASE 4: "+cities[validWayIndex].getName()+" is now an airport!");
							System.out.println("CASE 4: "+cities[i].getName()+" is now connected!");
							cities[i].setState(City.STATE_CON);
							cities[i].setConnection(validWayIndex, true);

							cities[validWayIndex].setState(City.STATE_AIR);
							cities[validWayIndex].setConnection(i, true);
//						} else {
//							System.out.println("CASE 4: "+cities[i].getName()+" is now an airport!");
//							System.out.println("CASE 4: "+cities[validWayIndex].getName()+" is now connected!");
//							cities[validWayIndex].setState(City.STATE_CON);
//							cities[validWayIndex].setConnection(i, true);
//
//							cities[i].setState(City.STATE_AIR);
//							cities[i].setConnection(validWayIndex, true);
						}
					}
				}
			}
		}
	}
	
	/**
	 * IF THE CHEAPEST CONNECTION FOR A CITY IS CHEAPER THAN BUILDING AN AIRPORT,
	 * AND IS ALREADY AN AIRPORT, BUILD THE CONNECTION
	 */
	private static void case5() {
		System.out.println("5");
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				if (cities[i].getCheapestTravelCost() <= cities[i].getAirportCost()) {
					if (cities[i].getNumCheapestConnections() == 1) {
						int cheapestIndex = cities[i].getCheapestConnections()[0];
						if (cities[cheapestIndex].isAirport()) {
							System.out.println("CASE 5: "+cities[i].getName()+" is now connected!");
							cities[i].setState(City.STATE_CON);
							cities[i].setConnection(cheapestIndex, true);
						}
					}
				}
			}
		}
	}
	
	/**
	 * IF THE CHEAPEST CONNECTION FOR A CITY IS CHEAPER THAN BUILDING AN AIRPORT,
	 * AND ONE OF THEM IS ALREADY AN AIRPORT, BUILD THE CONNECTION
	 */
	private static void case6() {
		System.out.println("6");
		for (int i = 0; i < cities.length; i++) {
			if (!cities[i].isDone()) {
				if (cities[i].getCheapestTravelCost() <= cities[i].getAirportCost()) {
					int[] cheapestConnections = cities[i].getCheapestConnections();
					int airportIndex = -1;
					for (int j = 0; j < cheapestConnections.length; j++) {
						if (cities[cheapestConnections[j]].isAirport()) {
							airportIndex = cheapestConnections[j];
						}
					}
					if (airportIndex != -1) {
						System.out.println("CASE 6: "+cities[i].getName()+" is now connected!");
						cities[i].setState(City.STATE_CON);
						cities[i].setConnection(airportIndex, true);

						cities[airportIndex].setConnection(i, true);
					}
				}
			}
		}
	}
	
	/**
	 * IF THE CHEAPEST AVAILABLE CONNECTION FOR A CITY IS CHEAPER THAN BUILDING AN AIRPORT,
	 * AND IS ALREADY AN AIRPORT, BUILD THE CONNECTION
	 * 
	 * IF NOT, COMPARE AIRPORT PRICES, BUILD CHEAPEST ONE AND CONNECT
	 */
	private static void case7() {
		System.out.println("7");
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				int lowestAvailableCost = -1;
				int lowestAvailableIndex = -1;
				for (int j = 0; j < numCities; j++) {
					boolean valid = cities[i].isValidConnection(j);
					boolean validCost = cities[i].getTravelCost(j) < cities[i].getAirportCost();
					boolean validState = !cities[j].isConnected();
					if (valid && validCost && validState) {
						int cost = cities[i].getTravelCost(j);
						if (!cities[j].isAirport()) {
							cost = cost + cities[j].getAirportCost();
						}
						if (cost < lowestAvailableCost || lowestAvailableCost == -1) {
							lowestAvailableCost = cities[i].getTravelCost(j);
							lowestAvailableIndex = j;
						}
					}
				}
				
				if (lowestAvailableIndex != -1) {
					if (cities[lowestAvailableIndex].isAirport()) {
						System.out.println("CASE 7: "+cities[i].getName()+" is now connected!");
						cities[i].setState(City.STATE_CON);
						cities[i].setConnection(lowestAvailableIndex, true);

						cities[lowestAvailableIndex].setConnection(i, true);
					} else {
						int thisAirportCost = cities[i].getAirportCost();
						int thatAirportCost = cities[lowestAvailableIndex].getAirportCost();
						
						int airportIndex = -1;
						int connectIndex = -1;
						if (thisAirportCost < thatAirportCost) {
							airportIndex = i;
							connectIndex = lowestAvailableIndex;
						} else if (thatAirportCost < thisAirportCost) {
							airportIndex = lowestAvailableIndex;
							connectIndex = i;
						} else {
							if (pendingCities(i) == 0) {
								connectIndex = i;
								airportIndex = lowestAvailableIndex;
							} else if (pendingCities(lowestAvailableIndex) == 0) {
								airportIndex = i;
								connectIndex = lowestAvailableIndex;
							}
						}
						
						if (airportIndex != -1) {
							System.out.println("CASE 7: "+cities[connectIndex].getName()+" is now connected!");
							System.out.println("CASE 7: "+cities[airportIndex].getName()+" is now an airport!");
							cities[connectIndex].setState(City.STATE_CON);
							cities[connectIndex].setConnection(airportIndex, true);

							cities[airportIndex].setConnection(connectIndex, true);
							cities[airportIndex].setState(City.STATE_AIR);
						}
					}
				}
			}
		}
	}
	
	/**
	 * FIND THE MOST EFFICIENT AIRPORT, BUILD IT
	 */
	private static void case8() {
		System.out.println("8");
		int bestCitiesRemoved = 1;
		int bestCost = -1;
		int bestIndex = -1;
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				int numCitiesRemoved = 0;
				int cost = cities[i].getAirportCost();
				for (int j = 0; j < numCities; j++) {
					if (!cities[j].isDone()) {
						if (cities[j].isCheapestConnection(i)) {
							numCitiesRemoved++;
							cost = cost + cities[i].getTravelCost(j);
						}
					}
				}
				
				if (cost < bestCost || bestCost == -1) {
					if (numCitiesRemoved >= bestCitiesRemoved) {
						bestIndex = i;
						bestCost = cost;
						bestCitiesRemoved = numCitiesRemoved;
					}
				}
			}
		}
		if (bestIndex != -1) {
			System.out.println("CASE 8: "+cities[bestIndex].getName()+" is now an airport!");
			cities[bestIndex].setState(City.STATE_AIR);
		}
	}
	
	/**
	 * DESTRUCTIVE CASE
	 * 
	 * IF A CITY HAS NO AVAILABLE OPTIONS CHEAPER THAN BUILDING AN AIRPORT, FINDS
	 * THE CHEAPEST CONNECTION THAT COSTS LESS THAN AN AIRPORT, AND BUILD THAT
	 */
	private static void case9() {
		System.out.println("9");
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				int validChoices = 0;
				for (int j = 0; j < numCities; j++) {
					boolean valid = cities[i].isValidConnection(j);
					boolean validCost = cities[i].getTravelCost(j) < cities[i].getAirportCost();
					boolean validState = !cities[j].isConnected();
					if (valid && validCost && validState) {
						validChoices++;
					}
				}
				if (validChoices == 0 && cities[i].numCheaperWays() > 0) {
					int bestChoice = -1;
					int bestCost = -1;
					for (int j = 0; j < numCities; j++) {
						boolean valid = cities[i].isValidConnection(j);
						boolean validCost = cities[i].getTravelCost(j) < cities[i].getAirportCost();
						if (valid && validCost) {
							if (cities[i].getTravelCost(j)+cities[j].getAirportCost() < bestCost || bestCost == -1) {
								bestCost = cities[i].getTravelCost(j)+cities[j].getAirportCost();
								bestChoice = j;
							}
						}
					}
					if (bestChoice != -1) {
						for (int j = 0; j < numCities; j++) {
							cities[bestChoice].setConnection(j, false);
						}
						cities[bestChoice].setConnection(i, true);
						cities[bestChoice].setState(City.STATE_AIR);
						System.out.println("CASE 8: "+cities[bestChoice].getName()+" is now disconnected!");
						System.out.println("CASE 8: "+cities[bestChoice].getName()+" is now an airport!");
						System.out.println("CASE 8: "+cities[i].getName()+" is now connected!");
						
						cities[i].setConnection(bestChoice, true);
						cities[i].setState(City.STATE_CON);
					}
				}
			}
		}
	}
	
	private static int pendingCities(int index) {
		int pc = 0;
		for (int i = 0; i < numCities; i++) {
			if (cities[index].isValidConnection(i) && !cities[i].isDone()) {
				
			}
		}
		return pc;
	}
	
	private void init() {
		numCities = 0;
		totalCost = 0;
                
		screen = new Display();
		screen.setSize(WINDOW_X,WINDOW_Y);
		screen.setLocation(1,1);
		add(screen);
		
	}
	
	private static int numCitiesMissing() {
		int num = 0;
		
		for (int i = 0; i < numCities; i++) {
			if (!cities[i].isDone()) {
				num++;
			}
		}
		
		return num;
	}
	
}
