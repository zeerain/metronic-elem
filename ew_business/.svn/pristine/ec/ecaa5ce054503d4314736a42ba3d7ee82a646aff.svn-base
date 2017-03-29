/**
 * 元数据页面scripts
 * @author mazc
 */
(function($) {
	$(document).ready(function(){
		
	});
})(jQuery);


// 滚动加载 TAB
function infinite_tab(totalPages, url) {
	var currentpage = 1; //默认第一页
	if (totalPages < 2) {
		$(".viewMore").hide(); //总页数小于2则不显示加载更多
	} else {
		var loading = false;
		
		$(".infinite").infinite().on("infinite", function() {
			if(loading || currentpage==totalPages) return;
	    	loading = true;
	    	
	      	setTimeout(function() {
	      		currentpage ++; // 计数+1
	      		// ajax获取数据
	      		$.ajax({ url: url+'/'+currentpage,
		        	type: 'GET',
		        	beforeSend: function() {
		        	},
					success: function(data) {
						if(data.errors){
							//toast('error', '获取数据失败');
			    		} else {
						    $('.viewMoreTR').before(data);
						    if (currentpage >= totalPages) $('.viewMore').hide(); //加载所有页, 隐藏下拉按钮
			    		}
					}
		        });
				loading = false;
	      	}, 500);
	    });
	}
}

//滚动加载
function infinite(totalPages, url) {
	var currentpage = 1; //默认第一页
	if (totalPages < 2) {
		$(".viewMore").hide(); //总页数小于2则不显示加载更多
	} else {
		var loading = false;
		
		$(document.body).infinite().on("infinite", function() {
			if(loading || currentpage==totalPages) return;
	    	loading = true;
	    	
	      	setTimeout(function() {
	      		currentpage ++; // 计数+1
	      		// ajax获取数据
	      		$.ajax({ url: url+'/'+currentpage,
		        	type: 'GET',
		        	beforeSend: function() {
		        	},
					success: function(data) {
						if(data.errors){
							//toast('error', '获取数据失败');
			    		} else {
						    $('.viewMoreTR').before(data);
						    if (currentpage >= totalPages) $('.viewMore').hide(); //加载所有页, 隐藏下拉按钮
			    		}
					}
		        });
				loading = false;
	      	}, 500);
	    });
	}
}