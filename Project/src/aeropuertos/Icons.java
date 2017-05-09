/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuertos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Andres
 */
public class Icons {
	
	public static BufferedImage CITY_DIS;
	public static BufferedImage CITY_CON;
	public static BufferedImage CITY_AIR;
		
	public static void loadImages() {
		
		try {
			CITY_DIS = ImageIO.read(new File("assets/city_dis.png"));
			CITY_CON = ImageIO.read(new File("assets/city_con.png"));
			CITY_AIR = ImageIO.read(new File("assets/city_air.png"));
		} catch (IOException ex) {
			
		}
	}
	
}
