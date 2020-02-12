package com.bmsantana.minhasfinancas.model.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bmsantana.minhasfinancas.model.entity.Lancamento;

public interface LancamentoRepository extends JpaRepository<Lancamento, Long>{

}
