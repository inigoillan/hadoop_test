package com.aol.logprocessor.transformations;

import com.aol.logprocessor.georesolver.GeoResolver;
import com.aol.logprocessor.parser.address.Address;
import com.aol.logprocessor.parser.address.AddressParser;

import javax.annotation.Nonnull;

/**
 * Transforms the given field into a country code.
 *
 * @author <a href="mailto:inigo.illan@gmail.com">Inigo Illan</a>
 * @since 1.0
 */
public class AddressToCountryCode implements Transformation {
    private final GeoResolver resolver;
    private final AddressParser parser;
    private final int fieldIndex;

    public <K, T extends Address<K>> AddressToCountryCode(
                    @Nonnull GeoResolver<T> resolver,
                    @Nonnull AddressParser<T> addressParser,
                    @Nonnull AddressToCountryCodeConfig config) {

        this.fieldIndex = config.getAddressField();
        this.resolver = resolver;
        this.parser = addressParser;
    }

    @Override
    @Nonnull
    public String[] transform(String[] input) throws TransformationException {
        String ip = input[fieldIndex];

        String countryCode = null;

        try {
            Address address = parser.parse(ip);
            countryCode = resolver.resolveLocation(address).getCountryCode();
        } catch (Exception e) {
            throw new TransformationException();
        }

        input[fieldIndex] = countryCode;

        return input;
    }
}
