package com.fmourabrasil.minhasfinancas.service;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fmourabrasil.minhasfinancas.exception.ErroAutenticaException;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.model.repository.UsuarioRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {

	@SpyBean
	UsuarioService service;
	
	@MockBean
	UsuarioRepository repository;
	
	@Test
	public void naoDeveSalvarUsuarioComEmailJaCadastrado() {
		
		//cenário
		String email = "usuario@email.com";	
		Usuario usuario = Usuario.builder().email(email).build();	
		Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);
		
		//ação
		assertThrows(RegraNegocioException.class, () -> {
			service.salvarUsuario(usuario);				
		});
				
		//verificaçao
		Mockito.verify(repository, Mockito.never()).save(usuario);
		
	}
	
	@Test
	public void deveSalvarUmUsuario() {
		//cenário
		Usuario usuario = Usuario.builder()
				.id(1L)
				.nome("usaurio")
				.email("usuario@email.com")
				.senha("123456")
				.build();
		
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
			
		//ação
		service.salvarUsuario(new Usuario());
				
		//verificação
		Assertions.assertThat(usuario.getId()).isNotNull();		
		
	}
	
	@Test
	public void deveValidarEmailUnicoNaBase() {
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
	
		//ação
		service.validarEmail("usuario@email.com");				
		
	}
	
	@Test
	public void deveLancarErroAoValidarEmailQuandoExistirEmailCadastrado() {
		
		//cenário
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação
		assertThrows(RegraNegocioException.class, () -> {
			service.validarEmail("usuario@email.com");				
		});
				
	}	
	
	@Test
	public void deveAutenticarUsuarioComSucesso(){

		String email = "usuario@email.com";
		String senha = "123456";
		
		//cenário
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		System.out.println("USUARIO::: " + usuario);
		
		//ação
		Usuario result = service.autenticar(email, senha);
		
		//verificação
		Assertions.assertThat(result).isNotNull();
					
	}
	
	@Test
	public void deveLancarErroQuandoNaoAutenticarUsuarioPorEmail() {
		
		//cenário	
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
//		ação
//		assertThrows(ErroAutenticaException.class, () -> {
//			service.autenticar("usuario@email.com", "123456");				
//		});							

		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("usuario@email.com", "123456") );
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticaException.class).hasMessage("Usuário não encontrado com o email informado.");
		
	}
	
	@Test
	public void deveLancarErroQuandoSenhaNaoValidar() {
		
		//cenário			
		String email = "usuario@email.com";
		String senha = "123456";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação
		//assertThrows(ErroAutenticaException.class, () -> {
		//	service.autenticar(email, senha + 1);				
		//});	

		//ação
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar(email, senha + 1) );
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticaException.class).hasMessage("Senha inválida.");
	}	
}
