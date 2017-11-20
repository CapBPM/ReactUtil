package com.capbpm.react.util;

import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpServerErrorException;

import static java.util.Objects.nonNull;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriTemplateHandler;

import com.capbpm.react.data.Credential;

public class RestUtil {

	private void addHeaders(MultiValueMap<String, String> headers) {
		credential.getHeaders().stream().forEach(h -> headers.add(h.getName(), h.getValue()));
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

	final Credential credential = null;
}
