package com.joseth.contas.daos;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.Query;

import org.apache.commons.logging.Log;

import com.joseth.contas.web.ContasRequestListener;

public class DAOGenericoQueryCache extends DAOBase {
//	@Logger
	Log log;

//	@In("hibSession") Session hibSession;

	public DAOGenericoQueryCache(Class clazz) 
	{
		super(clazz);
	}

	public List pesquisar(Map<String, Object> m, String ord) {
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
		if (ord != null) {
			hql += " order by " + ord;
		}

		Query q = ContasRequestListener.getEntityManager().createQuery(hql);
		q.setHint("org.hibernate.cacheable", true);

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
		q.setHint("org.hibernate.cacheable", new Boolean(true));
		return q.getResultList();
	}

	public List pesquisar(String campo, Object valor, String ord) {
		String hql = "from " + clazz.getCanonicalName() + " as o ";
		Query q = null;

		if (valor != null) {
			hql += "where o." + campo + "=? ";
		} else
			hql += "where o." + campo + " is null ";

		if (ord != null) {
			hql += "order by o." + ord;
		}

		q = ContasRequestListener.getEntityManager().createQuery(hql);
		q.setHint("org.hibernate.cacheable", new Boolean(true));
		q.setParameter(0, valor);

		return q.getResultList();
	}

	public List pesquisarTodos(String campo, boolean up) {
		String jpaQL = "from " + clazz.getCanonicalName();

		if (campo != null)
			jpaQL += " order by " + campo + (up ? " asc" : " desc");

		Query q = ContasRequestListener.getEntityManager().createQuery(jpaQL);
		q.setHint("org.hibernate.cacheable", true);
		return q.getResultList();
	}
}