#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package};

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Slf4j
public class ParentApplication {

    public static void main(String[] args) {
        log.info("Starting app with args: {}", args);
        SpringApplication.run(ParentApplication.class, args);
    }

}
