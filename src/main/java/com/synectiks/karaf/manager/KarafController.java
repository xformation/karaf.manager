/**
 * 
 */
package com.synectiks.karaf.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.synectiks.commons.utils.IUtils;
import com.synectiks.karaf.manager.utils.JolokiaUtils;

/**
 * @author Rajesh
 */
@RestController
@RequestMapping(path = "/karaf", method = RequestMethod.POST)
public class KarafController {

	private static Logger logger = LoggerFactory.getLogger(KarafController.class);

	/**
	 * Create a READ request to request one or more attributes from the remote j4p agent
	 * @param mbean  object name as sting which gets converted to a javax.management.ObjectName
	 * @param attribs zero, one or more attributes to request
	 * @return
	 */
	@RequestMapping(path = "/read", method = RequestMethod.GET)
	public ResponseEntity<Object> read(String mbean, String[] attribs) {
		String res = null;
		try {
			res = JolokiaUtils.readRequest(mbean, attribs);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * API to create write request
	 * @param mbean  MBean name which attribute should be set
	 * @param attrib  name of the attribute to set
	 * @param value new value
	 * @param path optional path for setting an inner value
	 * @return
	 */
	@RequestMapping(path = "/write", method = RequestMethod.GET)
	public ResponseEntity<Object> write(String mbean, String attrib, String value,
			@RequestParam(name = "path", required = false) String path) {
		String res = null;
		try {
			res = JolokiaUtils.writeRequest(mbean, attrib, value);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Api to create client request for executing a JMX operation
	 * @param mbean  name of the MBean to execute the request on
	 * @param operation operation to execute
	 * @param args any arguments to pass
	 *  (which must match the JMX operation's declared signature)
	 * @return
	 */
	@RequestMapping(path = "/execute", method = RequestMethod.GET)
	public ResponseEntity<Object> exec(String mbean, String operation, Object... args) {
		String res = null;
		try {
			res = JolokiaUtils.execRequest(mbean, operation, args);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Searches the mbeans with name regex
	 * @param mbeanRegex  MBean names pattern as string
	 * @return
	 */
	@RequestMapping(path = "/search", method = RequestMethod.GET)
	public ResponseEntity<Object> search(String mbeanRegex) {
		String res = null;
		try {
			res = JolokiaUtils.searchRequest(mbeanRegex);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * List all mbeans using a path to restrict the information returned by the list command
	 * @param path path into the JSON response.
	 * 	The path must already be properly escaped when it contains slashes or exclamation marks.
	 *  You can use escape(String) in order to escape a single path element.
	 * @return
	 */
	@RequestMapping(path = "/list", method = RequestMethod.GET)
	public ResponseEntity<Object> list(String path) {
		String res = null;
		try {
			res = JolokiaUtils.listRequest(path);
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}

	/**
	 * Create and run a version request
	 * @return
	 */
	@RequestMapping(path = "/version", method = RequestMethod.GET)
	public ResponseEntity<Object> version() {
		String res = null;
		try {
			res = JolokiaUtils.versionRequest();
		} catch (Throwable ex) {
			logger.error(ex.getMessage(), ex);
			return new ResponseEntity<>(IUtils.getFailedResponse(ex),
					HttpStatus.PRECONDITION_FAILED);
		}
		return new ResponseEntity<>(res, HttpStatus.OK);
	}
}
