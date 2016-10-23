package com.joseth.contas.daos;

import com.joseth.contas.beans.Usuario;

//@Stateless(name="ud")
public class UsuarioDAO extends DAOBase 
{
	public UsuarioDAO(){super(Usuario.class);}
}