package minesweeper;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 *
 * @author Stusiak
 */
public class Minesweeper implements ActionListener {
    JFrame frame;
    JButton beginner;
    JButton intermediate;
    JButton expert;
    JPanel subPanel;
    public static GroupLayout layout;
    public static final int TILE_WIDTH = 40;
    int rows = 16;
    int cols = 30;
    int NUM_MINES = 10;
    public int[][] grid = new int[rows][cols];
    ArrayList<Integer> mineNums = new ArrayList<>();
    ImageIcon MINE = new ImageIcon("mine.png");
    ImageIcon TILE = new ImageIcon("minesweeper tile.jpg");
    ImageIcon RED_MINE = new ImageIcon("red_mine.png");
    ImageIcon FLAG = new ImageIcon("flagged tile.jpg");
    ArrayList<Location> checked = new ArrayList<>();
    ArrayList<Location> exposed = new ArrayList<>();
    int numExposed;
    JButton[][] buttons;
    HashMap buttonLocations = new HashMap();
    HashMap buttonByLocation = new HashMap();
    JButton current;
    boolean playing;
    /**
     * @param args the command line arguments
     */
    private void clearCellsAround(int row, int col) {
        Location loc = new Location(row,col);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int c = col-1+i;
                int r = row-1+j;
                Location newLoc = new Location(r,c);
                if (0 <= r && r < rows && 0 <= c && c < cols) {
                    int val = grid[r][c];
                    boolean cond1 = !newLoc.isIn(checked);
                    boolean cond2 = !newLoc.isIn(exposed);                    
                    if (cond1 && cond2){
                        current = buttons[r][c];
                        checked.add(newLoc);
                        if (val >= 0) {
                            numExposed++; 
                            current.setBackground(Color.white);
                            current.setForeground(Color.black);
                            exposed.add(newLoc);
                            checkWin();
                        }
                        if (val == 0) {
                            clearCellsAround(r,c);
                        }
                        
                    }
                
                }
            }
        }
    }
    
    public static void main(String[] args) {
        Minesweeper game = new Minesweeper();
    }
    public Minesweeper() {
        TILE = scaleIcon(TILE);
        MINE = scaleIcon(MINE);
        RED_MINE = scaleIcon(RED_MINE);
        FLAG = scaleIcon(FLAG);
        playing = true;
        numExposed = 0;
        buttons = new JButton[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                grid[i][j] = 0;
            }
        }
        Random rand = new Random(); 
        while (mineNums.size() < NUM_MINES){
            int newMine = rand.nextInt(rows*cols);
            if (!mineNums.contains(newMine)) {
                mineNums.add(newMine);
            }
        }
        for (int num : mineNums) {
            grid[num / cols][num % cols] = -1;
        }
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                if (grid[row][col] == -1) {
                    for (int i = 0; i < 3; i++) {
                        for (int j = 0; j < 3; j++) {
                            int r = row - 1 + i;
                            int c = col - 1 + j;
                            if (0<= r && r < rows && 0 <= c && c < cols){
                                if (grid[r][c] != -1){
                                    grid[r][c] += 1;
                                }
                            }
                        }
                    }
                }
                
            }
        }
        frame = new JFrame("Minesweeper");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new GridLayout(16, 30));
        for (int row = 0; row < rows; row++) {
            for (int col = 0; col < cols; col++) {
                int[] loc = new int[]{row,col};
                JButton button = new JButton();
                button.setPreferredSize(new Dimension(40,40));
                int val = grid[row][col];
                if (val != 0){
                    button.setText(Integer.toString(val));
                } else {
                    button.setText(" ");
                }
                buttons[row][col] = button;
                frame.add(buttons[row][col]);
                buttonLocations.put(button, loc);
                buttonByLocation.put(loc,button);
                button.addActionListener(this);
                button.setBackground(Color.lightGray);
                button.setForeground(Color.lightGray);
                button.setBorder(BorderFactory.createLineBorder(Color.black));
            }
        }
        frame.pack();
        frame.setVisible(true);
    }
    

    @Override
    public void actionPerformed(ActionEvent e) {
        if (playing) {
            JButton currentButton = (JButton) e.getSource();
            if ("-1".equals(currentButton.getText())) {
                currentButton.setText("");
                currentButton.setIcon(RED_MINE);
                loseGame();
            }
            currentButton.setBackground(Color.white);
            currentButton.setForeground(Color.black);
            numExposed++;
            int[] location = (int[])buttonLocations.get(currentButton);
            int row = location[0];
            int col = location[1];
            Location currentLoc = new Location(row,col);
            if (!currentLoc.isIn(exposed)) {
                exposed.add(new Location(row,col));
                checkWin();
                if (" ".equals(currentButton.getText())) {
                    clearCellsAround(row,col);
                }
            }
            System.out.println("Exposed: " + numExposed);
            System.out.println();
            System.out.println(rows*cols - exposed.size());
        }
    }

    private void checkWin() {
        if (exposed.size() == rows*cols - NUM_MINES) {
            playing = false;
            for (JButton[] buttonList : buttons) {
                for (JButton button : buttonList) {
                    if ("-1".equals(button.getText())) {
                        button.setText("");
                        button.setIcon(FLAG);
                    }
                }
            }
        }
    }

    private void loseGame() {
        for (int i = 0; i < rows; i++){
            for (int j = 0; j < cols; j++) {
                JButton button = buttons[i][j];
                button.setBackground(Color.white);
                if ("-1".equals(button.getText())) {
                    button.setText("");
                    button.setIcon(MINE);
                } else {
                    button.setForeground(Color.black);
                }
                 
            }
            
        }
    }

    private ImageIcon scaleIcon(ImageIcon imageIcon) {
        Image image = imageIcon.getImage(); 
        Image newimg = image.getScaledInstance(30, 30,  java.awt.Image.SCALE_SMOOTH); // scale it the smooth way 
        ImageIcon scaledIcon = new ImageIcon(newimg);
        return scaledIcon;
    }
    
    
    private static class Location {
        public int x;
        public int y;
        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }
        public static String toString(Location newLoc){
            return "(" + Integer.toString(newLoc.x) + ", " + Integer.toString(newLoc.y) + ")";
        }
        public boolean isEqualTo(Location newLoc){
            return this.x == newLoc.x && this.y == newLoc.y;
        }
        public boolean isIn(ArrayList<Location> newLocList) {
            boolean result = false;
            for (Location item : newLocList) {
                if (item.isEqualTo(this)) {
                    result = true;
                }
            }
            return result;
        }
    }
}   
