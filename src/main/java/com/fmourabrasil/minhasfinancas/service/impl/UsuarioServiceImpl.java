package com.fmourabrasil.minhasfinancas.service.impl;

import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fmourabrasil.minhasfinancas.exception.ErroAutenticaException;
import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.model.repository.UsuarioRepository;
import com.fmourabrasil.minhasfinancas.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService{

	private UsuarioRepository repository;
		
	public UsuarioServiceImpl(UsuarioRepository repository) {
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
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if ( existe ) {
			throw new RegraNegocioException("Já existe usuário cadastrado com o email informado.");
		}
		
	}

}
