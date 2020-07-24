package othello.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * サンプルRestControllerクラス
 */
@RestController("/")
public class HelloController {
    @RequestMapping
    public String hello() {
        return "Hello Again!";
    }
}
