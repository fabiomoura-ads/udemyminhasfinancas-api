package com.fmourabrasil.minhasfinancas.service;

import java.util.List;

import com.fmourabrasil.minhasfinancas.model.entity.Lancamento;
import com.fmourabrasil.minhasfinancas.model.enums.StatusLancamento;

public interface LancamentoService {

	Lancamento salvar(Lancamento lancamento);
	
	Lancamento atualizar(Lancamento lancamento);
	
	void deletar(Lancamento lancamento);
	
	List<Lancamento> buscar(Lancamento lancamentoBusca);
	
	Lancamento atualizarStatus(Lancamento lancamento, StatusLancamento status);
	
	void validar(Lancamento lancamento);
	
}
