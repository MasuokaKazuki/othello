package othello.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import othello.data.Piece;
import othello.data.PutResult;
import othello.data.ResetResult;
import othello.model.BoardModel;
import othello.repository.BoardRepository;

@Service
public class BoardService {
	private int boardSize = 8;
	private BoardModel board = new BoardModel();
	private int[][] pieces = new int[boardSize][boardSize];
	private List<Piece> reversePieceList = new ArrayList<Piece>();

	private final int EMPTY = -1;
	private final int BLACK = 0;
	private final int WHITE = 1;

	private int player = BLACK;    // or WHITE

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

	public BoardModel getBoardData() {
		return boardRepository.findLatest();
	}	
	
	public ResetResult reset() {
		ResetResult result = new ResetResult();
		
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
		board.setPlayer(this.player);
		board.setStatus("open");
		
		boardRepository.save(board);
		
		result.setResult(true);
		result.setMessage("リセットしました。");
		
		return result;
	}

	public boolean passCheck(int player) {
		this.player = player;
		for(int y = 0; y < this.boardSize; y++) {
			for(int x = 0; x < this.boardSize; x++) {
				if( canPut(x,y) ){
					return false;
				}
			}
		}
		return true;
	}
	
	public PutResult put(int targetX,int targetY){
		this.init();

		PutResult result = new PutResult();
		result.setResult(false);

		this.reversePieceList = new ArrayList<Piece>();
		
		if(canPut(targetX,targetY,player)) {
			this.pieces[targetX][targetY] = this.player;
			this.reverse();
			String status = "open";
			
			int nextPlayer = ( BLACK == this.player ) ? WHITE : BLACK;

			if( passCheck(this.player) == true && passCheck(nextPlayer) == true ) {
				status = "close";
				result.setResult(true);
				result.setMessage("試合は終了しました。");

			}else if( passCheck(nextPlayer) == true ){
				nextPlayer = this.player;
				status = "pass";
				result.setResult(true);
				result.setMessage("置ける場所がないため、パスしました。");
			}
			
			board.setPieces(arrToJson(this.pieces));
			board.setPlayer(nextPlayer);
			board.setStatus(status);
			boardRepository.save(board);

			result.setResult(true);
			result.setMessage("駒を配置しました。");

		}else{
			result.setResult(false);
			result.setMessage("その位置に駒を置くことはできません");
		}
		
		return result;
	}

	public boolean canPut(int targetX,int targetY) {
		return this.canPut(targetX,targetY,this.player);
	}
	
	public boolean canPut(int targetX,int targetY,int player) {
		boolean judge = false;
		
		if( this.pieces[targetX][targetY] != EMPTY ) return false;
		
		int startY = targetX-1;
		int endY   = targetY+1;

		int startX = targetX-1;
		int endX   = targetY+1;
		
		int rivalPlayer = ( BLACK == player ) ? WHITE : BLACK;
		
		for(int y = startY; y <= endY; y++) {
			for(int x = startX; x <= endX; x++) {

				if( x < boardSize && y < boardSize && x > -1 && y > -1 ){
					if( this.pieces[x][y] == rivalPlayer ){
						String conX = "";
						String conY = "";
						
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

						if(StringUtils.isNotEmpty(conX) && StringUtils.isNotEmpty(conY)) {
							boolean reverseJudge = canReverse(x,y,conX,conY,player);
							if( judge != true ) judge = reverseJudge;							
						}
					}
				}
			}
		}
		
		return judge;
	}

	private boolean canReverse(int x, int y, String conX, String conY, int player) {
		boolean judge = false;		
		List<Piece> tmpPieceList = new ArrayList<Piece>();

		while(true) {
			Piece tmpPiece = new Piece();
			tmpPiece.setX(x);
			tmpPiece.setY(y);
			tmpPieceList.add(tmpPiece);

			switch( conX ){
				case "minus": x--; break;
				case "plus" : x++; break;
			}
			
			switch( conY ){
				case "minus": y--; break;
				case "plus":  y++; break;
			}

			if( x > boardSize-1 || y > boardSize-1 || x < 0 || y < 0 ) break;
			
			if( this.pieces[x][y] == player ){
				judge = true;
				this.reversePieceList.addAll(tmpPieceList);
				break;

			}else if( this.pieces[x][y] == EMPTY ){
				break;
			}
		}

		return judge;
	}

	private void reverse(){
		for( Piece piece : this.reversePieceList ) {
			int reverseX = piece.getX();
			int reverseY = piece.getY();
			this.pieces[reverseX][reverseY] = this.player;
		}
	}
	
	public void display() {
		this.init();
		
		System.out.println("プレイヤー:"+this.player);
		StringBuilder sb = new StringBuilder();

		sb.append(" | 0| 1| 2| 3| 4| 5| 6| 7|\n");
		sb.append(" ーーーーーーーーーーーーー\n");

		for(int y = 0; y < boardSize; y++) {
			sb.append(y).append("|");
			for(int x = 0; x < boardSize; x++) {
				sb.append(this.pieces[x][y]).append("|");
			}
			sb.append("\n");
			sb.append(" ーーーーーーーーーーーーー\n");
		}

		System.out.println(sb.toString());
	}
	
	public int[][] getPieces() {
		return this.pieces;
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
