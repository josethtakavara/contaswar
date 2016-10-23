package com.joseth.contas.daos;

import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.joseth.contas.beans.BlackString;

//@Stateless(name="bsd")
public class BlackStringDAO extends DAOBase 
{
	public BlackStringDAO(){super(BlackString.class);}
	
	private static Mapper mapper;
	
	@Transactional
    public BlackString atualizarBlackString(BlackString t)
    {
	    if( mapper == null ) 
	        mapper = new DozerBeanMapper();
	    
	    BlackString t2 = (BlackString)pesquisarPorChavePrimaria(t.getId());
        if( t2 == null )
            t2 = new BlackString();
        t2.setPadrao(t.getPadrao());
        salvar(t2);
        t2 = mapper.map(t2, BlackString.class);
        return t2;
    }
}