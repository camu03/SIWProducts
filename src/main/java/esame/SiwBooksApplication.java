package esame;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.thymeleaf.spring6.SpringTemplateEngine;
import org.thymeleaf.spring6.view.ThymeleafViewResolver;

@SpringBootApplication
public class SiwBooksApplication {

	public static void main(String[] args) {
		SpringApplication.run(SiwBooksApplication.class, args);
	}
	
	@Bean
	public ThymeleafViewResolver thymeleafViewResolver(SpringTemplateEngine engine) {
	  ThymeleafViewResolver vr = new ThymeleafViewResolver();
	  vr.setTemplateEngine(engine);
	  vr.setCharacterEncoding("UTF-8");
	  vr.setContentType("text/html; charset=UTF-8");
	  return vr;
	}

}
