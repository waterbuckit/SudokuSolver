/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sudokusolver;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 *
 * @author waterbucket
 */
public class SudokuPlayer {

    private JFrame frame;
    private SudokuBoard sb;

    public SudokuPlayer() {
        this.frame = new JFrame();
        this.frame.setTitle("SudokuSolver");
        this.sb = new SudokuBoard();
        this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.frame.add(sb);
        this.frame.pack();
        this.frame.setVisible(true);
        this.sb.setVisible(true);
        this.sb.startSolvingBoard();
    }

    public static void main(String[] args) {
        SudokuPlayer player = new SudokuPlayer();
    }

    private class Location {

        private int x;
        private int y;

        public Location(Location loc) {
            this.x = loc.getX();
            this.y = loc.getY();
        }

        public Location(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public void setX(int x) {
            this.x = x;
        }

        public int getY() {
            return y;
        }

        public void setY(int y) {
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Location)) {
                return false;
            }
            Location loc = (Location) obj;

            return this.x == loc.x && this.y == loc.y;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 83 * hash + this.x;
            hash = 83 * hash + this.y;
            return hash;
        }

    }

    private class SudokuBoard extends JPanel {

        private Cell[][] board;
        private final int ScaleFactor = 50;

        public SudokuBoard() {
            this.board = new Cell[9][9];
            this.setUpboard(); // set everything to 0 for the sake of avoiding nullpointerexceptions
        }

        private int roundTo(int n) {
            if (n > 0) {
                return (int) (Math.ceil(n / 3.0) * 3);
            } else if (n < 0) {
                return (int) (Math.floor(n / 3.0) * 3);
            } else {
                return 3;
            }
        }

        private boolean checkBox(int boundX, int boundY, int i) {
            for(int yTemp = 0; yTemp < 3; yTemp++){
                for(int xTemp = 0; xTemp < 3; xTemp++){
                    if(this.board[xTemp+boundX][yTemp+boundY].getValue() == i){
                        return false;
                    }
                }
            }
            return true;
//            for (int yTemp = boundY - 3; yTemp < boundY; yTemp++) {
//                for (int xTemp = boundX - 3; xTemp < boundX; xTemp++) {
//                    if (this.board[xTemp][yTemp].getValue() == i) {
//                        return false;
//                    }
//                }
//            }
//            return true;
        }

        private class Cell {

            private boolean fixed;
            private int value;

            public Cell(int value, boolean fixed) {
                this.fixed = fixed;
                this.value = value;
            }

            public boolean isFixed() {
                return fixed;
            }

            public void setFixed(boolean fixed) {
                this.fixed = fixed;
            }

            public int getValue() {
                return value;
            }

            public void setValue(int value) {
                this.value = value;
            }
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            this.setBackground(Color.BLACK);
            this.drawGridLines(g2d);
            this.drawNumbers(g2d);
        }

        private boolean backTrack() {
            Location loc = new Location(this.findBlankCell());
            if (loc.getX() == -1) { // if there are no blank cells then we must have finished so we can just return true.
                return true;
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                System.out.println("Interrupted!");
            }
            for (int i = 1; i <= 9; i++) {
                // we need to ensure that the number we want to put in is valid.
                if (isValid(loc.getX(), loc.getY(), i)) {
                    this.board[loc.getX()][loc.getY()].setValue(i);
                    this.repaint();
                    this.revalidate();
                    if (backTrack()) { // recursively call this until there are no blank cells.
                        return true;
                    }
                    // if the number didn't work, we set the value to 0, try a different number
                    this.board[loc.getX()][loc.getY()].setValue(0);
                    this.repaint();
                    this.revalidate();
                }
            }
            // if all the numbers we tried don't work then we go back.
            return false;
        }

        private boolean isValid(int x, int y, int i) {
            // we need to check the row, column, and box!
            boolean rows = true;
            boolean cols = true;
            boolean box = true;
            //check rows
            for (int row = 0; row < this.board.length; row++) {
                if (this.board[x][row].getValue() == i) {
                    rows = false;
                    break;
                }
            }
            //check cols
            for (int col = 0; col < this.board.length; col++) {
                if (this.board[col][y].getValue() == i) {
                    cols = false;
                    break;
                }
            }
            //check box (bound will define up to which element we need to iterate
//            box = checkBox(roundTo(x), roundTo(y), i);
            box = checkBox(x - x % 3, y - y % 3,i);
//            for (int yTemp = boundY - 3; yTemp < boundY; yTemp++) {
//                for (int xTemp = boundX - 3; xTemp < boundX; xTemp++) {
//                    if (this.board[xTemp][yTemp].getValue() == i) {
//                        box = false;
//                        okay = false;
//                        break;
//                    }
//                }
//                if (!okay) {
//                    break;
//                }
//            }
            // if all are satisfied
            return cols && rows && box;
        }

        private Location findBlankCell() {
            for (int y = 0; y < this.board.length; y++) {
                for (int x = 0; x < this.board.length; x++) {
                    if (this.board[x][y].getValue() == 0 && !this.board[x][y].isFixed()) {
                        return new Location(x, y);
                    }
                }
            }
            return new Location(-1, -1); // return -1 if there are no blank cells
        }

        private void manipulateBoardBacktrack() {
            Stack<Location> stack = new Stack<>(); // stack of all previous locations so that we can backtrack later on.
            for (int y = 0; y < this.board.length; y++) { // for every row
                for (int x = 0; x < this.board.length; x++) {
                    try {
                        // for every column
                        Thread.sleep(10);
                    } catch (InterruptedException ex) {
                        System.out.println("Interrupted!");
                    }
                    this.repaint();
                    this.revalidate();
                    // check whether the location is currently in the stack
                    if (!this.board[x][y].isFixed()) { // make sure that the current location is able to be changed.
                        stack.push(new Location(x, y)); // push the current x y to the stack so that we know we have visited here.
                        int arbitrary;
                        if (this.board[x][y].getValue() == 0) {
                            arbitrary = 1;
                        } else {
                            arbitrary = this.board[x][y].getValue() + 1;
                        }
                        while (arbitrary < 10) {
                            //Check whether the "arbitrary" variable is an acceptable number
                            // variables for conditions - assume that our arbitrary can be placed until proven otherwise
                            boolean rows = true;
                            boolean cols = true;
                            boolean box = true;
                            //check rows
                            for (int row = 0; row < this.board.length; row++) {
                                if (this.board[x][row].getValue() == arbitrary) {
                                    rows = false;
                                    break;
                                }
                            }
                            //check cols
                            for (int col = 0; col < this.board.length; col++) {
                                if (this.board[col][y].getValue() == arbitrary) {
                                    cols = false;
                                    break;
                                }
                            }
                            //check box (bound will define up to which element we need to iterate
                            int boundX = this.roundTo(x);
                            int boundY = this.roundTo(y);
                            boolean okay = true;
                            for (int yTemp = boundY - 3; yTemp < boundY; yTemp++) {
                                for (int xTemp = boundX - 3; xTemp < boundX; xTemp++) {
                                    if (this.board[xTemp][yTemp].getValue() == arbitrary) {
                                        box = false;
                                        okay = false;
                                        break;
                                    }
                                }
                                if (!okay) {
                                    break;
                                }
                            }
                            // if all are satisfied
                            if (cols && rows && box) {
                                this.board[x][y].setValue(arbitrary);
                                break;
                            } else {
                                arbitrary++;
                            }
                            if (arbitrary > 9) { // go back to the previous location in the stack
                                this.board[x][y].setValue(0);
                                stack.pop();
                                stack.pop();
                                // set the loop to be looking at the elements that are in the element previous to the top of the stack 
                                // the stack should had the element that could not be represented in the current position at the top.
                                x = stack.peek().getX();
                                y = stack.peek().getY();
//                                for (Location location : stack) {
//                                    System.out.println(location.getX() + "," + location.getY());
//                                }
//                                System.out.println("FINISH");
                                break;
                            }
                        }
                    }
                }
            }
        }
//        private void manipulateBoard() {
//            // go down the rows
//            for (int y = 0; y < this.board.length; y++) {
//                // check if the current row is complete
//                int sum = 0;
//                boolean changed = false;
//                for (int x = 0; x < this.board.length; x++) {
//                    // check and see if the current row's sum == 45
//                    sum = sum + this.board[x][y];
//                }
//                // if the row isn't complete then check which numbers we need
//                if (sum != 45) {
//                    ArrayList<Integer> tempArray = new ArrayList<>();
//                    // add all the possible numbers
//                    for (int i = 1; i <= 9; i++) {
//                        tempArray.add(i);
////                        System.out.println(tempArray.toString());
//                    }
//                    ArrayList<Integer> elementsToRemove = new ArrayList<>();
//                    for (int x = 0; x < this.board.length; x++) {
//                        if (tempArray.contains(this.board[x][y])) {
//                            elementsToRemove.add(this.board[x][y]);
//                        }
//                    }
//                    tempArray.removeAll(elementsToRemove);
//                    // find the first place where the is currently no number.
//                    for (int x = 0; x < this.board.length; x++) {
//                        if (this.board[x][y] == 0) {
////                            int yTemp = y;
//                            ArrayList<Integer> alreadyInColumn = new ArrayList<>();
//                            // check the column going downwards to see which numbers are contained compared to our tempArray
////                            while(yTemp < this.board.length){
//                            for (int yTemp = y; yTemp < this.board.length; yTemp++) {
//                                if (tempArray.contains(this.board[x][yTemp])) {
//                                    alreadyInColumn.add(this.board[x][yTemp]);
//                                }
////                                if(tempArray.contains(this.board[x][yTemp])){
////                                    for(int i = 0; i < tempArray.size(); i++){
////                                        if(tempArray.get(i) == this.board[x][yTemp]){
////                                            tempArray.remove(i);
////                                        }
////                                    }
////                                }
////                                yTemp++;
//                            }
//                            for (int yTemp2 = y; yTemp2 >= 0; yTemp2--) {
//                                if (tempArray.contains(this.board[x][yTemp2])) {
//                                    alreadyInColumn.add(this.board[x][yTemp2]);
//                                }
//                            }
//                            //check whether it will cause a fuck up further down the line
//                            tempArray.removeAll(alreadyInColumn);
//                            if (tempArray.size() > 0) {
//                                this.board[x][y] = tempArray.get(rand.nextInt(tempArray.size()));
//                                changed = true;
//                            }
////                            this.board[x][y] = tempArray.get(0);
//                            break;
//                        }
//                        // break so that this whole process can be done incrementally frame by frame so it looks cool lol
//                    }
//                    if (changed) {
//                        break;
//                    }
//                } else if (sum == 45 && y == this.board.length - 1) {
//                    this.done = true;
//                }
//            }
//        }

        private void startSolvingBoard() {
            if(this.backTrack());
            else{
                System.out.println("No solution!");
            }
//            this.manipulateBoardBacktrack();

            // board will be manipulated once per second
            // There should only be one element manipulated at a time.
//            Timer time = new Timer(delay, (ActionEvent arg0) -> {
////                this.manipulateBoard();
//                this.repaint();
//                this.revalidate();
//            });
//            time.start();
//            if (done) {
//                time.stop();
//                System.out.println("Stopped!");
//            }
        }

        private void setUpboard() {
            for (int y = 0; y < this.board.length; y++) {
                for (int x = 0; x < this.board.length; x++) {
                    this.board[x][y] = new Cell(0, false);
                }
            }
            this.board[0][0].setValue(5);
            this.board[0][0].setFixed(true);
            this.board[1][0].setValue(3);
            this.board[1][0].setFixed(true);
            this.board[0][1].setValue(6);
            this.board[0][1].setFixed(true);
            this.board[4][0].setValue(7);
            this.board[4][0].setFixed(true);
            this.board[1][2].setValue(9);
            this.board[1][2].setFixed(true);
            this.board[2][2].setValue(8);
            this.board[2][2].setFixed(true);
            this.board[3][1].setValue(1);
            this.board[3][1].setFixed(true);
            this.board[4][1].setValue(9);
            this.board[4][1].setFixed(true);
            this.board[5][1].setValue(5);
            this.board[5][1].setFixed(true);
            this.board[7][2].setValue(6);
            this.board[7][2].setFixed(true);
            this.board[0][3].setValue(8);
            this.board[0][3].setFixed(true);
            this.board[0][4].setValue(4);
            this.board[0][4].setFixed(true);
            this.board[0][5].setValue(7);
            this.board[0][5].setFixed(true);
            this.board[4][3].setValue(6);
            this.board[4][3].setFixed(true);
            this.board[3][4].setValue(8);
            this.board[3][4].setFixed(true);
            this.board[8][3].setValue(3);
            this.board[8][3].setFixed(true);
            this.board[8][4].setValue(1);
            this.board[8][4].setFixed(true);
            this.board[8][5].setValue(6);
            this.board[8][5].setFixed(true);
            this.board[5][4].setValue(3);
            this.board[5][4].setFixed(true);
            this.board[4][5].setValue(2);
            this.board[4][5].setFixed(true);
            this.board[1][6].setValue(6);
            this.board[1][6].setFixed(true);
            this.board[3][7].setValue(4);
            this.board[3][7].setFixed(true);
            this.board[4][7].setValue(1);
            this.board[4][7].setFixed(true);
            this.board[5][7].setValue(9);
            this.board[5][7].setFixed(true);
            this.board[4][8].setValue(8);
            this.board[4][8].setFixed(true);
            this.board[6][6].setValue(2);
            this.board[6][6].setFixed(true);
            this.board[7][6].setValue(8);
            this.board[7][6].setFixed(true);
            this.board[7][8].setValue(7);
            this.board[7][8].setFixed(true);
            this.board[8][8].setValue(9);
            this.board[8][8].setFixed(true);
            this.board[8][7].setValue(5);
            this.board[8][7].setFixed(true);
        }

        private void drawGridLines(Graphics2D g2d) {
            g2d.setColor(Color.WHITE);
            for (int y = 0; y < this.board.length + 1; y++) {
                for (int x = 0; x < this.board.length + 1; x++) {
                    // if the current lines being drawn are divisible by 3 (are multiples of 3)
                    if (x % 3 == 0 && y % 3 == 0) {
                        g2d.setStroke(new BasicStroke(3));
                        g2d.drawLine(0, y * this.ScaleFactor, this.board.length * this.ScaleFactor, y * this.ScaleFactor);
                        g2d.drawLine(x * this.ScaleFactor, 0, x * this.ScaleFactor, this.board.length * this.ScaleFactor);
                    } else {
                        g2d.setStroke(new BasicStroke(1));
                        g2d.drawLine(0, y * this.ScaleFactor, this.board.length * this.ScaleFactor, y * this.ScaleFactor);
                        g2d.drawLine(x * this.ScaleFactor, 0, x * this.ScaleFactor, this.board.length * this.ScaleFactor);
                    }
                }
            }
        }

        private void drawNumbers(Graphics2D g2d) {
            g2d.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
            for (int y = 0; y < this.board.length; y++) {
                for (int x = 0; x < this.board.length; x++) {
                    if (this.board[x][y].getValue() != 0 && this.board[x][y].isFixed()) {
                        g2d.setColor(Color.RED);
                        g2d.drawString(Integer.toString(this.board[x][y].getValue()), x * ScaleFactor + (ScaleFactor / 2), y * ScaleFactor + (ScaleFactor / 2));
                    } else if (this.board[x][y].getValue() != 0) {
                        g2d.setColor(Color.WHITE);
                        g2d.drawString(Integer.toString(this.board[x][y].getValue()), x * ScaleFactor + (ScaleFactor / 2), y * ScaleFactor + (ScaleFactor / 2));
                    }
                }
            }
        }
    }

}
