/* Hanna Bonert
 * Class: CSt 338- Software Design
 * This project creates a user-friendly puzzle-game
 * Make sure the folder with the puzzle piece images is in the src folder,
 * and entitled with the puzzle name in lower case letters.
 * The original picture should be entitled original.gif
 * BUG:If you select a piece, and then select a position that already has
 * a piece, that piece will override it, and you lose that piece. Also, 
 * you can drop a piece on any number of spaces.
 * TODO: Test for other puzzles
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

public class FinalProject
{
   static PuzzlePiece chosenPiece = new PuzzlePiece(), swapPiece1 = new PuzzlePiece(),
                       swapPiece2 = new PuzzlePiece();
   static int playedPieceRow, playedPieceCol, swapPiece1Row, swapPiece1Col;
   static boolean swapReady, swapPiece1Chosen;
   static PuzzleBoard puzzleGame = new PuzzleBoard("PuzzleMania", 3, 4);//think about
   
   public static void main(String[] args)
   {
      //declare and initialize the original puzzle, a scrambled version, and a 
      //blank version that will keep track of the player's solution
     Puzzle originalPuzzle = new Puzzle(3, 4, "riverside"), 
            scrambledPuzzle = new Puzzle(3, 4, "riverside"),
            playerPuzzle = new Puzzle(); 
     originalPuzzle.fillWithDefault();
     scrambledPuzzle.fillWithDefault();
     scrambledPuzzle.scramble();
     playerPuzzle.setNumColumns(originalPuzzle.getNumColumns());
     playerPuzzle.setNumRows(originalPuzzle.getNumRows());
          
      //set up GUI     
      puzzleGame.setSize(1250, 700);
      puzzleGame.setLocationRelativeTo(null);
      puzzleGame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
      //begin multithreading
      OriginalPicGUI picToCopyThread = new OriginalPicGUI("riverside");
      picToCopyThread.start();
     
      int rows = originalPuzzle.getNumRows();
      int columns = originalPuzzle.getNumColumns();
      JButton[][] piecesButtons = new JButton[rows][columns];
      JButton[][] puzzleButtons = new JButton[rows][columns];
      swapReady = false;
      
      Icon icon1;
      
      //add swap button
      JButton swapButton = new JButton("SWAP");
      puzzleGame.masterPuzzlePanel.add(swapButton, BorderLayout.PAGE_END);
      swapButton.addActionListener(new ActionListener() 
      {
         public void actionPerformed(ActionEvent e) 
         { 
            swapReady = true;
         }
      });
      
      for(int i = 0; i < scrambledPuzzle.getNumRows(); i++)
      {
         for(int j = 0; j < scrambledPuzzle.getNumColumns(); j++)
         {
            int rowNumber = i, columnNumber = j;           
            icon1 = scrambledPuzzle.getPieceAt(i, j).generatePieceIcon();
            JButton button1 = new JButton(icon1);
            puzzleGame.piecesPanel.add(button1);
            piecesButtons[i][j] = button1;
            piecesButtons[i][j].addActionListener(new ActionListener() 
            {
               public void actionPerformed(ActionEvent e) 
               { 
                  chosenPiece = scrambledPuzzle.getPieceAt(rowNumber, columnNumber);                  
                  playedPieceRow = rowNumber;
                  playedPieceCol = columnNumber;
                  piecesButtons[playedPieceRow][playedPieceCol].setBackground(Color.BLUE);
                  piecesButtons[playedPieceRow][playedPieceCol].setOpaque(true); 
               }
            });
         }
      }
      
      for(int i = 0; i < playerPuzzle.getNumRows(); i++)
      {
         for(int j = 0; j < playerPuzzle.getNumColumns(); j++)
         {
            int row = i, col = j;
            icon1 = playerPuzzle.getPieceAt(i, j).generatePieceIcon();
            JButton button2 = new JButton(icon1);
            puzzleGame.puzzlePanel.add(button2);
            puzzleButtons[i][j] = button2;
            puzzleButtons[i][j].addActionListener(new ActionListener() 
            {
               public void actionPerformed(ActionEvent e) 
               { 
                  //if swapReady = true, this piece was chosen to be swapped
                  if(swapReady)
                  {
                     //if swapPiece1Chosen = true, the second piece is being
                     //selected, and we can execute the swap
                     if(swapPiece1Chosen)
                     {
                        swapPiece2 = playerPuzzle.getPieceAt(row, col);
                        
                        playerPuzzle.setPieceAt(row, col, swapPiece1);
                        puzzleButtons[row][col].setIcon(swapPiece1.generatePieceIcon());
                        
                        playerPuzzle.setPieceAt(swapPiece1Row, swapPiece1Col, swapPiece2);
                        puzzleButtons[swapPiece1Row][swapPiece1Col].setIcon(swapPiece2.generatePieceIcon());
                        puzzleGame.setVisible(true);
                        
                        //reset the swap flags
                        swapReady = false;
                        swapPiece1Chosen = false;
                       
                     }
                     //this is the first piece chosen for swapping
                     else
                     {
                        //set all fields for swapPiece1
                        swapPiece1Chosen = true;
                        swapPiece1 = playerPuzzle.getPieceAt(row, col);
                        swapPiece1Row = row;
                        swapPiece1Col = col;
                     }
                  }
                  
                  //If we reached here, this piece was not chosen for swapping
                  else
                  {
                     playerPuzzle.setPieceAt(row, col, chosenPiece);
                     puzzleButtons[row][col].setIcon(chosenPiece.generatePieceIcon());
                     piecesButtons[playedPieceRow][playedPieceCol].setVisible(false);
                  }
                  
                  //check if the puzzle is unscrambled. If so, we are finished!
                  if(playerPuzzle.equals(originalPuzzle))
                  {
                     endGame();
                  }
               }
            });
         }
      }
      
      puzzleGame.setVisible(true);

   }
   
   //method that ends the game
   public static void endGame()
   {
      JLabel endMessage = new JLabel("Congratulations! You finished!");
      puzzleGame.piecesPanel.setLayout(new FlowLayout());
      puzzleGame.piecesPanel.add(endMessage);
      puzzleGame.setVisible(true);
   }
}

//This class creates a GUI representing the puzzle board
class PuzzleBoard extends JFrame
{
   public JPanel masterPuzzlePanel, piecesPanel, puzzlePanel;
   
   public PuzzleBoard(String title, int rows, int columns)
   {
      super(title);
      
      JPanel puzzlePiecesPanel = new JPanel();
      puzzlePiecesPanel.setLayout(new GridLayout(rows, columns));
      
      JLabel swapDirections = new JLabel("To swap: Hit the swap button, and then select the "
            + "2 pieces on the blue side you would like to swap");
      JLabel directions = new JLabel("First, select a piece from the green " +
                           "side. Then, select an empty position on the blue side.");
      directions.setHorizontalAlignment(JLabel.CENTER);
      
      JPanel masterPiecesPanel = new JPanel();
      masterPiecesPanel.setLayout(new BorderLayout());
      masterPiecesPanel.setBackground(new Color(255, 215, 0));
      //0, 153, 153
      
      piecesPanel = new JPanel();
      piecesPanel.setLayout(new GridLayout(rows, columns));
      piecesPanel.setBackground(new Color(0, 153, 153));
      
      masterPiecesPanel.add(directions, BorderLayout.PAGE_START);
      masterPiecesPanel.add(piecesPanel, BorderLayout.CENTER);
      
      masterPuzzlePanel = new JPanel();
      masterPuzzlePanel.setLayout(new BorderLayout());
      masterPuzzlePanel.setBackground(new Color(255, 215, 0));
      //102, 178, 255
      
      puzzlePanel = new JPanel();
      puzzlePanel.setLayout(new GridLayout(rows, columns));
      puzzlePanel.setBackground(new Color(102, 178, 255));
      
      masterPuzzlePanel.add(swapDirections, BorderLayout.PAGE_START);
      masterPuzzlePanel.add(puzzlePanel, BorderLayout.CENTER);
      
      
      setLayout(new GridLayout(1, 2));
      add(masterPiecesPanel);
      add(masterPuzzlePanel);
   }
}


/*
 * Objects of this class represent a puzzle piece. 
 * The pieceID member corresponds to the unique pieceID of each puzzle piece,
 * which is its row number (starting from zero), and then its column number,
 * represented by letter of alphabet (A = 1, etc.)
 * .gif files that represent the puzzle pieces are assumed to be
 * titled according to the format pieceID.gif
 * Make sure the folder with the puzzle piece images is in the src folder
 * and entitled with the puzzle name in lower case letters
 *TODO: implement clone
 */
class PuzzlePiece//--------------------------------------------------------------------
{
   private String pieceID;
   private String puzzleName;
   
   //default constructor-creates a blank piece
   public PuzzlePiece()
   {
      pieceID = "";
      puzzleName = "";
   }
   
   //argument-taking constructor
   public PuzzlePiece(String pieceId, String puzzleNam)
   {
      if(!setPieceID(pieceId))
         pieceID = "";
      if(!setPuzzleName(puzzleNam))
         puzzleName = "";
   }
   
   //mutator for pieceID
   public boolean setPieceID(String pieceId)
   {
      //only puzzles max 10 x 10 are allowed, so the length should not be more  
      //or less than 2
      if(pieceId.length() == 2)
      {
         char row = pieceId.charAt(0), col = pieceId.charAt(1);
         //check that the first char is a digit, and the second char a letter
         if(Character.isDigit(row) && Character.isLetter(col))
         {
            pieceID = pieceId;
            return true;
         }
      }

      return false;
   }   
   
   //mutator for puzzleName
   public boolean setPuzzleName(String puzzleNam)
   {
      String pzlNam = "";
      
      //checks that the puzzle name is not an empty string
      if(puzzleNam.length() == 0)
         return false;
      
      //checks that the puzzle name is only lower case letters, and if not,
      //makes them all lower case
      for(int i = 0; i < puzzleNam.length(); i++)
      {
         if(Character.isUpperCase(puzzleNam.charAt(i)))
            pzlNam += Character.toLowerCase(puzzleNam.charAt(i));
         else
            pzlNam += puzzleNam.charAt(i);
      }
      
      puzzleName = pzlNam;
      return true;
   }
   
   //accessor for pieceID
   public String getPieceID()
   {
      return pieceID;
   }
   
   //accessor for puzzleName
   public String getPuzzleName()
   {
      return puzzleName;
   }

   

   //generates an icon representing the puzzle piece
   public Icon generatePieceIcon()
   {
      Icon pieceIcon;
      
      pieceIcon = new ImageIcon("src/" + puzzleName + "/"  + pieceID  + ".gif");
      
      return pieceIcon;
   }
   
   //generates a string representing the puzzle piece
   public String toString()
   {
      String retStr = "Puzzle Piece ID: " + pieceID + " of puzzle: " 
                       + puzzleName;
                 
      return retStr;
   }
   
   //tests if two puzzlePiece objects are equal
   public boolean equals(PuzzlePiece compPiece)
   {
      if(puzzleName.equals(compPiece.getPuzzleName())
                && pieceID.equals(compPiece.getPieceID()))
         return true;
      
      return false;        
   }
   
}

//TODO: implement clone
class Puzzle//------------------------------------------------------------------------
{
   //static final int MAX_PIECES_IN_PUZZLE = 100;
   static final int MAX_ROWS = 10;
   static final int MAX_COLUMNS = 10;
   
   private int numRows;
   private int numColumns;
   private String puzzleName;
   private PuzzlePiece[][] puzzleArray;
   
   //default constructor
   public Puzzle()
   {
      setNumRows(MAX_ROWS);
      setNumColumns(MAX_COLUMNS);
      setPuzzleName("");
      
      puzzleArray= new PuzzlePiece [numRows][numColumns];
      
      for(int row = 0; row < numRows; row++)
         for(int col = 0; col < numColumns; col++)
            puzzleArray[row][col] = new PuzzlePiece();
   }
   
   //constructor that just takes row and column numbers and puzzleName
   //fills the puzzle with blank puzzle pieces
   public Puzzle(int rowsNum, int columnsNum, String puzzleNam)
   {
      if(!setNumRows(rowsNum))
            numRows = MAX_ROWS;
      if(!setNumColumns(columnsNum))
            numColumns = MAX_COLUMNS;
      
      if(!setPuzzleName(puzzleNam))
         puzzleName = "";
      
      puzzleArray= new PuzzlePiece [numRows][numColumns];
      
      for(int row = 0; row < numRows; row++)
         for(int col = 0; col < numColumns; col++)
            puzzleArray[row][col] = new PuzzlePiece();
      
   }
   
   //accessor for numRows
   public int getNumRows()
   {
      return numRows;
   }
   
   //accessor for numColumns
   public int getNumColumns()
   {
      return numColumns;
   }
   
   //accessor for puzzleName
   public String getPuzzleName()
   {
      return puzzleName;
   }
   
   //mutator for numRows
   public boolean setNumRows(int rowNum)
   {
      if(rowNum <= MAX_ROWS)
      {
         numRows = rowNum;
         return true;
      }
      return false;
   }
   
   //mutator for numColumns
   public boolean setNumColumns(int colNum)
   {
      if(colNum <= MAX_COLUMNS)
      {
         numColumns = colNum;
         return true;
      }
      return false;
   }
   
   //mutator for puzzleName
   public boolean setPuzzleName(String puzzleNam)
   {
      String pzlNam = "";
      
      //checks that the puzzle name is not an empty string
      if(puzzleNam.length() == 0)
         return false;
      
      //checks that the puzzle name is only lower case letters, and if not,
      //makes them all lower case
      for(int i = 0; i < puzzleNam.length(); i++)
      {
         if(Character.isUpperCase(puzzleNam.charAt(i)))
            pzlNam += Character.toLowerCase(puzzleNam.charAt(i));
         else
            pzlNam += puzzleNam.charAt(i);
      }
      
      puzzleName = pzlNam;
      return true;
   }
   
   //fills puzzleArray with its pieces in order, based on pieceID
   public void fillWithDefault()
   {
      String pieceIDStr = "";
      for(int row = 0; row < numRows; row++)
         for(int col = 0; col < numColumns; col++)
         {
            pieceIDStr = Integer.toString(row) + convertColNumToAlpha(col);
            puzzleArray[row][col].setPuzzleName(puzzleName);
            puzzleArray[row][col].setPieceID(pieceIDStr);
         }
   }
   
   //private helper to convert a column number to its alphabet version
   private static String convertColNumToAlpha(int colNum)
   {
      //we are only allowed to have up to 10 columns
      switch(colNum)
      {
         case 0:
            return "A";
         case 1:
            return "B";
         case 2:
            return "C";
         case 3:
            return "D";
         case 4:
            return "E";
         case 5:
            return "F";
         case 6:
            return "G";
         case 7:
            return "H";
         case 8:
            return "I";
         case 9:
            return "J";
      }
      return "";
   }
   
   //returns a copy of the puzzle piece at puzzleArray[row][col]
   public PuzzlePiece getPieceAt(int row, int col)
   {
      PuzzlePiece retPiece = new PuzzlePiece();
      
      if(row >= numRows || row < 0 || col < 0
                                           || col >= numColumns)
         return retPiece;
      //use clone-------------------------------------------------------------------
      retPiece.setPieceID(puzzleArray[row][col].getPieceID());
      retPiece.setPuzzleName(puzzleArray[row][col].getPuzzleName());
      
      return retPiece;      
   }
   
   //returns true if the piece was successfully set 
   //sets puzzleArray[row][col] to a copy of the piece passed as an argument
   //returns false if it exceeds numRows or numCols
   public boolean setPieceAt(int row, int col, PuzzlePiece piece)
   {
      if(row >= numRows || col >= numColumns || row < 0 || col < 0)
         return false;
      //use clone---------------------------------------------------------------------
      puzzleArray[row][col].setPieceID(piece.getPieceID());
      puzzleArray[row][col].setPuzzleName(piece.getPuzzleName());
      return true;
      
   }
   
   //scrambles up a puzzle by randomly generating a number between 0 and numRows
   //and one between 0 and numColumns. The piece we are up to in the nested loop
   //is then exchanged with the piece at the randomly generated position
   //CAUTION: This method scrambles up the puzzle itself, not a copy
   public void scramble()
   {
      Random rand = new Random();
      PuzzlePiece temp = new PuzzlePiece();
      
      for(int i = 0; i < numRows; i++)
         for(int j = 0; j < numColumns; j++)
         {
            int randRow = rand.nextInt(numRows - i);//should be numRows?
            int randCol = rand.nextInt(numColumns - i);
            
            temp = puzzleArray[randRow][randCol];
            puzzleArray[randRow][randCol] = puzzleArray[i][j];
            puzzleArray[i][j] = temp;
         }
   }
   
   //Tests if the contents of 2 puzzle objects are the same
   public boolean equals(Puzzle compPuzzle)
   {
      boolean retBool = true;
      
      if(numRows != compPuzzle.getNumRows() || numColumns != compPuzzle.getNumColumns())
         return false;
      
      for(int i = 0; i < numRows; i++)
         for(int j = 0; j < numColumns; j++)
         {
            if(!puzzleArray[i][j].equals(compPuzzle.getPieceAt(i, j)))
               retBool = false;
         }
        
      return retBool;
   }
   
   //for testing
   public void displayToConsole()
   {
      System.out.println( "Content of puzzle: " + 
                               Arrays.deepToString( puzzleArray ) );
   }
}

//A class that creates a separate GUI displaying the original image to be 
//unscrambled, using multithreading
class OriginalPicGUI extends Thread
{
   private String puzzleName;
   
   //Only one constructor- parameter-taking, that takes the puzzlename as a parameter
   public OriginalPicGUI(String pzlNam)
   {
      puzzleName = pzlNam;
   }
   
   //overriden run method that creates the GUI
   public void run()
   {
      JFrame originalPicFrame = new JFrame("PuzzleMania");
      originalPicFrame.setSize(400, 300);
      originalPicFrame.setLayout(new FlowLayout());
      originalPicFrame.setLocationRelativeTo(null);
      originalPicFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      
      JPanel picPanel = new JPanel();
      picPanel.setLayout(new BorderLayout());
      
      Icon picIcon = new ImageIcon("src/" + puzzleName + "/original.gif");
      JLabel picLabel = new JLabel(picIcon);
      
      JLabel header = new JLabel
                      ("This is the picture you are trying to create:");
      header.setHorizontalAlignment(JLabel.CENTER); 
      
      picPanel.add(picLabel, BorderLayout.CENTER);
      picPanel.add(header, BorderLayout.PAGE_START);
      originalPicFrame.add(picPanel);
      originalPicFrame.setVisible(true);     
   }
}

