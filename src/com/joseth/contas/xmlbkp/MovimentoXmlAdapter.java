package com.joseth.contas.xmlbkp;

import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.joseth.contas.beans.Movimento;
import com.joseth.contas.daos.MovimentoDAO;

public class MovimentoXmlAdapter extends XmlAdapter<String,Movimento>
{
//	@EJB(beanName="mvd")
	MovimentoDAO mvd = new MovimentoDAO();
	
	public String marshal(Movimento c)
	{
		StringWriter sw = new StringWriter();
    	JAXB.marshal(c,sw);
    	return sw.toString();
	}
	
    public Movimento unmarshal(String v)
	{
//    	try
//		{
//    		InitialContext ctx = new InitialContext();
//			mvd = (MovimentoDAO)ctx.lookup("java:module/mvd");
//		} catch (NamingException e) {
//            throw new EJBException(e);
//        }
    	Movimento c = JAXB.unmarshal(v,Movimento.class);
    	Movimento c2 = (Movimento)mvd.pesquisarPorChavePrimaria(c.getId());
    	if( c2 == null )
    	{
    		mvd.salvar(c);
    		c = c2;
		}
    	return c2;
	}
}
