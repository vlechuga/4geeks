package com.fourgeeks.test.server.facade;

import com.fourgeeks.test.server.domain.entities.Person;
import com.fourgeeks.test.server.providers.exceptions.EntityAlreadyExistsException;
import com.fourgeeks.test.server.providers.exceptions.NotFoundException;
import com.fourgeeks.test.server.services.interfaces.PasswordEncryptionService;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;
import java.util.Objects;
import java.util.Optional;


@Stateless
public class PersonFacade extends AbstractFacade<Person> {

    @PersistenceContext(unitName = "JPA_TEST")
    private EntityManager em;

    @Inject
    private PasswordEncryptionService passwordEncryptionService;

    protected EntityManager getEntityManager() {
        return this.em;
    }

    public PersonFacade() {
        super(Person.class);
    }

    public Person findByEmail(String email) {
        List<Person> fromDB = getPersonByEmail(email);
        if (Objects.nonNull(fromDB) && fromDB.isEmpty()) {
            throw new NotFoundException();
        }
        return fromDB.get(0);
    }

    public Person create(Person entity) {
        List<Person> fromDB = getPersonByEmail(entity.getEmail());
        if (Objects.nonNull(fromDB) && !fromDB.isEmpty()) {
            throw new EntityAlreadyExistsException("This User already exists");
        }
        entity.setPassword(passwordEncryptionService.encrypt(entity.getPassword()));
        super.create(entity);
        return entity;
    }

    private List<Person> getPersonByEmail(String email) {
        return em.createNamedQuery("Person.findByEmail")
                .setParameter("email", email)
                .getResultList();
    }
}
