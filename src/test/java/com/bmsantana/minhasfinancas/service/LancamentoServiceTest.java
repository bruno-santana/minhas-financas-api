package com.bmsantana.minhasfinancas.service;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.bmsantana.minhasfinancas.exception.RegraNegocioException;
import com.bmsantana.minhasfinancas.model.entity.Lancamento;
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
		return Lancamento.builder()
							.ano(2020)
							.mes(01)
							.descricao("Lançamento Teste")
							.valor(BigDecimal.valueOf(10))
							.tipo(TipoLancamento.RECEITA)
							.status(StatusLancamento.PENDENTE)
							.dataCadastro(LocalDate.now())
							.build();
		
	}
	
	@Test
	public void salvar() {
		//Deve salvar um lancamento
		
		//Cenário
		Lancamento lancamentoAsalvar = criarLancamento();
		Mockito.doNothing().when(service).validar(lancamentoAsalvar);
		
		Lancamento lancamentoSalvo = criarLancamento();
		lancamentoSalvo.setId(1l);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		Mockito.when(repository.save(lancamentoAsalvar)).thenReturn(lancamentoSalvo);
		
		//Execução
		Lancamento lancamento = service.salvar(lancamentoAsalvar);
		
		//Verificação
		Assertions.assertThat(lancamento.getId()).isEqualTo(lancamentoSalvo.getId());
		Assertions.assertThat(lancamento.getStatus()).isEqualTo(StatusLancamento.PENDENTE);
	}
	
	@Test
	public void erroValidacaoSave() {
		//Não deve salvar quando houver erro na validação
		
		//Cenário
		Lancamento lancamentoAsalvar = criarLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(lancamentoAsalvar);
		
		//Execução e Verificação
		Assertions.catchThrowableOfType(() -> service.salvar(lancamentoAsalvar), RegraNegocioException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);
	}
	
	@Test
	public void atualizar() {
		//Deve atualizar um lançamento
		
		//Cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		lancamento.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamento);
		Mockito.when(repository.save(lancamento)).thenReturn(lancamento);
		
		//Execução
		service.atualizar(lancamento);
		
		//Verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamento);
	}
	
	@Test
	public void erroAtualizacao() {
		//Deve lançar erro ao tentar atualizar um lançamento que ainda não foi salvo
		
		//Cenário
		Lancamento lancamentoAsalvar = criarLancamento();
		
		//Execução e Verificação
		Assertions.catchThrowableOfType(() -> service.atualizar(lancamentoAsalvar), NullPointerException.class);
		Mockito.verify(repository, Mockito.never()).save(lancamentoAsalvar);
	}
	
	@Test
	public void deletar() {
		//Deve deletar um lançamento
		
		//Cenário
		Lancamento lancamento = criarLancamento();
		lancamento.setId(1l);
		
		//Execução
		service.deletar(lancamento);
		
		//Verificação
		Mockito.verify(repository).delete(lancamento);
	}
	
	@Test
	public void erroDeletar() {
		//Deve lançar erro ao tentar deletar um lançamento que ainda não foi salvo
		//Cenário
		Lancamento lancamento = criarLancamento();
				
		//Execução
		Assertions.catchThrowableOfType(() -> service.deletar(lancamento), NullPointerException.class);

		//Verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamento);
	}
}
