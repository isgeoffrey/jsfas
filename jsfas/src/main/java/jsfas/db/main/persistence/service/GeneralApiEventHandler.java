package jsfas.db.main.persistence.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.OAuth2RestTemplate;
import org.springframework.transaction.annotation.Transactional;

import jsfas.common.constants.PymtTypeConstants;
import jsfas.common.exception.InvalidDateException;
import jsfas.common.exception.InvalidParameterException;
import jsfas.common.json.CommonJson;
import jsfas.common.object.CoaObject;
import jsfas.common.utils.GeneralUtil;

import jsfas.security.SecurityUtils;

public class GeneralApiEventHandler implements GeneralApiService {

	@Inject
	Environment env;

}
