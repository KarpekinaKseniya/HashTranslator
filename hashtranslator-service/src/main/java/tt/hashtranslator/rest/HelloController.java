package tt.hashtranslator.rest;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tt.hashtranslator.service.HelloService;

@Slf4j
@RestController
@RequestMapping("/api/hello")
public class HelloController {
    private final HelloService helloService;

    @Autowired
    public HelloController(final HelloService helloService) {
        this.helloService = helloService;
    }

    @GetMapping
    public String printGreeting(@RequestParam(name = "name", required = false, defaultValue = "World")final String name) {
        log.info("greeting for {}", name);
        return helloService.makeGreetingMessageFor(name);
    }
}
