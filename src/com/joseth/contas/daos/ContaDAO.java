package com.joseth.contas.daos;

import com.joseth.contas.beans.Conta;

//@Stateless(name="cd")
public class ContaDAO extends DAOBase 
{
	public ContaDAO(){super(Conta.class);}
}