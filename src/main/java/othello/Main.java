package othello;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {
	static int boardSize = 8;
	static String[][] board = new String[boardSize][boardSize];
	//static String player = "BLACK";

	static final String EMPTY = "　";
	static final String BLACK = "●";
	static final String WHITE = "○";

	public static void main(String[] args) {
		init();
		display();
		
		Scanner sc = new Scanner(System.in);
		while(true) {
			int x = sc.nextInt();
			int y = sc.nextInt();	    
			String myColor = sc.next();
			
			if( canPut(x,y,myColor) ) {
				board[x][y] = ( "WHITE".equals(myColor) ) ? WHITE : BLACK ;
				display();
			}else {
				System.out.println("置けないよ");
			}

			if("end".equals(myColor)) break;
		}
	    sc.close();
	}
	
	private static void init(){
	    board[3][3] = BLACK;
	    board[3][4] = WHITE;
	    board[4][3] = WHITE;
	    board[4][4] = BLACK;
	}
	
	private static void display() {
		StringBuilder sb = new StringBuilder();

		sb.append(" | 0| 1| 2| 3| 4| 5| 6| 7|\n");
		sb.append(" ーーーーーーーーーーーーー\n");

		for(int y = 0; y < boardSize; y++) {
			sb.append(y).append("|");
			for(int x = 0; x < boardSize; x++) {
				if( board[x][y] == null ) board[x][y] = EMPTY;
				sb.append(board[x][y]).append("|");
			}
			sb.append("\n");
			sb.append(" ーーーーーーーーーーーーー\n");
		}

		System.out.println(sb.toString());
	}

	private static boolean canPut(int inputX,int inputY,String myColor){
		boolean judge = false;
		
		int startY = inputY-1;
		int endY   = inputY+1;

		int startX = inputX-1;
		int endX   = inputX+1;
		
		String enemy = ( "BLACK".equals(myColor) ) ? WHITE : BLACK;
		
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {

				if( x < 8 && y < 8 && x > -1 && y > -1 ){
					if( board[x][y] == enemy ){
						String conX = "";
						String conY = "";

						switch( x - inputX ){
							case -1: conX = "minus"; break;
							case  0: conX = "none";  break;
							case  1: conX = "plus";  break;
						}

						switch( y - inputY ){
							case -1: conY = "minus"; break;
							case  0: conY = "none";  break;
							case  1: conY = "plus";  break;
						}

						boolean reverseJudge = reverse(x,y,conX,conY,myColor);
						if( judge != true ) judge = reverseJudge;
					}
				}
			}
		}
		
		return judge;
	}
	
	private static boolean reverse(int x,int y, String conX, String conY,String myColor) {
		boolean judge = false;

		List<Map<String,Integer>> reverseList = new ArrayList<Map<String,Integer>>();

		while(true) {			
			Map<String,Integer> reverseTarget = new LinkedHashMap<>();
			reverseTarget.put("x",x);
			reverseTarget.put("y",y);
			reverseList.add(reverseTarget);
			
			switch( conX ){
				case "minus": x--; break;
				case "plus" : x++; break;
			}
			
			switch( conY ){
				case "minus": y--; break;
				case "plus":  y++; break;
			}
			
			String myPieceMark = ( "WHITE".equals(myColor) ) ? WHITE : BLACK ;
			
			if( myPieceMark.equals( board[x][y] ) ){
				judge = true;

				for( Map<String,Integer> target : reverseList ) {
					int reverseX = target.get("x");
					int reverseY = target.get("y");
					
					board[reverseX][reverseY] = myPieceMark;
				}

			}else if( board[x][y] == EMPTY ){
				break;
			}
			
			if( x == 7 || y == 7 || x == 0 || y == 0 ) break;
		}

		return judge;
	}
}
