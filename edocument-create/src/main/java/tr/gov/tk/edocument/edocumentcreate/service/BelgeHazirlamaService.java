package tr.gov.gib.evdbelge.evdbelgehazirlama.service;

import tr.gov.gib.tahsilat.thsexception.custom.GibException;

import java.util.concurrent.CompletableFuture;

public interface BelgeHazirlamaService {
    CompletableFuture<Boolean> belgeGetirVeIsle(int limit) throws GibException;
}
