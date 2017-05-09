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
	static int airportCost;
	static int cityNum;
	
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
			cities[cityNum%cities.length].setX(x);
			cities[cityNum%cities.length].setY(y);
			cityNum++;
		}
	}
	
	public static void solve() {
//		int[] possibleConnections = new int[cities.length];
		System.out.println("Starting...");
		
		boolean changed = true;
		while (changed) {
			changed = case1and2();
		}
		
		changed = true;
		while (changed) {
			changed = case3();
			changed = changed || case4();
		}
		System.out.println("Done!");
	}
	
	private static boolean case1and2() {
		boolean changed = false;
		for (int i = 0; i < cities.length; i++) {
			if (!cities[i].isDone()) {
				int validWays = 0;
				int validWayIndex= -1;
				int cost = -1;
				int samePrice = 0;
				for (int j = 0; j < cities.length; j++) {
					if (cities[i].isValidConnection(j)) {
						if (cities[i].getTravelCost(j) < airportCost) {
							validWays++;
							validWayIndex = j;
							if (cities[i].getTravelCost(j) != cost) {
								cost = cities[i].getTravelCost(j);
								samePrice = 0;
							} else {
								samePrice++;
							}
						}
					}
				}
				if (validWays == 0) {
					// SI TODAS LAS CARRETERAS DE UNA CIUDAD
					// CUESTAN MAS QUE UN AEROPUERTO, SE PONE
					// UN AEROPUERTO
					cities[i].setState(City.STATE_AIR);
					System.out.println("CASE 1: "+(i+1)+" is now an airport!");
					changed = true;
				} else if (validWays == 1) {
					// SI LA CIUDAD SOLO TIENE UNA CARRETERA QUE
					// CUESTA MENOS QUE UN AEROPUERTO, SE PONE
					// ESA CARRETERA
					cities[i].setConnection(validWayIndex, true);
					cities[i].setState(City.STATE_CON);
					cities[validWayIndex].setState(City.STATE_AIR);
					System.out.println("CASE 2: "+(i+1)+" is now connected!");
					System.out.println("CASE 2: "+(validWayIndex+1)+" is now an airport!");
					changed = true;
				}
			}
		}
		return changed;
	}
	
	private static boolean case3() {
		boolean changed = false;
		for (int i = 0; i < cities.length; i++) {
			if (!cities[i].isDone()) {
				int cheapestRoad = -1;
				boolean[] areCheapest = new boolean[cities.length];
				for (int j = 0; j < cities.length; j++) {
					if (cities[i].isValidConnection(j)) {
						if (cities[i].getTravelCost(j) < cheapestRoad || cheapestRoad == -1) {
							if (!cities[j].isConnected()){
								// SOLO ES POSIBILIDAD SI
								// LA CIUDAD NO HA TERMINADO
								for (int k = 0; k < cities.length; k++) {
									areCheapest[k] = false;
								}
								areCheapest[j] = true;
								cheapestRoad = cities[i].getTravelCost(j);
							}
						} else if (cities[i].getTravelCost(j) == cheapestRoad) {
							areCheapest[j] = true;
						}
					}
				}
				int airportIndex = -1;
				int j = 0;
				while (j < cities.length && airportIndex == -1) {
					if (areCheapest[j] && cities[j].isAirport()) {
						airportIndex = j;
					}
					j++;
				}
				if (airportIndex != -1) {
					// SI LA CARRETERA MAS BARATA SE CONECTA
					// CON UNA CIUDAD QUE YA TIENE AEROPUERTO,
					// SE PONE ESA CARRETERA
					cities[i].setConnection(airportIndex,true);
					cities[i].setState(City.STATE_CON);
					System.out.println("CASE 3: "+(i+1)+" is now connected!");
					changed = true;
				}
			}
		}
		return changed;
	}
	
	private static boolean case4() {
		boolean changed = false;
		int lowestSum = -1;
		int lowestIndex = -1;
		for (int i = 0; i < cities.length; i++) {
			if (!cities[i].isDone()) {
				int sum = 0;
				for (int j = 0; j < cities.length; j++) {
					if (!cities[j].isDone() && cities[i].isValidConnection(j)) {
						sum = sum + cities[i].getTravelCost(j);
					}
				}
				if (sum < lowestSum || lowestSum == -1) {
					lowestIndex = i;
					lowestSum = sum;
					changed = true;
				}
			}
		}
		if (changed) {
			cities[lowestIndex].setState(City.STATE_AIR);
			System.out.println("CASE 4: "+(lowestIndex+1)+" is now an airport!");
		}
		return changed;
	}
	
	private void init() {
		cityNum = 0;
		
		screen = new Display();
		screen.setSize(WINDOW_X,WINDOW_Y);
		screen.setLocation(1,1);
		add(screen);
		
	}
	
}
