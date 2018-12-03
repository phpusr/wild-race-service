import SockJS from 'sockjs-client'
import {Stomp} from '@stomp/stompjs'

let stompClient = null;
const handlers = [];

export function connectToWS() {
    const socket = new SockJS('/wild-race-ws');
    stompClient = Stomp.over(socket);
    stompClient.debug = () => {};
    stompClient.connect({}, () => {
        handlers.forEach(h => stompClient.subscribe(h.id, message =>
            h.handler(JSON.parse(message.body))
        ));
    });
}

export function addHandler(id, handler) {
    handlers.push({ id, handler });
}

export function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
}

export function sendData(action, object) {
    stompClient.send(action, {}, JSON.stringify(object));
}
