package com.joseth.contas.xmlbkp;

import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.joseth.contas.beans.Classificacao;
import com.joseth.contas.daos.ClassificacaoDAO;

public class ClassificacaoXmlAdapter extends XmlAdapter<String,Classificacao>
{
//	@EJB(beanName="cld")
	ClassificacaoDAO cld = new ClassificacaoDAO();
	
	public String marshal(Classificacao c)
	{
		StringWriter sw = new StringWriter();
    	JAXB.marshal(c,sw);
    	return sw.toString();
	}
	
    public Classificacao unmarshal(String v)
	{
    	Classificacao c = JAXB.unmarshal(v,Classificacao.class);
    	Classificacao c2 = (Classificacao)cld.pesquisarPorChavePrimaria(c.getId());
    	if( c2 == null )
    	{
    		cld.salvar(c);
    		c = c2;
		}
    	return c2;
	}
}
