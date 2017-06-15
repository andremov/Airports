/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuertos;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

/**
 *
 * @author Andres
 */
public class Display extends Canvas implements Runnable {

	/**
	 * devuelve el color dado por parametros en HSB
	 * @param h
	 * @param s
	 * @param b
	 * @return 
	 */
	private Color color(double h, double s, double b) {
		return Color.getHSBColor((float)(h/360f),(float)(s/100f),(float)(b/100f));
	}
	
	/**
	 * pinta el canvas
	 */
	@Override
	public void run() {
		createBufferStrategy(2);
		setBackground(color(51,54.1,91.4));
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) { Window.locateCity(e.getX(), e.getY()); }
			@Override
			public void mousePressed(MouseEvent e) {}
			@Override
			public void mouseReleased(MouseEvent e) {}
			@Override
			public void mouseEntered(MouseEvent e) {}
			@Override
			public void mouseExited(MouseEvent e) {}
		});
		while(true){
			Graphics g = getBufferStrategy().getDrawGraphics();
			
			if (Window.cities != null) {
				g.clearRect(0, 0, Window.WINDOW_X, Window.WINDOW_Y);
				g.setColor(Color.red);
				
				for (int i = 0; i < Window.cities.length; i++) {
					for (int j = 0; j < Window.cities.length; j++) {
						if (Window.cities[i].getConnection(j)) {
							int x1 = Window.cities[i].getCenterX();
							int y1 = Window.cities[i].getCenterY();
							int x2 = Window.cities[j].getCenterX();
							int y2 = Window.cities[j].getCenterY();
							g.drawLine(x1, y1, x2, y2);	
						}
					}
				}
				
				for (int i = 0; i < Window.cities.length; i++) {
					
					BufferedImage img = Window.cities[i].getImage();
					int x = Window.cities[i].getX();
					int y = Window.cities[i].getY();
					
					g.drawImage(img, x, y, null);
				}
                                
				if (Window.totalCost != 0){

					int fontSize = 30;
					java.awt.Font font = new java.awt.Font("Arial",java.awt.Font.BOLD,fontSize);
					java.awt.FontMetrics metrics = g.getFontMetrics(font);
					
					String costoDisplay = "Precio total: "+Window.totalCost;
					int fontX = 196 - metrics.stringWidth(costoDisplay);
					int fontY = 36 - metrics.getHeight();
					while (fontX < 0 || fontY < 0) {
						fontSize--;
						font = new java.awt.Font("Arial",java.awt.Font.BOLD,fontSize);
						metrics = g.getFontMetrics(font);
						fontX = 196 - metrics.stringWidth(costoDisplay);
						fontY = 36 - metrics.getHeight();
					}
					fontX = fontX / 2;
					fontY = (fontY / 2) + metrics.getAscent();
					g.setFont(font);

					g.setColor(Color.black);
					g.fillRect(2, getHeight()-40, 200, 40);

					g.setColor(Color.white);
					g.drawString(costoDisplay,2+fontX,getHeight()-40+fontY);
				}

				getBufferStrategy().show();
			}
			
			try {
				Thread.sleep(100);
			} catch(Exception e){}
		}
	}
	
}
