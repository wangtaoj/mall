package com.waston.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class JsonUtil {

	private static final Logger logger = LoggerFactory.getLogger(JsonUtil.class);
	
	private static ObjectMapper objectMapper = new ObjectMapper();

	private static final String pattern = "yyyy-MM-dd HH:mm:ss";

	static {
		//包含所有字段
		objectMapper.setSerializationInclusion(JsonInclude.Include.ALWAYS);

		//设置转换的日期格式, 默认是将时间转换为时间戳
		objectMapper.setDateFormat(new SimpleDateFormat(pattern));

		/*
		 * 在序列化一个空对象时(bean对象没有发现任何属性可以序列化)时不抛出异常
		 * 获取属性时根据getXXX方法的
		 * 1. 如果一个bean对象没有属性
		 * 2. bean对象有属性但是没有提供public的getXXX方法来获取属性值
		 * 不抛出异常, 而是返回json字符串{}
		 */
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);

		//忽略反序列化时在json字符串中存在，但是在java对象中不存在对应属性的情况。防止错误
		objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
	}
	
	/**
	 * 对象转json字符串
	 * @param object
	 * @return object为null时返回null
	 */
	public static String objectToJson(Object object){
		try {
			return objectMapper.writeValueAsString(object);
		} catch (JsonProcessingException e) {
			logger.error("parse object to json error", e);
		}
		return null;
	}
	
	/**
	 * json字符串转实体对象
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> T jsonToObject(String json,Class<T> cls){
		try {
			return objectMapper.readValue(json, cls);
		} catch (IOException e) {
			logger.error("parse json to object error", e);
		}
		return null;
	}
	
	/**
	 * json转List集合
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> List<T> jsonToList(String json,Class<T> cls){
		JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(List.class, cls);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			logger.error("parse json to object error", e);
		}
		return null;
	}
	
	/**
	 * json转Set集合
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> Set<T> jsonToSet(String json,Class<T> cls){
		JavaType javaType = objectMapper.getTypeFactory().constructCollectionType(Set.class, cls);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			logger.error("parse json to object error", e);
		}
		return null;
	}
	
	/**
	 * json转map
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> Map<String,T> jsonToMap(String json,Class<T> cls){
		//构造一个java类型Map<String,T>
		JavaType javaType = objectMapper.getTypeFactory().constructMapType(Map.class,String.class,cls);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			logger.error("parse json to object error", e);
		}
		return null;
	}
	
	/**
	 * 将json数据转为map结构 并且map结构中还包括容器类型
	 * @param json
	 * @param cls
	 * @return
	 */
	public static <T> Map<String,List<T>> jsonToMapList(String json,Class<T> cls){
		//将普通的java类型转为JavaType对象
		JavaType temp1 = objectMapper.getTypeFactory().constructType(String.class);
		//做一个List<泛型>的JavaType
		JavaType temp2 = objectMapper.getTypeFactory().constructCollectionType(List.class, cls);
		//做一个Map<String,List<T>>的javaType
		JavaType javaType = objectMapper.getTypeFactory().constructMapType(Map.class,temp1,temp2);
		try {
			return objectMapper.readValue(json, javaType);
		} catch (IOException e) {
			logger.error("parse json to object error", e);
		}
		return null;
	}
	
}
