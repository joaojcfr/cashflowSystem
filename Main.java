package Classes;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;

import SubClasses.Cashier;
import SubClasses.DeleteService;
import SubClasses.NewService;
import SubClasses.SalesPanel;

public class Main extends JFrame implements KeyListener{
	private static final long serialVersionUID = 1L;
	
	//Menu GUI objects ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	JMenuBar menuBar = new JMenuBar();
	JMenu fileMenu = new JMenu("File");
	JMenu cashierMenu = new JMenu("Cashier");
	
	//Cashier Menu
	JMenuItem cashierItem = new JMenuItem("Cashier");
	JMenuItem productIn = new JMenuItem("Order In");
	JMenuItem reportItem = new JMenuItem("Reports");
	
	//file Menu
	JMenuItem newUserMenu = new JMenuItem("New User");
	JMenuItem deleteUserMenu = new JMenuItem("Delete User");
	JMenuItem salesMenu = new JMenuItem("Sales");
	
	
	DataBaseManagement dbm = new DataBaseManagement();
	PreparedStatement loadSales = dbm.preparedStatement("SELECT * FROM sales;");
	PreparedStatement loadCashier = dbm.preparedStatement("SELECT * FROM cashflow;");
	PreparedStatement productSold;
	PreparedStatement insertSale;
	
	ResultSet salesInformation;
	ResultSet cashierInformation;
	
	//GUI Frame objects -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	JPanel systemPanel = new JPanel();
	
	JTabbedPane tabsPane = new JTabbedPane();
	
	CashFlow cashFlowPanel = new CashFlow();
	ServicesPanel servicesPanel = new ServicesPanel(dbm);
	StockPanel stockControlPanel = new StockPanel(dbm);
	
	//GUI Objects -----------------------------------------------------------------------------------------------------------------------------------------------
	JLabel operatorLbl = new JLabel("Operator: ");
	JLabel dateLbl = new JLabel("Date: ");
	JLabel timeLbl = new JLabel("Time: ");
	
	User user;
	
	ArrayList<Sale> sales = new ArrayList<>();
	ArrayList<Operation> operations = new ArrayList<>();
	private double totalCash = 0;
		
	public Main(User user) {
		this.user = user;
		user.setPatent(1);
		
		applyPatent(user);
		
		loadPanels();
		loadSales();
		loadCashier();
		addComponents();
		editComponents();
		addEvents();
		new Timer().start();
	}
	
	public void loadPanels() {
		setSize(1000,700);
		setTitle("Aloha Bikes");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setVisible(true);	
		
		systemPanel.setLayout(new BoxLayout(systemPanel, BoxLayout.X_AXIS));
		
	}
	public void loadSales() {
		salesInformation = dbm.result(loadSales);
		sales.clear();
		
		int saleID;
		String idsAcumulator = "";
		String namesAcumulator = "";
		String priceAcumulator = "";
		String quantityAcumulator = "";
		double totalOrder = 0;
		double currentCashier = 0;
		String operatorName = "";
		Date date;
		Time time;
			try {
				while(salesInformation.next()) {
					saleID = salesInformation.getInt("idsales");
					idsAcumulator =  salesInformation.getString("proserid");
					namesAcumulator = salesInformation.getString("prosername");
					priceAcumulator = salesInformation.getString("proserprice");
					quantityAcumulator = salesInformation.getString("proserquantity");
					totalOrder = salesInformation.getDouble("totalorder");
					currentCashier = salesInformation.getDouble("currentincashier");
					operatorName = salesInformation.getString("operatorname");
					date = salesInformation.getDate("date");
					time = salesInformation.getTime("time");
					
					Sale newSale = new Sale();
					
					newSale.setSaleID(saleID);
					newSale.setProserID(idsAcumulator.split(","));
					newSale.setProSerName(namesAcumulator.split(","));
					newSale.setProSerPrice(priceAcumulator.split(","));
					newSale.setProSerQuantity(quantityAcumulator.split(","));
					newSale.setTotalOrder(totalOrder);
					newSale.setCurrentInCashier(currentCashier);
					newSale.setOperatorName(operatorName);
					newSale.setDate(date);
					newSale.setTime(time);
					
					sales.add(newSale);
					
				}
			} catch (SQLException e) {
				JOptionPane.showMessageDialog(this, "Error loading sales table from database" + e);
			}
	}
	public void loadCashier() {
		cashierInformation = dbm.result(loadCashier);
	
		int id = 0;
		String operation = "";
		double currentincashier = 0;
		double value = 0;
		double afterOperation = 0;
		Date date = null;
		Time time = null;
		try {
			while(cashierInformation.next()) {
				id = cashierInformation.getInt("idoperation");
				operation = cashierInformation.getString("operation");
				currentincashier = cashierInformation.getDouble("currentincashier");
				value = cashierInformation.getDouble("value");
				afterOperation = cashierInformation.getDouble("afteroperation");
				date = cashierInformation.getDate("date");
				time = cashierInformation.getTime("time");
				
				Operation newOperation = new Operation();
				
				newOperation.setIdOperation(id);
				newOperation.setOperationName(operation);
				newOperation.setCurrentInCashier(currentincashier);
				newOperation.setValue(value);
				newOperation.setAfterOperation(afterOperation);
				newOperation.setDate(date);
				newOperation.setTime(time);
				
				operations.add(newOperation);
				
				totalCash = afterOperation;
				
			}
		} catch (SQLException e) {JOptionPane.showMessageDialog(this, "Error loading cashier table" + e);}
	}
	public void applyPatent(User user) {
		if (user.getPatent() == 1) {
			
		}
		if(user.getPatent() == 2) {
			fileMenu.setEnabled(false);
			
			servicesPanel.newServiceBtn.setEnabled(false);
			servicesPanel.updateServiceBtn.setEnabled(false);
			servicesPanel.deleteServiceBtn.setEnabled(false);
			
			stockControlPanel.newProductBtn.setEnabled(false);
			stockControlPanel.deleteProductBtn.setEnabled(false);
			stockControlPanel.updateProductBtn.setEnabled(false);
		}
	}
	
	public void addComponents() {
		
		// adding menus -------------------------------------------------------------------------
		cashierMenu.add(cashierItem);
		cashierMenu.add(productIn);
		cashierMenu.add(reportItem);
		
		fileMenu.add(newUserMenu);
		fileMenu.add(deleteUserMenu);
		fileMenu.add(salesMenu);
		
		menuBar.add(fileMenu);
		menuBar.add(cashierMenu);
		add(menuBar, BorderLayout.PAGE_START);
		
		//Adding panels-------------------------------------------------------------------------
		add(BorderLayout.CENTER, tabsPane);
			tabsPane.addTab("Cash Flow", cashFlowPanel);
			tabsPane.addTab("Services", servicesPanel);
			tabsPane.addTab("Stock Control", stockControlPanel);
			
		add(systemPanel, BorderLayout.PAGE_END);
			systemPanel.add(operatorLbl);
			systemPanel.add(Box.createHorizontalGlue());
			systemPanel.add(dateLbl);
			systemPanel.add(timeLbl);
		
	}
	public void editComponents() {
		
		operatorLbl.setBorder(new EmptyBorder(5, 5, 5, 0));
		timeLbl.setBorder(new EmptyBorder(5, 10, 5, 5));
		
		operatorLbl.setText("Operator: " + user.getFirstName());
		
	}
	public void addServiceButtonEvents() {
		for(JButton jb: servicesPanel.buttons) {
			jb.addActionListener(e -> {
				serviceToCashFlow(jb.getText());
				jb.setBackground(new Color(75, 178, 240, 94));
			});
			
		}
	}
	public void addEvents() {
		addServiceButtonEvents();
		
		cashFlowPanel.removeBtn.addActionListener(e -> removeItem(cashFlowPanel.cashFlowData));
		cashFlowPanel.checkOutBtn.addActionListener(e -> finishOrder(cashFlowPanel.cashFlowData));
		cashFlowPanel.discountBtn.addActionListener(e -> applyDiscount(-(Double.parseDouble(JOptionPane.showInputDialog("Enter Discount Value")))));
		
		cashFlowPanel.productIdTF.addKeyListener(this);
		cashFlowPanel.serviceIdTF.addKeyListener(this);
		
		cashFlowPanel.productQuantityTF.addKeyListener(this);
		cashFlowPanel.serviceQuantityTF.addKeyListener(this);
		
		//Sub-Frames
		Collections.reverse(sales);
		salesMenu.addActionListener(e -> new SalesPanel(sales));
		
		servicesPanel.newServiceBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == servicesPanel.newServiceBtn) {
					NewService newService = new NewService(dbm);
					newService.addWindowListener(new java.awt.event.WindowAdapter() {
				        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
				        	servicesPanel.cleanData();
				        	servicesPanel.loadDataBase();
				        	servicesPanel.populatePanel();
				        	servicesPanel.validate();
				        	addServiceButtonEvents();
				            newService.dispose();
				        }
				    });
				}
			}
		});
		servicesPanel.deleteServiceBtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(e.getSource() == servicesPanel.deleteServiceBtn) {
					DeleteService deleteService = new DeleteService(servicesPanel.services,dbm);
					deleteService.addWindowListener(new java.awt.event.WindowAdapter() {
				        public void windowClosed(java.awt.event.WindowEvent windowEvent) {
				        	servicesPanel.cleanData();
				        	servicesPanel.loadDataBase();
				        	servicesPanel.populatePanel();
				        	servicesPanel.validate();
				        	addServiceButtonEvents();
				        	deleteService.dispose();
				        }
				    });
				}
			}
		});
		Collections.reverse(operations);
		cashierItem.addActionListener(e -> new Cashier(operations, totalCash));
	}
	
	private void serviceToCashFlow(String buttonText) {
		for(Service s : servicesPanel.services) {
			if(s.getServiceName().equals(buttonText)) {
				for(int i = 0 ; i < cashFlowPanel.cashFlowData.length; i++) {
					if(cashFlowPanel.cashFlowData[i][0] == null) {
						cashFlowPanel.cashFlowData[i][0] = s.getServiceID();
						cashFlowPanel.cashFlowData[i][1] = s.getServiceName();
						cashFlowPanel.cashFlowData[i][2] = s.getServiceDescription();
						cashFlowPanel.cashFlowData[i][3] = s.getServicePrice();
						cashFlowPanel.cashFlowData[i][4] = 1;
						cashFlowPanel.servicePriceTF.setText(s.getServicePrice()+"");
						break;
					}
				}
				cashFlowPanel.startOrder();
				break;
			}
		}
		updateTotal(cashFlowPanel.cashFlowData);
		cashFlowPanel.cashFlowTable.updateUI();
		tabsPane.setSelectedIndex(0);
	}
	public void applyDiscount(double discountValue) {
		for(int i = 0 ; i < cashFlowPanel.cashFlowData.length; i++) {
			if(cashFlowPanel.cashFlowData[i][0] == null) {
				cashFlowPanel.cashFlowData[i][0] = 000;
				cashFlowPanel.cashFlowData[i][1] = "Discount";
				cashFlowPanel.cashFlowData[i][2] = "Discount applied";
				cashFlowPanel.cashFlowData[i][3] = discountValue;
				cashFlowPanel.cashFlowData[i][4] = 1;
				break;
			}
		}
		updateTotal(cashFlowPanel.cashFlowData);
		cashFlowPanel.cashFlowTable.updateUI();
	}
	private void addToCashFlow(int productID, int serviceID, int productQuantity,int serviceQuantity, JTextField textF) {
		boolean found = false;
		
		if(textF.equals(cashFlowPanel.productIdTF)) {
			for(Product p: stockControlPanel.products) {
				if(p.getProductID() == productID) {
					for(int i = 0; i < cashFlowPanel.cashFlowData.length; i ++) {
						if(cashFlowPanel.cashFlowData[i][0] == null) {
							cashFlowPanel.cashFlowData[i][0] = p.getProductID();
							cashFlowPanel.cashFlowData[i][1] = p.getProductName();
							cashFlowPanel.cashFlowData[i][2] = p.getProductDescription();
							cashFlowPanel.cashFlowData[i][3] = p.getProductPrice();
							cashFlowPanel.cashFlowData[i][4] = productQuantity;
							cashFlowPanel.priceTF.setText(p.getProductPrice()+"");
							break;
						}					
					}	
					found = true;
					break;
				}		
			}
			if(!found) {JOptionPane.showMessageDialog(this, "Product has not been found in Data Base");}
		}
		if(textF.equals(cashFlowPanel.serviceIdTF)) {
			for(Service s : servicesPanel.services) {
				if(s.getServiceID() == serviceID) {
					for(int i = 0 ; i < cashFlowPanel.cashFlowData.length; i++) {
						if(cashFlowPanel.cashFlowData[i][0] == null) {
							cashFlowPanel.cashFlowData[i][0] = s.getServiceID();
							cashFlowPanel.cashFlowData[i][1] = s.getServiceName();
							cashFlowPanel.cashFlowData[i][2] = s.getServiceDescription();
							cashFlowPanel.cashFlowData[i][3] = s.getServicePrice();
							cashFlowPanel.cashFlowData[i][4] = serviceQuantity;
							cashFlowPanel.servicePriceTF.setText(s.getServicePrice()+"");
							break;
						}
					}
					found = true;
					break;
				}
			}
			if(!found) {JOptionPane.showMessageDialog(this, "Service has not been found in Data Base");}
		}
		updateTotal(cashFlowPanel.cashFlowData);
		cashFlowPanel.cashFlowTable.updateUI();
		validate();
	}
	private void removeItem(Object[][] data) {
		String remove = (String) cashFlowPanel.cashFlowTable.getValueAt(cashFlowPanel.cashFlowTable.getSelectedRow(), 1);
		for(int i = 0; i < data.length; i ++) {
			if(data[i][1].equals(remove)) {
				data[i][0] = null;
				data[i][1] = null;
				data[i][2] = null;
				data[i][3] = null;
				data[i][4] = null;
				break;
			}
		}
		for(int i = 0; i < data.length-1; i++) {
			if(data[i][0] == null && data[i+1][0] != null) {
				data[i][0] = data[i+1][0];
				data[i][1] = data[i+1][1];
				data[i][2] = data[i+1][2];
				data[i][3] = data[i+1][3];
				data[i][4] = data[i+1][4];
				
				data[i+1][0] = null;
				data[i+1][1] = null;
				data[i+1][2] = null;
				data[i+1][3] = null;
				data[i+1][4] = null;
			}
		}
		updateTotal(cashFlowPanel.cashFlowData);
		cashFlowPanel.cashFlowTable.updateUI();
	}
	private void updateTotal(Object[][] data) {
		double total=0;
		for(int i = 0; i < data.length; i++) {
			if(data[i][3] != null) {
				total+= ((double) data[i][3]) * ((int)data[i][4]);
			}
		}
		cashFlowPanel.totalLbl.setText("Total:       "+ total);
	}
	private void finishOrder(Object[][] data) {
		
		String idsAcumulator = "";
		String namesAcumulator = "";
		String priceAcumulator = "";
		String quantityAcumulator = "";
		String operatorName = user.getFirstName() + " " + user.getSecondName();
		double totalOrder = 0;
		
		if(JOptionPane.showConfirmDialog(this, "Do you really want to Check out?") == JOptionPane.YES_OPTION) {
		
		for(int i = 0; i < data.length; i ++) {
			if(data[i][0] != null) {
				
				idsAcumulator += data[i][0] + ",";
				namesAcumulator += data[i][1] + ",";
				priceAcumulator += data[i][3] + ",";
				quantityAcumulator += data[i][4] +",";
				totalOrder += ((double) data[i][3]) * ((int)data[i][4]);
			}
		}
		
		DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");  
		LocalDateTime now = LocalDateTime.now();
		if(!idsAcumulator.equals("")) {
			insertSale = dbm.preparedStatement("INSERT INTO `alohabikes`.`sales`"+
			" (`proserid`, `prosername`, `proserprice`, `proserquantity`, `totalorder`, `currentincashier`, `operatorname`, `date`, `time`)"+
			" VALUES ('"+idsAcumulator+"', '"+namesAcumulator+"', '"+priceAcumulator+"', '"+quantityAcumulator+"', '"+totalOrder+"', '"+totalCash+"', '"+operatorName+"', '"+df.format(now)+"', '"+tf.format(now)+"');");
			
			dbm.execute(insertSale);
			cashFlowPanel.cancelOrder();
			loadSales();
			
			productSold(idsAcumulator, quantityAcumulator);
			totalCash += totalOrder;
			stockControlPanel.loadProductsData();
			stockControlPanel.stockTable.updateUI();
			JOptionPane.showMessageDialog(this, "Your order has been executed");
			}else {JOptionPane.showMessageDialog(this, "Please select a product or service to proceed with the Check out.");}
		}
		
	}
	public void productSold(String ids, String quantities) {
		
		String[] idArray = ids.split(",");
		String[] quantityArray = quantities.split(",");
		
		for(int i = 0; i < idArray.length; i++) {
			productSold = dbm.preparedStatement("UPDATE `alohabikes`.`products` SET `quantity` = quantity - "+quantityArray[i]+" WHERE (`idproducts` = '"+idArray[i]+"');");
			dbm.executeUpdate(productSold);
		}
	}
	public static void main(String[] args) {
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, you can set the GUI to another look and feel.
		}
		new Main(new User());
		
	}

	public class Timer extends Thread{
		public void run() {
			while(true) {
				DateTimeFormatter df = DateTimeFormatter.ofPattern("dd-MM-yyyy");  
				DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm:ss");  
				LocalDateTime now = LocalDateTime.now(); 
				timeLbl.setText("Clock: " + tf.format(now));
				dateLbl.setText("Date: " + df.format(now));
				try {sleep(1000);} catch (InterruptedException e) {}
			}
		}
	}

	@Override
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		int productID;
		int productQuantity;
		int serviceID;
		int serviceQuantity;
		
			if(e.getKeyCode() == 10 && e.getSource() == cashFlowPanel.productIdTF) {
				if(!cashFlowPanel.productIdTF.getText().equals("")) {
				productID = Integer.parseInt(cashFlowPanel.productIdTF.getText());
				productQuantity = Integer.parseInt(cashFlowPanel.productQuantityTF.getText());
				serviceID = 0;
				serviceQuantity = 0;
				
				addToCashFlow(productID,serviceID,productQuantity, serviceQuantity , cashFlowPanel.productIdTF);
				}else {JOptionPane.showMessageDialog(this, "Please enter an ID in the field");}
			}
			
			if(e.getKeyCode() == 10 && e.getSource() == cashFlowPanel.productQuantityTF) {
				if(!cashFlowPanel.productIdTF.getText().equals("")) {
				productID = Integer.parseInt(cashFlowPanel.productIdTF.getText());
				productQuantity = Integer.parseInt(cashFlowPanel.productQuantityTF.getText());
				serviceID = 0;
				serviceQuantity = 0;
				
				addToCashFlow(productID,serviceID,productQuantity, serviceQuantity , cashFlowPanel.productIdTF);
				}else {JOptionPane.showMessageDialog(this, "Please enter an ID in the field");}
			}
		
		
			if(e.getKeyCode() == 10 && e.getSource() == cashFlowPanel.serviceIdTF) {
				if(!cashFlowPanel.serviceIdTF.getText().equals("")) {
				productID = 0;
				productQuantity = 0;
				serviceID = Integer.parseInt(cashFlowPanel.serviceIdTF.getText());
				serviceQuantity = Integer.parseInt(cashFlowPanel.serviceQuantityTF.getText());
				
				addToCashFlow(productID,serviceID,productQuantity, serviceQuantity , cashFlowPanel.serviceIdTF);
				}else {JOptionPane.showMessageDialog(this, "Please enter an ID in the field");}
			}
			
			if(e.getKeyCode() == 10 && e.getSource() == cashFlowPanel.serviceQuantityTF) {
				if(!cashFlowPanel.serviceIdTF.getText().equals("")) {
				productID = 0;
				productQuantity = 0;
				serviceID = Integer.parseInt(cashFlowPanel.serviceIdTF.getText());
				serviceQuantity = Integer.parseInt(cashFlowPanel.serviceQuantityTF.getText());
				
				addToCashFlow(productID,serviceID,productQuantity, serviceQuantity , cashFlowPanel.serviceIdTF);
				}else {JOptionPane.showMessageDialog(this, "Please enter an ID in the field");}
			}
		
	}
	public void keyReleased(KeyEvent e) {}
}
	
