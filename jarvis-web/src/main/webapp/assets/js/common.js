function getChineseName(uname) {
    if (uname == null || uname == '') {
        return "请传入花名拼音";
    }

    return usersJson[uname];
}


