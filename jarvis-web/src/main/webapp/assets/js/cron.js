function getCircleType(expContent) {

    if (expContent == null || expContent == '') {
        return CONST.SCHEDULE_CIRCLE_TYPE.PER_DAY;
    }

    var circle = CONST.SCHEDULE_CIRCLE_TYPE.NONE;
    var fields = expContent.replace(/(\s)+/g, ' ').split(' ');
    var second = (fields[0] == undefined ? 0 : fields[0]),        // seconds
        minute = (fields[1] == undefined ? 0 : fields[1]),        // minutes
        hour = (fields[2] == undefined ? 0 : fields[2]),          // hours
        day = (fields[3] == undefined ? '*' : fields[3]),           // day of month
        month = (fields[4] == undefined ? '*' : fields[4]),         // month
        weekDay = (fields[5] == undefined ? '?' : fields[5]),       // day of week
        year = (fields[6] == undefined ? '*' : fields[6]);          // year

    if (month == '?' && weekDay == '*') {   //确保 month='*' ,weekDay='?',而不是相反.
        month = '*';
        weekDay = '?';
    }

    for (; ;) {
        if (!$.isNumeric(second)) break;
        if (!$.isNumeric(minute)) break;

        if (hour != '*' && !$.isNumeric(hour)) {
            break;
        } else if (hour == '*') {
            if (day == '*' && month == '*' && weekDay == '?' && year == '*') {
                circle = CONST.SCHEDULE_CIRCLE_TYPE.PER_HOUR;
                break;
            } else {
                break;
            }
        }

        if (day != '?') {
            if (day == '*') {
                if (month == '*' && weekDay == '?' && year == '*') {
                    circle = CONST.SCHEDULE_CIRCLE_TYPE.PER_DAY;
                    break;
                }
            } else {
                if (!isNumberStr(day)) {
                    break;
                }
            }
        }

        if (month == '*') {
            if (weekDay == '?' && year == '*') {
                circle = CONST.SCHEDULE_CIRCLE_TYPE.PER_MONTH;
                break;
            }
        } else {
            if (!isNumberStr(month)) {
                break;
            }
        }

        if (weekDay != '?') {
            if (isNumberStr(weekDay)) {
                if (month = '*' && year == '*') {
                    circle = CONST.SCHEDULE_CIRCLE_TYPE.PER_WEEK;
                    break;
                } else {
                    break;
                }
            } else {
                break;
            }
        }

        if (year == '*') {
            circle = CONST.SCHEDULE_CIRCLE_TYPE.PER_YEAR;
            break;
        } else {
            break;
        }
    }
    return circle;
}

function isNumberStr(field) {
    var subs = field.split(',');
    var okFlg = true;
    for (var key in subs) {
        if (subs[key] != "" && !$.isNumeric(subs[key])) {
            okFlg = false;
            break;
        }
    }
    return okFlg;
}


function getExpDesc(expType, expContent, circleType) {

    var expDesc = "";
    if (expType == null || expContent == null || expContent == "") {
        return expDesc;
    }

    if (circleType == undefined) {
        if (expType == CONST.SCHEDULE_EXP_TYPE.CRON) {
            circleType = getCircleType(expContent);
        } else {
            circleType = CONST.SCHEDULE_CIRCLE_TYPE.NONE;
        }
    }


    if (expType == CONST.SCHEDULE_EXP_TYPE.CRON) {

        var fields = expContent.replace(/(\s)+/g, ' ').split(' ');
        var second = (fields[0] == undefined ? '' : fields[0]),        // seconds
            minute = (fields[1] == undefined ? '' : fields[1]),        // minutes
            hour = (fields[2] == undefined ? '' : fields[2]),          // hours
            day = (fields[3] == undefined ? '' : fields[3]),           // day of month
            month = (fields[4] == undefined ? '' : fields[4]),         // month
            weekDay = (fields[5] == undefined ? '' : fields[5]),       // day of week
            year = (fields[6] == undefined ? '' : fields[6]);          // year

        if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.NONE) {
            expDesc = "秒[" + second + "] 分[" + minute + "] 时[" + hour + '] 日['
                + day + '] 月[' + month + '] 星期[' + weekDay + '] 年[' + year + ']'
            return expDesc;
        } else {

            if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_YEAR) {
                expDesc = "每年 ";
                expDesc = expDesc + month + "月 ";
            }
            if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_MONTH) {
                expDesc = "每月 ";
            }
            if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_YEAR || circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_MONTH) {
                expDesc = expDesc + day + "日 ";
            }
            if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_DAY) {
                expDesc = "每天 ";
            }
            if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_WEEK) {
                expDesc = "每周 " + convertWeekDay(weekDay);
            }
            if (circleType == CONST.SCHEDULE_CIRCLE_TYPE.PER_HOUR) {
                expDesc = "每小时 ";
            } else {
                expDesc = expDesc + "  " + hour + '点';
            }

            expDesc = expDesc + minute + "分" + second + "秒";

            return expDesc;
        }
    }

    return expDesc;

}


function convertWeekDay(weekDay) {

    if (weekDay == null || weekDay == "") {
        return "";
    }

    return weekDay.replace(/(1)/g, '周一')
        .(/(2)/g, '周二')
        .(/(3)/g, '周三')
        .(/(4)/g, '周四')
        .(/(5)/g, '周五')
        .(/(6)/g, '周六')
        .(/(7)/g, '周日');

}