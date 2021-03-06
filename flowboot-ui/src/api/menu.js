import {ajax} from "../ajax";
export function getMenuNav() {
    return ajax.get("/sys/menu/nav");
}

export function getMenuById(id) {

    return ajax.get("/sys/menu/info/"+id);
}

export function getMenuTreeselect(id) {

    return ajax.get("/sys/menu/roleMenuTreeselect/"+id);
}

export function deleteById(id) {

    return ajax.post("/sys/menu/delete/"+id);
}

export function getMenuTreeOptions() {
    return ajax.get("/sys/menu/treeOptions");
}
export function getMenuTrees() {
    return ajax.get("/sys/menu/tree");
}
export function saveMenu(action,data){
    return ajax.postJson("/sys/menu/"+action,data);
}

export function updateMenuStatus(id,data){
    return ajax.postForm('/sys/menu/update/status/'+id, {status:data});
}
