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

	
	private Color color(double h, double s, double b) {
		return Color.getHSBColor((float)(h/360f),(float)(s/100f),(float)(b/100f));
	}
	
	@Override
	public void run() {
		createBufferStrategy(2);
		setBackground(color(51,54.1,91.4));
		addMouseListener(new MouseListener() {
			@Override
			public void mouseClicked(MouseEvent e) { Window.addCity(e.getX(), e.getY()); }
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

				getBufferStrategy().show();
			}
			
			try {
				Thread.sleep(100);
			} catch(Exception e){}
		}
	}
	
}
