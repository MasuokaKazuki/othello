package othello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import othello.model.BoardModel;
import othello.repository.BoardRepository;

@Service
public class BoardService {
	private int boardSize = 8;
	private BoardModel board = new BoardModel();
	private int[][] pieces = new int[boardSize][boardSize];
	private String player = "BLACK";    // or WHITE

	private final int EMPTY = -1;
	private final int BLACK = 0;
	private final int WHITE = 1;

	@Autowired
	public BoardRepository boardRepository;

	public void init() {
		this.board = boardRepository.findLatest();
		this.player = board.getPlayer();

		String tmpPieces = board.getPieces();
		ObjectMapper mapper = new ObjectMapper();

		try {
			this.pieces = mapper.readValue(tmpPieces, new TypeReference<>() {});
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
	}
	
	public BoardModel reset() {
		BoardModel board = new BoardModel();

		this.pieces = new int[boardSize][boardSize];
		
		for (int i = 0; i < this.boardSize; i++) {
			for (int j = 0; j < this.boardSize; j++) {
				this.pieces[i][j] = EMPTY;
			}
		}

		int halfSize = this.boardSize / 2;
		
		this.pieces[ halfSize - 1 ][ halfSize - 1 ] = BLACK;
		this.pieces[ halfSize - 1 ][ halfSize     ] = WHITE;
		this.pieces[ halfSize     ][ halfSize - 1 ] = WHITE;
		this.pieces[ halfSize     ][ halfSize     ] = BLACK;
		
		board.setPieces(arrToJson(this.pieces));
		board.setPlayer("BLACK");
		board.setPlayStyle("HUMAN");
		
		boardRepository.save(board);
		
		return board;
	}

	public boolean passCheck() {
		init();
		for(int y = 3; y < this.boardSize; y++) {
			for(int x = 0; x < this.boardSize; x++) {
				System.out.println("x:"+x+"/y:"+y);
				if( canPut(x,y) ){
					System.out.println("↑おけます");
				}
			}
		}
		return true;
	}
	
	
	public boolean canPut(int targetX,int targetY) {
		boolean judge = false;
		
		if( this.pieces[targetX][targetY] != EMPTY ) return false;
		
		int startY = targetX-1;
		int endY   = targetY+1;

		int startX = targetX-1;
		int endX   = targetY+1;
		
		int rivalMark = ( "BLACK".equals(this.player) ) ? WHITE : BLACK;
		
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {

				if( x < boardSize && y < boardSize && x > -1 && y > -1 ){
					if( this.pieces[x][y] == rivalMark ){
						String conX = "";
						String conY = "";

System.out.println(x - targetX);
						
						switch( x - targetX ){
							case -1: conX = "minus"; break;
							case  0: conX = "none";  break;
							case  1: conX = "plus";  break;
						}

						switch( y - targetY ){
							case -1: conY = "minus"; break;
							case  0: conY = "none";  break;
							case  1: conY = "plus";  break;
						}

						if("none".equals(conX) && "none".equals(conY)) {
							continue;
						}else {
							boolean reverseJudge = canReverse(x,y,conX,conY);
							if( judge != true ) judge = reverseJudge;							
						}
					}
				}
			}
		}
		
		return judge;
	}

	private boolean canReverse(int x, int y, String conX, String conY) {
		boolean judge = false;
		int playerMark = ( "BLACK".equals(this.player) ) ? BLACK : WHITE;
		System.out.println("conX:"+conX);
		System.out.println("conY:"+conY);

		while(true) {
			switch( conX ){
				case "minus": x--; break;
				case "plus" : x++; break;
			}
			
			switch( conY ){
				case "minus": y--; break;
				case "plus":  y++; break;
			}

			if( x > boardSize-1 || y > boardSize-1 || x < 0 || y < 0 ) break;
			
			if( this.pieces[x][y] == playerMark ){
				judge = true;
				break;

			}else if( this.pieces[x][y] == EMPTY ){
				break;
			}
		}

		return judge;
	}
	
	private String arrToJson(int[][] arr) {
		ObjectMapper mapper = new ObjectMapper();
		String json = null;
		try {
			json = mapper.writeValueAsString(arr);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return json;
	}

}
