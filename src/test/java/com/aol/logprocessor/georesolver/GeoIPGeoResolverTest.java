package com.aol.logprocessor.georesolver;

import com.aol.logprocessor.parser.IPAddress;
import com.google.common.base.Throwables;
import org.junit.Test;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.UnknownHostException;

import static org.junit.Assert.*;


public class GeoIPGeoResolverTest {

    // IP Address are taken from:
    // http://www.nirsoft.net/countryip/gb.html
    @Test
    public void ResolveLocation_GivenGBIP_ReturnsUK() throws ResolvingException {
        // Arrange
        IPAddress address = getInetAddress("2.24.1.1");
        GeoIPGeoResolver resolver = new GeoIPGeoResolver();

        // Act
        GeoLocation country = resolver.resolveLocation(address);

        // Assert
        GeoLocation expectedLocation = buildLocation("GB");
        assertEquals(expectedLocation, country);
    }

    @Test(expected = ResolvingException.class)
    public void ResolveLocation_GivenInvalidIP_ThrowsResolvingException() throws ResolvingException {
        // Arrange
        IPAddress address = getInetAddress("192.168.0.1");
        GeoIPGeoResolver resolver = new GeoIPGeoResolver();

        // Act
        resolver.resolveLocation(address);
    }


    private GeoLocation buildLocation(String countryCode) {
        return new GeoLocation(countryCode);
    }

    private IPAddress getInetAddress(String ip) {
        try {
            return new IPAddress(Inet4Address.getByName(ip));
        } catch (UnknownHostException e) {
            Throwables.propagate(e);
        }

        // Just to make the compiler happy
        return null;
    }
}