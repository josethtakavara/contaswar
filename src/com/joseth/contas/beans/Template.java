package com.joseth.contas.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Version;

@Entity
public class Template implements Serializable, Comparable<Template> {
	private Integer id;
	private Integer version;
	private String nome;
	private String padrao;
	
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

	public void setVersion(Integer version) {
	     this.version = version;
	}   	
	
	public String getPadrao() { 
	     return padrao;
	}

	public void setPadrao(String padrao) {
	     this.padrao = padrao;
	}
	
    public String getNome() { 
         return nome;
    }

    public void setNome(String nome) {
         this.nome = nome;
    }
	
	public int compareTo(Template t) {
		return padrao.compareTo(t.getPadrao());
	}
	public int hashCode(){return id==null?super.hashCode():id.intValue();}
	public boolean equals( Object o ){return o instanceof Template && ((Template)o).getId().equals(id);}
}
