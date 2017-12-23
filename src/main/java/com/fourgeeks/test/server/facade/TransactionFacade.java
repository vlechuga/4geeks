package com.fourgeeks.test.server.facade;

import com.fourgeeks.test.server.domain.entities.Transaction;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class TransactionFacade extends AbstractFacade<Transaction> {

    @PersistenceContext(unitName = "JPA_TEST")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public TransactionFacade() {
        super(Transaction.class);
    }

}
