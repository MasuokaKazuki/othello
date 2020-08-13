package othello.data;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GameResult {
	String message;
	int blackCnt;
	int whiteCnt;
}
