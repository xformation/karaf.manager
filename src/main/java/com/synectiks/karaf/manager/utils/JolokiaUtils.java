package com.synectiks.karaf.manager.utils;

import java.util.HashMap;

import javax.management.MalformedObjectNameException;

import org.jolokia.client.BasicAuthenticator;
import org.jolokia.client.J4pClient;
import org.jolokia.client.exception.J4pException;
import org.jolokia.client.request.J4pExecRequest;
import org.jolokia.client.request.J4pExecResponse;
import org.jolokia.client.request.J4pListRequest;
import org.jolokia.client.request.J4pListResponse;
import org.jolokia.client.request.J4pReadRequest;
import org.jolokia.client.request.J4pReadResponse;
import org.jolokia.client.request.J4pSearchRequest;
import org.jolokia.client.request.J4pSearchResponse;
import org.jolokia.client.request.J4pVersionRequest;
import org.jolokia.client.request.J4pVersionResponse;
import org.jolokia.client.request.J4pWriteRequest;
import org.jolokia.client.request.J4pWriteResponse;
import org.json.simple.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.synectiks.commons.utils.IUtils;

public class JolokiaUtils {

	public static Logger logger = LoggerFactory.getLogger(JolokiaUtils.class);

	private static J4pClient j4pClient = J4pClient.url("http://localhost:8181/jolokia")
			.user("karaf").password("karaf")
			.authenticator(new BasicAuthenticator().preemptive())
			.connectionTimeout(3000)
			.build();

	@SuppressWarnings("unchecked")
	public static <V> V readRequest(String mbean, String... attribs)
			throws MalformedObjectNameException, J4pException {
		J4pReadRequest req = new J4pReadRequest(mbean, attribs);
		J4pReadResponse resp = j4pClient.execute(req);
		return (V) getResponseValue(resp.asJSONObject());
	}

	@SuppressWarnings("unchecked")
	public static <V> V writeRequest(String mbean, String attrib, Object value)
			throws MalformedObjectNameException, J4pException {
		J4pWriteRequest req = new J4pWriteRequest(mbean, attrib, value);
		J4pWriteResponse resp = j4pClient.execute(req);
		return (V) getResponseValue(resp.asJSONObject());
	}

	@SuppressWarnings("unchecked")
	public static <V> V execRequest(String mbean, String operation, Object... args)
			throws MalformedObjectNameException, J4pException {
		J4pExecRequest req = new J4pExecRequest(mbean, operation, args);
		J4pExecResponse resp = j4pClient.execute(req);
		return (V) getResponseValue(resp.asJSONObject());
	}

	@SuppressWarnings("unchecked")
	public static <V> V searchRequest(String mbeanRegex)
			throws MalformedObjectNameException, J4pException {
		J4pSearchRequest req = new J4pSearchRequest(mbeanRegex);
		J4pSearchResponse resp = j4pClient.execute(req);
		return (V) getResponseValue(resp.asJSONObject());
	}

	@SuppressWarnings("unchecked")
	public static <V> V listRequest(String path)
			throws MalformedObjectNameException, J4pException {
		J4pListRequest req = new J4pListRequest(path);
		J4pListResponse resp = j4pClient.execute(req);
		return (V) getResponseValue(resp.asJSONObject());
	}

	@SuppressWarnings("unchecked")
	public static <V> V versionRequest()
			throws MalformedObjectNameException, J4pException {
		J4pVersionRequest req = new J4pVersionRequest();
		J4pVersionResponse resp = j4pClient.execute(req);
		return (V) getResponseValue(resp.asJSONObject());
	}

	@SuppressWarnings("unchecked")
	public static Object getResponseValue(JSONObject json) {
		Object val = null;
		if (!IUtils.isNull(json)) {
			logger.info("Response status: " + json.getOrDefault("status", ""));
			logger.info("Response timestamp: " + json.getOrDefault("timestamp", ""));
			logger.info("Response request: " + json.getOrDefault("request", ""));
			logger.info("Response value: " + json.getOrDefault("value", ""));
			val = json.get("value");
		}
		return val;
	}

	public static void main(String[] args) {
		try {
			Long bndlId = execRequest("org.apache.karaf:type=bundle,name=root",
					"install(java.lang.String)", "mvn:com.synectiks.osgi/bundle/1.0.0-SNAPSHOT");
			logger.info("Write response: " + bndlId);
			String start = execRequest("org.apache.karaf:type=bundle,name=root",
					"start", bndlId.toString());
			logger.info("Write response: " + start);
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			Long stop = execRequest("org.apache.karaf:type=bundle,name=root",
					"stop", bndlId.toString());
			logger.info("Write response: " + stop);
			HashMap<String, Long> vals = readRequest("java.lang:type=Memory",
					"HeapMemoryUsage");
			long used = vals .get("used");
			long max = vals.get("max");
			int usage = (int) (used * 100 / max);
			logger.info("Memory usage: used: " + used + " / max: " + max + " = "
					+ usage + "%");
			Boolean verbose = writeRequest("java.lang:type=ClassLoading",
					"Verbose", "true");
			logger.info("Write response: " + verbose);
			Object obj = execRequest("java.lang:type=Memory", "gc");
			logger.info("exec loadUsers response: " + obj);
			obj = execRequest("java.lang:type=Threading",
					"dumpAllThreads", true, true);
			logger.info("exec dumpAllThreads response: " + obj);
			obj = searchRequest("java.lang:*");
			logger.info("exec search java response: " + obj);
			obj = searchRequest("*:j2eeType=J2EEServer,*");
			logger.info("exec search j2ee response: " + obj);
			obj = versionRequest();
			logger.info("version response: " + obj);
		} catch (MalformedObjectNameException | J4pException e) {
			e.printStackTrace();
		}
	}

}
