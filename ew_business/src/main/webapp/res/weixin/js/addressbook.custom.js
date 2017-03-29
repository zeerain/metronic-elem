$(function(){
    var searchFunc = new SearchFunc();
});
function SearchFunc(){
    this.init();
    if ($(".btn-category-list").length == 0){
    	$(".btn-search").trigger("click");
    	$(".btn-category").hide();
    }

    // 添加右侧通讯录滑动事件

    var navDom = document.getElementById('nav-bar');
    navDom.addEventListener('touchmove', touch, false);

    function touch(event){
        event.preventDefault();
        var event = event || window.event;
        var t = event.touches[0];
        location(t.clientY);
        return false;
    }

    function location(clientY){
        var top = parseInt(navDom.style.top);
        var allHeight = $('#nav-bar').height();
        var cellHeight = $('#nav-bar a').height();
        if (clientY > top && clientY < allHeight){
            var sub = clientY - top;
            var idx = Math.floor(sub/cellHeight);
            window.location.href = $('#nav-bar a').eq(idx).attr('href');
        }

        
    }
}
SearchFunc.prototype = {
    init: function(){
        var _self = this;
        var $control = $(".main-control");

        // 去掉每个导航前面的导航线
        $(".list .title").each(function(){
            var idx = $(this).index();
            if (idx > 0){
                $(".list li").eq(idx - 1).css('borderBottom', 'none');
            }
        })

        $(".btn-search").click(function(){
            if (!$control.hasClass('open-search')){
                _self.reset();
                $control.addClass('open-search');
                $(".input-search").focus();
            } else{
                $("#search-fm").submit();
            }
        });
        $("#search-fm").submit(function(){
             _self.matchAll($(".input-search").val());
             return false;
        });
        $(".btn-category").click(function(){
            $control.removeClass("open-search");
            $(".input-search").val("");
            _self.reset();
        });
        // 搜索文本框失去焦点时候，若文本框中没有值，恢复到下面所有名单都显示
        $(".input-search").blur(function(){
            if ($(this).val() == ''){
                _self.reset();
            }
        });
        $(".btn-category-list").click(function(){
            _self.matchAll($(this).text());
        });
    },
    matchAll: function(val){
        if (val == ''){
            return ;
        }
        $('.list li').hide();
        $(".nav-bar a").hide();
        $('.people *:contains("' + val + '")').each(function(){
            var $li = $(this).closest('li');
            $li.show();
            var idx = $li.index();
            for (var i = idx - 1; i >= 0; i--){
                var $title = $('.list li').eq(i);
                if ($title.hasClass('title')){
                    if (!$title.is(":visible")){
                        $title.show();
                        var id = $title.attr('id');
                        $('.nav-bar a[href="#' + id + '"]').show();
                    }
                    break;
                }
            }
        });
    },
    reset: function(){
        $('.nav-bar a').show();
        $('.list li').show();
    }
}
