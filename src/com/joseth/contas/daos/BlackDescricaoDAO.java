package com.joseth.contas.daos;

import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.joseth.contas.beans.BlackDescricao;

//@Stateless(name="bdd")
public class BlackDescricaoDAO extends DAOBase 
{
	public BlackDescricaoDAO(){super(BlackDescricao.class);}
	
	private static Mapper mapper;
	
	@Transactional
    public BlackDescricao atualizarBlackDescricao(BlackDescricao t)
    {
	    if( mapper == null ) 
	        mapper = new DozerBeanMapper();
	    
	    BlackDescricao t2 = (BlackDescricao)pesquisarPorChavePrimaria(t.getId());
        if( t2 == null )
            t2 = new BlackDescricao();
        t2.setPadrao(t.getPadrao());
        salvar(t2);
        t2 = mapper.map(t2, BlackDescricao.class);
        return t2;
    }
}