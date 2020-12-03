package com.fmourabrasil.minhasfinancas.model.repository;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fmourabrasil.minhasfinancas.model.entity.Usuario;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
@AutoConfigureTestDatabase(replace = Replace.NONE)
public class UsuarioRepositoryTest {

	@Autowired
	UsuarioRepository repository;
	
	@Autowired
	TestEntityManager entityManager;
	
	@Test
	public void deveVerificarAExistenciaDeUmEmail() {
		
		//cenário
		Usuario usuario = Usuario.builder().nome("usuario").email("usuario@email.com").build();
		entityManager.persist(usuario);
				
		//ação
		boolean result = repository.existsByEmail(usuario.getEmail());
		
		//verificação
		Assertions.assertThat(result).isTrue();		
		
	}
	
	@Test
	public void deveRetornaFalsoQuandoNaoEncontrarUsuarioPorEmail() {
		
		//cenário		
						
		//ação
		boolean result = repository.existsByEmail("usuario@email.com");
		
		//verificação
		Assertions.assertThat(result).isFalse();		
		
	}
	
	@Test
	public void devePersistirUmUsuarioNaBaseDeDados() {
		//cenário
		
		Usuario usuario = criarUsuarioTeste();
		
		//ação		
		entityManager.persist(usuario);
		
		//verificação
		Assertions.assertThat(usuario.getId()).isNotNull();
				
	}
	
	@Test
	public void deveBuscarUmUsuarioPorEmail() {
		
		//cenário
		Usuario usuario = criarUsuarioTeste();
		entityManager.persist(usuario);
		
		//ação
		Optional<Usuario> optUsuario = repository.findByEmail(usuario.getEmail());
		
		//verificação
		Assertions.assertThat(optUsuario.isPresent()).isTrue();
	}
	
	@Test
	public void deveRetornaUsuarioVazioAoBuscarUsuarioPorEmailQuandoNaoExistirNaBase() {
		
		//cenário
		Usuario usuario = criarUsuarioTeste();
		entityManager.persist(usuario);
		
		//ação
		Optional<Usuario> optUsuario = repository.findByEmail(usuario.getEmail()+".br");
		
		//verificação
		Assertions.assertThat(optUsuario.isPresent()).isFalse();
	}
	
	private Usuario criarUsuarioTeste() {
		
		return Usuario.builder()
				.nome("usuario")
				.email("usuario@email.com")
				.senha("123456")
				.build();
				
	}	
}
