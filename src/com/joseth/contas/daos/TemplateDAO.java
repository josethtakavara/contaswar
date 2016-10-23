package com.joseth.contas.daos;

import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.joseth.contas.beans.Template;

//@Stateless(name="td")
public class TemplateDAO extends DAOBase 
{
	public TemplateDAO(){super(Template.class);}
	
	private static Mapper mapper;
	
	@Transactional
    public Template atualizarTemplate(Template t)
    {
	    if( mapper == null ) 
	        mapper = new DozerBeanMapper();
	    
        Template t2 = (Template)pesquisarPorChavePrimaria(t.getId());
        if( t2 == null )
            t2 = new Template();
        t2.setNome(t.getNome());
        t2.setPadrao(t.getPadrao());
        salvar(t2);
        t2 = mapper.map(t2, Template.class);
        return t2;
    }
}