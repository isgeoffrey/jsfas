package jsfas.db.rbac.persistence.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang.BooleanUtils;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import jsfas.common.constants.AppConstants;
import jsfas.common.json.CommonJson;
import jsfas.db.rbac.persistence.domain.PredParamDAO;
import jsfas.db.rbac.persistence.repository.PredParamRepository;

public class PredicateEventHandler implements PredicateService {

    @Autowired
    @Qualifier("sessionFactoryJsfasMain")
    private SessionFactory sessionFactoryMain;
    
    @Autowired
    private PredParamRepository predParamRepository;
    
    private final Logger log = LoggerFactory.getLogger(this.getClass());
    
    @SuppressWarnings("rawtypes")
    private Function<String, Map> getPredQueryParams(String predQueryParamType) {
        switch(predQueryParamType) {
        case AppConstants.PRED_QUERY_PARAM_JSON:
            return getPredQueryParamsInJSON();
        }
        throw new RuntimeException(String.format("Unknown predQueryParamType=%s", predQueryParamType));
    }
    
    @SuppressWarnings("rawtypes")
    private Function<String, Map> getPredQueryParamsInJSON() {
        Function<String, Map> function = str -> {
            ObjectMapper mapper = new ObjectMapper();
            try {
                log.debug("Convert json string={}", str);
                CommonJson commonJson = mapper.readValue(str, CommonJson.class);
                
                if(commonJson.getProps().containsKey("params")) {
                    return commonJson.get("params", Map.class);
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                log.error(String.format("Cannot convert json string=%s", str), e);
                throw new RuntimeException(e);
            }
            return Collections.emptyMap();
        };
        return function;
    }
    
    @SuppressWarnings("rawtypes")
    private BiFunction<String, Map, Boolean> getPredQuery(String predQueryType) {
        switch(predQueryType) {
        case AppConstants.PRED_QUERY_SQL:
            return getPredQueryInSQL();
        }
        throw new RuntimeException(String.format("Unknown predQueryType=%s", predQueryType));
    }
    
    @SuppressWarnings("rawtypes")
    private BiFunction<String, Map, Boolean> getPredQueryInSQL() {
        BiFunction<String, Map, Boolean> biFunction = (str, map) -> {
            Session session = sessionFactoryMain.openSession();
            try {
                SQLQuery query = session.createSQLQuery(str);
                for(Object obj: map.entrySet()) {
                    if(obj instanceof Map.Entry) {
                        Map.Entry entry = (Map.Entry)obj;
                        if(entry.getValue() instanceof List) {
                            query.setParameterList(entry.getKey().toString(), (Collection) entry.getValue());
                        } else {
                            query.setParameter(entry.getKey().toString(), entry.getValue());
                        }
                    }
                }
                log.debug("Execute sql query={}", query.getQueryString());
                return BooleanUtils.toBoolean(Integer.parseInt(query.uniqueResult().toString()));  
            } catch (Exception ex) {
                log.error("getPredQueryInSQL error occured", ex);
                throw ex;
            } finally {
                session.close();
            }
        };
        return biFunction;
    }
    
    @SuppressWarnings("rawtypes")
    @Override
    @Transactional(value = "transactionManagerJsfasMain", readOnly = true)
    public boolean evalPredicate(Optional<PredParamDAO> optionalPredParam) {
        // TODO Auto-generated method stub
        if(!optionalPredParam.isPresent()) {
            return true;
        }
        
        PredParamDAO predParam = predParamRepository.findOne(optionalPredParam.get().getPredParamType());
        Map predParamMap = getPredQueryParams(predParam.getPredQueryParam().getPredQueryParamType()).apply(predParam.getPredQueryParamStr());
        
        
        return getPredQuery(predParam.getPredicate().getPredQuery().getPredQueryType()).apply(predParam.getPredicate().getPredQueryStr(), predParamMap);
    }

}
