package com.joseth.contas.xmlbkp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.daos.ClassificacaoDAO;

public class ClassificacaoRefXmlAdapter extends XmlAdapter<String,Classificacao>
{
	//@EJB(beanName="cld")
	ClassificacaoDAO cld;
	 
	public String marshal(Classificacao c)
	{
		return ""+c.getId();
	}
	
    public Classificacao unmarshal(String v)
	{
//    	try
//		{
//    		InitialContext ctx = new InitialContext();
//			cld = (ClassificacaoDAO)ctx.lookup("java:module/cld");
//		} catch (NamingException e) {
//            throw new EJBException(e);
//        }
        cld = new ClassificacaoDAO();
    	Integer id = Integer.parseInt(v);
    	return (Classificacao)cld.pesquisarPorChavePrimaria(id);
	}
}
