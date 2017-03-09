$(document).ready(function() {
    // 收缩时隐藏用户头像和服务
    $('.menu-toggler.sidebar-toggler').click(
        function() {
            $(".input-group.nav-toggle.nav-link.nav-justified").toggle();
            $(".nav-toggle.clearfix").toggle();
            $(".logo.font-white").toggle();
        }
    )

    // 日期初始化
    $(".datepicker").datepicker({
        language: 'zh-CN',
        format: "yyyy-mm-dd"
    });
});