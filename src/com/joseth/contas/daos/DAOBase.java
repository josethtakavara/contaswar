package com.joseth.contas.daos;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.xml.bind.JAXB;

import org.hibernate.ReplicationMode;
import org.hibernate.Session;

import com.joseth.contas.web.ContasRequestListener;

public class DAOBase 
{
	//@PersistenceContext  (unitName = "primary")
//	protected EntityManager em = ContasRequestListener.getEntityManager();

	protected Class clazz;

	public DAOBase(Class clazz) {
		if (clazz == null)
			throw new NullPointerException();
		this.clazz = clazz;
	}

	public Object pesquisarPorChavePrimaria(Serializable o) {
		return (Object) ContasRequestListener.getEntityManager().find(clazz, o);
	}

	public List pesquisar(Map<String, Object> m) {
		return pesquisar(m, null);
	}

	public List pesquisar(Map<String, Object> m, String ordem) {
		String hql = "from " + clazz.getCanonicalName() + " as o ";
		int i = 0;
		for (Iterator<String> it = m.keySet().iterator(); it.hasNext(); i++) {
			String campo = (String) it.next();
			if (i == 0)
				hql += "where ";
			else
				hql += "and ";
			if (m.get(campo) == null)
				hql += "o." + campo + " is null ";
			else
				hql += "o." + campo + "=? ";
		}
		if (ordem != null) {
			hql += " order by " + ordem;
		}

		Query q = ContasRequestListener.getEntityManager().createQuery(hql);

		i = 0;
		for (Iterator it = m.values().iterator(); it.hasNext();) {
			Object v = it.next();
			if (v != null) {
				q.setParameter(i, v);
				i++;
			}
		}

		return q.getResultList();
	}

	public List pesquisar(String campo, Object valor) {
		Query q = null;
		if (valor != null) {
			q = ContasRequestListener.getEntityManager().createQuery(
					"from " + clazz.getCanonicalName() + " as o where o."
							+ campo + "=?");
			q.setParameter(1, valor);
		} else
			q = ContasRequestListener.getEntityManager().createQuery(
					"from " + clazz.getCanonicalName() + " as o where o."
							+ campo + " is null");
		return q.getResultList();
	}

	public List pesquisar(String campo, Object valor, String ordem) {
		String hql = "from " + clazz.getCanonicalName() + " as o ";
		Query q = null;

		if (valor != null) {
			hql += "where o." + campo + "=? ";
		} else
			hql += "where o." + campo + " is null ";

		if (ordem != null) {
			hql += "order by o." + ordem;
		}

		q = ContasRequestListener.getEntityManager().createQuery(hql);
		q.setParameter(1, valor);

		return q.getResultList();
	}

	public List pesquisarTodos() {
		return pesquisarTodos(null);
	}

	public List pesquisarTodos(String campo) {
		return pesquisarTodos(campo, true);
	}

	public List pesquisarTodos(String campo, boolean up) {
		String jpaQL = "from " + clazz.getCanonicalName();

		if (campo != null)
			jpaQL += " order by " + campo + (up ? " asc" : " desc");

		return ContasRequestListener.getEntityManager().createQuery(jpaQL).getResultList();
	}

	public void salvar(Collection c) 
	{
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			salvarInterno(o);
		}
	}

	public void salvar(Object o) 
	{
		salvarInterno(o);
	}

	protected void salvarInterno(Object o) 
	{
		verificaClasse(o);
		ContasRequestListener.getEntityManager().persist(o);
//		ContasRequestListener.getEntityManager().flush();
	}

	@Transactional
	public void replicar(Object o) {
		verificaClasse(o);
		Session s = ContasRequestListener.getEntityManager().unwrap(Session.class);
		s.replicate(o, ReplicationMode.OVERWRITE);
		s.flush();
	}

	@Transactional
	public void apagar(Collection c) 
	{
		for (Iterator it = c.iterator(); it.hasNext();) {
			Object o = it.next();
			apagarInterno(o);
		}
	}

	@Transactional
	public void apagar(Object o) 
	{
		apagarInterno(o);
	}

	public void apagarInterno(Object o) {
		verificaClasse(o);
		o = ContasRequestListener.getEntityManager().merge(o);
		ContasRequestListener.getEntityManager().remove(o);
	}

	public void verificaClasse(Object o) {
		if (!clazz.isInstance(o))
			throw new Error("Uso incorreto do DAO. Esperado objecto da classe "
					+ clazz.getName() + " mas encontrada "
					+ (o == null ? null : o.getClass().getName()));
	}

	public String toXml(Object o) {
		verificaClasse(o);
		StringWriter sw = new StringWriter();
		JAXB.marshal(o, sw);
		return sw.toString();
	}

	public Object fromXml(String s) {
		return JAXB.unmarshal(new StringReader(s), clazz);
	}
	
	@Transactional
	public void merge( Object o )
	{
		ContasRequestListener.getEntityManager().merge(o);
	}

}
