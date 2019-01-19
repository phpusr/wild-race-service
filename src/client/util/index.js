export function fetchHandler(e) {
    alert(`${e.status}: ${e.body.error} on "${e.url}"`);
}