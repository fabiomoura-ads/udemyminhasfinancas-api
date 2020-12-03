package com.fmourabrasil.minhasfinancas.api.resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fmourabrasil.minhasfinancas.api.dto.UsuarioDTO;
import com.fmourabrasil.minhasfinancas.exception.ErroAutenticaException;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.service.UsuarioService;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioResource {

	private UsuarioService service;
	
	public UsuarioResource(UsuarioService service) {
		this.service = service;
	}
	
	@PostMapping("/autenticar")
	public ResponseEntity autenticar( @RequestBody UsuarioDTO dto) {
		
		Usuario usuario = this.retornaUsuario(dto);
		
		try {
			
			Usuario usuarioAutenticado = service.autenticar(usuario.getEmail(), usuario.getSenha());
			
			return ResponseEntity.ok(usuarioAutenticado);
			
		}catch(ErroAutenticaException e ) {
			
			return ResponseEntity.badRequest().body(e.getMessage());			
		}
		
	}
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody UsuarioDTO dto){
	
		Usuario usuario = this.retornaUsuario(dto);
	
		try {
			
			service.salvarUsuario(usuario);
			
			return new ResponseEntity(usuario, HttpStatus.CREATED);
						
		}catch(RegraNegocioException e ) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
		
	}
	
	public Usuario retornaUsuario(UsuarioDTO dto) {
		return Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();		
	}
	
}
