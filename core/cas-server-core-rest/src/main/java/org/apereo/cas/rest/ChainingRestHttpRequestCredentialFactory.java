package org.apereo.cas.rest;

import lombok.extern.slf4j.Slf4j;
import org.apereo.cas.authentication.Credential;
import org.springframework.core.OrderComparator;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This is {@link ChainingRestHttpRequestCredentialFactory}.
 *
 * @author Misagh Moayyed
 * @since 5.3.0
 */
@Slf4j
public class ChainingRestHttpRequestCredentialFactory implements RestHttpRequestCredentialFactory {
    private final List<RestHttpRequestCredentialFactory> chain;

    public ChainingRestHttpRequestCredentialFactory(final List<RestHttpRequestCredentialFactory> chain) {
        this.chain = chain;
    }

    public ChainingRestHttpRequestCredentialFactory(final RestHttpRequestCredentialFactory... chain) {
        this.chain = Stream.of(chain).collect(Collectors.toList());
    }

    /**
     * Add credential factory.
     *
     * @param factory the factory
     */
    public void registerCredentialFactory(final RestHttpRequestCredentialFactory factory) {
        this.chain.add(factory);
    }

    public List<RestHttpRequestCredentialFactory> getChain() {
        return chain;
    }

    @Override
    public List<Credential> fromRequestBody(final MultiValueMap<String, String> requestBody) {
        OrderComparator.sort(this.chain);
        return this.chain
            .stream()
            .map(f -> f.fromRequestBody(requestBody))
            .flatMap(List::stream)
            .collect(Collectors.toList());
    }
}
