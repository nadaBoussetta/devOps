package devOps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SampleDataJpaApplication {

	public static void main(String[] args) {

		SpringApplication.run(SampleDataJpaApplication.class, args);
	}
}