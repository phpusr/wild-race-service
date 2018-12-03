
export function getIndex(list, id) {
    for (let i = 0; i < list.length; i++) {
        if (list[i].id === id) {
            return i;
        }
    }

    return -1;
}

export function replaceObject(list, object) {
    const index = getIndex(list, object.id);
    if (index > -1) {
        list.splice(index, 1, object);
    }
}

export function deleteObject(list, id) {
    const index = getIndex(list, id);
    if (index > -1) {
        list.splice(index, 1)
    }
}