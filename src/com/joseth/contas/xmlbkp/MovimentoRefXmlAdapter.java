package com.joseth.contas.xmlbkp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.joseth.contas.beans.Movimento;
import com.joseth.contas.daos.MovimentoDAO;

public class MovimentoRefXmlAdapter extends XmlAdapter<String,Movimento>
{
//	@EJB(beanName="mvd")
	MovimentoDAO mvd = new MovimentoDAO();

	public String marshal(Movimento c)
	{
		return ""+c.getId();
	}
	
    public Movimento unmarshal(String v) throws Exception
	{
//    	try
//		{
//    		InitialContext ctx = new InitialContext();
//			mvd = (MovimentoDAO)ctx.lookup("java:module/mvd");
//		} 
//    	catch (NamingException e) 
//    	{
//            throw new EJBException(e);
//        }
    	Integer id = Integer.parseInt(v);
    	Movimento m =(Movimento)mvd.pesquisarPorChavePrimaria(id);

    	if( m == null )
    		throw new Exception("Movimento não carregado");
    	return m;
    	//return (Movimento)hibSession.load(Movimento.class, id);
	}
}
