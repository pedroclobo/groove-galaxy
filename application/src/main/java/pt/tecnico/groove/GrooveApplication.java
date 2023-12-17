package pt.tecnico.groove;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import java.time.LocalDateTime;
import java.util.List;

import pt.tecnico.groove.domain.User;
import pt.tecnico.groove.domain.Song;

import pt.tecnico.groove.repository.UserRepository;
import pt.tecnico.groove.repository.SongRepository;

import pt.tecnico.groove.demo.DemoUtils;

@SpringBootApplication
public class GrooveApplication  extends SpringBootServletInitializer implements InitializingBean {

    @Autowired
    private DemoUtils demoUtils;

    public static void main(String[] args) {
        SpringApplication.run(GrooveApplication.class, args);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        demoUtils.resetDemoInfo();
    }

}
