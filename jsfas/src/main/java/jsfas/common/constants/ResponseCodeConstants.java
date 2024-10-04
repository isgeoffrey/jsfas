package jsfas.common.constants;

public class ResponseCodeConstants {
	
	//standard response
	public static final Integer SUCCESS               = 200; //for success response. empty return list should be usually regards as 200
	public static final Integer SUCCESS_WITH_WARNING  = 210;
	public static final Integer ERROR                 = 400;
	public static final Integer CONFLICT			  = 409;
	public static final Integer UNAUTHORIZED          = 403;
	public static final Integer INTERNAL_SERVER_ERROR = 500;

}
