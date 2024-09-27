package jsfas.db.rbac.persistence.service;

import java.util.Optional;

import jsfas.db.rbac.persistence.domain.PredParamDAO;

public interface PredicateService {

    public boolean evalPredicate(Optional<PredParamDAO> optionalPredParam);
}
