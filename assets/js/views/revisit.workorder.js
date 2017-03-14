/**
 * 元数据页面scripts
 * @author mazc
 */
(function($) {
	$(document).ready(function(){
		$("#status").on("click", function(){
			var checked = $(this).is(':checked');
			if(checked){
				$(".triggerby-status").removeClass("hide");
			}
			else{
				$(".triggerby-status").addClass("hide");				
			}
		});
	});
})(jQuery);