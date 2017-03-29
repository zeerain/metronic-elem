/**
 * toastr通知参数设置
 */
toastr.options = {
    "positionClass": "toast-top-center",
    "closeButton": true,
    "debug": false,
    "progressBar": false,
    "onclick": null,
    "showDuration": "500",
    "hideDuration": "1000",
    "timeOut": "5000",
    "extendedTimeOut": "1000",
    "showEasing": "swing",
    "hideEasing": "linear",
    "showMethod": "fadeIn",
    "hideMethod": "fadeOut"
};


(function($) {
    /**
     * 处理Ajax返还结果，将状态显示到Form
     */
    $.fn.handleResult = function(data) {
        var parentRow = $("#" + $(this).attr("id") + "Row");
        if (data.errors) {
            parentRow.removeClass("has-success").addClass("has-error");
            parentRow.find("label").html(data.errors[0]);
            parentRow.find("label").removeClass("hide");
        } else {
            parentRow.removeClass("has-error").addClass("has-success");
            parentRow.find("label").html("");
            parentRow.find("label").addClass("hide");
        }
    };

    // extend functions
    $.extend({
        Utils: {
            initPageEvent: function() {
                // 初始化Form Validator 
                $("form").each(function(index, element) {
                    $(element).bindValidator();
                });
                //-- 初始化删除确认事件 -->
                $("a.action-confirm").on("click", $.Utils.confirmEventHandler);
                // 初始化selectAll事件
                $("#selectAll").on("click", function() {
                    $(".selectAll-child").prop("checked", $(this).is(':checked'));
                });
            },


            // 操作之前的确认框
            confirmEventHandler: function() {
                var link = $(this);
                if (confirm(link.data("confirm"))) {
                    var redirectUrl = link.data("redirect");
                    $.post(link.data("url"), {}, function(result) {
                        if (result) {
                            if (result.error) {
                                alert(result.error);
                            } else if (result.success) {
                                toastr.success("删除操作成功！", "操作成功");
                                if (redirectUrl) {
                                    location.href = redirectUrl;
                                } else {
                                    location.reload();
                                }
                            }
                        }
                    });
                }
            },

            // 聚焦菜单
            focusMenu: function() {
                var url = jQuery.ajaxSettings.url;
                var matched = false;
                $("ul.page-sidebar-menu li").each(function() {
                    var menu = $(this).data("m");
                    if (menu) {
                        if (url.indexOf(menu) > 0) {
                            $(this).addClass("active");
                            matched = true;
                        } else {
                            $(this).removeClass("active");
                        }
                    }
                });
                if (!matched) {
                    $("ul.page-sidebar-menu li.start.nav-item").addClass("active");
                } else {
                    $("li.treeview").each(function() {
                        var activeMenu = $(this).find("li.active");
                        if (activeMenu && activeMenu.length > 0) {
                            $(this).addClass("active");
                        } else {
                            $(this).removeClass("active");
                        }
                    });
                }
            },

            // 初始化公告跑马灯
            startTicker: function() {
                if ($('.vticker')) {
                    var dd = $('.vticker').easyTicker({
                        direction: 'up',
                        easing: 'easeInOutBack',
                        speed: 'slow',
                        interval: 9000,
                        height: 50,
                        visible: 0,
                        mousePause: 1
                    }).data('easyTicker');
                    dd.start();
                }
            }
        }
    });
    window.setInterval(function() {
        $.get(contextPath + "/ping?r=" + new Date().getTime())
    }, 300000);
})(jQuery);