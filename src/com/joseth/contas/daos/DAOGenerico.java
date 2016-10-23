package com.joseth.contas.daos;

import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.joseth.contas.beans.BlackDescricao;
import com.joseth.contas.beans.BlackString;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Saldo;
import com.joseth.contas.beans.Template;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.web.ContasRequestListener;

//@Stateless(name="dg")
public class DAOGenerico
{
//	@PersistenceContext  (unitName = "primary")
//    protected EntityManager em = ContasRequestListener.getEntityManager();
	
	@Transactional
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Classificacao> getClassificacoes()
	{
		Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.Classificacao");
		return q.getResultList();
	}
	@Transactional
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Usuario> getUsuarios()
	{
		Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.Usuario");
		List<Usuario> in = q.getResultList();
		List<Usuario> out = new ArrayList<Usuario>();
		Mapper mapper = new DozerBeanMapper();

		for( Usuario u: in)
		{
			u = mapper.map(u, Usuario.class);
			out.add(u);
		}
		return out;
	}
	@Transactional
//	@TransactionAttribute(TransactionAttributeType.REQUIRED)
	public List<Conta> getContas()
	{
		Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.Conta");
		List<Conta> in = q.getResultList();
		List<Conta> out = new ArrayList<Conta>();
		Mapper mapper = new DozerBeanMapper();

		for( Conta c: in)
		{
			c = mapper.map(c, Conta.class);
			out.add(c);
		}
		return out;
	}
	
    @Transactional
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Template> getTemplates()
    {
        Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.Template");
        List<Template> in = q.getResultList();
        List<Template> out = new ArrayList<Template>();
        Mapper mapper = new DozerBeanMapper();
    
        for( Template t: in)
        {
            t = mapper.map(t, Template.class);
            out.add(t);
        }
        return out;
    }
    
    @Transactional
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BlackDescricao> getBlackDescricoes()
    {
        Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.BlackDescricao");
        List<BlackDescricao> in = q.getResultList();
        List<BlackDescricao> out = new ArrayList<BlackDescricao>();
        Mapper mapper = new DozerBeanMapper();
    
        for( BlackDescricao t: in)
        {
            t = mapper.map(t, BlackDescricao.class);
            out.add(t);
        }
        return out;
    }
    
    @Transactional
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<BlackString> getBlackStrings()
    {
        Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.BlackString");
        List<BlackString> in = q.getResultList();
        List<BlackString> out = new ArrayList<BlackString>();
        Mapper mapper = new DozerBeanMapper();
    
        for( BlackString t: in)
        {
            t = mapper.map(t, BlackString.class);
            out.add(t);
        }
        return out;
    }
	
	@Transactional
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Saldo> getSaldos(String contaId)
    {   
	    Integer id=-1;
	    try
	    {
	        NumberFormat nf = NumberFormat.getInstance();
	        id = nf.parse(contaId).intValue();
	    }
	    catch( ParseException nfe)
	    {
	        throw new RuntimeException(nfe);
	    }
	    
	     
        Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.Saldo where conta.id = ?");
        
        q.setParameter(1,id);
        List<Saldo> in = q.getResultList();
        List<Saldo> out = new ArrayList<Saldo>();
        Mapper mapper = new DozerBeanMapper();

        for( Saldo s: in)
        {
            s = mapper.map(s, Saldo.class);
            out.add(s);
        }
        return out;
    }
	
	@Transactional
//    @TransactionAttribute(TransactionAttributeType.REQUIRED)
    public List<Map<String,String>> getAnoMesSoma(String contaId, String ano)
    {   
        Integer contaInt=-1;
        Integer anoInt=-1;
        try
        {
            NumberFormat nf = NumberFormat.getInstance();
            contaInt = nf.parse(contaId).intValue();
            anoInt = nf.parse(ano).intValue();
        }
        catch( ParseException nfe)
        {
            throw new RuntimeException(nfe);
        }
        
         
        Query q = ContasRequestListener.getEntityManager().createQuery(
            "select new Map(year(m.data) as ano, month(m.data) as mes, round(sum(m.valor),2) as soma) "+
            "from com.joseth.contas.beans.Movimento m "+
            "where conta.id = :cid and m.movimento is null "+
            "      and year(m.data) = :ano "+
            "group by year(m.data), month(m.data)"
        );
        
        q.setParameter("cid",contaInt);
        q.setParameter("ano",anoInt);
//        List<Saldo> in = q.getResultList();
//        List<Saldo> out = new ArrayList<Saldo>();
//        Mapper mapper = new DozerBeanMapper();
//
//        for( Saldo s: in)
//        {
//            s = mapper.map(s, Saldo.class);
//            out.add(s);
//        }
        return q.getResultList();
    }
	
//    @Transactional
//    public void update(Classificacao c)
//	{
//		Query q = ContasRequestListener.getEntityManager().createQuery("from com.joseth.contas.beans.Classificacao c where c.id = ?");
//		q.setParameter(1,c.getId());
//		Classificacao c1 = (Classificacao)q.getSingleResult();
//		c1.setNome(c.getNome());
//		ContasRequestListener.getEntityManager().persist(c1);
//	}
    
    
	private List<String> meses = new ArrayList(Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12"));
	public List<String> getMeses() 
	{   
	   return meses;
    }
	
	public List<Integer> getAnos() 
	{   
	        Query query = ContasRequestListener.getEntityManager().createQuery(
                "select distinct year(data) " +
                "from Movimento " +
                "where conta is not null " +
                "order by year(data)"                
            );
	        return (List<Integer>) query.getResultList();
    }
	
	public List<String> getMesesAnos() 
	{   
	        Query query = ContasRequestListener.getEntityManager().createQuery(
                "select distinct concat(lpad(month(data),2,'0'),'/',year(data)) " +
                "from Movimento " +
                "where conta is not null " +
                "order by concat(year(data),lpad(month(data),2,'0'))"                
            );
	        return (List<String>) query.getResultList();
    }
	
	public List<String> getMesesAnos(Conta c) 
	{   
	        Query query = ContasRequestListener.getEntityManager().createQuery(
                "select distinct concat(lpad(month(data),2,'0'),'/',year(data)) " +
                "from Movimento " +
                "where conta = :conta " +
                "order by year(data),month(data)"                
            );
	        query.setParameter("conta",c);
	        return (List<String>) query.getResultList();
    }
}
