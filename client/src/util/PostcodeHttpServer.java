package util;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.Files;

/**
 * 우편번호 검색 HTML 파일을 제공하는 간단한 HTTP 서버
 */
public class PostcodeHttpServer {
    
    private static HttpServer server;
    private static int actualPort = -1;
    private static volatile AddressCallback addressCallback;
    
    public interface AddressCallback {
        void onAddressReceived(String postal, String address);
    }
    
    public static void setAddressCallback(AddressCallback callback) {
        addressCallback = callback;
    }
    
    public static void start() throws IOException {
        if (server != null) {
            return; // 이미 실행 중
        }
        
        // 사용 가능한 포트 찾기 (8888부터 시작해서 사용 가능한 포트 탐색)
        IOException lastException = null;
        for (int port = 8888; port <= 8898; port++) {
            try {
                server = HttpServer.create(new InetSocketAddress(port), 0);
                actualPort = port;
                lastException = null;
                break;
            } catch (IOException e) {
                lastException = e;
                // 다음 포트 시도
            }
        }
        
        if (server == null) {
            throw new IOException("사용 가능한 포트를 찾을 수 없습니다 (8888-8898)", lastException);
        }
        
        // HTML 파일 제공
        server.createContext("/postcode", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    File htmlFile = new File("postcode_browser.html");
                    if (!htmlFile.exists()) {
                        String response = "postcode_browser.html not found";
                        exchange.sendResponseHeaders(404, response.length());
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes());
                        os.close();
                        return;
                    }
                    
                    byte[] content = Files.readAllBytes(htmlFile.toPath());
                    exchange.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
                    exchange.sendResponseHeaders(200, content.length);
                    OutputStream os = exchange.getResponseBody();
                    os.write(content);
                    os.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        // 주소 데이터 수신 엔드포인트
        server.createContext("/address", new HttpHandler() {
            @Override
            public void handle(HttpExchange exchange) throws IOException {
                try {
                    // CORS 헤더 추가
                    exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "POST, OPTIONS");
                    exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
                    
                    if ("OPTIONS".equals(exchange.getRequestMethod())) {
                        exchange.sendResponseHeaders(204, -1);
                        return;
                    }
                    
                    if ("POST".equals(exchange.getRequestMethod())) {
                        // 요청 바디 읽기
                        InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "UTF-8");
                        BufferedReader br = new BufferedReader(isr);
                        StringBuilder body = new StringBuilder();
                        String line;
                        while ((line = br.readLine()) != null) {
                            body.append(line);
                        }
                        
                        String jsonBody = body.toString();
                        System.out.println("[HTTP Server] Received address data: " + jsonBody);
                        
                        // JSON 파싱 (간단한 문자열 처리)
                        String postal = extractJsonValue(jsonBody, "postal");
                        String address = extractJsonValue(jsonBody, "address");
                        
                        if (postal != null && address != null && addressCallback != null) {
                            addressCallback.onAddressReceived(postal, address);
                            System.out.println("[HTTP Server] Callback invoked: " + postal + " | " + address);
                        }
                        
                        // 응답
                        String response = "{\"ok\":true}";
                        exchange.getResponseHeaders().set("Content-Type", "application/json");
                        exchange.sendResponseHeaders(200, response.getBytes("UTF-8").length);
                        OutputStream os = exchange.getResponseBody();
                        os.write(response.getBytes("UTF-8"));
                        os.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        server.setExecutor(null);
        server.start();
        System.out.println("[PostcodeHttpServer] HTTP 서버 시작됨: http://localhost:" + actualPort + "/postcode");
    }
    
    private static String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex = json.indexOf("\"", startIndex + searchKey.length()) + 1;
        int endIndex = json.indexOf("\"", startIndex);
        
        if (startIndex > 0 && endIndex > startIndex) {
            return json.substring(startIndex, endIndex);
        }
        return null;
    }
    
    public static void stop() {
        if (server != null) {
            server.stop(0);
            server = null;
            actualPort = -1;
            System.out.println("[PostcodeHttpServer] HTTP 서버 종료됨");
        }
    }
    
    public static String getUrl() {
        if (actualPort == -1) {
            return "http://localhost:8888/postcode"; // 기본값
        }
        return "http://localhost:" + actualPort + "/postcode";
    }
}
