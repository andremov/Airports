/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package aeropuertos;

import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author Andres
 */
public class SettingsWindow extends JFrame {
	
	JLabel infoDisplay;
	JButton processBtn;
	
	JLabel cityNameDisplay;
	JTextField cityName;
	JButton addCityBtn;
	JButton remCityBtn;
	
	JLabel airportCostDisplay;
	JTextField airportCost;
	
	JTable citiesTable;
	
	public SettingsWindow(boolean canCancel) {
		setLayout(null);
		setSize(800,600);
		setLocationRelativeTo(null);
		setResizable(false);
		setTitle("Conexiones");
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		
		init();
		
		setVisible(true);
	}
	
	private void init() {
		
		citiesTable = new JTable();
		citiesTable.setModel(new TableModel(new String[] {"Nombre"},0));
		citiesTable.setRowHeight(30);
		
		JScrollPane scroll = new JScrollPane(citiesTable);
		scroll.setSize(600, 559);
		scroll.setLocation(1,1);
		add(scroll);
		
		cityNameDisplay = new JLabel("Nombre:");
		cityNameDisplay.setSize(50, 35);
		cityNameDisplay.setLocation(605, 1);
		add(cityNameDisplay);
		
		cityName = new JTextField();
		cityName.setSize(120, 35);
		cityName.setFont(new Font("Arial",Font.PLAIN,20));
		cityName.setLocation(660, 1);
		add(cityName);
		
		addCityBtn = new JButton("Agregar Ciudad");
		addCityBtn.setSize(175, 50);
		addCityBtn.setLocation(605, 37);
		addCityBtn.addActionListener(new ActionListener( ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				addCity();
			}
		});
		add(addCityBtn);
		
		remCityBtn = new JButton("Eliminar Ciudad");
		remCityBtn.setSize(175, 50);
		remCityBtn.setLocation(605, 90);
		remCityBtn.addActionListener(new ActionListener( ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				removeCity();
			}
		});
		add(remCityBtn);
		
		airportCostDisplay = new JLabel("Costo de Aeropuerto:");
		airportCostDisplay.setSize(180, 40);
		airportCostDisplay.setLocation(605, 140);
		add(airportCostDisplay);
		
		airportCost = new JTextField();
		airportCost.setSize(175, 40);
		airportCost.setFont(new Font("Arial",Font.BOLD,30));
		airportCost.setLocation(605, 180);
		add(airportCost);
		
		final String text = "Ingrese '-1' para inhabilitar una conexion.";
        final String html1 = "<html><body style='width: ";
        final String html2 = "px'>";
		infoDisplay = new JLabel(html1+175+html2+text);
		infoDisplay.setSize(175, 40);
		infoDisplay.setLocation(605, 320);
		add(infoDisplay);
		
		processBtn = new JButton("Resolver");
		processBtn.setSize(175, 60);
		processBtn.setLocation(605, 400);
		processBtn.addActionListener(new ActionListener( ) {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					Window.airportCost = Integer.parseInt(airportCost.getText());
					create();
					Window.solve();
				} catch(Exception ex) {
					
				}
			}
		});
		add(processBtn);
	}
	
	private void addCity() {
		String name = cityName.getText();
		if (!name.isEmpty()) {
			DefaultTableModel model = (DefaultTableModel) citiesTable.getModel();
			
			Object[] columnData = new Object[citiesTable.getRowCount()];
			model.addColumn(name,columnData);
			
			Object[] rowData = new Object[citiesTable.getColumnCount()];
			for (int i = 1; i < rowData.length; i++) {
				rowData[i] = "10";
			}
			rowData[0] = name;
			model.addRow(rowData);
			
			cityName.setText("");
			create();
		}
	}
	
	private void removeCity() {
		int row = citiesTable.getSelectedRow();
		if (row >= 0) {
			DefaultTableModel model = (DefaultTableModel) citiesTable.getModel();
			model.removeRow(row);
			((TableModel)model).removeColumn(row+1);
			create();
		}
	}
	
	private void create() {
		DefaultTableModel model = (DefaultTableModel) citiesTable.getModel();
		
		try {
			City[] cities = new City[model.getRowCount()];

			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 1; j < i+1; j++) {
					citiesTable.setValueAt(citiesTable.getValueAt(i,j), j-1, i+1);
				}
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				citiesTable.setValueAt("-1", i, i+1);
			}


			for (int i = 0; i < model.getRowCount(); i++) {
				String name = (String) model.getValueAt(i,0);
				int[] costos = new int[model.getColumnCount()];
				for (int j = 1; j < model.getColumnCount(); j++) {
					costos[j-1] = Integer.parseInt((String)model.getValueAt(i,j));
				}
				City newCity = new City(name,costos);
				cities[i] = newCity;
			}

			Window.setCities(cities);
		}catch (Exception e) {
			for (int i = 0; i < e.getStackTrace().length; i++) {
				System.err.println(e.getStackTrace()[i]);
			}
			JOptionPane.showMessageDialog(null,"Digite numeros validos.", "Error", JOptionPane.ERROR_MESSAGE);
		} finally {
			for (int i = 0; i < model.getRowCount(); i++) {
				for (int j = 1; j < model.getColumnCount(); j++) {
					if (i < j-1) {
						citiesTable.setValueAt("", i, j);
					}
				}
			}
			for (int i = 0; i < model.getRowCount(); i++) {
				citiesTable.setValueAt("", i, i+1);
			}
		}
	}
	private class TableModel extends DefaultTableModel {

		public TableModel(Object[] columnNames, int rowCount) {
			super(columnNames, rowCount);
		}
		
		@Override
		public boolean isCellEditable(int row, int col) {
			return (col != 0 && row > (col-1));
		}

		public void removeColumn(int col) {
			if (col != getColumnCount()-1) {
				for (int i = 0; i < this.getRowCount(); i++) {
					((Vector)dataVector.elementAt(i)).setElementAt(((Vector)dataVector.elementAt(i)).elementAt(col+1), col);
				}
			}
			columnIdentifiers.remove(col);
			fireTableStructureChanged();
		}
	}
}
