export default class EventsAPI {

    constructor(httpClient) {
        this.httpClient = httpClient;
    }

    events(aggregateIdentifier) {
        return this.httpClient.get(`/events/${aggregateIdentifier}`)
            .then(r => r.data);
    }
}
