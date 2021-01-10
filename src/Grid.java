/*
 * This class is to create an object to record the icon of buttons in the gameboard
 */
public class Grid {
	//gridIcon, used to record the current icon of each grid. 0 for black stone, 1 for white
	//stone, and 2 for empty grid
	private int gridIcon;

	//constructive method
	Grid(int g){
	gridIcon=g;
	}//end method


	//setIcon
	public void setIcon(int i) {
		gridIcon=i;
	}//end method

	//getIcon
	public int getIcon() {
		return gridIcon;
	}//end method

}//end class
