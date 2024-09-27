package jsfas.common;

import jsfas.common.utils.GeneralUtil;

/**
 * @author iswill
 * @since 17/7/2017
 * @see Class for Cache Version Constants
 */
public class CacheVersion {
	public static final String STATIC_CACHE_VERSION = GeneralUtil.getCurrentDateStr("T"); //auto refresh in each build/compile
	
	public String getStaticVer() {
		return STATIC_CACHE_VERSION;
	}
	
	public String getDynamicVer() {
		return GeneralUtil.getCurrentDateStr("T");
	}
}
