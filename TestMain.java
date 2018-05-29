package com.mkyong.common.controller;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.security.KeyStore;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.util.EntityUtils;


/**
 * Created by gadde on 5/27/2018.
 */
public class TestMain {
    static {
        //for localhost testing only
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
    }
    public static void main1(String args[]) throws Exception {

        System.setProperty("javax.net.ssl.trustStore", "C:\\Users\\ramch\\Downloads\\jetty-distribution-9.0.7.v20131107\\etc\\certificate\\jcg.pkcs12");
        System.setProperty("javax.net.ssl.trustStorePassword", "Ramchand1");
        System.setProperty("javax.net.ssl.keyStore", "C:\\Users\\ramch\\Downloads\\jetty-distribution-9.0.7.v20131107\\etc\\certificate\\keystore");
        System.setProperty("javax.net.ssl.keyStorePassword", "Ramchand1");


        String url = "https://localhost:8443/RESTfulExample/rest/hello/mkyong";
        URL obj = new URL(url);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("GET");
        con.setRequestProperty("content-type", "application/xml");
        String urlParameters = null;
        // Send post request
        con.setDoOutput(true);

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + url);
        //System.out.println("Post parameters : " + urlParameters);
        System.out.println("Response Code : " + responseCode);
        System.out.println("Response String : " + receiveResponse(con));

        // }
    }

    public static void main(String args[]) throws Exception {
        Console console = System.console();

        // Trust own CA and all self-signed certs
        SSLContext sslcontext = SSLContexts.custom()
                .loadTrustMaterial(new File("C:\\Users\\ramch\\Downloads\\certificate\\keystore"), "Ramchand1".toCharArray(),
                        new TrustSelfSignedStrategy())
                .loadKeyMaterial(new File("C:\\Users\\ramch\\Downloads\\certificate\\keystore"), "Ramchand1".toCharArray(),"Ramchand1".toCharArray())
                .build();
        String url = "https://localhost:8443/RESTfulExample/rest/hello/mkyong";

        // Allow TLSv1 protocol only
        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                sslcontext,
                new String[] { "TLSv1", "TLSv1.2" },
                null,
                new javax.net.ssl.HostnameVerifier(){

                    public boolean verify(String hostname,
                                          javax.net.ssl.SSLSession sslSession) {
                        if (hostname.equals("localhost")) {
                            return true;
                        }
                        return false;
                    }
                });
        CloseableHttpClient httpclient = HttpClients.custom()
                .setSSLSocketFactory(sslsf)
                .build();
        try (CloseableHttpResponse closeableHttpResponse = httpclient.execute(
                     new HttpGet(URI.create(url)))) {
            //console.writer().println(closeableHttpResponse.getStatusLine());
            HttpEntity entity = closeableHttpResponse.getEntity();
            try (InputStream content = entity.getContent();
                 ReadableByteChannel src = Channels.newChannel(content);
                 WritableByteChannel dest = Channels.newChannel(System.out)) {
                ByteBuffer buffer = ByteBuffer.allocateDirect(16 * 1024);
                while (src.read(buffer) != -1) {
                    buffer.flip();
                    dest.write(buffer);
                    buffer.compact();
                }
                buffer.flip();
                while (buffer.hasRemaining())
                    dest.write(buffer);
            }
        }
    }


    public static String receiveResponse(HttpURLConnection conn)
            throws IOException {
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        // retrieve the response from server
        InputStream is = null;
        try {
            is = conn.getInputStream();
            int ch;
            StringBuffer sb = new StringBuffer();
            while ((ch = is.read()) != -1) {
                sb.append((char) ch);
            }
            return sb.toString();
        } catch (IOException e) {
            throw e;
        } finally {
            if (is != null) {
                is.close();
            }
        }
    }

    }
