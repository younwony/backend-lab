package dev.wony.backendlab.board.index;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class IndexController {

    private static final String WELCOME_MESSAGE = "Hi, Wony World";

    @GetMapping("/")
    @ResponseBody
    public String index() {
        return WELCOME_MESSAGE;
    }
}
