package othello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import othello.data.PutResult;
import othello.data.ResetResult;
import othello.model.BoardModel;
import othello.service.BoardService;

@RestController
@CrossOrigin
@RequestMapping(path="/api/v1")
public class ApiController {  
  @Autowired
  private BoardService boardService;

  @GetMapping(path="/board/reset")
  public @ResponseBody ResetResult boardReset() {
	  return boardService.reset();
  }
  @GetMapping(path="/board/put")
  public @ResponseBody PutResult boardPut(@RequestParam("x")Integer x, @RequestParam("y")Integer y) {	  
	  return boardService.put(x,y);
  }
  @GetMapping(path="/board/")
  public @ResponseBody BoardModel display() {
	  return boardService.getBoardData();
  }
}