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
	
	//World map image
	private Image mapImage;
	
	//list of cities
	private static ArrayList<City> cities;
	
	//Game panel
	private JPanel gamePanel;
	
	//Popup menu
	private JPopupMenu popupMenu;
	
	//menu items for the popup menu
	private JMenuItem travelButton, pricesButton;
	
	//Determines which city was clicked
	private City clickedCity;
	
	//Components on the button panel
	private JButton inventoryButton, statsButton, priceButton;
		
	//The screen that shows the player's inventory
	private InventoryScreen invScreen;
	
	//The screen that shows city prices
	private PricesScreen priceScreen;
	
	
	public Game()
	{
		super ("Druglord " + Globals.VERSION);
			
		frameWidth = 1280;
		frameHeight =960;
		
		setSize(frameWidth, frameHeight);
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		addMouseListener(this);
		addMouseMotionListener(this);
		
		mouseX = mouseY = 0;
		
		load();
		
		Start();
	}
	
	private void load()
	{
		cities = new ArrayList<City>();
		mapImage = new ImageIcon("images/worldmap.jpg").getImage();
		saveFile = new File("saves/save.bin");
		
		loadCityInfo();

		
		travelButton = new JMenuItem("Travel");
		travelButton.addActionListener(this);
		pricesButton = new JMenuItem("Prices");
		pricesButton.addActionListener(this);
		
		popupMenu = new JPopupMenu();
		popupMenu.add(travelButton);
		popupMenu.add(pricesButton);
		
		inventoryButton = new JButton("Inventory");
		inventoryButton.addActionListener(this);
		
		inventoryButton.setBounds(200, 20, 100, 25);
		
		statsButton = new JButton("Stats");
		statsButton.addActionListener(this);
		
		statsButton.setBounds(300, 20, 100, 25);
		
		priceButton = new JButton("Prices");
		priceButton.addActionListener(this);
		
		priceButton.setBounds(400, 20, 100, 25);
			
		gamePanel = new GamePanel();
		gamePanel.add(inventoryButton);
		gamePanel.add(statsButton);
		gamePanel.add(priceButton);
		
		invScreen = new InventoryScreen();
		invScreen.repaint();
		priceScreen = new PricesScreen();
		priceScreen.repaint();

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
						player.giveItem(new Drug(Globals.WEED, "images/items/weed.jpg", "weed", "OG Kush", 33, 999, 96));
						player.giveItem(new Drug(Globals.WEED, "images/items/weed2.jpg", "weed", "AK-47", 5, 800, 80));
						player.giveItem(new Weapon(Globals.PISTOL, "images/items/pistol.jpg", "pistol" , "Glock", 1, 1000, 2, 4));
						for (int i = 0; i < 5; i++)
							player.giveItem(null);
						playerCreationMenu.dispose();
						new Thread(){
							public void run()
							{
								setVisible(true);
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
		
		gamePanel.repaint();
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
		if (e.getSource() == inventoryButton)
		{
				showInventory();
		}
		
		if (e.getSource() == statsButton)
		{
			
		}
		
		if (e.getSource() == priceButton)
		{
			showPrices(cities.indexOf(player.getCurrentCity()));
		}
		
		if (e.getSource() == travelButton)
		{
			int result = JOptionPane.showConfirmDialog(null, "Travel to " + clickedCity.getName() + "? All flights free in this shitty beta", "Travel", JOptionPane.YES_NO_OPTION);
			
			if (result == JOptionPane.YES_OPTION)
			{
				player.setPlayerCity(clickedCity);
			}
		}
		
		if (e.getSource() == pricesButton)
		{
			showPrices(cities.indexOf(clickedCity));
		}
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
			g.drawString("(" + mouseX + "," + mouseY + ")", 0, 25);
			
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
			}
			super.paintComponents(g);
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
			super("World prices");
			
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
		
		public InventoryScreen()
		{
			super("Inventory");
			
			
			equipButton = new JButton("Equip");
			equipButton.addActionListener(this);
			dropButton = new JButton("Drop");
			dropButton.addActionListener(this);
			
			JPanel buttonPanel = new JPanel(new GridLayout(4, 1));
			buttonPanel.setPreferredSize(new Dimension(80, 400));
			buttonPanel.add(equipButton);
			buttonPanel.add(dropButton);
			
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
					
			for (int i = 0; i < 8; i++)
			{
				itemSlots.get(i).setItem(null); //reset item slot
				itemSlots.get(i).setBorder(BorderFactory.createLineBorder(Color.BLACK));

				if (player.getInventory().get(i) != null)
				{
					itemSlots.get(i).setItem(player.getInventory().get(i));
				}

			}
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
				}
				
				else
				{
					equipButton.setText("Equip");
					player.equipWeapon(null);
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
					if (itemSlot.getItem() != null )
					{
						Point center = new Point(itemSlot.getX() +  itemSlot.getWidth()/2-itemSlot.getImage().getWidth(null)/2, itemSlot.getY() + itemSlot.getHeight()/2 - itemSlot.getImage().getHeight(null)/2);
						g.drawImage(itemSlot.getImage(), center.x, center.y, null);
						g.drawString(itemSlot.getItem().getDescription(), center.x-20 , center.y-30);
						g.drawString("Quantity : " + itemSlot.getItem().getQuantity(), center.x-20, center.y+60);
						g.drawString("Value : " + itemSlot.getItem().getValue() + "c", center.x-20, center.y+80);
						
						switch(itemSlot.getItem().getID())
						{
						case Globals.WEED:
							g.drawString("Purity: " + ((Drug) itemSlot.getItem()).getPurity() + "%", center.x-20, center.y-15);
							break;
							
						case Globals.WEAPON:
							g.drawString("Ammo: " + ((Weapon) itemSlot.getItem()).getAmmo(), center.x-20, center.y);
							g.drawString("Damage: " + ((Weapon) itemSlot.getItem()).getMinDamage() + "-" + ((Weapon) itemSlot.getItem()).getMaxDamage(), center.x-20, center.y-15);
							break;
							
						default:
							break;
						}
					}
				}
			}
		}
		
		private class ItemSlot extends JPanel implements MouseListener
		{
			private Item item;
			private Image image;
			
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
				
				addMouseListener(this);
				setBackground(Color.LIGHT_GRAY);
				setOpaque(true);
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
				
			}

			@Override
			public void mouseEntered(MouseEvent arg0) {
				hoverSlot = this;
			}

			@Override
			public void mouseExited(MouseEvent arg0) {
				hoverSlot = null;
				setBorder(BorderFactory.createLineBorder(Color.BLACK));
			}

			@Override
			public void mousePressed(MouseEvent arg0) {
				if (selectedSlot != null)
					selectedSlot.setBorder(BorderFactory.createLineBorder(Color.BLACK));
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
				
			}

			@Override
			public void mouseReleased(MouseEvent arg0) {
				// TODO Auto-generated method stub
				
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
	
	
	@SuppressWarnings("unused")
	private static class PlayerCreationMenu extends JFrame
	{
		//Points left for distribution
		private int remainingPoints;
		
		//Player portrait
		private Image portrait;
		
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
				image = new ImageIcon("images/portrait.jpg").getImage();
				
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
}
