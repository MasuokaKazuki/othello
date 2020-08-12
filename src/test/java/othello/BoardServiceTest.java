package othello;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import othello.service.BoardService;

@SpringBootTest
public class BoardServiceTest {
	@Autowired
	private BoardService boardService;

    @Test
    public void canPut() {
    	// 初期状態で黒が置ける場所のチェック
    	boardService.reset();
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
}
