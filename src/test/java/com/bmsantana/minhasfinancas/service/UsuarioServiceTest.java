package com.bmsantana.minhasfinancas.service;


import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bmsantana.minhasfinancas.exception.ErroAutenticacao;
import com.bmsantana.minhasfinancas.exception.RegraNegocioException;
import com.bmsantana.minhasfinancas.model.entity.Usuario;
import com.bmsantana.minhasfinancas.model.repository.UsuarioRepository;
import com.bmsantana.minhasfinancas.service.impl.UsuarioServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class UsuarioServiceTest {
	
	@SpyBean
	UsuarioServiceImpl service;
	
	@MockBean
	UsuarioRepository repository;
		
	
	@Test
	public void sucessoAutenticacaoUsuario() {
		//Deve autenticar com sucesso um usuário
		
		//cenario
		String email = "email@email.com";
		String senha = "senha";
		
		Usuario usuario = Usuario.builder().email(email).senha(senha).id(1L).build();
		Mockito.when(repository.findByEmail(email)).thenReturn(Optional.of(usuario));
		
		//ação
		Usuario result = service.autenticar(email, senha);
		
		//verificacao
		Assertions.assertThat(result).isNotNull();
		
	}
	
	
	@Test
	public void erroUsuarioAutenticacao() {
		//Deve lançar erro quando não encontrar usuario cadastrado com o email informado
		
		//cenario
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.empty());
		
		//ação - verificação
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "senha"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Usuário não localizado para o email informado!");
	}
	
	
	@Test
	public void erroSenhaAutenticacao() {
		//Deve lançar erro quando a senha não conferir com a senha informada
		
		//cenario
		String senha = "senha";
		Usuario usuario = Usuario.builder().email("email@email.com").senha(senha).build();
		Mockito.when(repository.findByEmail(Mockito.anyString())).thenReturn(Optional.of(usuario));
		
		//ação - verificação
		Throwable exception = Assertions.catchThrowable(() -> service.autenticar("email@email.com", "outraSenha"));
		Assertions.assertThat(exception).isInstanceOf(ErroAutenticacao.class).hasMessage("Senha inválida!");
	}
	
	
	@Test
	public void salvarUsuario() {
		//Deve salvar um usuario de forma correta
		
		//cenario
		Mockito.doNothing().when(service).validarEmail(Mockito.anyString());
		Usuario usuario = Usuario.builder().id(1L).nome("nome").email("email@email.com").senha("senha").build();
		Mockito.when(repository.save(Mockito.any(Usuario.class))).thenReturn(usuario);
		
		//ação
		Usuario usuarioSalvo = service.salvarUsuario(new Usuario());
		
		//verificacao
		Assertions.assertThat(usuarioSalvo).isNotNull();
		Assertions.assertThat(usuarioSalvo.getId()).isEqualTo(1L);
		Assertions.assertThat(usuarioSalvo.getNome()).isEqualTo("nome");
		Assertions.assertThat(usuarioSalvo.getEmail()).isEqualTo("email@email.com");
		Assertions.assertThat(usuarioSalvo.getSenha()).isEqualTo("senha");
	}
	
	@Test
	public void erroEmailsalvarUsuario() {
		//Não deve salvar um usuario com email já cadastrado na base de dados
		
		//cenario
		String email = "email@email.com";
		Usuario usuario = Usuario.builder().email(email).build();
			Mockito.doThrow(RegraNegocioException.class).when(service).validarEmail(email);	
	
		//ação
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {	
		service.salvarUsuario(usuario);
		});
		
		//verificacao
		Mockito.verify(repository, Mockito.never()).save(usuario);			
	}
	
	@Test
	public void validarEmail() {
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(false);
		
		//ação
		service.validarEmail("email@email.com");
		
		//verificação
		
	}
	
	@Test
	public void erroValidacaoEmail() {
		//deve lancar erro ao validar email, quando este não existir o email cadastrado
		
		//cenario
		Mockito.when(repository.existsByEmail(Mockito.anyString())).thenReturn(true);
		
		//ação-verificação
		org.junit.jupiter.api.Assertions.assertThrows(RegraNegocioException.class, () -> {
			service.validarEmail("email@email.com");
			
		});
		Throwable exception = Assertions.catchThrowable(() -> service.validarEmail("email@email.com"));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Já existe um usuário cadastrado com este email!");
	}
	
	
}
