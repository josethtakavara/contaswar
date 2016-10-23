package com.joseth.contas.beans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Version;
import javax.xml.bind.annotation.XmlTransient;

@Entity
public class Usuario implements Serializable, Comparable<Usuario> {
	private Integer id;
	private Integer version;
	private String nome;
	@XmlTransient private List<Conta> contas=new ArrayList<Conta>();
	
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
	
	public String getNome() { 
	     return nome;
	}

	public void setNome(String nome) {
	     this.nome = nome;
	}
	
	@XmlTransient
	@OneToMany(mappedBy="usuario",cascade=CascadeType.ALL)
    public List<Conta> getContas() {
        return contas;
    }
	
	public void setContas(List<Conta> contas) {
        this.contas = contas;
    }
	public int compareTo(Usuario u) {
		return nome.compareTo(u.getNome());
	}
	public int hashCode(){return id==null?super.hashCode():id.intValue();}
	public boolean equals( Object o ){return o instanceof Usuario && ((Usuario)o).getId().equals(id);}
}
