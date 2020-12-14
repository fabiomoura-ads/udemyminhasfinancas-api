package com.fmourabrasil.minhasfinancas.service;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import org.assertj.core.api.Assertions;
import org.hamcrest.collection.ArrayAsIterableMatcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.data.domain.Example;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.fmourabrasil.minhasfinancas.exception.RegraNegocioException;
import com.fmourabrasil.minhasfinancas.model.entity.Lancamento;
import com.fmourabrasil.minhasfinancas.model.entity.Usuario;
import com.fmourabrasil.minhasfinancas.model.enums.StatusLancamento;
import com.fmourabrasil.minhasfinancas.model.enums.TipoLancamento;
import com.fmourabrasil.minhasfinancas.model.repository.LancamentoRepository;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class LancamentoServiceTest {

	@SpyBean
	LancamentoService service;
	
	@MockBean
	LancamentoRepository repository;
	
	@Test
	public void deveSalvarUmLancamento() {
		
		//cenário
		Lancamento lancamentoASalvar = criaLancamento();
		Mockito.doNothing().when(service).validar(lancamentoASalvar);
		
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
				
		Mockito.when(repository.save(lancamentoASalvar)).thenReturn(lancamentoSalvo);
		
		//ação
		Lancamento lancamento = service.salvar(lancamentoASalvar);
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoASalvar);		
		Assertions.assertThat(lancamento.getStatus().equals(StatusLancamento.PENDENTE)).isTrue();
		
	}
	
	@Test
	public void deveLancarErroAoSalvarUmLancamentoInvalido() {
		//cenário
		Lancamento lancamento = criaLancamento();
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(Mockito.any(Lancamento.class));

		//ação		
		assertThrows(RegraNegocioException.class, () -> {
			service.salvar(lancamento);	
		});
		
		//verificação
		Mockito.verify(repository, Mockito.never()).save(lancamento);		
		
	}
	
	@Test
	public void deveAtualizarUmLancamento() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Lancamento lancamentoAtualizado = criaLancamento();
		lancamentoAtualizado.setId(1L);
		
		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoAtualizado);
		
		//ação
		Lancamento lancamento = service.salvar(lancamentoSalvo);
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		Assertions.assertThat(lancamentoSalvo.getId().equals(lancamento.getId())).isTrue();
				
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoSemID() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		//ação
		assertThrows(NullPointerException.class, () -> {
			service.atualizar(lancamentoSalvo);
		});
		
		//verificação
		Mockito.verify(repository, Mockito.never()).save(lancamentoSalvo);
				
	}
	
	@Test
	public void deveLancarErroAoTentarAtualizarUmLancamentoInvalido() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doThrow(RegraNegocioException.class).when(service).validar(Mockito.any(Lancamento.class));
		
		//ação
		assertThrows(RegraNegocioException.class, () -> {
			service.atualizar(lancamentoSalvo);
		});
		
		//verificação
		Mockito.verify(repository, Mockito.never()).save(lancamentoSalvo);
				
	}
	
	@Test
	public void deveDeletarUmLancamento() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
				
		//ação
		service.deletar(lancamentoSalvo);
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).delete(lancamentoSalvo);		
				
	}
	
	@Test
	public void deveLancarErroAoTentarDeletarUmLancamentoSemID() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
				
		//ação
		assertThrows(NullPointerException.class, () -> {
			service.deletar(lancamentoSalvo);
		});		
			
		//verificação
		Mockito.verify(repository, Mockito.never()).delete(lancamentoSalvo);		
				
	}

	@Test
	public void deveAtualizarStatusDeUmLancamento() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.doNothing().when(service).validar(lancamentoSalvo);
		
		Lancamento lancamentoAtualizado = criaLancamento();
		lancamentoAtualizado.setId(1L);
		lancamentoAtualizado.setStatus(StatusLancamento.EFETIVADO);

		Mockito.when(repository.save(lancamentoSalvo)).thenReturn(lancamentoAtualizado);
					
		//ação
		Lancamento lancamento = service.atualizarStatus(lancamentoSalvo, StatusLancamento.EFETIVADO);		
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).save(lancamentoSalvo);
		Assertions.assertThat(lancamento.getId().equals(lancamentoAtualizado.getId())).isTrue();
		Assertions.assertThat(lancamento.getStatus().equals(StatusLancamento.EFETIVADO)).isTrue();
				
	}
	
	@Test
	public void deveObterUmLancamentoPorID() {
		
		//cenário
		Lancamento lancamentoSalvo = criaLancamento();
		lancamentoSalvo.setId(1L);
		lancamentoSalvo.setStatus(StatusLancamento.PENDENTE);
		
		Mockito.when(repository.findById(1L)).thenReturn(Optional.of(lancamentoSalvo));
		
		//ação
		Optional<Lancamento> optLancamento = service.obterPorId(1l);
		
		//verificação
		Mockito.verify(repository, Mockito.times(1)).findById(1l);
		Assertions.assertThat(optLancamento.isPresent()).isTrue();
		Assertions.assertThat(optLancamento.get().getId().equals(lancamentoSalvo.getId())).isTrue();
				
	}
	
	@Test
	public void deveValidarUmLancamento() {
		
		//cenário
		Lancamento lancamento = new Lancamento();
		
		Throwable exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe uma Descrição válida.");	
				
		lancamento.setDescricao("descricao qualquer");
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");	
				
		lancamento.setMes(0);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(13);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Mês válido.");
		
		lancamento.setMes(10);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setMes(10);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(200);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Ano válido.");
		
		lancamento.setAno(2020);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");	
		
		lancamento.setUsuario(new Usuario());
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Usuário.");	
		
		lancamento.setUsuario(Usuario.builder().id(1L).build());
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");
		
		lancamento.setValor(BigDecimal.ZERO);
		
		exception = Assertions.catchThrowable(() -> service.validar(lancamento));
		Assertions.assertThat(exception).isInstanceOf(RegraNegocioException.class).hasMessage("Informe um Valor válido.");		
	}
	
	@Test
	public void deveObterSaldoDeUmUsuarioPorID() {
		
		//cenário	
		Long id = 1L;
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.RECEITA, StatusLancamento.EFETIVADO)).thenReturn(BigDecimal.valueOf(100));
		Mockito.when(repository.obterSaldoPorTipoLancamentoEUsuario(id, TipoLancamento.DESPESA, StatusLancamento.EFETIVADO)).thenReturn(BigDecimal.valueOf(100));
		
		//ação
		BigDecimal saldo = service.obterSaldoDoUsuario(id);
		
		System.out.println("SALDO::: " + saldo.doubleValue());
		
		//verificação
		Assertions.assertThat(saldo.equals(BigDecimal.valueOf(0))).isTrue();
				
	}	
	
	@Test
	public void deveBuscarLancamentos() {
		
		//cenário	
		Lancamento lancamento = criaLancamento();
		lancamento.setId(1L);
		
		List<Lancamento> lista = Arrays.asList(lancamento);
		Mockito.when(repository.findAll(Mockito.any(Example.class))).thenReturn(lista);
		
		//ação
		List<Lancamento> resultado = service.buscar(lancamento);
				
		//verificação
		Assertions.assertThat(resultado).isNotEmpty().hasSize(1);
		Assertions.assertThat(resultado.get(0).getId().equals(lancamento.getId())).isTrue();
				
	}
	
	public Lancamento criaLancamento() {
		return Lancamento.builder()
				.descricao("Lancamento teste")
				.ano(2020)
				.mes(1)
				.usuario(Usuario.builder().id(1L).build())
				.valor(BigDecimal.valueOf(100))
				.tipo(TipoLancamento.RECEITA)
				.build();
		
	}
}
