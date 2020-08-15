package othello.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import othello.data.GameResult;
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

	/**
	 * 盤面の状況を取得し、各変数にセットする。
	 */	
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

	/**
	 * 盤面の状況を最新一件取得する。
	 * @return 現在の盤の状況（プレイヤー,どの位置に駒が置かれているなど）
	 */	
	public BoardModel getBoardData() {
		return boardRepository.findLatest();
	}	
	
	/**
	 * 盤面の状態を初期化する。
	 * @return 実行結果
	 */	
	public ResetResult reset() {
		ResetResult result = new ResetResult();
		
		this.pieces = new int[this.boardSize][this.boardSize];
		
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
		
		this.board.setId(null);	
		this.board.setPieces(arrToJson(this.pieces));
		this.board.setPlayer(BLACK);
		this.board.setStatus("open");
		
		boardRepository.save(this.board);
		
		result.setResult(true);
		result.setMessage("リセットしました。");
		
		return result;
	}

	/**
	 * 盤面の状況から、パスするかどうか判定する。
	 * @param player 対象のプレイヤー
	 * @return true or false
	 */	
	public boolean passCheck(int player) {
		for(int y = 0; y < this.boardSize; y++) {
			for(int x = 0; x < this.boardSize; x++) {
				if( canPut(x,y,player) ){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 盤面の指定の位置に駒を置き、データを更新する。
	 * @param targetX 置きたい場所のx軸での位置
	 * @param targetY 置きたい場所のy軸での位置
	 * @return 処理結果
	 */	
	public PutResult put(int targetX,int targetY){
		return put(targetX, targetY, player);
	}
	
	/**
	 * 盤面の指定の位置に駒を置き、データを更新する。
	 * @param targetX 置きたい場所のx軸での位置
	 * @param targetY 置きたい場所のy軸での位置
	 * @param player 対象のプレイヤー
	 * @return 処理結果
	 */	
	public PutResult put(int targetX,int targetY, int player){
		this.init();
		PutResult result = new PutResult();
		result.setResult(false);

		this.reversePieceList = new ArrayList<Piece>();
		
		if(canPut(targetX,targetY,this.player)) {
			this.pieces[targetX][targetY] = this.player;
			this.reverse();
			String status = "open";
			
			int nextPlayer = ( BLACK == this.player ) ? WHITE : BLACK;

			if( passCheck(this.player) == true && passCheck(nextPlayer) == true ) {
				status = "close";
				result.setResult(true);

			}else if( passCheck(nextPlayer) == true ){
				nextPlayer = this.player;
				status = "pass";
				result.setResult(true);
			}
			this.board.setId(null);	
			this.board.setPieces(arrToJson(this.pieces));
			this.board.setPlayer(nextPlayer);
			this.board.setStatus(status);
			boardRepository.save(this.board);

			result.setResult(true);
			result.setMessage("駒を配置しました。");

		}else{
			result.setResult(false);
			result.setMessage("その位置に駒を置くことはできません");
		}
		
		return result;
	}

	/**
	 * 盤面の指定の位置に駒が置けるかどうかを判定する。
	 * @param targetX 置きたい場所のx軸での位置
	 * @param targetY 置きたい場所のy軸での位置
	 * @return true or false
	 */	
	public boolean canPut(int targetX,int targetY) {
		return this.canPut(targetX,targetY,this.player);
	}

	/**
	 * 盤面の指定の位置に駒が置けるかどうかを判定する。
	 * @param targetX 置きたい場所のx軸での位置
	 * @param targetY 置きたい場所のy軸での位置
	 * @param player 対象のプレイヤー
	 * @return true or false
	 */	
	public boolean canPut(int targetX,int targetY,int player) {
		boolean judge = false;
		
		if( this.pieces[targetX][targetY] != EMPTY ) return false;
		
		int startY = targetY-1;
		int endY   = targetY+1;

		int startX = targetX-1;
		int endX   = targetX+1;
		
		int rivalPlayer = ( player == BLACK ) ? WHITE : BLACK;
		
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

	/**
	 * 盤面の指定の位置に駒を置いて、敵の駒をひっくり返すことができるか判定する。
	 * ※またひっくり返すことができると判定された場合、ひっくり返る対象の位置を変数にセットする。
	 * 
	 * @param targetX 置きたい場所のx軸での位置
	 * @param targetY 置きたい場所のy軸での位置
	 * @param conX 調査する範囲を指定（x軸）
	 * @param conY 調査する範囲を指定（y軸）
	 * @param player 対象のプレイヤー
	 * @return true or false
	 */	
	private boolean canReverse(int x, int y, String conX, String conY, int player) {
		boolean judge = false;		
		List<Piece> tmpPieceList = new ArrayList<Piece>();
		
		if(StringUtils.isEmpty(conX) || StringUtils.isEmpty(conY)) {
			return false;
		}

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

	/**
	 * canReverseでセットしたひっくり返す対象の駒をひっくり返す処理。
	 */	
	private void reverse(){
		for( Piece piece : this.reversePieceList ) {
			int reverseX = piece.getX();
			int reverseY = piece.getY();
			this.pieces[reverseX][reverseY] = this.player;
		}
	}

	/**
	 * ゲームの結果を返却する。（現在の駒数も返却する）
	 * @return ゲーム結果 and 現在の駒数
	 */	
	public GameResult getResult() {
		this.init();

		GameResult result = new GameResult();
		
		int blackCnt = 0;
		int whiteCnt = 0;

		for(int y = 0; y < boardSize; y++) {
			for(int x = 0; x < boardSize; x++) {
				if( this.pieces[x][y] == BLACK ){ 
					blackCnt++;
				}else if( this.pieces[x][y] == WHITE ) {
					whiteCnt++;
				}
			}
		}
		
		String message = "";
		if("close".equals(this.board.getStatus())){
			if( blackCnt > whiteCnt ) {
				message = "黒の勝利です";
			}else{
				message = "白の勝利です";
			}
		}
		
		result.setBlackCnt(blackCnt);
		result.setWhiteCnt(whiteCnt);
		result.setMessage(message);

		return result;
	}

	/**
	 * 盤面の駒をセットし、保存する。
	 * ※正直、テストコード用のものなので、ここにあるのは微妙だと感じている。
	 */	
	public void setPiecesAndSave(int[][] pieces) {
		this.board.setPieces(arrToJson(pieces));
		boardRepository.save(this.board);
	}
	
	/**
	 * int二次元配列をstringのJSON形式に変換
	 * @return stringのJSON形式の値
	 */	
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
