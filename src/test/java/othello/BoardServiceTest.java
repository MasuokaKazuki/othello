package othello;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import othello.model.BoardModel;
import othello.service.BoardService;

@SpringBootTest
public class BoardServiceTest {
	@Autowired
	private BoardService boardService;

	@Test
	public void canPut() {
		// 初期状態で黒が置ける場所のチェック
		boardService.reset();
		assertTrue(boardService.canPut(4,2));
		assertTrue(boardService.canPut(5,3));
		assertTrue(boardService.canPut(2,4));
		assertTrue(boardService.canPut(3,5));
		assertFalse(boardService.canPut(0,0));
		assertFalse(boardService.canPut(7,7));
	}
	
	@Test
	public void put() {
		// 初期状態から、黒が x=2,y=4 に駒を配置した結果が正しいかチェック
		boardService.reset();
		boardService.put(2,4);
		boardService.init();
		int[][] pieces = boardService.getPieces();
		assertEquals(pieces[3][3],0);
		assertEquals(pieces[2][4],0);
		assertEquals(pieces[3][4],0);
		assertEquals(pieces[4][3],1);
	}
	
	@Test
	public void passCheck() {
		// パスチェックが正しく動いている科の確認
		// 参考：https://bassy84.net/othello-rule2.html
		//「稀に序盤で双方打ち切れなくなるケースもある」の項。
		boardService.reset();
		int[][] pieces = new int[8][8];

		for (int i = 0; i < 8; i++) {
			for (int j = 0; j < 8; j++) {
				pieces[i][j] = -1;
			}
		}
		
		pieces[1][0] = 0;
		pieces[5][0] = 0;
		pieces[2][1] = 0;
		pieces[5][1] = 0;
		pieces[3][2] = 0;
		pieces[5][2] = 0;
		pieces[7][2] = 1;
		pieces[3][3] = 0;
		pieces[4][3] = 0;
		pieces[5][3] = 0;
		pieces[6][3] = 1;
		pieces[0][4] = 0;
		pieces[1][4] = 0;
		pieces[2][4] = 0;
		pieces[3][4] = 0;
		pieces[4][4] = 0;
		pieces[5][4] = 1;
		pieces[2][5] = 0;
		pieces[3][5] = 0;
		pieces[4][5] = 0;
		pieces[2][6] = 0;
		pieces[3][6] = 0;
		pieces[4][6] = 0;
		pieces[2][7] = 0;
		pieces[3][7] = 0;
		pieces[4][7] = 0;
		pieces[5][7] = 0;
		pieces[6][7] = 0;

		boardService.setPiecesAndSave(pieces);
		boardService.put(7,4);

		BoardModel boardModel = boardService.getBoardData();
		assertEquals(boardModel.getStatus(),"pass");
	}
	
}
