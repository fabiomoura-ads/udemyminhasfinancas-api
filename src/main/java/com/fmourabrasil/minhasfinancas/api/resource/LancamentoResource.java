package com.fmourabrasil.minhasfinancas.api.resource;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fmourabrasil.minhasfinancas.api.dto.LancamentoDTO;
import com.fmourabrasil.minhasfinancas.api.dto.UsuarioDTO;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Lancamento;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.model.enums.StatusLancamento;
import com.fmourabrasil.minhasfinancas.model.enums.TipoLancamento;
import com.fmourabrasil.minhasfinancas.service.LancamentoService;
import com.fmourabrasil.minhasfinancas.service.UsuarioService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("api/lancamentos")
@RequiredArgsConstructor
public class LancamentoResource {

	private final UsuarioService usuarioService;
	private final LancamentoService service;
	
	@GetMapping
	public ResponseEntity buscar(
			@RequestParam(value = "descricao", required = false) String descricao,
			@RequestParam(value = "ano", required = false) Integer ano,
			@RequestParam(value = "mes", required = false) Integer mes,
			@RequestParam(value = "tipo", required = false) String tipo,
			@RequestParam("usuario") Long idUsuario) {
	
		Usuario usuario = this.retornaUsuarioPorId(idUsuario);
		
		Lancamento lancamentoFiltro = Lancamento.builder()
				.descricao(descricao)
				.ano(ano)
				.mes(mes)
				.usuario(usuario)
				.tipo(tipo != null ? TipoLancamento.valueOf(tipo) : null)
				.build();
						
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		
		return ResponseEntity.ok(lancamentos);
		
	}	
	
	@GetMapping("{id}")
	public ResponseEntity buscarPorId( @PathVariable("id") Long id, @RequestParam("usuario") Long idUsuario) {
		
		Usuario usuario = this.retornaUsuarioPorId(idUsuario);
		
		Lancamento lancamentoFiltro = Lancamento.builder()
				.id(id)
				.usuario(usuario)
				.build();
		
		List<Lancamento> lancamentos = service.buscar(lancamentoFiltro);
		
		return ResponseEntity.ok(lancamentos);
	}	
	
	@PostMapping
	public ResponseEntity salvar( @RequestBody LancamentoDTO dto) {
		
		try {
			
			Lancamento lancamento = this.retornaLancamento(dto);
			
			service.salvar(lancamento);
			
			return new ResponseEntity(lancamento, HttpStatus.CREATED);
			
		}catch(RegraNegocioException e) {
			
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	
	@PutMapping("{id}")
	public ResponseEntity atualizar( @PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
							
		return service.obterPorId(id).map( entidade -> {
			
			try {
				
				Lancamento lancamento = this.retornaLancamento(dto);
				
				lancamento.setId(entidade.getId());
				
				service.atualizar(lancamento);
				
				return ResponseEntity.ok(lancamento);				
				
			}catch(RegraNegocioException e ) {
				
				return ResponseEntity.badRequest().body(e.getMessage());
				
			}
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado para o ID informado.", HttpStatus.BAD_REQUEST) );			

	};
	
	@PutMapping("{id}/atualiza-status")
	public ResponseEntity atualizaStatus(@PathVariable("id") Long id, @RequestBody LancamentoDTO dto) {
		
		return service.obterPorId(id).map(entidade -> {
			
			try {

				StatusLancamento statusSelecionado = StatusLancamento.valueOf(dto.getStatus());
				
				if ( statusSelecionado == null ) {
					return ResponseEntity.badRequest().body("Status inválido");
				}
				
				entidade.setStatus(statusSelecionado);
				
				service.atualizar(entidade);
				
				return ResponseEntity.ok(entidade);
						
			} catch(RegraNegocioException e){
				
				return ResponseEntity.badRequest().body(e.getMessage()); 
			}
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado para o ID informado.", HttpStatus.BAD_REQUEST) );
		
	}
	
	@DeleteMapping("{id}")
	public ResponseEntity deletar(@PathVariable("id") Long id) {
	
		return service.obterPorId(id).map( entity -> {
			
			try {
				
				service.deletar(entity);
				
				return new ResponseEntity( HttpStatus.NO_CONTENT);
				
			}catch(RegraNegocioException e ) {
				System.out.println("Caiu no trow...");
				return ResponseEntity.badRequest().body(e.getMessage());
			}
			
		}).orElseGet(() -> new ResponseEntity("Lançamento não encontrado para o ID informado.", HttpStatus.BAD_REQUEST));
		
	}
		
	public Usuario retornaUsuarioPorId(Long idUsuario) {
		Optional<Usuario> optUsuario = usuarioService.obterPorId(idUsuario);
		if ( !optUsuario.isPresent() ) {
			throw new RegraNegocioException("Usuário não localizado pelo ID informado.");
		}
		return optUsuario.get();
	}
	public Lancamento retornaLancamento(LancamentoDTO dto) {
		
		Usuario usuario = this.usuarioService
				.obterPorId(dto.getUsuario())
				.orElseThrow(() -> new RegraNegocioException("Usuário não encontrado pelo ID informado"));
		
		return Lancamento.builder()
				.id(dto.getId())
				.descricao(dto.getDescricao())
				.mes(dto.getMes())
				.ano(dto.getAno())
				.tipo(TipoLancamento.valueOf(dto.getTipo()))
				.status(dto.getStatus() != null ? StatusLancamento.valueOf(dto.getStatus()) : null)
				.ano(dto.getAno())
				.valor(dto.getValor())
				.dataCadastro(dto.getDataCadastro())
				.usuario(usuario)
				.build();
		
		
	}
}
