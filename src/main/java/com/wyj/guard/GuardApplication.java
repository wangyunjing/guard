package com.wyj.guard;

import com.wyj.guard.bootstrap.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class GuardApplication implements CommandLineRunner {

    public final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static void main(String[] args) {
        SpringApplication.run(GuardApplication.class, args);
    }

    @Autowired
    BootStrap bootStrap;

    @Override
    public void run(String... args) throws Exception {
        bootStrap.launch();
    }
}
