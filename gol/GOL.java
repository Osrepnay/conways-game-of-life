package gol;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
public class GOL{
	boolean[][] board;
	int[] boardSize;
	boolean paused=true;
	Thread gameLoop;
	JFrame frame;
	JPanel panel;
	JPanel outerPanel;
	int[] screenSize=new int[2];
	int squareSize;
	
	public static void main(String[] args){
		new GOL(args);
	}
	public GOL(String[] boardSizeString){
		//set look and feel
		try{
			for(UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
				if("Nimbus".equals(info.getName())){
					UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		}catch(Exception e){System.err.println("Could not set Nimbus Look and Feel:\n"+e);}
		
		int[] boardSizeTemp=new int[]{Integer.valueOf(boardSizeString[0]), Integer.valueOf(boardSizeString[1])};
		boardSize=boardSizeTemp;
		board=new boolean[boardSize[0]][boardSize[1]];
		
		frame=new JFrame();
		GraphicsEnvironment ge=GraphicsEnvironment.getLocalGraphicsEnvironment();
		screenSize[0]=ge.getMaximumWindowBounds().width;
		screenSize[1]=ge.getMaximumWindowBounds().height;
		System.out.println(boardSize[0]);
		frame.setSize(screenSize[0], screenSize[1]);
		squareSize=(int)((screenSize[0]/2)/boardSize[0]);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		outerPanel=new JPanel();
		panel=new GamePanel();
		panel.addMouseListener(new MouseDetector());
		JButton pauseButton=new JButton("Unpause");
		pauseButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(paused){
					paused=false;
					pauseButton.setText("Pause");
				}else{
					paused=true;
					pauseButton.setText("Unpause");
				}
			}
		});
		JPanel buttonPanel=new JPanel();
		buttonPanel.add(pauseButton);
		outerPanel.setLayout(new GridLayout(1, 2));
		outerPanel.add(panel);
		outerPanel.add(buttonPanel);
		
		frame.add(outerPanel);
		frame.setVisible(true);
		
		gameLoop=new Thread(new GameLoop());
		gameLoop.start();
	}
	class GamePanel extends JPanel{
		@Override
		public void paintComponent(Graphics g){
			Graphics2D g2=(Graphics2D)g;
			for(int i=0; i<boardSize[0]; i++){
				for(int j=0; j<boardSize[1]; j++){
					g2.setColor(board[i][j] ? Color.BLACK : Color.WHITE);
					g2.fillRect(i*squareSize, j*squareSize, squareSize, squareSize);
					
					g2.setColor(Color.LIGHT_GRAY);
					g2.drawLine(0, j*squareSize, boardSize[0]*squareSize, j*squareSize);
				}
				g2.setColor(Color.LIGHT_GRAY);
				g2.drawLine(i*squareSize, 0, i*squareSize, boardSize[1]*squareSize);
			}
		}
	}
	class GameLoop implements Runnable{
		public void run(){
			while(true){
				while(!paused){
					boolean[][] newBoard=new boolean[boardSize[0]][boardSize[1]];
					for(int i=0; i<boardSize[0]; i++){
						for(int j=0; j<boardSize[1]; j++){
							int neighbors=getNeighborsAlive(new int[]{i, j});
							if(board[i][j]){
								if(neighbors==2 || neighbors==3){
									newBoard[i][j]=true;
								}else{
									newBoard[i][j]=false;
								}
							}else{
								if(neighbors==3){
									newBoard[i][j]=true;
								}
							}
						}
					}
					board=newBoard;
					//printBoard(board);
					panel.repaint();
					try{
						Thread.sleep(1000);
					}catch(Exception e){System.err.println(e);}
				}
				System.out.print("");
			}
		}
	}
	class MouseDetector implements MouseListener{
		//useless
		public void mousePressed(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseExited(MouseEvent e) {}
		
		public void mouseClicked(MouseEvent e) {
			int clickedSquareX=(int)e.getX()/squareSize;
			int clickedSquareY=(int)e.getY()/squareSize;
			board[clickedSquareX][clickedSquareY]=!board[clickedSquareX][clickedSquareY];
			panel.repaint();
		}
	}
	public int getNeighborsAlive(int[] pos){
		int alive=0;
		if(pos[0]-1>=0 && board[pos[0]-1][pos[1]]){
			alive++;
		}
		if(pos[0]+1<boardSize[0] && board[pos[0]+1][pos[1]]){
			alive++;
		}
		if(pos[1]-1>=0 && board[pos[0]][pos[1]-1]){
			alive++;
		}
		if(pos[1]+1<boardSize[1] && board[pos[0]][pos[1]+1]){
			alive++;
		}
		if(pos[0]-1>=0 && pos[1]-1>=0 && board[pos[0]-1][pos[1]-1]){
			alive++;
		}
		if(pos[0]+1<boardSize[0] && pos[1]-1>=0 && board[pos[0]+1][pos[1]-1]){
			alive++;
		}
		if(pos[0]-1>=0 && pos[1]+1<boardSize[1] && board[pos[0]-1][pos[1]+1]){
			alive++;
		}
		if(pos[0]+1<boardSize[0] && pos[1]+1<boardSize[1] && board[pos[0]+1][pos[1]+1]){
			alive++;
		}
		return alive;
	}
	public void printBoard(boolean[][] board){
		//flipped to go rtl-utd instead of utd-rtl
		for(int j=0; j<boardSize[1]; j++){
			for(int i=0; i<boardSize[0]; i++){
				System.out.print(board[i][j] ? "O " : "  ");
			}
			System.out.println();
		}
	}
}
