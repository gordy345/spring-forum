package spring_forum.utils;

import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CountryResponse;
import com.neovisionaries.i18n.CountryCode;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

@Slf4j
public class GeoUtils {

    private GeoUtils() {
    }

    public static String getCountryByIp(String ip) {
        log.info("Getting country by ip: " + ip);
        File database = new File("src/main/resources/maxmind/GeoLite2-City.mmdb");
        try {
            DatabaseReader dbReader = new DatabaseReader.Builder(database)
                    .build();
            InetAddress inetAddress = InetAddress.getByName(ip);
            CountryResponse countryResponse;
            countryResponse = dbReader.country(inetAddress);
            return countryResponse.getCountry().getName();
        } catch (GeoIp2Exception e) {
            return "Unknown";
        } catch (IOException e) {
            log.warn("IOException happened, message: " + e.getMessage());
            return "Unknown";
        }
    }

    public static String getLanguageByCountry(String country) {
        log.info("Getting language for country: " + country);
        if (country.equals("Unknown")) {
            return "en";
        }
        try {
            String language = CountryCode.findByName(country).get(0).toLocale().getLanguage();
            if (language.equals("")) {
                return "en";
            }
            return language;
        } catch (IndexOutOfBoundsException e) {
            return "en";
        }
    }
}
