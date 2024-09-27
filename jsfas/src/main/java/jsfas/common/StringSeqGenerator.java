package jsfas.common;

import java.io.Serializable;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.hibernate.MappingException;
import org.hibernate.Session;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.jdbc.env.spi.JdbcEnvironment;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.internal.util.config.ConfigurationHelper;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.type.Type;
import org.springframework.hateoas.Identifiable;

import jsfas.common.utils.GeneralUtil;

import org.hibernate.id.Configurable;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.id.enhanced.SequenceStyleGenerator;

public class StringSeqGenerator implements IdentifierGenerator, Configurable {
    
    private boolean includeModCtrlTxtPrefix;
    
	private int leftPadSize;
	
	private String sequenceCallSyntax;
	
	@Override
	public void configure(Type type, Properties params, ServiceRegistry serviceRegistry) throws MappingException {
		final JdbcEnvironment jdbcEnvironment = serviceRegistry.getService(JdbcEnvironment.class);
		final Dialect dialect = jdbcEnvironment.getDialect();
		includeModCtrlTxtPrefix = ConfigurationHelper.getBoolean("includeModCtrlTxtPrefix", params, false);
        leftPadSize = ConfigurationHelper.getInt( "leftPadSize", params, 6);
		
		final String sequencePerEntitySuffix = ConfigurationHelper.getString(SequenceStyleGenerator.CONFIG_SEQUENCE_PER_ENTITY_SUFFIX, 
		        params,
		        SequenceStyleGenerator.DEF_SEQUENCE_SUFFIX);
		
		final String defaultSequenceName = ConfigurationHelper.getBoolean(SequenceStyleGenerator.CONFIG_PREFER_SEQUENCE_PER_ENTITY, params, false )
                ? params.getProperty( JPA_ENTITY_NAME ) + sequencePerEntitySuffix
                : SequenceStyleGenerator.DEF_SEQUENCE_NAME;
		
		final String sequenceName = ConfigurationHelper.getString(SequenceStyleGenerator.SEQUENCE_PARAM, params, defaultSequenceName );
		
		sequenceCallSyntax = dialect.getSequenceNextValString(sequenceName);
		
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public Serializable generate(SharedSessionContractImplementor session, Object obj) {
		if (obj instanceof Identifiable) {
			Identifiable identifiable = (Identifiable) obj;
			Serializable id = identifiable.getId();
			if (id != null) {
				return id;
			}
		}
		long seqValue = ((Number) Session.class.cast(session).createSQLQuery(sequenceCallSyntax).uniqueResult()).longValue();
		String seqValueStr = StringUtils.leftPad(String.valueOf(seqValue), leftPadSize, '0');
		if (includeModCtrlTxtPrefix) {
			seqValueStr = GeneralUtil.genModCtrlTxt() + seqValueStr;
		}
		return seqValueStr;
	}
}