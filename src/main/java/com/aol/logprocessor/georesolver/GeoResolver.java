package com.aol.logprocessor.georesolver;

import com.google.common.base.Throwables;
import com.maxmind.geoip2.DatabaseReader;
import com.maxmind.geoip2.exception.GeoIp2Exception;
import com.maxmind.geoip2.model.CityResponse;

import javax.annotation.Nonnull;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;

/**
 * Resolves an IP to a Geolocation
 *
 * @author <a href="mailto:inigo.illan@gmail.com">Inigo Illan</a>
 * @since 1.0
 */
public class GeoResolver {

    private final DatabaseReader dbReader;

    public GeoResolver() {
        this(new File("./GeoLite2-City.mmdb"));
    }

    public GeoResolver(File database) {
        DatabaseReader.Builder builder = new DatabaseReader.Builder(database);
        DatabaseReader reader = null;

        try {
            reader = builder.build();
        } catch (IOException e) {
            Throwables.propagate(e);
        }

        this.dbReader = reader;
    }

    @Nonnull
    public GeoLocation resolveLocation(@Nonnull InetAddress address) throws ResolvingException {
        CityResponse city = getCity(address);

        return buildGeoLocation(city);
    }

    @Nonnull
    private CityResponse getCity(@Nonnull InetAddress address) throws ResolvingException {
        CityResponse city;
        try {
            city = dbReader.city(address);
        } catch (IOException e) {
            throw new ResolvingException();
        } catch (GeoIp2Exception e) {
            throw new ResolvingException();
        }

        return city;
    }

    private GeoLocation buildGeoLocation(CityResponse city) {
        String countryCode = city.getCountry().getIsoCode();

        return new GeoLocation(countryCode);
    }

}
