$(function(){
	$("#scanQRCode").click(function(){
		scanQRCode();
	});
});

function scanQRCode(){
	if (!weixin){
		return ;
	}
	wx.scanQRCode({
	    desc: 'scanQRCode desc',
	    needResult: 0, // 默认为0，扫描结果由微信处理，1则直接返回扫描结果，
	    scanType: ["qrCode","barCode"], // 可以指定扫二维码还是一维码，默认二者都有
	    success: function (res) {
	       // 回调
	    },
	    error: function(res){
	          if(res.errMsg.indexOf('function_not_exist') > 0){
	               alert('版本过低请升级')
	            }
	     }
	});
}