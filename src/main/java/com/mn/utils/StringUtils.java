package com.mn.utils;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class StringUtils {

	// 定义script的正则表达式"<script[^>]*?>[\\s\\S]*?<\\/script>";
	private static final String regEx_script = "<[\\s]*?script[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?script[\\s]*?>";
	// 定义style的正则表达式"<style[^>]*?>[\\s\\S]*?<\\/style>";
	private static final String regEx_style = "<[\\s]*?style[^>]*?>[\\s\\S]*?<[\\s]*?\\/[\\s]*?style[\\s]*?>";

	private static final String regEx_html = "<[^>]+>"; // 定义HTML标签的正则表达式

	private static final String regEx_nbsp = "[&nbsp;|&quot;|&amp;|&lt;|&gt;]";// 定义去掉&nbsp;的正则表达式

	private static final String regEx_htmlSpace = "&[\\w]+;"; // 定义HTML标签的正则表达式

	private static final String regEx_space = "\\s*|\t|\r|\n"; // 定义HTML标签的正则表达式

	private static final String regEx_digit = "^\\d+$"; // 定义纯数字的正则表达式


	private static final String EMPTY_STRING = "";

	private static String lastId = "";
	private static String uuid = "";
	public static final String EMPTY = "";
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * 根据当前时间组合成一个30位的唯一字符串:year+month+day+hour+minute+second+000001+0+uuid(9位 )
	 *
	 * @return yyyyMMddhhmmss000001+0+uuid(9位)
	 */
	public synchronized static String getId() {
		String year = "";
		String month = "";
		String day = "";
		String hour = "";
		String minute = "";
		String second = "";
		Calendar calendar = Calendar.getInstance();
		switch (String.valueOf(calendar.get(Calendar.YEAR)).length()) {
			case 1:
				year = "000" + String.valueOf(calendar.get(Calendar.YEAR));
				break;
			case 2:
				year = "00" + String.valueOf(calendar.get(Calendar.YEAR));
				break;
			case 3:
				year = "0" + String.valueOf(calendar.get(Calendar.YEAR));
				break;
			default:
				year = String.valueOf(calendar.get(Calendar.YEAR));
				break;
		}
		month = (String.valueOf(calendar.get(Calendar.MONTH)).length() == 1 && calendar.get(Calendar.MONTH) != 9) ? ("0" + String
				.valueOf(calendar.get(Calendar.MONTH) + 1)) : String.valueOf(calendar.get(Calendar.MONTH) + 1);
		day = (String.valueOf(calendar.get(Calendar.DATE)).length() == 1) ? ("0" + String.valueOf(calendar
				.get(Calendar.DATE))) : String.valueOf(calendar.get(Calendar.DATE));
		hour = (String.valueOf(calendar.get(Calendar.HOUR_OF_DAY)).length() == 1) ? ("0" + String.valueOf(calendar
				.get(Calendar.HOUR_OF_DAY))) : String.valueOf(calendar.get(Calendar.HOUR_OF_DAY));
		minute = (String.valueOf(calendar.get(Calendar.MINUTE)).length() == 1) ? ("0" + String.valueOf(calendar
				.get(Calendar.MINUTE))) : String.valueOf(calendar.get(Calendar.MINUTE));
		second = (String.valueOf(calendar.get(Calendar.SECOND)).length() == 1) ? ("0" + String.valueOf(calendar
				.get(Calendar.SECOND))) : String.valueOf(calendar.get(Calendar.SECOND));
		String id = year + month + day + hour + minute + second;
		if (lastId.length() == 0) {
			lastId = id + "000001";
		} else if (id.substring(0, 14).equals(lastId.substring(0, 14)) == false) {
			lastId = id + "000001";
		} else {
			if (lastId.length() != 20) {
				lastId = id + "000001";
			} else {
				int m = Integer.valueOf(lastId.substring(14)).intValue() + 1;
				for (int i = 0; i < 6 - String.valueOf(m).length(); i++) {
					id = id + "0";
				}
				lastId = id + m;
			}
		}

		// 每次都生成新的UUID，以免服务器时间来回修改导致主键重复
		uuid = "";

		if (uuid.equals("")) {
			uuid = String.valueOf(UUID.randomUUID().hashCode());
			if (uuid.startsWith("-")) {
				uuid = uuid.substring(1);
			}
			int len = uuid.length();
			if (len > 9) {
				uuid = uuid.substring(len - 9);
			}
			len = uuid.length();
			for (int i = 10; i > len; i--) {
				uuid = "0" + uuid;
			}
		}

		return lastId + uuid;
	}

	/**
	 * 根据当前时间组合成一个36位的包含了分隔符的唯一字符串:uuid(36位 )
	 *
	 * @return uuid(36位)
	 */
	public synchronized static String getUID36() {
		// 每次都生成新的UUID，以免服务器时间来回修改导致主键重复
		uuid = "";
		uuid = String.valueOf(UUID.randomUUID());
		uuid = String.valueOf(UUID.randomUUID());
		return uuid.toUpperCase();
	}

	/**
	 * 根据当前时间组合成一个32位的去除了分隔符的唯一字符串:uuid(32位 )
	 *
	 * @return uuid(32位)
	 */
	public synchronized static String getUID32() {
		uuid = getUID36();
		uuid = valueOf(String.valueOf(uuid).split("-"), "");
		return uuid.toUpperCase();
	}

	/**
	 * 根据当前时间组合成一个46位的去除了分隔符且有日期时间字符串开头的唯一字符串:uuid(46位 )
	 *
	 * @return yyyyMMddHHmmss+uuid(32位)
	 */
	public synchronized static String getUID46() {
		// uuid = getUID36();
		// uuid = DateUtils.getDateTime14() + getUID32();//排除对DateUtils的依赖。
		try {
			DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
			uuid = df.format(new Date()) + getUID32();
		} catch (Exception ex) {
			uuid = "" + getUID32();
		}
		return uuid.toUpperCase();
	}


	final static String[] SHORT_UUID_TBL = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};

	public static String generateNumberUuid(int numLength) {
		StringBuffer shortBuffer = new StringBuffer();
		String uuid = getUID32();
		for (int i = 0; i < numLength; i++) {
			String str = uuid.substring(i * 4, i * 4 + 4);
			int x = Integer.parseInt(str, 16);
			shortBuffer.append(SHORT_UUID_TBL[x % 9]);
		}
		return shortBuffer.toString();
//		String uuid = UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 将对象转成字符串，为null时返回空字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String valueOf(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}

		return obj.toString();
	}

	/**
	 * 将对象转成字符串，为null时返回替代字符串
	 *
	 * @param obj
	 * @param replaceString
	 * @return
	 */
	public static String valueOf(Object obj, String replaceString) {
		if (obj == null) {
			return replaceString;
		}

		return obj.toString();
	}

	/**
	 * 根据字符列表生成指定格式的字符串
	 *
	 * @param list 字符数组
	 * @param sepa 分隔符
	 * @return
	 */
	public static String valueOf(List<String> list, String sepa) {
		if ((null == list) || (list.size() <= 0)) {
			return "";
		}
		String[] strings = list.toArray(new String[0]);
		return StringUtils.valueOf(strings, sepa);
	}

	/**
	 * 根据字符数组生成指定格式的字符串
	 *
	 * @param strings 字符数组
	 * @param sepa    分隔符
	 * @return
	 */
	public static String valueOf(String[] strings, String sepa) {

		sepa = trim(sepa);
		String str = EMPTY_STRING;
		if (null == strings || strings.length <= 0) {
			return EMPTY_STRING;
		}

		for (String el : strings) {
			str = str + (StringUtils.isNullorWhiteSpace(el) ? "" : el + sepa);
		}
		str = trim(str);
		if (!isNullorWhiteSpace(sepa)) {
			str = str.endsWith(sepa) ? str.substring(0, str.lastIndexOf(sepa)) : str;
		}
		return str;
	}

	/**
	 * 根据字符数组生成指定格式的字符串
	 *
	 * @param strings 字符数组
	 * @param prefix  前缀字符串
	 * @param sepa    分隔符
	 * @return
	 */
	public static String valueOf(String[] strings, String prefix, String sepa) {
		sepa = trim(sepa);
		prefix = trim(prefix);
		String str = EMPTY_STRING;

		str = valueOf(strings, sepa);
		str = isNullorWhiteSpace(str) ? trim(str) : prefix + trim(str);

		return str;
	}

	/**
	 * 根据字符数组生成指定格式的字符串
	 *
	 * @param strings 字符数组
	 * @param suffix  后缀字符串
	 * @param sepa    分隔符
	 * @return
	 */
	public static String valueOf(String suffix, String sepa, String[] strings) {
		sepa = trim(sepa);
		suffix = trim(suffix);
		String str = EMPTY_STRING;

		str = valueOf(strings, sepa);
		str = isNullorWhiteSpace(str) ? trim(str) : trim(str) + suffix;

		return str;
	}

	/**
	 * 将对象转成字符串并去除首尾空格，为null时返回空字符串
	 *
	 * @param obj
	 * @return
	 */
	public static String trim(Object obj) {
		if (obj == null) {
			return EMPTY_STRING;
		}

		return obj.toString().trim();
	}

	/**
	 * 去除字符串左边的空格，为null时返回空字符串
	 *
	 * @param str
	 * @return
	 */
	public static String ltrim(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		Pattern pattern = Pattern.compile("^[ |　|\\s]*", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(str);
		return matcher.replaceAll("");
	}

	/**
	 * 去除字符串右边的空格，为null时返回空字符串
	 *
	 * @param str
	 * @return
	 */
	public static String rtrim(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		Pattern pattern = Pattern.compile("[ |　|\\s]*$", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(str);
		return matcher.replaceAll("");
	}

	@SuppressWarnings({"rawtypes"})
	public static String getMapKeyVal(Map map, String key) {
		if (map == null)
			return "";
		if (key == null)
			return "";
		Object obj = map.get(key);
		String result;
		if (obj == null) {
			result = "";
		} else {
			result = obj.toString();
		}
		return result;
	}

	/**
	 * 首字符大写
	 *
	 * @param str
	 * @return
	 */
	public static String upperFirstChar(String str) {
		if (str == null || str.length() == 0) {
			return EMPTY_STRING;
		}

		return str.substring(0, 1).toUpperCase() + str.substring(1);
	}

	/**
	 * 首字符小写
	 *
	 * @param str
	 * @return
	 */
	public static String lowerFirstChar(String str) {
		if (str == null || str.equals("")) {
			return EMPTY_STRING;
		}

		return str.substring(0, 1).toLowerCase() + str.substring(1);
	}

	/**
	 * 按分隔符将字符串转换为字符串数组
	 *
	 * @param str       字符串
	 * @param separator 分隔符
	 * @return
	 */
	public static String[] getArrayFromString(String str, String separator) {
		if (str == null || str.equals("")) {
			return new String[0];
		}

		return str.split(separator);
	}

	/**
	 * 按分隔符将字符串数组转换为字符串
	 *
	 * @param strs      字符串数组
	 * @param separator 分隔符
	 * @return
	 */
	public static String getStringFromArray(String[] strs, String separator) {
		if (strs == null || strs.length == 0) {
			return EMPTY_STRING;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < strs.length; i++) {
			if (strs[i] == null) {
				sb.append(EMPTY_STRING);
			} else {
				sb.append(strs[i]);
			}

			if (i != strs.length - 1) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	/**
	 * 按分隔符将数组转换为字符串
	 *
	 * @param objs      字符串数组
	 * @param separator 分隔符
	 * @return
	 */
	public static String getStringFromArray(byte[] objs, String separator) {
		if (objs == null || objs.length == 0) {
			return EMPTY_STRING;
		}

		StringBuffer sb = new StringBuffer();

		for (int i = 0; i < objs.length; i++) {
			sb.append(objs[i]);

			if (i != objs.length - 1) {
				sb.append(separator);
			}
		}

		return sb.toString();
	}

	/**
	 * 按分隔符将字符串转换为字符串列表
	 *
	 * @param str       字符串
	 * @param separator 分隔符
	 * @return
	 */
	public static List<String> getListFromString(String str, String separator) {
		return Arrays.asList(getArrayFromString(str, separator));
	}

	/**
	 * 按分隔符将字符串列表转换为字符串
	 *
	 * @param list      字符串列表
	 * @param separator 分隔符
	 * @return
	 */
	public static String getStringFromList(List<String> list, String separator) {
		if (list == null || list.size() == 0) {
			return EMPTY_STRING;
		}

		return getStringFromArray((String[]) list.toArray(new String[list.size()]), separator);
	}

	/**
	 * 按分隔符将Map列表转换为字符串
	 *
	 * @param list      Map列表
	 * @param key       键值
	 * @param separator 分隔符
	 * @return 根据键值要求去Map中的值，再和分隔符拼接成字符串
	 */
	@SuppressWarnings("rawtypes")
	public static String getStringFromList(List<Map> list, String key, String separator) {
		if (list == null || list.size() == 0) {
			return EMPTY_STRING;
		}

		if (isNullorWhiteSpace(key)) {
			return EMPTY_STRING;
		}

		if (isNullorWhiteSpace(key)) {
			separator = EMPTY_STRING;
		}

		String str = EMPTY_STRING;
		for (int i = 0; i < list.size(); i++) {

			str = str + ((i <= 0) ? "" : separator) + trim(list.get(i).get(key));

		}

		return str;
	}

	/**
	 * 将Map列表转到字符列表
	 *
	 * @param list Map列表
	 * @param key  键值
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public static List<String> getStringListByMapList(List<Map> list, String key) {
		List<String> result = new ArrayList<String>();

		if (list == null || list.size() == 0) {
			return result;
		}

		if (isNullorWhiteSpace(key)) {
			return result;
		}

		String str = "";
		for (int i = 0; i < list.size(); i++) {

			str = StringUtils.trim(list.get(i).get(key));
			if (!isNullorWhiteSpace(str)) {
				result.add(str);
			}
		}

		return result;
	}

	public static Map<String, String> getMapByStringList(List<String> list, int len, String separator) {
		Map<String, String> result = new HashMap<String, String>();

		if (list == null || list.size() == 0) {
			return result;
		}

		int i = 0;
		int j = 0;
		String str = EMPTY_STRING;

		for (String el : list) {

			if (i < len) {
			} else {
				i = 0;
				j++;

				result.put("str" + j, str);
				str = "";
			}

			if (!isNullorWhiteSpace(el)) {
				str = str + ((i <= 0) ? "" : separator) + el;
				i++;
			}

		}

		j++;
		result.put("str" + j, str);

		list = null;
		return result;
	}

	@SuppressWarnings("rawtypes")
	public static StringBuffer getConditionStringByMap(Map<String, String> userNameMap, String columnName) {
		StringBuffer result = new StringBuffer();
		StringBuffer sql = new StringBuffer();

		if (null == userNameMap || userNameMap.size() <= 0) {
			return result;
		}

		Iterator iter = userNameMap.entrySet().iterator();
		int i = 0;
		while (iter.hasNext()) {
			Map.Entry entry = (Map.Entry) iter.next();
			String value = entry.getValue().toString();

			if (i >= 1) {
				sql.append(" OR \n");
			}
			sql.append(columnName).append(" IN ('").append(value).append("') \n");
			i++;
		}

		result.append("( \n").append(sql).append(" ) \n");

		return result;
	}

	// @SuppressWarnings("unchecked")
	// public static List<StringBuffer> getConditionListbyStringList(List<String> stringList, int maxPageSize,
	// int maxLineSize, String column_id) {
	//
	// List<StringBuffer> result = new ArrayList<StringBuffer>();
	//
	// if (null == stringList || stringList.size() <= 0) {
	// return result;
	// }
	//
	// PageList pageList = new PageList(stringList, 1, maxPageSize);
	//
	// int pageCount = pageList.getPageCount();
	// for (int i = 1; i <= pageCount; i++) {
	//
	// pageList = new PageList(stringList, i, maxPageSize);
	//
	// Map<String, String> map = StringUtils.getMapByStringList(pageList.getPage(), maxLineSize, "','");
	// StringBuffer userCondition = StringUtils.getConditionStringByMap(map, column_id);
	//
	// if (null != userCondition && userCondition.length() > 0) {
	// result.add(userCondition);
	// }
	// }
	//
	// stringList = null;
	// pageList = null;
	// return result;
	//
	// }

	/**
	 * 获取字符串字节长度
	 *
	 * @param str
	 * @return
	 */
	public static int getByteLength(String str) {
		if (str == null) {
			return 0;
		}

		return str.getBytes().length;
	}

	/**
	 * 根据字节长度从字符串中截取一段字符串
	 *
	 * @param str
	 * @param byteLength
	 * @return
	 */
	public static String getTextByByteLength(String str, int byteLength) {
		if (str == null) {
			return EMPTY_STRING;
		}

		String txt = "";
		for (int i = 0; i < str.length(); i++) {
			txt = txt + str.substring(i, i + 1);
			if (getByteLength(txt) > byteLength) {
				txt = txt.substring(0, i);
				break;
			}
		}

		return txt;
	}

	/**
	 * 获取汉字的拼音首字母
	 *
	 * @param str
	 * @return
	 */
	// public static String getFirstLetter(String str) {
	// if (str == null) {
	// return EMPTY_STRING;
	// }
	//
	// StringBuffer sb = new StringBuffer();
	// for (int j = 0; j < str.length(); j++) {
	// char word = str.charAt(j);
	// String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
	// if (pinyinArray != null) {
	// sb.append(pinyinArray[0].charAt(0));
	// } else {
	// sb.append(word);
	// }
	// }
	// return sb.toString();
	// }

	/**
	 * 获取汉字全拼
	 *
	 * @param str
	 * @return
	 */
	// public static String getPingYin(String str) {
	// if (str == null) {
	// return EMPTY_STRING;
	// }
	//
	// char[] chars = str.toCharArray();
	// String[] strs = new String[chars.length];
	// HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
	// hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	// hpof.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	// hpof.setVCharType(HanyuPinyinVCharType.WITH_V);
	// StringBuffer sb = new StringBuffer();
	// try {
	// for (int i = 0; i < chars.length; i++) {
	// // 判断是否为汉字字符
	// if (Character.toString(chars[i]).matches("[\\u4E00-\\u9FA5]+")) {
	// strs = PinyinHelper.toHanyuPinyinStringArray(chars[i], hpof);
	// sb.append(strs[0]);
	// } else {
	// sb.append(Character.toString(chars[i]));
	// }
	// }
	// return sb.toString();
	// } catch (BadHanyuPinyinOutputFormatCombination ex) {
	// return EMPTY_STRING;
	// }
	// }

	/**
	 * 获取汉字全拼
	 *
	 * @param str
	 *            汉字
	 * @param separator
	 *            分隔符
	 * @return
	 */
	// public static String getPingYin(String str, String separator) {
	// if (str == null) {
	// return EMPTY_STRING;
	// }
	//
	// char[] chars = str.toCharArray();
	// String[] strs = new String[chars.length];
	// HanyuPinyinOutputFormat hpof = new HanyuPinyinOutputFormat();
	// hpof.setCaseType(HanyuPinyinCaseType.LOWERCASE);
	// hpof.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
	// hpof.setVCharType(HanyuPinyinVCharType.WITH_V);
	// StringBuffer sb = new StringBuffer();
	// try {
	// for (int i = 0; i < chars.length; i++) {
	// // 判断是否为汉字字符
	// if (Character.toString(chars[i]).matches("[\\u4E00-\\u9FA5]+")) {
	// strs = PinyinHelper.toHanyuPinyinStringArray(chars[i], hpof);
	// sb.append(strs[0] + separator);
	// } else {
	// sb.append(Character.toString(chars[i]) + separator);
	// }
	// }
	//
	// return sb.toString().substring(0, sb.length() - separator.length());
	// } catch (BadHanyuPinyinOutputFormatCombination ex) {
	// return EMPTY_STRING;
	// }
	// }

	/**
	 * 文本串转换成Html格式字符串
	 *
	 * @param str
	 * @return
	 */
	public static String getHtmlFromText(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		str = str.replace("&", "&amp;");
		str = str.replace("<", "&lt;");
		str = str.replace(">", "&gt;");
		str = str.replace(" ", "&nbsp;");
		str = str.replace("\r\n", "<br />");
		str = str.replace("\r", "<br />");
		str = str.replace("\n", "<br />");

		return str;
	}

	/**
	 * Html格式字符串转换成文本串
	 *
	 * @param str
	 * @return
	 */
	public static String getTextFromHtml(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		// 删除脚本
		str = Pattern.compile("<script[^>]*?>.*?</script>", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
		// 删除样式
		str = Pattern.compile("<style[^>]*?>.*?</style>", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
		// 删除Html标记
		str = Pattern.compile("<(.[^>]*)>", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
		str = Pattern.compile("([\\r\\n])[\\s]+", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
		str = Pattern.compile("-->", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
		str = Pattern.compile("<!--.*", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");
		str = Pattern.compile("&(quot|#34);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("\"");
		str = Pattern.compile("&(amp|#38);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("&");
		str = Pattern.compile("&(lt|#60);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("<");
		str = Pattern.compile("&(gt|#62);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll(">");
		str = Pattern.compile("&(nbsp|#160);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll(" ");
		str = Pattern.compile("&(iexcl|#161);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("\\xa1");
		str = Pattern.compile("&(cent|#162);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("\\xa2");
		str = Pattern.compile("&(pound|#163);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("\\xa3");
		str = Pattern.compile("&(copy|#169);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("\\xa9");
		str = Pattern.compile("&#(\\d+);", Pattern.CASE_INSENSITIVE).matcher(str).replaceAll("");

		str = str.replace("<", "");
		str = str.replace(">", "");
		str = str.replace("\r\n", "");
		str = str.replace("\r", "");
		str = str.replace("\n", "");

		return str.trim();
	}

	private final static String[] hexMd5 = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e",
			"f"};

	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0)
			n = 256 + n;
		int d1 = n / 16;
		int d2 = n % 16;
		return hexMd5[d1] + hexMd5[d2];
	}

	private static String byteArrayToHexString(byte[] bytes) {
		StringBuffer resultSb = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			resultSb.append(byteToHexString(bytes[i]));
		}
		return resultSb.toString();
	}

	/**
	 * MD5加密
	 *
	 * @param str 被加密字符串
	 * @return MD5加密后的32位密文
	 */
	public static String md5(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException ex) {
			return EMPTY_STRING;
		}
		return byteArrayToHexString(md.digest(str.getBytes()));
	}

	/**
	 * SHA-1加密
	 *
	 * @param str 被加密字符串
	 * @return SHA-1加密后的40位密文
	 */
	public static String sha1(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		MessageDigest md;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException ex) {
			return EMPTY_STRING;
		}
		return byteArrayToHexString(md.digest(str.getBytes()));
	}

	private static final String[] hexEscape = {"00", "01", "02", "03", "04", "05", "06", "07", "08", "09", "0A", "0B",
			"0C", "0D", "0E", "0F", "10", "11", "12", "13", "14", "15", "16", "17", "18", "19", "1A", "1B", "1C", "1D",
			"1E", "1F", "20", "21", "22", "23", "24", "25", "26", "27", "28", "29", "2A", "2B", "2C", "2D", "2E", "2F",
			"30", "31", "32", "33", "34", "35", "36", "37", "38", "39", "3A", "3B", "3C", "3D", "3E", "3F", "40", "41",
			"42", "43", "44", "45", "46", "47", "48", "49", "4A", "4B", "4C", "4D", "4E", "4F", "50", "51", "52", "53",
			"54", "55", "56", "57", "58", "59", "5A", "5B", "5C", "5D", "5E", "5F", "60", "61", "62", "63", "64", "65",
			"66", "67", "68", "69", "6A", "6B", "6C", "6D", "6E", "6F", "70", "71", "72", "73", "74", "75", "76", "77",
			"78", "79", "7A", "7B", "7C", "7D", "7E", "7F", "80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
			"8A", "8B", "8C", "8D", "8E", "8F", "90", "91", "92", "93", "94", "95", "96", "97", "98", "99", "9A", "9B",
			"9C", "9D", "9E", "9F", "A0", "A1", "A2", "A3", "A4", "A5", "A6", "A7", "A8", "A9", "AA", "AB", "AC", "AD",
			"AE", "AF", "B0", "B1", "B2", "B3", "B4", "B5", "B6", "B7", "B8", "B9", "BA", "BB", "BC", "BD", "BE", "BF",
			"C0", "C1", "C2", "C3", "C4", "C5", "C6", "C7", "C8", "C9", "CA", "CB", "CC", "CD", "CE", "CF", "D0", "D1",
			"D2", "D3", "D4", "D5", "D6", "D7", "D8", "D9", "DA", "DB", "DC", "DD", "DE", "DF", "E0", "E1", "E2", "E3",
			"E4", "E5", "E6", "E7", "E8", "E9", "EA", "EB", "EC", "ED", "EE", "EF", "F0", "F1", "F2", "F3", "F4", "F5",
			"F6", "F7", "F8", "F9", "FA", "FB", "FC", "FD", "FE", "FF"};

	private static final byte[] valEscape = {0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x00, 0x01, 0x02, 0x03, 0x04, 0x05, 0x06, 0x07, 0x08, 0x09, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x0A,
			0x0B, 0x0C, 0x0D, 0x0E, 0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x0A, 0x0B, 0x0C, 0x0D, 0x0E,
			0x0F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F,
			0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F, 0x3F};

	/**
	 * 类js的escape编码，为null时返回空字符串
	 *
	 * @param str
	 * @return
	 */
	public static String escape(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		StringBuffer sb = new StringBuffer();
		int len = str.length();
		for (int i = 0; i < len; i++) {
			int ch = str.charAt(i);
			if (ch == ' ') {// space : map to '+'
				sb.append('+');
			} else if ('A' <= ch && ch <= 'Z') {// 'A'..'Z' : as it was
				sb.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {// 'a'..'z' : as it was
				sb.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {// '0'..'9' : as it was
				sb.append((char) ch);
			} else if (ch == '-' || ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')') {
				sb.append((char) ch);
			} else if (ch <= 0x007F) {// other ASCII : map to %XX
				sb.append('%');
				sb.append(hexEscape[ch]);
			} else {// unicode : map to %uXXXX
				sb.append('%');
				sb.append('u');
				sb.append(hexEscape[(ch >>> 8)]);
				sb.append(hexEscape[(0x00FF & ch)]);
			}
		}

		return sb.toString().replace("+", "%20");
	}

	/**
	 * 解码类js的escape编码的字符串，为null时返回空字符串
	 *
	 * @param str
	 * @return
	 */
	public static String unescape(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		StringBuffer sb = new StringBuffer();
		int i = 0;
		int len = str.length();
		while (i < len) {
			int ch = str.charAt(i);
			if (ch == '+') {// + : map to ' '
				sb.append(' ');
			} else if ('A' <= ch && ch <= 'Z') {// 'A'..'Z' : as it was
				sb.append((char) ch);
			} else if ('a' <= ch && ch <= 'z') {// 'a'..'z' : as it was
				sb.append((char) ch);
			} else if ('0' <= ch && ch <= '9') {// '0'..'9' : as it was
				sb.append((char) ch);
			} else if (ch == '-' || ch == '_' // unreserved : as it was
					|| ch == '.' || ch == '!' || ch == '~' || ch == '*' || ch == '\'' || ch == '(' || ch == ')') {
				sb.append((char) ch);
			} else if (ch == '%') {
				int cint = 0;
				if ('u' != str.charAt(i + 1)) { // %XX : map to ascii(XX)
					cint = (cint << 4) | valEscape[str.charAt(i + 1)];
					cint = (cint << 4) | valEscape[str.charAt(i + 2)];
					i += 2;
				} else {// %uXXXX : map to unicode(XXXX)
					cint = (cint << 4) | valEscape[str.charAt(i + 2)];
					cint = (cint << 4) | valEscape[str.charAt(i + 3)];
					cint = (cint << 4) | valEscape[str.charAt(i + 4)];
					cint = (cint << 4) | valEscape[str.charAt(i + 5)];
					i += 5;
				}
				sb.append((char) cint);
			}
			i++;
		}

		return sb.toString();
	}

	/**
	 * Base64编码
	 *
	 * @param str 被编码字符串
	 * @return
	 */
	public static String encodeBase64(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		return Base64.encode(str.getBytes());
	}

	/**
	 * Base64解码，解码失败返回空字符串
	 *
	 * @param str Base64编码后的字符串
	 * @return
	 */
	public static String decodeBase64(String str) {
		if (str == null) {
			return EMPTY_STRING;
		}

		try {
			return new String(Base64.decode(str));
		} catch (Exception ex) {
			return EMPTY_STRING;
		}
	}

	/**
	 * Url编码
	 *
	 * @param str 被编码字符串
	 * @return
	 */
	public static String encodeUrl(String str) {
		return encodeUrl(str, System.getProperty("file.encoding"));
	}

	/**
	 * Url编码，编码失败返回空字符串
	 *
	 * @param str    被编码字符串
	 * @param encode 编码：GBK、UTF-8
	 * @return
	 */
	public static String encodeUrl(String str, String encode) {
		if (str == null) {
			return EMPTY_STRING;
		}

		try {
			return URLEncoder.encode(str, encode);
		} catch (Exception ex) {
			return EMPTY_STRING;
		}
	}

	/**
	 * Url解码
	 *
	 * @param str 编码后的字符串
	 * @return
	 */
	public static String decodeUrl(String str) {
		return decodeUrl(str, System.getProperty("file.encoding"));
	}

	/**
	 * Url解码，解码失败返回空字符串
	 *
	 * @param str    编码后的字符串
	 * @param encode 编码：GBK、UTF-8
	 * @return
	 */
	public static String decodeUrl(String str, String encode) {
		if (str == null) {
			return EMPTY_STRING;
		}

		try {
			return URLDecoder.decode(str, encode);
		} catch (Exception ex) {
			return EMPTY_STRING;
		}
	}

	/**
	 * 编码转换，转换失败返回空字符串
	 *
	 * @param str        需要编码转换的字符串
	 * @param fromEncode 源编码
	 * @param toEncode   目的编码
	 * @return
	 */
	public static String convertEncode(String str, String fromEncode, String toEncode) {
		if (str == null || fromEncode == null || toEncode == null) {
			return EMPTY_STRING;
		}

		try {
			byte[] bytes = str.getBytes(fromEncode);
			str = new String(bytes, toEncode);
		} catch (Exception ex) {
			return EMPTY_STRING;
		}

		return str;
	}

	/**
	 * Des加密，加密码失败返回空字符串
	 *
	 * @param str
	 * @param key 8字节长
	 * @return
	 */
	public static String encodeDes(String str, String key) {
		try {
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

			return toHexString(cipher.doFinal(str.getBytes("UTF-8"))).toUpperCase();
		} catch (Exception ex) {
			return EMPTY_STRING;
		}
	}

	/**
	 * Des解密，解密失败返回空字符串
	 *
	 * @param str
	 * @param key 8字节长
	 * @return
	 */
	public static String decodeDes(String str, String key) {
		try {
			byte[] bytesrc = convertHexString(str);
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
			SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
			IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

			cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

			byte[] retByte = cipher.doFinal(bytesrc);
			return URLDecoder.decode(new String(retByte), "UTF-8");
		} catch (Exception ex) {
			return EMPTY_STRING;
		}
	}

	private static byte[] convertHexString(String ss) {
		byte digest[] = new byte[ss.length() / 2];
		for (int i = 0; i < digest.length; i++) {
			String byteString = ss.substring(2 * i, 2 * i + 2);
			int byteValue = Integer.parseInt(byteString, 16);
			digest[i] = (byte) byteValue;
		}

		return digest;
	}

	public static String transCoding2UTF8(String str) {
		try {
			return new String(str.getBytes("ISO-8859-1"), "utf-8");
		} catch (UnsupportedEncodingException e) {
			return str;
		}
	}

	private static String toHexString(byte b[]) {
		StringBuffer hexString = new StringBuffer();
		for (int i = 0; i < b.length; i++) {
			String plainText = Integer.toHexString(0xff & b[i]);
			if (plainText.length() < 2)
				plainText = "0" + plainText;
			hexString.append(plainText);
		}

		return hexString.toString();
	}

	/**
	 * 过滤sql参数传值，防止注入攻击，主要用于自定义查询参数中有不确定的传值
	 *
	 * @param value
	 * @return
	 */
	public static String filterSqlValue(String value) {
		if (isNotBlank(value)) {
			return value.replace("'", "''");
		}
		return value;
	}

	/**
	 * <p>
	 * 检查字符串是 empty ("") 或 null.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 * <p/>
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the String. That functionality is available in
	 * isBlank().
	 * </p>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isNullorEmpty(String str) {
		if ((null == str) || ("".equals(str))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * <p>
	 * 检查字符串是whitespace(空白), empty ("") 或 null.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param strs the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 * @since 2.0
	 */
	public static boolean isNullorWhiteSpace(String... strs) {
		if (strs == null || strs.length == 0) {
			return true;
		}
		for (int count = 0, size = strs.length; count < size; count++) {
			String str = strs[count];
			if ((null == str) || ("".equals(trim(str)))) {
				return true;
			}
		}
		return false;
	}

	/**
	 * <p>
	 * Checks if a String is empty ("") or null.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.isEmpty(null)      = true
	 * StringUtils.isEmpty("")        = true
	 * StringUtils.isEmpty(" ")       = false
	 * StringUtils.isEmpty("bob")     = false
	 * StringUtils.isEmpty("  bob  ") = false
	 * </pre>
	 * <p/>
	 * <p>
	 * NOTE: This method changed in Lang version 2.0. It no longer trims the String. That functionality is available in
	 * isBlank().
	 * </p>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is empty or null
	 */
	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	/**
	 * <p>
	 * Checks if a String is not empty ("") and not null.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.isNotEmpty(null)      = false
	 * StringUtils.isNotEmpty("")        = false
	 * StringUtils.isNotEmpty(" ")       = true
	 * StringUtils.isNotEmpty("bob")     = true
	 * StringUtils.isNotEmpty("  bob  ") = true
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null
	 */
	public static boolean isNotEmpty(String str) {
		return !StringUtils.isEmpty(str);
	}

	/**
	 * <p>
	 * Checks if a String is whitespace, empty ("") or null.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.isBlank(null)      = true
	 * StringUtils.isBlank("")        = true
	 * StringUtils.isBlank(" ")       = true
	 * StringUtils.isBlank("bob")     = false
	 * StringUtils.isBlank("  bob  ") = false
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is null, empty or whitespace
	 * @since 2.0
	 */
	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * <p>
	 * Checks if a String is not empty (""), not null and not whitespace only.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.isNotBlank(null)      = false
	 * StringUtils.isNotBlank("")        = false
	 * StringUtils.isNotBlank(" ")       = false
	 * StringUtils.isNotBlank("bob")     = true
	 * StringUtils.isNotBlank("  bob  ") = true
	 * </pre>
	 *
	 * @param str the String to check, may be null
	 * @return <code>true</code> if the String is not empty and not null and not whitespace
	 * @since 2.0
	 */
	public static boolean isNotBlank(String str) {
		return !StringUtils.isBlank(str);
	}

	/**
	 * 检查列表中的元素是否是关键字的一部分
	 *
	 * @param keyWord 比较的关键字
	 * @param list    字符串列表
	 * @return
	 */
	public static Boolean isKeywordContainInList(String keyWord, List<String> list) {
		if (StringUtils.isNullorWhiteSpace(keyWord)) {
			return false;
		}

		if ((null == list) || list.size() <= 0) {
			return true;
		}

		for (int i = 0; i < list.size(); i++) {
			if (keyWord.indexOf(list.get(i)) != -1) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查列表中的元素是否是关键字的结尾
	 *
	 * @param keyWord 比较的关键字
	 * @param list    字符串列表
	 * @return
	 */
	public static Boolean isKeywordEndsWithList(String keyWord, List<String> list) {
		if (StringUtils.isNullorWhiteSpace(keyWord)) {
			return false;
		}

		if ((null == list) || list.size() <= 0) {
			return true;
		}

		for (int i = 0; i < list.size(); i++) {
			if (keyWord.endsWith(list.get(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * 检查列表中的元素是否与关键字相等
	 *
	 * @param keyWord 比较的关键字
	 * @param list    字符串列表
	 * @return
	 */
	public static Boolean isKeywordEqualsInList(String keyWord, List<String> list) {
		if (StringUtils.isNullorWhiteSpace(keyWord)) {
			return false;
		}

		if ((null == list) || list.size() <= 0) {
			return true;
		}

		for (int i = 0; i < list.size(); i++) {
			if (keyWord.equals(list.get(i))) {
				return true;
			}
		}

		return false;
	}

	/**
	 * <p>
	 * Gets a substring from the specified String avoiding exceptions.
	 * </p>
	 * <p/>
	 * <p>
	 * A negative start position can be used to start <code>n</code> characters from the end of the String.
	 * </p>
	 * <p/>
	 * <p>
	 * A <code>null</code> String will return <code>null</code>. An empty ("") String will return "".
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.substring(null, *)   = null
	 * StringUtils.substring("", *)     = ""
	 * StringUtils.substring("abc", 0)  = "abc"
	 * StringUtils.substring("abc", 2)  = "c"
	 * StringUtils.substring("abc", 4)  = ""
	 * StringUtils.substring("abc", -2) = "bc"
	 * StringUtils.substring("abc", -4) = "abc"
	 * </pre>
	 *
	 * @param str   the String to get the substring from, may be null
	 * @param start the position to start from, negative means count back from the end of the String by this many
	 *              characters
	 * @return substring from start position, <code>null</code> if null String input
	 */
	public static String substring(String str, int start) {
		if (str == null) {
			return null;
		}

		// handle negatives, which means last n characters
		if (start < 0) {
			start = str.length() + start; // remember start is negative
		}

		if (start < 0) {
			start = 0;
		}
		if (start > str.length()) {
			return EMPTY;
		}

		return str.substring(start);
	}

	/**
	 * <p>
	 * Gets the substring before the first occurrence of a separator. The separator is not returned.
	 * </p>
	 * <p/>
	 * <p>
	 * A <code>null</code> string input will return <code>null</code>. An empty ("") string input will return the empty
	 * string. A <code>null</code> separator will return the input string.
	 * </p>
	 * <p/>
	 * <p>
	 * If nothing is found, the string input is returned.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.substringBefore(null, *)      = null
	 * StringUtils.substringBefore("", *)        = ""
	 * StringUtils.substringBefore("abc", "a")   = ""
	 * StringUtils.substringBefore("abcba", "b") = "a"
	 * StringUtils.substringBefore("abc", "c")   = "ab"
	 * StringUtils.substringBefore("abc", "d")   = "abc"
	 * StringUtils.substringBefore("abc", "")    = ""
	 * StringUtils.substringBefore("abc", null)  = "abc"
	 * </pre>
	 *
	 * @param str       the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring before the first occurrence of the separator, <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String substringBefore(String str, String separator) {
		if (isEmpty(str) || separator == null) {
			return str;
		}
		if (separator.length() == 0) {
			return EMPTY;
		}
		int pos = str.indexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * <p>
	 * Gets the substring before the last occurrence of a separator. The separator is not returned.
	 * </p>
	 * <p/>
	 * <p>
	 * A <code>null</code> string input will return <code>null</code>. An empty ("") string input will return the empty
	 * string. An empty or <code>null</code> separator will return the input string.
	 * </p>
	 * <p/>
	 * <p>
	 * If nothing is found, the string input is returned.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.substringBeforeLast(null, *)      = null
	 * StringUtils.substringBeforeLast("", *)        = ""
	 * StringUtils.substringBeforeLast("abcba", "b") = "abc"
	 * StringUtils.substringBeforeLast("abc", "c")   = "ab"
	 * StringUtils.substringBeforeLast("a", "a")     = ""
	 * StringUtils.substringBeforeLast("a", "z")     = "a"
	 * StringUtils.substringBeforeLast("a", null)    = "a"
	 * StringUtils.substringBeforeLast("a", "")      = "a"
	 * </pre>
	 *
	 * @param str       the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring before the last occurrence of the separator, <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String substringBeforeLast(String str, String separator) {
		if (isEmpty(str) || isEmpty(separator)) {
			return str;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND) {
			return str;
		}
		return str.substring(0, pos);
	}

	/**
	 * <p>
	 * Gets the substring after the last occurrence of a separator. The separator is not returned.
	 * </p>
	 * <p/>
	 * <p>
	 * A <code>null</code> string input will return <code>null</code>. An empty ("") string input will return the empty
	 * string. An empty or <code>null</code> separator will return the empty string if the input string is not
	 * <code>null</code>.
	 * </p>
	 * <p/>
	 * <p>
	 * If nothing is found, the empty string is returned.
	 * </p>
	 * <p/>
	 * <pre>
	 * StringUtils.substringAfterLast(null, *)      = null
	 * StringUtils.substringAfterLast("", *)        = ""
	 * StringUtils.substringAfterLast(*, "")        = ""
	 * StringUtils.substringAfterLast(*, null)      = ""
	 * StringUtils.substringAfterLast("abc", "a")   = "bc"
	 * StringUtils.substringAfterLast("abcba", "b") = "a"
	 * StringUtils.substringAfterLast("abc", "c")   = ""
	 * StringUtils.substringAfterLast("a", "a")     = ""
	 * StringUtils.substringAfterLast("a", "z")     = ""
	 * </pre>
	 *
	 * @param str       the String to get a substring from, may be null
	 * @param separator the String to search for, may be null
	 * @return the substring after the last occurrence of the separator, <code>null</code> if null String input
	 * @since 2.0
	 */
	public static String substringAfterLast(String str, String separator) {
		if (isEmpty(str)) {
			return str;
		}
		if (isEmpty(separator)) {
			return EMPTY;
		}
		int pos = str.lastIndexOf(separator);
		if (pos == INDEX_NOT_FOUND || pos == (str.length() - separator.length())) {
			return EMPTY;
		}
		return str.substring(pos + separator.length());
	}

	@SuppressWarnings({"rawtypes"})
	public static StringBuilder getSqlCondition(Map<String, String> map) {
		Set<String> key = map.keySet();
		StringBuilder sb = new StringBuilder();
		for (Iterator it = key.iterator(); it.hasNext(); ) {
			String s = (String) it.next();
			sb.append(" and ");
			sb.append(s);
			sb.append(" like " + "'%" + map.get(s) + "%' ");
			// System.out.println(map.get(s));
		}
		return sb;
	}

	public static String PadL(String str, char chr, int len) {

		return str;
	}

	/**
	 * 在指定字符串的左侧增加指定数量的‘0’字符
	 *
	 * @param str 指定字符串
	 * @param len 指定长度
	 * @return
	 */
	public static String PadL4Zero(String str, int len) {

		String s_jtlx = StringUtils.trim(str);
		try {
			if ((!"".equals(s_jtlx)) && (s_jtlx.length() < len)) {
				String len_str = "%0" + len + "d";
				s_jtlx = String.format(len_str, Integer.valueOf(s_jtlx));
				// s_jtlx = String.format("%04d", Integer.valueOf(s_jtlx));

			}
		} catch (Exception e) {
		}
		return s_jtlx;
	}

	@SuppressWarnings("unused")
	public static String trimPoint(String str) {
		if (isNullorWhiteSpace(str)) {
			return EMPTY_STRING;
		}
		String[] strs = str.split("\\.");
		str = valueOf(str.split("\\."), ".");

		return str;
	}


	public static String html2PureText(String html) {
		if (html == null) {
			html = "";
		}

		if (html.length() == 0) {
			return "";
		}

		String result = "";
		Pattern pattern = null;
		Matcher matcher = null;

		try {

			result = html2Text(html);

			pattern = Pattern.compile(regEx_space, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(result);
			result = matcher.replaceAll("");

			return result;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return result;
	}

	public static String html2Text(String html) {
		if (html == null) {
			html = "";
		}

		if (html.length() == 0) {
			return "";
		}

		String result = "";
		Pattern pattern = null;
		Matcher matcher = null;

		try {

			pattern = Pattern.compile(regEx_script, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(html);
			result = matcher.replaceAll("");

			pattern = Pattern.compile(regEx_style, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(result);
			result = matcher.replaceAll("");

			pattern = Pattern.compile(regEx_html, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(result);
			result = matcher.replaceAll("");

			pattern = Pattern.compile(regEx_nbsp, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(result);
			result = matcher.replaceAll("");

			pattern = Pattern.compile(regEx_htmlSpace, Pattern.CASE_INSENSITIVE);
			matcher = pattern.matcher(result);
			result = matcher.replaceAll("");

			return result;
		} catch (Exception e) {
			System.err.println("Html2Text: " + e.getMessage());
		}

		return result;
	}

	public static boolean isDigit(String str) {
		return str.matches(regEx_digit);
	}

	public static String jsonpString(String callbackFuncName, String realDataJson) {
		return new StringBuilder().append(callbackFuncName).append("(").append(realDataJson).append(")").toString();
	}
}