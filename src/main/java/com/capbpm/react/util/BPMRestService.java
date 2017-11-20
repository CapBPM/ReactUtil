package com.capbpm.react.util;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.parser.JSONParser;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import com.capbpm.react.data.Activity;
import com.capbpm.react.data.BPD;
import com.capbpm.react.data.Credential;
import com.capbpm.react.data.Model;
import com.capbpm.react.data.Server;;

public class BPMRestService {
	public static void mainX(String args[]) throws Exception {
		Server s = new Server();
		s.setPassword("passw0rd");
		s.setUserId("Danny");
		s.setPort(9443);
		s.setServer("169.48.72.245");
		s.setUsername("Danny");
		Credential c = new Credential(s);
		List<BPD> bpds = getAllProcesses(c);

		int ind = 0;
		for (BPD currentBPD : bpds) {
			getInputsAndActivities(c, currentBPD.getItemID(), currentBPD.getSnapshotID(), currentBPD);
			System.out.println(ind + " of " + bpds.size());
			System.out.println(ind + ":" + currentBPD.getProcessAppName() + ":" + currentBPD.getName());
			System.out.println(currentBPD);
			ind++;
		}

		System.out.println(bpds.size());

	}

	public static void main(String args[]) throws Exception {
		Server s = new Server();
		s.setPassword("passw0rd");
		s.setUserId("Danny");
		s.setPort(9443);
		s.setServer("169.48.72.245");
		s.setUsername("Danny");

		Credential c = new Credential(s);
		String pid = "25.22fd5444-f579-4a2e-9e4f-51e8537a8245";
		String sid = "2064.d38174ae-5453-4e1b-b002-a2879578ce28";
		String serviceId = "1.50c47237-1732-4329-92f8-4a8e08ff3db1";

		getActivityDataFromService(c,serviceId,sid);
		BPD bpd = new BPD();
		getInputsAndActivities(c, pid, sid, bpd);
		// System.out.println(bpd);
		getS(c, serviceId, sid);
		// getA(c,pid,sid);
	}

	public static void getA(Credential c, String pid, String sid) throws Exception {

		BPMRestService b = new BPMRestService(c);
		String url = getURLForBPDVisualData(pid, sid);

		String str = b.GET(url);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = (org.json.simple.JSONObject) json.get("data");
		org.json.simple.JSONArray items = (org.json.simple.JSONArray) data.get("items");
		for (int i = 0; i < items.size(); i++) {
			org.json.simple.JSONObject currentItem = (org.json.simple.JSONObject) items.get(i);
			System.out.println(currentItem.get("poType"));
			System.out.println(currentItem.get("poId"));

		}
	}

	public static void getActivityDataFromService(Credential c, String serviceId, String sid) throws Exception {

		BPMRestService b = new BPMRestService(c);
		String url = getURLForServiceModel(serviceId, sid);

		String str = b.GET(url);
		writeToFile("C:\\_backup\\service.json",str);
		
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = (org.json.simple.JSONObject) json.get("data");
		org.json.simple.JSONArray items = (org.json.simple.JSONArray) data.get("items");

		for (int i = 0; i < items.size(); i++) {
			org.json.simple.JSONObject currentItem = (org.json.simple.JSONObject) items.get(i);
			System.out.println(currentItem.get("poType"));
			System.out.println(currentItem.get("poId"));

		}
	}

	private static void writeToFile(String filePath, String str) throws FileNotFoundException, IOException {
		File f = new File(filePath);
		FileOutputStream fos = new FileOutputStream(f);
		fos.write(str.getBytes());
		fos.flush();
		fos.close();
	}

	//
	public static void getS(Credential c, String serviceId, String sid) throws Exception {

		BPMRestService b = new BPMRestService(c);
		String url = getURLForServiceModel(serviceId, sid);

		String str = b.GET(url);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json =  (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = JSONUtil.findChild(json,"data");//(org.json.simple.JSONObject) json.get("data");
		
		org.json.simple.JSONObject dm =JSONUtil.findChild(data,"DataModel");
		org.json.simple.JSONObject validation  =JSONUtil.findChild(dm,"validation");
		
		// get the inputs
		org.json.simple.JSONObject inputs = JSONUtil.findChild(dm, "inputs");
		// get the outputs
		org.json.simple.JSONObject outputs =JSONUtil.findChild(dm,"outputs");

		List<Model> inputz = getInputData(validation, inputs);
		List<Model> outputz = getOutputData(validation, outputs);
	}

	public static List<BPD> getAllProcesses(Credential c) throws Exception {
		List<BPD> retval = new ArrayList<BPD>();

		BPMRestService b = new BPMRestService(c);
		String url = getURLForForAllProcesses();

		String str = b.GET(url);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = (org.json.simple.JSONObject) json.get("data");
		// org.json.simple.JSONObject tmp = (org.json.simple.JSONObject)
		// data.get("exposedItemsList");

		org.json.simple.JSONArray exposedItemsList = (org.json.simple.JSONArray) data.get("exposedItemsList");

		for (int i = 0; i < exposedItemsList.size(); i++) {
			org.json.simple.JSONObject currentItem = (org.json.simple.JSONObject) exposedItemsList.get(i);
			BPD currentBPD = new BPD();
			String t = currentItem.get("type").toString();
			if (t.equals("process")) {
				currentBPD.setType(t);
				currentBPD.setName(getValFromJSON(currentItem, "display"));
				currentBPD.setItemID(getValFromJSON(currentItem, "itemID"));
				currentBPD.setProcessAppID(getValFromJSON(currentItem, "processAppID"));
				currentBPD.setProcessAppName(getValFromJSON(currentItem, "processAppName"));
				currentBPD.setProcessAppAcronym(getValFromJSON(currentItem, "processAppAcronym"));
				currentBPD.setSnapshotID(getValFromJSON(currentItem, "snapshotID"));
				currentBPD.setDisplay(getValFromJSON(currentItem, "display"));
				currentBPD.setBranchID(getValFromJSON(currentItem, "branchID"));
				currentBPD.setBranchName(getValFromJSON(currentItem, "branchName"));
				currentBPD.setStartURL(getValFromJSON(currentItem, "startURL"));
				retval.add(currentBPD);
			}

		}

		return retval;

	}

	private static String getValFromJSON(org.json.simple.JSONObject currentItem, String key) {
		String retval = "";
		try {
			retval = currentItem.get(key).toString();
		} catch (Exception e) {

		}

		return retval;

	}

	public static BPD getInputsAndActivities(Credential c, String pid, String sid, BPD bpd) throws Exception {
		String tmp = c.getBaseUrl();

		BPMRestService b = new BPMRestService(c);
		String url = getURLForBPDData(pid, sid);

		String str = b.GET(url);
		writeToFile("c:\\backup\\process.json", str);
		JSONParser parser = new JSONParser();
		org.json.simple.JSONObject json = (org.json.simple.JSONObject) parser.parse(str);
		org.json.simple.JSONObject data = (org.json.simple.JSONObject) json.get("data");
		org.json.simple.JSONObject header = (org.json.simple.JSONObject) data.get("Header");
		org.json.simple.JSONObject dm = (org.json.simple.JSONObject) data.get("DataModel");
		org.json.simple.JSONObject validation = (org.json.simple.JSONObject) dm.get("validation");
		org.json.simple.JSONObject Diagram = (org.json.simple.JSONObject) data.get("Diagram");
		org.json.simple.JSONArray steps = (org.json.simple.JSONArray) Diagram.get("step");

		List<Activity> activities = getActivityData(steps);

		// get the inputs
		org.json.simple.JSONObject inputs = (org.json.simple.JSONObject) dm.get("inputs");

		List<Model> inputz = getInputData(validation, inputs);

		// get the outputs
		org.json.simple.JSONObject outputs = (org.json.simple.JSONObject) dm.get("outputs");
		List<Model> outputz = getOutputData(validation, outputs);

		getProcessData(bpd, header, activities, inputz, outputz);

		return bpd;

	}

	private static void getProcessData(BPD bpd, org.json.simple.JSONObject header, List<Activity> activities,
			List<Model> inputz, List<Model> outputz) {
		getProcessData(bpd, bpd.getItemID(), bpd.getSnapshotID(), header, activities, inputz, outputz);
	}

	private static void getProcessData(BPD bpd, String pid, String sid, org.json.simple.JSONObject header,
			List<Activity> activities, List<Model> inputz, List<Model> outputz) {
		bpd.setActivities(activities);
		bpd.setInputs(inputz);
		bpd.setOutputs(outputz);
		bpd.setName(header.get("name").toString());
		bpd.setItemID(pid);
		bpd.setSnapshotID(sid);
	}

	private static List<Model> getOutputData(org.json.simple.JSONObject validation, org.json.simple.JSONObject outputs) {
		List<Model> outputz = new ArrayList<Model>();
		if (outputs==null )
		{
			return outputz;
		}
		Iterator<String> it = outputs.keySet().iterator();

		
		while (it.hasNext()) {
			String keyName = it.next();

			try {
				String t = ((org.json.simple.JSONObject) outputs.get(keyName)).get("type").toString();

				org.json.simple.JSONObject curVar = (org.json.simple.JSONObject) validation.get(t);

				Model output = new Model();

				output.setLabel(keyName);
				output.setType(t);
				output.setName(t);
				output.setOrder(outputz.size());
				output.setOid(curVar.get("ID").toString());
				outputz.add(output);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return outputz;
	}

	private static List<Model> getInputData(org.json.simple.JSONObject validation, org.json.simple.JSONObject inputs) {
		List<Model> inputz = new ArrayList<Model>();
		if (inputs==null)
		{
			return inputz;
		}
		Iterator<String> it = inputs.keySet().iterator();
		
		while (it.hasNext()) {
			String keyName = it.next();
			try {
				String t = ((org.json.simple.JSONObject) inputs.get(keyName)).get("type").toString();

				org.json.simple.JSONObject curVar = (org.json.simple.JSONObject) validation.get(t);

				Model input = new Model();

				input.setLabel(keyName);
				input.setType(t);
				input.setName(t);
				input.setOrder(inputz.size());
				if (curVar != null && curVar.get("ID") != null) {
					input.setOid(curVar.get("ID").toString());
				}
				inputz.add(input);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}
		}
		return inputz;
	}

	private static List<Activity> getActivityDataV(org.json.simple.JSONArray steps) {
		// Get all the activties
		List<Activity> activities = new ArrayList<Activity>();
		Iterator<org.json.simple.JSONObject> itz = steps.iterator();
		while (itz.hasNext()) {

			try {

				org.json.simple.JSONObject curr = itz.next();
				// System.out.println(curr);
				String name = curr.get("label").toString();
				String type = curr.get("type").toString();
				String lane = curr.get("lane").toString();
				String id = curr.get("ID").toString();
				String serviceType = curr.get("poType").toString();
				String serviceId = curr.get("poId").toString();
				String category = curr.get("category").toString();
				int x = Integer.parseInt(curr.get("x").toString());
				int y = Integer.parseInt(curr.get("y").toString());

				//
				if (type.equals("activity")) {
					Activity a = new Activity();
					a.setName(name);
					a.setLane(lane);
					a.setId(id);
					a.setX(x);
					a.setY(y);
					a.calcOrder();

					// dfgfg

					activities.add(a);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		}
		return activities;
	}

	private static List<Activity> getActivityData(org.json.simple.JSONArray steps) {
		// Get all the activties
		List<Activity> activities = new ArrayList<Activity>();
		Iterator<org.json.simple.JSONObject> itz = steps.iterator();
		while (itz.hasNext()) {

			try {

				org.json.simple.JSONObject curr = itz.next();
				// System.out.println(curr);
				String name = curr.get("name").toString();
				String type = curr.get("type").toString();
				String lane = curr.get("lane").toString();
				String id = curr.get("ID").toString();
				int x = Integer.parseInt(curr.get("x").toString());
				int y = Integer.parseInt(curr.get("y").toString());
				// System.out.println(name+":"+type);
				if (type.equals("activity")) {
					Activity a = new Activity();
					a.setName(name);
					a.setLane(lane);
					a.setId(id);
					a.setX(x);
					a.setY(y);
					a.calcOrder();

					// dfgfg

					activities.add(a);
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				// e.printStackTrace();
			}

		}
		return activities;
	}

	private static String getURLForForAllProcesses() {
		return "/rest/bpm/wle/v1/exposed/process";

	}

	private static String getURLForBPDData(String pid, String sid) {
		return "/rest/bpm/wle/v1/processModel/" + pid + "?snapshotId=" + sid + "&parts=all";

		// return
		// "/rest/bpm/wle/v1/processModel/25.22fd5444-f579-4a2e-9e4f-51e8537a8245?snapshotId=2064.d38174ae-5453-4e1b-b002-a2879578ce28&parts=all";
	}

	private static String getURLForBPDVisualData(String pid, String sid) {
		return "/rest/bpm/wle/v1/visual/processModel/" + pid + "?snapshotId=" + sid + "&parts=all";
		// https://169.48.72.245:9443/rest/bpm/wle/v1/visual/processModel/25.22fd5444-f579-4a2e-9e4f-51e8537a8245?snapshotId=2064.d38174ae-5453-4e1b-b002-a2879578ce28
	}

	private static String getURLForServiceModel(String serviceId, String sid) {
		return "/rest/bpm/wle/v1/visual/serviceModel/" + serviceId + "?snapshotId=" + sid;
	}

	final Credential credential;

	public BPMRestService(Credential credential) {
		this.credential = credential;
	}

	public String GET(String url) {
		HttpHeaders headers = new HttpHeaders();
		addHeaders(headers);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);
		String r = makeURLHttps(url);

		ResponseEntity<String> response = createRestTemplate().exchange(r, HttpMethod.GET, entity, String.class);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			if (HttpStatus.FOUND.equals(response.getStatusCode())) {
				throw new HttpServerErrorException(HttpStatus.UNAUTHORIZED, "Bad credentials");
			}
			throw new HttpServerErrorException(response.getStatusCode(), response.getBody());
		}
		return response.getBody();
	}

	public String GETHttp(String url) {
		HttpHeaders headers = new HttpHeaders();
		addHeaders(headers);
		HttpEntity<Object> entity = new HttpEntity<>(headers);
		ResponseEntity<String> response = createRestTemplate().exchange(makeURLHttp(url), HttpMethod.GET, entity,
				String.class);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			throw new HttpServerErrorException(response.getStatusCode(), response.getBody());
		}
		return response.getBody();
	}

	public byte[] GETBYTES(String url) {
		HttpHeaders headers = new HttpHeaders();
		addHeaders(headers);
		HttpEntity<Object> entity = new HttpEntity<Object>(headers);
		ResponseEntity<byte[]> response = createRestTemplate().exchange(makeURLHttps(url), HttpMethod.GET, entity,
				byte[].class);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			throw new HttpServerErrorException(response.getStatusCode(), new String(response.getBody()));
		}
		return response.getBody();
	}

	public String POST(String url, String body, MultiValueMap<String, String> headers) {
		addHeaders(headers);
		return sendData(HttpMethod.POST, url, body, headers);
	}

	public String POST(String url, String body) {
		MultiValueMap<String, String> headers = new HttpHeaders();
		addHeaders(headers);
		return sendData(HttpMethod.POST, url, body, headers);
	}

	public String PUT(String url, String body, MultiValueMap<String, String> headers) {
		addHeaders(headers);
		return sendData(HttpMethod.PUT, url, body, headers);
	}

	public String PUT(String url, String body) {
		MultiValueMap<String, String> headers = new HttpHeaders();
		addHeaders(headers);
		return sendData(HttpMethod.PUT, url, body, headers);
	}

	public String PUTHttp(String url, String body) {
		HttpHeaders headers = new HttpHeaders();
		addHeaders(headers);
		headers.setContentType(MediaType.APPLICATION_JSON);
		return sendHttpData(HttpMethod.PUT, url, body, headers);
	}

	private String sendData(HttpMethod httpMethod, String url, String body, MultiValueMap<String, String> headers) {
		// try {
		final String urlHttps = makeURLHttps(url);
		RequestEntity<String> entity = new RequestEntity<>(body, headers, httpMethod, null);
		ResponseEntity<String> response = createRestTemplate().exchange(urlHttps, httpMethod, entity, String.class);
		if (!HttpStatus.OK.equals(response.getStatusCode())) {
			throw new HttpServerErrorException(response.getStatusCode(), response.getBody());
		}
		return response.getBody();
		// } catch (URISyntaxException e) {
		// throw new HttpServerErrorException(HttpStatus.BAD_REQUEST,
		// e.getMessage());
		// }
	}

	public String sendHttpData(HttpMethod httpMethod, String url, String body, MultiValueMap<String, String> headers) {
		try {
			RequestEntity<String> entity = new RequestEntity<>(body, headers, httpMethod, new URI(makeURLHttp(url)));
			ResponseEntity<String> response = createRestTemplate().exchange(makeURLHttp(url), httpMethod, entity,
					String.class);
			if (!(HttpStatus.OK.equals(response.getStatusCode())
					|| HttpStatus.CREATED.equals(response.getStatusCode()))) {
				throw new HttpServerErrorException(response.getStatusCode(), response.getBody());
			}
			return response.getBody();
		} catch (URISyntaxException e) {
			throw new HttpServerErrorException(HttpStatus.BAD_REQUEST, e.getMessage());
		}
	}

	public ResponseEntity<String> sendHttpDataWithoutMake(HttpMethod httpMethod, String url, String body,
			MultiValueMap<String, String> headers) throws URISyntaxException {
		RequestEntity<String> entity = new RequestEntity<>(body, headers, httpMethod, new URI(url));
		ResponseEntity<String> response = createRestTemplate().exchange(url, httpMethod, entity, String.class);
		return response;
	}

	public Credential getCredential() {
		return credential;
	}

	public String makeURLHttps(String restURL) {
		return credential.getBaseUrl() + restURL;
	}

	public String makeURLHttp(String restURL) {
		return makeURLHttps(restURL).replaceAll("https://", "http://");
	}

	private void fillInterceptors(List<ClientHttpRequestInterceptor> interceptors) {
		if (nonNull(credential)) {
			Optional<HttpHeader> hh = credential.getHeaders().stream().filter(h -> "Cookie".equals(h.getName()))
					.findAny();
			if (!hh.isPresent()) {
				HeaderRequestInterceptor hri = new HeaderRequestInterceptor(null, null);

				interceptors.add(new HeaderRequestInterceptor(AUTHORIZATION, "Basic "
						+ DecodeEncodeUtil.encodeBase64(credential.getUsername() + ":" + credential.getPassword())));
			}
		}
	}

	private RestTemplate createRestTemplate() {
		final List<ClientHttpRequestInterceptor> interceptors = new ArrayList<ClientHttpRequestInterceptor>();
		fillInterceptors(interceptors);
		final HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();

		TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}

			public void checkClientTrusted(X509Certificate[] certs, String authType) {
			}

			public void checkServerTrusted(X509Certificate[] certs, String authType) {
			}
		} };

		SSLContext sslContext = null;
		try {
			sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustAllCerts, new SecureRandom());
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		}

		final HttpClient httpClient = HttpClientBuilder.create().disableRedirectHandling()
				.setSSLHostnameVerifier((s, sslSession) -> true).setSSLContext(sslContext).build();

		requestFactory.setHttpClient(httpClient);
		final RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new ResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}

			@Override
			public void handleError(ClientHttpResponse response) throws IOException {
			}
		});
		restTemplate.setRequestFactory(requestFactory);
		restTemplate.setInterceptors(interceptors);
		restTemplate.setUriTemplateHandler(new UriTemplateHandler() {

			@Override
			public URI expand(String uriTemplate, Object... uriVariableValues) {
				try {
					return new URI(uriTemplate);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				return null;
			}

			@Override
			public URI expand(String uriTemplate, Map<String, ?> uriVariables) {
				try {
					return new URI(uriTemplate);
				} catch (URISyntaxException e) {
					e.printStackTrace();
				}
				return null;
			}
		});
		return restTemplate;
	}

	private void addHeaders(MultiValueMap<String, String> headers) {
		credential.getHeaders().stream().forEach(h -> headers.add(h.getName(), h.getValue()));
	}

	private static org.json.simple.JSONObject getSubElement(org.json.simple.JSONObject obj, String[] path) {
		org.json.simple.JSONObject retval = null;
		if (obj != null && path != null && path.length > 0) {
			if (path.length == 1) {
				retval = (org.json.simple.JSONObject) obj.get(path[0]);
				return retval;
			} else {
				String[] newArray = Arrays.copyOfRange(path, 0, path.length - 1);
				return getSubElement(obj, newArray);
			}
		}

		return retval;
	}
}
