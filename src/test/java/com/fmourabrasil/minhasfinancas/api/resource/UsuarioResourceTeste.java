package com.fmourabrasil.minhasfinancas.api.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fmourabrasil.minhasfinancas.api.dto.UsuarioDTO;
import com.fmourabrasil.minhasfinancas.exception.ErroAutenticaException;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.service.LancamentoService;
import com.fmourabrasil.minhasfinancas.service.UsuarioService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = UsuarioResource.class)
@AutoConfigureMockMvc
public class UsuarioResourceTeste {

	static final String API = "/api/usuarios";
	
	@MockBean
	UsuarioService service;
	
	@MockBean
	LancamentoService lancamentoService;
	
	@Autowired
	MockMvc mvc;

	@Test
	public void deveAutenticarUmUsuario() throws Exception{
		
		String email = "usuario@email.com";
		String senha = "123456";
		
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().nome("usuario").email(email).senha(senha).id(1L).build();
				
		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API.concat("/autenticar"))
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.content(json);
					
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
			.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
			.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
		
	}
	
	@Test
	public void deveLancarErroAoTentarAutenticarUmUsuario() throws Exception{
		
		String email = "usuario@email.com";
		String senha = "123456";
						
		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();		
		String json = new ObjectMapper().writeValueAsString(dto);

		Mockito.doThrow(ErroAutenticaException.class).when(service).autenticar(email, senha);
		
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API.concat("/autenticar"))
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.content(json);
					
		
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isBadRequest());		
	}	
	
	@Test
	public void deveCriarUmUsuario() throws Exception{
		
		String email = "usuario@email.com";
		String senha = "123456";
		
		UsuarioDTO dto = UsuarioDTO.builder().nome("usuarioaa").email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1L).email(email).senha(senha).build();
			
		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		System.out.println("IDD>" + usuario.getId());
		
		String json = new ObjectMapper().writeValueAsString(dto);
		
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
												.post(API)
												.accept(MediaType.APPLICATION_JSON)
												.contentType(MediaType.APPLICATION_JSON)
												.content(json);
							
		mvc.perform(request)
			.andExpect(MockMvcResultMatchers.status().isCreated())
			.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()));
		
	}	
	
	
	
}
