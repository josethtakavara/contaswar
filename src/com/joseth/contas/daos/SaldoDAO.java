package com.joseth.contas.daos;

import javax.transaction.Transactional;

import com.joseth.contas.beans.Saldo;

//@Stateless(name="sd")
public class SaldoDAO extends DAOBase 
{
	public SaldoDAO(){super(Saldo.class);}

	@Transactional
    public void salvar(Saldo s)
    {
        Saldo s2 = (Saldo)pesquisarPorChavePrimaria(s.getId());
        if( s2 != null )
        {
            s2.setSaldoInicial(s.getSaldoInicial());
            s2.setSaldoFinal(s.getSaldoFinal());
            s = s2;
        }
        //Conta c = (Conta)cld.pesquisarPorChavePrimaria(c.getId());
        super.salvar(s);
    }
}