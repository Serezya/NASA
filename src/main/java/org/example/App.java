package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


public class App {
    public static void main(String[] args) throws IOException {
        final String REQUEST = "https://api.nasa.gov/planetary/apod?api_key=yfUb9SdNZBYVN9YBFgcfRnWOkS2NfF0jfPGa6Fas";
        CloseableHttpClient httpClient = HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)    // максимальное время ожидание подключения к серверу
                        .setSocketTimeout(30000)    // максимальное время ожидания получения данных
                        .setRedirectsEnabled(false) // возможность следовать редиректу в ответе
                        .build())
                .build();

        HttpGet request = new HttpGet(REQUEST);
        CloseableHttpResponse response = httpClient.execute(request);

        ObjectMapper mapper = new ObjectMapper();
        NASA nasaResponse = mapper.readValue(response.getEntity().getContent(), NASA.class);
        String imgUrl = nasaResponse.getUrl();

        HttpGet requestImg = new HttpGet(imgUrl);

        CloseableHttpResponse responseImg = httpClient.execute(requestImg);
        imgUrl = imgUrl.trim().substring(imgUrl.indexOf("2008/") + 5);
        File image = new File(imgUrl);

        BufferedInputStream bis = new BufferedInputStream(responseImg.getEntity().getContent());
        FileOutputStream fos = new FileOutputStream(image);

        int ch;
        while ((ch = bis.read()) != -1) {
            fos.write(ch);
        }
        bis.close();
        fos.flush();
        fos.close();
    }
}
