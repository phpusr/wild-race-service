export function fetchHandler(e) {
    console.log(">> e", e)
    alert(`${e.status}: ${e.body.error} on "${e.url}"`)
}

export function stringToInt(str) {
    if (str === "") {
        return null
    }

    const value = +str
    if (isNaN(value)) {
        return null
    }

    return value
}