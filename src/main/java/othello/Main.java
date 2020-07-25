package othello;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Main {
	static int boardSize = 8;
	static String[][] board = new String[boardSize][boardSize];
	static String player = "BLACK";
	static int passCnt = 0;

	static List<Map<String,Integer>> reverseList = new ArrayList<Map<String,Integer>>();

	static final String EMPTY = "　";
	static final String BLACK = "●";
	static final String WHITE = "○";

	public static void main(String[] args) {
		init();
		display();
		
		Scanner sc = new Scanner(System.in);
		while( true ) {
			System.out.println( player + "のターン" );
			if( passCheck() == false ) {
				reverseList = new ArrayList<Map<String,Integer>>();

				int x = sc.nextInt();
				int y = sc.nextInt();	    
			
				if( canPut(x,y) ) {
					board[x][y] = ( "BLACK".equals(player) ) ? BLACK : WHITE;
					reverse();
					display();
					player = ( "BLACK".equals(player) ) ? "WHITE" : "BLACK";
				}else {
					System.out.println("そこは置けないよ");
					display();
				}
			}else{
				if( passCnt == 2 ) break;
				System.out.println( "置ける場所がないので、パスします。" );
				player = ( "BLACK".equals(player) ) ? "WHITE" : "BLACK";
			}
		}
	    sc.close();
		
	    System.out.println("試合終了");
		
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
	
	private static boolean passCheck() {
		for(int y = 0; y < boardSize; y++) {
			for(int x = 0; x < boardSize; x++) {
				if( board[x][y] == EMPTY && canPut(x,y) ){
					passCnt = 0;
					return false;
				}
			}
		}
		passCnt++;
		return true;
	}

	private static boolean canPut(int inputX,int inputY){
		boolean judge = false;
		
		int startY = inputY-1;
		int endY   = inputY+1;

		int startX = inputX-1;
		int endX   = inputX+1;
		
		String enemyPlayer = ( "BLACK".equals(player) ) ? WHITE : BLACK;
		
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {

				if( x < boardSize && y < boardSize && x > -1 && y > -1 ){
					if( board[x][y] == enemyPlayer ){
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

						boolean reverseJudge = canReverse(x,y,conX,conY);
						if( judge != true ) judge = reverseJudge;
					}
				}
			}
		}
		
		return judge;
	}


	private static boolean canReverse(int x,int y, String conX, String conY) {
		boolean judge = false;
		List<Map<String,Integer>> tmpList = new ArrayList<Map<String,Integer>>();
		
		while(true) {
			Map<String,Integer> reverseTarget = new LinkedHashMap<>();
			reverseTarget.put("x",x);
			reverseTarget.put("y",y);
			tmpList.add(reverseTarget);

			switch( conX ){
				case "minus": x--; break;
				case "plus" : x++; break;
			}
			
			switch( conY ){
				case "minus": y--; break;
				case "plus":  y++; break;
			}

			if( x == boardSize-1 || y == boardSize-1 || x < 1 || y < 1 ) break;
			
			String myPieceMark = ( "BLACK".equals(player) ) ? BLACK : WHITE;
			
			if( board[x][y]!=null && myPieceMark.equals( board[x][y] ) ){
				reverseList = Stream.concat(reverseList.stream(), tmpList.stream()).collect(Collectors.toList());
				//debug();
				judge = true;

			}else if( board[x][y] == EMPTY ){
				break;
			}
		}

		return judge;
	}

	
	private static void reverse(){
		for( Map<String,Integer> target : reverseList ) {
			int reverseX = target.get("x");
			int reverseY = target.get("y");

			//System.out.println("x:"+reverseX+",y:"+reverseY);
			
			board[reverseX][reverseY] = ( "BLACK".equals(player) ) ? BLACK : WHITE;
		}
	}
	
	private static void debug(){
		System.out.println("====debug start====");
		for( Map<String,Integer> reverseTarget : reverseList ) {
			for (String key : reverseTarget.keySet()) {
				System.out.println(key + ":" + reverseTarget.get(key));
			}
	    }
		System.out.println("====debug end====");
	}
}
