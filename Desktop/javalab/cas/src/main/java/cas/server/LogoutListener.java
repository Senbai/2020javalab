package cas.server;
import javax.servlet.annotation.WebListener;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

@WebListener
public class LogoutListener implements HttpSessionListener {
	private RequestConfig requestConfig = RequestConfig.custom()
            .setSocketTimeout(15000)
            .setConnectTimeout(15000)
            .setConnectionRequestTimeout(15000)
            .build();
	@Override
	public void sessionCreated(HttpSessionEvent se) {
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		sendHttpPost("http://localhost:8080/app1/logout");
		sendHttpPost("http://localhost:8080/app2/logout");
	}

	public void sendHttpPost(String httpUrl) {
        HttpPost httpPost = new HttpPost(httpUrl);
        testHttpClietPost(httpPost);
    }
	public void testHttpClietPost(HttpPost httpPost) {
        CloseableHttpClient closeableHttpClient = null;
        CloseableHttpResponse closeableHttpResponse = null;
        HttpEntity httpEntity = null;
        String responseContent = null;
        try {
            closeableHttpClient = HttpClients.createDefault();
            httpPost.setConfig(requestConfig);
            closeableHttpResponse = closeableHttpClient.execute(httpPost);
            httpEntity = closeableHttpResponse.getEntity();
            responseContent = EntityUtils.toString(httpEntity, "UTF-8");
            System.out.println("responseContent=" + responseContent);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (closeableHttpResponse != null) {
                    closeableHttpResponse.close();
                }
                if (closeableHttpClient != null) {
                    closeableHttpClient.close();
                }
            } catch (Exception e) {
            }

        }
    }
}
