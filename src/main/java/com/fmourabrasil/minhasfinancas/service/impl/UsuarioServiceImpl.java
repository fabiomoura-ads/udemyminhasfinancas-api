package com.fmourabrasil.minhasfinancas.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fmourabrasil.minhasfinancas.exception.ErroAutenticaException;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.model.repository.LancamentoRepository;
import com.fmourabrasil.minhasfinancas.model.repository.UsuarioRepository;
import com.fmourabrasil.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;
		
	public UsuarioServiceImpl(UsuarioRepository repository, LancamentoRepository lancamentoRepository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		
		Optional<Usuario> optUsuario = repository.findByEmail(email);
		
		if ( !optUsuario.isPresent() ) {
			throw new ErroAutenticaException("Usuário não encontrado com o email informado.");
		}
		
		if ( ! (optUsuario.get().getSenha().equals(senha)) ) {
			throw new ErroAutenticaException("Senha inválida.");
		}
		
		return optUsuario.get();
	}

	@Override
	@Transactional
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		validarDadosObrigatorios(usuario);
		return repository.save(usuario);
	}

	private void validarDadosObrigatorios(Usuario usuario) {
		if ( usuario.getNome().isEmpty() ) {
			throw new RegraNegocioException("Nome do usuário é obrigatório e não foi informado.");
		}
		if ( usuario.getEmail().isEmpty() ) {
			throw new RegraNegocioException("Email do usuário é obrigatório e não foi informado.");
		}
		if ( usuario.getSenha().isEmpty() ) {
			throw new RegraNegocioException("Senha do usuário é obrigatório e não foi informado.");
		}	
		
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if ( existe ) {
			throw new RegraNegocioException("Já existe usuário cadastrado com o email informado.");
		}
		
	}

	@Override
	public Optional<Usuario> obterPorId(Long id) {
		 return repository.findById(id);			
	}

	@Override
	public List<Usuario> listar() {
		return repository.findAll();
	}


}
