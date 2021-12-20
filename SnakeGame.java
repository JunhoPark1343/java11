package finalproject;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.LinkedList;
import java.util.Random;

public class SnakeGame {
	static class MyFrame extends JFrame{
		
		static class Coordinate {
			int x;
			int y;
			public Coordinate(int x, int y) {
				this.x = x;
				this.y = y;
			}
		}
		
		static JPanel panelNorth;
		static JPanel panelCenter;
		static JLabel labelTitle;
		static JLabel labelMessage;
		static JPanel[][] panels = new JPanel[20][20];
		static int[][] map = new int[20][20]; //Jewel 9, Bomb 8, 0 Blank 
		static LinkedList<Coordinate> snake = new LinkedList<Coordinate>();
		static int dir = 3; // 진행방향 0: up, 1 : down, 2 : left, 3 : right
		static int score = 0;
		static int time = 0; // 초단위
		static int timeCount = 0; // 뱀의 속도
		static Timer timer = null;
		
		public MyFrame(String title) {
			super(title);
			this.setSize(400, 500);
			this.setVisible(true);
			this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			
			initUI(); 
			makeSnakeList(); // 뱀 몸통
			startTimer(); // 타이머
			setKeyListener(); 
			makeJewel(); // 보석
		}
		
		public void makeJewel() {
			Random rand = new Random(); // x : 0~19 , y : 0~19
			int randX = rand.nextInt(19);
			int randY = rand.nextInt(19);
			map[randX][randY] = 9; 
		}
		
		public void setKeyListener() {
			this.addKeyListener(new KeyAdapter() {
				@Override
				public void keyPressed(KeyEvent e) {
					if(e.getKeyCode() == KeyEvent.VK_UP) {
						if(dir != 1)
							dir = 0;
					}else if(e.getKeyCode() == KeyEvent.VK_DOWN) {
						if(dir != 0)
							dir = 1;
					}else if(e.getKeyCode() == KeyEvent.VK_LEFT) {
						if(dir != 3)
							dir = 2;
					}else if(e.getKeyCode() == KeyEvent.VK_RIGHT) {
						if(dir != 2)
							dir = 3;
					}					
				}
			});
		}
		public void startTimer() {
			timer = new Timer(200, new ActionListener() { // 200ms 간격
				
				@Override
				public void actionPerformed(ActionEvent e) {
					timeCount += 1;
					
					if(timeCount % 5 == 0) {
						time ++; // 1초 증가
					}
					moveSnake(); // 뱀의 이동
					updateUI();  // 뱀과 보석의 갱신
				}
			});
			timer.start();
		}
		public void moveSnake() {
			Coordinate headCoordinateXY = snake.get(0); //머리
			int  headCoordinateX =  headCoordinateXY.x;
			int  headCoordinateY =  headCoordinateXY.y;
			
			if(dir == 0) {
				boolean isColl = checkCollision(headCoordinateX, headCoordinateY-1);
				if(isColl == true) {
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new Coordinate(headCoordinateX, headCoordinateY-1));
				snake.remove(snake.size()-1);
			}else if(dir == 1) {
				boolean isColl = checkCollision(headCoordinateX, headCoordinateY+1);
				if(isColl == true) {
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new Coordinate(headCoordinateX, headCoordinateY+1));
				snake.remove(snake.size()-1);
			}else if(dir == 2) {
				boolean isColl = checkCollision(headCoordinateX-1, headCoordinateY);
				if(isColl == true) {
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new Coordinate(headCoordinateX-1, headCoordinateY));
				snake.remove(snake.size()-1);
			}else if(dir == 3) {
				boolean isColl = checkCollision(headCoordinateX+1, headCoordinateY);
				if(isColl == true) {
					labelMessage.setText("Game Over!");
					timer.stop();
					return;
				}
				snake.add(0, new Coordinate(headCoordinateX+1, headCoordinateY));
				snake.remove(snake.size()-1);
			}
		}
		
		public boolean checkCollision(int headCoordinateX, int headCoordinateY) {
			if(headCoordinateX<0 || headCoordinateX>19 || headCoordinateY<0 || headCoordinateY>19) { //벽에 충돌
				return true;
			}
			for(Coordinate xy : snake) {
				if(headCoordinateX == xy.x && headCoordinateY == xy.y) {
					return true;
				}
			}
			if(map[headCoordinateY][headCoordinateX]==9) {
				map[headCoordinateY][headCoordinateX]=0;
				addTail();
				makeJewel();
				score += 100;
			}
			return false;
		}
		
		public void addTail() {
			int tailX = snake.get(snake.size()-1).x;
			int tailY = snake.get(snake.size()-1).y;
			int tailX2 = snake.get(snake.size()-2).x;
			int tailY2 = snake.get(snake.size()-2).y;
			
			if(tailX<tailX2) { // right
				snake.add(new Coordinate(tailX-1,tailY));
			}else if(tailX>tailX2) { // left
				snake.add(new Coordinate(tailX+1,tailY));
			}else if(tailY<tailY2) { // up
				snake.add(new Coordinate(tailX,tailY-1));
			}else if(tailY>tailY2) { // down
				snake.add(new Coordinate(tailX,tailY+1));
			}
		}
		
		public void updateUI() {
			labelTitle.setText("Score: "+ score + " Time: "+time);
			
			//clear tile(panel)
			for(int i=0; i<20; i++) { // Row
				for(int j=0; j<20; j++) {
					if(map[i][j] == 0) {
						panels[i][j].setBackground(Color.ORANGE);
					}else if(map[i][j] == 9) {
						panels[i][j].setBackground(Color.GREEN);
					}
				}
			}	
			
			//draw Snake
			int index = 0;
			for(Coordinate xy : snake) {
				if(index == 0) { // head
					panels[xy.y][xy.x].setBackground(Color.RED);
				}else { // body, tail
					panels[xy.y][xy.x].setBackground(Color.BLUE);
				}
				
				index ++;
			}
		}
		
		public void makeSnakeList() {
			snake.add(new Coordinate(10, 10)); // 뱀의 머리
			snake.add(new Coordinate(9, 10)); // 몸통
			snake.add(new Coordinate(8, 10));
		}
		
		public void initUI() {
			this.setLayout(new BorderLayout());
			
			panelNorth = new JPanel();
			panelNorth.setPreferredSize(new Dimension(400, 100));
			panelNorth.setBackground(Color.BLACK);
			panelNorth.setLayout(new FlowLayout());
			
			labelTitle = new JLabel("Score : 0, Time : 0sec");
			labelTitle.setPreferredSize(new Dimension(400, 50));
			labelTitle.setFont(new Font("TimesRoman", Font.BOLD, 20));
			labelTitle.setForeground(Color.WHITE);
			labelTitle.setHorizontalAlignment(JLabel.CENTER);
			panelNorth.add( labelTitle );
			
			labelMessage = new JLabel("EAT JEWEL!!");
			labelMessage.setPreferredSize(new Dimension(400, 20));
			labelMessage.setFont(new Font("TimesRoman", Font.BOLD, 20));
			labelMessage.setForeground(Color.RED);
			labelMessage.setHorizontalAlignment(JLabel.CENTER);
			panelNorth.add( labelMessage );
			
			this.add("North",panelNorth);
			
			panelCenter = new JPanel();
			panelCenter.setLayout(new GridLayout(20, 20));
			for(int i=0; i<20; i++) { // Row
				for(int j=0; j<20; j++) { // Column
					map[i][j] = 0; // blank
					panels[i][j] = new JPanel();
					panels[i][j].setPreferredSize(new Dimension(20, 20));
					panels[i][j].setBackground(Color.ORANGE);
					panelCenter.add(panels[i][j]);
				}
			}
			this.add("Center",panelCenter);
			this.pack(); // 빈공간 삭제
		}	
	}
	public static void main(String[] args) {
		new MyFrame("Snake Game");

	}

}
