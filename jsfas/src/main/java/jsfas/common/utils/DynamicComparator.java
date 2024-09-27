package jsfas.common.utils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;


/**
 * This class provides a way to build a dynamic comparator sort fields using java bean class naming convention, support the following types:
 * 1)  java.lang.String
 * 2)  byte
 * 3)  java.lang.Byte
 * 4)  short
 * 5)  java.lang.Short
 * 6)  int
 * 7)  java.lang.Integer
 * 8)  long
 * 9)  java.lang.Long
 * 10) float
 * 11) java.lang.Float
 * 12) double
 * 13) java.lang.Double
 * 14) boolean
 * 15) java.lang.Boolean
 * 16) java.math.BigDecimal
 * 17) java.sql.Timestamp
 * 18) java.util.Date
 * 19) java.sql.Date
 * 
 * @author isalister
 * @since 27/02/2018
 * @version 1.1
 */
public class DynamicComparator<T> implements Comparator<T> {

    private final Logger log = LoggerFactory.getLogger(DynamicComparator.class);
    
    private List<Method> methodLst;
    private List<String> returnTypeLst;
    private List<Order> orderLst;
    
    private Method getMethod(Class<T> cls, String prefix, String fieldName) {
        //Capitalize the first letter of field name to match java bean naming rules
        String methodName = prefix + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
        
        try {
            return cls.getMethod(methodName);
        } catch (Exception e) {
            log.error("Can't get method", e);
        }
        return null;
    }
    
    public DynamicComparator() {
        this.methodLst = new ArrayList<>();
        this.returnTypeLst = new ArrayList<>();
        this.orderLst = new ArrayList<>();
    }
    
    public DynamicComparator(Class<T> cls, Order order) {
        this();
        addOrder(cls, order);
    }
    
    public void addOrder(Class<T> cls, Order order) {
        Method method = getMethod(cls, "get", order.getProperty());
        this.methodLst.add(method);
        this.returnTypeLst.add(method.getReturnType().getName());
        this.orderLst.add(order);
    }
    
    public static <T> DynamicComparator<T> of(Class<T> cls, Sort sort) {
        DynamicComparator<T> comparator = new DynamicComparator<>();
        for(Iterator<Order> orderIter = sort.iterator(); orderIter.hasNext();) {
            comparator.addOrder(cls, orderIter.next());
        }
        return comparator;
    }
    
    public static <T> DynamicComparator<T> of(Class<T> cls, String property) {
        return DynamicComparator.of(cls, property, Sort.DEFAULT_DIRECTION);
    }
    
    public static <T> DynamicComparator<T> of(Class<T> cls, String property, Direction direction) {
        return DynamicComparator.of(cls, new Order(direction, property));
    }
    
    public static <T> DynamicComparator<T> of(Class<T> cls, Order order) {
        return new DynamicComparator<>(cls, order);
    }

    private int compareRecursive(T o1, T o2, int index) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        String returnType = returnTypeLst.get(index);
        Method method = methodLst.get(index);
        Order order = orderLst.get(index);
        Direction direction = order.getDirection();
        T a = o1;
        T b = o2;
        
        int compare = 0;
        
        if(direction.equals(Direction.DESC)) {
            a = o2;
            b = o1;
        }
        
        if(String.class.getName().equalsIgnoreCase(returnType) && !order.isIgnoreCase()) {
            compare = ((String)method.invoke(a)).compareTo((String)method.invoke(b));
        } else if(String.class.getName().equalsIgnoreCase(returnType) && order.isIgnoreCase()) {
            compare = ((String)method.invoke(a)).toLowerCase().compareTo(((String)method.invoke(b)).toLowerCase());
        } else if(byte.class.getName().equalsIgnoreCase(returnType) || Byte.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Byte)method.invoke(a)).compareTo((Byte)method.invoke(b));
        } else if(short.class.getName().equalsIgnoreCase(returnType) || Short.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Short)method.invoke(a)).compareTo((Short)method.invoke(b));
        } else if(int.class.getName().equalsIgnoreCase(returnType) || Integer.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Integer)method.invoke(a)).compareTo((Integer)method.invoke(b));
        } else if(long.class.getName().equalsIgnoreCase(returnType) || Long.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Long)method.invoke(a)).compareTo((Long)method.invoke(b));
        } else if(float.class.getName().equalsIgnoreCase(returnType) || Float.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Float)method.invoke(a)).compareTo((Float)method.invoke(b));
        } else if(double.class.getName().equalsIgnoreCase(returnType) || Double.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Double)method.invoke(a)).compareTo((Double)method.invoke(b));
        } else if(boolean.class.getName().equalsIgnoreCase(returnType) || Boolean.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Boolean)method.invoke(a)).compareTo((Boolean)method.invoke(b));
        } else if(BigDecimal.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((BigDecimal)method.invoke(a)).compareTo((BigDecimal)method.invoke(b));
        } else if(Timestamp.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((Timestamp)method.invoke(a)).compareTo((Timestamp)method.invoke(b));
        } else if(Date.class.getName().equalsIgnoreCase(returnType)) {
        	compare = ((Date)method.invoke(a)).compareTo((Date)method.invoke(b));
        } else if(java.sql.Date.class.getName().equalsIgnoreCase(returnType)) {
            compare = ((java.sql.Date)method.invoke(a)).compareTo((java.sql.Date)method.invoke(b));
        } else {
            throw new UnsupportedOperationException("Unsupported type=" + returnType);
        }
        
        //recursive to compare for other orders
        ++index;
        if(compare == 0 && index < returnTypeLst.size() && index < methodLst.size() && index < orderLst.size()) {
            compare = compareRecursive(o1, o2, index);
        }
        
        return compare;
    }
    
    @Override
    public int compare(T o1, T o2) {
        // TODO Auto-generated method stub
        if(o1 == null && o2 == null) {
            return 0;
        } else if(o1 == null || o2 == null) {
            return o1 == null? -1: 1;
        } else {
            try {
                return compareRecursive(o1, o2, 0);
            } catch(Exception e) {
                log.error("Can't compare", e);
            }
        }
        return 0;
    }
}
