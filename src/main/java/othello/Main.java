package othello;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

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
		try {
			init();
			display();
			
			Scanner sc = new Scanner(System.in);
			while( true ) {
				System.out.println( player + "のターン" );
				if( passCheck() == false ) {
					reverseList = new ArrayList<Map<String,Integer>>();

					int x = sc.nextInt();
					int y = sc.nextInt();
					
					if( x > boardSize-1 || y > boardSize-1 || x < 0 || y < 0 ) {
						System.out.println("数値が有効範囲内じゃないよ。0～7までの数値を入力するんだよ。");
						continue;
					}
				
					if( canPut(x,y,true) ) {
						board[x][y] = ( "BLACK".equals(player) ) ? BLACK : WHITE;
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
			result();

		} catch (ArrayIndexOutOfBoundsException e) {
			System.out.println(e);

			for(int y = 0; y < boardSize; y++) {
				for(int x = 0; x < boardSize; x++) {
					if( board[x][y] != EMPTY ) {
						System.out.println("board["+x+"]["+y+"] = " + board[x][y]);
					}
				}
			}
			
		}
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
				if( board[x][y] == EMPTY && canPut(x,y,false) ){
					passCnt = 0;
					return false;
				}
			}
		}
		passCnt++;
		return true;
	}

	private static boolean canPut(int inputX,int inputY,boolean needsReverse){
		boolean judge = false;
		
		if( board[inputX][inputY] != EMPTY ) return false;
		
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

						boolean reverseJudge = canReverse(x,y,conX,conY,needsReverse);
						if( judge != true ) judge = reverseJudge;
					}
				}
			}
		}
		
		return judge;
	}


	private static boolean canReverse(int x, int y, String conX, String conY, boolean needsReverse) {
		boolean judge = false;
		List<Map<String,Integer>> tmpList = new ArrayList<Map<String,Integer>>();
		
		String myPieceMark = ( "BLACK".equals(player) ) ? BLACK : WHITE;
		
		while(true) {
			if( needsReverse ){
				Map<String,Integer> reverseTarget = new LinkedHashMap<>();
				reverseTarget.put("x",x);
				reverseTarget.put("y",y);
				tmpList.add(reverseTarget);
			}

			switch( conX ){
				case "minus": x--; break;
				case "plus" : x++; break;
			}
			
			switch( conY ){
				case "minus": y--; break;
				case "plus":  y++; break;
			}

			if( x > boardSize-1 || y > boardSize-1 || x < 0 || y < 0 ) break;
						
			if( myPieceMark.equals( board[x][y] ) ){
				judge = true;

				if( needsReverse ){
					for( Map<String,Integer> target : tmpList ) {
						int reverseX = target.get("x");
						int reverseY = target.get("y");
						board[reverseX][reverseY] = ( "BLACK".equals(player) ) ? BLACK : WHITE;
					}
				}

				break;

			}else if( board[x][y] == EMPTY ){
				break;
			}
		}

		return judge;
	}

	private static void result(){
		int blackCnt = 0;
		int whiteCnt = 0;

		for(int y = 0; y < boardSize; y++) {
			for(int x = 0; x < boardSize; x++) {
				if( board[x][y] == BLACK ){ 
					blackCnt++;
				}else if( board[x][y] == WHITE ) {
					whiteCnt++;
				}
			}
		}

		System.out.println("BLACKの数：" + blackCnt );
		System.out.println("WHITEの数：" + whiteCnt );
		
		if( blackCnt > whiteCnt ) {
			System.out.println("BLACKの勝利");
		}else{
			System.out.println("WHITEの勝利");
		}
	}
}
