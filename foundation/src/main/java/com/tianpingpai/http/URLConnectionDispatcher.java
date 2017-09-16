package com.tianpingpai.http;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.tianpingpai.utils.SingletonFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class URLConnectionDispatcher  extends RequestDispatcher{

    public static URLConnectionDispatcher getInstance(){
        return SingletonFactory.getInstance(URLConnectionDispatcher.class);
    }


    private ExecutorService service = Executors.newCachedThreadPool();
    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    public <T> void dispatch(final HttpRequest<T> r) {
        service.submit(new Runnable() {
            @Override
            public void run() {
                try {
//                    File avatarFile = new File("");
                    ConnectionWrapper wrapper = new ConnectionWrapper(r.getUrl());
                    for(String key:r.getParams().keySet()){
                        String value = r.getParams().get(key);
                        wrapper.addFormField(key,value);
                    }
//                    wrapper.addFormField("test","value");

                    for(String key:r.getFiles().keySet()){
                        File file = r.getFiles().get(key);
                        wrapper.addFilePart(key, file);
                    }

                    List<String> response = wrapper.finish();
                    Log.e("xx","SERVER REPLIED:" + response);
                    for (String line : response) {
//                        System.out.println(line);
                        Log.e("xx","line:" + line);
                    }

                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static class ConnectionWrapper{

        private static final String LINE_FEED = "\r\n";

        private String url;
        private String boundary;
        private String charset = "UTF-8";
        private PrintWriter writer;
        private OutputStream outputStream;
        private HttpURLConnection connection;

        ConnectionWrapper(String urlString) throws IOException {
            this.url = urlString;
//            this.boundary = "UCD_CW_BD_" + UUID.randomUUID() + "_UCD_CW_BD_END";
            boundary = "===" + System.currentTimeMillis() + "===";
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setUseCaches(false);
            connection.setDoOutput(true);//TODO
            connection.setRequestProperty("Content-Type",
                    "multipart/form-data; boundary=" + boundary);
            connection.setRequestProperty("User-Agent", "CodeJava Agent");
            connection.setRequestProperty("Test", "Bonjour");
            outputStream = connection.getOutputStream();
            writer = new PrintWriter(new OutputStreamWriter(outputStream, charset),
                    true);
        }

        /**
         * Adds a form field to the request
         * @param name field name
         * @param value field value
         */
        public void addFormField(String name, String value) {
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append("Content-Disposition: form-data; name=\"" + name + "\"")
                    .append(LINE_FEED);
            writer.append("Content-Type: text/plain; charset=" + charset).append(
                    LINE_FEED);
            writer.append(LINE_FEED);
            writer.append(value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a upload file section to the request
         * @param fieldName name attribute in <input type="file" name="..." />
         * @param uploadFile a File to be uploaded
         * @throws IOException
         */
        public void addFilePart(String fieldName, File uploadFile)
                throws IOException {
            String fileName = uploadFile.getName();
            writer.append("--" + boundary).append(LINE_FEED);
            writer.append(
                    "Content-Disposition: form-data; name=\"" + fieldName
                            + "\"; filename=\"" + fileName + "\"")
                    .append(LINE_FEED);
            writer.append(
                    "Content-Type: "
                            + URLConnection.guessContentTypeFromName(fileName))
                    .append(LINE_FEED);
            writer.append("Content-Transfer-Encoding: binary").append(LINE_FEED);
            writer.append(LINE_FEED);
            writer.flush();

            FileInputStream inputStream = new FileInputStream(uploadFile);
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
            outputStream.flush();
            inputStream.close();

            writer.append(LINE_FEED);
            writer.flush();
        }

        /**
         * Adds a header field to the request.
         * @param name - name of the header field
         * @param value - value of the header field
         */
        public void addHeaderField(String name, String value) {
            writer.append(name + ": " + value).append(LINE_FEED);
            writer.flush();
        }

        /**
         * Completes the request and receives response from the server.
         * @return a list of Strings as response in case the server returned
         * status OK, otherwise an exception is thrown.
         * @throws IOException
         */
        public List<String> finish() throws IOException {
            List<String> response = new ArrayList<>();

            writer.append(LINE_FEED).flush();
            writer.append("--" + boundary + "--").append(LINE_FEED);
            writer.close();

            // checks server's status code first
            int status = connection.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(
                        connection.getInputStream()));
                String line = null;
                while ((line = reader.readLine()) != null) {
                    response.add(line);
                }
                reader.close();
                connection.disconnect();
            } else {
                throw new IOException("Server returned non-OK status: " + status + "response:" + response);
            }
            return response;
        }
    }
}
