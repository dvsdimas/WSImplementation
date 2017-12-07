package hello;

/**
 * Created by dmylnev on 05.06.2017.
 */

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {

        final SpringApplication app = new SpringApplication(Application.class);

        app.setBannerMode(Banner.Mode.OFF);

        app.run(args);
    }
}
