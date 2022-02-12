package com.frostsowner.magic.weather.event;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Attributes{

	private Map<Object, Object> attrs;
	
	@SuppressWarnings("unchecked")
	public <T> T getAttr(Object key, Class<T> clazz) {
		return (T) getAttr(key);
	}
	
	public String getAttrStr(Object key){
		Object value = getAttr(key);
		if(value!=null) return value.toString();
		else return null;
	}
	
	public byte getAttrByte(Object key){
		return Double.valueOf(getAttrStr(key)).byteValue();
	}
	
	public short getAttrShort(Object key){
		return Double.valueOf(getAttrStr(key)).shortValue();
	}
	
	public int getAttrInt(Object key){
		return Double.valueOf(getAttrStr(key)).intValue();
	}
	
	public long getAttrLong(Object key){
		return Double.valueOf(getAttrStr(key)).longValue();
	}
	
	public float getAttrFloat(Object key){
		return Double.valueOf(getAttrStr(key)).floatValue();
	}
	
	public double getAttrDouble(Object key){
		return Double.parseDouble(getAttrStr(key));
	}

	public boolean getAttrBoolean(Object key){
		return Boolean.parseBoolean(getAttrStr(key));
	}

	public Object getAttr(Object key) {
		if (attrs == null)
			return null;
		return attrs.get(key);
	}

	public void setAttr(Object key, Object value) {
		if(key==null) return;
		if (attrs == null)
			attrs = new ConcurrentHashMap<Object, Object>();
		if(value==null) attrs.remove(key);
		else attrs.put(key, value);
	}

	@SuppressWarnings("unchecked")
	public Map<Object, Object> getAttrs() {
		if(attrs==null) return Collections.EMPTY_MAP;
		return Collections.unmodifiableMap(attrs);
	}

	public void setAttrs(Map<Object, Object> attrs) {
		this.attrs = new ConcurrentHashMap<Object, Object>(attrs);
	}
}
