package com.fmourabrasil.minhasfinancas.api.resource;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fmourabrasil.minhasfinancas.api.dto.UsuarioDTO;
import com.fmourabrasil.minhasfinancas.exception.ErroAutenticaException;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Lancamento;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.model.enums.StatusLancamento;
import com.fmourabrasil.minhasfinancas.service.LancamentoService;
import com.fmourabrasil.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioResource {

	private final UsuarioService service;
	private final LancamentoService lancamentoService;
	
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
			
			Usuario usuarioSalvo = service.salvarUsuario(usuario);
						
			return new ResponseEntity(usuarioSalvo, HttpStatus.CREATED);
						
		}catch(RegraNegocioException e ) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
			
		}
		
	}
	
	@GetMapping("{id}/saldo")
	public ResponseEntity obterSaldo( @PathVariable("id") Long idUsuario) {
		
		Optional<Usuario> optUsuario = service.obterPorId(idUsuario);
		
		if ( !optUsuario.isPresent() ) {
			return ResponseEntity.badRequest().body("Usuário não localizado pelo ID informado.");
		}
				
		BigDecimal saldo = lancamentoService.obterSaldoDoUsuario(optUsuario.get().getId());		
		
		return ResponseEntity.ok(saldo);
	}
	
	@GetMapping
	public ResponseEntity listar() {
		return ResponseEntity.ok(service.listar());		
	}
	
	public Usuario retornaUsuario(UsuarioDTO dto) {
		return Usuario.builder()
				.nome(dto.getNome())
				.email(dto.getEmail())
				.senha(dto.getSenha())
				.build();		
	}
	
}
