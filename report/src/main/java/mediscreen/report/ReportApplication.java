package mediscreen.report;

import com.fasterxml.jackson.databind.Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.openfeign.support.PageJacksonModule;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableFeignClients
public class ReportApplication {

    @Bean
    public Module pageJacksonModule() {
        return new PageJacksonModule(); // Required to deserialize JSON pages without Page constructor
    }

    public static void main(String[] args) {
        SpringApplication.run(ReportApplication.class, args);
    }
}
