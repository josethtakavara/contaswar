package com.joseth.contas.xmlbkp;

import java.io.StringWriter;

import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.joseth.contas.beans.Usuario;
import com.joseth.contas.daos.UsuarioDAO;

public class UsuarioXmlAdapter extends XmlAdapter<String,Usuario>
{
//	@EJB(beanName="ud")
	UsuarioDAO ud = new UsuarioDAO();
	
	public String marshal(Usuario c)
	{
		StringWriter sw = new StringWriter();
    	JAXB.marshal(c,sw);
    	return sw.toString();
	}
	
    public Usuario unmarshal(String v)
	{
    	Usuario c = JAXB.unmarshal(v,Usuario.class);
    	Usuario c2 = (Usuario)ud.pesquisarPorChavePrimaria(c.getId());
    	if( c2 == null )
    	{
    		ud.salvar(c);
    		c = c2;
		}
    	return c2;
	}
}
