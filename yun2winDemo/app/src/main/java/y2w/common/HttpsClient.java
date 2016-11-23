package y2w.common;

import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

public class HttpsClient {
	
	public static HttpURLConnection getHttpURLConnection(URL url) throws Exception{
		if(url == null){
			return null;
		}
		if(url.getProtocol().startsWith("https")){
			trustAllHttpsCertificates();
			HostnameVerifier hv = new HostnameVerifier() {

				@Override
				public boolean verify(String arg0, SSLSession arg1) {
					return true;
				}
			};
			HttpsURLConnection.setDefaultHostnameVerifier(hv);
			return (HttpsURLConnection) url.openConnection();
		}else{
			return (HttpURLConnection) url.openConnection();
		}
	}
	
	private static void trustAllHttpsCertificates() throws Exception {
		 
        // Create a trust manager that does not validate certificate chains:
 
        TrustManager[] trustAllCerts = new TrustManager[1];
 
        TrustManager tm = new X509TrustManager() {      
            
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {      
                return null;      
            }      
      
            @Override      
            public void checkClientTrusted(      
                    java.security.cert.X509Certificate[] chain, String authType)      
                    throws java.security.cert.CertificateException {      
      
            }      
      
            @Override      
            public void checkServerTrusted(      
                    java.security.cert.X509Certificate[] chain, String authType)      
                    throws java.security.cert.CertificateException {      
      
            }      
        };      
 
        trustAllCerts[0] = tm;
 
        SSLContext sc = SSLContext
                .getInstance("SSL");
 
        sc.init(null, trustAllCerts, null);
 
        HttpsURLConnection.setDefaultSSLSocketFactory(sc
                .getSocketFactory());
 
    }

}
