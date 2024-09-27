package jsfas.common;

import java.math.BigDecimal;

/**
 * @author iseric
 * @since 12/5/2016
 * @see This is a Tools Class for double calculation
 */
public final class Arith {
	 private static final int DEF_DIV_SCALE=10;
	 
	    private Arith(){}

	    public static double add(double v1,double v2){
	        BigDecimal b1=new BigDecimal(Double.toString(v1));
	        BigDecimal b2=new BigDecimal(Double.toString(v2));
	        return b1.add(b2).doubleValue();
	    }
	    public static double add(double v1,double v2,double v3){
	        double r=add(v1,v2);
	        return add(r,v3);
	    }

	    public static int add(int[] v){
	    	int sum=0;
	        for(int i=0;i<v.length;i++){
	        	sum=(int)add(sum,v[i]);
	        }
	        return sum;
	    }
	    
	    public static double add(double[] v){
	        double sum=0;
	        for(int i=0;i<v.length;i++){
	        	sum=add(sum,v[i]);
	        }
	        return sum;
	    }
	    
	    public static double sub(double v1,double v2){
	        BigDecimal b1=new BigDecimal(Double.toString(v1));
	        BigDecimal b2=new BigDecimal(Double.toString(v2));
	        return b1.subtract(b2).doubleValue();
	    }
	 
	    public static double mul(double v1,double v2){
	        BigDecimal b1=new BigDecimal(Double.toString(v1));
	        BigDecimal b2=new BigDecimal(Double.toString(v2));
	        return b1.multiply(b2).doubleValue();
	    }
	    public static double mul(double v1,double v2,int scale){
	    	if(scale<0){
	            return -1;
	        }
	    	return round(mul(v1,v2),scale);
	    }
	 
	    public static double div(double v1,double v2){
	        return div(v1,v2,DEF_DIV_SCALE);
	    }
	 
	    public static double div(double v1,double v2,int scale){
	        if(scale<0){
	            return -1;
	        }
	        BigDecimal b1=new BigDecimal(Double.toString(v1));
	        BigDecimal b2=new BigDecimal(Double.toString(v2));
	        return b1.divide(b2,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
	 
	    public static double round(double v,int scale){
	        if (scale<0) {
	            return -1;
	        }
	        BigDecimal b=new BigDecimal(Double.toString(v));
	        BigDecimal one=new BigDecimal("1");
	        return b.divide(one,scale,BigDecimal.ROUND_HALF_UP).doubleValue();
	    }
}
