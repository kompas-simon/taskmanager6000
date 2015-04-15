package taskmanager6000.client.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

import org.apache.log4j.Logger;

public class ParameterHelper {
	private static final Logger log = Logger.getLogger(ParameterHelper.class);
	
	public static void checkParamPresence(Map parameters, String param) throws InvalidRequestParameterException {
		if (!parameters.containsKey(param) || ((Object[])parameters.get(param)).length != 1) {
			throw new InvalidRequestParameterException(param);
		}
	}
	
	public static String getParameter(Map parameters, String param) {
		try {
			return URLDecoder.decode(((String[]) parameters.get(param))[0], "UTF-8");
		} catch (UnsupportedEncodingException e) {
			log.error(e);
		}
		return ((String[]) parameters.get(param))[0];
	}
}
