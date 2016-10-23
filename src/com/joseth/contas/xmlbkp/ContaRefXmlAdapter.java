package com.joseth.contas.xmlbkp;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.joseth.contas.beans.Conta;
import com.joseth.contas.daos.ContaDAO;

public class ContaRefXmlAdapter extends XmlAdapter<String,Conta>
{
//	@EJB(beanName="cd")
	ContaDAO cd = new ContaDAO();
	
	public String marshal(Conta c)
	{
		return ""+c.getId();
	}
	
    public Conta unmarshal(String v)
	{
    	Integer id = Integer.parseInt(v);
//    	if( cd == null )
//    	{
//    		try
//    		{
//    			InitialContext ctx = new InitialContext();
//    			cd = (ContaDAO)ctx.lookup("java:module/cd");
//    		} catch (NamingException e) {
//                throw new EJBException(e);
//            }
//    	}
    	return (Conta)cd.pesquisarPorChavePrimaria(id);
	}
}
