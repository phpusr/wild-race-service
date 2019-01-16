
export function replaceObject(list, object) {
    const index = list.findIndex(el => el.id === object.id);
    if (index > -1) {
        list.splice(index, 1, object);
    }
}

export function deleteObject(list, id) {
    const index = list.findIndex(el => el.id === id);
    if (index > -1) {
        list.splice(index, 1);
        return true;
    }

    return false;
}