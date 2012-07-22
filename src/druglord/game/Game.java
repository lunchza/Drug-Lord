package druglord.game;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import druglord.items.*;
import druglord.player.Player;
import druglord.utils.NumericTextField;
import druglord.utils.Globals;

@SuppressWarnings("serial")
public class Game extends JFrame implements ActionListener, MouseListener, MouseMotionListener {
		
	//save file
	private static File saveFile;
	
	//The dimensions of the game frame
	private static int frameHeight;
	private static int frameWidth;
	
	//The Player playing the current game
	private Player player;
	
	//constants for determining the result of the main menu
	static final int CLICKED_NEW_GAME = 1;
	static final int CLICKED_CONTINUE = 2;
	static final int CLICKED_OPTIONS = 3;
	
	//Mouse coordinate
	private int mouseX, mouseY;
	
	//Travel coordinate
	private int curX, curY;
	
	//Determines if a player is currently en-route
	private boolean travelling;
	
	//Determines whether or not the shop dialog is shown
	private boolean shopActive;
	
	//Determines whether or not the stats dialog is shown
	private boolean statsActive;
	
	//World map image, current Plane image and paused image
	private Image mapImage, planeImg, pausedImg;
	
	//Shop tab image
	private ImageIcon shopImage, shopAltImage;
	
	//Stats tab image
	private ImageIcon statsImage, statsAltImage;
	
	//Plane images
	private Image planeUpImg, planeDownImg, planeLeftImg, planeRightImg;
	
	//Player portrait
	private static Image portrait;
	
	//Game drug market
	Market market;
	
	//list of cities
	private static ArrayList<City> cities;
	
	//Game panel
	private JPanel gamePanel;
	
	//Popup menu
	private JPopupMenu popupMenu;
	
	//menu items for the popup menu
	private JMenuItem travelButton, pricesButton, historiesButton;
	
	//Determines which city was clicked
	private City clickedCity;
	
	//Components on the button panel
	private JButton inventoryButton, statsButton, priceButton, shopButton;
	
	//Time control components
	private JButton pause, normalSpeed, doubleSpeed, quadSpeed, insaneSpeed;
		
	//The screen that shows the player's inventory
	private InventoryScreen invScreen;
	
	//The screen that shows city prices
	private PricesScreen priceScreen;
	
	//the screen that shows price histories
	private HistoryWindow historyWindow;
	
	//The game timer
	private GameTimer timer;
	
	/**
	 * SHOP ITEMS
	 */
	private JLabel marketLabel, invLabel;
	private JLabel cashLabel, sellLabel;
	private JList marketList, invList;
	private ListSelectionListener listListener;
	private JSlider slider;
	private JLabel quantityLabel;
	private JButton buyButton, sellButton, confirmButton;
	private boolean buying, selling;
	
	/**
	 * STATS ITEMS
	 */
	
	
	public Game()
	{
		super ("Druglord " + Globals.VERSION);
			
		frameWidth = 1024;
		frameHeight =768;
		
		setSize(frameWidth, frameHeight);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		mouseX = mouseY = 0;
		
		load();
		
		Start();
	}
	
	private void load()
	{
		market = new Market();
		
		cities = new ArrayList<City>();
		
		portrait = new ImageIcon("images/portrait.jpg").getImage();

		mapImage = new ImageIcon("images/worldmap.jpg").getImage();
		planeDownImg = new ImageIcon("images/plane-down.jpg").getImage();
		planeLeftImg = new ImageIcon("images/plane-left.jpg").getImage();
		planeRightImg = new ImageIcon("images/plane-right.jpg").getImage();
		planeUpImg = new ImageIcon("images/plane-up.jpg").getImage();
		
		planeImg = planeUpImg;
		
		pausedImg = new ImageIcon("images/paused.png").getImage();
		shopImage = new ImageIcon("images/shop.png");
		shopAltImage = new ImageIcon("images/shop2.png");
		statsImage = new ImageIcon("images/stats.png");
		statsAltImage = new ImageIcon("images/stats2.png");
		
		
		saveFile = new File("saves/save.bin");
		
		curX = curY = -1;
		
		shopActive = false;
		buying = selling = false;
		
		timer = new GameTimer();
		
		loadCityInfo();
		
		travelButton = new JMenuItem("Travel");
		travelButton.addActionListener(this);
		pricesButton = new JMenuItem("Prices");
		pricesButton.addActionListener(this);
		historiesButton = new JMenuItem("Trends");
		historiesButton.addActionListener(this);
		
		popupMenu = new JPopupMenu();
		popupMenu.add(travelButton);
		popupMenu.add(pricesButton);
		popupMenu.add(historiesButton);
		
		inventoryButton = new JButton("Inventory");
		inventoryButton.addActionListener(this);
		inventoryButton.setBounds(200, 20, 100, 25);
		
		//statsButton = new JButton("Stats");
		//statsButton.addActionListener(this);
		//statsButton.setBounds(300, 20, 100, 25);
		
		priceButton = new JButton("Prices");
		priceButton.addActionListener(this);
		priceButton.setBounds(400, 20, 100, 25);
		
		pause = new JButton(new ImageIcon("images/pause.jpg"));
		pause.addActionListener(this);
		pause.setBounds(844, 75, 32, 32);

		
		normalSpeed = new JButton(new ImageIcon("images/normalSpeed.jpg"));
		normalSpeed.addActionListener(this);
		normalSpeed.setBounds(876, 75, 32, 32);
		
		doubleSpeed = new JButton(new ImageIcon("images/doubleSpeed.jpg"));
		doubleSpeed.addActionListener(this);
		doubleSpeed.setBounds(908, 75, 32, 32);
		
		quadSpeed = new JButton(new ImageIcon("images/quadSpeed.jpg"));
		quadSpeed.addActionListener(this);
		quadSpeed.setBounds(940, 75, 32, 32);
		
		insaneSpeed = new JButton(new ImageIcon("images/insaneSpeed.jpg"));
		insaneSpeed.addActionListener(this);
		insaneSpeed.setBounds(972, 75, 32, 32);
		
		pause.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		normalSpeed.setBorder(BorderFactory.createLineBorder(Color.BLUE));
		doubleSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		quadSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		insaneSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		shopButton = new JButton(shopImage);
		shopButton.addActionListener(this);
		shopButton.setBounds(0, 470, 50, 270);
		//shopButton.setVisible(false);
		
		statsButton = new JButton(statsImage);
		statsButton.addActionListener(this);
		statsButton.setBounds(0, 0, 49, 198);
		
			
		gamePanel = new GamePanel();
		gamePanel.setLayout(null);
		gamePanel.add(inventoryButton);
		gamePanel.add(statsButton);
		gamePanel.add(priceButton);
		gamePanel.add(shopButton);
		
		gamePanel.add(pause);
		gamePanel.add(normalSpeed);
		gamePanel.add(doubleSpeed);
		gamePanel.add(quadSpeed);
		gamePanel.add(insaneSpeed);
		
		/**
		 * SHOP INIT
		 */
		marketLabel = new JLabel("Market prices");
		marketLabel.setBounds(10, 465, 100, 25);
		marketLabel.setVisible(false);
		invLabel = new JLabel("Your inventory");
		invLabel.setBounds(135, 465, 100, 25);
		invLabel.setVisible(false);
		sellLabel = new JLabel("Sells for: ");
		sellLabel.setBounds(5, 710, 120, 25);
		sellLabel.setVisible(false);
		
		listListener = new ListListener();
		
		marketList = new JList(Globals.drugNameList);
		marketList.setBounds(10, 485, 105, 200);
		marketList.addListSelectionListener(listListener);
		marketList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		marketList.setVisible(false);
		
		invList = new JList();
		invList.setBounds(135, 485, 105, 175);
		invList.addListSelectionListener(listListener);
		invList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		invList.setVisible(false);
		
		buyButton = new JButton("Buy");
		buyButton.addActionListener(this);
		buyButton.setBounds(135, 670, 100, 25);
		buyButton.setVisible(false);
		
		sellButton = new JButton("Sell");
		sellButton.addActionListener(this);
		sellButton.setBounds(135, 705, 100, 25);
		sellButton.setVisible(false);
		
		slider = new JSlider();
		slider.setBounds(300, 680, 200, 75);
		slider.setMinimum(1);
		slider.setMinorTickSpacing(1);
		//slider.setMajorTickSpacing(10);
		slider.setPaintLabels(true);
		slider.setPaintTicks(true);
		slider.setVisible(false);
		
		cashLabel = new JLabel("Cash: ");
		cashLabel.setBounds(10, 685, 100, 25);
		cashLabel.setVisible(false);
		
		quantityLabel = new JLabel("");
		quantityLabel.setForeground(new Color(118, 201, 255));
		quantityLabel.setBounds(350, 650, 175, 25);
		quantityLabel.setVisible(false);
		
		confirmButton = new JButton("Confirm");
		confirmButton.addActionListener(this);
		confirmButton.setBounds(505, 715, 100, 25);
		confirmButton.setVisible(false);
		
		slider.addChangeListener(new ChangeListener(){

			@Override
			public void stateChanged(ChangeEvent e) {
				String action;
				if (buying)
					action = "Buying";
				else
					action = "Selling";
				quantityLabel.setText("");
				String labelText = action + " " + quantityLabel.getText() + " " + slider.getValue() +  ((slider.getValue() > 1) ? " units." : " unit.");
				if (action.equals("Buying"))
				{
					int numBought = slider.getValue();
					double value = player.getCurrentCity().getPriceList().get(marketList.getSelectedIndex()+2);
					double total = numBought*value;
					total = (double)Math.round(total * 10 / 10);
					labelText = labelText.concat(" for " + total + "c");
				}
				
				else
				{
					int numBought = slider.getValue();
					double value =player.getInventory().get(invList.getSelectedIndex()).getValue();
					double total = numBought*value;
					total = (double)Math.round(total * 10 / 10);
					labelText = labelText.concat(" for " + total + "c");
				}
						
				quantityLabel.setText(labelText);			
			}
			
		});
		
		gamePanel.add(marketLabel);
		gamePanel.add(invLabel);
		gamePanel.add(marketList);
		gamePanel.add(invList);
		gamePanel.add(cashLabel);
		gamePanel.add(sellLabel);
		gamePanel.add(buyButton);
		gamePanel.add(sellButton);
		gamePanel.add(slider);
		gamePanel.add(quantityLabel);
		gamePanel.add(confirmButton);
		/**************/
		
		invScreen = new InventoryScreen();
		invScreen.repaint();
		priceScreen = new PricesScreen();
		priceScreen.repaint();
		historyWindow = new HistoryWindow();
		historyWindow.repaint();

		add(gamePanel);
	}
	
	private void loadCityInfo()
	{
		File cityFile = new File("data/citydata.dat");
		if (cityFile.exists())
		{
			Scanner sc = null;
			try {
				sc = new Scanner(cityFile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			while (sc.hasNext())
			{
				String name = sc.nextLine();
				int x = Integer.parseInt(sc.nextLine());
				int y = Integer.parseInt(sc.nextLine());
				
				//TODO: add more drugs
				Map<Integer, Double> prices = new HashMap<Integer, Double>();
				Double weedPrice = Double.parseDouble(sc.nextLine());
				Double mdmaPrice = Double.parseDouble(sc.nextLine());
				Double speedPrice = Double.parseDouble(sc.nextLine());
				Double heroinPrice = Double.parseDouble(sc.nextLine());
				
				prices.put(Globals.WEED, weedPrice);
				prices.put(Globals.MDMA, mdmaPrice);
				prices.put(Globals.SPEED, speedPrice);
				prices.put(Globals.HEROIN, heroinPrice);
				
				City city = new City(name, x, y);
				city.setPrices(prices);
				cities.add(city);
			}
			sc.close();
		}
	}
	
	//Start the game
	private void Start()
	{
		new Thread(){
			public void run()
			{
				final MainMenu mainMenu = new MainMenu();
				while (true)
				{
					if (mainMenu.menuResult() == CLICKED_NEW_GAME)
					{
						mainMenu.dispose();
						createPlayer();
						break;
					}
				}

			}
		}.start();
	}
	
	protected void createPlayer()
	{
		new Thread(){
			public void run()
			{
				final PlayerCreationMenu playerCreationMenu = new PlayerCreationMenu();
				while (true)
				{
					if (playerCreationMenu.finishedCreatingPlayer())
					{
						player = playerCreationMenu.createPlayer();
						player.setPlayerCity(playerCreationMenu.getStartingCity());
						//TODO: test code
						//player = new Player("lunch", 18, 100, new ImageIcon("images/portrait.jpg").getImage());
						
						//TODO: remove this test code
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						//player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "WEED", 33, 999, 96));
						player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "AK-47", 5, 800, 80));
						player.giveItem(new Weapon(Globals.PISTOL, "images/items/pistol.jpg", "pistol" , "GLOCK", 1, 1000, 2, 4));
						playerCreationMenu.dispose();
						new Thread(){
							public void run()
							{
								setVisible(true);
								Thread timerThread = new Thread(timer);
								timerThread.start();
								while(true)
								{		
								gameLoop();
								}
							}
						}.start();
						break;
					}
				}
			}
		}.start();
	}
	
	//permanently iterate through gameloop
	private void gameLoop()
	{
		if (invScreen.isVisible())
			invScreen.repaint();
		if (priceScreen.isVisible())
			priceScreen.repaint();
		
		if (timer.isDueForUpdate())
			updateCityPrices();
		
		gamePanel.repaint();
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void toggleShop()
	{
		if (shopActive)
		{		
			//populate market list
			String[] list = new String[Globals.drugNameList.length];
			for (int i = 0; i < list.length; i++)
			{
				int len = Globals.drugNameList[i].length();
				String item = Globals.drugNameList[i];
				for (int j = 0; j < 15-len; j++)
					item = item.concat(" ");
				item = item.concat(""+player.getCurrentCity().getPriceList().get(Globals.drugList[i]));
				item = item.concat("c");
				item = list[i] = item;
			}
			marketList.setListData(list);
			
			//populate inventory list
			invList.setListData(player.inventoryToArray());
			
			//set cash label
			cashLabel.setText("Cash: " + (double)Math.round(player.getCash() * 100 / 100) + "c");
			
			marketLabel.setVisible(true);
			invLabel.setVisible(true);
			marketList.setVisible(true);
			invList.setVisible(true);
			buyButton.setVisible(true);
			sellButton.setVisible(true);
			cashLabel.setVisible(true);
			buyButton.setEnabled(false);
			sellButton.setEnabled(false);
		}
		else
		{
			marketLabel.setVisible(false);
			invLabel.setVisible(false);
			marketList.setVisible(false);
			invList.setVisible(false);
			buyButton.setVisible(false);
			sellButton.setVisible(false);
			cashLabel.setVisible(false);
			slider.setVisible(false);
			sellLabel.setVisible(false);
			confirmButton.setVisible(false);
			quantityLabel.setVisible(false);
			buyButton.setEnabled(false);
			sellButton.setEnabled(false);
		}
	}
	
	private void updateCityPrices() {
		for (City city: cities)
			market.adjustPrices(city);
		
		priceScreen.updatePrices(priceScreen.cityBox.getSelectedIndex());
		timer.removeUpdateFlag();
	}

	public void showInventory()
	{
		invScreen.populateInventoryScreen();
		
		if (!invScreen.isVisible())
			invScreen.setVisible(true);
		
		invScreen.setState(Frame.NORMAL);
		invScreen.toFront();
		invScreen.requestFocus();
	}

	//opens the prices screen with the specified index as the active one
	public void showPrices(int selectedIndex)
	{
		priceScreen.updatePrices(selectedIndex);
		
		if (!priceScreen.isVisible())
			priceScreen.setVisible(true);
		
		priceScreen.setState(Frame.NORMAL);
		priceScreen.toFront();
		priceScreen.requestFocus();
	}
	
	/**
	 * Get drug name from it's ID
	 * @param ID - The ID of the drug
	 * @return the name of the drug
	 */
	public String getDrugName(int ID)
	{
		switch(ID)
		{
		case Globals.WEED:
			return "Weed";
		case Globals.MDMA:
			return "MDMA";
		case Globals.SPEED:
			return "Speed";
		case Globals.HEROIN:
			return "Heroin";
		}
		
		return "null";
	}
	
	
	public static void main (String[] arg)
	{
		java.awt.EventQueue.invokeLater(new Runnable() {
		    @Override
		    public void run() {
		        new Game();
		    }
		});
	}
	
	public void actionPerformed(ActionEvent e)
	{
		if (timer.isPaused() && e.getSource() != normalSpeed && e.getSource() != doubleSpeed && e.getSource() != quadSpeed && e.getSource() != insaneSpeed)
		{
			return;
		}
		
		if (e.getSource() == inventoryButton)
		{
				showInventory();
		}
		
		if (e.getSource() == priceButton)
		{
			if(player.getCurrentCity() != null)
				showPrices(cities.indexOf(player.getCurrentCity()));
			else
				showPrices(0);
		}
		
		if (e.getSource() == historiesButton)
		{
			if (player.getCurrentCity() != null)
				showTrends(cities.indexOf(player.getCurrentCity()));
			else
				showTrends(0);
		}
		
		if (e.getSource() == statsButton)
		{
			System.out.println("Weed history length: " + player.getCurrentCity().getWeedPriceHistory().size());
			new Thread()
			{
				public void run()
				{
					if (!statsActive)
					{
						statsButton.setIcon(statsAltImage);
					while (statsButton.getX() != 150)
					{
						statsButton.setLocation(statsButton.getX()+1, statsButton.getY());
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						//shop.setVisible(true);
						statsActive = true;
						//toggleShop();
					}
					else
					{
						statsActive = false;
						//toggleShop();
						//shop.setVisible(false);
						statsButton.setIcon(statsImage);
						while (statsButton.getX() != 0)
						{
							statsButton.setLocation(statsButton.getX()-1, statsButton.getY());
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}	
					}
				}
			}.start();
		}
		
		if (e.getSource() == shopButton)
		{
			if (travelling)
				return;
			
			new Thread()
			{
				public void run()
				{
					if (!shopActive)
					{
						shopButton.setIcon(shopAltImage);
					while (shopButton.getX() != 250)
					{
						shopButton.setLocation(shopButton.getX()+1, shopButton.getY());
						try {
							Thread.sleep(1);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
						//shop.setVisible(true);
						shopActive = true;
						toggleShop();
					}
					else
					{
						shopActive = false;
						toggleShop();
						//shop.setVisible(false);
						shopButton.setIcon(shopImage);
						while (shopButton.getX() != 0)
						{
							shopButton.setLocation(shopButton.getX()-1, shopButton.getY());
							try {
								Thread.sleep(1);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}	
					}
				}
			}.start();
		}
		
		if (e.getSource() == buyButton)
		{
			buying = true;
			double cash = player.getCash();
			double value = player.getCurrentCity().getPriceList().get(marketList.getSelectedIndex()+2);
			int max = (int)(cash / value);
			int numBought = Math.min(max, Globals.STACK_MAXIMUM);
			slider.setMaximum(numBought);
			slider.setValue(max);
			slider.setVisible(true);
			confirmButton.setVisible(true);
			quantityLabel.setText("");
			quantityLabel.setText("Buying " + quantityLabel.getText() + " " + slider.getValue() +  (((slider.getValue() > 1) ? " units " : " unit ") + "for " + (numBought*value) + "c"));
			quantityLabel.setVisible(true);
		}
		
		if (e.getSource() == sellButton)
		{
			selling = true;
			int numSold = player.getInventory().get(invList.getSelectedIndex()).getQuantity();
			slider.setMaximum(numSold);
			double value =player.getInventory().get(invList.getSelectedIndex()).getValue();
			slider.setValue(slider.getMaximum());
			slider.setVisible(true);
			confirmButton.setVisible(true);
			quantityLabel.setText("");
			quantityLabel.setText("Selling " + quantityLabel.getText() + " " + slider.getValue() +  (((slider.getValue() > 1) ? " units " : " unit ") + "for " + (numSold*value) + "c"));
			quantityLabel.setVisible(true);
		}
		
		if (e.getSource() == confirmButton)//TODO: find out why prices are bugged!
		{
			if (buying)
			{
				if (!player.hasFullInventory())
				{
				int buyQuantity = slider.getValue();
				double value = player.getCurrentCity().getPriceList().get(marketList.getSelectedIndex()+2);
				double total_cost = buyQuantity*value;
				System.out.println("Total cost: " + total_cost);
				Item boughtItem = Globals.convertIDToItem(marketList.getSelectedIndex()+2);
				player.addCash(total_cost*-1);
				//if (Globals.isDrug(boughtItem.getID()))
					//player.addCash(((Drug) boughtItem).getPurity());
				boughtItem.setQuantity(buyQuantity);
				boughtItem.setValue(value);
				player.giveItem(boughtItem);
				}
				else
					JOptionPane.showMessageDialog(null, "Inventory full!");
			}
			
			else
			{
				int sellQuantity = slider.getValue();
				Item soldItem = player.getInventory().get(invList.getSelectedIndex());
				double value =soldItem.getValue();
				double total_value = sellQuantity*value;
				player.addCash(total_value);
				//if (Globals.isDrug(soldItem.getID()))
					//player.addCash(((Drug) player.getInventory().get(invList.getSelectedIndex())).getPurity());
				player.removeItem(invList.getSelectedIndex());
				
			}
			cashLabel.setText("Cash: " + (double)Math.round(player.getCash() * 100 / 100) + "c");
			sellLabel.setVisible(false);
			slider.setVisible(false);
			quantityLabel.setVisible(false);
			confirmButton.setVisible(false);
			invScreen.populateInventoryScreen();
			invList.setListData(player.inventoryToArray());
		}
		
		if (e.getSource() == travelButton)
		{
			if (!travelling)
			{
				int result = JOptionPane.showConfirmDialog(null, "Travel to " + clickedCity.getName() + "? All flights free in this shitty beta", "Travel", JOptionPane.YES_NO_OPTION);
			
				if (result == JOptionPane.YES_OPTION)
				{
					if (shopActive)
					{
						actionPerformed(new ActionEvent(shopButton, 1, null));
					}
					travel(player.getCurrentCity(), clickedCity);
				}
			}
			
			else
				JOptionPane.showMessageDialog(null, "Wait until you land");
		}
		
		if (e.getSource() == pricesButton)
		{
			showPrices(cities.indexOf(clickedCity));
		}
		
		if (e.getSource() == pause)
		{
			/*
			if (travelling)
			{
				JOptionPane.showMessageDialog(null, "Cannot pause while travelling, sorry :<");
				return;
			}*/
			
			resetBorders();
			pause.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			timer.setPaused(true);
		}
		
		if (e.getSource() == normalSpeed)
		{
			resetBorders();
			normalSpeed.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			timer.setSpeed(GameTimer.NORMAL_SPEED);
		}
		
		if (e.getSource() == doubleSpeed)
		{
			resetBorders();
			doubleSpeed.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			timer.setSpeed(GameTimer.DOUBLE_SPEED);
		}
		
		if (e.getSource() == quadSpeed)
		{
			resetBorders();
			quadSpeed.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			timer.setSpeed(GameTimer.QUAD_SPEED);
		}
		
		if (e.getSource() == insaneSpeed)
		{
			resetBorders();
			insaneSpeed.setBorder(BorderFactory.createLineBorder(Color.BLUE));
			timer.setSpeed(GameTimer.INSANE_SPEED);
		}
	}
	
	private void showTrends(int indexOf) {
		
		
		
	}

	private void resetBorders()
	{
		pause.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		normalSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		doubleSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		quadSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		insaneSpeed.setBorder(BorderFactory.createLineBorder(Color.GRAY));

	}
	
	private void travel(final City sourceCity, final City targetCity) {
		new Thread()
		{

			public void run()
			{
				int targetX = targetCity.getX();
				int targetY = targetCity.getY();
				curX = sourceCity.getX();
				curY = sourceCity.getY();
				player.setPlayerCity(null);
				travelling = true;
				//System.out.println("Travelling from (" + curX +  "," + curY + ") to (" + targetX + "," + targetY + ")");
				
				//determine plane orientation
				
				int xDiff = targetX - curX;
				int yDiff = targetY - curY;
				
				if (xDiff > 0) //target is to the right
				{
					if (Math.abs(xDiff) > Math.abs(yDiff)) //x differential greater than y differential
						planeImg = planeRightImg;
					else
						if (yDiff > 0)
							planeImg = planeDownImg;
						else
							planeImg = planeUpImg;
				}
				
				else
				{
					if (Math.abs(xDiff) > Math.abs(yDiff)) //x differential greater than y differential
						planeImg = planeLeftImg;
					else
						if (yDiff > 0)
							planeImg = planeDownImg;
						else
							planeImg = planeUpImg;
				}
				
				while(curX != targetX || curY != targetY)
				{
					if (!timer.isPaused())
					{
						if (curX < targetX)
							curX+=1;

						else if (curX > targetX)
							curX-=1;

						if (curY < targetY)
							curY+=1;

						else if (curY > targetY)
							curY-=1;

						try {
							Thread.sleep((int)(500*timer.getSpeed()));
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
				}
				
				curX = curY = -1;
				player.setPlayerCity(targetCity);
				travelling = false;
				resetSpeed();
			}

		}.start();
	}
	
	private void resetSpeed()
	{
		actionPerformed(new ActionEvent(normalSpeed, 1, null));
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		
	}


	@Override
	public void mouseMoved(MouseEvent e) {
		//These values are offset to compensate for the left-hand frame border and title bar height
		mouseX = e.getX()-3;
		mouseY = e.getY()-25;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		int clickedX = e.getX()-3;
		int clickedY = e.getY()-25;
		for (City city: cities)
		{
			if (city.getCityRectangle().contains(clickedX, clickedY))
			{
				clickedCity = city;
				
				//player clicked on the city he is in
				if (player.getCurrentCity() == city)
				{
					travelButton.setEnabled(false);
				}
				
				else
					travelButton.setEnabled(true);
				
				if (!timer.isPaused())
					popupMenu.show(e.getComponent(), e.getX(), e.getY());
				break;
			}
			else
			{
				travelButton.setEnabled(true);
				clickedCity = null;
			}
		}
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}


	@Override
	public void mousePressed(MouseEvent e) {
		
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		
	}
	
	private class GamePanel extends JPanel
	{
		
		public GamePanel()
		{
			setLayout(null);
		}
		
		public void paint(Graphics g)
		{
			g.drawImage(mapImage, 0, 0, null);
			g.setColor(Color.WHITE);
			//g.drawString("(" + mouseX + "," + mouseY + ")", 0, 25);
			
			//paint timer
			g.drawString(timer.getDateInfo(), 852, 25);
			g.drawString(timer.getTimeInfo(), 852, 50);
			
			//paint city rectangles
			for (City city: cities)
			{
				g.setColor(Color.WHITE);
				Rectangle cityRect = city.getCityRectangle();
				g.fillRect(cityRect.x, cityRect.y, cityRect.width, cityRect.height);
				g.drawString(city.getName(), city.getX()-20, city.getY());
				
				if (city == player.getCurrentCity())
				{
					g.setColor(Color.GREEN);
					g.fillRect(cityRect.x, cityRect.y, cityRect.width, cityRect.height);
				}
				
				//player is hovering over the city,draw it in red
				if (city.isFocussed(mouseX, mouseY))
				{
					g.setColor(Color.RED);
					{
						g.fillRect(cityRect.x, cityRect.y, cityRect.width, cityRect.height);
					}
				}
				
				//if (!shopActive)
				//	g.drawImage(shopImage, 0, 500, null);
				
			}
			g.setColor(Color.WHITE);
			if (timer.isPaused())
			{
				g.drawImage(pausedImg, 300, 350, null);
			}
			
			g.setColor(Color.BLUE);
			if (curX > 0 && curY > 0)
			{
				//g.fillRect(curX, curY, 10, 10);
				g.drawImage(planeImg,curX-planeImg.getWidth(null)/2, curY-planeImg.getHeight(null)/2, null);
			}
			/*
			g.setColor(new Color(118, 201, 255));
			//calculate and draw notoriety meter
			g.drawString("NOTORIETY", 700, 20);
			
			
			g.drawOval(700, 50, 75, 75);
			g.drawOval(710, 60, 55, 55);
			
			//first arc
			if (player.getNotoriety() >= 0)
			{
				g.fillArc(700, 50, 20, 20, 0, 10);
			}
			*/
			
			
			if (shopActive)
			{
				//setBounds(0, 470, 200, 270);
				//setBorder(BorderFactory.createLineBorder(new Color(118, 201, 255)));
				g.setColor(new Color(118, 201, 255));
				g.fill3DRect(0,  470, 250, 270, true);
				g.setColor(Color.BLACK);
				g.drawLine(125, 470, 125, 740);
				g.setColor(Color.WHITE);
				g.drawRect(0, 470, 250, 269);
			}
			
			if (statsActive)
			{
				g.setColor(new Color(118, 201, 255));
				g.fill3DRect(0,  0, 150, 198, true);
				g.drawImage(portrait, 10, 0, null);
				g.setColor(Color.BLACK);
				g.drawString("H:", 10, 150);
				g.drawString("N:", 10, 170);
				g.drawString("Cash: " + player.getCash() + "c",30, 190);
				Rectangle currentHealthRect;
				
				//Draw maximum health rectangle
				g.drawRect(30, 140, 100, 10);
				//Draw maximum notoriety rectangle
				g.drawRect(30, 160, 100, 10);
				
				//Calculate and draw current health rectangle
				g.setColor(Color.RED);
				g.fill3DRect(30, 140, player.getHealth()/Globals.MAX_HEALTH*100, 10, true);
				
				//Calculate and draw current notoriety rectangle
				double percentage = (double)player.getNotoriety()/(double)Globals.MAX_NOTORIETY;
				g.setColor(Color.BLUE);
				g.fill3DRect(30, 160, (int)(percentage*100), 10, true);
		
				
				g.setColor(Color.BLACK);
				g.drawString(player.getHealth() + "/" + Globals.MAX_HEALTH, 60, 150);
				g.drawString(player.getNotoriety() + "", 75, 170);
				
			}
			super.paintComponents(g);
		}
	}
	
	private class ListListener implements ListSelectionListener
	{
		@Override
		public void valueChanged(ListSelectionEvent e) {
			buying = selling = false;
			slider.setVisible(false);
			quantityLabel.setVisible(false);
			confirmButton.setVisible(false);
			if (e.getSource() == marketList)
			{
				sellLabel.setVisible(false);
				int newIndex = marketList.getSelectedIndex();
				
				if (!invList.isSelectionEmpty())
					invList.clearSelection();
				
				marketList.setSelectedIndex(newIndex);
				
				buyButton.setEnabled(true);
				sellButton.setEnabled(false);
			}
			
			if (e.getSource() == invList)
			{
				int newIndex = invList.getSelectedIndex();
				
				if (newIndex >= 0)
				{
				sellLabel.setText("Orig. value: " + player.getInventory().get(newIndex).getValue() + "c");
				sellLabel.setVisible(true);
				}
				
				if (!marketList.isSelectionEmpty())
					marketList.clearSelection();
				
				invList.setSelectedIndex(newIndex);
				
				sellButton.setEnabled(true);
				buyButton.setEnabled(false);	
			}
			
		}
	}
	
	private class PricesScreen extends JFrame implements ActionListener, WindowListener
	{
		
		//city list for the drop-down box
		private String[] cityNames;
		
		//drop-down box for cities
		private JComboBox cityBox;
		
		//frame panel
		private JPanel panel;
		
		//city names maps drug ID's and prices that are drawn to the frame
		private HashMap<City, Map<Integer, Double>> priceList;
		
		public PricesScreen()
		{
			super("World drug prices");
			
			setSize(400, 600);
			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setLayout(new BorderLayout());
			addWindowListener(this);
			
			panel = new JPanel(new GridLayout(Globals.drugList.length, 2));
			
			cityNames = new String[cities.size()];
			
			for (int i = 0; i < cityNames.length; i++)
		    {
		    	cityNames[i] = cities.get(i).getName();
		    }
			
			cityBox = new JComboBox(cityNames);
			cityBox.addActionListener(this);
			
			priceList = new HashMap<City, Map<Integer, Double>>();
			/*
			priceGrid = new HashMap<String, String>();
			
			for (int i = 0; i < Globals.drugList.length; i++)
			{
				priceGrid.put(new JLabel(getDrugName(Globals.drugList[i])), null);
			}*/
						
			cityNames = new String[cities.size()];
			
			JPanel cityPanel = new JPanel();
			cityPanel.add(new JLabel("Select city: "));
			cityPanel.add(cityBox);
			
			add(cityPanel, BorderLayout.NORTH);
			add(panel, BorderLayout.CENTER);
		}
		
		public void updatePrices(int selectedIndex)
		{
			priceList.clear();
			//build pricelist
			for (City city: cities)
			{
				priceList.put(city, city.getPriceList());
			}
			cityBox.setSelectedIndex(selectedIndex);
			//build JLabels
			rebuildLabels(selectedIndex);
		}
		
		public void rebuildLabels(int cityIndex)
		{
			remove(panel);
			panel = new JPanel(new GridLayout(Globals.drugList.length, 2));

			for (int i = 0; i < Globals.drugList.length; i++)
			{
				JLabel nameLabel = new JLabel(getDrugName(Globals.drugList[i]));
				nameLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				JLabel priceLabel = new JLabel(cities.get(cityIndex).getPriceList().get(Globals.drugList[i]).toString());
				priceLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				panel.add(nameLabel);
				panel.add(priceLabel);
			}
			add(panel, BorderLayout.CENTER);
			validate();
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == cityBox)
			{
				rebuildLabels(cityBox.getSelectedIndex());
			}
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			setVisible(false);
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			setVisible(false);
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// 
			
		}
	}
	
	private class InventoryScreen extends JFrame implements ActionListener, WindowListener
	{
		ArrayList<ItemSlot> itemSlots;
		
		private JPanel invPanel;
		
		//selected slot, hovered-over slot
		private ItemSlot selectedSlot, hoverSlot;
		
		//Layout of the inventory panel
		private GridLayout layout;
		
		//inventory buttons
		private JButton equipButton, dropButton;
		
		//Player cash label
		private JLabel cashLabel, cash;
		
		public InventoryScreen()
		{
			super("Inventory");
			
			cashLabel = new JLabel("Cash: ");
			cash = new JLabel();
			
			equipButton = new JButton("Equip");
			equipButton.addActionListener(this);
			dropButton = new JButton("Drop");
			dropButton.addActionListener(this);
			
			JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
			buttonPanel.setPreferredSize(new Dimension(80, 400));
			buttonPanel.add(equipButton);
			buttonPanel.add(dropButton);
			buttonPanel.add(new JPanel());
			JPanel cashPanel = new JPanel(new BorderLayout());
			cashPanel.add(cashLabel, BorderLayout.NORTH);
			cashPanel.add(cash, BorderLayout.CENTER);
			buttonPanel.add(cashPanel);
			
			selectedSlot = null;
			hoverSlot = null;
			//invPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

			setSize(500, 400);
			
			setResizable(false);
			layout = new GridLayout(2, 4, 1, 1);
			
			invPanel = new InventoryPanel();
			invPanel.setSize(getSize());
			
			invPanel.setLayout(layout);
			setLocationRelativeTo(null);
			setLayout(new BorderLayout());
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setVisible(false);
			addWindowListener(this);
			itemSlots = new ArrayList<ItemSlot>(8); //maximum of 8 inventory slots
			for (int i = 0; i < 8; i++)
			{
				ItemSlot is = new ItemSlot(null);
				is.setPreferredSize(new Dimension(100, 200));
				itemSlots.add(is);
				invPanel.add(itemSlots.get(i));
			}
			add(invPanel, BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.EAST);
			pack();
		}
		
		public void populateInventoryScreen()
		{
			equipButton.setEnabled(false);
			dropButton.setEnabled(false);
					
			for (int i = 0; i < player.getInventory().size(); i++)
			{
				itemSlots.get(i).setItem(null); //reset item slot
				itemSlots.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK));

				if (player.getInventory().get(i) != null)
				{
					itemSlots.get(i).setItem(player.getInventory().get(i));
				}
					
			}
			cash.setText("     " + (double)Math.round(player.getCash() * 100 / 100) + "c");
			invPanel.repaint();
		}
		
		public void actionPerformed(ActionEvent e)
		{
			if (e.getSource() == equipButton)
			{
				if (equipButton.getText().equals("Equip"))
				{
				Weapon clickedWeapon = (Weapon)selectedSlot.getItem();
				equipButton.setText("Unequip");
				player.equipWeapon(clickedWeapon);
				selectedSlot.setEquipped(true);
				selectedSlot.setBorder(BorderFactory.createLineBorder(Color.BLUE));
				}
				
				else
				{
					equipButton.setText("Equip");
					player.equipWeapon(null);
					selectedSlot.setEquipped(false);
					selectedSlot.setBorder(BorderFactory.createLineBorder(Color.RED));
				}
			}
			
			if (e.getSource() == dropButton)
			{
				Item clickedItem = selectedSlot.getItem();
				if (clickedItem == player.getEquippedWeapon())
				{
					JOptionPane.showMessageDialog(null, "Uneqip item first");
					return;
				}
				int result = JOptionPane.showConfirmDialog(null, "Drop " + clickedItem.getQuantity() + ((clickedItem.getQuantity() > 1)? " units ": " unit ") + " of " + clickedItem.getName() + "?", "Confirm", JOptionPane.YES_NO_OPTION);
				
				if (result == JOptionPane.YES_OPTION)
				{
					player.getInventory().remove(clickedItem);
					player.getInventory().add(null);
					selectedSlot = null;
					hoverSlot = null;
					populateInventoryScreen();
				}
			}
			invPanel.repaint();
		}
		
		private class InventoryPanel extends JPanel
		{
			/*
			public void paint(Graphics g)
			{
				super.paintComponents(g);
				
				for (ItemSlot itemSlot: itemSlots)
				{
					
					if (itemSlot == hoverSlot)
					{
						itemSlot.setBorder(BorderFactory.createLineBorder(Color.GREEN));
					}
					
					if (itemSlot == selectedSlot)
					{
						itemSlot.setBorder(BorderFactory.createLineBorder(Color.BLUE));
					}
					
					if (player.getEquippedWeapon() != null && itemSlot.getItem() == player.getEquippedWeapon())
					{
						itemSlot.setBorder(BorderFactory.createLineBorder(Color.RED));
					}
					
					//Draw image icons and text for occupied slots
					if (itemSlot.getItem() != null)
					{
						Point center = new Point(itemSlot.getX() +  itemSlot.getWidth()/2-itemSlot.getImage().getWidth(null)/2, itemSlot.getY() + itemSlot.getHeight()/2 - itemSlot.getImage().getHeight(null)/2);
						g.drawImage(itemSlot.getImage(), center.x, center.y, null);
						g.drawString(itemSlot.getItem().getDescription(), center.x-20 , center.y-30);
						g.drawString("Quantity : " + itemSlot.getItem().getQuantity(), center.x-20, center.y+60);
						g.drawString("Value : " + itemSlot.getItem().getValue() + "c", center.x-20, center.y+80);
						
						switch(itemSlot.getItem().getID())
						{
						case Globals.DRUG:
						case Globals.WEED:
							g.drawString("Purity: " + ((Drug) itemSlot.getItem()).getPurity() + "%", center.x-20, center.y-15);
							break;
							
						case Globals.WEAPON:
						case Globals.PISTOL:
							g.drawString("Ammo: " + ((Weapon) itemSlot.getItem()).getAmmo(), center.x-20, center.y);
							g.drawString("Damage: " + ((Weapon) itemSlot.getItem()).getMinDamage() + "-" + ((Weapon) itemSlot.getItem()).getMaxDamage(), center.x-20, center.y-15);
							break;
							
						default:
							break;
						}
					}
				}
			}*/
		}
		
		private class ItemSlot extends JPanel implements MouseListener
		{
			private Item item;
			private Image image;
			private boolean equipped;
			
			public ItemSlot(Item item)
			{
				this.item = item;
				if (item != null)
				{
					image = item.getImage();
					setToolTipText(item.getName());
				}
				else
					setToolTipText("Empty");
				
				equipped = false;
				addMouseListener(this);
				setBackground(Color.DARK_GRAY);
				//setOpaque(true);
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}
			
			public void setEquipped(boolean b)
			{
				equipped = b;
			}
			
			public void paintComponent(Graphics g)
			{
				if (item != null)
				{
				g.drawString(item.getDescription(), 15, 20);
				g.drawString("Quantity: " + item.getQuantity(), 15, 40);
				
				g.drawImage(image, 30, 80, null);
				
					switch(item.getID())
					{
					case Globals.DRUG:
					case Globals.WEED:
					case Globals.MDMA:
					case Globals.SPEED:
					case Globals.HEROIN:
						g.drawString("Purity: " + ((Drug) item).getPurity() + "%", 15, 160);
						if(!travelling)
							g.drawString("Value: " + player.getCurrentCity().getPriceList().get(item.getID()) + "c", 15, 185);
						break;
						
					case Globals.WEAPON:
					case Globals.PISTOL:
						g.drawString("Ammo: " + ((Weapon) item).getAmmo(), 15, 60);
						g.drawString("Damage: " + ((Weapon) item).getMinDamage() + "-" + ((Weapon) item).getMaxDamage(), 15, 160);
						g.drawString("Value: " + item.getValue() + "c", 15, 185);
						break;
					}
				}
				else
					g.drawString("Empty", 30, 100);
			}
			
			public Item getItem()
			{
				return item;
			}
			
			public void setItem(Item item)
			{
				this.item = item;
				if (item != null)
				{
					image = item.getImage();
					setToolTipText(item.getDescription());
				}
				else
					setToolTipText("Empty");
			}
			
			public Image getImage()
			{
				return image;
			}
			
			public void mouseClicked(MouseEvent arg0) {
				if (selectedSlot != null)
					selectedSlot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				setBorder(BorderFactory.createLineBorder(Color.RED));
				if (item != null)
				{
					selectedSlot = this;
					if (item instanceof Equipabble)
					{
						if (item == player.getEquippedWeapon())
							equipButton.setText("Unequip");
						else
							equipButton.setText("Equip");
						equipButton.setEnabled(true);
					}
					else
					{
						equipButton.setEnabled(false);
						equipButton.setText("Equip");
					}
					dropButton.setEnabled(true);
				}
				else
				{
					selectedSlot = null;
					equipButton.setEnabled(false);
					dropButton.setEnabled(false);
				}
				
				//if (equipped)
				//{
				//	setBorder(BorderFactory.createLineBorder(Color.BLUE));	
				//}
				
				for (ItemSlot i: itemSlots)
				{
					if (i.isEquipped())
						i.setBorder(BorderFactory.createLineBorder(Color.BLUE));	
				}
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				if (hoverSlot != null)
				{
					hoverSlot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				}
				hoverSlot = this;
				if (hoverSlot != selectedSlot)
					setBorder(BorderFactory.createLineBorder(Color.GREEN));
				
				if (equipped)
				{
					setBorder(BorderFactory.createLineBorder(Color.BLUE));
				}
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				hoverSlot = null;
				if (selectedSlot != this)
					setBorder(BorderFactory.createLineBorder(Color.BLACK));
				if (equipped)
				{
					setBorder(BorderFactory.createLineBorder(Color.BLUE));
				}
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (selectedSlot != null)
					selectedSlot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				setBorder(BorderFactory.createLineBorder(Color.RED));
				if (item != null)
				{
					selectedSlot = this;
					if (item instanceof Equipabble)
					{
						if (item == player.getEquippedWeapon())
							equipButton.setText("Unequip");
						else
							equipButton.setText("Equip");
						equipButton.setEnabled(true);
					}
					else
					{
						equipButton.setEnabled(false);
						equipButton.setText("Equip");
					}
					dropButton.setEnabled(true);
				}
				else
				{
					selectedSlot = null;
					equipButton.setEnabled(false);
					dropButton.setEnabled(false);
				}
				
				//if (equipped)
				//{
				//	setBorder(BorderFactory.createLineBorder(Color.BLUE));	
				//}
				
				for (ItemSlot i: itemSlots)
				{
					if (i.isEquipped())
						i.setBorder(BorderFactory.createLineBorder(Color.BLUE));	
				}
			}

			private boolean isEquipped() {
				return equipped;
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {				
			}
		}

		@Override
		public void windowActivated(WindowEvent arg0) {			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			setVisible(false);	
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			setVisible(false);
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
		
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			
		}	
	}
	
	private static class HistoryWindow extends JFrame implements ActionListener, WindowListener
	{
		private JCheckBox weedBox, mdmaBox, speedBox, heroinBox;
		
		private JPanel choicePanel;
		
		private GraphPanel graphPanel;
		
		private static int width;

		private static int height;
		
		
		public HistoryWindow()
		{
			super("Price history");
			
			choicePanel = new JPanel();
			choicePanel.setAlignmentY(JPanel.BOTTOM_ALIGNMENT);
			
			weedBox = new JCheckBox("Weed");
			mdmaBox = new JCheckBox("MDMA");
			speedBox = new JCheckBox("Speed");
			heroinBox = new JCheckBox("Heroin");
			
			choicePanel.add(weedBox);
			choicePanel.add(mdmaBox);
			choicePanel.add(speedBox);
			choicePanel.add(heroinBox);
			
			graphPanel = new GraphPanel();
			
			setLayout(new BorderLayout());
			
			add(choicePanel, BorderLayout.NORTH);
			add(graphPanel, BorderLayout.CENTER);
			
			width = height = 500;
			setSize(width, height);
			setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			setVisible(true);
		}

		@Override
		public void windowActivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosed(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowClosing(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeactivated(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowDeiconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowIconified(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void windowOpened(WindowEvent arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void actionPerformed(ActionEvent arg0) {
			// TODO Auto-generated method stub
			
		}
		
		private static class GraphPanel extends JPanel
		{
			public void paintComponent(Graphics g)
			{
				g.setColor(Color.BLACK);
				g.fillRect(50, 0, width-100, height-100);
				
				g.setColor(Color.GREEN);
				
				super.paintComponents(g);
			}
		}
	}
	
	
	@SuppressWarnings("unused")
	private static class PlayerCreationMenu extends JFrame
	{
		//Points left for distribution
		private int remainingPoints;
		
		//List of possible player backgrounds
		private String[] backgrounds = {"None", "Thief", "Thug", "Confidence man"};
		
		//Descriptions of the above backgrounds
		private String[] bgDescripts = {"Your character has no special background", "Your character is a thief", "Your character is a thug", "Your character is a confidence trickster"};
		
		//Button that changes player portrait
		private JButton changeAvatarButton;
		
		//Text fields for name and age
		private JTextField nameField, ageField;
		
		//Player creation complete
		private boolean finished;
		
		private JPanel mainPanel;
		
		private JComboBox cityBox;
		
		public PlayerCreationMenu()
		{
			super("Create player");
			remainingPoints = 5;
			setSize(frameWidth, frameHeight);
			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			setLayout(null);
			
			changeAvatarButton = new JButton("Change");
			nameField = new JTextField("Steve");
			nameField.setColumns(30);
			ageField = new JTextField("18");
			ageField.setColumns(2);
			
			mainPanel = new PlayerCreationPanel();
			
			//center of the frame
			Point center = new Point(getWidth()/2, getHeight()/2);
			mainPanel.setLocation(center.x-mainPanel.getWidth()/2, center.y-mainPanel.getHeight()/2);
			
			add(mainPanel);
			
			//finished = true; //TODO: Change this back! Skips player creation
			
			remainingPoints = 5;
			
			setVisible(true);
		}
		
		public Player createPlayer() {
			Player player = new Player(nameField.getText(), Integer.parseInt(ageField.getText()), 100, ((PlayerCreationPanel) mainPanel).getImage());
			
			return player;
		}

		public boolean finishedCreatingPlayer()
		{
			return finished;
		}
		
		//Retrieves the selected starting city from the combo box
		public City getStartingCity()
		{
			return cities.get(cityBox.getSelectedIndex());
		}
		
		private class PlayerCreationPanel extends JPanel implements ActionListener
		{
			private Image image;
			private JLabel avatarLabel, strLabel, dexLabel, intLabel, chaLabel;
			private JLabel nameLabel, ageLabel;
			private JLabel pointsRemainingLabel;
			private JLabel backgroundLabel;
			private JTextField strField, dexField, intField, chaField;
			private JTextField nameField, ageField;
			private JButton strPlus, strMinus, dexPlus, dexMinus, intPlus, intMinus, chaPlus, chaMinus;
			private JComboBox bgChooser;
			private JTextArea bgArea;
			private JLabel cityLabel;
			private String[] cityNames;
			private JButton startButton;
			
			
			public PlayerCreationPanel() {
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
				setLayout(null);
				
				image = portrait;
				
				avatarLabel = new JLabel("Your avatar");
				strLabel = new JLabel("Strength:");
				strLabel.setToolTipText("Strength determines your carrying capacity and melee damage.");
				strField = new JTextField("10");
				strPlus = new JButton("+");
				strPlus.addActionListener(this);
				strMinus = new JButton("-");
				strMinus.addActionListener(this);
				strField.setEditable(false);
				
				dexLabel = new JLabel("Dexterity:");
				dexLabel.setToolTipText("Dexterity affects your running speed and evasion.");
				dexField = new JTextField("10");
				dexPlus = new JButton("+");
				dexPlus.addActionListener(this);
				dexMinus = new JButton("-");
				dexMinus.addActionListener(this);
				dexField.setEditable(false);
				
				intLabel = new JLabel("Intelligence:");
				intLabel.setToolTipText("Intelligence does nothing atm :/");
				intField = new JTextField("10");
				intPlus = new JButton("+");
				intPlus.addActionListener(this);
				intMinus = new JButton("-");
				intMinus.addActionListener(this);
				intField.setEditable(false);
				
				chaLabel = new JLabel("Charisma:");
				chaLabel.setToolTipText("High charisma allows you to talk yourself out of difficult situations");
				chaField = new JTextField("10");
				chaPlus = new JButton("+");
				chaPlus.addActionListener(this);
				chaMinus = new JButton("-");
				chaMinus.addActionListener(this);
				chaField.setEditable(false);
				pointsRemainingLabel = new JLabel("Points remaining: " + remainingPoints);
				
				nameLabel = new JLabel("Name: ");
				nameField = new JTextField();
				
				ageLabel = new JLabel("Age: ");
				ageLabel.setToolTipText("Maximum age of 99");
			    ageField = new NumericTextField(2, null);
			    
			    backgroundLabel = new JLabel("Select your background");
			    bgChooser = new JComboBox(backgrounds);
			    bgChooser.addActionListener(this);
			    bgArea = new JTextArea("Your character has no special background");
			    
			    cityLabel = new JLabel("Select starting city");
			    
			    cityNames = new String[cities.size()];
			    
			    for (int i = 0; i < cityNames.length; i++)
			    {
			    	cityNames[i] = cities.get(i).getName();
			    }
			    
			    cityBox = new JComboBox(cityNames);
				
				startButton = new JButton("Start!");
				startButton.addActionListener(this);
				
				avatarLabel.setBounds(75, 0, 100, 50);
				changeAvatarButton.setBounds(75, 200, 80, 30);
				
				strLabel.setBounds(5, 275, 100, 25);
				strField.setBounds(80, 275, 20, 25);
				strPlus.setBounds(105, 275, 50, 25);
				strMinus.setBounds(155, 275, 50, 25);
				
				dexLabel.setBounds(5, 325, 100, 25);
				dexField.setBounds(80, 325, 20, 25);
				dexPlus.setBounds(105, 325, 50, 25);
				dexMinus.setBounds(155, 325, 50, 25);
				
				intLabel.setBounds(5, 375, 100, 25);
				intField.setBounds(80, 375, 20, 25);
				intPlus.setBounds(105, 375, 50, 25);
				intMinus.setBounds(155, 375, 50, 25);
				
				chaLabel.setBounds(5, 425, 100, 25);
				chaField.setBounds(80, 425, 20, 25);
				chaPlus.setBounds(105, 425, 50, 25);
				chaMinus.setBounds(155, 425, 50, 25);
				
				pointsRemainingLabel.setBounds(5, 475, 150, 25);
				
				nameLabel.setBounds(275, 50, 50, 25);
				nameField.setBounds(325, 50, 150, 25);
				
				ageLabel.setBounds(275, 100, 50, 25);
				ageField.setBounds(325, 100, 20, 25);
				
				backgroundLabel.setBounds(350, 150, 150, 20);
				bgChooser.setBounds(325, 175, 200, 20);
				bgArea.setBounds(325, 200, 200, 100);
				bgArea.setEditable(false);
				bgArea.setLineWrap(true);
				bgArea.setBackground(getBackground()); //default grey of the frame
				bgArea.setBorder(BorderFactory.createLineBorder(Color.BLACK));
				
				cityLabel.setBounds(350, 325, 150, 20);
				cityBox.setBounds(325, 350, 200, 20);
				
				startButton.setBounds(475, 550, 100, 25);
				
				add(avatarLabel);
				add(changeAvatarButton);
				
				add(strLabel);
				add(strField);
				add(strPlus);
				add(strMinus);
				add(dexLabel);
				add(dexField);
				add(dexPlus);
				add(dexMinus);
				add(intLabel);
				add(intField);
				add(intPlus);
				add(intMinus);
				add(chaLabel);
				add(chaField);
				add(chaPlus);
				add(chaMinus);
				
				add(pointsRemainingLabel);
				
				add(nameLabel);
				add(nameField);
				
				add(ageLabel);
				add(ageField);
				
				add(backgroundLabel);
				add(bgChooser);
				add(bgArea);
				
				add(cityLabel);
				add(cityBox);
				
				add(startButton);
				
				setSize(600, 600);
			}
			
			

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(image, 50, 55, this);
				g.drawRect(0, 0, changeAvatarButton.getX()+150, changeAvatarButton.getY()+50); //group portrait area
				g.drawRect(0, 0, changeAvatarButton.getX()+150, getHeight()); //group stat area
				g.drawRect(changeAvatarButton.getX()+150, 0, getWidth(), ageField.getY()+50); //group details area
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				
				if (e.getSource() == bgChooser)
				{
					bgArea.setText(bgDescripts[bgChooser.getSelectedIndex()]);
				}
				
				int str = Integer.parseInt(strField.getText());
				int dex = Integer.parseInt(dexField.getText());
				int int_ = Integer.parseInt(intField.getText());
				int cha = Integer.parseInt(chaField.getText());
				
				if (e.getSource() == strPlus)
				{
					if (remainingPoints == 0)
						return;
					
					if (str < Globals.MAX_STAT_VALUE)
					{
						str++;
						remainingPoints--;
						strMinus.setEnabled(true);
					}
				}
				
				if (e.getSource() == strMinus)
				{
					if (str > Globals.MIN_STAT_VALUE)
					{
						str--;
						remainingPoints++;
						
						if (str == Globals.MIN_STAT_VALUE)
							strMinus.setEnabled(false);
					}
				}
				
				if (e.getSource() == dexPlus)
				{
					if (remainingPoints == 0)
						return;
					
					if (dex < Globals.MAX_STAT_VALUE)
					{
						dex++;
						remainingPoints--;
						dexMinus.setEnabled(true);
					}
				}
				
				if (e.getSource() == dexMinus)
				{
					if (dex > Globals.MIN_STAT_VALUE)
					{
						dex--;
						remainingPoints++;
						
						if (dex == Globals.MIN_STAT_VALUE)
							dexMinus.setEnabled(false);
					}
				}
				
				if (e.getSource() == intPlus)
				{
					if (remainingPoints == 0)
						return;
					
					if (int_ < Globals.MAX_STAT_VALUE)
					{
						int_++;
						remainingPoints--;
						intMinus.setEnabled(true);
					}
				}
				
				if (e.getSource() == intMinus)
				{
					if (int_ > Globals.MIN_STAT_VALUE)
					{
						int_--;
						remainingPoints++;
						
						if (int_ == Globals.MIN_STAT_VALUE)
							intMinus.setEnabled(false);
					}
				}
				
				if (e.getSource() == chaPlus)
				{
					if (remainingPoints == 0)
						return;
					
					if (cha < Globals.MAX_STAT_VALUE)
					{
						cha++;
						remainingPoints--;
						chaMinus.setEnabled(true);
					}
				}
				
				if (e.getSource() == chaMinus)
				{
					if (cha > Globals.MIN_STAT_VALUE)
					{
						cha--;
						remainingPoints++;
						
						if (cha == Globals.MIN_STAT_VALUE)
							chaMinus.setEnabled(false);
					}
				}
				
				//disable '+' keys when no points are left
				if (remainingPoints == 0)
				{
					strPlus.setEnabled(false);
					dexPlus.setEnabled(false);
					intPlus.setEnabled(false);
					chaPlus.setEnabled(false);
				}
				
				//re-enable + keys
				if ((e.getSource() == strMinus || e.getSource() == dexMinus || e.getSource() == intMinus || e.getSource() == chaMinus) && remainingPoints == 1)
				{
					strPlus.setEnabled(true);
					dexPlus.setEnabled(true);
					intPlus.setEnabled(true);
					chaPlus.setEnabled(true);
				}
								
				strField.setText(str+"");
				dexField.setText(dex+"");
				intField.setText(int_+"");
				chaField.setText(cha+"");
				
				pointsRemainingLabel.setText("Points remaining: " + remainingPoints);
				
				if (e.getSource() == startButton)
				{
					if (nameField.getText().length() == 0 || ageField.getText().length() == 0)
					{
						JOptionPane.showMessageDialog(null, "Please fill in all fields.");
					}
					
					else if (remainingPoints != 0)
					{
						JOptionPane.showMessageDialog(null, "You still have points remaining.");
					}
					else
						finished = true;
				}
			}

			public Image getImage() {
				return image;
			}
		}
	}
	
	private static class MainMenu extends JFrame implements ActionListener
	{
		private JButton newGameButton, continueButton, optionsButton, quitButton;
		
		//The result of the main menu
		int result = 0;
		
		public MainMenu()
		{
			super("Druglord " + Globals.VERSION);
			setSize(frameWidth, frameHeight);
			setResizable(false);
			setLocationRelativeTo(null);
			setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLayout(new BorderLayout());
			
			newGameButton = new JButton("New Game");
			newGameButton.addActionListener(this);
			continueButton = new JButton("Continue");
			continueButton.addActionListener(this);
			optionsButton = new JButton("Options");
			optionsButton.addActionListener(this);
			quitButton = new JButton("Quit");
			quitButton.addActionListener(this);
			
			JPanel menuPanel = new JPanel(new GridLayout(4, 1));
			menuPanel.add(newGameButton);
			menuPanel.add(continueButton);
			menuPanel.add(optionsButton);
			menuPanel.add(quitButton);
			
			JPanel buttonPanel = new JPanel(new GridLayout(1, 3));
			buttonPanel.add(new JPanel());
			buttonPanel.add(menuPanel);
			buttonPanel.add(new JPanel());
			
			add(new MenuPanel(), BorderLayout.CENTER);
			add(buttonPanel, BorderLayout.SOUTH);
			setVisible(true);
		}
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			if (e.getSource() == newGameButton)
			{
				result = CLICKED_NEW_GAME;
				return;
			}
			
			if (e.getSource() == continueButton)
			{
				if (!saveFile.exists())
				{
					JOptionPane.showMessageDialog(null, "Save not found!");
					return;
				}
				
				//read savefile
				Scanner saveReader = null;
				
				try {
					saveReader= new Scanner(saveFile);
					
					String playerData = saveReader.nextLine();
					
					/*
					 * TODO: convert playerData string to actualdata
					 * 
					 * 
					 */
					
					//read integrity hash from savefile
					String integrityHash = saveReader.nextLine();
					
				} catch (FileNotFoundException e1) {
					JOptionPane.showMessageDialog(null, "Error reading save file!");
				} catch (NoSuchElementException e3) {
				JOptionPane.showMessageDialog(null, "Save file has been tampered with!");
			    } catch (NullPointerException e2) {
					JOptionPane.showMessageDialog(null, "Save file has been tampered with!");
				}
				finally
				{
					saveReader.close();
				}
			}
			
			if (e.getSource() == optionsButton)
			{
				
			}
			
			if (e.getSource() == quitButton)
			{
				System.exit(0);
			}
			
		}
		
		public int menuResult()
		{
			return result;
		}
		
		static class MenuPanel extends JPanel
		{
			private Image image;
			
			public MenuPanel() {
				image = new ImageIcon("images/Main Menu.jpg").getImage();
			}

			public void paintComponent(Graphics g)
			{
				super.paintComponent(g);
				g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
			}
			
		}
	}

	/**
	* This method makes the color in image transparent.
	*
	* @param im The image who's color needs to be made transparent.
	* @param color The color which needs to be filtered out of the image.
	* @return  Image from which the color has been removed.
	* 
	public static Image makeColorTransparent (Image im, final Color color) {
	 
	ImageFilter filter = new RGBImageFilter() {
	 
	// the color we are looking for... Alpha bits are set to opaque
	public int markerRGB = color.getRGB() | 0xFF000000;
	 
	public final int filterRGB(int x, int y, int rgb) {
	if ( ( rgb | 0xFF000000 ) == markerRGB ) {
	// Mark the alpha bits as zero - transparent
	return 0x00FFFFFF & rgb;
	}
	else {
	// nothing to do
	return rgb;
	}
	}
	};
	 
	ImageProducer ip = new FilteredImageSource(im.getSource(), filter);
	return Toolkit.getDefaultToolkit().createImage(ip);
	}*/
}
