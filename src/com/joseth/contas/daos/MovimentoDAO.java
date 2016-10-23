package com.joseth.contas.daos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.transaction.Transactional;

import org.dozer.DozerBeanMapper;
import org.dozer.Mapper;

import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.web.ContasRequestListener;

//@Stateless(name="mvd")
public class MovimentoDAO extends DAOBase 
{
//    @EJB(beanName="cld")
    ClassificacaoDAO cld = new ClassificacaoDAO();
    
//    @EJB(beanName="cd")
    ContaDAO cd = new ContaDAO();

    
//	static Mapper mapper = new DozerBeanMapper(); 
	public MovimentoDAO(){super(Movimento.class);}
	
	private List<String> meses = new ArrayList<String>(Arrays.asList("01","02","03","04","05","06","07","08","09","10","11","12"));
	public List<String> getMeses() 
	{   
	   return meses;
    }
	
	@Transactional
	public int apagarTodos()
	{
		Query query = ContasRequestListener.getEntityManager().createQuery("delete from Movimento");
	    int r = query.executeUpdate();
	    query = ContasRequestListener.getEntityManager().createNativeQuery("delete from Movimento_Classificacao");
	    query.executeUpdate();
	    return r;
	}
	
	public List<String> getAnos() 
	{   
	        Query query = ContasRequestListener.getEntityManager().createQuery(
                "select distinct year(data) " +
                "from Movimento " +
                "where conta is not null " +
                "order by year(data)"                
            );
	        return (List<String>) query.getResultList();
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

	public List<Movimento> pesquisar(String filtroMesDe, String filtroMesAte, 
			Usuario usuario,Conta conta,
			List classificacoes, 
			String descricao, String comentario, Double valor, 
			Boolean pessoal,
			Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos)
	{
		return pesquisarInterno(true, null, filtroMesDe, filtroMesAte, 
				 usuario,conta,
				 classificacoes, 
				 descricao,  comentario,  valor,
				 pessoal,
				 semComentario,  semClassificacoes,sinalMais,sinalMenos);
	}
	
	public List<Movimento> pesquisarTudo(Boolean subMovimentos,
			Movimento pai,
			String filtroMesDe, String filtroMesAte, 
			Usuario usuario, Conta conta,
			Collection<Classificacao> classificacoes, 
			String descricao, String comentario,  Double valor,
			Boolean pessoal,
			Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos)
	{
		return pesquisarInterno(!subMovimentos, pai, filtroMesDe, filtroMesAte, 
				 usuario,conta,
				 classificacoes, 
				 descricao,  comentario,  valor,
				 pessoal,
				 semComentario,  semClassificacoes,sinalMais,sinalMenos);
	}
	
	public List<Movimento> pesquisar(Movimento pai, String filtroMesDe, String filtroMesAte, 
			Usuario usuario,Conta conta,
			List classificacoes, 
			String descricao, String comentario,  Double valor,
			Boolean pessoal,
			Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos)
	{
		return pesquisarInterno(true, pai, filtroMesDe, filtroMesAte, 
				 usuario,conta,
				 classificacoes, 
				 descricao,  comentario, valor,
				 pessoal,
				 semComentario,  semClassificacoes,sinalMais,sinalMenos);
	}
	
	private static Classificacao ad;
    private static Classificacao pe;
    private static Classificacao sc;
    private static Classificacao sq;
    private static Classificacao ac;
    private static Classificacao iv;
    private static Classificacao emp;
    private void initCache()
    {
    	if( ad == null )
    		ad = (Classificacao)cld.pesquisar("nome","A Dividir").iterator().next();
    	if( pe == null )
    		pe = (Classificacao)cld.pesquisar("nome","Pessoal").iterator().next();
    	if( sc == null )
    		sc = (Classificacao)cld.pesquisar("nome","Sem classificação").iterator().next();
    	if( sq == null )
    		sq = (Classificacao)cld.pesquisar("nome","Saque").iterator().next();
    	if( ac == null )
    		ac = (Classificacao)cld.pesquisar("nome","Acertos").iterator().next();
    	if( iv == null )
    		iv = (Classificacao)cld.pesquisar("nome","Investimento").iterator().next();
    	if( emp == null )
    		emp = (Classificacao)cld.pesquisar("nome","Empréstimos").iterator().next();
    }
	
	private List<Movimento> pesquisarInterno(boolean soBase, Movimento pai, String filtroMesDe, String filtroMesAte, 
									Usuario usuario, Conta conta,
									Collection<Classificacao> classificacoes, 
									String descricao, String comentario,  Double valor,
									Boolean pessoal,
									Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos)
	{
		initCache();
		List<Movimento> ret = new ArrayList<Movimento>();
		List<Classificacao> classList = new ArrayList<Classificacao>(classificacoes);

/*
 * Se pai == null e soBase
 *   m.* = dados
 * Se pai != null e soBase
 *   m.*{sem conta,u,data} = dados
 * Se pai == null e tudo
 *   m.*|raiz == dados
 * Se pai != null e tudo
 *   
 * 		
 */
//			Set<Movimento> x = new HashSet();
//			for(Movimento m: (List<Movimento>)pesquisarTodos() )
//			{
//				if( m.getMovimento() != null )
//				{
//					m.setMovimento(m.getMovimento());
//					x.add(m);
//				}				
//			}
//			salvar(x);
			
		
		
			String jpaQL = "select distinct m from Movimento m left join fetch m.movimentos where 1=1";
			
			if( soBase && pai == null )
				jpaQL += " and m.movimento is null ";
			if( pai != null )
		        jpaQL+=" and m.movimento = :pai ";
	        if( filtroMesDe != null )
		        jpaQL+=" and str_to_date(concat('01/',month(m.dataRaiz),'/',year(m.dataRaiz)),'%d/%m/%Y')>=str_to_date(concat('01/',:filtroMesDe),'%d/%m/%Y') ";
	        if( filtroMesAte != null )
		        jpaQL+=" and str_to_date(concat('01/',month(m.dataRaiz),'/',year(m.dataRaiz)),'%d/%m/%Y')<=str_to_date(concat('01/',:filtroMesAte),'%d/%m/%Y') ";
		    if( usuario != null )
		        jpaQL+=" and m.conta.usuario=:usuario ";
		    if( conta != null )
		        jpaQL+=" and m.conta=:conta ";
		    if( classificacoes != null && classificacoes.size()>0)
		    {
		    	jpaQL+=" and ( 1=0 ";
		    	for( int i=0; i<classificacoes.size(); i++ )
		    	{
		    		jpaQL+=" or :c"+i+" member of m.classificacoes ";
		    		if( classList.get(i).equals(sc) )
		    			jpaQL+=" or size(m.classificacoes)=0 ";
		    	}
		    	jpaQL+=" ) ";
		    }
		        
		    if( descricao     != null && !"".equals(descricao))
		        jpaQL+=" and m.descricao like concat('%',:descricao,'%') ";
		    if( comentario     != null && !"".equals(comentario))
		        jpaQL+=" and m.comentario like concat('%',:comentario,'%') ";
		    if( valor     != null && !"".equals(valor))
		        jpaQL+=" and m.valor=:valor ";
		    if( semComentario     != null && semComentario )
		        jpaQL+=" and (m.comentario is null or m.comentario='') ";
//		    if( (semClassificacoes != null && semClassificacoes) || (classificacoes != null && classificacoes.contains(sc)) )
		    if( semClassificacoes != null && semClassificacoes )
		        jpaQL+=" and size(m.classificacoes)=0 ";
		    if( pessoal != null && pessoal )
		    {
		        jpaQL+=" and :ad not member of m.classificacoes ";
		        jpaQL+=" and :sq not member of m.classificacoes ";
		        jpaQL+=" and :ac not member of m.classificacoes ";
		        jpaQL+=" and :iv not member of m.classificacoes ";
		        jpaQL+=" and :emp not member of m.classificacoes ";
		    	jpaQL+=" and m.valor <= 0  ";
//		    	jpaQL+=" and m.movimentos is empty ";
		    	jpaQL+=" and size(m.movimentos)=0 ";
		    	jpaQL+=" and m.classificacoes is not empty ";
		    }
		    if( sinalMais ^ sinalMenos )
		    	jpaQL+=" and " + (sinalMais?"m.valor >= 0 ":"") + (sinalMenos?"m.valor <= 0 ":"");
		    
		    jpaQL += " order by m.data";
		    
			Query query = ContasRequestListener.getEntityManager().createQuery(jpaQL);
			
			if( pai != null )
				query.setParameter("pai",pai);
			if( filtroMesDe != null )
		        query.setParameter("filtroMesDe",filtroMesDe);
	        if( filtroMesAte != null )
	        	query.setParameter("filtroMesAte",filtroMesAte);
		    if( usuario != null )
		    	query.setParameter("usuario",usuario);
		    if( conta != null )
		    	query.setParameter("conta",conta);
		    if( classificacoes != null && classificacoes.size()>0)
		    {
		    	//query.setParameter("classificacoes",classificacoes);
		    	for( int i=0; i<classificacoes.size(); i++ )
		    		query.setParameter("c"+i,classList.get(i));
		    }
		    if( descricao     != null && !"".equals(descricao))
		    	query.setParameter("descricao",descricao);
		    if( comentario     != null && !"".equals(comentario))
		    	query.setParameter("comentario",comentario);
		    if( valor     != null && !"".equals(valor))
		    	query.setParameter("valor",valor);
		    if( pessoal != null && pessoal )
		    {
		    	query.setParameter("ad",ad);
		    	query.setParameter("sq",sq);
		    	query.setParameter("ac",ac);
		    	query.setParameter("iv",iv);
		    	query.setParameter("emp",emp);
		    }
            for( Movimento m: (List<Movimento>)query.getResultList() )
            {
                m = mapMovimento(m);
                ret.add(m);
            }
		return ret;
	}
	
    Mapper mapper;
    public Movimento mapMovimento(Movimento m)
    {
        if( mapper == null )
        {
            
            mapper = new DozerBeanMapper();
//            mapper = DozerBeanMapperSingletonWrapper.getInstance();
        }
        
        Movimento ret = mapper.map(m, Movimento.class);
        ret.setClassificacoes(new ArrayList(m.getClassificacoes()));
        List subs = new ArrayList();
        for( Movimento sub: m.getMovimentos() )
        {
            sub = mapMovimento(sub);
            subs.add(sub);
        }
        ret.setMovimentos(subs);
        return ret;
    }

	
	public List<Movimento> pesquisar(Movimento pai)
	{
		String jpaQL = "select distinct m from Movimento m where m.movimento=? order by data";
		
		Query query = ContasRequestListener.getEntityManager().createQuery(jpaQL);
		query.setParameter(1,pai);
		return query.getResultList();
	}
	
	public boolean pesquisarIdentico(Movimento mov) {
		Query query = ContasRequestListener.getEntityManager().createQuery(
				"select count(*) as c from Movimento m where " +
				"m.data=? and m.descricao=? and m.valor=? and m.conta=?");
		query.setParameter(1,mov.getData());
		query.setParameter(2,mov.getDescricao());
		query.setParameter(3,mov.getValor());
		query.setParameter(4,mov.getConta());
		int res = ((Number)query.getSingleResult()).intValue();
		return res!=0;
	}
	
	public Map<String,Number> getEstatisticas()  
    {
        Query query = ContasRequestListener.getEntityManager().createQuery(
            "select new Map( " +
            "   count(distinct m) as movimentosCnt, " +
            "   sum(case when m.classificacoes.size>0 then 1 else 0 end) as classificadosCnt, " +
            "   round(sum(case when m.classificacoes.size>0 then 1 else 0 end)/count(distinct m)*100) as classificadosPercentual, " +
            "   sum(case when m.valor<0 then -m.valor else m.valor end) as valor " +
            ") " +
            "from Movimento m"
        );
	    return (Map<String,Number>) query.getSingleResult();
    } 
	
	public List<Map<String,Number>> sumarizarSaldosMesAno(Conta c)
	{
		if( c == null )
			return null;
		Query query = ContasRequestListener.getEntityManager().createQuery(
	            "select new Map( " +
	            "   s.ano as ano, s.mes as mes, " +
	            "   s.saldoInicial as saldoInicial, s.saldoFinal as saldoFinal, "+
	            "   round(sum(m.valor) + case when s.saldoInicial is null then 0 else s.saldoInicial end,2) as im, " +
	            "   round(sum(m.valor),2) as movs " +
	            ") " +
	            "from Movimento m, Saldo s "+
	            "where s.conta = :conta and year(m.data)=s.ano and month(m.data)=s.mes and s.conta = m.conta "+
	            " and m.movimento is null "+
	            "group by s.ano, s.mes, s.saldoInicial, s.saldoFinal"
	        );
		query.setParameter("conta", c);
	    List<Map<String,Number>> res = query.getResultList();
	    
	    int i=0;
	    for( Map<String,Number> m: res )
	    {
	    	if( m.get("saldoInicial") == null )
	    	{
	    		if( i==0 )
	    			m.put("saldoInicial",0);
	    		else
	    		{
	    			m.put("saldoInicial",res.get(i-1).get("saldoFinal"));
	    			m.put("im",(double)(Math.round( (m.get("im").doubleValue() + res.get(i-1).get("saldoFinal").doubleValue())*100) / 100.0d));
	    			m.put("sid", 1);
	    		}
	    	}
	    	
	    	if( m.get("saldoFinal") == null )
	    	{
	    		m.put("saldoFinal", (double)(Math.round( (m.get("saldoInicial").doubleValue() + m.get("movs").doubleValue())*100) / 100.0d) );
	    		m.put("sfd", 1);
	    	}
	    	i++;
	    }
	    
	    return res;
	}
	
	@Transactional
	public Movimento atualizarMovimento(Movimento m)
    {
	    
	    Conta c2 = null;
	    if( m.getConta() != null && m.getConta().getId() > 0 )
	        c2 = (Conta)cd.pesquisarPorChavePrimaria(m.getConta().getId());
	    
        Movimento m2 = (Movimento)pesquisarPorChavePrimaria(m.getId());
        if( m2 == null )
            m2 = new Movimento();
        m2.setData(m.getData());
        m2.setDescricao(m.getDescricao());
        m2.setValor(m.getValor());
        m2.setComentario(m.getComentario());
        m2.setConta(c2);
        m2.getClassificacoes().clear();
        for(Classificacao c:m.getClassificacoes())
        {
            c = (Classificacao)cld.pesquisarPorChavePrimaria(c.getId());
            m2.getClassificacoes().add(c);
        }
        
        if( m.getMovimento() != null )
        {
            Movimento m3 = (Movimento)pesquisarPorChavePrimaria(m.getMovimento().getId());
            m2.setMovimento(m3);
        }
        
        salvar(m2);
        m2 = mapMovimento(m2);
        return m2;
    }
	
	@Transactional
    public void apagarMovimento(Movimento m)
    {
        Movimento m2 = (Movimento)pesquisarPorChavePrimaria(m.getId());
        apagar(m2);
    }
}