;(function(){
    var selectDateDom = $('.s-datepicker');
    var showDateDom = $('.s-datepicker .date');
    var showWeekendDom = $('.s-datepicker .weekend');
    var inputDateDom = $('.s-datepicker .input-date');
    // 初始化时间
    var now = new Date();
    var nowYear = now.getFullYear();
    var nowMonth = now.getMonth() + 1;
    var nowDate = now.getDate();
    // 数据初始化

    init();

    function formatYear (nowYear) {
        var arr = [];
        for (var i = nowYear - 5; i <= nowYear + 5; i++) {
            arr.push({
                id: i + '',
                value: i + '年'
            });
        }
        return arr;
    }
    function formatMonth () {
        var arr = [];
        for (var i = 1; i <= 12; i++) {
            arr.push({
                id: i + '',
                value: i + '月'
            });
        }
        return arr;
    }
    function formatDate (count) {
        var arr = [];
        for (var i = 1; i <= count; i++) {
            arr.push({
                id: i + '',
                value: i + '日'
            });
        }
        return arr;
    }
    var yearData = function(callback) {
        callback(formatYear(nowYear))
    }
    var monthData = function (year, callback) {
        callback(formatMonth());
    };
    var dateData = function (year, month, callback) {
        if (/^(1|3|5|7|8|10|12)$/.test(month)) {
            callback(formatDate(31));
        }
        else if (/^(4|6|9|11)$/.test(month)) {
            callback(formatDate(30));
        }
        else if (/^2$/.test(month)) {
            if (year % 4 === 0 && year % 100 !==0 || year % 400 === 0) {
                callback(formatDate(29));
            }
            else {
                callback(formatDate(28));
            }
        }
        else {
            throw new Error('month is illegal');
        }
    };
    selectDateDom.bind('click', function () {
        showDateDom = $(this).find('.date');
        showWeekendDom = $(this).find('.weekend');
        inputDateDom = $(this).find('.input-date');
        var oneLevelId = showDateDom.attr('data-year');
        var twoLevelId = showDateDom.attr('data-month');
        var threeLevelId = showDateDom.attr('data-date');
        var iosSelect = new IosSelect(3, 
            [yearData, monthData, dateData],
            {
                title: '日期选择',
                itemHeight: 35,
                relation: [1, 1, 0, 0],
                itemShowCount: 9,
                oneLevelId: oneLevelId,
                twoLevelId: twoLevelId,
                threeLevelId: threeLevelId,
                callback: function (selectOneObj, selectTwoObj, selectThreeObj) {
                    showDateDom.attr('data-year', selectOneObj.id);
                    showDateDom.attr('data-month', selectTwoObj.id);
                    showDateDom.attr('data-date', selectThreeObj.id);
                    showDateDom.html(selectTwoObj.value + selectThreeObj.value);
                    showWeekendDom.html(getWeekendString(selectOneObj.id, selectTwoObj.id, selectThreeObj.id));
                    inputDateDom.val(selectOneObj.id + "-" + formatStr(selectTwoObj.id) + "-" + formatStr(selectThreeObj.id));
                }
        });
    });

    // author: yang

    function init(){
        var date = new Date();
        $('.s-datepicker').each(function(){
            var $inputDateDom = $(this).find('.input-date');
            var $showDateDom = $(this).find('.date');
            var $showWeekendDom = $(this).find('.weekend');

            var initVal = $inputDateDom.val();
            if (initVal != ''){
                date = new Date(initVal);
            }

            var year = date.getFullYear();
            var month = date.getMonth() + 1;
            var day = date.getDate();

            $showDateDom.attr('data-year', year);
            $showDateDom.attr('data-month', month);
            $showDateDom.attr('data-date', day);

            var inputval = date.getFullYear() + "-" + formatStr(month) + "-" + formatStr(day);
            $inputDateDom.val(inputval);
            $showDateDom.html(month + "月" + day + "日")
            $showWeekendDom.html(getWeekendString(year, month, day));
        });
    }

    function formatStr(val){
        if (Math.floor(val/10) == 0){
            val =  '0' + val;
        }
        return val;
    }

    function getWeekendString(year, month, day){
        var arr = ['周日', '周一', '周二', '周三', '周四', '周五', '周六'];
        var str = year + '/' + month + '/' + day;
        var date = new Date(str);
        return arr[date.getDay()];
    }
})();