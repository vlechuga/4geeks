package com.fourgeeks.test.server.facade;

import com.fourgeeks.test.server.domain.entities.Note;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;


@Stateless
public class NoteFacade extends AbstractFacade<Note> {

    @PersistenceContext(unitName = "JPA_TEST")
    private EntityManager em;

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public NoteFacade() {
        super(Note.class);
    }

}
