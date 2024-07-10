package healthplan.beneficios.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		// Configuração de autenticação (usuário em memória para exemplo)
		auth.inMemoryAuthentication().withUser("admin").password(passwordEncoder().encode("admin")).roles("USER");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		// Configuração de autorização
		http.csrf().disable();
		http.authorizeRequests().antMatchers("/api/beneficiarios/**").hasRole("USER")
				.antMatchers(HttpMethod.POST, "/api/beneficiarios/**").hasRole("USER")
				.antMatchers(HttpMethod.PUT, "/api/beneficiarios/**").hasRole("USER")
				.antMatchers(HttpMethod.DELETE, "/api/beneficiarios/**").hasRole("USER")
				.antMatchers("/home").authenticated()
				.antMatchers("/create").authenticated()
				.antMatchers("/update").authenticated()
				.antMatchers("/listarbeneficiarios").authenticated()
				.antMatchers("/listardocumentos").authenticated()
				.and()
				.formLogin()
				.loginProcessingUrl("/perform_login") 
				.defaultSuccessUrl("/home", true) // Redireciona para "/home" em caso de login bem-sucedido
				.failureUrl("/?error=true") // Adiciona o parâmetro error na URL em caso de falha no login
				.usernameParameter("username")
				.passwordParameter("password")
				.permitAll()
				.and()
				.logout()
				.logoutUrl("/sair")
				.logoutSuccessUrl("/?logout")
				.permitAll()
				.and()
				.httpBasic();

		// Adiciona logs para depuração
		http.exceptionHandling().accessDeniedHandler((request, response, accessDeniedException) -> {
			System.out.println("Acesso negado: " + accessDeniedException.getMessage());
		});

		http.exceptionHandling().authenticationEntryPoint((request, response, authException) -> {
			System.out.println("Falha na autenticação: " + authException.getMessage());
		});
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}