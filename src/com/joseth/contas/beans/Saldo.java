package com.joseth.contas.beans;

import java.io.Serializable;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Version;

//@XmlRootElement
@Entity
//@-Cache(usage=CacheConcurrencyStrategy.READ_WRITE)
public class Saldo implements Serializable, Comparable<Saldo> {
	
	private Integer id;
	private Integer version;
	private Integer mes;
	private Integer ano;
	private Conta conta;
	private Double saldoInicial;
	private Double saldoFinal;
		
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
	

	public Integer getMes() {
		return mes;
	}

	public void setMes(Integer mes) {
		this.mes = mes;
	}

	public Integer getAno() {
		return ano;
	}

	public void setAno(Integer ano) {
		this.ano = ano;
	}

	public Double getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(Double saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public Double getSaldoFinal() {
		return saldoFinal;
	}

	public void setSaldoFinal(Double saldoFinal) {
		this.saldoFinal = saldoFinal;
	}
	
	@ManyToOne
	public Conta getConta() {
		return conta;
	}

	public void setConta(Conta conta) {
		this.conta = conta;
	}

	public int hashCode(){return id==null?super.hashCode():id.intValue();}
	public boolean equals( Object o )
	{
		if( o instanceof Saldo )
		{
			Saldo s = (Saldo)o;
			if( s.getId().equals(id) )
				return true;
			if( s.getId() == null || id == null || s.getId() == 0 || id == 0 )
				if( conta.equals(s.getConta()) && mes.equals(s.getMes()) && ano.equals(s.getAno()))
					return true;
		}
		return false;
	}

	public String toString(){return "Saldo("+id+","+mes+","+ano+")";}
	
	public int compareTo(Saldo s) {
		return conta.compareTo(s.getConta()) + ano.compareTo(s.getAno()) + mes.compareTo(s.getMes());
	}
}
