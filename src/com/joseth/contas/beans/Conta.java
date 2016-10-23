package com.joseth.contas.beans;
 
import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.joseth.contas.xmlbkp.UsuarioRefXmlAdapter;

@Entity
public class Conta implements Serializable, Comparable<Conta> {
	
	private Integer id;
	private Integer version;
	private Usuario usuario;
	private String numero;
		 
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	public Integer getId() {
	     return id;
	}

	public void setId(Integer id) {
	     this.id = id;
	}
	
	@Version
	public Integer getVersion() {
	     return version;
	}

//	@SuppressWarnings("unused")
	public void setVersion(Integer version) {
	     this.version = version;
	}   	
	
	@XmlJavaTypeAdapter(UsuarioRefXmlAdapter.class)
	@ManyToOne
	public Usuario getUsuario() {
	     return usuario;
	}

	public void setUsuario(Usuario usuario) {
	     this.usuario = usuario;
	}
	
	public String getNumero() {
	     return numero;
	}

	public void setNumero(String numero) {
	     this.numero = numero;
	}   	
	public int hashCode(){return id==null?super.hashCode():id.intValue();}
	public boolean equals( Object o ){return o instanceof Conta && ((Conta)o).getId().equals(id);}
	public int compareTo(Conta c) {
		return usuario.compareTo(c.getUsuario()) + numero.compareTo(c.getNumero());
	}
}
