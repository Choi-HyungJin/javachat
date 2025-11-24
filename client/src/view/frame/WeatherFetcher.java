package view.frame;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class WeatherFetcher {

    /**
     * OpenWeatherMap API를 통해 날씨 정보를 가져옵니다.
     * @param city 도시명 (영문)
     * @param apiKey OpenWeatherMap API 키
     * @return 날씨 상태 (Clear, Clouds, Rain, Snow 등)
     */
    public static String getWeatherCondition(String city, String apiKey) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" 
                + URLEncoder.encode(city, "UTF-8") 
                + "&appid=" + apiKey;
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // 간단한 JSON 파싱 (외부 라이브러리 없이)
                String json = response.toString();
                String weatherPattern = "\"main\":\"";
                int startIndex = json.indexOf(weatherPattern);
                if (startIndex != -1) {
                    startIndex += weatherPattern.length();
                    int endIndex = json.indexOf("\"", startIndex);
                    if (endIndex != -1) {
                        return json.substring(startIndex, endIndex);
                    }
                }
            } else {
                System.out.println("날씨 API 호출 실패: HTTP " + responseCode);
            }
        } catch (Exception e) {
            System.out.println("날씨 정보 조회 중 오류: " + e.getMessage());
        }
        return "Unknown";
    }

    /**
     * 날씨와 온도 정보를 함께 가져옵니다.
     * @param city 도시명
     * @param apiKey API 키
     * @return 배열 [날씨상태, 온도(°C)]
     */
    public static String[] getWeatherInfo(String city, String apiKey) {
        try {
            String urlString = "https://api.openweathermap.org/data/2.5/weather?q=" 
                + URLEncoder.encode(city, "UTF-8") 
                + "&appid=" + apiKey 
                + "&units=metric";  // 섭씨 온도
            
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);
            conn.setReadTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String json = response.toString();
                
                // 날씨 상태 파싱
                String weather = "Unknown";
                String weatherPattern = "\"main\":\"";
                int startIndex = json.indexOf(weatherPattern);
                if (startIndex != -1) {
                    startIndex += weatherPattern.length();
                    int endIndex = json.indexOf("\"", startIndex);
                    if (endIndex != -1) {
                        weather = json.substring(startIndex, endIndex);
                    }
                }
                
                // 온도 파싱
                String temp = "N/A";
                String tempPattern = "\"temp\":";
                startIndex = json.indexOf(tempPattern);
                if (startIndex != -1) {
                    startIndex += tempPattern.length();
                    int endIndex = json.indexOf(",", startIndex);
                    if (endIndex == -1) endIndex = json.indexOf("}", startIndex);
                    if (endIndex != -1) {
                        temp = json.substring(startIndex, endIndex).trim();
                        // 소수점 첫째자리까지만
                        try {
                            double d = Double.parseDouble(temp);
                            temp = String.format("%.1f", d);
                        } catch (NumberFormatException ignored) {}
                    }
                }
                
                return new String[]{weather, temp};
            }
        } catch (Exception e) {
            System.out.println("날씨 정보 조회 중 오류: " + e.getMessage());
        }
        return new String[]{"Unknown", "N/A"};
    }
}
