package com.joseth.contas.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.joseth.contas.beans.BlackDescricao;
import com.joseth.contas.beans.BlackString;
import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.beans.Conta;
import com.joseth.contas.beans.Movimento;
import com.joseth.contas.beans.Saldo;
import com.joseth.contas.beans.Template;
import com.joseth.contas.beans.Usuario;
import com.joseth.contas.client.ServiceBus;
import com.joseth.contas.client.resumo.ResumoDTO;
import com.joseth.contas.daos.BlackDescricaoDAO;
import com.joseth.contas.daos.BlackStringDAO;
import com.joseth.contas.daos.ClassificacaoDAO;
import com.joseth.contas.daos.ContaDAO;
import com.joseth.contas.daos.DAOGenerico;
import com.joseth.contas.daos.MovimentoDAO;
import com.joseth.contas.daos.SaldoDAO;
import com.joseth.contas.daos.TemplateDAO;
import com.joseth.contas.daos.UsuarioDAO;

@WebServlet(urlPatterns="/com.joseth.contas.ContasGWT/serviceBus") 
//@ServletSecurity(value=@HttpConstraint(rolesAllowed={"contas"})) 
public class ServiceBusImpl extends RemoteServiceServlet implements ServiceBus
{
    DAOGenerico dg = new DAOGenerico();
    
//	@EJB(beanName="mvd")
	MovimentoDAO mvd = new MovimentoDAO();
	
//	@EJB(beanName="ud") 
	UsuarioDAO ud = new UsuarioDAO();
	
//	@EJB(beanName="cd")
	ContaDAO cd = new ContaDAO();
	
//	@EJB(beanName="sd")
    SaldoDAO sd = new SaldoDAO();
	
//	@EJB(beanName="td")
    TemplateDAO td = new TemplateDAO();

//	@EJB(beanName="bdd")
	BlackDescricaoDAO bdd = new BlackDescricaoDAO();
   
//  @EJB(beanName="bsd")
    BlackStringDAO bsd = new BlackStringDAO();
	
//	@EJB(beanName="cld")
	ClassificacaoDAO cld = new ClassificacaoDAO();
	
    private SimpleDateFormat sdf = new SimpleDateFormat();
    public ServiceBusImpl()
    {
        sdf.applyPattern("MM/yyyy");
    }
	
	@Override
	public List<Classificacao> getClassificacoes() 
	{
		return cld.pesquisarTodos("nome");
	}
	
	@Override
	public List<Usuario> getUsuarios() 
	{
		return dg.getUsuarios();
	}
	
	@Override
	public List<Conta> getContas() 
	{
		return dg.getContas();
	}
	
	@Override
    public List<Template> getTemplates() 
    {
        return dg.getTemplates();
    }
	
	@Override
    public List<BlackDescricao> getBlackDescricoes() 
    {
        return dg.getBlackDescricoes();
    }
	
	@Override
    public List<BlackString> getBlackStrings() 
    {
        return dg.getBlackStrings();
    }
	
	public List<Saldo> getSaldos(String contaId) 
    {
        return dg.getSaldos(contaId);
    }
	
	public List<Map<String,String>> getAnoMesSoma(String contaId, String ano) 
    {
        return dg.getAnoMesSoma(contaId,ano);
    }
	
	public List<String> getMeses()
	{
		return dg.getMeses();
	}
	public List<Integer> getAnos()
	{ 
		return dg.getAnos();
	}
	public List<String> getMesesAnos()
	{
		return dg.getMesesAnos();
	}
	public List<String> getMesesAnos(Conta c)
	{
		return dg.getMesesAnos(c);
	}
	    
	public Collection<Movimento> getMovimentos(
			Boolean subMovimentos,
			String movimentoPaiId,
			String filtroDe, String filtroAte,
			String usuarioId, String contaId,
			Collection<String> classificacoesId, 
			String descricao, String comentario,  Double valor,
			Boolean pessoal,
			Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos
			)
	{
		Usuario u = null;
		Conta c = null;
		Collection<Classificacao> classList = new ArrayList<Classificacao>();
		Movimento pai = null;
		
		if( movimentoPaiId != null && !"".equals(movimentoPaiId))
		{
			try
			{
				pai = (Movimento)mvd.pesquisarPorChavePrimaria(Integer.parseInt(movimentoPaiId));
			}
			catch(NumberFormatException nfe){}
		}
		if( usuarioId != null && !"".equals(usuarioId))
		{
			try
			{
				u = (Usuario)ud.pesquisarPorChavePrimaria(Integer.parseInt(usuarioId));
			}
			catch(NumberFormatException nfe){}
		}
		if( contaId != null && !"".equals(contaId))
		{
			try
			{
				c = (Conta)cd.pesquisarPorChavePrimaria(Integer.parseInt(contaId));
			}
			catch(NumberFormatException nfe){}
		}
		for( String cl:classificacoesId)
		{
			try
			{
				classList.add((Classificacao)cld.pesquisarPorChavePrimaria(Integer.parseInt(cl)));
			}
			catch(NumberFormatException nfe){}
		}
		
		Collection<Movimento> ret = mvd.pesquisarTudo(
				subMovimentos,
				pai,
				filtroDe, filtroAte,
				u, c, classList, descricao, comentario, valor, pessoal, 
				semComentario, semClassificacoes, sinalMais, sinalMenos);
		
//		for(Movimento m: ret)
//			m.setMovimentos(null);
		
		return ret;
	}
	
	public List[] getArquivoMovimentos(String pass, Boolean inverter, Conta conta, Movimento pai)
	{
		HttpServletRequest tlr = this.getThreadLocalRequest();
		HttpSession s = tlr.getSession(true);
		Text2Movs t2m = new Text2Movs(mvd,pai,conta);
		List[] ret = t2m.preProcessar( (String)s.getAttribute(pass), inverter ); 
		return ret;
	}
	
	public List[] getTextoMovimentos(String texto, Boolean inverter, Conta conta, Movimento pai)
	{
		Text2Movs t2m = new Text2Movs(mvd,pai,conta);
		List[] ret = t2m.preProcessar( texto, inverter ); 
		return ret;
	}
	
	public List[] getTextoMovimentosTemplate(String template,String texto, Boolean inverter, Conta conta, Movimento pai)
    {
	    Set bss = new TreeSet();
	    Set bds = new TreeSet();
	    for( BlackString bs: getBlackStrings() )
	        bss.add(bs.getPadrao());
	    for( BlackDescricao bd: getBlackDescricoes() )
            bds.add(bd.getPadrao());
	    Text2MovsTemplate t2mt = new Text2MovsTemplate(template,mvd,bss,bds,pai,conta);
        List[] ret = t2mt.preProcessar( texto, inverter ); 
        return ret;
    }
	
	public Movimento atualizarMovimento(Movimento m)
	{
	    return mvd.atualizarMovimento(m);
	}
	public void removerMovimento(Movimento m)
	{
	    mvd.apagarMovimento(m);   
	}
	
	public Template atualizarTemplate(Template t)
    {
        return td.atualizarTemplate(t); 
    }
    public void removerTemplate(Template t)
    {
        td.apagar(t);   
    }
    
    public BlackDescricao atualizarBlackDescricao(BlackDescricao t)
    {
        return bdd.atualizarBlackDescricao(t); 
    }
    public void removerBlackDescricao(BlackDescricao t)
    {
        bdd.apagar(t);   
    }
    
    public BlackString atualizarBlackString(BlackString t)
    {
        return bsd.atualizarBlackString(t); 
    }
    public void removerBlackString(BlackString t)
    {
        bsd.apagar(t);   
    }


	public void atualizarSaldo(Saldo s)
    {
        sd.salvar(s);
    }

	
	public ResumoDTO getResumo()
	{
	    ResumoDTO ret = new ResumoDTO();
	    ret.usuariosCnt = ud.pesquisarTodos().size();
	    List<String> mesesAnos = dg.getMesesAnos();
	    if( mesesAnos.size() > 0 )
	    {
    	    ret.mesDe = mesesAnos.get(0);
    	    ret.mesAte = mesesAnos.get(mesesAnos.size()-1);
	    }
	    Map<String,Number> est = mvd.getEstatisticas();
	    ret.movCnt = est.get("movimentosCnt").intValue();
	    ret.movClassCnt = est.get("classificadosCnt").intValue();
	    ret.movPercentClass = est.get("classificadosPercentual").doubleValue();
	    ret.valor = est.get("valor").doubleValue();
	    return ret;
	}
 
	public void atualizar(Collection<Movimento> mvs)
	{
	    for(Movimento m: mvs)
	        mvd.salvar(m);
	}
	
	Set<String> mesAnoTabela=new TreeSet<String>(new Comparator<String>()
            {
            public int compare(String k1, String k2)
            {
                String sp1[]=k1.split("/");
                String sp2[]=k2.split("/");
                return (sp1[1].compareTo(sp2[1])!=0?sp1[1].compareTo(sp2[1]):sp1[0].compareTo(sp2[0]));
            }
        });
	
	public List relatorioPeriodo(
            Boolean subMovimentos,
            String movimentoPaiId,
            String filtroMesAnoDe, String filtroMesAnoAte,
            String usuarioId, String contaId,
            Collection<String> classificacoesId, 
            String descricao, String comentario,  Double valor,
            Boolean pessoal,
            Boolean semComentario, Boolean semClassificacoes, Boolean sinalMais, Boolean sinalMenos)
	{
	    mesAnoTabela.clear();
	    List movimentosFiltrados;
	       
	    Collection<Movimento> l = getMovimentos(true,null,
                filtroMesAnoDe,
                filtroMesAnoAte,
                usuarioId,contaId,
                classificacoesId,
                descricao,comentario, valor,
                pessoal,
                semComentario,semClassificacoes,sinalMais,sinalMenos);
        
//      Map<String,Map> res = new TreeMap<String,Map>(new Comparator<String>()
//              {
//                  public int compare(String k1, String k2)
//                  {
//                      String sp1[]=k1.split("/");
//                      String sp2[]=k2.split("/");
//                      return (sp1[1].compareTo(sp2[1])!=0?sp1[1].compareTo(sp2[1]):sp1[0].compareTo(sp2[0]));
//                  }
//              }
//      );
//      Map<String,Map> res = new TreeMap<String,Map>(new Comparator<String>()
//          {
//              public int compare(String k1, String k2)
//              {
//                  String sp1[]=k1.split("/");
//                  String sp2[]=k2.split("/");
//                  return (sp1[1].compareTo(sp2[1])!=0?sp1[1].compareTo(sp2[1]):sp1[0].compareTo(sp2[0]));
//              }
//          }
//      );
        Map<String,Map> res = new TreeMap<String,Map>();
//        graphData = new HashMap();
//        graphData.put("max",0d);
        
        processarMovimentos( new HashSet(), l, res );
        
        Set buf = new TreeSet(new Comparator<Map>()
        {

            public int compare(Map o1, Map o2) {
                if( !o1.get("nome").equals(o2.get("nome")))
                {
                    if( o1.get("nome").equals("Salário"))
                        return -10000000;
                    if( o2.get("nome").equals("Salário"))
                        return 10000000;
                    if( o1.get("nome").equals("Total"))
                        return 10000000;
                    if( o2.get("nome").equals("Total"))
                        return -10000000;
                }
                int cmp = ((Double)o1.get("Total")).compareTo((Double)o2.get("Total"));
                if( cmp == 0 )
                    cmp = ((String)o1.get("nome")).compareTo((String)o2.get("nome"));
                return cmp;
            }
            
        }
        );
        buf.addAll(res.values());
        
        movimentosFiltrados = new ArrayList(buf);
        List bufMesAno = new ArrayList();
        bufMesAno.addAll(mesAnoTabela);
        movimentosFiltrados.add(0,bufMesAno);

//        Double m = (Double)graphData.get("max");
//        List gl = new ArrayList();
//        
//        int h=0;
//        String cores[]=new String[]{"black","red","green","blue","gray","pink","orange","yellow"};
//        for( Map ll: (List<Map>)movimentosFiltrados )
//        {
//            if( ll.get("nome").equals("Total") )
//                continue;
//            String pl="";
//            int i=0;
//            for( String x: mesAnoTabela )
//            {
//                //pl+= (10+i*(mesAnoTabela.size()*100-20)/(mesAnoTabela.size()-1));
//                pl+= (10+i*(mesAnoTabela.size()*100-20)/(mesAnoTabela.size()));
//                pl+= ",";
//                Double v = (Double)ll.get(x);
//                v = (v==null?0d:Math.abs(v));
////                pl+= (185-((Double)(v/m*170)).intValue());
//                pl+= " ";
//                i++;
//            }
//            Map ml = new HashMap();
//            ml.put("pl",pl);
//            ml.put("cor",cores[h%cores.length]);
//            gl.add(ml);
//            h++;
//        }
//        graphData.put("lines",gl);
        /*
          <script>
            var pd = "<ui:repeat value='#{movimentoRelatorio.mesAnoTabela.toArray()}' var='rel' varStatus='s'>
                #{50+(s.index*700/movimentoRelatorio.mesAnoTabela.size()).intValue()},
                #{200+(rel[movimentoRelatorio.mesAnoTabela[s.index]]/movimentoRelatorio.graphData.max*200).intValue()}</ui:repeat>";
          </script>
          #{movimentoRelatorio.movimentosFiltrados[0]['01/2012'].intValue()}
            <svg xmlns="http://www.w3.org/2000/svg" version="1.1" style="border-style:solid;border-width:10px;height:200px;width:800px;">
              <ui:repeat value="#{movimentoRelatorio.movimentosFiltrados}" var="rel">
                <polyline points="" style="fill:none;stroke:black;stroke-width:3" />
              </ui:repeat>
            </svg>
            <!--                                 50,#{200+(rel['01/2012']/movimentoRelatorio.graphData.max*200).intValue()} 
                                  400,#{200+(rel['02/2012']/movimentoRelatorio.graphData.max*200).intValue()}
                                  750,#{200+(rel['03/2012']/movimentoRelatorio.graphData.max*200).intValue()} -->*/
        
        return movimentosFiltrados;
    }
    private void processarMovimentos( Set<Movimento> processados, Collection<Movimento> l, Map<String,Map>res )
    {   
        Map total = res.get("Total");
        if( total == null )
        {
            total = new HashMap();
            total.put("nome","Total");
            res.put("Total",total);
        }
        for( Movimento m:l  )
        {
            if( processados.contains(m) )
                continue;
            processados.add(m);
            
            // MovimentoRaiz é para considerar a data do cartao de credito e nao a da entrada
            String mesAnoKey = sdf.format(m.getMovimentoRaiz().getData());
            mesAnoTabela.add(mesAnoKey);
                        
            for(Classificacao c:m.getClassificacoesExtra())
            {
                Map linha = res.get(c.getNome());
                if( linha == null )
                {
                    linha = new HashMap();
                    linha.put("nome",c.getNome());
                    res.put(c.getNome(),linha);
                }
                Double valor = (Double)linha.get(mesAnoKey);
                valor=(valor==null?0:valor);
                linha.put(mesAnoKey,valor+m.getValor());
//                graphData.put("max", Math.max((Double)graphData.get("max"),Math.abs(valor+m.getValor())));
                
                Double colunaTotalValor = (Double)linha.get("Total");
                colunaTotalValor=(colunaTotalValor==null?0:colunaTotalValor);
                linha.put("Total",colunaTotalValor+m.getValor());
            }
            
            if( m.getMovimentos() == null || m.getMovimentos().size()==0 )
            {
            
                Double linhaTotalValor = (Double)total.get(mesAnoKey);
                linhaTotalValor=(linhaTotalValor==null?0:linhaTotalValor);
                total.put(mesAnoKey,linhaTotalValor+m.getValor());
                
                Double linhaTotalTotalValor = (Double)total.get("Total");
                linhaTotalTotalValor=(linhaTotalTotalValor==null?0:linhaTotalTotalValor);
                total.put("Total",linhaTotalTotalValor+m.getValor());
            }
            
            
            /*Map mesAnoLinha = res.get(mesAnoKey);
            if( mesAnoLinha == null )
            {
                mesAnoLinha = new HashMap();
                res.put(mesAnoKey,mesAnoLinha);
            }
            
            usuariosTabela.add(m.getUsuarioRaiz());
            Double usuarioRaizValor = (Double)mesAnoLinha.get(m.getUsuarioRaiz().getNome());
            Double totalHValor = (Double)mesAnoLinha.get("Total");
            usuarioRaizValor=(usuarioRaizValor==null?0:usuarioRaizValor);
            totalHValor=(totalHValor==null?0:totalHValor);
            
            
            mesAnoLinha.put("mesAno",mesAnoKey);
            
            Double linhaTotalValor = (Double)total.get(m.getUsuarioRaiz().getNome());
            Double linhaTotalHValor = (Double)total.get("Total");

            
            if( m.getMovimentos() == null || m.getMovimentos().size()==0 )
            {
                mesAnoLinha.put(m.getUsuarioRaiz().getNome(),usuarioRaizValor+m.getValor());
                mesAnoLinha.put("Total",totalHValor+m.getValor());
                
                linhaTotalValor=(linhaTotalValor==null?0:linhaTotalValor);
                total.put(m.getUsuarioRaiz().getNome(),linhaTotalValor+m.getValor());

                linhaTotalHValor=(linhaTotalHValor==null?0:linhaTotalHValor);               
                total.put("Total",linhaTotalHValor+m.getValor());
            }*/
            
            
            
            if( m.getMovimentos() !=null && m.getMovimentos().size() > 0 )
                processarMovimentos(processados,m.getMovimentos(), res);
        }
    }

    @Override
    public Classificacao atualizar(Classificacao c)
    {
        return cld.atualizar(c);
    }

    @Override
    public void apagar(Classificacao c)
    {
        cld.apagar(c);        
    }
    
    public List<Map> pesquisarTodosContagem()
    {
        return cld.pesquisarTodosContagem();
    }

    @Override
    public List<Map> getClassificacoesContagem()
    {
        return cld.pesquisarTodosContagem();
    }
} 