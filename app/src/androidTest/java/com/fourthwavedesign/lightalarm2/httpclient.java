package com.fourthwave;

import java.io.IOException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;



public class httpclient {

	private String domain = "http://192.168.1.9";
	private String port = "8080";
	private String path = "bedtime/wakeup";
	
	public void main(String[] args){
	// Create instance of HTTPCLient
	 CloseableHttpClient httpclient = HttpClients.createDefault();
	 
	 try {
		 // Setup the verb and url
		 HttpPut httpPut = new HttpPut(domain + ":" + port + "/" + path);

         System.out.println("Executing request " + httpPut.getRequestLine());
         
         // If we had request params to send we would add them here.
         // However for this particular request we only need to hit the endpoint

         // Create a custom response handler
         ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
        	 
        	 // Deal with the response
             public String handleResponse(
                     final HttpResponse response) throws ClientProtocolException, IOException {
                 int status = response.getStatusLine().getStatusCode();
                 
                 // Check the response code - was the request successful?
                 if (status >= 200 && status < 300) {
                     HttpEntity entity = response.getEntity();
                     return entity != null ? EntityUtils.toString(entity) : null;
                 } else {
                	 // Request returned with non-successful status code - this will kick the code
                	 // out of processing
                     throw new ClientProtocolException("Unexpected response status: " + status);
                 }
             }

         };
         
         // Actually send the request
         String responseBody = httpclient.execute(httpPut, responseHandler);
         System.out.println("----------------------------------------");
         System.out.println(responseBody);
	 } catch(Exception ex){
		 System.out.println("Error while connecting: " + ex.getMessage());
     } finally {
    	 // Regardless of what happens on the request we need to make sure we dispose of the
    	 // httpclient object so we don't keep the connection open. Not doing this could eventually
    	 // cause a memory leak or potentionally tie up all available connections
         try {
			httpclient.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println(e.printStackTrace());
		}
     }
	}
}