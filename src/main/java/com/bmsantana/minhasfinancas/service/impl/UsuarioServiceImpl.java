package com.bmsantana.minhasfinancas.service.impl;

import java.util.Optional;

import com.bmsantana.minhasfinancas.exception.ErroAutenticacao;
import com.bmsantana.minhasfinancas.exception.RegraNegocioException;
import com.bmsantana.minhasfinancas.model.entity.Usuario;
import com.bmsantana.minhasfinancas.model.repository.UsuarioRepository;
import com.bmsantana.minhasfinancas.service.UsuarioService;

public class UsuarioServiceImpl implements UsuarioService{
	
	private UsuarioRepository repository;	

	public UsuarioServiceImpl(UsuarioRepository repository) {
		super();
		this.repository = repository;
	}

	@Override
	public Usuario autenticar(String email, String senha) {
		Optional<Usuario> usuario = repository.findByEmail(email);
		
		if(!usuario.isPresent()) {
			throw new ErroAutenticacao("Usuário não localizado para o email informado!");
		}
		
		if(!usuario.get().getSenha().equals(senha)) {
			throw new ErroAutenticacao("Senha inválida!");
		}
		return usuario.get();
	}

	@Override
	public Usuario salvarUsuario(Usuario usuario) {
		validarEmail(usuario.getEmail());
		return repository.save(usuario);
	}

	@Override
	public void validarEmail(String email) {
		boolean existe = repository.existsByEmail(email);
		if(existe) {
			throw new RegraNegocioException("Já existe um usuário cadastrado com este email!");
		}
		
	}

}
