package com.fmourabrasil.minhasfinancas;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.fmourabrasil.minhasfinancas.model.entity.Lancamento;

public class TesteReduce {

	public static void main(String[] args) {
		
		Lancamento l1 = Lancamento.builder()
				.valor(new BigDecimal(100))
				.build();
		
		Lancamento l2 = Lancamento.builder()
				.valor(new BigDecimal(50))
				.build();		
		
		Lancamento l3 = Lancamento.builder()
				.valor(new BigDecimal(10))
				.build();		
		
				
		List<BigDecimal> list = new ArrayList<>();
		//list.add(l1);
		//list.add(l2);
		list.add(BigDecimal.valueOf(0.2));
		list.add(BigDecimal.valueOf(5.5));

	
		BigDecimal res = list
				.stream()
				.reduce(BigDecimal.ZERO, BigDecimal::add );
		
		System.out.println(res);
//		List<Integer> lista = Arrays.asList(1,2,3,4,5);		
//		Integer res = lista.stream()
//						.reduce(0, ( subtotal, elemento ) -> subtotal + elemento );
//		
//		System.out.println(res);
//		
//		BigDecimal valor1 = new BigDecimal(0);
//		BigDecimal valor2 = new BigDecimal(1);
//		System.out.println(valor1.add(valor2));
		
		
		
	}
}
