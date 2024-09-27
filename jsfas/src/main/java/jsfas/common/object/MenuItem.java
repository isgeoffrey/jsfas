package jsfas.common.object;

import java.util.List;

public class MenuItem {
	public static final int NORMAL = 0;
	public static final int SEPARATOR = 1;
	public static final int DROPDOWN_HEADER = 2;
	public static final int OUTSIDE_LINK = 3;
	
	public String id;
	public String name;
	public String restURI;
	public String fullUrl;
	public int type;
	public List<MenuItem> subMenuList;
	
	public MenuItem(String id, String name, String restURI) {
		this.id = id;
		this.name = name;
		this.restURI = restURI;
	}
	
	public MenuItem(String id, String name, int type) {
		this.id = id;
		this.name = name;
		this.type = type;
	}
	
	public MenuItem(String id, String name, int type, String fullUrl) {
		this.id = id;
		this.name = name;
		this.type = type;
		this.fullUrl = fullUrl;
	}
	
	public MenuItem(String id, String name, List<MenuItem> subMenuList) {
		this.id = id;
		this.name = name;
		this.subMenuList = subMenuList;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getRestURI() {
		return restURI;
	}
	public void setRestURI(String restURI) {
		this.restURI = restURI;
	}
	public String getFullUrl() {
		return fullUrl;
	}
	public void setFullUrl(String fullUrl) {
		this.fullUrl = fullUrl;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public List<MenuItem> getSubMenuList() {
		return subMenuList;
	}
	public void setSubMenuList(List<MenuItem> subMenuList) {
		this.subMenuList = subMenuList;
	}

}
