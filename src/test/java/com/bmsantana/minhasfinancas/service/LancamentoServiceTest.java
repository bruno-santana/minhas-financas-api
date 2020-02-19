package com.bmsantana.minhasfinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bmsantana.minhasfinancas.exception.RegraNegocioException;
import com.bmsantana.minhasfinancas.model.entity.Lancamento;
import com.bmsantana.minhasfinancas.model.entity.Usuario;
import com.bmsantana.minhasfinancas.model.enums.StatusLancamento;
import com.bmsantana.minhasfinancas.model.enums.TipoLancamento;
import com.bmsantana.minhasfinancas.model.repository.LancamentoRepository;
import com.bmsantana.minhasfinancas.service.impl.LancamentoServiceImpl;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoServiceImpl service;

	@MockBean
	LancamentoRepository repository;

	private Lancamento criarLancamento() {
		return Lancamento.builder().ano(2020).mes(01).descricao("Lançamento Teste").valor(BigDecimal.valueOf(10))
				.tipo(TipoLancamento.RECEITA).status(StatusLancamento.PENDENTE).dataCadastro(LocalDate.now()).build();

	}

	@Test
	public void salvar() {
		// Deve salvar um lancamento

		// Cenário
		Lancamento lancamentoAsalvar = criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoAsalvar);

		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoAsalvar)).thenReturn(lancamentoSalvo);

		// Execução
		Lancamento lancamento = service.salvar(lancamentoAsalvar);

		// Verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}

	@Test
	public void erroValidacaoSave() {
		// Não deve salvar quando houver erro na validação

		// Cenário
		Lancamento lancamentoAsalvar = criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoAsalvar);

		// Execução e Verificação
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoAsalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);
	}

	@Test
	public void atualizar() {
		// Deve atualizar um lançamento

		// Cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		Mockito.doNothing().when(service).validar(lancamento);
		Mockito.when(repository.save(lancamento)).thenReturn(lancamento);

		// Execução
		service.atualizar(lancamento);

		// Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamento);
	}

	@Test
	public void erroAtualizacao() {
		// Deve lançar erro ao tentar atualizar um lançamento que ainda não foi salvo

		// Cenário
		Lancamento lancamentoAsalvar = criarLancamento();

		// Execução e Verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoAsalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);
	}

	@Test
	public void deletar() {
		// Deve deletar um lançamento

		// Cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);

		// Execução
		service.deletar(lancamento);

		// Verificação
		Mockito.verify(repository).delete(lancamento);
	}

	@Test
	public void erroDeletar() {
		// Deve lançar erro ao tentar deletar um lançamento que ainda não foi salvo
		// Cenário
		Lancamento lancamento = criarLancamento();

		// Execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		// Verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}

	@Test
	public void buscar() {
		// Deve filtrar um lançamento
		// Cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);

		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);

		// Execução
		List<Lancamento> resultado = service.buscar(lancamento);

		// Verificação
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1).contains(lancamento);
	}

	@Test
	public void atualizarStatus() {
		// Deve atualizar o Status de um lançamento
		// Cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);

		StatusLancamento novoStatus = StatusLancamento.EFETIVADO;
		Mockito.doReturn(lancamento).when(service).atualizar(lancamento);

		// Execução
		service.atualizarStatus(lancamento, novoStatus);

		// Verificação
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(novoStatus);
		Mockito.verify(service).atualizar(lancamento);
	}

	@Test
	public void buscarPorID() {
		// Deve retornar um lançamento quando buscado por ID
		// Cenário
		Long id = 1l;

		Lancamento lancamento = criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.of(lancamento));

		// Execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificação
		Assertions.assertThat(resultado.isPresent()).isTrue();
	}

	@Test
	public void erroBuscarPorID() {
		// Deve retornar vazio quando o lançamento não existir
		// Cenário
		Long id = 1l;

		Lancamento lancamento = criarLancamento();
		lancamento.setId(id);

		Mockito.when(repository.findById(id)).thenReturn(Optional.empty());

		// Execução
		Optional<Lancamento> resultado = service.obterPorId(id);

		// Verificação
		Assertions.assertThat(resultado.isPresent()).isFalse();
	}

	@Test
	public void validarLancamento() {
		/*Deve verificar os erros de validação. Alterando os parametros para fazer
		 todas as validações */		
		// Cenário
		Lancamento lancamento = new Lancamento();

		// descrição vazia
		Throwable erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma descrição válida!");

		lancamento.setDescricao("");

		// descrição em branco
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe uma descrição válida!");

		lancamento.setDescricao("teste");

		// mes vazio
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(0);

		// mes menor que 1
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(13);

		// mes maior que 12
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido!");

		lancamento.setMes(1);

		// ano vazio
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(20);

		// ano menor que 4
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido!");

		lancamento.setAno(2020);

		// usuario vazio
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido!");

		lancamento.setUsuario(new Usuario());

		// usuario com id vazio
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário válido!");

		lancamento.getUsuario().setId(1l);

		// valor vazio
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.ZERO);

		// valor menor que 1
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido!");

		lancamento.setValor(BigDecimal.valueOf(1));

		// tipo vazio
		erro = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(erro).isInstanceOf(RegraNegocioException.class)
				.hasMessage("Informe um Tipo de lançamento!");
	}
}
