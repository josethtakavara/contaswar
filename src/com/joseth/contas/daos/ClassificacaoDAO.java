package com.joseth.contas.daos;

import java.util.List;
import java.util.Map;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.web.ContasRequestListener;

//@Stateless(name="cld")
public class ClassificacaoDAO extends DAOBase 
{
	public ClassificacaoDAO(){super(Classificacao.class);}
	
	private static Mapper mapper;
	
	public Classificacao atualizar(Classificacao c)
    {
        if( mapper == null ) 
            mapper = new DozerBeanMapper();
        
        Classificacao c2 = null;
        
        if( c.getId() != null )
            c2 = (Classificacao)pesquisarPorChavePrimaria(c.getId());
        
        if( c2 == null )
            c2 = new Classificacao();
        c2.setNome(c.getNome());
        salvar(c2);
        c2 = mapper.map(c2, Classificacao.class);
        return c2;
    }
	
	public List<Map> pesquisarTodosContagem()
	{
	    String jpaQL = "select new Map(c as classificacao, size(c.movimentos) as cnt) from Classificacao c " ;
        //jpaQL += " order by nome asc";

	    return ContasRequestListener.getEntityManager().createQuery(jpaQL).getResultList();
	}
}