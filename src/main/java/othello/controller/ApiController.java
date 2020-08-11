package othello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import othello.model.BoardModel;
import othello.repository.BoardRepository;

@Controller
@RequestMapping(path="/api/v1")
public class ApiController {
  @Autowired
  private BoardRepository boardRepository;

  @PostMapping(path="/board/put")
  public @ResponseBody String boardPut(@RequestParam String pieces, @RequestParam String player) {
    BoardModel board = new BoardModel();
    board.setPieces(pieces);
    board.setPlayer(player);
    boardRepository.save(board);
    return "Saved";
  }

  @GetMapping(path="/all")
  public @ResponseBody Iterable<BoardModel> getAllUsers() {
    return boardRepository.findAll();
  }
}