$(function(){
	voice = new VoiceByWechat();
	voice.init();
});

var $progress = $('.js_progress');
var $btnUpload = $('#startRecord');
var progress = 0;
var step = 100/(1000/50 * 60);
function next() {
    if(progress > 100 || $btnUpload.hasClass("weui-btn_primary")){
        progress = 0;
        return;
    }
    $progress.css({width: progress + '%'});
    progress = progress + step;
    setTimeout(next, 50);
}

function VoiceByWechat(){
	this.isWxInit = false;

	this.localId = "";
	this.serverId = "";
	this.ctx = $("body").data("ctx");
	
	this.$btn = $("#startRecord");
	this.$progress = $(".weui-progress");
	
	this.$loadingToast = $("#loadingToast");
	
	
}

VoiceByWechat.prototype = {
	init: function(){
		this.bindEvent();
	},
	
	bindEvent: function(){
		var _self = this;
		$(document).delegate("#startRecord", 'touchstart', function(e){
			if(!weixin){
				alert("还没准备好，稍后再试吧！");
				return false;
			}
			$(this).removeClass("weui-btn_primary").addClass("weui-btn_warn").text("松开结束录音");
			next();
			if(!_self.isWxInit){
				_self.wxInit();
				_self.isWxInit = true;
			}
			wx.startRecord();
			if ( e && e.preventDefault ) {
			    e.preventDefault(); 
			}
			return false;
		}).delegate("#startRecord", 'touchend', function(e){
			wx.stopRecord({
			    success: function (res) {
			        _self.recordEnd(res);
			        _self.translate();
			    }
			});
			if ( e && e.preventDefault ) {
			    e.preventDefault(); 
			}
			return false;
		});
		
		$("#playVoice").click(function(){
			var isPlay = $(this).attr("isplay");
			if(isPlay == 1){
				wx.stopVoice({
				    localId: _self.localId
				});
			    _self.playEnd();
			}else if(isPlay == 0){
				$(this).attr("isplay", 1).removeClass("weui-btn_primary").addClass("weui-btn_warn");
				wx.playVoice({
				    localId: _self.localId
				});
			}
		});
	},
	
	wxInit: function(){
		var _self = this;
		wx.onVoiceRecordEnd({
		    // 录音时间超过一分钟没有停止的时候会执行 complete 回调
		    complete: function (res) {
		        _self.recordEnd(res);
		    }
		});
		
		wx.onVoicePlayEnd({
		    success: function (res) {
		        var localId = res.localId; // 返回音频的本地ID
		        _self.playEnd();
		    }
		});
	},
	
	playEnd: function(){
		$("#playVoice").attr("isplay", 0).removeClass("weui-btn_warn").addClass("weui-btn_primary");
	},
	
	recordEnd: function(res){
		var localId = res.localId;
		this.localId = localId;
		$(".play-voice-box").fadeIn(200);
		$("#startRecord").removeClass("weui-btn_warn").addClass("weui-btn_primary").text("长按开始录音");
	},
	
	translate: function(){
		var _self = this;
		_self.$loadingToast.fadeIn(200);
		wx.translateVoice({
		   localId: _self.localId, // 需要识别的音频的本地Id，由录音相关接口获得
		    isShowProgressTips: 0, // 默认为1，显示进度提示
		    success: function (res) {
		        $("#translateResult").val(res.translateResult);
		        _self.uploadVoice();
		    }
		});
	}, 
	
	uploadVoice: function(){
		var _self = this;
		if(this.localId == ""){
			alert("还没开始录音");
		}
		
		wx.uploadVoice({
		    localId: this.localId, // 需要上传的音频的本地ID，由stopRecord接口获得
		    isShowProgressTips: 0, // 默认为1，显示进度提示
		    success: function (res) {
		        var serverId = res.serverId; // 返回音频的服务器端ID
		        $("#mediaId").val(serverId);
		        _self.serverId = serverId;
				_self.$loadingToast.fadeOut(200);
		        //_self.saveVoice();
		    }
		});
	},
	
	saveVoice: function(){
		var _self = this;
		$.ajax({
			url: this.ctx + "/wechat/group/voice/saveVoice",
			type: 'post',
			data: {"serverId": this.serverId, "text": $("#translateResult").val()},
			dataType: 'json'
		}).done(function(){
			$("#downloadVoice").removeClass("weui-btn_disabled");
		});
	}
}