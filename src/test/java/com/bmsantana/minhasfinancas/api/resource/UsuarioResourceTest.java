package com.bmsantana.minhasfinancas.api.resource;

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
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.bmsantana.minhasfinancas.api.dto.UsuarioDTO;
import com.bmsantana.minhasfinancas.exception.ErroAutenticacao;
import com.bmsantana.minhasfinancas.exception.RegraNegocioException;
import com.bmsantana.minhasfinancas.model.entity.Usuario;
import com.bmsantana.minhasfinancas.service.LancamentoService;
import com.bmsantana.minhasfinancas.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@AutoConfigureMockMvc
@WebMvcTest(controllers = UsuarioResource.class)
public class UsuarioResourceTest {

	static final String API = "/api/usuarios";
	static final MediaType JSON = MediaType.APPLICATION_JSON;

	@Autowired
	MockMvc mvc;

	@MockBean
	UsuarioService service;

	@MockBean
	LancamentoService lancamentoService;

	@Test
	public void autenticar() throws Exception {
		// Deve autenticar um usuário
		// Cenário
		String email = "email@email.com";
		String senha = "senha";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

		Mockito.when(service.autenticar(email, senha)).thenReturn(usuario);
		String jsonUsuario = new ObjectMapper().writeValueAsString(dto);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(jsonUsuario);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}

	@Test
	public void badRequestAutenticar() throws Exception {
		// Deve retornar uma BadRequest ao tentar autenticar um usuário
		// Cenário
		String email = "email@email.com";
		String senha = "senha";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Mockito.when(service.autenticar(email, senha)).thenThrow(ErroAutenticacao.class);

		String jsonUsuario = new ObjectMapper().writeValueAsString(dto);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API.concat("/autenticar")).accept(JSON)
				.contentType(JSON).content(jsonUsuario);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}
	
	@Test
	public void salvar() throws Exception {
		// Deve Salvar um novo usuário
		// Cenário
		String email = "email@email.com";
		String senha = "senha";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();
		Usuario usuario = Usuario.builder().id(1l).email(email).senha(senha).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenReturn(usuario);
		String jsonUsuario = new ObjectMapper().writeValueAsString(dto);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON)
				.contentType(JSON).content(jsonUsuario);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isCreated())
				.andExpect(MockMvcResultMatchers.jsonPath("id").value(usuario.getId()))
				.andExpect(MockMvcResultMatchers.jsonPath("nome").value(usuario.getNome()))
				.andExpect(MockMvcResultMatchers.jsonPath("email").value(usuario.getEmail()));
	}

	@Test
	public void badRequestSalvar() throws Exception {
		// Deve retornar uma BadRequest ao tentar salvar um usuário inválido
		// Cenário
		String email = "email@email.com";
		String senha = "senha";

		UsuarioDTO dto = UsuarioDTO.builder().email(email).senha(senha).build();

		Mockito.when(service.salvarUsuario(Mockito.any(Usuario.class))).thenThrow(RegraNegocioException.class);
		String jsonUsuario = new ObjectMapper().writeValueAsString(dto);

		// Execução e verificação
		MockHttpServletRequestBuilder request = MockMvcRequestBuilders.post(API).accept(JSON)
				.contentType(JSON).content(jsonUsuario);

		mvc.perform(request).andExpect(MockMvcResultMatchers.status().isBadRequest());
	}

}
