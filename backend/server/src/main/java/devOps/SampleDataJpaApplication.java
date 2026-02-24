package devOps;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class SampleDataJpaApplication {

	public static void main(String[] args) {
		String rawPassword = "password123";
		String hashed = new BCryptPasswordEncoder().encode(rawPassword);
		System.out.println("Hash pour password123 : " + hashed);

		SpringApplication.run(SampleDataJpaApplication.class, args);
	}
}