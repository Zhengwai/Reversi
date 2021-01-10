import java.awt.*;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.TimeUnit;

/*
 * This application creates a reversi game with a menu, instructions and labels labeling 
 * information. User can choose to either play person vs person game or play against the AI.
 * User can also choose the color playing against the AI.
 * 
 * Author: Guanyu Song
 * Date: June 19th, 2018
 * 
 */
public class Reversi extends JFrame implements ActionListener{
	//declare an array of Grid objects to later record the icon of each button
	private Grid[] grids= new Grid[64];
	//declare an array of buttons to later form the gameboard
	private JButton[] buttons = new JButton[64];  
	//four panels
	private JPanel startPanel, gameboardPanel, controlPanel, instructionPanel; 
	//declare an array of imageIcon to later store the three types of icon
	private ImageIcon [] stones= new ImageIcon[3];
	//buttons of functions
	private JButton start, instruction;
	//moves - to record the total moves in one game
	private int moves;
	//helper arrays used in reversing
	private int[] index= new int[8];
	private int[] nextTo= {1,-1, 7, -7, 8, -8,9,-9};
	//menu bar
	private	JMenuBar menuBar;
	//menu and a submenu
	private JMenu menu,PVsAI;
	//menu items
	private JMenuItem restart, PVsP, usingBlack, usingWhite, getTotalMoves;
	//control the mode
	private boolean ai=false;
	//labels
	private JLabel click, blackNum, whiteNum, mode;
	//helper arrays used in AIPlay() method
	private int[] centerIndex= {18,19,20,21,26,27,28,29,34,35,36,37,42,43,44,45};
	private int[] cornerIndex= {0,7,56,63};
    private int[] badIndex= {1,6,8,9,14,15,48,49,54,55,57,62};
	private int[] goodIndex= {2,5,16,23,40,47,58,61};
	//string to store the user's color while playing against ai
	private String userColor;
	
	//constructive method
	public Reversi()
	{	//basic set up of the JFrame
		super("Reversi");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(630,750);  
		
		//initialize and set startPanel
		startPanel = new JPanel();
		startPanel.setBackground(Color.GRAY);
		
		//initialize and set the start button
		start = new JButton(new ImageIcon("cover.jpg"));
		start.setBackground(Color.LIGHT_GRAY);
		start.addActionListener(this);
		startPanel.add(start);
		
		//initialize and set the click label
		click=new JLabel("Click to Start");
		click.setForeground(Color.black);
		click.setFont(new Font("Arial", Font.BOLD, 30));
		startPanel.add(click);
		
		//initialize and set the control panel
		controlPanel = new JPanel();
	    controlPanel.setLayout(new FlowLayout(50,35,15));
	    controlPanel.setBackground(Color.LIGHT_GRAY);
	    
	    //initialize menu bar
	    menuBar = new JMenuBar();
	    
	    //initialize and set the menu
	    menu = new JMenu("Menu");
	    menu.setFont(new Font("Arial", Font.BOLD, 15));
	    
	    //initialize and set the menu item restart
	    restart = new JMenuItem("Restart");
        restart.addActionListener(this);
        
        //initialize and set the menu item PVsP 
        PVsP = new JMenuItem("Person Vs Person");
        PVsP.addActionListener(this);
        
        //initialize and set the menu PVsAI
        PVsAI= new JMenu("Person Vs AI");
        
        //initialize and set the menu items of using black and white
        usingBlack=new JMenuItem("Using Black");
        usingBlack.addActionListener(this);
        usingWhite=new JMenuItem("Using White");
        usingWhite.addActionListener(this);
        PVsAI.add(usingBlack);
        PVsAI.add(usingWhite);
        
        //initialize and set the item of getTotalMoves
        getTotalMoves = new JMenuItem("Get Total Number of Moves");
        getTotalMoves.addActionListener(this);
        
        //menu add the items(including a submenu)
        menu.add(restart);
        menu.add(PVsP);
        menu.add(PVsAI);
        menu.add(getTotalMoves);
        
        //menu bar adds menu, control panel adds menu bar
        menuBar.add(menu);
		controlPanel.add(menuBar);
	
		//initialize and set the gameboard panel
		gameboardPanel = new JPanel();
		gameboardPanel.setLayout(new GridLayout(8,8,0,0)); // 4x4 grid with 5 pixel 
		
		
		gameboardPanel.setBackground(Color.black); //Allows empty space to be black
		
		//initialize the image icons of black stone, white stone and grid
		stones[0]= new ImageIcon("black.png");	
		stones[1]= new ImageIcon("white.png");
		stones[2]= new ImageIcon("grid.png");
		
		//using a for loop to set the grid objects and buttons
		for (int i = 0; i < buttons.length; i++) 
		{
			grids[i]=new Grid(2);	
			buttons[i] = new JButton();
			buttons[i].setSize(100,100); 
			buttons[i].setIcon(resizeIcon(stones[2]));
			buttons[i].addActionListener(this); 
			gameboardPanel.add(buttons[i]);    								   //gameboardPanel
		}
		
		//add 4 stones to the center
		buttons[27].setIcon(resizeIcon(stones[0]));
		buttons[28].setIcon(resizeIcon(stones[1]));
		buttons[35].setIcon(resizeIcon(stones[1]));
		buttons[36].setIcon(resizeIcon(stones[0]));
		grids[27].setIcon(0);
		grids[28].setIcon(1);
		grids[35].setIcon(1);
		grids[36].setIcon(0);
		
		//initialize moves
		moves=0;
		
		//initialize and set the mode label
		mode = new JLabel("Mode: Person Vs Person");
		mode.setForeground(Color.BLACK);
		mode.setFont(new Font("Arial", Font.BOLD, 13));
		controlPanel.add(mode);
		
		//initialize and set the blackNum and whiteNum labels
		blackNum= new JLabel("Black Pieces: "+2);
		blackNum.setFont(new Font("Arial", Font.BOLD, 13));
		whiteNum= new JLabel("White Pieces: "+2);
		whiteNum.setFont(new Font("Arial", Font.BOLD, 13));
	
		//add the num labels
		controlPanel.add(blackNum);
		controlPanel.add(whiteNum);
		
		//initialize and set the instruction panel
		instructionPanel=new JPanel();
		instructionPanel.setBackground(Color.lightGray);
	
		//initialize and set the instruction button
		instruction= new JButton("Instruction");
		instruction.setBackground(Color.GRAY);
		instruction.setForeground(Color.BLACK);
		instruction.setFont(new Font("Arial", Font.PLAIN, 25));
		instruction.addActionListener(this);
		instructionPanel.add(instruction);
		
		//store file path and instructions
		String filePath = "instruction.txt";
		String instructions="Flip your opponent's pieces by  vertically, horizontally or diagonally trapping them between two of your own. " + 
				"You may flip multiple stones from multiple directions at a time. " +
				"At the start, there are 2 stones for black and white each, and black plays first. "+
				"You cannot put a stone on where there's already any stone. " + 
				"You cannot put stones on where you cannot flip any stones. " + 
				"You cannot flip your own stones. " + 
				"When there's no available place for one player, the other player will keep playing till there's a spot on board for player1 to play. " + 
				"When all grids are occupied by stones, the player with more stones of his color on the board wins. "+
				"You can choose to restart game/change mode/count total moves in the menu. ";
		
		//create a file of instructions
		try {
			IO.createOutputFile(filePath);
			//using string manipulation to make each sentence a line
			while(instructions.length()>0) {
				IO.println(instructions.substring(0,instructions.indexOf(".")+1));
				instructions=instructions.substring(instructions.indexOf(".")+2);
			}
			IO.closeOutputFile();
			
			}
		catch(Exception e) {
			
			}
		
		//get JFrame ready
		add(startPanel);
		setResizable(false);
		setVisible(true);     
	}//end method
	
	//actionPerformed method
	public void actionPerformed(ActionEvent e)  {
		
		//if start button is pressed, it switches to gameboard
		if(e.getSource()==start) {
			startPanel.setVisible(false);
			this.add(controlPanel, BorderLayout.NORTH);
			this.add(gameboardPanel, BorderLayout.CENTER);
			this.add(instructionPanel, BorderLayout.SOUTH);
			}
		
		//if any buttons on the gameboard is pushed
		for(int i=0; i<buttons.length; i++) {
			if(e.getSource() == buttons[i])    
			{	
				//if ai is turned off
				if(ai==false) {
					//run reverse method
					reverse(i);
					//update labels
					setNumLabels();
					
					//if the game is not over
					if(!finish())
					{
						//check if there's any available places to put stones for the other color
						boolean a=false;
						for(int j=0; j<64; j++) {
							a=available(j);
							if(a)
								break;
						}
						
						//if there's no available move now for one player, the system will
						//send a message and let the other player to play
						if(a==false) {
							JOptionPane.showMessageDialog(this, "No available moves now.");
							moves++;
						}
					}
					
					//if the game is over, count the result and message it
					else {
						count();
					}		
				}
				
				//if the ai is turned on
				else {
					//about the same process as above for player
					if(available(i)) {
						reverse(i);
						setNumLabels();
						if(!finish()) {
							boolean a=false;
							for(int j=0; j<64; j++) {
								a=available(j);
								if(a)
									break;
							}
				
							if(a==false) {
								JOptionPane.showMessageDialog(this, "No available moves for AI now. It's still your turn." );
								moves++;
							}
							
							//if there's any available places for ai, ai plays
							if(a) {
								AIPlay();
							}
						}
							//if not, count the result
						else {
							count();
						}	
					}
				}
			}
		}
		
		//if instruction button is pushed
		if(e.getSource()==instruction) {
			//read and message out the instruction file created before 
			try {
				IO.openInputFile("instruction.txt");
				String instruction="";
				String line = IO.readLine();
				while (line != null)
				{	instruction+=line+"\n";
					line = IO.readLine();
				}
				JOptionPane.showMessageDialog(this, instruction);	
			}
			catch(Exception ex) {
			}	
		}
		
		//if restart button is pushed, it will reset itself in the current mode
		if(e.getSource()==restart ) {
			reset();
		}
		
		//if PVsP button is pushed, it will reset itself and switch to person vs person mode
		if(e.getSource()==PVsP ) {
			reset();
			ai=false;
			mode.setText("Mode: Person Vs Person");
		}
		
		//if usingBlack button is pressed, it will reset itself and switch to person vs ai mode,
		//the player will use stones of black color
		if(e.getSource()==usingBlack ) {
			reset();
			userColor="black";
			ai=true;
			mode.setText("Mode: Person Vs AI - Using Black");
		}
		
		//if usingBlack button is pressed, it will reset itself and switch to person vs ai mode,
		//the player will use stones of white color
		if(e.getSource()==usingWhite ) {
			reset();
			userColor="white";
			ai=true;
			AIPlay();
			mode.setText("Mode: Person Vs AI - Using White");
		}
		
		//if getTotalMoves button is pressed, it will send a message of the total moves
		if(e.getSource()==getTotalMoves ) {
			JOptionPane.showMessageDialog(this, "The number of total moves is "+moves);
		}
	}//end method
	
	//short main
	public static void main(String[] args) {
		new Reversi();    	
	}//end main
	
	//reverse method
	private void reverse(int i) {
		//it is possible to reverse only when the icon is grid
		if(grids[i].getIcon()==2) {
			
			//check if it's an available place to reverse
			for(int j=0; j<8; j++) {
				if(i+nextTo[j]>=0&&i+nextTo[j]<=63&&sameLine(i, nextTo[j])) {
					if(grids[i+nextTo[j]].getIcon()==1-moves%2) {
						index[j]=i+nextTo[j];
						if(index[j]+nextTo[j]>=0&&index[j]+nextTo[j]<=63){
							while(grids[index[j]+nextTo[j]].getIcon()==1-moves%2) {
						
								if(index[j]+2*nextTo[j]<0||index[j]+2*nextTo[j]>63||
										sameLine(index[j], nextTo[j])==false)
									break;
								index[j]+=nextTo[j];
							}
							if(index[j]+nextTo[j]>=0&&index[j]+nextTo[j]<=63&&sameLine(index[j], nextTo[j])) 
								if(grids[index[j]+nextTo[j]].getIcon()==moves%2){
								
									//reverse the stone
									while(index[j]!=i) {
										buttons[index[j]].setIcon(resizeIcon(stones[moves%2]));
										grids[index[j]].setIcon(moves%2);
										index[j]-=nextTo[j];
					
									}
									
									//add stone to the place clicked
									buttons[i].setIcon(resizeIcon(stones[moves%2]));
									grids[i].setIcon(moves%2);
								}
				
						}
					}	
				}
			}
			
			//update moves
			if(grids[i].getIcon()==moves%2) 
				moves++;
		}
	}//end method
	
	//method of checking if a grid is available to put
	private boolean available(int i) {
		//about the same process as the first proccess in method reverse
		boolean a=false;
		if(grids[i].getIcon()==2) {
			for(int j=0; j<8; j++) {
				if(i+nextTo[j]>=0&&i+nextTo[j]<=63&&sameLine(i, nextTo[j])) {
					if(grids[i+nextTo[j]].getIcon()==1-moves%2) {
						index[j]=i+nextTo[j];
						if(index[j]+nextTo[j]>=0&&index[j]+nextTo[j]<=63){
							while(grids[index[j]+nextTo[j]].getIcon()==1-moves%2) {
						
								if(index[j]+2*nextTo[j]<0||index[j]+2*nextTo[j]>63||
										sameLine(index[j], nextTo[j])==false)
									break;
								index[j]+=nextTo[j];
							}
							if(index[j]+nextTo[j]>=0&&index[j]+nextTo[j]<=63&&sameLine(index[j], nextTo[j])) 
								if(grids[index[j]+nextTo[j]].getIcon()==moves%2){
									a=true;
								}
						}
					}
				}
			}
		}
		return a;
	}
				
	//the reversed line of stones can only exist in the same horizontal, vertical 
	//or diagonal line. This method is to avoid confusion for the buttons at borders
	private boolean sameLine(int index, int nextTo) {
		boolean sameLine=false;
		//check if in the range
		if(index+nextTo>=0&&index+nextTo<=63) {
			
			//check if horizontally in the same line
			if(nextTo==1||nextTo==-1) {
				if(index/8==(index+nextTo)/8) 
					sameLine=true;
			}
			
			//vertically it must be in the same line
			if(nextTo==8||nextTo==-8) 
				sameLine=true;
			
			//check if diagonally in the same line
			if(nextTo==7||nextTo==-7||nextTo==9||nextTo==-9){
				if(index/8==(index+nextTo)/8+1||index/8==(index+nextTo)/8-1)
					sameLine=true;
			}
		}
		
		return sameLine;
	}//end method
	
	//used to update the labels
	private void setNumLabels() {
		int b=0;
		int w=0;
		//count black and white
		for(int i=0; i<64; i++) {
			if(grids[i].getIcon()==0)
				b++;
			if(grids[i].getIcon()==1)
				w++;
		}
		blackNum.setText("Black Pieces: "+b);
		whiteNum.setText("White Pieces: "+w);
	}//end method
	
	//method to restart
	private void reset() {
		//reset everything
		for (int i = 0; i < 64; i++)  
		{
			grids[i].setIcon(2);	
			buttons[i].setIcon(resizeIcon(stones[2]));
			buttons[i].setBackground(Color.getColor("white.png", 1));
		}
		
		buttons[27].setIcon(resizeIcon(stones[0]));
		buttons[28].setIcon(resizeIcon(stones[1]));
		buttons[35].setIcon(resizeIcon(stones[1]));
		buttons[36].setIcon(resizeIcon(stones[0]));
		grids[27].setIcon(0);
		grids[28].setIcon(1);
		grids[35].setIcon(1);
		grids[36].setIcon(0);
		setNumLabels();
		moves=0;	
	}//end method
	
	//count the result
	private void count() {	
		int blackNum=0;
		
		//count total number of black stones, the number of white stones equals 64 minus it
		for(int i=0; i<64; i++) {
			if(grids[i].getIcon()==0)
				blackNum++;
		}
		
		//if it's person vs person mode, only say the color of winning
		if(ai=false) {
			if(blackNum>64-blackNum) {
				JOptionPane.showMessageDialog(this, "Game over\nBlack: " + blackNum + "\nWhite: " + (64-blackNum)+"\nBlack Wins");
			}
			if(blackNum<64-blackNum) {
				JOptionPane.showMessageDialog(this, "Game over\nBlack: " + blackNum + "\nWhite: " + (64-blackNum)+"\nWhite Wins");
			}
		}
		
		//if it's person vs ai mode, say if the player wins
		else {
			if(blackNum>64-blackNum&&userColor.compareTo("black")==0) {
				JOptionPane.showMessageDialog(this, "Game over\nBlack: " + blackNum + "\nWhite: " + (64-blackNum)+"\nYou Won!");
			}
			else if(blackNum<64-blackNum&&userColor.compareTo("white")==0) {
				JOptionPane.showMessageDialog(this, "Game over\nBlack: " + blackNum + "\nWhite: " + (64-blackNum)+"\nYou Won!");
			}
			else if(blackNum<64-blackNum&&userColor.compareTo("black")==0) {
				JOptionPane.showMessageDialog(this, "Game over\nBlack: " + blackNum + "\nWhite: " + (64-blackNum)+"\nYou Lost");
			}
			else if(blackNum>64-blackNum&&userColor.compareTo("white")==0) {
				JOptionPane.showMessageDialog(this, "Game over\nBlack: " + blackNum + "\nWhite: " + (64-blackNum)+"\nYou Lost");
			}
		}
		
		//if black have 32 stones, it draws
		if(blackNum==32) {
			JOptionPane.showMessageDialog(this, "Game over\nBlack: 32" + "\nWhite: 32"+"\nDraw");
		}
	}//end method
	
	//method to check if the game is over
	private boolean finish() {
		boolean f=true;
		//if there's any button on the board still with the icon of empty grid, the game is 
		//not over
		for(int i=0; i<64; i++) {
			if(grids[i].getIcon()==2){
				f=false;
				break;
			}	
		}
		return f;
	}//end method
	
	//method used in AIPlay(), check if the place is a "badIndex"
	private boolean badIndex(int i) {
		boolean a=false;
		for(int x=0; x<12;x++) {
			if(i==badIndex[x]) {
				a=true;
				break;
			}
		}
		
		return a;
	}//end method
	
	//AI play
	private void AIPlay() {
		
		SwingWorker<Void, String> worker = new SwingWorker<Void, String>(){
			
			protected Void doInBackground() throws Exception {
				//pause 0.5 seconds before playing
				Thread.sleep(500);
		        int b=0;
		     //Strategies
		        
		        //first, if there's empty place in the center area, play in the center area
		        for(int l=0; l<16; l++) {
		        	
		        	if(available(centerIndex[l])) {
		        		reverse(centerIndex[l]);
		        		b=1;
		        		break;
		        	}
		        }
		        
		        //second, if there's no available place in the center, occupy a corner
		        //if possible
		  		if(b==0) {
		        	for(int y=0; y<4; y++) {
		       			if(available(cornerIndex[y])) {
		   					reverse(cornerIndex[y]);
		        				b=1;
		        				break;
		        		}
		        	}	
		        }
		        
		  		//third, if corners are not available, try to play in the "goodIndex"
		  		if(b==0) {
		        	for(int i=0; i<8;i++) {
		        		if(available(goodIndex[i])) {
		        			reverse(goodIndex[i]);
		        			b=1;
		        			break;
		        		}
		        	}
		        }
		  		
		  		//fourth, if "goodIndexes" are not available, try to avoid "badIndexes"
		   		if(b==0) {
		   			for(int z=0; z<64; z++) {
		        		if(available(z)&&!badIndex(z)) {
		        			reverse(z);
		        			b=1;
		        			break;
		        		}		
		        	}
		        }
		   		
		   		//finally, if there's only "badIndexes" left, play in the "badIndexes"
		   		//where it's available to put
		        if(b==0) {
		 			for(int i=0; i<12; i++) {
		        		if(available(badIndex[i])) {
		        			reverse(badIndex[i]);
		        			b=1;
		        			break;
		        		}
		        	}
		        }
		        		
				//update labels
		  		setNumLabels();
		        
		  		//if finish, count
		  		if(finish())
		        	count();
		        
		  		//check if the play has anywhere available to put stones
		  		else {
		        	boolean a=false;
					for(int j=0; j<64; j++) {
						a=available(j);
						if(a)
							break;
					}
						
					//if not, message it and let AI play next
					if(a==false) {
						moves++;
						setNumLabels();
						message();			
						AIPlay();
					}
		        }
		        return null;
			}
		};	
		worker.execute();	
	}//end method

	//message when the player has no place to put stones
	private void message() {
		JOptionPane.showMessageDialog(this, "No available moves for you now. It's AI's turn." );
	}//end method

	//resize icon0
	private Icon resizeIcon(ImageIcon icon) {
	    //resize the image of each grid of gameboard to the fit size
		Image img = icon.getImage();  
	    Image resizedImage = img.getScaledInstance(80, 80,  java.awt.Image.SCALE_SMOOTH);  
	    return new ImageIcon(resizedImage);
	}//end method
	
}//end class