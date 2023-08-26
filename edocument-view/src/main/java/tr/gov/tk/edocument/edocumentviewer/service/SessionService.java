package tr.gov.gib.evdbelge.evdbelgegoruntuleme.service;

import tr.gov.gib.evdbelge.evdbelgegoruntuleme.object.request.CreateSession;

public interface SessionService {
    String createSession(CreateSession body);

}
