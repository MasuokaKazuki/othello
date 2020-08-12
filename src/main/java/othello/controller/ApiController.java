package othello.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import othello.service.BoardService;

@Controller
@RequestMapping(path="/api/v1")
public class ApiController {  
  @Autowired
  private BoardService boardService;

  @GetMapping(path="/board/reset")
  public @ResponseBody String boardReset() {
	  boardService.reset();
	  return "Saved";
  }
  
  @GetMapping(path="/board/put")
  public @ResponseBody boolean boardPut(@RequestParam("x")Integer x, @RequestParam("y")Integer y) {	  
	  boardService.put(x,y);
	  boardService.display();
	  return false;
  }

  @GetMapping(path="/board/display")
  public @ResponseBody boolean display() {	  
	  boardService.display();
	  return false;
  }
}